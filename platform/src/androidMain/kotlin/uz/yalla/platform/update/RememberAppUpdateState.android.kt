package uz.yalla.platform.update

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * Android actual for [rememberAppUpdateState].
 *
 * Uses the Play Core [AppUpdateManagerFactory] to check for updates. On success, if
 * [UpdateAvailability.UPDATE_AVAILABLE], sets [AppUpdateState.isUpdateAvailable] to `true`
 * and builds a `market://details?id=` store URL. The [countryCode] parameter is unused
 * on Android (the Play Store handles regional availability automatically).
 */
@Composable
actual fun rememberAppUpdateState(
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
                    state.isUpdateAvailable = true
                    state.storeUrl = "market://details?id=$appId"
                }
                state.isChecking = false
            }.addOnFailureListener {
                state.isChecking = false
            }
    }

    return state
}
