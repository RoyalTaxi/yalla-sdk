package uz.yalla.core.session

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Global event bus for unauthorized session events.
 *
 * When the API returns a 401/unauthorized response, publish an event here.
 * Observers (typically the root navigator) collect [events] to trigger
 * logout and redirect to the login screen.
 *
 * Uses a [CONFLATED][kotlinx.coroutines.channels.Channel.Factory.CONFLATED] channel
 * so only the latest event is retained if the collector is slow.
 *
 * ## Usage
 * ```kotlin
 * // Publishing (in network interceptor):
 * UnauthorizedSessionEvents.publish()
 *
 * // Collecting (in root navigator):
 * UnauthorizedSessionEvents.events.collect { navigateToLogin() }
 * ```
 *
 * @since 0.0.1
 */
object UnauthorizedSessionEvents {
    private val eventsChannel = Channel<Unit>(capacity = Channel.CONFLATED)
    val events: Flow<Unit> = eventsChannel.receiveAsFlow()

    /** Emits an unauthorized event to all active collectors. */
    fun publish() {
        eventsChannel.trySend(Unit)
    }

    /**
     * Drops any event sitting in the CONFLATED channel without suspending.
     *
     * Because the event bus is a process-scoped singleton, a test that triggers
     * a 401 can leak a pending event into later tests that share the same test
     * process. Call this in a `@BeforeTest` (or equivalent) block to guarantee
     * each test starts from a clean slate.
     *
     * Safe to call at any time: non-suspending, returns immediately when the
     * channel is already empty.
     *
     * @since 0.0.9
     */
    fun drainPendingEventIfExists() {
        eventsChannel.tryReceive()
    }
}
