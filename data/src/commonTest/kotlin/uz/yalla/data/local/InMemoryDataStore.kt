package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Minimal in-memory [DataStore] used across preference-impl test classes.
 *
 * All writes go through [updateData], so ordering and concurrency are
 * deterministic inside a `runTest { ... }` block — no disk I/O, no
 * platform `PreferenceDataStoreFactory`, no coroutine scheduler quirks.
 */
internal class InMemoryDataStore(
    initial: Preferences = mutablePreferencesOf(),
) : DataStore<Preferences> {
    private val state = MutableStateFlow(initial)

    override val data = state.asStateFlow()

    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.value = updated
        return updated
    }
}
