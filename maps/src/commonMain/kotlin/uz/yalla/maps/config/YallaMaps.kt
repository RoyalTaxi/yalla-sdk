package uz.yalla.maps.config

import uz.yalla.maps.config.YallaMaps.current
import uz.yalla.maps.config.YallaMaps.install
import kotlin.concurrent.Volatile

/**
 * Process-wide entry point holding the installed [MapsConfig]. Call [install] exactly once at app
 * start, before any map is created; [current] reads it back. The backing field is `@Volatile` so a
 * read on a non-init thread (e.g. composition) observes the [install] write under the JVM/Native
 * memory model. The config carries the live [MapsConfig.locationProvider] (a PII source), so do not
 * re-install at runtime.
 */
public object YallaMaps {
    @Volatile
    @PublishedApi
    internal var config: MapsConfig? = null

    /** Installs the global [MapsConfig]. Intended to be called once at app start. */
    public fun install(config: MapsConfig) {
        this.config = config
    }

    /** Returns the installed [MapsConfig], or throws if [install] has not been called. */
    public fun current(): MapsConfig =
        config
            ?: error("YallaMaps not installed. Call YallaMaps.install(...) at app start.")
}

internal fun requireMaps(): MapsConfig = YallaMaps.current()
