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
                state.status = AppUpdateStatus.Available(storeInfo.second)
            } else {
                state.status = AppUpdateStatus.UpToDate
            }
        } catch (_: Exception) {
            state.status = AppUpdateStatus.UpToDate
        } finally {
            if (state.status is AppUpdateStatus.Checking) {
                state.status = AppUpdateStatus.UpToDate
            }
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
                    cont.resume(parseStoreInfo(json)?.let { it.version to it.storeUrl })
                } catch (_: Exception) {
                    cont.resume(null)
                }
            }
        task.resume()
        cont.invokeOnCancellation { task.cancel() }
    }
