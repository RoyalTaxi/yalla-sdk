package uz.yalla.media.utils

public expect fun compressImage(
    imageBytes: ByteArray,
    config: CompressionConfig = CompressionConfig.Default
): ByteArray
