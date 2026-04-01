package uz.yalla.media.picker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface

/**
 * Rotates [bitmap] according to the EXIF orientation tag embedded in the image at [uri].
 *
 * Reads the EXIF metadata via [ExifInterface] and applies the appropriate [Matrix]
 * transformation (rotation and/or flip). If the orientation is already upright
 * ([ExifInterface.ORIENTATION_NORMAL]) the original bitmap is returned unchanged.
 *
 * @param context Android context used to open a content resolver stream.
 * @param bitmap  Source bitmap to rotate.
 * @param uri     Content URI whose EXIF data is inspected.
 * @return A correctly-oriented bitmap (may be the same instance if no rotation is needed).
 * @since 0.0.1
 */
internal fun rotateImageIfRequired(
    context: Context,
    bitmap: Bitmap,
    uri: Uri
): Bitmap {
    val orientation =
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val exif = ExifInterface(inputStream)
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1.0f, 1.0f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1.0f, -1.0f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.preScale(-1.0f, 1.0f)
            matrix.postRotate(270f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.preScale(-1.0f, 1.0f)
            matrix.postRotate(90f)
        }
    }

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
