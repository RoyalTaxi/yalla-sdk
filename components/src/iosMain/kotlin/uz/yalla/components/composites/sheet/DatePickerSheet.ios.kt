package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.datetime.LocalDate
import uz.yalla.components.config.requireConfig
import uz.yalla.components.platform.findKeyWindowRootController
import uz.yalla.components.platform.toLocalDate
import uz.yalla.components.platform.toNSDate

@Composable
actual fun DatePickerSheet(
    isVisible: Boolean,
    startDate: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    title: String?,
    onSelect: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean
) {
    val currentOnSelect by rememberUpdatedState(onSelect)
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    // Keyed on the date params: the handle bakes in startDate at creation, so it must be rebuilt when
    // startDate changes — e.g. once the saved birthday loads after the initial (default) composition.
    val handle = remember(startDate, minDate, maxDate, title, dismissEnabled) {
        requireConfig().sheet.createDatePicker(
            startDate = startDate.toNSDate(),
            minDate = minDate?.toNSDate(),
            maxDate = maxDate?.toNSDate(),
            title = title,
            dismissEnabled = dismissEnabled,
            onSelect = { currentOnSelect(it.toLocalDate()) },
            onDismissRequest = { currentOnDismissRequest() }
        )
    }

    DisposableEffect(isVisible) {
        if (!isVisible) {
            return@DisposableEffect onDispose {}
        }

        val parent = findKeyWindowRootController() ?: return@DisposableEffect onDispose {}
        handle.present(parent)

        onDispose { handle.dismiss() }
    }
}
