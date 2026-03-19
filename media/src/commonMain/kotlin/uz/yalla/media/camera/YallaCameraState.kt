package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

/**
 * Creates and remembers a [YallaCameraState] that survives configuration changes.
 *
 * @param initialCameraMode Lens to activate on first composition. Defaults to [CameraMode.Back].
 * @param onFrame Optional per-frame callback for real-time analysis.
 * @param onCapture Callback invoked with captured JPEG bytes, or `null` on failure.
 * @return A saveable [YallaCameraState] instance.
 * @since 0.0.1
 */
@Composable
expect fun rememberYallaCameraState(
    initialCameraMode: CameraMode = CameraMode.Back,
    onFrame: ((frame: ByteArray) -> Unit)? = null,
    onCapture: (ByteArray?) -> Unit
): YallaCameraState

/**
 * Observable state holder for [YallaCamera].
 *
 * Exposes the current camera readiness, capture-in-progress flag, and active lens.
 * Call [capture] to take a photo and [toggleCamera] to switch between front and back lenses.
 *
 * ## Usage
 *
 * ```kotlin
 * val cameraState = rememberYallaCameraState(
 *     onCapture = { bytes -> bytes?.let(::upload) },
 * )
 *
 * YallaCamera(state = cameraState, modifier = Modifier.fillMaxSize())
 *
 * Button(onClick = { cameraState.capture() }) { Text("Snap") }
 * Button(onClick = { cameraState.toggleCamera() }) { Text("Flip") }
 * ```
 *
 * @since 0.0.1
 */
@Stable
expect class YallaCameraState {
    /** `true` once the camera session is running and the preview is visible. @since 0.0.1 */
    var isCameraReady: Boolean
        internal set

    /** `true` while a capture operation is in progress. @since 0.0.1 */
    var isCapturing: Boolean
        internal set

    /** Currently active camera lens. @since 0.0.1 */
    var cameraMode: CameraMode
        internal set

    /**
     * Switches between [CameraMode.Front] and [CameraMode.Back].
     *
     * @since 0.0.1
     */
    fun toggleCamera()

    /**
     * Initiates a photo capture. The result is delivered via the `onCapture` callback
     * provided to [rememberYallaCameraState].
     *
     * @since 0.0.1
     */
    fun capture()
}
