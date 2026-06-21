package uz.yalla.core.result

/**
 * A total result type holding exactly one of an error [E] ([Failure]) or a value [D] ([Success]).
 *
 * The SDK's universal result: domain operations return `Either<DomainError, T>`. Branch with the
 * total accessors ([fold], [getOrNull], [getOrElse]) rather than [getOrThrow] on iOS.
 */
public sealed interface Either<out E, out D> {
    /** The error case, carrying the typed [error]. */
    public data class Failure<E>(
        val error: E
    ) : Either<E, Nothing>

    /** The success case, carrying the [data]. */
    public data class Success<D>(
        val data: D
    ) : Either<Nothing, D>
}

/** Runs [action] with the value if this is a [Either.Success], then returns this unchanged. */
public inline fun <E, D> Either<E, D>.onSuccess(action: (D) -> Unit): Either<E, D> {
    if (this is Either.Success) action(data)
    return this
}

/** Runs [action] with the error if this is a [Either.Failure], then returns this unchanged. */
public inline fun <E, D> Either<E, D>.onFailure(action: (E) -> Unit): Either<E, D> {
    if (this is Either.Failure) action(error)
    return this
}

/** Maps the success value with [transform], leaving a [Either.Failure] untouched. */
public inline fun <E, D, R> Either<E, D>.mapSuccess(transform: (D) -> R): Either<E, R> =
    when (this) {
        is Either.Failure -> Either.Failure(error)
        is Either.Success -> Either.Success(transform(data))
    }

/** Maps the error with [transform], leaving a [Either.Success] untouched. */
public inline fun <E, D, R> Either<E, D>.mapFailure(transform: (E) -> R): Either<R, D> =
    when (this) {
        is Either.Failure -> Either.Failure(transform(error))
        is Either.Success -> Either.Success(data)
    }

/** Chains [transform] on the success value (which itself returns an [Either]); short-circuits on failure. */
public inline fun <E, D, R> Either<E, D>.flatMap(transform: (D) -> Either<E, R>): Either<E, R> =
    when (this) {
        is Either.Failure -> Either.Failure(error)
        is Either.Success -> transform(data)
    }

/** Returns the success value, or `null` on a [Either.Failure]. */
public inline fun <E, D> Either<E, D>.getOrNull(): D? =
    when (this) {
        is Either.Success -> data
        is Either.Failure -> null
    }

/**
 * Returns the success value, or throws [IllegalStateException] on a [Either.Failure].
 *
 * The thrown message does NOT interpolate the error payload, so a failure carrying sensitive data
 * (the error type `E` is unconstrained) cannot leak its `toString()` into a stack trace or crash
 * report (CWE-532). Inspect the typed error via [getOrNull]/[getOrElse]/[fold] instead.
 *
 * Prefer the total accessors ([getOrNull]/[getOrElse]/[fold]) on iOS: a thrown Kotlin exception
 * does not bridge to a catchable Swift error here.
 */
public inline fun <E, D> Either<E, D>.getOrThrow(): D =
    when (this) {
        is Either.Success -> data
        is Either.Failure -> error("Either.getOrThrow called on Failure")
    }

/** Returns the success value, or the result of [fallback] applied to the error. */
public inline fun <E, D> Either<E, D>.getOrElse(fallback: (E) -> D): D =
    when (this) {
        is Either.Success -> data
        is Either.Failure -> fallback(error)
    }

/** Collapses both cases to a single [R] via [ifFailure] / [ifSuccess]. */
public inline fun <E, D, R> Either<E, D>.fold(
    ifFailure: (E) -> R,
    ifSuccess: (D) -> R
): R =
    when (this) {
        is Either.Failure -> ifFailure(error)
        is Either.Success -> ifSuccess(data)
    }
