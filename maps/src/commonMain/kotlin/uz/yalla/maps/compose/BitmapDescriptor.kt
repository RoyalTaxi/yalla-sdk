package uz.yalla.maps.compose

/**
 * Platform-specific bitmap handle used as a map marker or overlay icon.
 *
 * On Android wraps `com.google.android.gms.maps.model.BitmapDescriptor`;
 * on iOS wraps `UIImage`.
 */
expect class BitmapDescriptor

/**
 * Factory for creating [BitmapDescriptor] instances from raw pixel data or encoded images.
 */
expect object BitmapDescriptorFactory {
    /**
     * Creates a [BitmapDescriptor] from raw ARGB pixel bytes.
     *
     * @return A new [BitmapDescriptor] wrapping the decoded image.
     */
    fun fromBytes(
        bytes: ByteArray,
        width: Int,
        height: Int
    ): BitmapDescriptor

    /**
     * Creates a [BitmapDescriptor] from an encoded image (e.g., PNG or JPEG bytes).
     *
     * @return A new [BitmapDescriptor] wrapping the decoded image.
     */
    fun fromEncodedImage(data: ByteArray): BitmapDescriptor
}
