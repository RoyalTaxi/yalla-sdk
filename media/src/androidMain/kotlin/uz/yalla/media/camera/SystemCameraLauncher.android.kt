package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.yalla.media.config.requireMedia
import uz.yalla.media.utils.MediaScope

@Composable
public actual fun rememberSystemCameraLauncher(
    @Suppress("UNUSED_PARAMETER") scope: CoroutineScope,
    onResult: (ByteArray?) -> Unit
): SystemCameraLauncher {
    val context = LocalContext.current
    val latestOnResult by rememberUpdatedState(onResult)
    return remember {
        SystemCameraLauncher {
            requireMedia().factory.captureImage { uri ->
                // Read on the module-owned scope, not the caller's: the camera can outlive the
                // composition scope, and a cancelled caller scope must not drop the captured image.
                MediaScope.launch(Dispatchers.IO) {
                    val bytes =
                        uri?.let {
                            context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() }
                        }
                    withContext(Dispatchers.Main) { latestOnResult(bytes) }
                }
            }
        }
    }
}
