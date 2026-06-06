package uz.yalla.telemetry.sink

import uz.yalla.telemetry.event.AnalyticsEvent

interface TelemetrySink {
    fun track(event: AnalyticsEvent)
    fun setUser(userId: String?)
}
