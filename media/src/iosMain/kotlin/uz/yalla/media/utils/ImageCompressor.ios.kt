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

/**
 * Minimum JPEG quality used during the binary search (maps to 0.1 in the 0.0–1.0 range).
 *
 * Kept as a named constant so the loop bound and the dimension-reduction fallback stay in sync.
 */
private const val MIN_QUALITY_INT = 10

/**
 * iOS implementation of [compressImage].
 *
 * Uses [UIImage] for decoding, [UIGraphicsImageRenderer] for proportional scaling, and
 * [UIImageJPEGRepresentation] with a binary-search quality reduction strategy.
 *
 * ### Algorithm
 * 1. **Dimension scaling** — if either dimension exceeds [CompressionConfig.maxDimension],
 *    the image is proportionally down-scaled using [UIGraphicsImageRenderer].
 * 2. **Quality binary search** — JPEG quality is reduced from [CompressionConfig.quality]
 *    down to [MIN_QUALITY_INT] (10 %) until the output fits within
 *    [CompressionConfig.maxFileSize].
 * 3. **Dimension-reduction fallback** — if the minimum quality still exceeds the size limit,
 *    dimensions are halved and the image is re-encoded at minimum quality.
 *
 * @param imageBytes Raw image bytes (JPEG, PNG, or any format decodable by [UIImage]).
 * @param config     Compression constraints; defaults to [CompressionConfig.Default].
 * @return Compressed JPEG bytes satisfying [config], or the original [imageBytes] when
 *         decoding fails or JPEG conversion returns `null`.
 * @since 0.0.1
 */
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

    // Binary search for optimal quality that meets size constraint.
    // Map quality from 10–100 int range to 0.1–1.0 double range.
    var lo = MIN_QUALITY_INT
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

    // Dimension-reduction fallback: halve dimensions and use minimum quality.
    if (bestData == null) {
        val smallerSize = CGSizeMake(newWidth / 2.0, newHeight / 2.0)
        val renderer = UIGraphicsImageRenderer(size = smallerSize)
        val smallerImage =
            renderer.imageWithActions { _ ->
                resizedImage.drawInRect(CGRectMake(0.0, 0.0, newWidth / 2.0, newHeight / 2.0))
            }
        val minQuality = MIN_QUALITY_INT.toDouble() / 100.0
        bestData = UIImageJPEGRepresentation(smallerImage, minQuality)
            ?: return imageBytes
    }

    return bestData.toByteArraySafe() ?: imageBytes
}

/**
 * Safely converts [NSData] to a [ByteArray].
 *
 * Returns `null` when [NSData.bytes] is `null` (empty data) or the length is zero,
 * preventing an invalid `memcpy` call.
 *
 * @return The byte array representation, or `null` if the data is empty.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArraySafe(): ByteArray? {
    val size = length.toInt()
    if (size == 0) return null
    val bytes = bytes ?: return null
    val result = ByteArray(size)
    result.usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length)
    }
    return result
}
