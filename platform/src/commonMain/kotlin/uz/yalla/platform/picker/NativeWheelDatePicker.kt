package uz.yalla.platform.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate

/**
 * Platform-native wheel-style date picker.
 *
 * On iOS, renders a `UIDatePicker` with `.wheels` style for the familiar
 * spinning-drum appearance. On Android, renders a custom wheel-scroll picker.
 *
 * ## Usage
 * ```kotlin
 * NativeWheelDatePicker(
 *     startDate = selectedDate,
 *     minDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
 *     onDateChanged = { viewModel.onDateSelected(it) },
 * )
 * ```
 *
 * @param startDate Initial date shown in the picker.
 * @param modifier Modifier applied to the picker container.
 * @param minDate Earliest selectable date. `null` for no lower bound.
 * @param maxDate Latest selectable date. `null` for no upper bound.
 * @param onDateChanged Called when the user scrolls to a new date.
 * @since 0.0.1
 */
@Composable
expect fun NativeWheelDatePicker(
    startDate: LocalDate,
    modifier: Modifier = Modifier,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onDateChanged: (LocalDate) -> Unit
)
