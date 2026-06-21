package uz.yalla.capabilities.browser

import androidx.compose.runtime.Composable

/** Remembers a platform [Browser] for opening web URLs in an in-app browser tab. */
@Composable
public expect fun rememberBrowser(): Browser

/** Opens web URLs in the platform in-app browser (Custom Tabs / SFSafariViewController). */
public interface Browser {
    /**
     * Opens [url] in an in-app browser tab.
     *
     * Only `http`/`https` URLs are honored; any other scheme (e.g. `file:`,
     * `intent:`, `javascript:`) or a scheme-less/malformed string is ignored.
     * This is the same contract on every platform.
     */
    public fun open(url: String)
}

/** `http`/`https` are the only schemes [Browser.open] will load. */
internal fun isWebUrl(scheme: String?): Boolean {
    val s = scheme?.lowercase()
    return s == "http" || s == "https"
}
