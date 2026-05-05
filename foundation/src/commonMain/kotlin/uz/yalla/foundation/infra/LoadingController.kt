package uz.yalla.foundation.infra

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/**
 * Controls loading state with smart timing to prevent UI flicker.
 *
 * Features:
 * - Delays showing loading indicator to avoid flash for quick operations
 * - Keeps indicator visible for minimum time once shown to prevent jarring UX
 * - Supports multiple concurrent operations
 * - Thread-safe with mutex synchronization
 *
 * ## Usage
 *
 * ```kotlin
 * class MyViewModel : ViewModel() {
 *     private val loadingController = LoadingController()
 *     val loading: StateFlow<Boolean> = loadingController.loading
 *
 *     fun loadData() {
 *         viewModelScope.launch {
 *             loadingController.withLoading {
 *                 repository.fetchData()
 *             }
 *         }
 *     }
 * }
 * ```
 *
 */
class LoadingController(
    private val showAfter: Duration = DEFAULT_SHOW_AFTER,
    private val minDisplayTime: Duration = DEFAULT_MIN_DISPLAY_TIME
) {
    private val _loading = MutableStateFlow(false)

    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val mutex = Mutex()
    private var activeOperations = 0
    private var showJob: Job? = null
    private var visibleSince: TimeSource.Monotonic.ValueTimeMark? = null
    private var generation = 0L

    /**
     * Executes [block] with loading state management.
     *
     * Optionally override [showAfter] and [minDisplayTime] for this specific call.
     */
    suspend fun <T> withLoading(
        showAfter: Duration = this.showAfter,
        minDisplayTime: Duration = this.minDisplayTime,
        block: suspend () -> T
    ): T =
        coroutineScope {
            mutex.withLock {
                activeOperations++
                if (activeOperations == 1 && !_loading.value) {
                    showJob =
                        launch {
                            delay(showAfter)
                            mutex.withLock {
                                if (activeOperations > 0 && !_loading.value) {
                                    _loading.value = true
                                    visibleSince = TimeSource.Monotonic.markNow()
                                }
                            }
                        }
                }
            }

            try {
                block()
            } finally {
                val (remainingDelay, gen) =
                    mutex.withLock {
                        activeOperations--
                        if (activeOperations == 0) {
                            showJob?.cancel()
                            showJob = null
                            val delay =
                                visibleSince?.elapsedNow()?.let { elapsed ->
                                    val remaining = minDisplayTime - elapsed
                                    if (remaining.isPositive()) remaining else null
                                }
                            Pair(delay, ++generation)
                        } else {
                            Pair(null, generation)
                        }
                    }
                remainingDelay?.let { delay(it) }
                mutex.withLock {
                    if (activeOperations == 0 && generation == gen) {
                        _loading.value = false
                        visibleSince = null
                    }
                }
            }
        }

    companion object {
        /**
         * Operations completing faster than 400 ms never show a spinner —
         * avoids flash for typical fast-path responses.
         */
        val DEFAULT_SHOW_AFTER: Duration = 400.milliseconds

        /**
         * Once the spinner appears, it stays at least 300 ms — prevents
         * a jarring sub-frame dismissal.
         */
        val DEFAULT_MIN_DISPLAY_TIME: Duration = 300.milliseconds
    }
}
