package uz.yalla.media.picker

import android.graphics.Bitmap
import android.util.LruCache
import java.io.ByteArrayOutputStream

/**
 * In-memory [LruCache] for resized and filtered [Bitmap] instances.
 *
 * The cache size is 12.5 % of the JVM maximum heap (at least 4 MB) and is keyed by a
 * composite string that encodes the source URI, target dimensions, and filter options.
 * This avoids redundant decodes and resizes when the same image is selected repeatedly.
 *
 * @since 0.0.1
 */
internal object YallaBitmapCache {
    /**
     * Lazily-initialized [LruCache] whose entries are sized by their pixel byte count (in KB).
     *
     * @since 0.0.1
     */
    internal val instance: LruCache<String, Bitmap> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        object : LruCache<String, Bitmap>(calculateMemoryCacheSize()) {
            override fun sizeOf(
                key: String,
                value: Bitmap
            ): Int = (value.byteCount / 1024).coerceAtLeast(1)
        }
    }

    /**
     * Computes the LRU cache size as 12.5 % of the maximum heap, with a 4 MB floor.
     *
     * @return Cache capacity in KB.
     * @since 0.0.1
     */
    private fun calculateMemoryCacheSize(): Int {
        val maxMemory = Runtime.getRuntime().maxMemory() / 1024
        val cacheSize = (maxMemory * 0.125).toInt()
        return cacheSize.coerceAtLeast(4 * 1024)
    }

    /**
     * Encodes [bitmap] to a JPEG byte array at maximum quality (100).
     *
     * @param bitmap Bitmap to encode.
     * @return JPEG-encoded bytes.
     * @since 0.0.1
     */
    internal fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            return stream.toByteArray()
        }
    }

    /**
     * Evicts all entries from the cache, freeing memory.
     *
     * @since 0.0.1
     */
    fun clear() {
        instance.evictAll()
    }
}
