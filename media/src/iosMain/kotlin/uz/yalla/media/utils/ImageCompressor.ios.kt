package uz.yalla.media.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy
import kotlin.math.min

@OptIn(ExperimentalForeignApi::class)
actual fun compressImage(
    imageBytes: ByteArray,
    config: CompressionConfig,
): ByteArray {
    val maxDimension = config.maxDimension
    val maxSizeBytes = config.maxFileSize

    val nsData =
        imageBytes.usePinned { pinned ->
            NSData.dataWithBytes(bytes = pinned.addressOf(0), length = imageBytes.size.toULong())
        }

    val originalImage = UIImage(nsData) ?: return imageBytes

    val originalWidth = originalImage.size.useContents { this.width }
    val originalHeight = originalImage.size.useContents { this.height }

    val scale =
        if (
            originalWidth.toDouble() > maxDimension ||
            originalHeight.toDouble() > maxDimension
        ) {
            min(
                maxDimension.toDouble() / originalWidth.toDouble(),
                maxDimension.toDouble() / originalHeight.toDouble(),
            )
        } else {
            1.0
        }

    val newWidth = originalWidth.toDouble() * scale
    val newHeight = originalHeight.toDouble() * scale
    val newSize = CGSizeMake(newWidth, newHeight)

    val resizedImage =
        if (scale < 1.0) {
            val renderer = UIGraphicsImageRenderer(size = newSize)
            renderer.imageWithActions { _ ->
                originalImage.drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
            }
        } else {
            originalImage
        }

    // Binary search for optimal quality that meets size constraint
    // Map quality from 10-100 int range to 0.1-1.0 double range
    var lo = 10
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

    // Dimension reduction fallback if even minimum quality exceeds size limit
    if (bestData == null) {
        val smallerSize = CGSizeMake(newWidth / 2.0, newHeight / 2.0)
        val renderer = UIGraphicsImageRenderer(size = smallerSize)
        val smallerImage =
            renderer.imageWithActions { _ ->
                resizedImage.drawInRect(CGRectMake(0.0, 0.0, newWidth / 2.0, newHeight / 2.0))
            }
        bestData = UIImageJPEGRepresentation(smallerImage, 0.1)
            ?: return imageBytes
    }

    val finalSize = bestData.length.toInt()
    val result = ByteArray(finalSize)
    if (finalSize > 0) {
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bestData.bytes, bestData.length)
        }
    }

    return result
}
