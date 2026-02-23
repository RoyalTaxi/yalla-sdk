package uz.yalla.media.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.ByteArrayOutputStream
import kotlin.math.min

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

    var quality = config.quality
    var attempts = 0
    var result: ByteArray

    while (quality > 10 && attempts < 5) {
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        result = outputStream.toByteArray()

        if (result.size <= maxSizeBytes || quality <= 10) {
            resizedBitmap.recycle()
            return result
        }

        quality -= 15
        attempts++
    }

    val outputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream)
    result = outputStream.toByteArray()
    resizedBitmap.recycle()

    return result
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
