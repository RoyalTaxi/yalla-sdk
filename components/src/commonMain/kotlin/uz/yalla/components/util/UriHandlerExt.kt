package uz.yalla.components.util

import androidx.compose.ui.platform.UriHandler

private val URI_SCHEME_REGEX = Regex("^[a-zA-Z][a-zA-Z0-9+.-]*:.*")

/**
 * Trims [rawUri] and prepends `https://` when it carries no scheme; returns null when it is blank.
 * Shared so the trips/contact/info screens normalize links identically instead of drifting copies.
 */
public fun normalizeWebUri(rawUri: String): String? {
    val trimmed = rawUri.trim()
    if (trimmed.isBlank()) return null
    return if (trimmed.matches(URI_SCHEME_REGEX)) trimmed else "https://$trimmed"
}

/**
 * Opens [rawUri] through the Compose [UriHandler], normalizing the scheme first and falling back to
 * the raw trimmed value if the normalized open fails.
 */
public fun UriHandler.openUriSafely(rawUri: String) {
    val normalized = normalizeWebUri(rawUri) ?: return
    runCatching { openUri(normalized) }
        .onFailure {
            val trimmed = rawUri.trim()
            if (normalized != trimmed) runCatching { openUri(trimmed) }
        }
}
