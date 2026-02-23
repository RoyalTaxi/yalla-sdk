package uz.yalla.core.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun Long?.toLocalFormattedDate(): String {
    val localDateTime = this.toLocalDateTimeOrNull() ?: return ""
    val day = localDateTime.day.toString().padStart(2, '0')
    val month =
        localDateTime.month.number
            .toString()
            .padStart(2, '0')
    return "$day.$month.${localDateTime.year}"
}

fun Long?.toLocalFormattedTime(): String {
    val localDateTime = this.toLocalDateTimeOrNull() ?: return ""
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}

private fun Long?.toLocalDateTimeOrNull() =
    this
        ?.normalizedEpochSeconds()
        ?.takeIf { it > 0L }
        ?.let { epochSeconds ->
            Instant
                .fromEpochSeconds(epochSeconds)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        }

private fun Long.normalizedEpochSeconds(): Long =
    if (this > 10_000_000_000L) {
        this / 1000
    } else {
        this
    }
