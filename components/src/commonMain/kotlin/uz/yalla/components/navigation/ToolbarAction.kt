package uz.yalla.components.navigation

sealed interface ToolbarAction {
    val onClick: () -> Unit

    data class Text(
        val label: String,
        override val onClick: () -> Unit
    ) : ToolbarAction

    data class Icon(
        val icon: ToolbarIcon,
        override val onClick: () -> Unit
    ) : ToolbarAction
}

enum class ToolbarIcon {
    Edit,
    ReadAll,
    More,
    Add
}
