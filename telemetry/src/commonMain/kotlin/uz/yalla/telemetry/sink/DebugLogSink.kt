package uz.yalla.telemetry.sink

import uz.yalla.telemetry.event.AnalyticsEvent

public class DebugLogSink : TelemetrySink {
    override fun track(event: AnalyticsEvent) {
        val params = if (event.params.isEmpty()) "" else " ${event.params}"
        println("[Telemetry] ${event.name}$params")
    }

    override fun setUser(userId: String?) {
        println("[Telemetry] user = $userId")
    }
}
