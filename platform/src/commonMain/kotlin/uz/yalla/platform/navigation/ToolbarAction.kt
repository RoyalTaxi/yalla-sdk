package uz.yalla.platform.navigation

/**
 * Action displayed in the navigation bar's trailing slot.
 *
 * On iOS, maps to `UIBarButtonItem` on `navigationItem.rightBarButtonItems`.
 * On Android, renders inside Material3 TopAppBar's `actions` slot.
 *
 * @since 0.0.5
 */
sealed interface ToolbarAction {
    val onClick: () -> Unit

    data class Text(
        val label: String,
        override val onClick: () -> Unit,
    ) : ToolbarAction

    data class Icon(
        val icon: ToolbarIcon,
        override val onClick: () -> Unit,
    ) : ToolbarAction
}

/**
 * Named icons available for [ToolbarAction.Icon].
 *
 * Platform implementations map these to native icons.
 *
 * @since 0.0.5
 */
enum class ToolbarIcon {
    Edit,
    ReadAll,
    More,
    Add,
}
