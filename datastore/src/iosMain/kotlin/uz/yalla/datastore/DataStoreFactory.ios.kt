package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import okio.Path.Companion.toPath
import org.koin.core.scope.Scope
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

// At-rest encryption (CWE-312, finding #4) is handled by SecureStore: the bearer/push tokens and the
// profile/payment PII go to the Keychain (SecureStore.ios.kt) under
// kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly — device-only and excluded from iCloud/iTunes backups.
// This plain DataStore proto under NSDocumentDirectory now holds ONLY non-sensitive UX prefs (locale,
// theme, map style, onboarding, last positions), the cash/card discriminator, cached config, and the
// per-key revision markers — none of which are credentials or PII, so backup exposure here is benign.

@OptIn(ExperimentalForeignApi::class)
internal actual fun createDataStore(scope: Scope): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = {
            val documentDirectory =
                memScoped {
                    val errorVar = alloc<ObjCObjectVar<NSError?>>()
                    val url =
                        NSFileManager.defaultManager.URLForDirectory(
                            directory = NSDocumentDirectory,
                            inDomain = NSUserDomainMask,
                            appropriateForURL = null,
                            create = false,
                            error = errorVar.ptr
                        )
                    requireNotNull(url) {
                        "DataStore documents dir unavailable: ${errorVar.value?.localizedDescription}"
                    }
                }
            (documentDirectory.path + "/$DATASTORE_FILE").toPath()
        }
    )
