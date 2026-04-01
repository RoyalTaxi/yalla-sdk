package uz.yalla.data.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

/**
 * iOS implementation of [createHttpEngine].
 *
 * Uses the Ktor [Darwin] engine backed by `NSURLSession`.
 *
 * @return [HttpClientEngine] configured for iOS
 * @see createHttpEngine
 * @since 0.0.5
 */
actual fun createHttpEngine(): HttpClientEngine = Darwin.create()
