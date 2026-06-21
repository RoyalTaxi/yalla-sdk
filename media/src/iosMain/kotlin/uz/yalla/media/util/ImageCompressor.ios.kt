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

    // Wrap the allocation-heavy region (renders + JPEG search + fallback) in an autorelease pool so
    // each iteration's intermediate NSData/UIImage is freed promptly on the K/N worker thread, which
    // has no Foundation run loop draining an enclosing pool.
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

        // Pin the render scale to 1.0 so points == pixels and maxDimension is honored exactly
        // (matching Android's inSampleSize); without this the renderer defaults to screen scale
        // (2x/3x) and silently produces 4-9x the documented pixel count.
        // Construct the format directly rather than via defaultFormat(): on K/N the static return
        // type of defaultFormat() is the base UIGraphicsRendererFormat, whose scale/opaque are not
        // exposed as writable vars — only the concrete UIGraphicsImageRendererFormat carries them.
        // Use the setScale/setOpaque accessor methods rather than property assignment: the cinterop
        // commonized UIKit binding can expose `scale` as a read-only val, but the underlying ObjC
        // setScale:/setOpaque: selectors are always present as functions across binding variants.
        val format =
            UIGraphicsImageRendererFormat().apply {
                setScale(1.0)
                setOpaque(true)
            }

        // Render unconditionally (even at scale == 1.0) so EXIF orientation is baked into the pixels
        // and stripped from metadata, matching Android. Passing through `originalImage` would keep
        // the orientation as a tag and ship a sideways avatar to clients that ignore it.
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
            // Honor the size-budget contract: the half-res min-quality encode is not guaranteed to
            // fit a high-entropy input, so only keep it when within budget; otherwise null so the
            // caller can react instead of silently uploading over-budget bytes.
            bestData = fallback?.takeIf { (it.length.toInt()) in 1..maxSizeBytes }
        }

        bestData?.toByteArray()
    }
}
