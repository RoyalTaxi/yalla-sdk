package uz.yalla.components.primitives.field

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.design.theme.YallaTheme
import uz.yalla.foundation.theme.rememberIsDarkTheme

public class PinFieldController(
    length: Int,
    code: String = "",
    error: Boolean = false,
    autoFocus: Boolean = true,
    horizontalPadding: Double = 0.0,
    alphanumeric: Boolean = false,
    onValueChange: (String) -> Unit
) {
    private var codeState by mutableStateOf(code)
    private var errorState by mutableStateOf(error)

    @OptIn(ExperimentalComposeUiApi::class)
    public val viewController: UIViewController =
        ComposeUIViewController(configure = { opaque = false }) {
            YallaTheme(isDark = rememberIsDarkTheme()) {
                val focusRequester = remember { FocusRequester() }
                if (autoFocus) {
                    LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
                }
                PinField(
                    value = codeState,
                    onValueChange = { new ->
                        codeState = new
                        onValueChange(new)
                    },
                    length = length,
                    error = errorState,
                    alphanumeric = alphanumeric,
                    focusRequester = if (autoFocus) focusRequester else null,
                    contentPadding = PaddingValues(horizontal = horizontalPadding.dp)
                )
            }
        }

    public fun setCode(code: String) {
        codeState = code
    }

    public fun setError(error: Boolean) {
        errorState = error
    }
}
