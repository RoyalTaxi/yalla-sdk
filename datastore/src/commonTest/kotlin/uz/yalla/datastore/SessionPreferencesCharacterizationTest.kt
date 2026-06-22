package uz.yalla.datastore

import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import uz.yalla.core.settings.LocaleKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SessionPreferencesCharacterizationTest {
    @Test
    fun clearSessionDropsCredentialsButKeepsUserExperiencePrefs() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val secure = FakeSecureStore()
            seedAll(store, secure)
            val session = SessionPreferencesImpl(store, secure)

            session.clearSession()
            advanceUntilIdle()

            assertEquals("", session.accessToken.first())
            assertNull(secure.peek(PreferenceKeys.ACCESS_TOKEN.name))
            assertEquals(LocaleKind.Ru, InterfacePreferencesImpl(store, this.backgroundScope).localeType.first())
            assertTrue(store.snapshot().contains(PreferenceKeys.THEME_TYPE))
            assertTrue(store.snapshot().contains(PreferenceKeys.SKIP_ONBOARDING))
            assertTrue(store.snapshot().contains(PreferenceKeys.LAST_GPS_POSITION))
        }

    @Test
    fun logoutPreservesUserExperiencePrefs() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val secure = FakeSecureStore()
            seedAll(store, secure)
            val session = SessionPreferencesImpl(store, secure)

            session.clearAndEnterGuestMode()

            assertEquals("", session.accessToken.first())
            assertNull(secure.peek(PreferenceKeys.ACCESS_TOKEN.name))
            assertTrue(session.isGuestMode.first())
            val ui = InterfacePreferencesImpl(store, this.backgroundScope)
            assertEquals(LocaleKind.Ru, ui.localeType.first())
            assertTrue(store.snapshot().contains(PreferenceKeys.THEME_TYPE))
            assertTrue(store.snapshot().contains(PreferenceKeys.MAP_TYPE))
            assertTrue(store.snapshot().contains(PreferenceKeys.SKIP_ONBOARDING))
        }

    @Test
    fun clearAllWipesEverythingIncludingUserExperiencePrefs() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val secure = FakeSecureStore()
            seedAll(store, secure)
            val session = SessionPreferencesImpl(store, secure)

            session.clearAll()
            advanceUntilIdle()

            assertNull(secure.peek(PreferenceKeys.ACCESS_TOKEN.name))
            assertFalse(store.snapshot().contains(PreferenceKeys.LOCALE_TYPE))
            assertFalse(store.snapshot().contains(PreferenceKeys.THEME_TYPE))
            assertFalse(store.snapshot().contains(PreferenceKeys.LAST_GPS_POSITION))
        }

    @Test
    fun sessionKeysExcludesTheUserExperiencePrefs() {
        val uxPrefs =
            listOf(
                PreferenceKeys.LOCALE_TYPE,
                PreferenceKeys.THEME_TYPE,
                PreferenceKeys.MAP_TYPE,
                PreferenceKeys.SKIP_ONBOARDING,
                PreferenceKeys.LAST_MAP_POSITION,
                PreferenceKeys.LAST_GPS_POSITION
            )
        uxPrefs.forEach { key ->
            assertFalse(PreferenceKeys.SESSION_KEYS.contains(key), "SESSION_KEYS must not contain $key")
        }
        assertTrue(PreferenceKeys.SESSION_KEYS.contains(PreferenceKeys.ACCESS_TOKEN))
    }

    private suspend fun seedAll(
        store: InMemoryPreferencesDataStore,
        secure: FakeSecureStore
    ) {
        secure.seed(PreferenceKeys.ACCESS_TOKEN.name, "live-token")
        store.edit { prefs ->
            prefs[PreferenceKeys.IS_DEVICE_REGISTERED] = true
            prefs[PreferenceKeys.BALANCE] = 1_000L
            prefs[PreferenceKeys.LOCALE_TYPE] = LocaleKind.Ru.code
            prefs[PreferenceKeys.THEME_TYPE] = "dark"
            prefs[PreferenceKeys.MAP_TYPE] = "libre"
            prefs[PreferenceKeys.SKIP_ONBOARDING] = true
            prefs[PreferenceKeys.LAST_MAP_POSITION] = "41.0,69.0"
            prefs[PreferenceKeys.LAST_GPS_POSITION] = "41.1,69.1"
        }
    }
}
