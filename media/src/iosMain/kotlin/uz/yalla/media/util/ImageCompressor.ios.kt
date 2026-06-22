package uz.yalla.media.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIGraphicsImageRendererFormat
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import uz.yalla.media.config.CompressionConfig
import kotlin.math.min

@OptIn(ExperimentalForeignApi::class)
public actual fun compressImage(
    imageBytes: ByteArray,
    config: CompressionConfig
): ByteArray? {
    val maxDimension = config.maxDimension
    val maxSizeBytes = config.maxFileSize

    val nsData =
        imageBytes.usePinned { pinned ->
            NSData.dataWithBytes(bytes = pinned.addressOf(0), length = imageBytes.size.toULong())
        }

    val originalImage = UIImage(nsData) ?: return null

    return autoreleasepool {
        val originalWidth = originalImage.size.useContents { this.width }
        val originalHeight = originalImage.size.useContents { this.height }

        val scale =
            if (
                originalWidth.toDouble() > maxDimension ||
                originalHeight.toDouble() > maxDimension
            ) {
                min(
                    maxDimension.toDouble() / originalWidth.toDouble(),
                    maxDimension.toDouble() / originalHeight.toDouble()
                )
            } else {
                1.0
            }

        val newWidth = originalWidth.toDouble() * scale
        val newHeight = originalHeight.toDouble() * scale

        val format =
            UIGraphicsImageRendererFormat().apply {
                setScale(1.0)
                setOpaque(true)
            }

        val resizedImage =
            UIGraphicsImageRenderer(size = CGSizeMake(newWidth, newHeight), format = format)
                .imageWithActions { _ ->
                    originalImage.drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
                }

        var lo = MIN_JPEG_QUALITY
        var hi = config.quality
        var bestData: NSData? = null

        while (lo <= hi) {
            val mid = (lo + hi) / 2
            val quality = mid.toDouble() / 100.0
            val compressed = UIImageJPEGRepresentation(resizedImage, quality)
            val size = compressed?.length?.toInt() ?: 0
            if (size in 1..maxSizeBytes) {
                bestData = compressed
                lo = mid + 1
            } else {
                hi = mid - 1
            }
        }

        if (bestData == null) {
            val smallerFormat =
                UIGraphicsImageRendererFormat().apply {
                    setScale(1.0)
                    setOpaque(true)
                }
            val smallerImage =
                UIGraphicsImageRenderer(size = CGSizeMake(newWidth / 2.0, newHeight / 2.0), format = smallerFormat)
                    .imageWithActions { _ ->
                        resizedImage.drawInRect(CGRectMake(0.0, 0.0, newWidth / 2.0, newHeight / 2.0))
                    }
            val minQuality = MIN_JPEG_QUALITY.toDouble() / 100.0
            val fallback = UIImageJPEGRepresentation(smallerImage, minQuality)
            bestData = fallback?.takeIf { (it.length.toInt()) in 1..maxSizeBytes }
        }

        bestData?.toByteArray()
    }
}
