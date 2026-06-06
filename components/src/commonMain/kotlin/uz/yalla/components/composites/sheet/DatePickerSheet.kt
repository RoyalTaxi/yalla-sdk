package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

@Composable
expect fun DatePickerSheet(
    isVisible: Boolean,
    startDate: LocalDate,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    title: String? = null,
    onSelect: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean = true
)
