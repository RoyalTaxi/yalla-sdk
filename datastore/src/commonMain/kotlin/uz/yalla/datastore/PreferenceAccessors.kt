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

/**
 * The single read/write policy for the five `*PreferencesImpl` classes. Every preference flows through
 * here so the cross-cutting rules — dedup on read, type-drift safety, and write error handling — are
 * expressed once instead of copy-pasted ~30 times (charter §0.4/§0.5).
 */

private val EMPTY_PREFERENCES: Preferences = emptyPreferences()

/**
 * Reads from `data`, mapping each snapshot via [transform].
 *
 * Applies [distinctUntilChanged] so a write to any key does not re-run this collector — `DataStore.data`
 * re-emits the whole snapshot on every write to any key — and guards [ClassCastException] so a key whose
 * persisted type drifted across an app upgrade defaults (as if absent) instead of throwing inside
 * `data.map` and taking down every collector of this cold flow.
 */
internal fun <T> DataStore<Preferences>.readFlow(transform: (Preferences) -> T): Flow<T> =
    data
        .map { prefs ->
            try {
                transform(prefs)
            } catch (_: ClassCastException) {
                transform(EMPTY_PREFERENCES)
            }
        }
        .distinctUntilChanged()

/**
 * Launches [block] as an `edit` on the shared [scope].
 *
 * An `IOException`-class failure from `edit` (disk full, corruption, atomic-rename failure) propagates to
 * the scope's [kotlinx.coroutines.CoroutineExceptionHandler] — installed once in [datastoreModule] — so
 * it is reported once instead of being silently dropped on Native or reaching the default uncaught
 * handler (process crash) on Android. Centralising the launch here is what makes that single handler the
 * one place the write-failure policy lives, rather than ~30 bare `scope.launch { edit { } }` sites.
 */
internal fun DataStore<Preferences>.write(
    scope: CoroutineScope,
    block: (MutablePreferences) -> Unit
) {
    scope.launch { edit(block) }
}
