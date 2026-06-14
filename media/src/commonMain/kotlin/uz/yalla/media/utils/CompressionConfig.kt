package uz.yalla.media.utils

public data class CompressionConfig(
    val maxFileSize: Int,
    val maxDimension: Int,
    val quality: Int
) {
    public companion object {
        public val Default: CompressionConfig = CompressionConfig(
            maxFileSize = 1024 * 1024,
            maxDimension = 1024,
            quality = 80
        )

        public val ProfilePhoto: CompressionConfig = CompressionConfig(
            maxFileSize = 512 * 1024,
            maxDimension = 512,
            quality = 85
        )

        public val ChatImage: CompressionConfig = CompressionConfig(
            maxFileSize = 2 * 1024 * 1024,
            maxDimension = 1920,
            quality = 75
        )
    }
}
