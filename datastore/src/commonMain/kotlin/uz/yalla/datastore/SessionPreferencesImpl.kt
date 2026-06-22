package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import uz.yalla.core.preferences.SessionPreferences
import uz.yalla.core.util.orFalse

internal class SessionPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val secureStore: SecureStore
) : SessionPreferences {
    override val accessToken: Flow<String> =
        dataStore.secureReadFlow(PreferenceKeys.ACCESS_TOKEN.name, secureStore)

    override suspend fun setAccessToken(value: String) {
        dataStore.secureSet(PreferenceKeys.ACCESS_TOKEN.name, value, secureStore)
    }

    override val firebaseToken: Flow<String> =
        dataStore.secureReadFlow(PreferenceKeys.FIREBASE_TOKEN.name, secureStore)

    override suspend fun setFirebaseToken(value: String) {
        dataStore.secureSet(PreferenceKeys.FIREBASE_TOKEN.name, value, secureStore)
    }

    override val isGuestMode: Flow<Boolean> = dataStore.readFlow { it[PreferenceKeys.IS_GUEST_MODE].orFalse() }

    override suspend fun setGuestMode(value: Boolean) {
        dataStore.writeNow { it[PreferenceKeys.IS_GUEST_MODE] = value }
    }

    override val isDeviceRegistered: Flow<Boolean> =
        dataStore.readFlow { it[PreferenceKeys.IS_DEVICE_REGISTERED].orFalse() }

    override suspend fun setDeviceRegistered(value: Boolean) {
        dataStore.writeNow { it[PreferenceKeys.IS_DEVICE_REGISTERED] = value }
    }

    override suspend fun clearSession() {
        dataStore.secureClear(PreferenceKeys.SECURE_KEYS, secureStore) { prefs ->
            PreferenceKeys.SESSION_KEYS.forEach { prefs.remove(it) }
        }
    }

    override suspend fun clearAll() {
        dataStore.secureClear(PreferenceKeys.SECURE_KEYS, secureStore) { it.clear() }
    }

    override suspend fun clearAndEnterGuestMode() {
        dataStore.secureClear(PreferenceKeys.SECURE_KEYS, secureStore) { prefs ->
            PreferenceKeys.SESSION_KEYS.forEach { prefs.remove(it) }
            prefs[PreferenceKeys.IS_GUEST_MODE] = true
        }
    }
}
