package uz.yalla.media.picker

import androidx.annotation.FloatRange
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSize
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

/**
 * Encodes this [UIImage] as JPEG bytes at the given [compressionQuality].
 *
 * @param compressionQuality JPEG quality in the 0.0 .. 1.0 range (clamped internally).
 * @return JPEG-encoded byte array.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
internal fun UIImage.toByteArray(compressionQuality: Double): ByteArray {
    val validQuality = compressionQuality.coerceIn(0.0, 1.0)
    val jpegData = UIImageJPEGRepresentation(this, validQuality)!!
    return ByteArray(jpegData.length.toInt()).apply {
        memcpy(this.refTo(0), jpegData.bytes, jpegData.length)
    }
}

/**
 * Conditionally resizes this [UIImage] to fit within [maxWidth] x [maxHeight] bounds,
 * then applies the specified [filterOptions] color filter.
 *
 * The image is resized only if its JPEG representation (at [compressionQuality]) exceeds
 * [resizeThresholdBytes]. Otherwise only the filter is applied.
 *
 * @param maxWidth            Maximum output width in pixels.
 * @param maxHeight           Maximum output height in pixels.
 * @param resizeThresholdBytes Byte-size threshold below which resizing is skipped.
 * @param compressionQuality  JPEG quality used to check the current file size (0.0 .. 1.0).
 * @param filterOptions       Color filter to apply after optional resizing.
 * @return A (possibly resized and filtered) [UIImage].
 * @since 0.0.1
 */
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

/**
 * Computes a proportionally scaled [CGSize] that fits this image within
 * [maxWidth] x [maxHeight] while preserving the original aspect ratio.
 *
 * @param maxWidth  Maximum output width in pixels.
 * @param maxHeight Maximum output height in pixels.
 * @return A [CGSize] whose dimensions respect the aspect ratio constraint.
 */
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

/**
 * Resizes this [UIImage] to exactly [targetSize] using [UIGraphicsImageRenderer].
 *
 * @param targetSize Desired output dimensions.
 * @return A new [UIImage] at the requested size.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
internal fun UIImage.resize(targetSize: CValue<CGSize>): UIImage {
    val renderer = UIGraphicsImageRenderer(size = targetSize)
    return renderer.imageWithActions { _ ->
        drawInRect(CGRectMake(0.0, 0.0, targetSize.useContents { width }, targetSize.useContents { height }))
    }
}
