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
 * @param skipPartiallyExpanded On Android, skips the partially-expanded state and goes directly
 *   to full expansion. Ignored on iOS (uses detent-based sizing). Default `false`.
 * @param onFullyExpanded Called once when the sheet has settled at its fully-expanded detent.
 *   Fires after the settle animation completes — not during the animation itself.
 *   On Android: fires when `SheetValue.Expanded == currentValue == targetValue` (both the
 *   current and target states are `Expanded`, meaning the animation has completed and the
 *   sheet is at rest). On iOS: fires when the `UISheetPresentationController` presentation
 *   animation completes and the sheet is at the largest configured detent.
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
    skipPartiallyExpanded: Boolean = false,
    onFullyExpanded: (() -> Unit)? = null,
    content: @Composable () -> Unit
)
