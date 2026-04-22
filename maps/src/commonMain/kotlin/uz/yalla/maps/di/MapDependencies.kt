package uz.yalla.maps.di

import kotlinx.coroutines.CoroutineScope
import uz.yalla.core.contract.location.LocationProvider
import uz.yalla.core.contract.preferences.InterfacePreferences

/**
 * External dependencies required by the maps module.
 *
 * The host application must provide an implementation of this interface
 * and register it in the Koin container so that map composables can
 * access user preferences and location data.
 *
 * ## Scope ownership (ADR-018)
 *
 * `scope` follows the caller-owned lifecycle pattern established in ADR-011 and ADR-013.
 * Provide a process-lifetime [CoroutineScope] (e.g., `CoroutineScope(SupervisorJob())`).
 * When that scope is cancelled, all map preference-observation coroutines stop automatically.
 *
 * @since 0.0.1
 */
interface MapDependencies {
    /**
     * User interface preferences including map provider choice and theme.
     *
     * @since 0.0.1
     */
    val interfacePreferences: InterfacePreferences

    /**
     * Location provider for user position tracking.
     *
     * @since 0.0.1
     */
    val locationProvider: LocationProvider

    /**
     * Process-lifetime coroutine scope for map preference observation.
     *
     * Cancelling this scope stops all coroutines inside [SwitchingMapProvider] and
     * any [SwitchingMapController] instances created by it. Mirrors the pattern used
     * by `LocationManager` (ADR-013) and `createHttpClient` (ADR-011).
     *
     * @since 0.0.1
     */
    val scope: CoroutineScope
}
