package uz.yalla.media.utils

/**
 * Configuration for image compression parameters.
 *
 * Defines the target constraints that [compressImage] uses when compressing JPEG images.
 * The compressor uses binary-search quality reduction first, then dimension halving as
 * a fallback if the minimum quality still exceeds [maxFileSize].
 *
 * ## Usage
 *
 * ```kotlin
 * // Use a built-in preset:
 * val compressed = compressImage(rawBytes, CompressionConfig.ProfilePhoto)
 *
 * // Or create a custom config:
 * val custom = CompressionConfig(
 *     maxFileSize = 256 * 1024,
 *     maxDimension = 256,
 *     quality = 90,
 * )
 * val compressed = compressImage(rawBytes, custom)
 * ```
 *
 * @property maxFileSize Maximum allowed output size in bytes.
 * @property maxDimension Maximum width or height in pixels; the image is scaled proportionally.
 * @property quality Upper bound for JPEG quality (1-100); the compressor searches downward from this value.
 * @since 0.0.1
 */
data class CompressionConfig(
    val maxFileSize: Int,
    val maxDimension: Int,
    val quality: Int,
) {
    companion object {
        /**
         * General-purpose preset: 1 MB max, 1024 px max dimension, quality 80.
         *
         * @since 0.0.1
         */
        val Default =
            CompressionConfig(
                maxFileSize = 1024 * 1024, // 1 MB
                maxDimension = 1024,
                quality = 80,
            )

        /**
         * Preset optimized for profile photos: 512 KB max, 512 px max dimension, quality 85.
         *
         * @since 0.0.1
         */
        val ProfilePhoto =
            CompressionConfig(
                maxFileSize = 512 * 1024, // 512 KB
                maxDimension = 512,
                quality = 85,
            )

        /**
         * Preset for chat/message images: 2 MB max, 1920 px max dimension, quality 75.
         *
         * @since 0.0.1
         */
        val ChatImage =
            CompressionConfig(
                maxFileSize = 2 * 1024 * 1024, // 2 MB
                maxDimension = 1920,
                quality = 75,
            )
    }
}
