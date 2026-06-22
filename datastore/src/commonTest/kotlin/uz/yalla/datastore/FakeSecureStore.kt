package uz.yalla.datastore

import kotlinx.coroutines.flow.MutableStateFlow

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

    fun peek(key: String): String? = entries.value[key]

    fun seed(
        key: String,
        value: String
    ) {
        entries.value = entries.value + (key to value)
    }
}
