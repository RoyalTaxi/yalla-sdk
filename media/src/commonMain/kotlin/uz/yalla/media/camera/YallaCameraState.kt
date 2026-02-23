package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Composable
expect fun rememberYallaCameraState(
    initialCameraMode: CameraMode = CameraMode.Back,
    onFrame: ((frame: ByteArray) -> Unit)? = null,
    onCapture: (ByteArray?) -> Unit
): YallaCameraState

@Stable
expect class YallaCameraState {
    var isCameraReady: Boolean
        internal set

    var isCapturing: Boolean
        internal set

    var cameraMode: CameraMode
        internal set

    fun toggleCamera()

    fun capture()
}
