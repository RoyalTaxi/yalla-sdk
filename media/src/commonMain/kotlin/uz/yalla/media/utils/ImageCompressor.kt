package uz.yalla.media.utils

expect fun compressImage(
    imageBytes: ByteArray,
    config: CompressionConfig = CompressionConfig.Default
): ByteArray
