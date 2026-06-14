package uz.yalla.components.primitives.field

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.design.theme.YallaTheme
import uz.yalla.foundation.theme.rememberIsDarkTheme

public class PrimaryFieldController(
    value: String = "",
    placeholder: String = "",
    enabled: Boolean = true,
    centered: Boolean = false,
    autoFocus: Boolean = false,
    onValueChange: (String) -> Unit
) {
    private var valueState by mutableStateOf(value)
    private var placeholderState by mutableStateOf(placeholder)
    private var enabledState by mutableStateOf(enabled)

    @OptIn(ExperimentalComposeUiApi::class)
    public val viewController: UIViewController = ComposeUIViewController(configure = { opaque = false }) {
        YallaTheme(isDark = rememberIsDarkTheme()) {
            val focusRequester = remember { FocusRequester() }
            if (autoFocus) {
                LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
            }
            PrimaryField(
                value = valueState,
                onValueChange = { new ->
                    valueState = new
                    onValueChange(new)
                },
                enabled = enabledState,
                placeholder = placeholderState,
                textAlign = if (centered) TextAlign.Center else TextAlign.Start,
                modifier = if (autoFocus) {
                    Modifier.fillMaxWidth().focusRequester(focusRequester)
                } else {
                    Modifier.fillMaxWidth()
                }
            )
        }
    }

    public fun setValue(value: String) {
        valueState = value
    }

    public fun setPlaceholder(placeholder: String) {
        placeholderState = placeholder
    }

    public fun setEnabled(enabled: Boolean) {
        enabledState = enabled
    }
}
