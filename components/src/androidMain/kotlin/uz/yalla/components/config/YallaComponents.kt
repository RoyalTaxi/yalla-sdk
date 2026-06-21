package uz.yalla.components.config

import uz.yalla.components.config.YallaComponents.config
import uz.yalla.components.config.YallaComponents.install
import kotlin.concurrent.Volatile

/**
 * Process-wide holder for the platform [ComponentsConfig]. Call [install] exactly once, on the main
 * thread, before the first composition; the render path reads [config] on every render.
 */
public object YallaComponents {
    // @Volatile: config is published from install() and read on the render path; ensure readers see
    // the installed instance rather than a stale null (M11).
    @Volatile
    @PublishedApi
    internal var config: ComponentsConfig? = null

    public fun install(config: ComponentsConfig) {
        this.config = config
    }
}

internal fun requireConfig(): ComponentsConfig = YallaComponents.config ?: error("YallaComponents not installed.")
