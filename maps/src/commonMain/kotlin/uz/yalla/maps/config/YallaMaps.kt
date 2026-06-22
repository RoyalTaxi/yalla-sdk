package uz.yalla.maps.config

import kotlin.concurrent.Volatile

public object YallaMaps {
    @Volatile
    @PublishedApi
    internal var config: MapsConfig? = null

    public fun install(config: MapsConfig) {
        this.config = config
    }

    public fun current(): MapsConfig =
        config
            ?: error("YallaMaps not installed. Call YallaMaps.install(...) at app start.")
}

internal fun requireMaps(): MapsConfig = YallaMaps.current()
