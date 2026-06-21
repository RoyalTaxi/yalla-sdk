package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.scope.Scope

/** The on-disk DataStore filename, shared by both platform actuals so they cannot silently diverge. */
internal const val DATASTORE_FILE: String = "prefs.preferences_pb"

/**
 * Builds the single shared [DataStore], resolving any platform inputs it needs from [scope].
 *
 * Android pulls `Context` from [scope]; iOS resolves its path directly and ignores it. Both actuals pass
 * a `ReplaceFileCorruptionHandler { emptyPreferences() }` so a corrupt prefs file self-heals to empty
 * (forcing a clean re-auth) instead of throwing on every read forever.
 */
internal expect fun createDataStore(scope: Scope): DataStore<Preferences>
