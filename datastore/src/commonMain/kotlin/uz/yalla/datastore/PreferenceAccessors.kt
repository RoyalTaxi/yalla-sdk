package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val EMPTY_PREFERENCES: Preferences = emptyPreferences()

internal fun <T> DataStore<Preferences>.readFlow(transform: (Preferences) -> T): Flow<T> =
    data
        .map { prefs ->
            try {
                transform(prefs)
            } catch (_: ClassCastException) {
                transform(EMPTY_PREFERENCES)
            }
        }.distinctUntilChanged()

internal fun DataStore<Preferences>.write(
    scope: CoroutineScope,
    block: (MutablePreferences) -> Unit
) {
    scope.launch { edit(block) }
}

internal suspend fun DataStore<Preferences>.writeNow(block: (MutablePreferences) -> Unit) {
    edit(block)
}
