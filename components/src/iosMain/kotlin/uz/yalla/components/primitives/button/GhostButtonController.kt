package uz.yalla.components.primitives.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.design.theme.YallaTheme
import uz.yalla.foundation.theme.rememberIsDarkTheme

public class GhostButtonController(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    private var textState by mutableStateOf(text)
    private var enabledState by mutableStateOf(enabled)

    @OptIn(ExperimentalComposeUiApi::class)
    public val viewController: UIViewController =
        ComposeUIViewController(configure = { opaque = false }) {
            YallaTheme(isDark = rememberIsDarkTheme()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    GhostButton(
                        text = textState,
                        onClick = onClick,
                        enabled = enabledState
                    )
                }
            }
        }

    public fun setText(text: String) {
        textState = text
    }

    public fun setEnabled(enabled: Boolean) {
        enabledState = enabled
    }
}
