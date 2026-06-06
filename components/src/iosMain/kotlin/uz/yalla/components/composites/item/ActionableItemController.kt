package uz.yalla.components.composites.item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.components.resource.asImageVector
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

class ActionableItemController(
    text: String,
    icon: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    @OptIn(ExperimentalComposeUiApi::class)
    val viewController: UIViewController = ComposeUIViewController(configure = { opaque = false }) {
        YallaTheme {
            ActionableItem(
                text = text,
                painter = icon.asImageVector()?.let { rememberVectorPainter(it) },
                onClick = onClick,
                colors = if (isDestructive) {
                    ActionableItemDefaults.colors(
                        iconColor = System.color.icon.red,
                        textColor = System.color.text.red
                    )
                } else {
                    ActionableItemDefaults.colors()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
