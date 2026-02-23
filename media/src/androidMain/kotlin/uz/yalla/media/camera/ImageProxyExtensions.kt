package uz.yalla.media.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

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

internal fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

internal fun Bitmap.rotate(degrees: Int): ByteArray {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    val rotatedBitmap = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    return rotatedBitmap.toByteArray()
}
