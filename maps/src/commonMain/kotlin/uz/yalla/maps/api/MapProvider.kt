package uz.yalla.maps.api

import uz.yalla.core.settings.MapKind
import uz.yalla.maps.api.model.MapCapabilities
import uz.yalla.maps.api.model.MapStyle

/**
 * Factory for creating map composables and controllers for a specific map backend.
 *
 * Each implementation (Google, Libre) exposes its capabilities, style configuration,
 * and factory methods for the three map tiers: lite, extended, and static.
 * The host application typically consumes this through
 * [SwitchingMapProvider][uz.yalla.maps.provider.SwitchingMapProvider], which delegates
 * to the appropriate backend at runtime.
 *
 * ## Usage
 *
 * ```kotlin
 * val provider: MapProvider = koinInject()
 * val controller = provider.createController()
 * provider.createLiteMap().Content(controller = controller)
 * ```
 *
 * @since 0.0.1
 */
interface MapProvider {
    /**
     * The kind of map backend this provider represents (Google or Libre).
     *
     * @since 0.0.1
     */
    val type: MapKind

    /**
     * Feature capabilities of this map backend.
     *
     * @since 0.0.1
     */
    val capabilities: MapCapabilities

    /**
     * Light/dark style URLs for this map backend.
     *
     * @since 0.0.1
     */
    val style: MapStyle

    /**
     * Creates a lite map composable suitable for simple pin-on-map use cases.
     *
     * @return A new [LiteMap] instance.
     * @since 0.0.1
     */
    fun createLiteMap(): LiteMap

    /**
     * Creates an extended map composable with route display, location markers, and custom content.
     *
     * @return A new [ExtendedMap] instance.
     * @since 0.0.1
     */
    fun createExtendedMap(): ExtendedMap

    /**
     * Creates a static (non-interactive) map composable for displaying routes and locations.
     *
     * @return A new [StaticMap] instance.
     * @since 0.0.1
     */
    fun createStaticMap(): StaticMap

    /**
     * Creates a controller for programmatic camera and marker manipulation.
     *
     * @return A new [MapController] instance.
     * @since 0.0.1
     */
    fun createController(): MapController
}
