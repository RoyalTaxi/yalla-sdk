package uz.yalla.media.util

import uz.yalla.media.config.CompressionConfig

/** Lower bound the JPEG-quality search may drop to before falling back to a half-resolution encode. */
internal const val MIN_JPEG_QUALITY: Int = 10

/**
 * Compresses [imageBytes] to a JPEG within [config]'s dimension and size budget.
 *
 * Returns null when the input can't be decoded as an image, can't be re-encoded, or can't be brought
 * within [CompressionConfig.maxFileSize] even at minimum quality and half resolution — the caller
 * decides what to do (e.g. surface an error). The function never returns the untouched input
 * disguised as a compressed result, and never returns bytes that exceed the size budget.
 *
 * The result has any EXIF orientation baked into the pixels (and stripped from metadata), so the same
 * input yields equivalently-oriented output on every platform. EXIF metadata, including GPS location,
 * is dropped by the re-encode.
 *
 * Decoding and encoding are CPU- and memory-heavy; run this off the main thread.
 */
public expect fun compressImage(
    imageBytes: ByteArray,
    config: CompressionConfig = CompressionConfig.Default
): ByteArray?
