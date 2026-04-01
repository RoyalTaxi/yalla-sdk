package uz.yalla.data.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * iOS implementation of [ioDispatcher].
 *
 * Delegates to [Dispatchers.Default] because iOS does not provide a
 * dedicated IO dispatcher. The default pool is sufficient for the
 * non-blocking DataStore and Ktor operations used in the data layer.
 *
 * @see ioDispatcher
 * @since 0.0.1
 */
actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
