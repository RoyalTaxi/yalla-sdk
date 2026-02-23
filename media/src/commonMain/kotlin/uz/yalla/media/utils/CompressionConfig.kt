package uz.yalla.media.utils

data class CompressionConfig(
    val maxFileSize: Int,
    val maxDimension: Int,
    val quality: Int,
) {
    companion object {
        val Default =
            CompressionConfig(
                maxFileSize = 1024 * 1024, // 1 MB
                maxDimension = 1024,
                quality = 80,
            )

        val ProfilePhoto =
            CompressionConfig(
                maxFileSize = 512 * 1024, // 512 KB
                maxDimension = 512,
                quality = 85,
            )

        val ChatImage =
            CompressionConfig(
                maxFileSize = 2 * 1024 * 1024, // 2 MB
                maxDimension = 1920,
                quality = 75,
            )
    }
}
