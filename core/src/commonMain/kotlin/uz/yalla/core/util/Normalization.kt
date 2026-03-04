package uz.yalla.core.util

/** Trims, lowercases, and defaults to empty string. Used for enum/ID parsing. */
internal fun String?.normalizedId(): String = this?.trim()?.lowercase().orEmpty()

/** Normalizes locale codes: trims, replaces underscores with hyphens, lowercases. */
internal fun String?.normalizedLocaleCode(): String =
    this
        ?.trim()
        ?.replace('_', '-')
        ?.lowercase()
        .orEmpty()
