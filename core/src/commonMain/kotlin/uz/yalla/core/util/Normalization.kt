package uz.yalla.core.util

/**
 * Trims whitespace, lowercases, and defaults to empty string.
 *
 * Used internally for case-insensitive enum/ID matching across all
 * `from()` factory methods in the core module.
 *
 * @return Normalized identifier string, or empty string if `null`
 */
internal fun String?.normalizedId(): String = this?.trim()?.lowercase().orEmpty()
