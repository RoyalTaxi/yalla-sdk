package uz.yalla.data.util

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Platform-specific [CoroutineDispatcher] for IO-bound operations.
 *
 * Maps to [Dispatchers.IO][kotlinx.coroutines.Dispatchers.IO] on Android
 * and [Dispatchers.Default][kotlinx.coroutines.Dispatchers.Default] on iOS
 * (where a dedicated IO dispatcher is not available).
 *
 * Used by [createHttpClient][uz.yalla.data.network.createHttpClient] and
 * all preferences implementations for background writes.
 */
expect val ioDispatcher: CoroutineDispatcher
