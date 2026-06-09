package uz.yalla.components.primitives.button

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.design.theme.YallaTheme
import uz.yalla.foundation.theme.rememberIsDarkTheme

@OptIn(ExperimentalComposeUiApi::class)
fun PrimaryButtonViewController(
    title: String,
    onClick: () -> Unit
): UIViewController = ComposeUIViewController(
    configure = { opaque = false }
) {
    YallaTheme(isDark = rememberIsDarkTheme()) {
        PrimaryButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) { _, _, styles ->
            Text(text = title, style = styles.textStyle)
        }
    }
}
