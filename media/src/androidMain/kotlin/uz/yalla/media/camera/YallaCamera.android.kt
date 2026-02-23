package uz.yalla.media.camera

import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.Executors

private val executor = Executors.newSingleThreadExecutor()

@Composable
actual fun YallaCamera(
    modifier: Modifier,
    cameraMode: CameraMode,
    captureIcon: @Composable (onClick: () -> Unit) -> Unit,
    convertIcon: @Composable (onClick: () -> Unit) -> Unit,
    progressIndicator: @Composable () -> Unit,
    onCapture: (byteArray: ByteArray?) -> Unit,
    onFrame: ((frame: ByteArray) -> Unit)?,
    permissionDeniedContent: @Composable () -> Unit
) {
    val state = rememberYallaCameraState(cameraMode, onFrame, onCapture)
    Box(modifier = modifier) {
        YallaCamera(state = state, modifier = modifier, permissionDeniedContent = permissionDeniedContent)
        CompatOverlay(Modifier.fillMaxSize(), state, captureIcon, convertIcon, progressIndicator)
    }
}

@Composable
private fun CompatOverlay(
    modifier: Modifier,
    state: YallaCameraState,
    captureIcon: @Composable (onClick: () -> Unit) -> Unit,
    convertIcon: @Composable (onClick: () -> Unit) -> Unit,
    progressIndicator: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        captureIcon(state::capture)
        convertIcon(state::toggleCamera)
        if (state.isCapturing) progressIndicator()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun YallaCamera(
    state: YallaCameraState,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit
) {
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    LaunchedEffect(permissionState.status) {
        if (permissionState.status is PermissionStatus.Denied) {
            permissionState.launchPermissionRequest()
        }
    }

    when (permissionState.status) {
        PermissionStatus.Granted -> CameraWithGrantedPermission(state, modifier)
        is PermissionStatus.Denied -> Box(modifier = modifier) { permissionDeniedContent() }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun YallaCamera(
    modifier: Modifier,
    scope: CoroutineScope,
    captureIcon: @Composable (onClick: () -> Unit) -> Unit,
    progressIndicator: @Composable () -> Unit,
    onCapture: (byteArray: ByteArray?) -> Unit,
    permissionDeniedContent: @Composable () -> Unit,
    autoLaunch: Boolean
) {
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)
    var isLaunching by remember { mutableStateOf(false) }
    var hasAutoLaunched by remember { mutableStateOf(false) }

    val launcher =
        rememberSystemCameraLauncher(scope) { bytes ->
            isLaunching = false
            onCapture(bytes)
        }

    LaunchedEffect(permissionState.status, autoLaunch, hasAutoLaunched) {
        if (permissionState.status is PermissionStatus.Denied) {
            permissionState.launchPermissionRequest()
        }
        if (autoLaunch && permissionState.status is PermissionStatus.Granted && !isLaunching && !hasAutoLaunched) {
            isLaunching = true
            hasAutoLaunched = true
            launcher.launch()
        }
    }

    when (permissionState.status) {
        PermissionStatus.Granted ->
            Box(modifier = modifier) {
                captureIcon {
                    if (!isLaunching) {
                        isLaunching = true
                        launcher.launch()
                    }
                }
                if (isLaunching) progressIndicator()
            }
        is PermissionStatus.Denied -> Box(modifier = modifier) { permissionDeniedContent() }
    }
}

@Composable
private fun CameraWithGrantedPermission(
    state: YallaCameraState,
    modifier: Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProvider by loadCameraProvider(context)

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val backgroundExecutor = remember { Executors.newSingleThreadExecutor() }

    val imageAnalyzer = rememberImageAnalyzer(state, backgroundExecutor)
    val cameraSelector = rememberCameraSelector(state.cameraMode)

    DisposableEffect(Unit) {
        onDispose { cameraProvider?.unbindAll() }
    }

    LaunchedEffect(state.cameraMode, cameraProvider, imageAnalyzer) {
        cameraProvider?.let { provider ->
            state.onCameraReady()
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                *listOfNotNull(preview, imageCapture, imageAnalyzer).toTypedArray()
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
        }
    }

    SideEffect {
        state.triggerCaptureAnchor = {
            imageCapture.takePicture(executor, ImageCaptureCallback(state::onCapture, state::stopCapturing))
        }
    }

    DisposableEffect(state) {
        onDispose { state.triggerCaptureAnchor = null }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}

@Composable
private fun rememberImageAnalyzer(
    state: YallaCameraState,
    executor: java.util.concurrent.Executor
) = remember(state.onFrame) {
    state.onFrame?.let { onFrame ->
        ImageAnalysis
            .Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .apply {
                setAnalyzer(executor) { imageProxy ->
                    onFrame(imageProxy.toByteArray())
                }
            }
    }
}

@Composable
private fun rememberCameraSelector(cameraMode: CameraMode) =
    remember(cameraMode) {
        val lensFacing =
            when (cameraMode) {
                CameraMode.Front -> CameraSelector.LENS_FACING_FRONT
                CameraMode.Back -> CameraSelector.LENS_FACING_BACK
            }
        CameraSelector.Builder().requireLensFacing(lensFacing).build()
    }

internal class ImageCaptureCallback(
    private val onCapture: (byteArray: ByteArray?) -> Unit,
    private val stopCapturing: () -> Unit
) : OnImageCapturedCallback() {
    override fun onCaptureSuccess(image: ImageProxy) {
        onCapture(image.toByteArray())
        stopCapturing()
    }
}
