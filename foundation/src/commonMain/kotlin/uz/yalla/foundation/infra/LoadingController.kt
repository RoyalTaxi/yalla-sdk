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
 * @param showAfter Delay before showing loading indicator. Defaults to 400ms.
 * @param minDisplayTime Minimum time to keep indicator visible once shown. Defaults to 300ms.
 * @since 0.0.1
 */
class LoadingController(
    private val showAfter: Duration = DEFAULT_SHOW_AFTER,
    private val minDisplayTime: Duration = DEFAULT_MIN_DISPLAY_TIME,
) {
    private val _loading = MutableStateFlow(false)

    /** Current loading state. Emits true when loading indicator should be visible. @since 0.0.1 */
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
     *
     * @param T Return type of the operation
     * @param showAfter Delay before showing loading. Defaults to controller's configured value.
     * @param minDisplayTime Minimum display time once shown. Defaults to controller's configured value.
     * @param block Suspending operation to execute
     * @return Result of [block]
     * @since 0.0.1
     */
    suspend fun <T> withLoading(
        showAfter: Duration = this.showAfter,
        minDisplayTime: Duration = this.minDisplayTime,
        block: suspend () -> T,
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
                val (remainingDelay, gen) = mutex.withLock {
                    activeOperations--
                    if (activeOperations == 0) {
                        showJob?.cancel()
                        showJob = null
                        val delay = visibleSince?.elapsedNow()?.let { elapsed ->
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
        /** Default delay before showing loading indicator. @since 0.0.1 */
        val DEFAULT_SHOW_AFTER: Duration = 400.milliseconds

        /** Default minimum display time once indicator is shown. @since 0.0.1 */
        val DEFAULT_MIN_DISPLAY_TIME: Duration = 300.milliseconds
    }
}
