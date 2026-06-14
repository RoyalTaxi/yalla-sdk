package uz.yalla.components.composites.item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.components.resource.asImageVector
import uz.yalla.design.theme.YallaTheme
import uz.yalla.foundation.theme.rememberIsDarkTheme

public class ActionableItemController(
    text: String,
    icon: String,
    trailingIcon: String? = null,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    private val model =
        ActionableItemModel(
            id = "",
            text = text,
            icon = icon,
            trailingIcon = trailingIcon,
            isDestructive = isDestructive
        )

    @OptIn(ExperimentalComposeUiApi::class)
    public val viewController: UIViewController =
        ComposeUIViewController(configure = { opaque = false }) {
            YallaTheme(isDark = rememberIsDarkTheme()) {
                ActionableItem(
                    text = model.text,
                    painter = model.icon.asImageVector()?.let { rememberVectorPainter(it) },
                    trailingPainter = model.trailingIcon?.asImageVector()?.let { rememberVectorPainter(it) },
                    onClick = onClick,
                    colors = ActionableItemDefaults.colorsFor(model),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
}
