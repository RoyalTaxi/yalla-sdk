package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
actual class YallaCameraState(
    cameraMode: CameraMode,
    internal var onFrame: ((frame: ByteArray) -> Unit)?,
    internal var onCapture: (ByteArray?) -> Unit
) {
    actual var isCameraReady: Boolean by mutableStateOf(false)

    internal var triggerCaptureAnchor: (() -> Unit)? = null

    actual var isCapturing: Boolean by mutableStateOf(false)

    actual var cameraMode: CameraMode by mutableStateOf(cameraMode)

    actual fun toggleCamera() {
        cameraMode = cameraMode.inverse()
    }

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

    fun onCameraReady() {
        isCameraReady = true
    }

    companion object {
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
