package uz.yalla.media.config

public class MediaConfig private constructor(
    public val factory: MediaFactory
) {
    public class Builder {
        public var factory: MediaFactory? = null

        public fun build(): MediaConfig =
            MediaConfig(
                factory = requireNotNull(factory) { "MediaConfig.factory required" }
            )
    }
}

public object YallaMedia {
    @PublishedApi internal var config: MediaConfig? = null

    public fun install(config: MediaConfig) {
        this.config = config
    }
}

internal fun requireMedia(): MediaConfig = YallaMedia.config ?: error("YallaMedia not installed.")
