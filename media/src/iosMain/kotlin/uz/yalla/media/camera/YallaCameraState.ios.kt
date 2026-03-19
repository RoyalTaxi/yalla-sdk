package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * iOS implementation of [YallaCameraState].
 *
 * Wraps AVFoundation capture session lifecycle and provides observable Compose state
 * for camera readiness, capture progress, and active lens.
 *
 * @param cameraMode Initial camera lens.
 * @param onFrame Optional per-frame callback for real-time analysis.
 * @param onCapture Callback with captured JPEG bytes or `null`.
 * @since 0.0.1
 */
@Stable
actual class YallaCameraState(
    cameraMode: CameraMode,
    internal var onFrame: ((frame: ByteArray) -> Unit)?,
    internal var onCapture: (ByteArray?) -> Unit
) {
    /** @since 0.0.1 */
    actual var isCameraReady: Boolean by mutableStateOf(false)

    internal var triggerCaptureAnchor: (() -> Unit)? = null

    /** @since 0.0.1 */
    actual var isCapturing: Boolean by mutableStateOf(false)

    /** @since 0.0.1 */
    actual var cameraMode: CameraMode by mutableStateOf(cameraMode)

    /** @since 0.0.1 */
    actual fun toggleCamera() {
        cameraMode = cameraMode.inverse()
    }

    /** @since 0.0.1 */
    actual fun capture() {
        isCapturing = true
        triggerCaptureAnchor?.invoke()
    }

    internal fun stopCapturing() {
        isCapturing = false
    }

    internal fun onCapture(image: ByteArray?) {
        onCapture.invoke(image)
    }

    /**
     * Marks the camera session as ready and the preview as visible.
     *
     * Called internally once the AVCaptureSession is running.
     *
     * @since 0.0.1
     */
    fun onCameraReady() {
        isCameraReady = true
    }

    companion object {
        /**
         * Creates a [Saver] for [YallaCameraState] that persists the active [CameraMode].
         *
         * @param onFrame Per-frame callback restored on recreation.
         * @param onCapture Capture callback restored on recreation.
         * @return Saver mapping state to an integer camera-mode ID.
         * @since 0.0.1
         */
        fun saver(
            onFrame: ((frame: ByteArray) -> Unit)?,
            onCapture: (ByteArray?) -> Unit
        ): Saver<YallaCameraState, Int> =
            Saver(
                save = {
                    it.cameraMode.toId()
                },
                restore = {
                    YallaCameraState(
                        cameraMode = cameraModeFromId(it),
                        onFrame = onFrame,
                        onCapture = onCapture
                    )
                }
            )
    }
}

/** iOS implementation of [rememberYallaCameraState]. @since 0.0.1 */
@Composable
actual fun rememberYallaCameraState(
    initialCameraMode: CameraMode,
    onFrame: ((frame: ByteArray) -> Unit)?,
    onCapture: (ByteArray?) -> Unit
): YallaCameraState =
    rememberSaveable(
        saver = YallaCameraState.saver(onFrame, onCapture)
    ) { YallaCameraState(initialCameraMode, onFrame, onCapture) }.apply {
        this.onFrame = onFrame
        this.onCapture = onCapture
    }
