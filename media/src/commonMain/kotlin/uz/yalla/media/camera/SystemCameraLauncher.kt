package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

@Composable
expect fun rememberSystemCameraLauncher(
    scope: CoroutineScope,
    onResult: (ByteArray?) -> Unit
): SystemCameraLauncher

class SystemCameraLauncher internal constructor(
    private val onLaunch: () -> Unit
) {
    fun launch() = onLaunch()
}
