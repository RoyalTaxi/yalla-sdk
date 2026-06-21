package uz.yalla.core.session

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * SDK-wide broadcast of session-invalidation (401 / unauthorized) signals.
 *
 * [unauthorized] is a hot broadcast: every active collector receives each
 * [publishUnauthorized] emission. Events are not replayed to late subscribers — a
 * subscriber only sees signals published while it is collecting — so a stale event from a
 * past session cannot force-logout a freshly authenticated one.
 */
public interface SessionEventBus {
    /** Emits once per [publishUnauthorized]; delivered to all active collectors, no replay. */
    public val unauthorized: Flow<Unit>

    /** Signals that the current session is unauthorized (e.g. a 401), driving forced logout. */
    public fun publishUnauthorized()
}

/**
 * Default [SessionEventBus] backed by a broadcast [MutableSharedFlow].
 *
 * `replay = 0` guarantees a new subscriber never receives a past event (no cross-session
 * bleed); `extraBufferCapacity = 1` + [BufferOverflow.DROP_OLDEST] keep [publishUnauthorized]
 * non-suspending and coalesce a burst with no collector to a single pending event — preserving
 * the conflation behavior callers relied on, without the fan-out (single-consumer) hazard of a
 * `Channel`.
 */
internal class DefaultSessionEventBus : SessionEventBus {
    private val _unauthorized =
        MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override val unauthorized: Flow<Unit> = _unauthorized.asSharedFlow()

    override fun publishUnauthorized() {
        _unauthorized.tryEmit(Unit)
    }
}

/** Creates the SDK's default unauthorized-session event bus. */
public fun createSessionEventBus(): SessionEventBus = DefaultSessionEventBus()
