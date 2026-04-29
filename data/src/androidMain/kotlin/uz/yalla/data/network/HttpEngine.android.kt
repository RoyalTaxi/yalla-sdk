package uz.yalla.data.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

/**
 * Android implementation of [createHttpEngine].
 *
 * Uses the Ktor [Android] engine backed by `HttpURLConnection`.
 *
 * @return [HttpClientEngine] configured for Android
 * @see createHttpEngine
 */
actual fun createHttpEngine(): HttpClientEngine = Android.create()
