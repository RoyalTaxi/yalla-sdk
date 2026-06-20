package uz.yalla.datastore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins the SDK-boundary dedup (#6): an exposed flow re-runs collectors only when its own value changes,
 * not on every write to any key. Without `distinctUntilChanged`, a write to `balance` (or a repeat write
 * of the same token) would re-emit `accessToken` to every collector — wasted work on the hot path where
 * `lastGpsPosition` is written continuously during a ride.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PreferenceFlowDedupTest {
    @Test
    fun accessTokenDoesNotReEmitOnUnrelatedWrites() = runTest {
        val store = InMemoryPreferencesDataStore()
        val scope = CoroutineScope(StandardTestDispatcher(testScheduler))
        val session = SessionPreferencesImpl(store, scope)
        val config = ConfigPreferencesImpl(store, scope)

        val emissions = mutableListOf<String>()
        val collector = backgroundScope.launch { session.accessToken.toList(emissions) }
        advanceUntilIdle()

        session.setAccessToken("token-1")
        advanceUntilIdle()
        // Unrelated writes (different keys) and a redundant same-value token write must not re-emit.
        config.setBalance(500L)
        advanceUntilIdle()
        config.setBalance(999L)
        advanceUntilIdle()
        session.setAccessToken("token-1")
        advanceUntilIdle()

        collector.cancel()

        // Only the initial empty value and the single real change.
        assertEquals(listOf("", "token-1"), emissions)
    }
}
