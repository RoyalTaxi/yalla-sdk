package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import uz.yalla.core.preferences.SessionPreferences
import uz.yalla.core.util.orFalse

internal class SessionPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) : SessionPreferences {
    override val accessToken: Flow<String> = dataStore.readFlow { it[PreferenceKeys.ACCESS_TOKEN].orEmpty() }

    override fun setAccessToken(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.ACCESS_TOKEN] = value }
    }

    override val firebaseToken: Flow<String> = dataStore.readFlow { it[PreferenceKeys.FIREBASE_TOKEN].orEmpty() }

    override fun setFirebaseToken(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.FIREBASE_TOKEN] = value }
    }

    override val isGuestMode: Flow<Boolean> = dataStore.readFlow { it[PreferenceKeys.IS_GUEST_MODE].orFalse() }

    override fun setGuestMode(value: Boolean) {
        dataStore.write(scope) { it[PreferenceKeys.IS_GUEST_MODE] = value }
    }

    override val isDeviceRegistered: Flow<Boolean> =
        dataStore.readFlow { it[PreferenceKeys.IS_DEVICE_REGISTERED].orFalse() }

    override fun setDeviceRegistered(value: Boolean) {
        dataStore.write(scope) { it[PreferenceKeys.IS_DEVICE_REGISTERED] = value }
    }

    override fun clearSession() {
        dataStore.write(scope) { prefs ->
            PreferenceKeys.SESSION_KEYS.forEach { prefs.remove(it) }
        }
    }

    override fun clearAll() {
        dataStore.write(scope) { it.clear() }
    }

    override suspend fun clearAndEnterGuestMode() {
        dataStore.edit { prefs ->
            // Logout: clear only the session-scoped keys (same contract as clearSession), PRESERVING the
            // user-experience prefs (locale, theme, map style, onboarding, last positions). A full
            // prefs.clear() here would reset the user's language/theme on logout — see SESSION_KEYS.
            PreferenceKeys.SESSION_KEYS.forEach { prefs.remove(it) }
            prefs[PreferenceKeys.IS_GUEST_MODE] = true
        }
    }
}
