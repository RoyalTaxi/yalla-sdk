package uz.yalla.datastore

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory [SecureStore] for tests — exercises the SecureStore-routing seam (which sensitive keys are
 * read/written/cleared through it) without a device's Keystore/Keychain, which a plain JVM test can't
 * reach. The real crypto round-trip is device-tested separately.
 */
internal class FakeSecureStore : SecureStore {
    private val entries = MutableStateFlow<Map<String, String>>(emptyMap())

    override suspend fun get(key: String): String? = entries.value[key]

    override suspend fun put(
        key: String,
        value: String
    ) {
        entries.value = entries.value + (key to value)
    }

    override suspend fun remove(key: String) {
        entries.value = entries.value - key
    }

    /** Direct, non-suspending peek at the encrypted-side state for assertions. */
    fun peek(key: String): String? = entries.value[key]

    /** Seeds a value as if it were already encrypted at rest (post-migration / prior session). */
    fun seed(
        key: String,
        value: String
    ) {
        entries.value = entries.value + (key to value)
    }
}
