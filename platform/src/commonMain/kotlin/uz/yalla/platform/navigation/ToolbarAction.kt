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
    /** Callback invoked when the action is tapped. */
    val onClick: () -> Unit

    /**
     * Text-labeled toolbar action (e.g., "Save", "Edit").
     *
     * @param label Display text for the action.
     * @param onClick Callback invoked when tapped.
     * @since 0.0.5
     */
    data class Text(
        val label: String,
        override val onClick: () -> Unit,
    ) : ToolbarAction

    /**
     * Icon-based toolbar action using a named [ToolbarIcon].
     *
     * @param icon The icon to display.
     * @param onClick Callback invoked when tapped.
     * @since 0.0.5
     */
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
    /** Pencil / edit icon. SF Symbol: `pencil`. */
    Edit,
    /** Open envelope / mark-all-read icon. SF Symbol: `envelope.open`. */
    ReadAll,
    /** Ellipsis / more options icon. SF Symbol: `ellipsis`. */
    More,
    /** Plus / add icon. SF Symbol: `plus`. */
    Add,
}
