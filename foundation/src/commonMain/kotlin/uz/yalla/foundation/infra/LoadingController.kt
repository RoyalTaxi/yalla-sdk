package uz.yalla.foundation.infra

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/**
 * Reference-counted, anti-flicker loading-state machine.
 *
 * [loading] emits `true` only once in-flight work outlives [gracePeriod] (so brief work shows no
 * spinner), and once shown it stays visible for at least [minVisible] (so the spinner never flashes
 * away instantly). Overlapping [withLoading] calls are reference-counted: the indicator stays up
 * until the last one completes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
public class LoadingController(
    scope: CoroutineScope,
    private val gracePeriod: Duration = GRACE_PERIOD,
    private val minVisible: Duration = MIN_VISIBLE
) {
    private val active = MutableStateFlow(0)
    private var shownAt: TimeSource.Monotonic.ValueTimeMark? = null

    /** Debounced visibility flag for the shared loading indicator. */
    public val loading: StateFlow<Boolean> =
        active
            .map { it > 0 }
            .distinctUntilChanged()
            .transformLatest { busy ->
                if (busy) {
                    delay(gracePeriod)
                    shownAt = TimeSource.Monotonic.markNow()
                    emit(true)
                    awaitCancellation()
                } else {
                    // Hold the spinner for the remainder of the min-visible window; clamp at zero so
                    // work that already outlived minVisible hides immediately (no negative delay).
                    shownAt?.let { delay((minVisible - it.elapsedNow()).coerceAtLeast(Duration.ZERO)) }
                    shownAt = null
                    emit(false)
                }
            }.stateIn(scope, SharingStarted.Eagerly, false)

    /**
     * Runs [block], counting it as in-flight for the duration. The count is decremented in a
     * `finally`, so the normal, exception, and cancellation paths all release it — a leaked decrement
     * would strand [loading] at `true` for the controller's lifetime.
     */
    public suspend fun <T> withLoading(block: suspend () -> T): T {
        active.update { it + 1 }
        try {
            return block()
        } finally {
            active.update { it - 1 }
        }
    }

    public companion object {
        /** Delay before a spinner is shown, suppressing it for work that finishes quickly. */
        public val GRACE_PERIOD: Duration = 200.milliseconds

        /** Minimum time a shown spinner stays visible, preventing an instant flash-away. */
        public val MIN_VISIBLE: Duration = 500.milliseconds
    }
}
