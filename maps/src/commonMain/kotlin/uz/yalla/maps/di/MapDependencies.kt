package uz.yalla.maps.di

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.contract.location.LocationProvider
import uz.yalla.core.contract.preferences.InterfacePreferences
import uz.yalla.core.kind.ThemeKind

interface MapDependencies {
    val interfacePreferences: InterfacePreferences
    val locationProvider: LocationProvider
}
