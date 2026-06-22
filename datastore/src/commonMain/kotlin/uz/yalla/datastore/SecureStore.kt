package uz.yalla.datastore

import org.koin.core.scope.Scope

internal interface SecureStore {
    suspend fun get(key: String): String?

    suspend fun put(
        key: String,
        value: String
    )

    suspend fun remove(key: String)
}

internal expect fun createSecureStore(scope: Scope): SecureStore
