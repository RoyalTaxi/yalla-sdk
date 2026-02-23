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
import uz.yalla.core.error.Either
import uz.yalla.core.error.GuestModeRequestBlocked
import kotlin.random.Random

suspend inline fun <reified T> safeApiCall(
    isIdempotent: Boolean = false,
    crossinline call: suspend () -> HttpResponse
): Either<T, DataError.Network> =
    try {
        val response = retryIO(isIdempotent = isIdempotent) { call() }
        when (response.status.value) {
            401 -> Either.Error(DataError.Network.Unauthorized)
            302 -> Either.Error(DataError.Network.InsufficientBalance)
            in 200..299 -> {
                if (T::class == Unit::class) {
                    Either.Success(Unit as T)
                } else {
                    Either.Success(response.body())
                }
            }
            in 300..399 -> Either.Error(DataError.Network.ClientError)
            in 400..499 -> Either.Error(DataError.Network.ClientError)
            in 500..599 -> Either.Error(DataError.Network.ServerError)
            else -> Either.Error(DataError.Network.Unknown())
        }
    } catch (e: ServerResponseException) {
        Either.Error(DataError.Network.ServerError)
    } catch (e: ClientRequestException) {
        Either.Error(DataError.Network.ClientError)
    } catch (e: RedirectResponseException) {
        Either.Error(DataError.Network.ClientError)
    } catch (e: IOException) {
        Either.Error(DataError.Network.NoInternet)
    } catch (e: SocketTimeoutException) {
        Either.Error(DataError.Network.Timeout)
    } catch (e: SerializationException) {
        Either.Error(DataError.Network.SerializationError)
    } catch (e: ResponseException) {
        Either.Error(DataError.Network.Unknown())
    } catch (e: GuestModeRequestBlocked) {
        Either.Error(DataError.Network.ClientError)
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
