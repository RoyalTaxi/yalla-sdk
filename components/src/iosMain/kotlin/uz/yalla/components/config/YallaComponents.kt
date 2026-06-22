package uz.yalla.components.config

import uz.yalla.components.config.YallaComponents.config
import uz.yalla.components.config.YallaComponents.install
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
