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
import uz.yalla.core.error.DataError
import uz.yalla.core.result.Either
import kotlin.random.Random

private const val DEFAULT_RETRY_COUNT = 3
private const val INITIAL_RETRY_DELAY_MS = 200L
private const val MAX_RETRY_DELAY_MS = 2_000L
private const val RETRY_BACKOFF_FACTOR = 2.0

public suspend inline fun <reified T> safeApiCall(
    isIdempotent: Boolean = false,
    crossinline call: suspend () -> HttpResponse
): Either<DataError.Network, T> = try {
    val response = retryWithBackoff(isIdempotent = isIdempotent) { call() }
    when (response.status.value) {
        in 200..299 -> {
            if (T::class == Unit::class) {
                Either.Success(Unit as T)
            } else {
                Either.Success(response.body())
            }
        }

        in 300..399 -> Either.Failure(parseApiError(response) ?: DataError.Network.Client)
        in 400..499 -> Either.Failure(parseApiError(response) ?: DataError.Network.Client)
        in 500..599 -> Either.Failure(parseApiError(response) ?: DataError.Network.Server)
        else -> Either.Failure(parseApiError(response) ?: DataError.Network.Unknown)
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
internal suspend fun parseApiError(response: HttpResponse): DataError.Network.Api? = runCatching { response.body<ApiErrorEnvelope>() }
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
