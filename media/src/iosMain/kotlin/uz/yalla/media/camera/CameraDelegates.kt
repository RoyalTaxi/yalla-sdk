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

internal class CameraFrameAnalyzerDelegate(private val onFrame: ((frame: ByteArray) -> Unit)?) :
    NSObject(),
    AVCaptureVideoDataOutputSampleBufferDelegateProtocol {
    @OptIn(ExperimentalForeignApi::class)
    override fun captureOutput(
        output: AVCaptureOutput,
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
