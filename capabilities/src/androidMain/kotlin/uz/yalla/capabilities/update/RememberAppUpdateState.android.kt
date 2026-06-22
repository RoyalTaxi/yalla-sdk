package uz.yalla.capabilities.update

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability

@Composable
public actual fun rememberAppUpdateState(
    appId: String,
    countryCode: String
): AppUpdateState {
    val context = LocalContext.current
    val state = remember { AppUpdateState() }

    LaunchedEffect(Unit) {
        val appUpdateManager = AppUpdateManagerFactory.create(context)
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    state.status = AppUpdateStatus.Available("market://details?id=$appId")
                } else {
                    state.status = AppUpdateStatus.UpToDate
                }
            }.addOnFailureListener {
                state.status = AppUpdateStatus.UpToDate
            }
    }

    return state
}
