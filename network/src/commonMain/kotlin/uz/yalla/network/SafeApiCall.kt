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
import uz.yalla.network.error.DataError
import uz.yalla.core.result.Either
import kotlin.random.Random

private const val DEFAULT_RETRY_COUNT = 3
private const val INITIAL_RETRY_DELAY_MS = 200L
private const val MAX_RETRY_DELAY_MS = 2_000L
private const val RETRY_BACKOFF_FACTOR = 2.0

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
    } catch (_: ResponseException) {
        Either.Failure(DataError.Network.Unknown)
    } catch (_: GuestBlockedException) {
        Either.Failure(DataError.Network.Guest)
    }

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
 */
@PublishedApi
internal fun statusToEither(
    status: Int,
    parsedError: DataError.Network.Api?
): Either<DataError.Network, Unit> =
    when {
        isSuccessStatus(status) -> Either.Success(Unit)
        status in 300..499 -> Either.Failure(parsedError ?: DataError.Network.Client)
        status in 500..599 -> Either.Failure(parsedError ?: DataError.Network.Server)
        else -> Either.Failure(parsedError ?: DataError.Network.Unknown)
    }

@PublishedApi
internal suspend fun parseApiError(response: HttpResponse): DataError.Network.Api? =
    runCatching { response.body<ApiErrorEnvelope>() }
        .getOrNull()
        ?.takeIf { it.message != null || it.error != null }
        ?.let { envelope ->
            DataError.Network.Api(
                code = envelope.code,
                message = envelope.message,
                errorCode = envelope.error?.errorCode,
                retryAfter = envelope.error?.retryAfter
            )
        }

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
        delay(currentDelay + jitter)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block()
}
