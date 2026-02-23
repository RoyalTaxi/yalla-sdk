package uz.yalla.platform.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.darkokoa.datetimewheelpicker.WheelDatePicker
import dev.darkokoa.datetimewheelpicker.core.WheelPickerDefaults
import kotlinx.datetime.LocalDate
import uz.yalla.design.theme.System

@Composable
actual fun NativeWheelDatePicker(
    startDate: LocalDate,
    modifier: Modifier,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onDateChanged: (LocalDate) -> Unit
) {
    WheelDatePicker(
        startDate = startDate,
        minDate = minDate ?: LocalDate(1900, 1, 1),
        maxDate = maxDate ?: LocalDate(2100, 12, 31),
        size = DpSize(360.dp, height = 280.dp),
        modifier = modifier,
        rowCount = 5,
        textStyle = System.font.title.base,
        textColor = System.color.textBase,
        onSnappedDate = onDateChanged,
        selectorProperties = WheelPickerDefaults.selectorProperties(false)
    )
}
