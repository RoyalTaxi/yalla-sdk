package uz.yalla.media.camera

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCapturePhoto
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCaptureVideoDataOutputSampleBufferDelegateProtocol
import platform.AVFoundation.fileDataRepresentation
import platform.CoreMedia.CMSampleBufferGetImageBuffer
import platform.CoreMedia.CMSampleBufferRef
import platform.CoreVideo.CVPixelBufferGetBaseAddress
import platform.CoreVideo.CVPixelBufferGetDataSize
import platform.CoreVideo.CVPixelBufferLockBaseAddress
import platform.CoreVideo.CVPixelBufferUnlockBaseAddress
import platform.Foundation.NSError
import platform.darwin.NSObject
import platform.posix.memcpy

/**
 * AVFoundation video data output delegate that forwards raw pixel data to a Kotlin callback.
 *
 * Each frame is extracted from the [CMSampleBufferRef]'s pixel buffer via a single
 * `memcpy` (BGRA layout), avoiding an intermediate `NSData` allocation.
 *
 * @param onFrame Callback receiving the raw pixel bytes for each video frame, or `null`
 *                to disable frame delivery.
 * @since 0.0.1
 */
internal class CameraFrameAnalyzerDelegate(private val onFrame: ((frame: ByteArray) -> Unit)?) :
    NSObject(),
    AVCaptureVideoDataOutputSampleBufferDelegateProtocol {
    @OptIn(ExperimentalForeignApi::class)
    override fun captureOutput(
        output: AVCaptureOutput,
        // Objective-C selector uses this parameter label; Kotlin must rename to match the ObjC protocol.
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        didOutputSampleBuffer: CMSampleBufferRef?,
        fromConnection: AVCaptureConnection
    ) {
        if (onFrame == null) return

        val pixelBuffer = CMSampleBufferGetImageBuffer(didOutputSampleBuffer) ?: return
        CVPixelBufferLockBaseAddress(pixelBuffer, 0uL)
        val baseAddress = CVPixelBufferGetBaseAddress(pixelBuffer)
        val dataSize = CVPixelBufferGetDataSize(pixelBuffer)

        // Single copy: CVPixelBuffer -> ByteArray directly (avoids intermediate NSData)
        val byteArray = ByteArray(dataSize.toInt())
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), baseAddress, dataSize)
        }

        CVPixelBufferUnlockBaseAddress(pixelBuffer, 0uL)
        onFrame.invoke(byteArray)
    }
}

/**
 * AVFoundation photo capture delegate that converts the captured photo to a byte array.
 *
 * On success the photo's `fileDataRepresentation` is delivered to [onCapture] as a
 * [ByteArray]; on error (or when the representation is `null`) the callback receives `null`.
 * [onCaptureEnd] is always invoked after the result to reset the capturing state.
 *
 * @param onCaptureEnd Action called after delivery to signal capture completion.
 * @param onCapture    Callback receiving the JPEG/HEIC file bytes, or `null` on failure.
 * @since 0.0.1
 */
internal class PhotoCaptureDelegate(
    private val onCaptureEnd: () -> Unit,
    private val onCapture: (byteArray: ByteArray?) -> Unit
) : NSObject(),
    AVCapturePhotoCaptureDelegateProtocol {
    @OptIn(ExperimentalForeignApi::class)
    override fun captureOutput(
        output: AVCapturePhotoOutput,
        didFinishProcessingPhoto: AVCapturePhoto,
        error: NSError?
    ) {
        if (error != null) {
            onCapture(null)
            onCaptureEnd()
            return
        }

        val photoData = didFinishProcessingPhoto.fileDataRepresentation()
        if (photoData == null) {
            onCapture(null)
            onCaptureEnd()
            return
        }

        onCapture(photoData.toByteArray())
        onCaptureEnd()
    }
}
