package uz.yalla.media.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

/**
 * Converts this CameraX [ImageProxy] to a JPEG-encoded byte array, applying EXIF rotation
 * when necessary.
 *
 * The proxy is **closed** after conversion, so callers must not reference it afterwards.
 *
 * @return JPEG bytes with the correct orientation baked in.
 * @since 0.0.1
 */
internal fun ImageProxy.toByteArray(): ByteArray {
    val bitmap = toBitmap()
    val rotatedData =
        if (imageInfo.rotationDegrees != 0) {
            bitmap.rotate(imageInfo.rotationDegrees)
        } else {
            bitmap.toByteArray()
        }
    close()
    return rotatedData
}

/**
 * Encodes this [Bitmap] to a JPEG byte array at maximum quality (100).
 *
 * @return JPEG-encoded bytes.
 * @since 0.0.1
 */
internal fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

/**
 * Rotates this [Bitmap] by the given [degrees] and encodes the result as JPEG.
 *
 * @param degrees Clockwise rotation in degrees (e.g., 90, 180, 270).
 * @return JPEG-encoded bytes of the rotated image.
 * @since 0.0.1
 */
internal fun Bitmap.rotate(degrees: Int): ByteArray {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    val rotatedBitmap = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    return rotatedBitmap.toByteArray()
}
