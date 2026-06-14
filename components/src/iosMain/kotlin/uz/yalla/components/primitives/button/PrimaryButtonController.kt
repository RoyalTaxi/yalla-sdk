package uz.yalla.components.primitives.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.design.theme.YallaTheme
import uz.yalla.foundation.theme.rememberIsDarkTheme

public class PrimaryButtonController(
    text: String,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    private var textState by mutableStateOf(text)
    private var enabledState by mutableStateOf(enabled)
    private var loadingState by mutableStateOf(loading)

    @OptIn(ExperimentalComposeUiApi::class)
    public val viewController: UIViewController =
        ComposeUIViewController(configure = { opaque = false }) {
            YallaTheme(isDark = rememberIsDarkTheme()) {
                PrimaryButton(
                    enabled = enabledState,
                    loading = loadingState,
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth()
                ) { _, _, styles ->
                    Text(text = textState, style = styles.textStyle)
                }
            }
        }

    public fun setText(text: String) {
        textState = text
    }

    public fun setEnabled(enabled: Boolean) {
        enabledState = enabled
    }

    public fun setLoading(loading: Boolean) {
        loadingState = loading
    }
}
