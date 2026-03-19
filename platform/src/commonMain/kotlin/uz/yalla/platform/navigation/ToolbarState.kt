package uz.yalla.platform.navigation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import uz.yalla.primitives.navigation.ToolbarAction

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
    var actions: List<ToolbarAction> by mutableStateOf(emptyList())
}
