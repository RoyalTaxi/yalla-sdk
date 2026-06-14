package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

@Composable
public expect fun rememberSystemCameraLauncher(
    scope: CoroutineScope,
    onResult: (ByteArray?) -> Unit
): SystemCameraLauncher

public class SystemCameraLauncher internal constructor(
    private val onLaunch: () -> Unit
) {
    public fun launch(): Unit = onLaunch()
}
