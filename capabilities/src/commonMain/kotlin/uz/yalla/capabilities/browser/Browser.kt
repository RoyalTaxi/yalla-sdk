package uz.yalla.capabilities.browser

import androidx.compose.runtime.Composable

@Composable
expect fun rememberBrowser(): Browser

interface Browser {
    fun open(url: String)
}
