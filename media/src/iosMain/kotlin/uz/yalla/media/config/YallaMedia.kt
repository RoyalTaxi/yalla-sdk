package uz.yalla.media.config

class MediaConfig private constructor(
    val factory: MediaFactory
) {
    class Builder {
        var factory: MediaFactory? = null

        fun build() = MediaConfig(
            factory = requireNotNull(factory) { "MediaConfig.factory required" }
        )
    }
}

object YallaMedia {
    @PublishedApi internal var config: MediaConfig? = null

    fun install(config: MediaConfig) {
        this.config = config
    }
}

internal fun requireMedia(): MediaConfig = YallaMedia.config ?: error("YallaMedia not installed.")
