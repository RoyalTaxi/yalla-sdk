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

    /**
     * Whether [install] has been called successfully.
     *
     * Can be used to guard platform component usage in environments where initialization
     * timing is uncertain (e.g., Compose previews).
     */
    val isInstalled: Boolean get() = config != null

    /**
     * Registers the platform-specific [config].
     *
     * Must be called once at app startup before any platform component is used.
     * On Android, use [installAndroid][uz.yalla.platform.config.installAndroid].
     * On iOS, build an [IosPlatformConfig][uz.yalla.platform.config.IosPlatformConfig]
     * and pass it here.
     *
     * @param config The platform configuration to register.
     * @see PlatformConfig
     */
    fun install(config: PlatformConfig) {
        this.config = config
    }

    /**
     * Retrieves the registered config cast to [T], or throws with clear installation instructions.
     *
     * @param T The expected platform config type.
     * @return The registered config cast to [T].
     * @throws IllegalStateException if [install] has not been called or the config is the wrong type.
     */
    @PublishedApi
    internal inline fun <reified T : PlatformConfig> requireConfig(): T =
        (config as? T) ?: error(
            "YallaPlatform not installed. Call YallaPlatform.install() " +
                "in your app's entry point before using platform components."
        )

    /**
     * Resets the platform configuration to `null`.
     *
     * **Testing only.** Call between tests to prevent global state pollution.
     * Do not call in production code.
     */
    fun reset() {
        config = null
    }
}
