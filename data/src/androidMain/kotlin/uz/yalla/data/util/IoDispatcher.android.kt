package uz.yalla.data.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Android implementation of [ioDispatcher].
 *
 * Delegates to [Dispatchers.IO], which is backed by a shared pool of
 * threads optimized for blocking IO operations.
 *
 * @see ioDispatcher
 * @since 0.0.1
 */
actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
