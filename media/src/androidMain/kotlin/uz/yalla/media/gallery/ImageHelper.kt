package uz.yalla.media.gallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import uz.yalla.media.gallery.model.YallaMediaImage
import uz.yalla.media.picker.rotateImageIfRequired
import java.io.ByteArrayOutputStream

/**
 * Reads the image at [uri], applies EXIF rotation, and returns it as JPEG bytes.
 *
 * Returns `null` when the stream cannot be opened **or** when [BitmapFactory] fails
 * to decode the image (e.g. corrupted file, unsupported format).
 *
 * @param context Android context used to resolve the content URI.
 * @param uri     Content URI pointing to an image.
 * @return JPEG-encoded byte array of the correctly-rotated image, or `null` on failure.
 * @since 0.0.1
 */
internal fun getOriginalImageByteArray(
    context: Context,
    uri: Uri
): ByteArray? =
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        val bitmap = BitmapFactory.decodeStream(inputStream) ?: return@use null
        val rotatedBitmap = rotateImageIfRequired(context, bitmap, uri)
        rotatedBitmap.toByteArray()
    }

/**
 * Decodes this byte array into a [Bitmap].
 *
 * @return Decoded bitmap. Throws if the bytes cannot be decoded.
 * @since 0.0.1
 */
internal fun ByteArray.toBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, size)

/**
 * Encodes this [Bitmap] to a JPEG byte array at maximum quality.
 *
 * @return JPEG-encoded bytes.
 * @since 0.0.1
 */
private fun Bitmap.toByteArray(): ByteArray =
    ByteArrayOutputStream()
        .apply {
            compress(Bitmap.CompressFormat.JPEG, 100, this)
        }.toByteArray()

/**
 * Loads a thumbnail [Bitmap] for the given [image] at the requested [sizePx].
 *
 * On API 29+ uses [android.content.ContentResolver.loadThumbnail]; on older APIs falls
 * back to [MediaStore.Images.Thumbnails].
 *
 * @param context Android context for content resolver access.
 * @param image   Gallery image to thumbnail.
 * @param sizePx  Desired thumbnail dimension in pixels (clamped to at least 1).
 * @return Thumbnail bitmap, or `null` if loading fails.
 * @since 0.0.1
 */
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
