package uz.yalla.maps.config

object YallaMaps {
    @PublishedApi internal var config: MapsConfig? = null

    fun install(config: MapsConfig) {
        this.config = config
    }

    fun current(): MapsConfig = config
        ?: error("YallaMaps not installed. Call YallaMaps.install(...) at app start.")
}

internal fun requireMaps(): MapsConfig = YallaMaps.current()
