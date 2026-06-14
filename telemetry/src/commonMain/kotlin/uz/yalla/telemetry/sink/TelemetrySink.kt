package uz.yalla.telemetry.sink

import uz.yalla.telemetry.event.AnalyticsEvent

public interface TelemetrySink {
    public fun track(event: AnalyticsEvent)
    public fun setUser(userId: String?)
}
