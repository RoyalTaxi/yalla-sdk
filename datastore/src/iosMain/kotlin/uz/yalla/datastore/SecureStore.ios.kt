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

/**
 * [SecureStore] backed by the iOS **Keychain** (`platform.Security`). Each value is a generic-password item
 * under a fixed service + the key as the account, with accessibility
 * [kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly] — readable after the first unlock, kept on THIS device
 * only, and excluded from iCloud and encrypted iTunes/Finder backups.
 *
 * This is the core CWE-312 fix: it replaces the old plaintext-in-`NSDocumentDirectory` storage (which was
 * backed up) for the tokens + PII. The Keychain is hardware-encrypted (Secure Enclave); no key material
 * ever reaches this code.
 *
 * CF memory: the `kSec*` constants are immortal singletons (never released); the service/account strings
 * and the value data are bridged with [CFBridgingRetain] and released with [CFBridgingRelease] once the
 * Keychain call (which copies what it needs) returns — see [withKeychainQuery].
 */
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
                    // CFBridgingRelease takes ownership of the +1 returned-data ref and yields an NSData.
                    val data = CFBridgingRelease(result.value) as? NSData
                    data?.let { NSString.create(it, NSUTF8StringEncoding) as String? }
                } else {
                    null // errSecItemNotFound and any read failure → treated as absent.
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
                // Update the existing item in place (preserving its accessibility), else add a fresh one.
                val updateStatus =
                    withKeychainQuery(key) { query ->
                        val attributes = cfDictionary(listOf(kSecValueData to dataRef))
                        val status = SecItemUpdate(query, attributes)
                        CFBridgingRelease(attributes) // Balances the +1 from CFDictionaryCreateMutable.
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

    /**
     * Runs [block] with a Keychain query dictionary identifying this store's item for [key] (generic
     * password, fixed service, key as account) plus any [extra] entries, releasing the dictionary and the
     * service/account string refs it owns afterwards. The `kSec*` keys and singleton values are not owned.
     */
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
            CFBridgingRelease(query) // Balances the +1 from CFDictionaryCreateMutable.
            CFBridgingRelease(serviceRef)
            CFBridgingRelease(accountRef)
        }
    }
}

/**
 * Builds a `CFMutableDictionaryRef` from [entries] with the CoreFoundation type callbacks, so the
 * dictionary retains its own copy of each key/value (caller can release its refs once the dict exists).
 * The caller owns the returned dictionary (a +1 from "Create") and releases it via `CFBridgingRelease`.
 */
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
