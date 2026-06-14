package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.preferences.SessionPreferences
import uz.yalla.core.preferences.StaticPreferences
import uz.yalla.core.util.orFalse

internal class SessionPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
    private val staticPreferences: StaticPreferences
) : SessionPreferences {
    override val accessToken: Flow<String> = dataStore.data.map { it[PreferenceKeys.ACCESS_TOKEN].orEmpty() }

    override fun setAccessToken(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.ACCESS_TOKEN] = value } }
    }

    override val firebaseToken: Flow<String> = dataStore.data.map { it[PreferenceKeys.FIREBASE_TOKEN].orEmpty() }

    override fun setFirebaseToken(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.FIREBASE_TOKEN] = value } }
    }

    override val isGuestMode: Flow<Boolean> = dataStore.data.map { it[PreferenceKeys.IS_GUEST_MODE].orFalse() }

    override fun setGuestMode(value: Boolean) {
        staticPreferences.setGuestMode(value)
        scope.launch { dataStore.edit { it[PreferenceKeys.IS_GUEST_MODE] = value } }
    }

    override val isDeviceRegistered: Flow<Boolean> =
        dataStore.data.map {
            it[PreferenceKeys.IS_DEVICE_REGISTERED].orFalse()
        }

    override fun setDeviceRegistered(value: Boolean) {
        staticPreferences.setDeviceRegistered(value)
        scope.launch { dataStore.edit { it[PreferenceKeys.IS_DEVICE_REGISTERED] = value } }
    }

    override fun clearSession() {
        staticPreferences.setGuestMode(false)
        staticPreferences.setDeviceRegistered(false)
        scope.launch {
            dataStore.edit { prefs ->
                PreferenceKeys.SESSION_KEYS.forEach { prefs.remove(it) }
            }
        }
    }

    override fun clearAll() {
        staticPreferences.setGuestMode(false)
        staticPreferences.setDeviceRegistered(false)
        scope.launch {
            dataStore.edit { it.clear() }
        }
    }
}
