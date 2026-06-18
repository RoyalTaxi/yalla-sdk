package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Characterization of the epoch-timestamp formatters. [normalizedEpochSeconds] is fully
 * deterministic and pinned exactly (the seconds-vs-milliseconds heuristic is a recurring bug spot).
 * The formatters themselves depend on the system time zone, so they are pinned by their empty-input
 * guards and their output *shape* (dd.MM.yyyy / HH:mm) rather than a time-zone-specific value.
 */
class DateTimeFormattingTest {
    @Test
    fun secondsValueBelowThresholdIsUnchanged() {
        assertEquals(1_700_000_000L, 1_700_000_000L.normalizedEpochSeconds())
    }

    @Test
    fun millisecondsValueAboveThresholdIsDividedToSeconds() {
        assertEquals(1_700_000_000L, 1_700_000_000_000L.normalizedEpochSeconds())
    }

    @Test
    fun thresholdItselfIsTreatedAsSeconds() {
        assertEquals(10_000_000_000L, 10_000_000_000L.normalizedEpochSeconds())
    }

    @Test
    fun justAboveThresholdIsTreatedAsMilliseconds() {
        assertEquals(10_000_000L, 10_000_000_001L.normalizedEpochSeconds())
    }

    @Test
    fun nullAndNonPositiveTimestampsFormatToEmptyString() {
        assertEquals("", (null as Long?).toLocalFormattedDate())
        assertEquals("", 0L.toLocalFormattedDate())
        assertEquals("", (-5L).toLocalFormattedDate())
        assertEquals("", (null as Long?).toLocalFormattedTime())
        assertEquals("", 0L.toLocalFormattedTime())
    }

    @Test
    fun positiveTimestampFormatsToExpectedShape() {
        val date = 1_700_000_000L.toLocalFormattedDate()
        val time = 1_700_000_000L.toLocalFormattedTime()
        assertTrue(Regex("""\d{2}\.\d{2}\.\d{4}""").matches(date), "date was '$date'")
        assertTrue(Regex("""\d{2}:\d{2}""").matches(time), "time was '$time'")
    }
}
