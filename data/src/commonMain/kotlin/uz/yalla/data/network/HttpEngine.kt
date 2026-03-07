package uz.yalla.data.network

import io.ktor.client.engine.HttpClientEngine

/**
 * Platform-specific HTTP client engine.
 *
 * Android uses the Android engine, iOS uses the Darwin engine.
 *
 * @since 0.0.5
 */
expect fun createHttpEngine(): HttpClientEngine
