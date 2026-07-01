package uz.yalla.carto.capability

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.yalla.carto.CartoBuilder
import uz.yalla.carto.CartoMap
import uz.yalla.carto.capability.framework.Capability
import uz.yalla.carto.capability.framework.CapabilityContext
import uz.yalla.carto.capability.framework.CapabilityHandle
import uz.yalla.carto.capability.framework.CapabilityKey
import uz.yalla.carto.capability.source.CircleSource
import uz.yalla.carto.model.CartoCircle

public class CircleHandle internal constructor() :
    CapabilityHandle,
    CircleSource {
        private val mutableCircles = MutableStateFlow<List<CartoCircle>>(emptyList())
        override val circles: StateFlow<List<CartoCircle>> = mutableCircles.asStateFlow()

        public fun set(next: List<CartoCircle>) {
            mutableCircles.value = next
        }
    }

public object CircleCapability : Capability<CircleHandle> {
    override val key: CapabilityKey<CircleHandle> = CapabilityKey("circles")

    override fun create(context: CapabilityContext): CircleHandle = CircleHandle()
}

public fun CartoBuilder.circles() {
    add(CircleCapability)
}

public val CartoMap.circles: CircleHandle get() = capability(CircleCapability.key)
