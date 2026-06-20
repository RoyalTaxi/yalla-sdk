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

/**
 * The read/write/migration policy for the SENSITIVE keys — the secure-store counterpart to
 * [readFlow]/[write]. Sensitive values live in [SecureStore] (encrypted at rest), but the public surface
 * is still a reactive `Flow<String>`, so this seam bridges the two while keeping the same dedup contract
 * the plain-prefs path has.
 *
 * Reactivity: a secure value is not part of `DataStore.data`, so a secure write wouldn't otherwise
 * re-emit. Each sensitive key carries a tiny **revision marker** in the plain DataStore that
 * [secureWrite]/[secureRemove]/[migrateIfNeeded] bump; [secureReadFlow] keys off that marker (deduped via
 * [distinctUntilChanged]) and reads the fresh secure value on each change. The marker holds NO sensitive
 * data — only a monotonically-changing token.
 */

/** Marker key whose value changes on every secure write, so the derived read flow re-reads. */
private fun revisionKey(key: String): Preferences.Key<String> = stringPreferencesKey("secure.$key.rev")

/** The plain-DataStore key a legacy (pre-encryption) cleartext value would have been stored under. */
private fun legacyKey(key: String): Preferences.Key<String> = stringPreferencesKey(key)

/**
 * A reactive view of the encrypted value for [key], emitting `""` when absent — mirroring the plain-prefs
 * `.orEmpty()` readers so the `*PreferencesImpl` getters keep their exact contract.
 *
 * Performs transparent migration on first observation: if a legacy CLEARTEXT value still sits in the plain
 * DataStore under [key] (a pre-encryption install), it is moved into [secureStore] and the plaintext is
 * deleted, so a logged-in user is not signed out and the old plaintext is scrubbed (see [migrateIfNeeded]).
 */
internal fun DataStore<Preferences>.secureReadFlow(
    key: String,
    secureStore: SecureStore
): Flow<String> =
    data
        // Re-run only when this key's revision marker changes (deduped), not on every unrelated write — the
        // marker is bumped by every secure write/remove and by the migration below.
        .map { it[revisionKey(key)] }
        .distinctUntilChanged()
        // Read-through migration: a legacy cleartext value is moved into the secure store and scrubbed on the
        // FIRST read, so the first emission already reflects it (the user stays logged in). Idempotent.
        .map {
            migrateIfNeeded(key, secureStore)
            secureStore.get(key).orEmpty()
        }
        // Collapse same-value re-writes (a redundant token set bumps the marker but not the value), matching
        // the plain-prefs readFlow's dedup contract at the SDK boundary.
        .distinctUntilChanged()

/** Encrypts [value] under [key] and bumps the revision marker so [secureReadFlow] re-emits. */
internal fun DataStore<Preferences>.secureWrite(
    key: String,
    value: String,
    secureStore: SecureStore,
    scope: CoroutineScope
) {
    scope.launch { secureSet(key, value, secureStore) }
}

/** Removes the encrypted value for [key], bumping its revision marker so [secureReadFlow] re-emits. */
internal fun DataStore<Preferences>.secureRemove(
    key: String,
    secureStore: SecureStore,
    scope: CoroutineScope
) {
    scope.launch { secureUnset(key, secureStore) }
}

/** Suspending encrypt-and-bump, so callers can ORDER it before a dependent plain write (see payment). */
internal suspend fun DataStore<Preferences>.secureSet(
    key: String,
    value: String,
    secureStore: SecureStore
) {
    secureStore.put(key, value)
    edit { it.bumpRevision(key) }
}

/** Suspending remove-and-bump counterpart to [secureSet]. */
internal suspend fun DataStore<Preferences>.secureUnset(
    key: String,
    secureStore: SecureStore
) {
    secureStore.remove(key)
    // Bump (not remove) the marker: a removal must still register as a distinct change so an ALREADY-active
    // secureReadFlow re-emits "" — removing the marker from a null state would be a no-op the collector misses.
    edit { it.bumpRevision(key) }
}

/**
 * Suspending clear of the secure entries for [keys], bumping each revision marker in one `edit`. Used by
 * [SessionPreferencesImpl] so logout/clear scrubs the encrypted credentials + PII it owns atomically,
 * respecting the same preserve/wipe contract as the plain `SESSION_KEYS` clear.
 */
internal suspend fun DataStore<Preferences>.secureClear(
    keys: List<String>,
    secureStore: SecureStore,
    alsoEdit: (MutablePreferences) -> Unit = {}
) {
    keys.forEach { secureStore.remove(it) }
    // Bump each marker so any active secureReadFlow re-emits "" on logout (see secureUnset's note). The
    // session-key removal is folded into this SAME edit so the clear is a single atomic snapshot transition:
    // a separate follow-up edit would produce a second `DataStore.data` emission that conflates the marker
    // bump away from an already-subscribed collector, and the scrubbed "" would never be observed.
    edit { prefs ->
        keys.forEach { prefs.bumpRevision(it) }
        alsoEdit(prefs)
    }
    // `DataStore.data` is a CONFLATED flow: it keeps only the latest value and a parked collector is merely
    // *scheduled* to resume, not run, when the value changes. A clear that returns immediately can win the
    // race and finish (and the caller may stop observing) before the already-subscribed secureReadFlow
    // collector is dispatched to re-read the now-scrubbed value, so the logout "" would be silently dropped.
    // Yield once after committing the bump to hand the dispatcher to that collector, guaranteeing the scrub
    // is delivered. (Cheap; runs only on logout/clear, never on the hot write path.)
    yield()
}

/**
 * Moves a legacy cleartext value for [key] (if any) from the plain DataStore into [secureStore] and
 * deletes the plaintext, exactly once. Idempotent: a no-op once the plaintext key is gone. This is what
 * keeps existing logged-in users signed in across the encryption upgrade while scrubbing the old plaintext.
 * Bumps the revision marker in the same edit so any active [secureReadFlow] re-emits the migrated value.
 */
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

/**
 * Advances [key]'s revision marker to a value distinct from the current one, inside the serialized `edit`
 * so concurrent writers can't collide on the token. Read-modify-write is safe here because DataStore
 * serializes all `edit` blocks for a store.
 */
private fun MutablePreferences.bumpRevision(key: String) {
    val next = (this[revisionKey(key)]?.toLongOrNull() ?: 0L) + 1L
    this[revisionKey(key)] = next.toString()
}
