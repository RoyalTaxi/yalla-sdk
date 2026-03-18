package uz.yalla.platform

/**
 * Marker interface for platform-specific configuration.
 *
 * Each platform provides its own implementation:
 * - iOS: `IosPlatformConfig` with native component factories
 * - Android: `AndroidPlatformConfig` (no factories needed)
 *
 * @since 0.0.1
 */
interface PlatformConfig

/**
 * Central entry point for platform module initialization.
 *
 * Call [install] once at app startup before using any native platform component.
 *
 * @since 0.0.1
 */
object YallaPlatform {
    @PublishedApi
    internal var config: PlatformConfig? = null

    /** Whether [install] has been called. */
    val isInstalled: Boolean get() = config != null

    /** Register platform configuration. Must be called before using platform components. */
    fun install(config: PlatformConfig) {
        this.config = config
    }

    /** Retrieve typed config or throw with clear installation instructions. */
    @PublishedApi
    internal inline fun <reified T : PlatformConfig> requireConfig(): T =
        (config as? T) ?: error(
            "YallaPlatform not installed. Call YallaPlatform.install() " +
                "in your app's entry point before using platform components."
        )

    /** Reset config — for testing only. Prevents global state pollution across tests. */
    fun reset() { config = null }
}
