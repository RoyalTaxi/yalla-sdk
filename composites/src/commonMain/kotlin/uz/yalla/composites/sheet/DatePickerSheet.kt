package uz.yalla.composites.sheet

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

/**
 * State for [DatePickerSheet].
 *
 * [title] is the localized header label (e.g. "Date of birth", "Pickup time").
 * `null` hides the entire header row and renders only the close/done icons +
 * the wheel picker. Caller passes a `stringResource(...)`; the composite
 * ships no default copy.
 */
data class DatePickerSheetState(
    val isVisible: Boolean,
    val startDate: LocalDate,
    val maxDate: LocalDate,
    val title: String? = null
)

/**
 * Effects emitted by [DatePickerSheet].
 */
sealed interface DatePickerSheetEffect {
    /** User dismissed the sheet. */
    data object Dismiss : DatePickerSheetEffect

    /** User selected a date. */
    data class Select(
        val date: LocalDate
    ) : DatePickerSheetEffect
}

/**
 * Platform-specific date picker presented as a bottom sheet.
 *
 * On Android this uses a wheel-style picker; on iOS it uses `UIDatePicker`.
 */
@Composable
expect fun DatePickerSheet(
    state: DatePickerSheetState,
    onEffect: (DatePickerSheetEffect) -> Unit
)
