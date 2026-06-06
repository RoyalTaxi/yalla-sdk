package uz.yalla.components.primitives.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.design.theme.YallaTheme

class PrimaryButtonController(
    text: String,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    private var textState by mutableStateOf(text)
    private var enabledState by mutableStateOf(enabled)
    private var loadingState by mutableStateOf(loading)

    @OptIn(ExperimentalComposeUiApi::class)
    val viewController: UIViewController = ComposeUIViewController(configure = { opaque = false }) {
        YallaTheme {
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

    fun setText(text: String) {
        textState = text
    }

    fun setEnabled(enabled: Boolean) {
        enabledState = enabled
    }

    fun setLoading(loading: Boolean) {
        loadingState = loading
    }
}
