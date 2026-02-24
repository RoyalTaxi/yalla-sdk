package uz.yalla.foundation.location

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.contract.AppPreferences
import uz.yalla.core.contract.MapPreferences
import uz.yalla.core.kind.MapKind

class MapPreferencesAdapter(
    private val appPreferences: AppPreferences
) : MapPreferences {
    override val mapKind: Flow<MapKind>
        get() = appPreferences.mapType

    override fun setMapKind(value: MapKind) {
        appPreferences.setMapType(value)
    }
}
