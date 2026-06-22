package uz.yalla.components.util

import androidx.compose.ui.platform.UriHandler

private val URI_SCHEME_REGEX = Regex("^[a-zA-Z][a-zA-Z0-9+.-]*:.*")
private val SCHEME_PREFIX_REGEX = Regex("^([a-zA-Z][a-zA-Z0-9+.-]*):")

private val ALLOWED_URI_SCHEMES = setOf("http", "https")

public fun normalizeWebUri(rawUri: String): String? {
    val trimmed = rawUri.trim()
    if (trimmed.isBlank()) return null
    if (!trimmed.matches(URI_SCHEME_REGEX)) return "https://$trimmed"
    val scheme =
        SCHEME_PREFIX_REGEX
            .find(trimmed)
            ?.groupValues
            ?.get(1)
            ?.lowercase()
    return if (scheme in ALLOWED_URI_SCHEMES) trimmed else null
}

public fun UriHandler.openUriSafely(rawUri: String) {
    val normalized = normalizeWebUri(rawUri) ?: return
    runCatching { openUri(normalized) }
}
