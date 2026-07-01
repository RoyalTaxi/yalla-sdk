package uz.yalla.carto.capability

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.yalla.carto.CartoBuilder
import uz.yalla.carto.CartoMap
import uz.yalla.carto.capability.framework.Capability
import uz.yalla.carto.capability.framework.CapabilityContext
import uz.yalla.carto.capability.framework.CapabilityHandle
import uz.yalla.carto.capability.framework.CapabilityKey
import uz.yalla.carto.capability.source.MarkerSource
import uz.yalla.carto.model.CartoMarker

public class MarkerHandle internal constructor(
    scope: CoroutineScope
) : CapabilityHandle,
    MarkerSource {
    private val mutableMarkers = MutableStateFlow<List<CartoMarker>>(emptyList())
    override val markers: StateFlow<List<CartoMarker>> = mutableMarkers.asStateFlow()

    public val flatMarkers: StateFlow<List<CartoMarker>> =
        markers.map { list -> list.filter { it.flat } }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    public fun set(next: List<CartoMarker>) {
        mutableMarkers.value = next
    }
}

public object MarkerCapability : Capability<MarkerHandle> {
    override val key: CapabilityKey<MarkerHandle> = CapabilityKey("markers")

    override fun create(context: CapabilityContext): MarkerHandle = MarkerHandle(context.scope)
}

public fun CartoBuilder.markers() {
    add(MarkerCapability)
}

public val CartoMap.markers: MarkerHandle get() = capability(MarkerCapability.key)
