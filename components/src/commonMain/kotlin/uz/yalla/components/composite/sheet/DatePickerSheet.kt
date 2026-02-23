package uz.yalla.components.composite.sheet

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

/**
 * State for [DatePickerSheet].
 *
 * @property isVisible Whether sheet is visible.
 * @property startDate Initial selected date.
 * @property maxDate Maximum selectable date.
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
    data class Select(
        val date: LocalDate
    ) : DatePickerSheetEffect
}

@Composable
@Suppress("FunctionName")
expect fun DatePickerSheet(
    state: DatePickerSheetState,
    onEffect: (DatePickerSheetEffect) -> Unit,
)
