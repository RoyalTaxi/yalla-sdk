package uz.yalla.components.config

import uz.yalla.components.config.composites.SheetFactory
import uz.yalla.components.config.feedback.SnackbarFactory
import uz.yalla.components.config.primitives.IconButtonFactory
import uz.yalla.components.config.primitives.LoadingIndicatorFactory
import uz.yalla.components.config.primitives.ToggleFactory

class ComponentsConfig private constructor(
    val loadingIndicator: LoadingIndicatorFactory,
    val iconButton: IconButtonFactory,
    val toggle: ToggleFactory,
    val snackbar: SnackbarFactory,
    val sheet: SheetFactory
) {
    class Builder {
        var loadingIndicator: LoadingIndicatorFactory? = null
        var iconButton: IconButtonFactory? = null
        var toggle: ToggleFactory? = null
        var snackbar: SnackbarFactory? = null
        var sheet: SheetFactory? = null

        fun build() = ComponentsConfig(
            loadingIndicator = requireNotNull(loadingIndicator) { "loadingIndicator required" },
            iconButton = requireNotNull(iconButton) { "iconButton required" },
            toggle = requireNotNull(toggle) { "toggle required" },
            snackbar = requireNotNull(snackbar) { "snackbar required" },
            sheet = requireNotNull(sheet) { "sheet required" }
        )
    }
}
