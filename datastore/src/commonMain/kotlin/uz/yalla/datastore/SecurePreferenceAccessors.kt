package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

private fun revisionKey(key: String): Preferences.Key<String> = stringPreferencesKey("secure.$key.rev")

private fun legacyKey(key: String): Preferences.Key<String> = stringPreferencesKey(key)

internal fun DataStore<Preferences>.secureReadFlow(
    key: String,
    secureStore: SecureStore
): Flow<String> =
    data
        .map { it[revisionKey(key)] }
        .distinctUntilChanged()
        .map {
            migrateIfNeeded(key, secureStore)
            secureStore.get(key).orEmpty()
        }.distinctUntilChanged()

internal fun DataStore<Preferences>.secureWrite(
    key: String,
    value: String,
    secureStore: SecureStore,
    scope: CoroutineScope
) {
    scope.launch { secureSet(key, value, secureStore) }
}

internal fun DataStore<Preferences>.secureRemove(
    key: String,
    secureStore: SecureStore,
    scope: CoroutineScope
) {
    scope.launch { secureUnset(key, secureStore) }
}

internal suspend fun DataStore<Preferences>.secureSet(
    key: String,
    value: String,
    secureStore: SecureStore
) {
    secureStore.put(key, value)
    edit { it.bumpRevision(key) }
}

internal suspend fun DataStore<Preferences>.secureUnset(
    key: String,
    secureStore: SecureStore
) {
    secureStore.remove(key)
    edit { it.bumpRevision(key) }
}

internal suspend fun DataStore<Preferences>.secureClear(
    keys: List<String>,
    secureStore: SecureStore,
    alsoEdit: (MutablePreferences) -> Unit = {}
) {
    keys.forEach { secureStore.remove(it) }
    edit { prefs ->
        keys.forEach { prefs.bumpRevision(it) }
        alsoEdit(prefs)
    }
    yield()
}

internal suspend fun DataStore<Preferences>.migrateIfNeeded(
    key: String,
    secureStore: SecureStore
) {
    val legacy = data.first()[legacyKey(key)] ?: return
    secureStore.put(key, legacy)
    edit { prefs ->
        prefs.remove(legacyKey(key))
        prefs.bumpRevision(key)
    }
}

private fun MutablePreferences.bumpRevision(key: String) {
    val next = (this[revisionKey(key)]?.toLongOrNull() ?: 0L) + 1L
    this[revisionKey(key)] = next.toString()
}
