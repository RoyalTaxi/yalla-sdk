package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

/**
 * Remembers a handle that launches the system camera. [onResult] is delivered on the main thread with
 * the captured image's bytes, or `null` on cancel. The bytes are the camera's raw original (EXIF
 * metadata, including GPS, is preserved) — run them through [uz.yalla.media.util.compressImage]
 * before uploading to strip metadata and bound the size.
 *
 * @param scope a coroutine scope used to read the captured bytes; the result is not lost if it is
 *   cancelled while the camera is still open (the read uses a module-owned scope).
 * @param onResult invoked on the main thread with the captured image's bytes, or `null` on cancel.
 */
@Composable
public expect fun rememberSystemCameraLauncher(
    scope: CoroutineScope,
    onResult: (ByteArray?) -> Unit
): SystemCameraLauncher

/** Handle returned by [rememberSystemCameraLauncher]; call [launch] to open the camera. */
public class SystemCameraLauncher internal constructor(
    private val onLaunch: () -> Unit
) {
    /** Opens the system camera. */
    public fun launch(): Unit = onLaunch()
}
