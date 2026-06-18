package uz.yalla.media.utils

/**
 * Compresses [imageBytes] to a JPEG within [config]'s dimension and size budget.
 *
 * Returns null when the input can't be decoded as an image or can't be re-encoded — the caller
 * decides what to do (e.g. fall back to the original, surface an error). The function never returns
 * the untouched input disguised as a compressed result.
 */
public expect fun compressImage(
    imageBytes: ByteArray,
    config: CompressionConfig = CompressionConfig.Default
): ByteArray?
