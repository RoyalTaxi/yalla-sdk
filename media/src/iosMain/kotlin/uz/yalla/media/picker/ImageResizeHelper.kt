package uz.yalla.media.picker

import androidx.annotation.FloatRange
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSize
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
internal fun UIImage.toByteArray(compressionQuality: Double): ByteArray {
    val validQuality = compressionQuality.coerceIn(0.0, 1.0)
    val jpegData = UIImageJPEGRepresentation(this, validQuality)!!
    return ByteArray(jpegData.length.toInt()).apply {
        memcpy(this.refTo(0), jpegData.bytes, jpegData.length)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun UIImage.fitInto(
    maxWidth: Int,
    maxHeight: Int,
    resizeThresholdBytes: Long,
    @FloatRange(from = 0.0, to = 1.0) compressionQuality: Double,
    filterOptions: FilterOptions
): UIImage {
    val imageData = toByteArray(compressionQuality)
    return if (imageData.size > resizeThresholdBytes) {
        val newSize = calculateNewSize(maxWidth, maxHeight)
        val resized = resize(newSize)
        applyFilterToUIImage(resized, filterOptions)
    } else {
        applyFilterToUIImage(this, filterOptions)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.calculateNewSize(
    maxWidth: Int,
    maxHeight: Int
): CValue<CGSize> {
    val (originalWidth, originalHeight) = size.useContents { width to height }
    val originalRatio = originalWidth / originalHeight
    val targetRatio = maxWidth.toDouble() / maxHeight.toDouble()

    val scale =
        if (originalRatio > targetRatio) {
            maxWidth.toDouble() / originalWidth
        } else {
            maxHeight.toDouble() / originalHeight
        }

    return CGSizeMake(originalWidth * scale, originalHeight * scale)
}

@OptIn(ExperimentalForeignApi::class)
internal fun UIImage.resize(targetSize: CValue<CGSize>): UIImage {
    UIGraphicsBeginImageContextWithOptions(targetSize, false, 0.0)
    drawInRect(CGRectMake(0.0, 0.0, targetSize.useContents { width }, targetSize.useContents { height }))
    val newImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return newImage!!
}
