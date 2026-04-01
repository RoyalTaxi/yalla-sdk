@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class, kotlin.time.ExperimentalTime::class)

package uz.yalla.platform.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDate
import platform.Foundation.NSSelectorFromString
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.timeZoneWithName
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UIDatePicker
import platform.UIKit.UIDatePickerMode
import platform.UIKit.UIDatePickerStyle

/**
 * iOS actual for [NativeWheelDatePicker].
 *
 * Renders a native [UIDatePicker] with `.wheels` style and `.date` mode via [UIKitView].
 * The picker's time zone is forced to UTC to avoid date-shifting issues across time zones.
 * A [DateChangeHandler] target-action pair forwards value changes to [onDateChanged].
 */
@Composable
actual fun NativeWheelDatePicker(
    startDate: LocalDate,
    modifier: Modifier,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onDateChanged: (LocalDate) -> Unit
) {
    val dateChangeHandler = remember { DateChangeHandler(onDateChanged) }

    UIKitView(
        factory = {
            UIDatePicker().apply {
                datePickerMode = UIDatePickerMode.UIDatePickerModeDate
                preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels
                timeZone = NSTimeZone.timeZoneWithName("UTC")
                backgroundColor = UIColor.clearColor
                setOpaque(false)

                setDate(startDate.toNSDate(), animated = false)
                minDate?.let { minimumDate = it.toNSDate() }
                maxDate?.let { maximumDate = it.toNSDate() }

                addTarget(
                    target = dateChangeHandler,
                    action = NSSelectorFromString("dateChanged:"),
                    forControlEvents = UIControlEventValueChanged
                )
            }
        },
        modifier = modifier,
        update = { picker ->
            minDate?.let { picker.minimumDate = it.toNSDate() }
            maxDate?.let { picker.maximumDate = it.toNSDate() }
            dateChangeHandler.onChange = onDateChanged
        }
    )
}

/**
 * ObjC-compatible target for [UIDatePicker] value-changed events.
 *
 * @param onChange Callback invoked with the selected [LocalDate] when the picker value changes.
 */
private class DateChangeHandler(
    var onChange: (LocalDate) -> Unit
) : platform.darwin.NSObject() {
    @ObjCAction
    fun dateChanged(sender: UIDatePicker) {
        onChange(sender.date.toLocalDate())
    }
}

/**
 * Converts a kotlinx-datetime [LocalDate] to an [NSDate] at the start of day in UTC.
 *
 * @return The corresponding [NSDate].
 */
private fun LocalDate.toNSDate(): NSDate {
    val epochMillis = this.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    return NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
}

/**
 * Converts an [NSDate] to a kotlinx-datetime [LocalDate] in UTC.
 *
 * @return The corresponding [LocalDate].
 */
private fun NSDate.toLocalDate(): LocalDate {
    val epochSeconds = timeIntervalSince1970.toLong()
    val instant = Instant.fromEpochSeconds(epochSeconds)
    val dateTime = instant.toLocalDateTime(TimeZone.UTC)
    return dateTime.date
}
