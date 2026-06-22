package uz.yalla.telemetry.sink

import uz.yalla.telemetry.event.AnalyticsEvent

public class DebugLogSink : TelemetrySink {
    override fun track(event: AnalyticsEvent) {
        println(renderLine(event))
    }

    override fun setUser(userId: String?) {
        println("[Telemetry] user = $userId")
    }
}

internal fun renderLine(event: AnalyticsEvent): String =
    if (event.params.isEmpty()) {
        "[Telemetry] ${event.name}"
    } else {
        "[Telemetry] ${event.name} ${event.params}"
    }
