package uz.yalla.maps.di

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.contract.LastLocationProvider
import uz.yalla.core.contract.LocationProvider
import uz.yalla.core.contract.MapPreferences
import uz.yalla.core.kind.ThemeKind

interface MapDependencies {
    val mapPreferences: MapPreferences
    val themeType: Flow<ThemeKind>
    val locationProvider: LocationProvider
    val lastLocationProvider: LastLocationProvider?
}
