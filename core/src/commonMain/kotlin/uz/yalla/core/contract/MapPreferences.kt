package uz.yalla.core.contract

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.kind.MapKind

interface MapPreferences {
    val mapKind: Flow<MapKind>

    fun setMapKind(value: MapKind)
}
