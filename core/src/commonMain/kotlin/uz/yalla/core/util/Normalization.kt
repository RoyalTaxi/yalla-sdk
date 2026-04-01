package uz.yalla.core.util

/**
 * Trims whitespace, lowercases, and defaults to empty string.
 *
 * Used internally for case-insensitive enum/ID matching across all
 * `from()` factory methods in the core module.
 *
 * @return Normalized identifier string, or empty string if `null`
 * @since 0.0.1
 */
internal fun String?.normalizedId(): String = this?.trim()?.lowercase().orEmpty()

/**
 * Normalizes a locale code for comparison.
 *
 * Trims whitespace, replaces underscores with hyphens (e.g., "uz_Cyrl" becomes "uz-cyrl"),
 * and lowercases the result. Returns empty string if `null`.
 *
 * @return Normalized BCP 47-style locale code, or empty string if `null`
 * @see LocaleKind.from
 * @since 0.0.1
 */
internal fun String?.normalizedLocaleCode(): String =
    this
        ?.trim()
        ?.replace('_', '-')
        ?.lowercase()
        .orEmpty()
