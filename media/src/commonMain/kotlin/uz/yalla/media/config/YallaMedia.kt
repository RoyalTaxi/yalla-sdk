package uz.yalla.media.config

import kotlin.concurrent.Volatile

public object YallaMedia {
    @Volatile
    private var config: MediaConfig? = null

    public fun install(config: MediaConfig) {
        check(this.config == null) {
            "YallaMedia already installed. Call YallaMedia.install(...) exactly once at app start."
        }
        this.config = config
    }

    public fun current(): MediaConfig =
        config
            ?: error("YallaMedia not installed. Call YallaMedia.install(...) at app start.")
}

internal fun requireMedia(): MediaConfig = YallaMedia.current()
