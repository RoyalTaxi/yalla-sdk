package uz.yalla.composites.sheet

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

/**
 * State for [DatePickerSheet].
 *
 * @property isVisible Whether sheet is visible.
 * @property startDate Initial selected date.
 * @property maxDate Maximum selectable date.
 * @since 0.0.1
 */
data class DatePickerSheetState(
    val isVisible: Boolean,
    val startDate: LocalDate,
    val maxDate: LocalDate,
)

/**
 * Effects emitted by [DatePickerSheet].
 * @since 0.0.1
 */
sealed interface DatePickerSheetEffect {
    /** User dismissed the sheet. */
    data object Dismiss : DatePickerSheetEffect

    /** User selected a date. */
    data class Select(val date: LocalDate) : DatePickerSheetEffect
}

/**
 * Platform-specific date picker presented as a bottom sheet.
 *
 * On Android this uses a wheel-style picker; on iOS it uses `UIDatePicker`.
 *
 * @param state Sheet state with visibility and date bounds.
 * @param onEffect Callback for dismiss and date selection effects.
 * @since 0.0.1
 */
@Composable
@Suppress("FunctionName")
expect fun DatePickerSheet(
    state: DatePickerSheetState,
    onEffect: (DatePickerSheetEffect) -> Unit,
)
