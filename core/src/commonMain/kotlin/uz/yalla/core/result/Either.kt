package uz.yalla.core.result

public sealed interface Either<out E, out D> {
    public data class Failure<E>(
        val error: E
    ) : Either<E, Nothing>

    public data class Success<D>(
        val data: D
    ) : Either<Nothing, D>
}

public inline fun <E, D> Either<E, D>.onSuccess(action: (D) -> Unit): Either<E, D> {
    if (this is Either.Success) action(data)
    return this
}

public inline fun <E, D> Either<E, D>.onFailure(action: (E) -> Unit): Either<E, D> {
    if (this is Either.Failure) action(error)
    return this
}

public inline fun <E, D, R> Either<E, D>.mapSuccess(transform: (D) -> R): Either<E, R> = when (this) {
    is Either.Failure -> Either.Failure(error)
    is Either.Success -> Either.Success(transform(data))
}

public inline fun <E, D, R> Either<E, D>.mapFailure(transform: (E) -> R): Either<R, D> = when (this) {
    is Either.Failure -> Either.Failure(transform(error))
    is Either.Success -> Either.Success(data)
}

public inline fun <E, D> Either<E, D>.getOrNull(): D? = when (this) {
    is Either.Success -> data
    is Either.Failure -> null
}

public inline fun <E, D> Either<E, D>.getOrThrow(): D = when (this) {
    is Either.Success -> data
    is Either.Failure -> error("Either.Failure: $error")
}

public inline fun <E, D, R> Either<E, D>.fold(
    ifFailure: (E) -> R,
    ifSuccess: (D) -> R
): R = when (this) {
    is Either.Failure -> ifFailure(error)
    is Either.Success -> ifSuccess(data)
}
