package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
