package uz.yalla.media.util

import uz.yalla.media.config.CompressionConfig

internal const val MIN_JPEG_QUALITY: Int = 10

public expect fun compressImage(
    imageBytes: ByteArray,
    config: CompressionConfig = CompressionConfig.Default
): ByteArray?
