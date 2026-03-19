package uz.yalla.platform.update

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Observable state holder for in-app update checks.
 *
 * Created via [rememberAppUpdateState] and populated asynchronously by the
 * platform-specific update checker. Properties are backed by Compose
 * [mutableStateOf] so UI recomposes automatically when state changes.
 *
 * ## Usage
 * ```kotlin
 * val updateState = rememberAppUpdateState(appId = "uz.yalla.client")
 *
 * if (updateState.isUpdateAvailable) {
 *     UpdateBanner(storeUrl = updateState.storeUrl)
 * }
 * ```
 *
 * @since 0.0.1
 */
@Stable
class AppUpdateState {
    /** Whether a newer version is available in the store. */
    var isUpdateAvailable by mutableStateOf(false)
        internal set

    /** Direct store URL for opening the update page. */
    var storeUrl by mutableStateOf("")
        internal set

    /** Whether the version check is still in progress. */
    var isChecking by mutableStateOf(true)
        internal set
}
