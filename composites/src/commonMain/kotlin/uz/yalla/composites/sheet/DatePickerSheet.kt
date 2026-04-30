package uz.yalla.composites.sheet

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

/**
 * State for [DatePickerSheet].
 */
data class DatePickerSheetState(
    val isVisible: Boolean,
    val startDate: LocalDate,
    val maxDate: LocalDate,
)

/**
 * Effects emitted by [DatePickerSheet].
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
 */
@Composable
// Kotlin compiler flags PascalCase for non-class functions; Composables use PascalCase by convention.
@Suppress("FunctionName")
expect fun DatePickerSheet(
    state: DatePickerSheetState,
    onEffect: (DatePickerSheetEffect) -> Unit,
)
