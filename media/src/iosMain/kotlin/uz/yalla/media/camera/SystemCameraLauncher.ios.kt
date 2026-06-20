package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.yalla.media.config.requireMedia
import uz.yalla.media.utils.MediaScope
import uz.yalla.media.utils.toByteArray

@Composable
public actual fun rememberSystemCameraLauncher(
    @Suppress("UNUSED_PARAMETER") scope: CoroutineScope,
    onResult: (ByteArray?) -> Unit
): SystemCameraLauncher {
    val latestOnResult by rememberUpdatedState(onResult)
    return remember {
        SystemCameraLauncher {
            requireMedia().factory.captureImage { data ->
                // Read on the module-owned scope, not the caller's: the camera can outlive the
                // composition scope, and a cancelled caller scope must not drop the captured image.
                // MediaScope is backed by Dispatchers.Default (Dispatchers.IO is JVM-only / absent on
                // Kotlin/Native), which is the off-main background dispatcher for the byte read.
                MediaScope.launch {
                    val bytes = data?.toByteArray()
                    withContext(Dispatchers.Main) { latestOnResult(bytes) }
                }
            }
        }
    }
}
