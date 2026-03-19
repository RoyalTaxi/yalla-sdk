package uz.yalla.media.utils

/**
 * Compresses a JPEG image to fit within the given [config] constraints.
 *
 * The algorithm works in two stages:
 * 1. **Dimension scaling** — if either dimension exceeds [CompressionConfig.maxDimension],
 *    the image is proportionally down-scaled.
 * 2. **Quality binary search** — JPEG quality is reduced from [CompressionConfig.quality]
 *    down to 10 until the output fits within [CompressionConfig.maxFileSize]. If even
 *    quality 10 is too large, dimensions are halved as a final fallback.
 *
 * ## Usage
 *
 * ```kotlin
 * val raw: ByteArray = capturedPhoto
 * val compressed = compressImage(raw, CompressionConfig.ProfilePhoto)
 * uploadAvatar(compressed)
 * ```
 *
 * @param imageBytes Raw image bytes (JPEG, PNG, or any format decodable by the platform).
 * @param config Compression constraints. Defaults to [CompressionConfig.Default].
 * @return Compressed JPEG bytes that satisfy [config], or the original bytes if decoding fails.
 * @since 0.0.1
 */
expect fun compressImage(
    imageBytes: ByteArray,
    config: CompressionConfig = CompressionConfig.Default,
): ByteArray
