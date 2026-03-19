package uz.yalla.media.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.ByteArrayOutputStream
import kotlin.math.min

/**
 * Android implementation of [compressImage].
 *
 * Uses [BitmapFactory] for decoding, [Matrix] for scaling, and JPEG [Bitmap.compress]
 * with binary-search quality reduction.
 *
 * @since 0.0.1
 */
actual fun compressImage(
    imageBytes: ByteArray,
    config: CompressionConfig,
): ByteArray {
    val maxDimension = config.maxDimension
    val maxSizeBytes = config.maxFileSize

    val options =
        BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

    val originalWidth = options.outWidth
    val originalHeight = options.outHeight

    val scale =
        if (
            originalWidth > maxDimension ||
            originalHeight > maxDimension
        ) {
            min(
                maxDimension.toDouble() / originalWidth,
                maxDimension.toDouble() / originalHeight,
            )
        } else {
            1.0
        }

    val newWidth = (originalWidth * scale).toInt()
    val newHeight = (originalHeight * scale).toInt()

    val decodedOptions =
        BitmapFactory.Options().apply {
            inJustDecodeBounds = false
            inSampleSize = calculateInSampleSize(options, newWidth, newHeight)
        }

    val bitmap =
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, decodedOptions)
            ?: return imageBytes

    val resizedBitmap =
        if (bitmap.width != newWidth || bitmap.height != newHeight) {
            val exactWidth = newWidth.toFloat()
            val exactHeight = newHeight.toFloat()
            val scaleX = exactWidth / bitmap.width
            val scaleY = exactHeight / bitmap.height

            val matrix =
                Matrix().apply {
                    postScale(scaleX, scaleY)
                }

            Bitmap
                .createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true,
                ).also {
                    if (it != bitmap) {
                        bitmap.recycle()
                    }
                }
        } else {
            bitmap
        }

    // Binary search for optimal quality that meets size constraint
    var lo = 10
    var hi = config.quality
    var bestBytes: ByteArray? = null

    while (lo <= hi) {
        val mid = (lo + hi) / 2
        val stream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, mid, stream)
        val compressed = stream.toByteArray()
        if (compressed.size <= maxSizeBytes) {
            bestBytes = compressed
            lo = mid + 1
        } else {
            hi = mid - 1
        }
    }

    // Dimension reduction fallback if even minimum quality exceeds size limit
    if (bestBytes == null) {
        val smaller =
            Bitmap.createScaledBitmap(
                resizedBitmap,
                resizedBitmap.width / 2,
                resizedBitmap.height / 2,
                true,
            )
        val stream = ByteArrayOutputStream()
        smaller.compress(Bitmap.CompressFormat.JPEG, 10, stream)
        bestBytes = stream.toByteArray()
        if (smaller != resizedBitmap) {
            smaller.recycle()
        }
    }

    resizedBitmap.recycle()
    return bestBytes
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while (
            halfHeight / inSampleSize >= reqHeight &&
            halfWidth / inSampleSize >= reqWidth
        ) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}
