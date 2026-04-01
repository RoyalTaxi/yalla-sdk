package uz.yalla.media.picker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.FloatRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Asynchronous image resizer that reads a content URI, down-scales if needed, applies a
 * color filter, and returns the result as a JPEG byte array.
 *
 * Intermediate results (resized and filtered bitmaps) are cached in [YallaBitmapCache]
 * to avoid redundant work when the same URI is processed again.
 *
 * @since 0.0.1
 */
internal object YallaImageResizer {
    /**
     * Reads the image at [uri], resizes it when larger than [resizeThresholdBytes], applies
     * the [filterOptions] filter, and delivers the JPEG bytes to [onResult] on the main thread.
     *
     * @param context              Android context for content resolver access.
     * @param coroutineScope       Scope used to launch the background work.
     * @param uri                  Content URI pointing to the source image.
     * @param width                Maximum output width in pixels.
     * @param height               Maximum output height in pixels.
     * @param resizeThresholdBytes Byte-size threshold below which resizing is skipped.
     * @param compressionQuality   JPEG compression quality (0.0 .. 1.0).
     * @param filterOptions        Color filter to apply to the image.
     * @param onResult             Callback with the processed JPEG bytes, or `null` on failure.
     * @since 0.0.1
     */
    internal fun resizeImageAsync(
        context: Context,
        coroutineScope: CoroutineScope,
        uri: Uri,
        width: Int,
        height: Int,
        resizeThresholdBytes: Long,
        @FloatRange(from = 0.0, to = 1.0) compressionQuality: Double,
        filterOptions: FilterOptions,
        onResult: (ByteArray?) -> Unit
    ) {
        coroutineScope.launch(Dispatchers.Default) {
            val result =
                if (getImageSize(context, uri) > resizeThresholdBytes) {
                    resizeImage(context, uri, width, height, compressionQuality, filterOptions)
                } else {
                    getOriginalImageByteArray(context, uri)
                }
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }

    /**
     * Queries the content resolver for the byte size of the file at [uri].
     *
     * @return File size in bytes, or `0` if the query fails.
     */
    private fun getImageSize(
        context: Context,
        uri: Uri
    ): Int =
        context.contentResolver.query(uri, null, null, null, null).use { cursor ->
            val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE)
            cursor?.moveToFirst()
            sizeIndex?.let { cursor.getInt(it) } ?: 0
        }

    /**
     * Reads the image at [uri], applies EXIF rotation, and returns it as JPEG bytes.
     *
     * @return JPEG bytes, or `null` when the stream cannot be opened.
     */
    private fun getOriginalImageByteArray(
        context: Context,
        uri: Uri
    ): ByteArray? =
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val rotatedBitmap = rotateImageIfRequired(context, bitmap, uri)
            rotatedBitmap.toByteArray()
        }

    /**
     * Resizes the image at [uri] to fit within [width] x [height], applies EXIF rotation
     * and the given [filterOptions], and returns the result as compressed JPEG bytes.
     *
     * Both the resized bitmap and the final filtered bitmap are cached in [YallaBitmapCache].
     *
     * @return Compressed JPEG bytes, or `null` when the image cannot be loaded.
     */
    private suspend fun resizeImage(
        context: Context,
        uri: Uri,
        width: Int,
        height: Int,
        @FloatRange(from = 0.0, to = 1.0) compression: Double,
        filterOptions: FilterOptions
    ): ByteArray? {
        val resizeCacheKey = "${uri}_w${width}_h$height"
        val filterCacheKey = "${resizeCacheKey}_$filterOptions"

        withContext(Dispatchers.IO) {
            YallaBitmapCache.instance.get(filterCacheKey)
        }?.let { return YallaBitmapCache.bitmapToByteArray(it) }

        val resizedBitmap =
            loadResizedBitmap(context, uri, width, height, resizeCacheKey)
                ?: return null

        val rotatedBitmap = rotateImageIfRequired(context, resizedBitmap, uri)
        val filteredBitmap = applyFilter(rotatedBitmap, filterOptions)

        return filteredBitmap.toByteArray((compression.coerceIn(0.0, 1.0) * 100).toInt()).also {
            YallaBitmapCache.instance.put(filterCacheKey, filteredBitmap)
        }
    }

    /**
     * Loads and down-samples the image at [uri] to approximately [width] x [height].
     *
     * Checks [YallaBitmapCache] first; on a miss decodes with an appropriate `inSampleSize`
     * and caches the result.
     *
     * @return The down-sampled bitmap, or `null` when the image cannot be decoded.
     */
    private suspend fun loadResizedBitmap(
        context: Context,
        uri: Uri,
        width: Int,
        height: Int,
        cacheKey: String
    ): Bitmap? {
        withContext(Dispatchers.IO) {
            YallaBitmapCache.instance.get(cacheKey)
        }?.let { return it }

        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(inputStream, null, options)

            options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, width, height)
            options.inJustDecodeBounds = false

            context.contentResolver.openInputStream(uri)?.use { scaledInputStream ->
                BitmapFactory.decodeStream(scaledInputStream, null, options)?.also {
                    YallaBitmapCache.instance.put(cacheKey, it)
                }
            }
        }
    }

    /**
     * Computes the largest power-of-two sample size that brings both dimensions at or below
     * [targetWidth] and [targetHeight].
     *
     * @return A power-of-two sample size (1, 2, 4, ...).
     */
    private fun calculateInSampleSize(
        outWidth: Int,
        outHeight: Int,
        targetWidth: Int,
        targetHeight: Int
    ): Int {
        var inSampleSize = 1
        while (outWidth / inSampleSize > targetWidth || outHeight / inSampleSize > targetHeight) {
            inSampleSize *= 2
        }
        return inSampleSize
    }
}

/**
 * Encodes this [Bitmap] as JPEG bytes at the given [quality].
 *
 * @param quality JPEG quality (0-100). Defaults to 100.
 * @return JPEG-encoded byte array.
 */
private fun Bitmap.toByteArray(quality: Int = 100): ByteArray =
    ByteArrayOutputStream().use { stream ->
        compress(Bitmap.CompressFormat.JPEG, quality, stream)
        stream.toByteArray()
    }
