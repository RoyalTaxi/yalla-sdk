package uz.yalla.telemetry.crash

/**
 * Destination for crash/exception reports routed by `Telemetry`.
 *
 * Implementations must be non-blocking and may be called off the main thread,
 * including from a crash-time `CoroutineExceptionHandler`. Prefer routing through
 * `Telemetry.recordCrash`, which isolates a throwing reporter; calling [record]
 * directly is unguarded.
 */
public interface CrashReporter {
    /** Records [throwable] as a non-fatal report. Must not block. */
    public fun record(throwable: Throwable)

    /**
     * Associates subsequent reports with a user, or clears it when [userId] is
     * `null`. [userId] must be an opaque, non-PII pseudonym (never a phone/email/name).
     */
    public fun setUser(userId: String?)

    /** No-op reporter; the default until a real one is installed. */
    public object Noop : CrashReporter {
        override fun record(throwable: Throwable) {}

        override fun setUser(userId: String?) {}
    }
}
