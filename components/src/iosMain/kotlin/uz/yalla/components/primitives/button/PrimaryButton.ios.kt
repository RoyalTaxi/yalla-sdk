package uz.yalla.components.primitives.button

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.design.theme.YallaTheme

@OptIn(ExperimentalComposeUiApi::class)
fun PrimaryButtonViewController(
    title: String,
    onClick: () -> Unit
): UIViewController = ComposeUIViewController(
    configure = { opaque = false }
) {
    YallaTheme(isDark = isSystemInDarkTheme()) {
        PrimaryButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) { _, _, styles ->
            Text(text = title, style = styles.textStyle)
        }
    }
}
