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

/**
 * Device types queried during camera discovery, ordered by preference.
 *
 * Includes wide-angle, dual-wide, dual, ultra-wide, and duo (legacy) camera types.
 */
private val deviceTypes =
    listOf(
        AVCaptureDeviceTypeBuiltInWideAngleCamera,
        AVCaptureDeviceTypeBuiltInDualWideCamera,
        AVCaptureDeviceTypeBuiltInDualCamera,
        AVCaptureDeviceTypeBuiltInUltraWideCamera,
        AVCaptureDeviceTypeBuiltInDuoCamera
    )

/**
 * Discovers an [AVCaptureDevice] matching the requested [cameraMode].
 *
 * Uses a discovery session with the [deviceTypes] list filtered by front or back position.
 *
 * @param cameraMode Desired lens (front or back).
 * @return The first matching capture device, or `null` on simulator / no camera hardware.
 * @since 0.0.1
 */
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

/**
 * Creates and configures an [AVCaptureSession] with input, photo output, and optional
 * video data output for real-time frame analysis.
 *
 * The session is configured with `AVCaptureSessionPresetHigh` (falling back to
 * `AVCaptureSessionPresetPhoto`) and video frames are delivered in `32BGRA` format
 * on a dedicated serial dispatch queue.
 *
 * @param camera        Hardware camera device to use as input.
 * @param photoOutput   Output for still-image capture.
 * @param videoOutput   Optional output for real-time video frames; `null` to skip.
 * @param frameAnalyzer Delegate receiving video frames; required when [videoOutput] is non-null.
 * @return A configured (but not yet running) [AVCaptureSession].
 * @since 0.0.1
 */
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

/**
 * Starts the given [AVCaptureSession] on a high-priority background queue and invokes
 * [onReady] on the main queue once the session is running.
 *
 * Uses `dispatch_group` to coordinate the background start with the main-queue callback.
 *
 * @param session The capture session to start.
 * @param onReady Callback fired on the main thread when the session begins running.
 * @since 0.0.1
 */
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

/**
 * Stops the given [AVCaptureSession] on a high-priority background queue.
 *
 * Safe to call even if the session is already stopped.
 *
 * @param session The capture session to stop.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
internal fun stopSession(session: AVCaptureSession) {
    val queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH.toLong(), 0UL)
    dispatch_async(queue) {
        if (session.running) {
            session.stopRunning()
        }
    }
}

/**
 * Hot-swaps the camera input of a running [AVCaptureSession] to the lens specified by
 * [newMode].
 *
 * Removes all existing inputs, discovers the new camera device, adds a fresh input, and
 * commits the configuration — all on a high-priority background queue. [onReady] is
 * dispatched on the main queue after the switch completes.
 *
 * @param session The running capture session whose input is replaced.
 * @param newMode Target camera lens (front or back).
 * @param onReady Callback fired on the main thread once the new input is active.
 * @since 0.0.1
 */
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
