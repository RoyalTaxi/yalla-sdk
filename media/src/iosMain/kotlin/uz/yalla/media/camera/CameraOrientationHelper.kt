package uz.yalla.media.camera

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCAction
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCaptureVideoDataOutput
import platform.AVFoundation.AVCaptureVideoOrientationLandscapeLeft
import platform.AVFoundation.AVCaptureVideoOrientationLandscapeRight
import platform.AVFoundation.AVCaptureVideoOrientationPortrait
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVMediaTypeVideo
import platform.Foundation.NSNotification
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation
import platform.darwin.NSObject

/**
 * `NSNotificationCenter` observer that synchronizes the AVFoundation video orientation with
 * the current physical device orientation.
 *
 * Registered for `UIDeviceOrientationDidChangeNotification` and updates the preview layer,
 * photo output, and video data output connections whenever the device is rotated.
 *
 * @param cameraPreviewLayer Preview layer whose connection orientation is updated.
 * @param capturePhotoOutput Photo output whose video connection orientation is updated.
 * @param videoOutput        Video data output whose connection orientation is updated.
 * @since 0.0.1
 */
internal class OrientationListener(
    private val cameraPreviewLayer: AVCaptureVideoPreviewLayer,
    private val capturePhotoOutput: AVCapturePhotoOutput,
    private val videoOutput: AVCaptureVideoDataOutput
) : NSObject() {
    /**
     * Notification handler invoked when the device orientation changes.
     *
     * Maps the current [UIDeviceOrientation] to the corresponding
     * `AVCaptureVideoOrientation` and applies it to all capture connections.
     *
     * @param arg The orientation-change notification (unused).
     * @since 0.0.1
     */
    @OptIn(BetaInteropApi::class)
    // @ObjCAction notification handler: NSNotification parameter is required by the ObjC selector even if unused here.
    @Suppress("UNUSED_PARAMETER")
    @ObjCAction
    fun orientationDidChange(arg: NSNotification) {
        val actualOrientation = getCurrentOrientation(cameraPreviewLayer.connection?.videoOrientation)
        cameraPreviewLayer.connection?.videoOrientation = actualOrientation
        capturePhotoOutput.connectionWithMediaType(AVMediaTypeVideo)?.videoOrientation = actualOrientation
        videoOutput.connectionWithMediaType(AVMediaTypeVideo)?.videoOrientation = actualOrientation
    }

    /**
     * Maps the current [UIDeviceOrientation] to the corresponding `AVCaptureVideoOrientation`.
     *
     * `UIDeviceOrientation` and `AVCaptureVideoOrientation` use opposite conventions for
     * landscape: `UIDeviceOrientationLandscapeLeft` means the device is rotated so the
     * home button is on the right, which corresponds to `AVCaptureVideoOrientationLandscapeRight`
     * (video captured in landscape-right orientation). The swap is intentional.
     *
     * @param fallback Orientation to use when the device orientation is face-up, face-down,
     *                 or unknown. Defaults to portrait if `null`.
     * @return The matching `AVCaptureVideoOrientation` constant.
     */
    private fun getCurrentOrientation(fallback: Long?) =
        when (UIDevice.currentDevice.orientation) {
            UIDeviceOrientation.UIDeviceOrientationPortrait -> AVCaptureVideoOrientationPortrait
            UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> AVCaptureVideoOrientationLandscapeRight
            UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> AVCaptureVideoOrientationLandscapeLeft
            UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> AVCaptureVideoOrientationPortrait
            else -> fallback ?: AVCaptureVideoOrientationPortrait
        }
}
