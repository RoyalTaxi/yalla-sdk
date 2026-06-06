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

@Composable
actual fun rememberSystemCameraLauncher(
    scope: CoroutineScope,
    onResult: (ByteArray?) -> Unit
): SystemCameraLauncher {
    val context = LocalContext.current
    val latestOnResult by rememberUpdatedState(onResult)
    return remember {
        SystemCameraLauncher {
            requireMedia().factory.captureImage { uri ->
                scope.launch(Dispatchers.IO) {
                    val bytes = uri?.let {
                        context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() }
                    }
                    withContext(Dispatchers.Main) { latestOnResult(bytes) }
                }
            }
        }
    }
}
