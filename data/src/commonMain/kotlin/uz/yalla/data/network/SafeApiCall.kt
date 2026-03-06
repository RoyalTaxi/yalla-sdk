package uz.yalla.data.network

import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.delay
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import uz.yalla.core.error.DataError
import uz.yalla.core.result.Either
import uz.yalla.data.api.ApiErrorResponse
import kotlin.random.Random

private const val DEFAULT_RETRY_COUNT = 3
private const val INITIAL_RETRY_DELAY_MS = 200L
private const val MAX_RETRY_DELAY_MS = 2_000L
private const val RETRY_BACKOFF_FACTOR = 2.0

/**
 * Executes an API call with error handling and optional retry.
 *
 * Wraps the raw [HttpResponse] into [Either] — mapping HTTP status codes
 * and exceptions to [DataError.Network] subtypes. Idempotent calls are
 * retried on IO failures with exponential backoff.
 *
 * @param T the expected success response type
 * @param isIdempotent whether the call can be safely retried on IO failure
 * @param call the suspend function producing the HTTP response
 * @return [Either.Success] with parsed body, or [Either.Failure] with typed error
 * @since 0.0.1
 */
suspend inline fun <reified T> safeApiCall(
    isIdempotent: Boolean = false,
    crossinline call: suspend () -> HttpResponse,
): Either<T, DataError.Network> =
    try {
        val response = retryWithBackoff(isIdempotent = isIdempotent) { call() }
        when (response.status.value) {
            in 200..299 -> {
                if (T::class == Unit::class) {
                    Either.Success(Unit as T)
                } else {
                    Either.Success(response.body())
                }
            }
            in 300..399 -> Either.Failure(DataError.Network.Client)
            in 400..499 -> {
                val message = try {
                    response.body<ApiErrorResponse>().message
                } catch (_: Exception) {
                    null
                }
                if (!message.isNullOrBlank()) {
                    Either.Failure(
                        DataError.Network.ClientWithMessage(
                            code = response.status.value,
                            message = message,
                        )
                    )
                } else {
                    Either.Failure(DataError.Network.Client)
                }
            }
            in 500..599 -> Either.Failure(DataError.Network.Server)
            else -> Either.Failure(DataError.Network.Unknown)
        }
    } catch (_: ServerResponseException) {
        Either.Failure(DataError.Network.Server)
    } catch (e: ClientRequestException) {
        val message = try {
            e.response.body<ApiErrorResponse>().message
        } catch (_: Exception) {
            null
        }
        if (!message.isNullOrBlank()) {
            Either.Failure(
                DataError.Network.ClientWithMessage(
                    code = e.response.status.value,
                    message = message,
                )
            )
        } else {
            Either.Failure(DataError.Network.Client)
        }
    } catch (_: RedirectResponseException) {
        Either.Failure(DataError.Network.Client)
    } catch (_: IOException) {
        Either.Failure(DataError.Network.Connection)
    } catch (_: SocketTimeoutException) {
        Either.Failure(DataError.Network.Timeout)
    } catch (_: SerializationException) {
        Either.Failure(DataError.Network.Serialization)
    } catch (_: ResponseException) {
        Either.Failure(DataError.Network.Unknown)
    } catch (_: GuestBlockedException) {
        Either.Failure(DataError.Network.Guest)
    }

/**
 * Retries a suspending [block] with exponential backoff and jitter.
 *
 * Only retries on [IOException] and [SocketTimeoutException] when
 * [isIdempotent] is `true`. Non-retryable exceptions propagate immediately.
 *
 * @param times maximum number of attempts
 * @param initialDelay delay before the first retry in milliseconds
 * @param maxDelay upper bound for delay in milliseconds
 * @param factor multiplier applied to delay after each retry
 * @param isIdempotent whether the operation is safe to retry
 * @param block the operation to execute
 * @since 0.0.1
 */
@PublishedApi
internal suspend fun <T> retryWithBackoff(
    times: Int = DEFAULT_RETRY_COUNT,
    initialDelay: Long = INITIAL_RETRY_DELAY_MS,
    maxDelay: Long = MAX_RETRY_DELAY_MS,
    factor: Double = RETRY_BACKOFF_FACTOR,
    isIdempotent: Boolean,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: Exception) {
            val retryable = isIdempotent && (e is IOException || e is SocketTimeoutException)
            if (!retryable) throw e
        }
        val jitter = Random.nextLong(0, (currentDelay / 2) + 1)
        delay(currentDelay + jitter)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block()
}
