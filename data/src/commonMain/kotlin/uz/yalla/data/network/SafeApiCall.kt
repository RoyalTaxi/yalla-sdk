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
import uz.yalla.data.remote.ApiErrorBody
import kotlin.random.Random

suspend inline fun <reified T> safeApiCall(
    isIdempotent: Boolean = false,
    crossinline call: suspend () -> HttpResponse
): Either<T, DataError.Network> =
    try {
        val response = retryIO(isIdempotent = isIdempotent) { call() }
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
                    response.body<ApiErrorBody>().message
                } catch (_: Exception) {
                    null
                }
                if (!message.isNullOrBlank()) {
                    Either.Failure(
                        DataError.Network.ClientWithMessage(
                            code = response.status.value,
                            message = message
                        )
                    )
                } else {
                    Either.Failure(DataError.Network.Client)
                }
            }
            in 500..599 -> Either.Failure(DataError.Network.Server)
            else -> Either.Failure(DataError.Network.Unknown)
        }
    } catch (e: ServerResponseException) {
        Either.Failure(DataError.Network.Server)
    } catch (e: ClientRequestException) {
        val message = try {
            e.response.body<ApiErrorBody>().message
        } catch (_: Exception) {
            null
        }
        if (!message.isNullOrBlank()) {
            Either.Failure(
                DataError.Network.ClientWithMessage(
                    code = e.response.status.value,
                    message = message
                )
            )
        } else {
            Either.Failure(DataError.Network.Client)
        }
    } catch (e: RedirectResponseException) {
        Either.Failure(DataError.Network.Client)
    } catch (e: IOException) {
        Either.Failure(DataError.Network.Connection)
    } catch (e: SocketTimeoutException) {
        Either.Failure(DataError.Network.Timeout)
    } catch (e: SerializationException) {
        Either.Failure(DataError.Network.Serialization)
    } catch (e: ResponseException) {
        Either.Failure(DataError.Network.Unknown)
    } catch (e: GuestBlockedException) {
        Either.Failure(DataError.Network.Guest)
    }

suspend fun <T> retryIO(
    times: Int = 3,
    initialDelay: Long = 200,
    maxDelay: Long = 2_000,
    factor: Double = 2.0,
    isIdempotent: Boolean,
    block: suspend () -> T
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
