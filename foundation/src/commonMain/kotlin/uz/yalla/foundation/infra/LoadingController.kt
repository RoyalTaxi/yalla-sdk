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

@OptIn(ExperimentalCoroutinesApi::class)
public class LoadingController(
    scope: CoroutineScope,
    private val gracePeriod: Duration = GRACE_PERIOD,
    private val minVisible: Duration = MIN_VISIBLE
) {
    private val active = MutableStateFlow(0)
    private var shownAt: TimeSource.Monotonic.ValueTimeMark? = null

    public val loading: StateFlow<Boolean> = active
        .map { it > 0 }
        .distinctUntilChanged()
        .transformLatest { busy ->
            if (busy) {
                delay(gracePeriod)
                shownAt = TimeSource.Monotonic.markNow()
                emit(true)
                awaitCancellation()
            } else {
                shownAt?.let { delay(minVisible - it.elapsedNow()) }
                shownAt = null
                emit(false)
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, false)

    public suspend fun <T> withLoading(block: suspend () -> T): T {
        active.update { it + 1 }
        try {
            return block()
        } finally {
            active.update { it - 1 }
        }
    }

    public companion object {
        public val GRACE_PERIOD: Duration = 200.milliseconds
        public val MIN_VISIBLE: Duration = 500.milliseconds
    }
}
