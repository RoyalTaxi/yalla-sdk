package uz.yalla.network

import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.delay
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import uz.yalla.core.result.Either
import uz.yalla.network.error.DataError
import kotlin.random.Random

private const val DEFAULT_RETRY_COUNT = 3
private const val INITIAL_RETRY_DELAY_MS = 200L
private const val MAX_RETRY_DELAY_MS = 2_000L
private const val RETRY_BACKOFF_FACTOR = 2.0
private const val HTTP_UNAUTHORIZED = 401

/**
 * Runs [call] and maps the outcome to an [Either] over the [DataError.Network] taxonomy — the SDK's
 * one safe wire seam. The body is deserialized as [T] only on a 2xx status; a reified `T == Unit`
 * short-circuits without touching the body, so write endpoints that return no value are first-class.
 *
 * Every failure mode is captured: a non-2xx status maps via [statusToEither] (a parsed server
 * envelope wins over the bucket default), a 401 becomes [DataError.Network.Unauthorized], transport
 * failures map to [DataError.Network.Timeout]/[DataError.Network.Connection], decode failures (including
 * an empty/204 body for a non-`Unit` `T`) to [DataError.Network.Serialization], and a guest-blocked
 * request to [DataError.Network.Guest]. Nothing escapes as a thrown exception.
 *
 * @param isIdempotent whether [call] may be safely retried on a transient I/O failure (see [retryWithBackoff]).
 */
public suspend inline fun <reified T> safeApiCall(
    isIdempotent: Boolean = false,
    crossinline call: suspend () -> HttpResponse
): Either<DataError.Network, T> =
    try {
        val response = retryWithBackoff(isIdempotent = isIdempotent) { call() }
        val status = response.status.value
        // Parse the error envelope only on a non-2xx status: the Ktor response body is a single-read
        // stream, so consuming it as an envelope here would leave nothing for the success deserialization.
        val parsedError = if (isSuccessStatus(status)) null else parseApiError(response)
        when (val verdict = statusToEither(status, parsedError)) {
            is Either.Success ->
                if (T::class == Unit::class) {
                    Either.Success(Unit as T)
                } else {
                    Either.Success(response.body())
                }

            is Either.Failure -> Either.Failure(verdict.error)
        }
    } catch (_: ServerResponseException) {
        Either.Failure(DataError.Network.Server)
    } catch (_: ClientRequestException) {
        Either.Failure(DataError.Network.Client)
    } catch (_: RedirectResponseException) {
        Either.Failure(DataError.Network.Client)
    } catch (_: HttpRequestTimeoutException) {
        Either.Failure(DataError.Network.Timeout)
    } catch (_: SocketTimeoutException) {
        Either.Failure(DataError.Network.Timeout)
    } catch (_: IOException) {
        Either.Failure(DataError.Network.Connection)
    } catch (_: ContentConvertException) {
        Either.Failure(DataError.Network.Serialization)
    } catch (_: SerializationException) {
        Either.Failure(DataError.Network.Serialization)
    } catch (_: UnsupportedOperationException) {
        // A 204/empty 2xx body with a non-Unit T makes ContentNegotiation throw
        // NoTransformationFoundException (an UnsupportedOperationException), which is neither a
        // SerializationException nor a ResponseException — without this it would escape the wrapper.
        Either.Failure(DataError.Network.Serialization)
    } catch (_: ResponseException) {
        Either.Failure(DataError.Network.Unknown)
    } catch (_: GuestBlockedException) {
        Either.Failure(DataError.Network.Guest)
    }

/** Whether [status] is a 2xx. An inline-implementation detail of [safeApiCall], not a supported API. */
@PublishedApi
internal fun isSuccessStatus(status: Int): Boolean = status in 200..299

/**
 * The pure decision behind [safeApiCall]: maps an HTTP status to a result, leaving the actual call,
 * retry, and body deserialization to the suspend orchestration. [Either.Success] means "the caller may
 * deserialize the body as `T`"; [Either.Failure] carries the resolved error. A parsed API envelope,
 * when present, always wins over the status-bucket default so server-supplied detail survives.
 *
 * A raw 3xx is treated as a client failure, not a redirect: the Ktor client follows redirects itself,
 * so a 3xx reaching here is an unfollowed/unexpected redirect we cannot act on — a malformed request
 * from our side, hence [DataError.Network.Client], the same bucket as 4xx.
 *
 * A 401 is split out as [DataError.Network.Unauthorized] so a caller can branch to logout/re-auth
 * rather than treating an expired session like an ordinary 400/404.
 */
@PublishedApi
internal fun statusToEither(
    status: Int,
    parsedError: DataError.Network.Api?
): Either<DataError.Network, Unit> =
    when {
        isSuccessStatus(status) -> Either.Success(Unit)
        status == HTTP_UNAUTHORIZED -> Either.Failure(parsedError ?: DataError.Network.Unauthorized)
        status in 300..499 -> Either.Failure(parsedError ?: DataError.Network.Client)
        status in 500..599 -> Either.Failure(parsedError ?: DataError.Network.Server)
        else -> Either.Failure(parsedError ?: DataError.Network.Unknown)
    }

/**
 * Reads the non-2xx response body as an [ApiErrorEnvelope] and lifts it to [DataError.Network.Api].
 * An inline-implementation detail of [safeApiCall]; not part of the supported public contract.
 *
 * Retains the envelope when it carries any structured signal — `message`, a nested `error`, **or** a
 * top-level `code` — so a flat rate-limit body like `{"code":429,"retry_after":30}` survives instead
 * of collapsing to the status bucket. `retry_after` is read from the top level as well as the nested
 * `error`. A body that is absent or non-JSON yields `null`, falling back to the bucket default.
 */
@PublishedApi
internal suspend fun parseApiError(response: HttpResponse): DataError.Network.Api? =
    runCatching { response.body<ApiErrorEnvelope>() }
        .getOrNull()
        ?.takeIf { it.message != null || it.error != null || it.code != null }
        ?.let { envelope ->
            DataError.Network.Api(
                code = envelope.code,
                message = envelope.message,
                errorCode = envelope.error?.errorCode,
                retryAfter = envelope.error?.retryAfter ?: envelope.retryAfter
            )
        }

/**
 * Retries [block] with exponential backoff + jitter. An inline-implementation detail of [safeApiCall],
 * not a general-purpose public utility: only idempotent calls are retried ([isIdempotent]) and only on
 * transient I/O ([IOException]/[SocketTimeoutException]); everything else surfaces on the first failure
 * so non-idempotent writes are never silently replayed.
 */
@PublishedApi
internal suspend fun <T> retryWithBackoff(
    times: Int = DEFAULT_RETRY_COUNT,
    initialDelay: Long = INITIAL_RETRY_DELAY_MS,
    maxDelay: Long = MAX_RETRY_DELAY_MS,
    factor: Double = RETRY_BACKOFF_FACTOR,
    isIdempotent: Boolean,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (
            @Suppress("TooGenericExceptionCaught") e: Exception
        ) {
            @Suppress("InstanceOfCheckForException")
            val retryable = isIdempotent && (e is IOException || e is SocketTimeoutException)
            if (!retryable) throw e
        }
        val jitter = Random.nextLong(0, (currentDelay / 2) + 1)
        // Cap the realized sleep at maxDelay: jitter (up to currentDelay/2) on top of a capped
        // currentDelay would otherwise reach ~1.5x the cap, exceeding MAX_RETRY_DELAY_MS.
        delay((currentDelay + jitter).coerceAtMost(maxDelay))
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block()
}
