package uz.yalla.telemetry

import uz.yalla.telemetry.crash.CrashReporter
import uz.yalla.telemetry.event.AnalyticsEvent
import uz.yalla.telemetry.sink.TelemetrySink
import kotlin.concurrent.Volatile

public object Telemetry {
    @Volatile
    private var sinks: List<TelemetrySink> = emptyList()

    @Volatile
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
        val current = sinks
        current.forEach { sink -> runCatching { sink.track(event) } }
    }

    public fun setUser(userId: String?) {
        val currentSinks = sinks
        val currentCrash = crash
        currentSinks.forEach { sink -> runCatching { sink.setUser(userId) } }
        runCatching { currentCrash.setUser(userId) }
    }

    public fun recordCrash(throwable: Throwable) {
        runCatching { crash.record(throwable) }
    }
}
