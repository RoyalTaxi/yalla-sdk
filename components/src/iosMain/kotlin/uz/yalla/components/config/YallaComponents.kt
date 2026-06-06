package uz.yalla.components.config

object YallaComponents {
    @PublishedApi internal var config: ComponentsConfig? = null

    fun install(config: ComponentsConfig) {
        this.config = config
    }
}

internal fun requireConfig(): ComponentsConfig = YallaComponents.config ?: error("YallaComponents not installed.")
