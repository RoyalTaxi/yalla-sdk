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

// TODO(quality, needs-decision): token/PII at-rest encryption (#4) — the bearer token and PII persist in
// this plain DataStore proto under NSDocumentDirectory (iCloud-backed, CWE-312). The fix (Keychain for
// the tokens + move to NSApplicationSupportDirectory excluded from backup) needs a crypto-strategy choice
// (Keychain/Keystore/Tink) and on-device validation; do not hand-roll. Tracked outside this change.

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
