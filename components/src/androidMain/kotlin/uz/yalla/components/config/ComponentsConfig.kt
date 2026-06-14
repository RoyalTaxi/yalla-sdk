package uz.yalla.components.config

import uz.yalla.components.config.composites.SheetFactory
import uz.yalla.components.config.feedback.SnackbarFactory
import uz.yalla.components.config.primitives.IconButtonFactory
import uz.yalla.components.config.primitives.LoadingIndicatorFactory
import uz.yalla.components.config.primitives.ToggleFactory

public class ComponentsConfig private constructor(
    public val loadingIndicator: LoadingIndicatorFactory,
    public val iconButton: IconButtonFactory,
    public val toggle: ToggleFactory,
    public val snackbar: SnackbarFactory,
    public val sheet: SheetFactory
) {
    public class Builder {
        public var loadingIndicator: LoadingIndicatorFactory? = null
        public var iconButton: IconButtonFactory? = null
        public var toggle: ToggleFactory? = null
        public var snackbar: SnackbarFactory? = null
        public var sheet: SheetFactory? = null

        public fun build(): ComponentsConfig =
            ComponentsConfig(
                loadingIndicator = requireNotNull(loadingIndicator) { "loadingIndicator required" },
                iconButton = requireNotNull(iconButton) { "iconButton required" },
                toggle = requireNotNull(toggle) { "toggle required" },
                snackbar = requireNotNull(snackbar) { "snackbar required" },
                sheet = requireNotNull(sheet) { "sheet required" }
            )
    }
}
