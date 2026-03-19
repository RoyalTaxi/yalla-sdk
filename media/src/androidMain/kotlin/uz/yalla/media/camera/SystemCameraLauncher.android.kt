package uz.yalla.media.camera

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/** Android implementation of [rememberSystemCameraLauncher]. @since 0.0.1 */
@Composable
actual fun rememberSystemCameraLauncher(
    scope: CoroutineScope,
    onResult: (ByteArray?) -> Unit
): SystemCameraLauncher {
    val context = LocalContext.current
    val latestOnResult by rememberUpdatedState(onResult)
    var currentUri by remember { mutableStateOf<Uri?>(null) }
    var launcher: SystemCameraLauncher? = null

    val activityLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                val uri = currentUri
                scope.launch(Dispatchers.IO) {
                    val bytes =
                        if (success && uri != null) {
                            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        } else {
                            null
                        }
                    withContext(Dispatchers.Main) {
                        latestOnResult(bytes)
                    }
                }
                launcher?.markCameraInactive()
            }
        )

    launcher =
        remember {
            SystemCameraLauncher {
                currentUri = createCameraImageUri(context)
                val uri = currentUri
                if (uri == null) {
                    latestOnResult(null)
                    launcher?.markCameraInactive()
                } else {
                    activityLauncher.launch(uri)
                }
            }
        }

    return launcher
}

private fun createCameraImageUri(context: Context): Uri? {
    val imagesDir = File(context.filesDir, "share_images").apply { mkdirs() }
    val imageFile = File.createTempFile("camera_", ".jpg", imagesDir)
    return FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)
}

/**
 * Android implementation of [SystemCameraLauncher].
 *
 * Guards against double-launch by tracking an internal active flag.
 *
 * @param onLaunch Action that creates a temp URI and launches `TakePicture` contract.
 * @since 0.0.1
 */
actual class SystemCameraLauncher actual constructor(private val onLaunch: () -> Unit) {
    private var isCameraActive = false

    /**
     * Resets the active flag so the launcher can be used again.
     *
     * Called automatically after the camera result is received.
     *
     * @since 0.0.1
     */
    fun markCameraInactive() {
        isCameraActive = false
    }

    /** @since 0.0.1 */
    actual fun launch() {
        if (isCameraActive) return
        isCameraActive = true
        onLaunch()
    }
}
