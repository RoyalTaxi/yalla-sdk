package uz.yalla.media.config

import uz.yalla.media.util.MIN_JPEG_QUALITY

public data class CompressionConfig(
    val maxFileSize: Int,
    val maxDimension: Int,
    val quality: Int
) {
    init {
        require(quality in MIN_JPEG_QUALITY..100) { "quality must be in $MIN_JPEG_QUALITY..100, was $quality" }
        require(maxDimension > 0) { "maxDimension must be positive, was $maxDimension" }
        require(maxFileSize > 0) { "maxFileSize must be positive, was $maxFileSize" }
    }

    public companion object {
        public val Default: CompressionConfig =
            CompressionConfig(
                maxFileSize = 1024 * 1024,
                maxDimension = 1024,
                quality = 80
            )

        public val ProfilePhoto: CompressionConfig =
            CompressionConfig(
                maxFileSize = 512 * 1024,
                maxDimension = 512,
                quality = 85
            )

        public val ChatImage: CompressionConfig =
            CompressionConfig(
                maxFileSize = 2 * 1024 * 1024,
                maxDimension = 1920,
                quality = 75
            )
    }
}
