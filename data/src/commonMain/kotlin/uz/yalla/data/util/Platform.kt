package uz.yalla.data.util

/**
 * Platform identifier used in HTTP headers.
 *
 * Returns `"android"` or `"ios"` depending on the compilation target.
 * Sent as the `User-Agent-OS` header by [createHttpClient][uz.yalla.data.network.createHttpClient].
 *
 * @see uz.yalla.data.network.createHttpClient
 * @since 0.0.5
 */
expect val platformName: String
