package uz.yalla.core.result

sealed interface Either<out D, out E> {
    data class Success<D>(val data: D) : Either<D, Nothing>
    data class Failure<E>(val error: E) : Either<Nothing, E>
}

inline fun <D, E> Either<D, E>.onSuccess(action: (D) -> Unit): Either<D, E> {
    if (this is Either.Success) action(data)
    return this
}

inline fun <D, E> Either<D, E>.onFailure(action: (E) -> Unit): Either<D, E> {
    if (this is Either.Failure) action(error)
    return this
}
