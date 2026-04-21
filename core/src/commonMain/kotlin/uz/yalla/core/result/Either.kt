package uz.yalla.core.result

/**
 * A discriminated union representing either a failure or a successful result.
 *
 * Preferred over try-catch for business logic error handling. All repository
 * and use-case functions return `Either<DataError, Data>` (error first, success second).
 *
 * The generic parameter order follows ecosystem convention: the error type `E`
 * is the first type parameter, the success data type `D` is the second. This
 * matches Arrow's `Either<Left, Right>`, Rust's `Result<T, E>` (dual), and the
 * broader functional-programming community.
 *
 * ## Usage
 * ```kotlin
 * val result: Either<DataError, Order> = orderRepository.getOrder(id)
 * result
 *     .onSuccess { order -> updateUi(order) }
 *     .onFailure { error -> handleError(error) }
 * ```
 *
 * @param E The error type (typically [uz.yalla.core.error.DataError])
 * @param D The success data type
 * @see onSuccess
 * @see onFailure
 * @since 0.0.1
 */
sealed interface Either<out E, out D> {
    /**
     * Failed result containing [error].
     *
     * @param E The error type
     * @property error The failure reason
     * @since 0.0.1
     */
    data class Failure<E>(val error: E) : Either<E, Nothing>

    /**
     * Successful result containing [data].
     *
     * @param D The success data type
     * @property data The successful result value
     * @since 0.0.1
     */
    data class Success<D>(val data: D) : Either<Nothing, D>
}

/**
 * Executes [action] if this is [Either.Success], then returns the same [Either] for chaining.
 *
 * @param action Callback invoked with the success data
 * @return This same [Either] instance
 */
inline fun <E, D> Either<E, D>.onSuccess(action: (D) -> Unit): Either<E, D> {
    if (this is Either.Success) action(data)
    return this
}

/**
 * Executes [action] if this is [Either.Failure], then returns the same [Either] for chaining.
 *
 * @param action Callback invoked with the error
 * @return This same [Either] instance
 */
inline fun <E, D> Either<E, D>.onFailure(action: (E) -> Unit): Either<E, D> {
    if (this is Either.Failure) action(error)
    return this
}

/**
 * Transforms the success [data] using [transform], preserving failures unchanged.
 *
 * ```kotlin
 * val orderId: Either<DataError, Int> =
 *     service.createOrder(request)
 *         .mapSuccess { response -> response.result?.orderId.or0() }
 * ```
 *
 * @param transform Mapping function applied to [Either.Success.data]
 * @return New [Either] with transformed success type, or the original [Either.Failure]
 */
inline fun <E, D, R> Either<E, D>.mapSuccess(transform: (D) -> R): Either<E, R> =
    when (this) {
        is Either.Failure -> Either.Failure(error)
        is Either.Success -> Either.Success(transform(data))
    }

/**
 * Transforms the failure [error] using [transform], preserving successes unchanged.
 *
 * ```kotlin
 * val result: Either<AppError, Order> =
 *     service.getOrder(id)
 *         .mapFailure { networkError -> networkError.toAppError() }
 * ```
 *
 * @param transform Mapping function applied to [Either.Failure.error]
 * @return New [Either] with transformed error type, or the original [Either.Success]
 */
inline fun <E, D, R> Either<E, D>.mapFailure(transform: (E) -> R): Either<R, D> =
    when (this) {
        is Either.Failure -> Either.Failure(transform(error))
        is Either.Success -> Either.Success(data)
    }

/**
 * Returns the success data if this is [Either.Success], or `null` otherwise.
 *
 * @since 0.0.9
 */
inline fun <E, D> Either<E, D>.getOrNull(): D? =
    when (this) {
        is Either.Success -> data
        is Either.Failure -> null
    }

/**
 * Returns the success data if this is [Either.Success], or throws if it's a failure.
 * Caller owns the exception-translation policy.
 *
 * @throws IllegalStateException if this is [Either.Failure]. Message includes the error.
 * @since 0.0.9
 */
inline fun <E, D> Either<E, D>.getOrThrow(): D =
    when (this) {
        is Either.Success -> data
        is Either.Failure -> error("Either.Failure: $error")
    }

/**
 * Returns [ifFailure] applied to the error when this is [Either.Failure],
 * or [ifSuccess] applied to the data when this is [Either.Success].
 *
 * Typical use: collapse an Either to a single value for UI rendering.
 *
 * @since 0.0.9
 */
inline fun <E, D, R> Either<E, D>.fold(
    ifFailure: (E) -> R,
    ifSuccess: (D) -> R,
): R =
    when (this) {
        is Either.Failure -> ifFailure(error)
        is Either.Success -> ifSuccess(data)
    }
