package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Minimal in-memory [DataStore] over the real androidx `Preferences` type, so tests exercise the actual
 * impl read/`edit`/`remove`/`clear` logic without a filesystem. Backed by a [MutableStateFlow], so a
 * single write re-emits to every collector — matching production `DataStore.data` behaviour, which is
 * exactly what the dedup (`distinctUntilChanged`) and reset-contract tests need to observe.
 */
internal class InMemoryPreferencesDataStore(
    initial: Preferences = emptyPreferences()
) : DataStore<Preferences> {
    private val state = MutableStateFlow(initial.toPreferences())

    override val data: Flow<Preferences> = state

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        // Hand the transform a fresh, mutable copy (as androidx's own `edit` does) and persist an
        // immutable snapshot of the result.
        val updated = transform(state.value.toMutablePreferences()).toPreferences()
        state.value = updated
        return updated
    }

    /** Snapshot of the current persisted preferences, for asserting which keys remain after a clear. */
    fun snapshot(): Preferences = state.value
}
