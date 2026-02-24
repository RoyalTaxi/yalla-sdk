package uz.yalla.platform.update

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.dataTaskWithURL
import kotlin.coroutines.resume

@Composable
actual fun rememberAppUpdateState(
    appId: String,
    countryCode: String
): AppUpdateState {
    val state = remember { AppUpdateState() }

    LaunchedEffect(Unit) {
        try {
            val storeInfo = fetchStoreInfo(appId, countryCode)
            if (storeInfo != null) {
                val installedVersion =
                    NSBundle.mainBundle.infoDictionary
                        ?.get("CFBundleShortVersionString") as? String ?: ""
                if (VersionComparator.isNewer(storeInfo.first, installedVersion)) {
                    state.isUpdateAvailable = true
                    state.storeUrl = storeInfo.second
                }
            }
        } catch (_: Exception) {
            // Silently fail - don't block user if version check fails
        } finally {
            state.isChecking = false
        }
    }

    return state
}

@OptIn(ExperimentalForeignApi::class)
@Suppress("UNCHECKED_CAST")
private suspend fun fetchStoreInfo(
    bundleId: String,
    countryCode: String
): Pair<String, String>? =
    suspendCancellableCoroutine { cont ->
        val urlString = "https://itunes.apple.com/lookup?bundleId=$bundleId&country=$countryCode"
        val url = NSURL(string = urlString)

        val task =
            NSURLSession.sharedSession.dataTaskWithURL(url) { data: NSData?, _, error ->
                if (error != null || data == null) {
                    cont.resume(null)
                    return@dataTaskWithURL
                }
                try {
                    val json = NSJSONSerialization.JSONObjectWithData(data, 0u, null) as? Map<Any?, *>
                    val results = json?.get("results") as? List<Map<Any?, *>>
                    val first = results?.firstOrNull()
                    val version = first?.get("version") as? String
                    val trackViewUrl = first?.get("trackViewUrl") as? String
                    if (version != null && trackViewUrl != null) {
                        cont.resume(version to trackViewUrl)
                    } else {
                        cont.resume(null)
                    }
                } catch (_: Exception) {
                    cont.resume(null)
                }
            }
        task.resume()
        cont.invokeOnCancellation { task.cancel() }
    }
