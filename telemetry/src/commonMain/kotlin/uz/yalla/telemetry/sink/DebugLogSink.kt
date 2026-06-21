package uz.yalla.telemetry.sink

import uz.yalla.telemetry.event.AnalyticsEvent

/**
 * Development-only [TelemetrySink] that prints each event to standard output.
 *
 * Intended for debug builds only — it must never appear in a release sink list, as
 * it performs a synchronous `println` (Logcat on Android, stdout on iOS) on the
 * caller's thread for every event. Output format is its contract; see [renderLine].
 */
public class DebugLogSink : TelemetrySink {
    override fun track(event: AnalyticsEvent) {
        println(renderLine(event))
    }

    override fun setUser(userId: String?) {
        println("[Telemetry] user = $userId")
    }
}

/**
 * Renders the single log line for [event]: `[Telemetry] <name>` with no suffix when
 * there are no params, and `[Telemetry] <name> {…}` otherwise. Extracted as a pure
 * function so the empty-vs-non-empty branch is unit-testable without intercepting
 * `println`, and so the params string is only built when params are present.
 */
internal fun renderLine(event: AnalyticsEvent): String =
    if (event.params.isEmpty()) {
        "[Telemetry] ${event.name}"
    } else {
        "[Telemetry] ${event.name} ${event.params}"
    }
