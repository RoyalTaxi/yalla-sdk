package uz.yalla.core.result

sealed interface Either<out E, out D> {
    data class Failure<E>(
        val error: E
    ) : Either<E, Nothing>

    data class Success<D>(
        val data: D
    ) : Either<Nothing, D>
}

inline fun <E, D> Either<E, D>.onSuccess(action: (D) -> Unit): Either<E, D> {
    if (this is Either.Success) action(data)
    return this
}

inline fun <E, D> Either<E, D>.onFailure(action: (E) -> Unit): Either<E, D> {
    if (this is Either.Failure) action(error)
    return this
}

inline fun <E, D, R> Either<E, D>.mapSuccess(transform: (D) -> R): Either<E, R> = when (this) {
    is Either.Failure -> Either.Failure(error)
    is Either.Success -> Either.Success(transform(data))
}

inline fun <E, D, R> Either<E, D>.mapFailure(transform: (E) -> R): Either<R, D> = when (this) {
    is Either.Failure -> Either.Failure(transform(error))
    is Either.Success -> Either.Success(data)
}

inline fun <E, D> Either<E, D>.getOrNull(): D? = when (this) {
    is Either.Success -> data
    is Either.Failure -> null
}

inline fun <E, D> Either<E, D>.getOrThrow(): D = when (this) {
    is Either.Success -> data
    is Either.Failure -> error("Either.Failure: $error")
}

inline fun <E, D, R> Either<E, D>.fold(
    ifFailure: (E) -> R,
    ifSuccess: (D) -> R
): R = when (this) {
    is Either.Failure -> ifFailure(error)
    is Either.Success -> ifSuccess(data)
}
