package uz.yalla.datastore

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PreferenceFlowDedupTest {
    @Test
    fun accessTokenDoesNotReEmitOnUnrelatedWrites() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val secure = FakeSecureStore()
            val session = SessionPreferencesImpl(store, secure)
            val config = ConfigPreferencesImpl(store, backgroundScope)

            val emissions = mutableListOf<String>()
            val collector = launch { session.accessToken.toList(emissions) }
            advanceUntilIdle()

            session.setAccessToken("token-1")
            advanceUntilIdle()
            config.setBalance(500L)
            advanceUntilIdle()
            config.setBalance(999L)
            advanceUntilIdle()
            session.setAccessToken("token-1")
            advanceUntilIdle()

            collector.cancel()

            assertEquals(listOf("", "token-1"), emissions)
        }

    @Test
    fun activeAccessTokenCollectorSeesTheValueClearedOnLogout() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val secure = FakeSecureStore()
            val session = SessionPreferencesImpl(store, secure)

            val emissions = mutableListOf<String>()
            val collector = launch { session.accessToken.toList(emissions) }
            advanceUntilIdle()

            session.setAccessToken("token-1")
            advanceUntilIdle()
            session.clearSession()
            advanceUntilIdle()

            collector.cancel()

            assertEquals(listOf("", "token-1", ""), emissions)
        }
}
