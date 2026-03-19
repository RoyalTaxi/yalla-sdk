package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope

/**
 * Full-featured camera composable with live preview, capture, camera switching, and frame analysis.
 *
 * Requests camera permission automatically. On Android this uses CameraX; on iOS it uses
 * AVFoundation with a `UIKitView` preview layer. The caller supplies composable slots for
 * the shutter button, camera-switch button, and a progress indicator shown during capture.
 *
 * ## Usage
 *
 * ```kotlin
 * YallaCamera(
 *     modifier = Modifier.fillMaxSize(),
 *     cameraMode = CameraMode.Back,
 *     captureIcon = { onClick -> IconButton(onClick = onClick) { Icon(Icons.Default.Camera, null) } },
 *     onCapture = { bytes -> bytes?.let(::uploadPhoto) },
 * )
 * ```
 *
 * @param modifier Layout modifier for the camera preview.
 * @param cameraMode Initial lens selection. Defaults to [CameraMode.Back].
 * @param captureIcon Composable slot for the shutter button; invoke [onClick] to trigger capture.
 * @param convertIcon Composable slot for the camera-switch button; invoke [onClick] to toggle lens.
 * @param progressIndicator Composable shown while a capture is in progress.
 * @param onCapture Callback with captured JPEG bytes, or `null` on failure.
 * @param onFrame Optional per-frame callback for real-time analysis (e.g., barcode scanning).
 * @param permissionDeniedContent Composable shown when camera permission is denied.
 * @since 0.0.1
 */
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

/**
 * State-driven camera composable for advanced control.
 *
 * Use [rememberYallaCameraState] to create the [state], then call [YallaCameraState.capture]
 * and [YallaCameraState.toggleCamera] programmatically. The composable renders only the live
 * preview; overlay UI (buttons, indicators) is the caller's responsibility.
 *
 * @param state Camera state holder controlling capture and lens switching.
 * @param modifier Layout modifier for the camera preview.
 * @param permissionDeniedContent Composable shown when camera permission is denied.
 * @since 0.0.1
 */
@Composable
expect fun YallaCamera(
    state: YallaCameraState,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit = {}
)

/**
 * System-camera-delegating composable that launches the platform camera app.
 *
 * Instead of embedding a live preview, this variant opens the device's built-in camera
 * application via [SystemCameraLauncher]. Useful when a full preview is not needed or when
 * targeting devices with limited CameraX support.
 *
 * @param modifier Layout modifier for the container.
 * @param scope Coroutine scope for asynchronous camera result handling.
 * @param captureIcon Composable slot for the launch button; invoke [onClick] to open the camera.
 * @param progressIndicator Composable shown while waiting for the camera result.
 * @param onCapture Callback with captured JPEG bytes, or `null` on cancellation.
 * @param permissionDeniedContent Composable shown when camera permission is denied.
 * @param autoLaunch When `true`, the system camera launches automatically on first composition.
 * @since 0.0.1
 */
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
