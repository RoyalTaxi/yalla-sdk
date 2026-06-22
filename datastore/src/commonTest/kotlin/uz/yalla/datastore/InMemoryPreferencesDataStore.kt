package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class InMemoryPreferencesDataStore(
    initial: Preferences = emptyPreferences()
) : DataStore<Preferences> {
    private val state = MutableStateFlow(initial.toPreferences())

    override val data: Flow<Preferences> = state

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val updated = transform(state.value.toMutablePreferences()).toPreferences()
        state.value = updated
        return updated
    }

    fun snapshot(): Preferences = state.value
}
