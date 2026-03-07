package uz.yalla.core.result

/**
 * A discriminated union representing either a successful result or a failure.
 *
 * Preferred over try-catch for business logic error handling. All repository
 * and use-case functions return `Either<Data, DataError>`.
 *
 * ## Usage
 * ```kotlin
 * val result: Either<Order, DataError> = orderRepository.getOrder(id)
 * result
 *     .onSuccess { order -> updateUi(order) }
 *     .onFailure { error -> handleError(error) }
 * ```
 *
 * @param D The success data type
 * @param E The error type (typically [uz.yalla.core.error.DataError])
 * @see onSuccess
 * @see onFailure
 * @since 0.0.1
 */
sealed interface Either<out D, out E> {
    /** Successful result containing [data]. */
    data class Success<D>(val data: D) : Either<D, Nothing>

    /** Failed result containing [error]. */
    data class Failure<E>(val error: E) : Either<Nothing, E>
}

/**
 * Executes [action] if this is [Either.Success], then returns the same [Either] for chaining.
 *
 * @param action Callback invoked with the success data
 * @return This same [Either] instance
 */
inline fun <D, E> Either<D, E>.onSuccess(action: (D) -> Unit): Either<D, E> {
    if (this is Either.Success) action(data)
    return this
}

/**
 * Executes [action] if this is [Either.Failure], then returns the same [Either] for chaining.
 *
 * @param action Callback invoked with the error
 * @return This same [Either] instance
 */
inline fun <D, E> Either<D, E>.onFailure(action: (E) -> Unit): Either<D, E> {
    if (this is Either.Failure) action(error)
    return this
}

/**
 * Transforms the success [data] using [transform], preserving failures unchanged.
 *
 * ```kotlin
 * val orderId: Either<Int, DataError> =
 *     service.createOrder(request)
 *         .mapSuccess { response -> response.result?.orderId.or0() }
 * ```
 *
 * @param transform Mapping function applied to [Either.Success.data]
 * @return New [Either] with transformed success type, or the original [Either.Failure]
 */
inline fun <D, E, R> Either<D, E>.mapSuccess(transform: (D) -> R): Either<R, E> =
    when (this) {
        is Either.Failure -> Either.Failure(error)
        is Either.Success -> Either.Success(transform(data))
    }
