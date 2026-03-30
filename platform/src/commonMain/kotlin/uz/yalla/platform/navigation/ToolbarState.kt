package uz.yalla.platform.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

/**
 * Mutable state holder for dynamic toolbar actions.
 *
 * Screens update [actions] at runtime; the platform navigation bar observes
 * changes and updates its toolbar items accordingly.
 *
 * On iOS, actions map to `UIBarButtonItem` on `navigationItem.rightBarButtonItems`.
 * On Android, actions render inside Material3 TopAppBar's `actions` slot.
 *
 * ## Usage
 * ```kotlin
 * @Composable
 * fun MenuScreen(toolbarState: ToolbarState) {
 *     LaunchedEffect(Unit) {
 *         toolbarState.actions = listOf(
 *             ToolbarAction.Icon(ToolbarIcon.Edit) { /* edit mode */ }
 *         )
 *     }
 * }
 * ```
 *
 * @since 0.0.5
 */
@Stable
class ToolbarState {
    /** Current toolbar actions. Changes trigger navigation bar updates. */
    var visible: Boolean by mutableStateOf(true)
    var actions: List<ToolbarAction> by mutableStateOf(emptyList())

    /**
     * Insets provided by the platform navigation container.
     *
     * On iOS, derived from `UIViewController.view.safeAreaInsets` (status bar + nav bar + home indicator).
     * On Android, derived from `Scaffold` padding values (top bar + system bars).
     *
     * Screens use this instead of querying `WindowInsets` directly, keeping them platform-agnostic.
     */
    var contentPadding: PaddingValues by mutableStateOf(PaddingValues(0.dp))
}
