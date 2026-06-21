package uz.yalla.capabilities.update

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.UriHandler

/** Result of an app-update check. */
public sealed interface AppUpdateStatus {
    /** The platform update check is still in flight. */
    public data object Checking : AppUpdateStatus

    /** No newer version was found, or the check failed closed. */
    public data object UpToDate : AppUpdateStatus

    /** A newer version exists and can be opened at [storeUrl]. */
    public data class Available(
        public val storeUrl: String
    ) : AppUpdateStatus
}

/**
 * Observable result of an app-update check. Produced by [rememberAppUpdateState].
 */
@Stable
public class AppUpdateState {
    /** Current check status. */
    public var status: AppUpdateStatus by mutableStateOf(AppUpdateStatus.Checking)
        internal set

    /** `true` while the update check is still in flight. */
    public val isChecking: Boolean
        get() = status is AppUpdateStatus.Checking

    /** `true` when a newer version is available. */
    public val isUpdateAvailable: Boolean
        get() = status is AppUpdateStatus.Available

    /** Store URL to open the update, or `null` when no update is available. */
    public val storeUrl: String?
        get() = (status as? AppUpdateStatus.Available)?.storeUrl

    /** Opens the app store for [AppUpdateStatus.Available]; returns whether a launch happened. */
    public fun launchUpdate(uriHandler: UriHandler): Boolean {
        val url = storeUrl ?: return false
        uriHandler.openUri(url)
        return true
    }
}
