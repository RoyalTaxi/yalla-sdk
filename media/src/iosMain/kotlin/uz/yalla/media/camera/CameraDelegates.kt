package uz.yalla.media.camera

import kotlinx.cinterop.ExperimentalForeignApi
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
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.dataWithBytes
import platform.darwin.NSObject

internal class CameraFrameAnalyzerDelegate(
    private val onFrame: ((frame: ByteArray) -> Unit)?
) : NSObject(),
    AVCaptureVideoDataOutputSampleBufferDelegateProtocol {
    @OptIn(ExperimentalForeignApi::class)
    override fun captureOutput(
        output: AVCaptureOutput,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        didOutputSampleBuffer: CMSampleBufferRef?,
        fromConnection: AVCaptureConnection
    ) {
        if (onFrame == null) return

        val imageBuffer = CMSampleBufferGetImageBuffer(didOutputSampleBuffer) ?: return
        CVPixelBufferLockBaseAddress(imageBuffer, 0uL)
        val baseAddress = CVPixelBufferGetBaseAddress(imageBuffer)
        val bufferSize = CVPixelBufferGetDataSize(imageBuffer)
        val data = NSData.dataWithBytes(bytes = baseAddress, length = bufferSize)
        CVPixelBufferUnlockBaseAddress(imageBuffer, 0uL)

        onFrame.invoke(data.toByteArray())
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
