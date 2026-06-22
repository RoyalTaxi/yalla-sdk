package uz.yalla.datastore

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.withContext
import org.koin.core.scope.Scope
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

internal actual fun createSecureStore(scope: Scope): SecureStore = KeychainSecureStore()

@OptIn(ExperimentalForeignApi::class)
private class KeychainSecureStore : SecureStore {
    override suspend fun get(key: String): String? =
        withContext(ioDispatcher) {
            memScoped {
                val result = alloc<CFTypeRefVar>()
                val status =
                    withKeychainQuery(key, kSecReturnData to kCFBooleanTrue) { query ->
                        SecItemCopyMatching(query, result.ptr)
                    }
                if (status == errSecSuccess) {
                    val data = CFBridgingRelease(result.value) as? NSData
                    data?.let { NSString.create(it, NSUTF8StringEncoding) as String? }
                } else {
                    null
                }
            }
        }

    override suspend fun put(
        key: String,
        value: String
    ) {
        withContext(ioDispatcher) {
            val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return@withContext
            val dataRef = CFBridgingRetain(data)
            try {
                val updateStatus =
                    withKeychainQuery(key) { query ->
                        val attributes = cfDictionary(listOf(kSecValueData to dataRef))
                        val status = SecItemUpdate(query, attributes)
                        CFBridgingRelease(attributes)
                        status
                    }
                if (updateStatus == errSecItemNotFound) {
                    withKeychainQuery(
                        key,
                        kSecValueData to dataRef,
                        kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
                    ) { query ->
                        SecItemAdd(query, null)
                    }
                }
            } finally {
                CFBridgingRelease(dataRef)
            }
        }
    }

    override suspend fun remove(key: String) {
        withContext(ioDispatcher) {
            withKeychainQuery(key) { query -> SecItemDelete(query) }
        }
    }

    private fun <R> withKeychainQuery(
        key: String,
        vararg extra: Pair<CFTypeRef?, CFTypeRef?>,
        block: (CFDictionaryRef?) -> R
    ): R {
        val serviceRef = CFBridgingRetain(SERVICE as NSString)
        val accountRef = CFBridgingRetain(key as NSString)
        val query =
            cfDictionary(
                listOf(
                    kSecClass to kSecClassGenericPassword,
                    kSecAttrService to serviceRef,
                    kSecAttrAccount to accountRef,
                    *extra
                )
            )
        try {
            return block(query)
        } finally {
            CFBridgingRelease(query)
            CFBridgingRelease(serviceRef)
            CFBridgingRelease(accountRef)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun cfDictionary(entries: List<Pair<CFTypeRef?, CFTypeRef?>>): CFMutableDictionaryRef? {
    val dictionary =
        CFDictionaryCreateMutable(
            allocator = null,
            capacity = entries.size.toLong(),
            keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
            valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr
        )
    entries.forEach { (key, value) -> CFDictionarySetValue(dictionary, key, value) }
    return dictionary
}

private const val SERVICE = "uz.yalla.datastore.secure"
