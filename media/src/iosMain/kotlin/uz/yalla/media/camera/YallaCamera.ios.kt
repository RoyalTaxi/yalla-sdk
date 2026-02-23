package uz.yalla.media.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import platform.AVFoundation.AVCaptureDevicePositionFront
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoSettings
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoDataOutput
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVVideoCodecKey
import platform.AVFoundation.AVVideoCodecTypeJPEG
import platform.AVFoundation.position
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIColor
import platform.UIKit.UIView

@Composable
actual fun YallaCamera(
    state: YallaCameraState,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit
) {
    var cameraAccess: CameraAccess by remember { mutableStateOf(CameraAccess.Undefined) }

    LaunchedEffect(Unit) {
        checkCameraPermission { cameraAccess = it }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        when (cameraAccess) {
            CameraAccess.Undefined -> {}
            CameraAccess.Denied -> Box(modifier = modifier) { permissionDeniedContent() }
            CameraAccess.Authorized -> {
                discoverCamera(state.cameraMode)?.let { camera ->
                    RealDeviceCamera(state, camera, Modifier.fillMaxSize())
                } ?: CameraUnavailableMessage()

                if (!state.isCameraReady) {
                    Box(Modifier.fillMaxSize().background(Color.Black))
                }
            }
        }
    }
}

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
actual fun YallaCamera(
    modifier: Modifier,
    scope: CoroutineScope,
    captureIcon: @Composable (onClick: () -> Unit) -> Unit,
    progressIndicator: @Composable () -> Unit,
    onCapture: (byteArray: ByteArray?) -> Unit,
    permissionDeniedContent: @Composable () -> Unit,
    autoLaunch: Boolean
) {
    var cameraAccess: CameraAccess by remember { mutableStateOf(CameraAccess.Undefined) }
    var isLaunching by remember { mutableStateOf(false) }
    var hasAutoLaunched by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        checkCameraPermission { cameraAccess = it }
    }

    val launcher =
        rememberSystemCameraLauncher(scope) { bytes ->
            isLaunching = false
            onCapture(bytes)
        }

    LaunchedEffect(cameraAccess, autoLaunch, hasAutoLaunched) {
        if (cameraAccess == CameraAccess.Authorized && autoLaunch && !isLaunching && !hasAutoLaunched) {
            isLaunching = true
            hasAutoLaunched = true
            launcher.launch()
        }
    }

    Box(modifier = modifier) {
        when (cameraAccess) {
            CameraAccess.Undefined -> {}
            CameraAccess.Denied -> Box(modifier = modifier) { permissionDeniedContent() }
            CameraAccess.Authorized -> {
                captureIcon {
                    if (!isLaunching) {
                        isLaunching = true
                        launcher.launch()
                    }
                }
                if (isLaunching) progressIndicator()
            }
        }
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

@Composable
private fun CameraUnavailableMessage() {
    Text("Camera is not available on simulator. Please try to run on a real iOS device.", color = Color.White)
}

@OptIn(ExperimentalForeignApi::class)
@Composable
private fun RealDeviceCamera(
    state: YallaCameraState,
    camera: platform.AVFoundation.AVCaptureDevice,
    modifier: Modifier
) {
    val capturePhotoOutput = remember { AVCapturePhotoOutput() }
    val videoOutput =
        remember(state.onFrame) {
            if (state.onFrame != null) AVCaptureVideoDataOutput() else null
        }
    val photoCaptureDelegate = remember(state) { PhotoCaptureDelegate(state::stopCapturing, state::onCapture) }
    val frameAnalyzerDelegate =
        remember(state.onFrame) {
            state.onFrame?.let { CameraFrameAnalyzerDelegate(it) }
        }

    val captureSession =
        remember(camera, state.onFrame) {
            createCaptureSession(camera, capturePhotoOutput, videoOutput, frameAnalyzerDelegate)
        }

    SideEffect {
        state.triggerCaptureAnchor = {
            capturePhoto(capturePhotoOutput, camera, photoCaptureDelegate)
        }
    }

    LaunchedEffect(captureSession) {
        startSession(captureSession) {
            state.onCameraReady()
        }
    }

    DisposableEffect(captureSession) {
        onDispose {
            stopSession(captureSession)
            state.isCameraReady = false
        }
    }

    LaunchedEffect(state.cameraMode) {
        if (state.isCameraReady) {
            switchCameraInput(captureSession, state.cameraMode, state::onCameraReady)
        }
    }

    SetupCameraView(
        modifier = modifier,
        session = captureSession,
        state = state,
        photoOutput = capturePhotoOutput,
        videoOutput = videoOutput
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun capturePhoto(
    photoOutput: AVCapturePhotoOutput,
    camera: platform.AVFoundation.AVCaptureDevice,
    delegate: PhotoCaptureDelegate
) {
    val photoSettings =
        AVCapturePhotoSettings.photoSettingsWithFormat(
            format = mapOf(pair = AVVideoCodecKey to AVVideoCodecTypeJPEG)
        )

    if (camera.position == AVCaptureDevicePositionFront) {
        photoOutput.connectionWithMediaType(AVMediaTypeVideo)?.apply {
            automaticallyAdjustsVideoMirroring = false
            videoMirrored = true
        }
    }

    photoOutput.capturePhotoWithSettings(photoSettings, delegate)
}

@OptIn(ExperimentalForeignApi::class)
@Composable
private fun SetupCameraView(
    modifier: Modifier,
    session: AVCaptureSession,
    state: YallaCameraState,
    photoOutput: AVCapturePhotoOutput,
    videoOutput: AVCaptureVideoDataOutput?
) {
    // Remember orientation listener to avoid recreating it
    val orientationListener =
        remember(photoOutput, videoOutput) {
            mutableStateOf<OrientationListener?>(null)
        }

    UIKitView(
        modifier = modifier.background(Color.Black),
        factory = {
            val container = UIView()
            container.backgroundColor = UIColor.blackColor
            container.opaque = true // Performance optimization
            container.userInteractionEnabled = false // Allow Compose overlay to handle touches
            container
        },
        update = { view ->
            // Only create preview layer when session is confirmed running
            if (state.isCameraReady && session.running) {
                val existingLayer = view.layer.sublayers?.firstOrNull() as? AVCaptureVideoPreviewLayer

                if (existingLayer == null) {
                    val previewLayer = AVCaptureVideoPreviewLayer(session = session)
                    previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
                    previewLayer.setFrame(view.bounds)
                    view.layer.addSublayer(previewLayer)

                    // Set up orientation listener only once if connection exists
                    previewLayer.connection?.let { connection ->
                        if (orientationListener.value == null && videoOutput != null) {
                            val listener = OrientationListener(previewLayer, photoOutput, videoOutput)
                            orientationListener.value = listener

                            val notificationName = platform.UIKit.UIDeviceOrientationDidChangeNotification
                            NSNotificationCenter.defaultCenter.addObserver(
                                observer = listener,
                                selector = NSSelectorFromString(OrientationListener::orientationDidChange.name + ":"),
                                name = notificationName,
                                `object` = null
                            )
                        }
                    }
                } else {
                    // Update frame on every recomposition to handle size changes
                    existingLayer.setFrame(view.bounds)
                }
            }
        }
    )
}
