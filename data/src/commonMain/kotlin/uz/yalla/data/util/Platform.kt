package uz.yalla.data.util

/**
 * Platform identifier used in HTTP headers.
 *
 * Returns `"android"` or `"ios"` depending on the compilation target.
 *
 * @since 0.0.5
 */
expect val platformName: String
