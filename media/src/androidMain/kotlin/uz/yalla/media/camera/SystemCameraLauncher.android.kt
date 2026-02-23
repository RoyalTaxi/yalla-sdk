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

actual class SystemCameraLauncher actual constructor(
    private val onLaunch: () -> Unit
) {
    private var isCameraActive = false

    fun markCameraInactive() {
        isCameraActive = false
    }

    actual fun launch() {
        if (isCameraActive) return
        isCameraActive = true
        onLaunch()
    }
}
