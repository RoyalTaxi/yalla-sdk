package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope

@Composable
expect fun YallaCamera(
    modifier: Modifier,
    cameraMode: CameraMode = CameraMode.Back,
    captureIcon: @Composable (onClick: () -> Unit) -> Unit,
    convertIcon: @Composable (onClick: () -> Unit) -> Unit = {},
    progressIndicator: @Composable () -> Unit = {},
    onCapture: (byteArray: ByteArray?) -> Unit,
    onFrame: ((frame: ByteArray) -> Unit)? = null,
    permissionDeniedContent: @Composable () -> Unit = {}
)

@Composable
expect fun YallaCamera(
    state: YallaCameraState,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit = {}
)

@Composable
expect fun YallaCamera(
    modifier: Modifier,
    scope: CoroutineScope,
    captureIcon: @Composable (onClick: () -> Unit) -> Unit,
    progressIndicator: @Composable () -> Unit = {},
    onCapture: (byteArray: ByteArray?) -> Unit,
    permissionDeniedContent: @Composable () -> Unit = {},
    autoLaunch: Boolean = false
)
