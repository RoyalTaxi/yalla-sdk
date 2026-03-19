package uz.yalla.platform.sheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

/**
 * Platform-native bottom sheet.
 *
 * On iOS, presents via `UISheetPresentationController` with detent-based sizing.
 * On Android, renders a Material3 `ModalBottomSheet`.
 *
 * ## Usage
 * ```kotlin
 * NativeSheet(
 *     isVisible = showSheet,
 *     shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
 *     containerColor = System.color.background.primary,
 *     onDismissRequest = { showSheet = false },
 * ) {
 *     SheetContent()
 * }
 * ```
 *
 * @param isVisible Controls sheet visibility. Animate-shows when `true`, hides when `false`.
 * @param shape Corner shape applied to the sheet container.
 * @param containerColor Background color of the sheet surface.
 * @param onDismissRequest Called when the user dismisses the sheet (swipe-down or scrim tap).
 * @param dismissEnabled Whether the user can interactively dismiss the sheet. Default `true`.
 * @param onDismissAttempt Called when the user tries to dismiss but [dismissEnabled] is `false`.
 * @param isDark Forces dark/light appearance on the sheet. `null` follows the system theme.
 * @param onFullyExpanded Called when the sheet reaches its fully-expanded detent.
 * @param content Composable content rendered inside the sheet.
 * @since 0.0.1
 */
@Composable
expect fun NativeSheet(
    isVisible: Boolean,
    shape: Shape,
    containerColor: Color,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean = true,
    onDismissAttempt: () -> Unit = {},
    isDark: Boolean? = null,
    onFullyExpanded: (() -> Unit)? = null,
    content: @Composable () -> Unit
)
