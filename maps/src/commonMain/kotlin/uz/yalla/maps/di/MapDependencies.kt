package uz.yalla.maps.di

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.contract.location.LocationProvider
import uz.yalla.core.contract.preferences.InterfacePreferences
import uz.yalla.core.settings.ThemeKind

/**
 * External dependencies required by the maps module.
 *
 * The host application must provide an implementation of this interface
 * and register it in the Koin container so that map composables can
 * access user preferences and location data.
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
}
