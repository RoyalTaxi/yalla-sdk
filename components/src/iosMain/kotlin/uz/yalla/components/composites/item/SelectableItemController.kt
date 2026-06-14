package uz.yalla.components.composites.item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.components.resource.asImageVector
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.foundation.theme.rememberIsDarkTheme

public class SelectableItemController(
    text: String,
    icon: String?,
    tintIcon: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    private var selectedState by mutableStateOf(selected)

    @OptIn(ExperimentalComposeUiApi::class)
    public val viewController: UIViewController =
        ComposeUIViewController(configure = { opaque = false }) {
            YallaTheme(isDark = rememberIsDarkTheme()) {
                SelectableItem(
                    text = text,
                    selected = selectedState,
                    leadingPainter = icon?.asImageVector()?.let { rememberVectorPainter(it) },
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        SelectableItemDefaults.colors(
                            iconColor = if (tintIcon) System.color.icon.base else Color.Unspecified,
                            selectedIconColor = if (tintIcon) System.color.icon.base else Color.Unspecified
                        ),
                    dimens =
                        SelectableItemDefaults.dimens(
                            iconSize = if (tintIcon) 24.dp else 34.dp
                        )
                )
            }
        }

    public fun setSelected(selected: Boolean) {
        selectedState = selected
    }
}
