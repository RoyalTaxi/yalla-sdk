package uz.yalla.components.config

import kotlin.concurrent.Volatile

/**
 * Process-wide holder for the platform [ComponentsConfig]. Call [install] exactly once, on the main
 * thread, before the first composition; the iOS render path reads [config] on every render.
 */
public object YallaComponents {
    // @Volatile: config is published from install() and read on the render path; ensure readers see
    // the installed instance rather than a stale null (M11).
    @Volatile @PublishedApi internal var config: ComponentsConfig? = null

    public fun install(config: ComponentsConfig) {
        this.config = config
    }
}

// TODO(quality, needs-decision): M2 — this is `public` on iOS but `internal` on Android, so it leaks
//  into the exported Swift surface (components.klib.api) for no consumer outside the module. Making it
//  `internal` (to match Android) is a BREAKING removal from the committed klib api; moving the whole
//  file to commonMain (M3) would also resolve the drift. Blocked on owner sign-off for the breaking
//  visibility change / commonMain move.
public fun requireConfig(): ComponentsConfig = YallaComponents.config ?: error("YallaComponents not installed.")
