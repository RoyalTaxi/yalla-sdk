package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

/**
 * Creates and remembers a [SystemCameraLauncher] that delegates to the platform camera app.
 *
 * On Android this uses `ActivityResultContracts.TakePicture`; on iOS it presents a
 * `UIImagePickerController` with camera source.
 *
 * @param scope Coroutine scope for asynchronous result processing.
 * @param onResult Callback with captured JPEG bytes, or `null` on cancellation/failure.
 * @return A remembered [SystemCameraLauncher] instance.
 * @since 0.0.1
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
 *
 * @param onLaunch Platform-specific action executed when [launch] is called.
 * @since 0.0.1
 */
expect class SystemCameraLauncher(onLaunch: () -> Unit) {
    /**
     * Opens the system camera. On Android, guards against double-launch; on iOS, presents
     * the picker immediately.
     *
     * @since 0.0.1
     */
    fun launch()
}
