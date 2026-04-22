package uz.yalla.media.camera

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIGraphicsImageRendererFormat
import platform.UIKit.UIImage
import platform.UIKit.UIImageOrientation
import platform.posix.memcpy

/**
 * Redraws this [UIImage] so that its `imageOrientation` becomes `.up`.
 *
 * Images captured by the camera often carry a non-identity orientation tag.
 * This function bakes the rotation into the pixel data using [UIGraphicsImageRenderer]
 * so that downstream consumers (encoders, display layers) see a correctly-oriented image.
 *
 * If the image is already `.up`, the receiver is returned unchanged (no copy).
 *
 * @return A new [UIImage] with `UIImageOrientationUp`, or the same instance when already upright.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
internal fun UIImage.normalizeOrientation(): UIImage {
    if (imageOrientation == UIImageOrientation.UIImageOrientationUp) return this

    val format = UIGraphicsImageRendererFormat().apply { this.scale = this@normalizeOrientation.scale }
    val renderer = UIGraphicsImageRenderer(size = size, format = format)
    return renderer.imageWithActions { _ ->
        drawInRect(CGRectMake(0.0, 0.0, size.useContents { width }, size.useContents { height }))
    }
}

/**
 * Copies the contents of this [NSData] into a Kotlin [ByteArray].
 *
 * Uses `memcpy` for a single bulk copy. Returns an empty array when [length] is zero.
 *
 * @return A newly-allocated byte array containing the data's bytes.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
internal fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val byteArray = ByteArray(size)
    if (size > 0) {
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, length)
        }
    }
    return byteArray
}

/**
 * Scales this [UIImage] down so that neither dimension exceeds [maxDimension], preserving
 * the aspect ratio.
 *
 * Uses [UIGraphicsImageRenderer] (Metal-backed on supported hardware) for the resize.
 * If the image already fits within the requested bounds it is returned unchanged.
 *
 * @param maxDimension      Maximum width or height in points. Defaults to 1024.
 * @param compressionQuality Unused in the resize step but kept for API symmetry.
 * @return A resized [UIImage], or the receiver if no scaling is needed.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
@Suppress("UnusedParameter") // compressionQuality wired in Phase 5 media-module pass; kept for call-site stability
internal fun UIImage.resizeAndCompress(
    maxDimension: Double = 1024.0,
    compressionQuality: Double = 0.7
): UIImage {
    val currentWidth = size.useContents { width }
    val currentHeight = size.useContents { height }

    // If image is already small enough, return as is
    if (currentWidth <= maxDimension && currentHeight <= maxDimension) {
        return this
    }

    // Calculate new size maintaining aspect ratio
    val aspectRatio = currentWidth / currentHeight
    val newSize =
        if (currentWidth > currentHeight) {
            CGSizeMake(maxDimension, maxDimension / aspectRatio)
        } else {
            CGSizeMake(maxDimension * aspectRatio, maxDimension)
        }

    // Resize image using modern Metal-backed renderer
    val renderer = UIGraphicsImageRenderer(size = newSize)
    return renderer.imageWithActions { _ ->
        drawInRect(CGRectMake(0.0, 0.0, newSize.useContents { width }, newSize.useContents { height }))
    }
}
