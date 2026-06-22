package uz.yalla.datastore

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SecureMigrationTest {
    @Test
    fun legacyCleartextTokenIsMovedIntoSecureStoreAndScrubbed() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val secure = FakeSecureStore()
            store.edit { it[stringPreferencesKey(PreferenceKeys.ACCESS_TOKEN.name)] = "legacy-token" }
            val session = SessionPreferencesImpl(store, secure)

            val token = session.accessToken.first()
            advanceUntilIdle()

            assertEquals("legacy-token", token)
            assertEquals("legacy-token", secure.peek(PreferenceKeys.ACCESS_TOKEN.name))
            assertFalse(store.snapshot().contains(stringPreferencesKey(PreferenceKeys.ACCESS_TOKEN.name)))
        }

    @Test
    fun legacyCleartextPiiIsMigratedForEachSensitiveKey() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val secure = FakeSecureStore()
            store.edit { prefs ->
                prefs[stringPreferencesKey(PreferenceKeys.NUMBER.name)] = "998901112233"
                prefs[stringPreferencesKey(PreferenceKeys.FIRST_NAME.name)] = "Aziz"
            }
            val user = UserPreferencesImpl(store, secure, CoroutineScope(StandardTestDispatcher(testScheduler)))

            assertEquals("998901112233", user.number.first())
            assertEquals("Aziz", user.firstName.first())
            advanceUntilIdle()

            assertEquals("998901112233", secure.peek(PreferenceKeys.NUMBER.name))
            assertEquals("Aziz", secure.peek(PreferenceKeys.FIRST_NAME.name))
            assertFalse(store.snapshot().contains(stringPreferencesKey(PreferenceKeys.NUMBER.name)))
            assertFalse(store.snapshot().contains(stringPreferencesKey(PreferenceKeys.FIRST_NAME.name)))
        }

    @Test
    fun absentLegacyValueIsANoOp() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val secure = FakeSecureStore()
            val session = SessionPreferencesImpl(store, secure)

            assertEquals("", session.accessToken.first())
            advanceUntilIdle()

            assertNull(secure.peek(PreferenceKeys.ACCESS_TOKEN.name))
        }

    @Test
    fun secureKeysCoverTheCredentialsAndPiiButNotTheUxPrefs() {
        val secure = PreferenceKeys.SECURE_KEYS
        assertEquals(true, secure.contains(PreferenceKeys.ACCESS_TOKEN.name))
        assertEquals(true, secure.contains(PreferenceKeys.FIREBASE_TOKEN.name))
        assertEquals(true, secure.contains(PreferenceKeys.NUMBER.name))
        assertEquals(true, secure.contains(PreferenceKeys.BIRTHDAY.name))
        assertEquals(true, secure.contains(PreferenceKeys.CARD_NUMBER.name))
        assertFalse(secure.contains(PreferenceKeys.LOCALE_TYPE.name))
        assertFalse(secure.contains(PreferenceKeys.THEME_TYPE.name))
        assertFalse(secure.contains(PreferenceKeys.PAYMENT_TYPE.name))
        secure.forEach { name ->
            assertEquals(true, PreferenceKeys.SESSION_KEYS.any { it.name == name }, "SESSION_KEYS lacks $name")
        }
    }
}
