package uz.yalla.components.util

import androidx.compose.ui.platform.UriHandler

private val URI_SCHEME_REGEX = Regex("^[a-zA-Z][a-zA-Z0-9+.-]*:.*")
private val SCHEME_PREFIX_REGEX = Regex("^([a-zA-Z][a-zA-Z0-9+.-]*):")

/** The only schemes a backend/user-supplied link is allowed to open with. */
private val ALLOWED_URI_SCHEMES = setOf("http", "https")

/**
 * Trims [rawUri] and prepends `https://` when it carries no scheme; returns `null` when the input is
 * blank **or** carries a scheme outside [ALLOWED_URI_SCHEMES].
 *
 * These helpers are fed backend/user-controlled strings (trips/contact/info screens), so a scheme
 * allowlist is enforced here: a crafted `javascript:`/`file:`/`content:`/`intent:`/app-scheme link is
 * rejected rather than launched verbatim. Shared so every screen normalizes links identically instead
 * of drifting copies.
 */
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

/**
 * Opens [rawUri] through the Compose [UriHandler] after normalizing and allowlisting its scheme via
 * [normalizeWebUri]. A blank or disallowed-scheme link is dropped; the raw, un-normalized value is
 * never re-opened on failure (that would bypass the allowlist).
 */
public fun UriHandler.openUriSafely(rawUri: String) {
    val normalized = normalizeWebUri(rawUri) ?: return
    runCatching { openUri(normalized) }
}
