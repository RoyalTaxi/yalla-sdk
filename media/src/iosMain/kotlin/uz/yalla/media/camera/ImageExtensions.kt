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

@OptIn(ExperimentalForeignApi::class)
internal fun UIImage.normalizeOrientation(): UIImage {
    if (imageOrientation == UIImageOrientation.UIImageOrientationUp) return this

    val format = UIGraphicsImageRendererFormat().apply { this.scale = this@normalizeOrientation.scale }
    val renderer = UIGraphicsImageRenderer(size = size, format = format)
    return renderer.imageWithActions { _ ->
        drawInRect(CGRectMake(0.0, 0.0, size.useContents { width }, size.useContents { height }))
    }
}

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

@OptIn(ExperimentalForeignApi::class)
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
