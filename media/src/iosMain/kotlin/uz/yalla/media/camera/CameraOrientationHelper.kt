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

internal class OrientationListener(
    private val cameraPreviewLayer: AVCaptureVideoPreviewLayer,
    private val capturePhotoOutput: AVCapturePhotoOutput,
    private val videoOutput: AVCaptureVideoDataOutput
) : NSObject() {
    @OptIn(BetaInteropApi::class)
    @Suppress("UNUSED_PARAMETER")
    @ObjCAction
    fun orientationDidChange(arg: NSNotification) {
        val actualOrientation = getCurrentOrientation(cameraPreviewLayer.connection?.videoOrientation)
        cameraPreviewLayer.connection?.videoOrientation = actualOrientation
        capturePhotoOutput.connectionWithMediaType(AVMediaTypeVideo)?.videoOrientation = actualOrientation
        videoOutput.connectionWithMediaType(AVMediaTypeVideo)?.videoOrientation = actualOrientation
    }

    private fun getCurrentOrientation(fallback: Long?) =
        when (UIDevice.currentDevice.orientation) {
            UIDeviceOrientation.UIDeviceOrientationPortrait -> AVCaptureVideoOrientationPortrait
            UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> AVCaptureVideoOrientationLandscapeRight
            UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> AVCaptureVideoOrientationLandscapeLeft
            UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> AVCaptureVideoOrientationPortrait
            else -> fallback ?: AVCaptureVideoOrientationPortrait
        }
}
