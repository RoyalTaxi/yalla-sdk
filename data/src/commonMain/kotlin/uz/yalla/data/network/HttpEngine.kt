package uz.yalla.data.network

import io.ktor.client.engine.HttpClientEngine

/**
 * Creates the platform-specific HTTP client engine.
 *
 * Android uses the Ktor Android engine (backed by `HttpURLConnection`),
 * iOS uses the Ktor Darwin engine (backed by `NSURLSession`).
 *
 * Called by [createHttpClient] during client initialization.
 *
 * @return platform-specific [HttpClientEngine] instance
 * @see createHttpClient
 * @since 0.0.5
 */
expect fun createHttpEngine(): HttpClientEngine
