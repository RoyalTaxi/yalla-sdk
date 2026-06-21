package uz.yalla.telemetry

import uz.yalla.telemetry.Telemetry.crash
import uz.yalla.telemetry.Telemetry.install
import uz.yalla.telemetry.Telemetry.recordCrash
import uz.yalla.telemetry.Telemetry.setUser
import uz.yalla.telemetry.Telemetry.track
import uz.yalla.telemetry.crash.CrashReporter
import uz.yalla.telemetry.event.AnalyticsEvent
import uz.yalla.telemetry.sink.TelemetrySink
import kotlin.concurrent.Volatile

/**
 * Process-wide entry point that fans analytics events out to the installed
 * [TelemetrySink]s and routes crashes to the installed [CrashReporter].
 *
 * ## Lifecycle
 * Call [install] exactly once, early in process start-up (e.g. Android
 * `Application.onCreate`, iOS app bootstrap), on the main thread, before any
 * [track]/[setUser]/[recordCrash] call. Until then this object is a safe no-op:
 * [track] drops events into an empty sink list and [crash] is [CrashReporter.Noop].
 * [install] is an unconditional, one-shot replace — see its own KDoc.
 *
 * ## Threading
 * [track], [setUser] and [recordCrash] are safe to call from any thread. The
 * installed configuration is published through `@Volatile` fields and each call
 * reads a single snapshot, so a reader either sees the pre-install no-op default
 * or the fully-installed set, never a torn `{sinks, crash}` view.
 *
 * ## Exception isolation
 * Telemetry is best-effort and never throws back to the caller: every sink call,
 * the crash-reporter `setUser`, and [recordCrash] are individually wrapped in
 * `runCatching`, so a misbehaving sink or reporter can neither break the fan-out
 * nor reach the caller. Failures are swallowed silently.
 *
 * ## Data handling
 * Event names, param keys/values, and the [setUser] id are forwarded verbatim to
 * every sink (and on to third-party analytics). Callers MUST NOT pass personally
 * identifiable information: the id must be an opaque, non-PII pseudonym (a server
 * account id or a salted hash), never a phone/email/name, and free user text
 * (search queries, addresses, card data) must never be put as a param. The
 * contract is enforced by caller discipline; the module does not redact.
 */
public object Telemetry {
    @Volatile
    private var sinks: List<TelemetrySink> = emptyList()

    /**
     * The installed crash reporter, or [CrashReporter.Noop] until [install] wires a
     * real one. Read-only to consumers; prefer [recordCrash] over calling
     * [CrashReporter.record] on this directly, as `record` here is unguarded.
     */
    @Volatile
    public var crash: CrashReporter = CrashReporter.Noop
        private set

    /**
     * Installs the [sinks] and [crash] reporter, replacing any previously installed
     * set. One-shot by contract: call once at process start-up before any other
     * entry point. A second call silently abandons the previous sinks/reporter with
     * no flush or close hook, so sinks must not hold closeable native resources that
     * need release on replacement.
     */
    public fun install(
        sinks: List<TelemetrySink>,
        crash: CrashReporter = CrashReporter.Noop
    ) {
        this.sinks = sinks
        this.crash = crash
    }

    /**
     * Records an analytics [event] on every installed sink. Best-effort: a throwing
     * sink is isolated and never breaks the fan-out or reaches the caller. Safe
     * no-op when nothing is installed.
     */
    public fun track(event: AnalyticsEvent) {
        val current = sinks
        current.forEach { sink -> runCatching { sink.track(event) } }
    }

    /**
     * Sets (or clears, when [userId] is `null`) the current user on every sink and
     * on the crash reporter. Best-effort: each call is isolated and never reaches
     * the caller. [userId] must be an opaque, non-PII pseudonym (see the type KDoc).
     */
    public fun setUser(userId: String?) {
        val currentSinks = sinks
        val currentCrash = crash
        currentSinks.forEach { sink -> runCatching { sink.setUser(userId) } }
        runCatching { currentCrash.setUser(userId) }
    }

    /**
     * Routes [throwable] to the installed crash reporter under the same
     * never-throws contract the sinks enjoy. Prefer this over
     * `Telemetry.crash.record(...)`: it isolates a throwing reporter, which matters
     * most on the crash path (e.g. inside a `CoroutineExceptionHandler`, where an
     * un-caught reporter failure would escape and crash the process).
     */
    public fun recordCrash(throwable: Throwable) {
        runCatching { crash.record(throwable) }
    }
}
