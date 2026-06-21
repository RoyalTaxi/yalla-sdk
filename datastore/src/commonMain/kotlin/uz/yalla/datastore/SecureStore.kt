package uz.yalla.datastore

import org.koin.core.scope.Scope

/**
 * Platform-backed at-rest encrypted store for the SDK's sensitive string values — the auth/refresh
 * tokens and profile/payment PII (CWE-312). It is the secure counterpart to the plain
 * [androidx.datastore.core.DataStore] used for non-sensitive UX prefs (locale, theme, positions).
 *
 * The backing keystore never exposes raw key material to this code:
 * - **Android:** AES-256-GCM with a key held in the Android Keystore (TEE/StrongBox); only
 *   `Base64(IV || ciphertext)` lands on disk.
 * - **iOS:** the Keychain with `kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly`, so values are
 *   device-only and excluded from iCloud/iTunes backups (the core fix vs. the old
 *   `NSDocumentDirectory` plaintext).
 *
 * Implementations are suspending because both platforms do blocking IO/crypto; callers bridge these
 * into the existing reactive surface (see `SecurePreferenceAccessors`). All methods are keyed by the
 * same logical name as the matching [PreferenceKeys] entry, so migration and clears stay 1:1.
 */
internal interface SecureStore {
    /** Returns the decrypted value for [key], or `null` when absent (or undecryptable — see actuals). */
    suspend fun get(key: String): String?

    /** Encrypts and persists [value] under [key], replacing any existing entry. */
    suspend fun put(
        key: String,
        value: String
    )

    /** Removes the entry for [key]; a no-op when absent. */
    suspend fun remove(key: String)
}

/**
 * Builds the single shared [SecureStore], resolving any platform inputs it needs from [scope] (Android
 * pulls `Context`; iOS needs nothing). Mirrors `createDataStore` so the two stores are wired the same way.
 */
internal expect fun createSecureStore(scope: Scope): SecureStore
