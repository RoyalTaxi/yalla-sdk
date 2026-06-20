package uz.yalla.capabilities.update

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
public actual fun rememberAppUpdateState(
    appId: String,
    countryCode: String
): AppUpdateState {
    val state = remember { AppUpdateState() }

    LaunchedEffect(Unit) {
        try {
            val installedVersion =
                (
                    NSBundle.mainBundle.infoDictionary
                        ?.get("CFBundleShortVersionString") as? String
                )?.takeIf { it.isNotBlank() }
            // Fail closed: if the installed version can't be determined, don't drive a
            // (potentially non-dismissible) force-update prompt off an unknown version.
            if (installedVersion == null) return@LaunchedEffect
            val storeInfo = fetchStoreInfo(appId, countryCode)
            if (storeInfo != null && VersionComparator.isNewer(storeInfo.first, installedVersion)) {
                state.isUpdateAvailable = true
                state.storeUrl = storeInfo.second
            }
        } catch (_: Exception) {
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
                    // TODO(quality, needs-decision): H6 — extract a pure parseStoreInfo(json) into
                    // commonMain and fetch via the shared :network Ktor client instead of raw
                    // NSURLSession + NSJSONSerialization. Blocked: requires the owner's call on
                    // adding a :network dependency to :capabilities (layering decision).
                    val json = NSJSONSerialization.JSONObjectWithData(data, 0u, null) as? Map<Any?, *>
                    val results = json?.get("results") as? List<Map<Any?, *>>
                    val first = results?.firstOrNull()
                    val version = first?.get("version") as? String
                    val trackViewUrl = first?.get("trackViewUrl") as? String
                    // Only trust an https store URL on an apple.com host; a MITM/compromised
                    // response must not be able to substitute an arbitrary URL the app opens.
                    if (version != null && isTrustedStoreUrl(trackViewUrl)) {
                        cont.resume(version to trackViewUrl!!)
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

/** True only for an `https` URL whose host is `apple.com` or a subdomain of it. */
private fun isTrustedStoreUrl(url: String?): Boolean {
    val nsUrl = url?.let { NSURL.URLWithString(it) } ?: return false
    if (nsUrl.scheme?.lowercase() != "https") return false
    val host = nsUrl.host?.lowercase() ?: return false
    return host == "apple.com" || host.endsWith(".apple.com")
}
