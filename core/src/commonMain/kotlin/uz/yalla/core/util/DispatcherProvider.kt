package uz.yalla.core.util

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Provides platform-specific coroutine dispatchers.
 * Use this interface for dependency injection to enable testing with test dispatchers.
 */
interface DispatcherProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}
