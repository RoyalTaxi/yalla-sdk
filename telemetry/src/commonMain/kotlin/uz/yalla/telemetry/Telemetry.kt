package uz.yalla.telemetry

import uz.yalla.telemetry.crash.CrashReporter
import uz.yalla.telemetry.event.AnalyticsEvent
import uz.yalla.telemetry.sink.TelemetrySink

object Telemetry {
    private var sinks: List<TelemetrySink> = emptyList()

    var crash: CrashReporter = CrashReporter.Noop
        private set

    fun install(sinks: List<TelemetrySink>, crash: CrashReporter = CrashReporter.Noop) {
        this.sinks = sinks
        this.crash = crash
    }

    fun track(event: AnalyticsEvent) {
        sinks.forEach { sink -> runCatching { sink.track(event) } }
    }

    fun setUser(userId: String?) {
        sinks.forEach { sink -> runCatching { sink.setUser(userId) } }
        crash.setUser(userId)
    }
}
