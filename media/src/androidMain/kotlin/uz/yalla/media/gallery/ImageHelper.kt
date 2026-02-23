package uz.yalla.media.gallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import uz.yalla.media.gallery.model.YallaMediaImage
import java.io.ByteArrayOutputStream

internal fun getOriginalImageByteArray(
    context: Context,
    uri: Uri
): ByteArray? =
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val rotatedBitmap = rotateImageIfRequired(context, bitmap, uri)
        rotatedBitmap.toByteArray()
    }

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

internal fun ByteArray.toBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, size)

private fun Bitmap.toByteArray(): ByteArray =
    ByteArrayOutputStream()
        .apply {
            compress(Bitmap.CompressFormat.JPEG, 100, this)
        }.toByteArray()

internal fun loadThumbnailBitmap(
    context: Context,
    image: YallaMediaImage,
    sizePx: Int
): Bitmap? =
    runCatching {
        val safeSize = sizePx.coerceAtLeast(1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver.loadThumbnail(image.uri, Size(safeSize, safeSize), null)
        } else {
            MediaStore.Images.Thumbnails.getThumbnail(
                context.contentResolver,
                image.id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null
            )
        }
    }.getOrNull()
