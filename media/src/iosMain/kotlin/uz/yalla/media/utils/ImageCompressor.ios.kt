package uz.yalla.media.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
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
            UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
            originalImage.drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
            val image = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            image ?: originalImage
        } else {
            originalImage
        }

    var quality = config.quality.toDouble() / 100.0
    var compressedData: NSData? = null
    var attempts = 0

    while (quality > 0.1 && attempts < 5) {
        compressedData = UIImageJPEGRepresentation(resizedImage, quality)
        val size = compressedData?.length?.toInt() ?: 0

        if (size <= maxSizeBytes || quality <= 0.1) {
            break
        }

        quality -= 0.15
        attempts++
    }

    val finalData =
        compressedData
            ?: UIImageJPEGRepresentation(resizedImage, 0.5)
            ?: return imageBytes

    val finalSize = finalData.length.toInt()
    val result = ByteArray(finalSize)
    if (finalSize > 0) {
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), finalData.bytes, finalData.length)
        }
    }

    return result
}
