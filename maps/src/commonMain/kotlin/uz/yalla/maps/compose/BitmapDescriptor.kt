package uz.yalla.maps.compose

/**
 * Platform-specific bitmap handle used as a map marker or overlay icon.
 *
 * On Android wraps `com.google.android.gms.maps.model.BitmapDescriptor`;
 * on iOS wraps `UIImage`.
 *
 * @since 0.0.1
 */
expect class BitmapDescriptor

/**
 * Factory for creating [BitmapDescriptor] instances from raw pixel data or encoded images.
 *
 * @since 0.0.1
 */
expect object BitmapDescriptorFactory {
    /**
     * Creates a [BitmapDescriptor] from raw ARGB pixel bytes.
     *
     * @param bytes Raw pixel data in ARGB format (4 bytes per pixel).
     * @param width Image width in pixels.
     * @param height Image height in pixels.
     * @return A new [BitmapDescriptor] wrapping the decoded image.
     * @since 0.0.1
     */
    fun fromBytes(
        bytes: ByteArray,
        width: Int,
        height: Int
    ): BitmapDescriptor

    /**
     * Creates a [BitmapDescriptor] from an encoded image (e.g., PNG or JPEG bytes).
     *
     * @param data The encoded image byte array.
     * @return A new [BitmapDescriptor] wrapping the decoded image.
     * @since 0.0.1
     */
    fun fromEncodedImage(data: ByteArray): BitmapDescriptor
}
