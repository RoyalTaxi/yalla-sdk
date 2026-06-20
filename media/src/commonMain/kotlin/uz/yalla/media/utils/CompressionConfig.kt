package uz.yalla.media.utils

// TODO(quality, needs-decision): finding #11 — the package should be `util` (singular, matching the
// rest of the SDK) and this `*Config` should live in `config/` next to MediaConfig. Both are breaking
// FQN renames: `uz.yalla.media.utils.compressImage`/`CompressionConfig` are imported by an external
// consumer (YallaClient ProfileRoute.kt:28) that this module cannot edit, so the rename would break
// its compile and change the committed .api/.klib.api. Needs the owner to coordinate the consumer
// move + an accepted ABI change before applying.

/**
 * Bounds for [compressImage]: the output JPEG is downsampled so its longest edge is at most
 * [maxDimension] pixels and its byte size is at most [maxFileSize].
 *
 * @property maxFileSize the upper bound on the encoded JPEG size, in bytes. Must be positive.
 * @property maxDimension the upper bound on the output's longest edge, in pixels. Must be positive.
 * @property quality the JPEG quality ceiling for the encoder's search, in `[MIN_JPEG_QUALITY]..100`.
 */
public data class CompressionConfig(
    val maxFileSize: Int,
    val maxDimension: Int,
    val quality: Int
) {
    init {
        // Floor at MIN_JPEG_QUALITY, not 1: a quality below the search's lower bound makes the
        // binary search a no-op (lo > hi) and silently forces the half-res fallback, so reject it
        // at the API boundary instead.
        require(quality in MIN_JPEG_QUALITY..100) { "quality must be in $MIN_JPEG_QUALITY..100, was $quality" }
        require(maxDimension > 0) { "maxDimension must be positive, was $maxDimension" }
        require(maxFileSize > 0) { "maxFileSize must be positive, was $maxFileSize" }
    }

    public companion object {
        /** General-purpose preset: 1 MB / 1024 px / quality 80. */
        public val Default: CompressionConfig =
            CompressionConfig(
                maxFileSize = 1024 * 1024,
                maxDimension = 1024,
                quality = 80
            )

        /** Avatar preset: 512 KB / 512 px / quality 85. */
        public val ProfilePhoto: CompressionConfig =
            CompressionConfig(
                maxFileSize = 512 * 1024,
                maxDimension = 512,
                quality = 85
            )

        /** Chat-attachment preset: 2 MB / 1920 px / quality 75. */
        public val ChatImage: CompressionConfig =
            CompressionConfig(
                maxFileSize = 2 * 1024 * 1024,
                maxDimension = 1920,
                quality = 75
            )
    }
}
