package uz.yalla.core.error

inline fun <D, E> Either<D, E>.onSuccess(action: (D) -> Unit): Either<D, E> {
    if (this is Either.Success) action(data)
    return this
}

inline fun <D, E> Either<D, E>.onFailure(action: (E) -> Unit): Either<D, E> {
    if (this is Either.Error) action(error)
    return this
}

inline fun <D, E, R> Either<D, E>.map(transform: (D) -> R): Either<R, E> =
    when (this) {
        is Either.Success -> Either.Success(transform(data))
        is Either.Error -> this
    }

inline fun <D, E, R> Either<D, E>.flatMap(transform: (D) -> Either<R, E>): Either<R, E> =
    when (this) {
        is Either.Success -> transform(data)
        is Either.Error -> this
    }

val <D, E> Either<D, E>.isSuccess: Boolean
    get() = this is Either.Success

val <D, E> Either<D, E>.isError: Boolean
    get() = this is Either.Error

fun <D, E> Either<D, E>.getOrNull(): D? = (this as? Either.Success)?.data

fun <D, E> Either<D, E>.errorOrNull(): E? = (this as? Either.Error)?.error

inline fun <D, E> Either<D, E>.getOrElse(defaultValue: (E) -> D): D =
    when (this) {
        is Either.Success -> data
        is Either.Error -> defaultValue(error)
    }

/**
 * Transforms both sides of [Either] into a single value [R].
 * This is the canonical way to handle an Either result.
 */
inline fun <D, E, R> Either<D, E>.fold(
    onSuccess: (D) -> R,
    onError: (E) -> R,
): R =
    when (this) {
        is Either.Success -> onSuccess(data)
        is Either.Error -> onError(error)
    }

/**
 * Transforms the error value if this is [Either.Error], leaving [Either.Success] unchanged.
 * Useful for mapping errors between layers (data -> domain -> presentation).
 */
inline fun <D, E, F> Either<D, E>.mapError(transform: (E) -> F): Either<D, F> =
    when (this) {
        is Either.Success -> this
        is Either.Error -> Either.Error(transform(error))
    }

/**
 * Recovers from an error by transforming it into a success value.
 */
inline fun <D, E> Either<D, E>.recover(transform: (E) -> D): Either.Success<D> =
    when (this) {
        is Either.Success -> this
        is Either.Error -> Either.Success(transform(error))
    }

/**
 * Catches exceptions and wraps them in [Either].
 */
inline fun <D> eitherCatch(block: () -> D): Either<D, Throwable> =
    try {
        Either.Success(block())
    } catch (e: Throwable) {
        Either.Error(e)
    }
