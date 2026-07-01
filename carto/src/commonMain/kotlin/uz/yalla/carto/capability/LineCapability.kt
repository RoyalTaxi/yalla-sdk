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
import uz.yalla.carto.capability.source.LineSource
import uz.yalla.carto.model.CartoRoute

public class LineHandle internal constructor() :
    CapabilityHandle,
    LineSource {
        private val mutableLines = MutableStateFlow<List<CartoRoute>>(emptyList())
        override val lines: StateFlow<List<CartoRoute>> = mutableLines.asStateFlow()

        public fun set(next: List<CartoRoute>) {
            mutableLines.value = next
        }
    }

public object LineCapability : Capability<LineHandle> {
    override val key: CapabilityKey<LineHandle> = CapabilityKey("lines")

    override fun create(context: CapabilityContext): LineHandle = LineHandle()
}

public fun CartoBuilder.lines() {
    add(LineCapability)
}

public val CartoMap.lines: LineHandle get() = capability(LineCapability.key)
