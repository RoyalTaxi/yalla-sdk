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

internal object YallaImageResizer {
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

    private fun getImageSize(
        context: Context,
        uri: Uri
    ): Int =
        context.contentResolver.query(uri, null, null, null, null).use { cursor ->
            val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE)
            cursor?.moveToFirst()
            sizeIndex?.let { cursor.getInt(it) } ?: 0
        }

    private fun getOriginalImageByteArray(
        context: Context,
        uri: Uri
    ): ByteArray? =
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val rotatedBitmap = rotateImageIfRequired(context, bitmap, uri)
            rotatedBitmap.toByteArray()
        }

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

private fun Bitmap.toByteArray(quality: Int = 100): ByteArray =
    ByteArrayOutputStream().use { stream ->
        compress(Bitmap.CompressFormat.JPEG, quality, stream)
        stream.toByteArray()
    }
