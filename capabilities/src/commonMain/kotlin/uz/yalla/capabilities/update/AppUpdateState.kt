package uz.yalla.capabilities.update

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.UriHandler

public sealed interface AppUpdateStatus {
    public data object Checking : AppUpdateStatus

    public data object UpToDate : AppUpdateStatus

    public data class Available(
        public val storeUrl: String
    ) : AppUpdateStatus
}

@Stable
public class AppUpdateState {
    public var status: AppUpdateStatus by mutableStateOf(AppUpdateStatus.Checking)
        internal set

    public val isChecking: Boolean
        get() = status is AppUpdateStatus.Checking

    public val isUpdateAvailable: Boolean
        get() = status is AppUpdateStatus.Available

    public val storeUrl: String?
        get() = (status as? AppUpdateStatus.Available)?.storeUrl

    public fun launchUpdate(uriHandler: UriHandler): Boolean {
        val url = storeUrl ?: return false
        uriHandler.openUri(url)
        return true
    }
}
