package uz.yalla.composites.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Full-height form sheet with header, scrollable content, and an optional bottom action.
 *
 * Composes [Sheet] + [SheetHeader] with IME-aware padding so the keyboard never
 * obscures the action button. Use for any sheet that collects user input: add-card,
 * edit-profile, promo-code entry, etc.
 *
 * ## Usage
 *
 * ```kotlin
 * FormSheet(
 *     isVisible = state.showAddCard,
 *     onDismissRequest = { viewModel.dismiss() },
 *     title = "Add Card",
 *     action = {
 *         PrimaryButton(
 *             onClick = { viewModel.save() },
 *             modifier = Modifier.fillMaxWidth(),
 *         ) { Text("Save") }
 *     },
 * ) {
 *     PrimaryField(
 *         value = state.cardNumber,
 *         onValueChange = { viewModel.onCardNumberChange(it) },
 *         placeholder = "Card number",
 *     )
 *     Spacer(Modifier.height(16.dp))
 *     PrimaryField(
 *         value = state.expiry,
 *         onValueChange = { viewModel.onExpiryChange(it) },
 *         placeholder = "MM/YY",
 *     )
 * }
 * ```
 *
 * @param isVisible Whether the sheet is visible.
 * @param onDismissRequest Called when the sheet is dismissed (swipe or close button).
 * @param modifier Applied to the underlying [Sheet].
 * @param title Optional centered title displayed in the [SheetHeader].
 * @param sheetState Material3 sheet state for controlling expand/collapse behavior.
 * @param colors Color configuration delegated to [Sheet], defaults to [SheetDefaults.colors].
 * @param snackbarHost Optional snackbar host rendered as a popup overlay inside the sheet.
 * @param onFullyExpanded Called when the sheet reaches its fully-expanded state.
 * @param action Optional bottom-anchored composable (typically a
 *   [PrimaryButton][uz.yalla.primitives.button.PrimaryButton]).
 * @param content Scrollable body content rendered between the header and the action.
 *
 * @see Sheet for the underlying bottom sheet primitive
 * @see SheetHeader for the header component
 * @since 0.0.5-alpha12
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    colors: SheetColors = SheetDefaults.colors(),
    snackbarHost: @Composable (() -> Unit)? = null,
    onFullyExpanded: (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Sheet(
        isVisible = isVisible,
        onDismissRequest = onDismissRequest,
        modifier = modifier.statusBarsPadding().fillMaxHeight(),
        sheetState = sheetState,
        colors = colors,
        dragHandle = null,
        snackbarHost = snackbarHost,
        onFullyExpanded = onFullyExpanded,
    ) {
        Column(Modifier.fillMaxHeight().imePadding()) {
            SheetHeader(onClose = onDismissRequest, title = title)
            Spacer(Modifier.height(24.dp))
            Column(Modifier.weight(1f).padding(horizontal = 20.dp)) { content() }
            action?.let {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp),
                ) { it() }
            }
        }
    }
}
