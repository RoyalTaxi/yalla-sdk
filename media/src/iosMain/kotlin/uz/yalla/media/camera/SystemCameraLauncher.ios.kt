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
import uz.yalla.media.utils.toByteArray

@Composable
actual fun rememberSystemCameraLauncher(
    scope: CoroutineScope,
    onResult: (ByteArray?) -> Unit
): SystemCameraLauncher {
    val latestOnResult by rememberUpdatedState(onResult)
    return remember {
        SystemCameraLauncher {
            requireMedia().factory.captureImage { data ->
                scope.launch(Dispatchers.Default) {
                    val bytes = data?.toByteArray()
                    withContext(Dispatchers.Main) { latestOnResult(bytes) }
                }
            }
        }
    }
}
