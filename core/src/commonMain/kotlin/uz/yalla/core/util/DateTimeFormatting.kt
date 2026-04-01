package uz.yalla.core.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Formats an epoch timestamp to a localized date string (`dd.MM.yyyy`).
 *
 * Automatically detects whether the value is in seconds or milliseconds
 * (values above 10 billion are treated as milliseconds and divided by 1000).
 *
 * Uses the device's current system [TimeZone] for conversion.
 *
 * ## Usage
 * ```kotlin
 * 1711929600L.toLocalFormattedDate()    // "01.04.2024" (seconds)
 * 1711929600000L.toLocalFormattedDate() // "01.04.2024" (milliseconds)
 * null.toLocalFormattedDate()           // ""
 * ```
 *
 * @return Formatted date string, or empty string if `null` or non-positive
 * @see toLocalFormattedTime
 * @since 0.0.1
 */
fun Long?.toLocalFormattedDate(): String {
    val localDateTime = this.toLocalDateTimeOrNull() ?: return ""
    val day = localDateTime.day.toString().padStart(2, '0')
    val month =
        localDateTime.month.number
            .toString()
            .padStart(2, '0')
    return "$day.$month.${localDateTime.year}"
}

/**
 * Formats an epoch timestamp to a localized time string (`HH:mm`).
 *
 * Automatically detects whether the value is in seconds or milliseconds
 * (values above 10 billion are treated as milliseconds and divided by 1000).
 *
 * Uses the device's current system [TimeZone] for conversion.
 *
 * ## Usage
 * ```kotlin
 * 1711929600L.toLocalFormattedTime()    // "14:00" (seconds, UTC+5)
 * null.toLocalFormattedTime()           // ""
 * ```
 *
 * @return Formatted time string, or empty string if `null` or non-positive
 * @see toLocalFormattedDate
 * @since 0.0.1
 */
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
