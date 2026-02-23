package uz.yalla.platform.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate

@Composable
expect fun NativeWheelDatePicker(
    startDate: LocalDate,
    modifier: Modifier = Modifier,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onDateChanged: (LocalDate) -> Unit
)
