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
import uz.yalla.media.util.MediaScope
import uz.yalla.media.util.toByteArray

@Composable
public actual fun rememberSystemCameraLauncher(
    @Suppress("UNUSED_PARAMETER") scope: CoroutineScope,
    onResult: (ByteArray?) -> Unit
): SystemCameraLauncher {
    val latestOnResult by rememberUpdatedState(onResult)
    return remember {
        SystemCameraLauncher {
            requireMedia().factory.captureImage { data ->
                MediaScope.launch {
                    val bytes = data?.toByteArray()
                    withContext(Dispatchers.Main) { latestOnResult(bytes) }
                }
            }
        }
    }
}
