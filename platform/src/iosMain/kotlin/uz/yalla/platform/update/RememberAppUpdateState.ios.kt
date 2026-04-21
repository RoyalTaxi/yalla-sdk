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

/**
 * iOS actual for [rememberAppUpdateState].
 *
 * Queries the iTunes Search API (`itunes.apple.com/lookup`) for the latest version
 * of the app identified by [appId] in the given [countryCode] region. Compares the
 * store version against the installed version from `CFBundleShortVersionString` using
 * [VersionComparator]. On success, sets [AppUpdateState.storeUrl] to the App Store
 * track URL for direct linking.
 *
 * Failures are silently swallowed to avoid blocking the user.
 */
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

/**
 * Fetches the latest version and store URL from the iTunes Search API.
 *
 * @param bundleId The iOS bundle identifier to look up.
 * @param countryCode ISO 3166-1 alpha-2 country code for the store region.
 * @return A [Pair] of (version, trackViewUrl), or `null` if the lookup fails or yields no results.
 */
@OptIn(ExperimentalForeignApi::class)
// NSJSONSerialization returns an erased Map/List tree; body narrows each node via `as?` before use.
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
