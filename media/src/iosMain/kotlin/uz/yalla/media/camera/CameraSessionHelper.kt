package uz.yalla.media.camera

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceDiscoverySession.Companion.discoverySessionWithDeviceTypes
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDevicePositionFront
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInDualCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInDualWideCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInDuoCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInUltraWideCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVCaptureInput
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetHigh
import platform.AVFoundation.AVCaptureSessionPresetPhoto
import platform.AVFoundation.AVCaptureVideoDataOutput
import platform.AVFoundation.AVMediaTypeVideo
import platform.CoreMedia.kCMPixelFormat_32BGRA
import platform.CoreVideo.kCVPixelBufferPixelFormatTypeKey
import platform.darwin.DISPATCH_QUEUE_PRIORITY_HIGH
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_group_create
import platform.darwin.dispatch_group_enter
import platform.darwin.dispatch_group_leave
import platform.darwin.dispatch_group_notify
import platform.darwin.dispatch_queue_create

private val deviceTypes =
    listOf(
        AVCaptureDeviceTypeBuiltInWideAngleCamera,
        AVCaptureDeviceTypeBuiltInDualWideCamera,
        AVCaptureDeviceTypeBuiltInDualCamera,
        AVCaptureDeviceTypeBuiltInUltraWideCamera,
        AVCaptureDeviceTypeBuiltInDuoCamera
    )

@OptIn(ExperimentalForeignApi::class)
internal fun discoverCamera(cameraMode: CameraMode): AVCaptureDevice? {
    val position =
        when (cameraMode) {
            CameraMode.Front -> AVCaptureDevicePositionFront
            CameraMode.Back -> AVCaptureDevicePositionBack
        }

    return discoverySessionWithDeviceTypes(
        deviceTypes,
        mediaType = AVMediaTypeVideo,
        position = position
    ).devices.firstOrNull() as? AVCaptureDevice
}

@OptIn(ExperimentalForeignApi::class)
internal fun createCaptureSession(
    camera: AVCaptureDevice,
    photoOutput: AVCapturePhotoOutput,
    videoOutput: AVCaptureVideoDataOutput?,
    frameAnalyzer: CameraFrameAnalyzerDelegate?
): AVCaptureSession {
    val session = AVCaptureSession()

    session.beginConfiguration()

    // Use high-resolution preset for better quality
    if (session.canSetSessionPreset(AVCaptureSessionPresetHigh)) {
        session.sessionPreset = AVCaptureSessionPresetHigh
    } else {
        session.sessionPreset = AVCaptureSessionPresetPhoto
    }

    val input = AVCaptureDeviceInput.deviceInputWithDevice(camera, error = null)
    if (input != null && session.canAddInput(input)) {
        session.addInput(input)
    }

    // Configure photo output
    if (session.canAddOutput(photoOutput)) {
        session.addOutput(photoOutput)
    }

    // Only add video output if frame analysis is needed
    if (videoOutput != null && frameAnalyzer != null && session.canAddOutput(videoOutput)) {
        // Use dedicated queue for frame processing
        val queue = dispatch_queue_create("uz.yalla.camera.frames", null)

        videoOutput.setSampleBufferDelegate(frameAnalyzer, queue)
        videoOutput.alwaysDiscardsLateVideoFrames = true

        @Suppress("UNCHECKED_CAST")
        videoOutput.videoSettings =
            mapOf(
                kCVPixelBufferPixelFormatTypeKey to kCMPixelFormat_32BGRA
            ) as Map<Any?, *>

        session.addOutput(videoOutput)
    }

    session.commitConfiguration()
    return session
}

@OptIn(ExperimentalForeignApi::class)
internal fun startSession(
    session: AVCaptureSession,
    onReady: () -> Unit
) {
    // Use high-priority queue for camera startup
    val queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH.toLong(), 0UL)
    val group = dispatch_group_create()

    dispatch_group_enter(group)
    dispatch_async(queue) {
        if (!session.running) {
            session.startRunning()
        }
        dispatch_group_leave(group)
    }

    // Notify on main queue when ready
    dispatch_group_notify(group, dispatch_get_main_queue()) {
        onReady()
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun stopSession(session: AVCaptureSession) {
    val queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH.toLong(), 0UL)
    dispatch_async(queue) {
        if (session.running) {
            session.stopRunning()
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun switchCameraInput(
    session: AVCaptureSession,
    newMode: CameraMode,
    onReady: () -> Unit
) {
    // Use high-priority queue for camera switching
    val queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH.toLong(), 0UL)

    discoverCamera(newMode)?.let { camera ->
        dispatch_async(queue) {
            session.beginConfiguration()

            // Remove old inputs
            session.inputs.forEach { input ->
                session.removeInput(input as AVCaptureInput)
            }

            // Add new input
            val newInput =
                AVCaptureDeviceInput.deviceInputWithDevice(camera, error = null)
                    as? AVCaptureDeviceInput

            if (newInput != null && session.canAddInput(newInput)) {
                session.addInput(newInput)
            }

            session.commitConfiguration()

            // Session should already be running, no need to restart
            dispatch_async(dispatch_get_main_queue()) {
                onReady()
            }
        }
    }
}
