@file:OptIn(kotlin.time.ExperimentalTime::class)

package uz.yalla.components.platform

import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

internal fun LocalDate.toNSDate(): NSDate {
    val epochMillis = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    return NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
}

internal fun NSDate.toLocalDate(): LocalDate {
    val epochSeconds = timeIntervalSince1970.toLong()
    return Instant.fromEpochSeconds(epochSeconds).toLocalDateTime(TimeZone.UTC).date
}
