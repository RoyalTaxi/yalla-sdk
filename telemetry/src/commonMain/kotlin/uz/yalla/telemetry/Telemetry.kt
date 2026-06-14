package uz.yalla.telemetry

import uz.yalla.telemetry.crash.CrashReporter
import uz.yalla.telemetry.event.AnalyticsEvent
import uz.yalla.telemetry.sink.TelemetrySink

public object Telemetry {
    private var sinks: List<TelemetrySink> = emptyList()

    public var crash: CrashReporter = CrashReporter.Noop
        private set

    public fun install(
        sinks: List<TelemetrySink>,
        crash: CrashReporter = CrashReporter.Noop
    ) {
        this.sinks = sinks
        this.crash = crash
    }

    public fun track(event: AnalyticsEvent) {
        sinks.forEach { sink -> runCatching { sink.track(event) } }
    }

    public fun setUser(userId: String?) {
        sinks.forEach { sink -> runCatching { sink.setUser(userId) } }
        crash.setUser(userId)
    }
}
