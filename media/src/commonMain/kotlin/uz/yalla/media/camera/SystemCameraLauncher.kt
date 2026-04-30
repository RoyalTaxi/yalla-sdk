package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

/**
 * Creates and remembers a [SystemCameraLauncher] that delegates to the platform camera app.
 *
 * On Android this uses `ActivityResultContracts.TakePicture`; on iOS it presents a
 * `UIImagePickerController` with camera source.
 *
 * @return A remembered [SystemCameraLauncher] instance.
 */
@Composable
expect fun rememberSystemCameraLauncher(
    scope: CoroutineScope,
    onResult: (ByteArray?) -> Unit
): SystemCameraLauncher

/**
 * Launcher that opens the platform's built-in camera application.
 *
 * Obtain an instance via [rememberSystemCameraLauncher]. Call [launch] to open the camera;
 * the result is delivered through the callback provided at creation time.
 */
expect class SystemCameraLauncher(onLaunch: () -> Unit) {
    /**
     * Opens the system camera. On Android, guards against double-launch; on iOS, presents
     * the picker immediately.
     */
    fun launch()
}
