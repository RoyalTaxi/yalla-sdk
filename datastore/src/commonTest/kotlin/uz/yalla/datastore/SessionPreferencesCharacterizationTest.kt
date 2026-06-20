package uz.yalla.datastore

import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import uz.yalla.core.settings.LocaleKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Pins the three reset paths' differing semantics — the load-bearing, easily-desynced session contract.
 *
 * The key regression this guards (#1): the explicit Log-out path ([SessionPreferences.clearAndEnterGuestMode])
 * must PRESERVE the user-experience prefs (locale, theme, map style, onboarding, last positions) — the
 * same set [SessionPreferences.clearSession] keeps — not wipe them like the old `prefs.clear()` did.
 * Before the fix, [logoutPreservesUserExperiencePrefs] fails because the user's language reset to Uz.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SessionPreferencesCharacterizationTest {
    @Test
    fun clearSessionDropsCredentialsButKeepsUserExperiencePrefs() = runTest {
        val store = InMemoryPreferencesDataStore()
        seedAll(store)
        val session = SessionPreferencesImpl(store, CoroutineScope(StandardTestDispatcher(testScheduler)))

        session.clearSession()
        advanceUntilIdle()

        // Credentials and cached profile/config gone.
        assertEquals("", session.accessToken.first())
        // UX prefs preserved.
        assertEquals(LocaleKind.Ru, InterfacePreferencesImpl(store, this.backgroundScope).localeType.first())
        assertTrue(store.snapshot().contains(PreferenceKeys.THEME_TYPE))
        assertTrue(store.snapshot().contains(PreferenceKeys.SKIP_ONBOARDING))
        assertTrue(store.snapshot().contains(PreferenceKeys.LAST_GPS_POSITION))
    }

    @Test
    fun logoutPreservesUserExperiencePrefs() = runTest {
        val store = InMemoryPreferencesDataStore()
        seedAll(store)
        val session = SessionPreferencesImpl(store, CoroutineScope(StandardTestDispatcher(testScheduler)))

        session.clearAndEnterGuestMode()

        // Token cleared, guest flag set...
        assertEquals("", session.accessToken.first())
        assertTrue(session.isGuestMode.first())
        // ...but the user's language/theme/onboarding survive logout.
        val ui = InterfacePreferencesImpl(store, this.backgroundScope)
        assertEquals(LocaleKind.Ru, ui.localeType.first())
        assertTrue(store.snapshot().contains(PreferenceKeys.THEME_TYPE))
        assertTrue(store.snapshot().contains(PreferenceKeys.MAP_TYPE))
        assertTrue(store.snapshot().contains(PreferenceKeys.SKIP_ONBOARDING))
    }

    @Test
    fun clearAllWipesEverythingIncludingUserExperiencePrefs() = runTest {
        val store = InMemoryPreferencesDataStore()
        seedAll(store)
        val session = SessionPreferencesImpl(store, CoroutineScope(StandardTestDispatcher(testScheduler)))

        session.clearAll()
        advanceUntilIdle()

        assertFalse(store.snapshot().contains(PreferenceKeys.ACCESS_TOKEN))
        assertFalse(store.snapshot().contains(PreferenceKeys.LOCALE_TYPE))
        assertFalse(store.snapshot().contains(PreferenceKeys.THEME_TYPE))
        assertFalse(store.snapshot().contains(PreferenceKeys.LAST_GPS_POSITION))
    }

    @Test
    fun sessionKeysExcludesTheUserExperiencePrefs() {
        // The curated SESSION_KEYS list is the contract: a new sensitive key added here without listing
        // it would silently survive logout; the UX prefs must stay excluded so logout never drops them.
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
        // ...and the access token MUST be cleared on a session reset.
        assertTrue(PreferenceKeys.SESSION_KEYS.contains(PreferenceKeys.ACCESS_TOKEN))
    }

    /** Seeds one credential/session value and every user-experience pref, written directly. */
    private suspend fun seedAll(store: InMemoryPreferencesDataStore) {
        store.edit { prefs ->
            prefs[PreferenceKeys.ACCESS_TOKEN] = "live-token"
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
