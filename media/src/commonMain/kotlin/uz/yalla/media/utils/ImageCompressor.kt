package uz.yalla.media.utils

/**
 * Compresses an image to ensure it fits within the given [config] constraints.
 * @param imageBytes Original image bytes
 * @param config Compression settings (max file size, max dimension, initial quality)
 * @return Compressed image bytes
 */
expect fun compressImage(
    imageBytes: ByteArray,
    config: CompressionConfig = CompressionConfig.Default,
): ByteArray
