package uz.yalla.core.error

sealed interface Either<out D, out E> {
    data class Success<D>(
        val data: D
    ) : Either<D, Nothing>

    data class Error<E>(
        val error: E
    ) : Either<Nothing, E>
}

typealias DataResult<D> = Either<D, DataError>
typealias NetworkResult<D> = Either<D, DataError.Network>
