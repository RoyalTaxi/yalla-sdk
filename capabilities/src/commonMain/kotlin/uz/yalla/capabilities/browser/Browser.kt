package uz.yalla.capabilities.browser

import androidx.compose.runtime.Composable

@Composable
public expect fun rememberBrowser(): Browser

public interface Browser {
    public fun open(url: String)
}

internal fun isWebUrl(scheme: String?): Boolean {
    val s = scheme?.lowercase()
    return s == "http" || s == "https"
}
