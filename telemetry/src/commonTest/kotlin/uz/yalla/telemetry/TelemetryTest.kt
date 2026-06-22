package uz.yalla.telemetry

import uz.yalla.telemetry.crash.CrashReporter
import uz.yalla.telemetry.event.AnalyticsEvent
import uz.yalla.telemetry.event.event
import uz.yalla.telemetry.sink.TelemetrySink
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TelemetryTest {
    @AfterTest
    fun tearDown() {
        Telemetry.install(emptyList(), CrashReporter.Noop)
    }

    @Test
    fun trackFansOutToEverySink() {
        val a = RecordingSink()
        val b = RecordingSink()
        Telemetry.install(listOf(a, b))

        val e = event("x")
        Telemetry.track(e)

        assertEquals(listOf(e), a.events)
        assertEquals(listOf(e), b.events)
    }

    @Test
    fun trackContinuesAfterAThrowingSinkAndDoesNotThrow() {
        val good = RecordingSink()
        Telemetry.install(listOf(ThrowingSink, good))

        val e = event("boom")
        Telemetry.track(e)

        assertEquals(listOf(e), good.events)
    }

    @Test
    fun setUserFansOutAndContinuesAfterAThrowingSink() {
        val good = RecordingSink()
        Telemetry.install(listOf(ThrowingSink, good))

        Telemetry.setUser("u-1")

        assertEquals(listOf<String?>("u-1"), good.users)
    }

    @Test
    fun setUserClearsWithNull() {
        val good = RecordingSink()
        Telemetry.install(listOf(good))

        Telemetry.setUser(null)

        assertEquals(listOf<String?>(null), good.users)
    }

    @Test
    fun setUserDoesNotThrowWhenCrashReporterThrows() {
        val good = RecordingSink()
        Telemetry.install(listOf(good), ThrowingReporter)

        Telemetry.setUser("u-2")

        assertEquals(listOf<String?>("u-2"), good.users)
    }

    @Test
    fun recordCrashIsolatesAThrowingReporter() {
        Telemetry.install(emptyList(), ThrowingReporter)

        Telemetry.recordCrash(RuntimeException("from coroutine"))
    }

    @Test
    fun recordCrashRoutesToTheInstalledReporter() {
        val reporter = RecordingReporter()
        Telemetry.install(emptyList(), reporter)

        val t = IllegalStateException("kaboom")
        Telemetry.recordCrash(t)

        assertEquals(listOf<Throwable>(t), reporter.recorded)
    }

    @Test
    fun trackOnEmptyInstallIsASafeNoop() {
        Telemetry.install(emptyList(), CrashReporter.Noop)

        Telemetry.track(event("nobody_home"))
        Telemetry.setUser("u-3")
        Telemetry.recordCrash(RuntimeException("silent"))

        assertTrue(true)
    }

    private class RecordingSink : TelemetrySink {
        val events = mutableListOf<AnalyticsEvent>()
        val users = mutableListOf<String?>()

        override fun track(event: AnalyticsEvent) {
            events += event
        }

        override fun setUser(userId: String?) {
            users += userId
        }
    }

    private object ThrowingSink : TelemetrySink {
        override fun track(event: AnalyticsEvent): Unit = throw RuntimeException("sink.track")

        override fun setUser(userId: String?): Unit = throw RuntimeException("sink.setUser")
    }

    private class RecordingReporter : CrashReporter {
        val recorded = mutableListOf<Throwable>()

        override fun record(throwable: Throwable) {
            recorded += throwable
        }

        override fun setUser(userId: String?) {}
    }

    private object ThrowingReporter : CrashReporter {
        override fun record(throwable: Throwable): Unit = throw RuntimeException("crash.record")

        override fun setUser(userId: String?): Unit = throw RuntimeException("crash.setUser")
    }
}
