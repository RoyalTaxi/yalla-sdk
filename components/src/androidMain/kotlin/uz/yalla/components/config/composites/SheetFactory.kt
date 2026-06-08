package uz.yalla.components.config.composites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import uz.yalla.components.composites.item.ActionableItemModel
import uz.yalla.components.composites.item.SelectableItemModel
import uz.yalla.design.image.ThemedImage

interface SheetFactory {
    @Composable
    fun ContentContent(
        isVisible: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        title: String?,
        onClose: (() -> Unit)?,
        fullHeight: Boolean,
        sheetSwipeEnabled: Boolean,
        footer: (@Composable () -> Unit)?,
        content: @Composable (padding: PaddingValues) -> Unit
    )

    @Composable
    fun ConfirmationContent(
        isVisible: Boolean,
        image: ThemedImage,
        title: String,
        description: String,
        actionText: String,
        onAction: () -> Unit,
        onDismissRequest: () -> Unit,
        dismissEnabled: Boolean
    )

    @Composable
    fun SelectionContent(
        isVisible: Boolean,
        title: String,
        items: List<SelectableItemModel>,
        selectedId: String?,
        onSelect: (id: String) -> Unit,
        onDismissRequest: () -> Unit
    )

    @Composable
    fun ActionContent(
        isVisible: Boolean,
        title: String,
        items: List<ActionableItemModel>,
        onAction: (id: String) -> Unit,
        onDismissRequest: () -> Unit
    )

    @Composable
    fun DatePickerContent(
        isVisible: Boolean,
        startDate: LocalDate,
        minDate: LocalDate?,
        maxDate: LocalDate?,
        title: String?,
        onSelect: (LocalDate) -> Unit,
        onDismissRequest: () -> Unit,
        dismissEnabled: Boolean
    )

    @Composable
    fun VerificationContent(
        isVisible: Boolean,
        code: String,
        onCodeChange: (String) -> Unit,
        codeLength: Int,
        headline: String,
        description: String,
        confirmText: String,
        onConfirm: () -> Unit,
        resendText: String,
        onResend: () -> Unit,
        onDismissRequest: () -> Unit,
        title: String?,
        isError: Boolean,
        isLoading: Boolean,
        resendEnabled: Boolean,
        onCodeComplete: (String) -> Unit,
        dismissEnabled: Boolean
    )
}
