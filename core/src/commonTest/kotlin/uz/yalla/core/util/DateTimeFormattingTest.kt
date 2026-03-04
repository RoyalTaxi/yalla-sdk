package uz.yalla.core.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class DateTimeFormattingTest {
    @Test
    fun shouldReturnEmptyDateWhenEpochIsNull() {
        val epoch: Long? = null

        assertEquals("", epoch.toLocalFormattedDate())
    }

    @Test
    fun shouldReturnEmptyTimeWhenEpochIsNull() {
        val epoch: Long? = null

        assertEquals("", epoch.toLocalFormattedTime())
    }

    @Test
    fun shouldReturnEmptyDateAndTimeWhenEpochIsNonPositive() {
        val zeroEpoch = 0L
        val negativeEpoch = -100L

        assertEquals("", zeroEpoch.toLocalFormattedDate())
        assertEquals("", zeroEpoch.toLocalFormattedTime())
        assertEquals("", negativeEpoch.toLocalFormattedDate())
        assertEquals("", negativeEpoch.toLocalFormattedTime())
    }

    @Test
    fun shouldFormatDateAndTimeWhenEpochIsInSeconds() {
        val epochSeconds = 1_725_102_000L

        val expectedDate = expectedDate(epochSeconds)
        val expectedTime = expectedTime(epochSeconds)

        assertEquals(expectedDate, epochSeconds.toLocalFormattedDate())
        assertEquals(expectedTime, epochSeconds.toLocalFormattedTime())
    }

    @Test
    fun shouldProduceSameDateAndTimeWhenEpochIsInMilliseconds() {
        val epochSeconds = 1_725_102_000L
        val epochMillis = epochSeconds * 1_000L

        assertEquals(
            epochSeconds.toLocalFormattedDate(),
            epochMillis.toLocalFormattedDate()
        )
        assertEquals(
            epochSeconds.toLocalFormattedTime(),
            epochMillis.toLocalFormattedTime()
        )
    }

    private fun expectedDate(epochSeconds: Long): String {
        val localDateTime = Instant.fromEpochSeconds(epochSeconds).toLocalDateTime(TimeZone.currentSystemDefault())
        val day = localDateTime.day.toString().padStart(2, '0')
        val month =
            localDateTime.month.number
                .toString()
                .padStart(2, '0')
        return "$day.$month.${localDateTime.year}"
    }

    private fun expectedTime(epochSeconds: Long): String {
        val localDateTime = Instant.fromEpochSeconds(epochSeconds).toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        return "$hour:$minute"
    }
}
