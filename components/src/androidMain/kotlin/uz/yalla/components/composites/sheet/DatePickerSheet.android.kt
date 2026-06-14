package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate
import uz.yalla.components.config.requireConfig

@Composable
public actual fun DatePickerSheet(
    isVisible: Boolean,
    startDate: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    title: String?,
    onSelect: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean
) {
    requireConfig().sheet.DatePickerContent(
        isVisible = isVisible,
        startDate = startDate,
        minDate = minDate,
        maxDate = maxDate,
        title = title,
        onSelect = onSelect,
        onDismissRequest = onDismissRequest,
        dismissEnabled = dismissEnabled
    )
}
