package uz.yalla.components.config

import kotlin.concurrent.Volatile

public object YallaComponents {
    @Volatile
    @PublishedApi
    internal var config: ComponentsConfig? = null

    public fun install(config: ComponentsConfig) {
        this.config = config
    }
}

internal fun requireConfig(): ComponentsConfig = YallaComponents.config ?: error("YallaComponents not installed.")
