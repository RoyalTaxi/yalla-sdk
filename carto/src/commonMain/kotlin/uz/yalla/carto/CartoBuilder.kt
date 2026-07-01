package uz.yalla.carto

import uz.yalla.carto.capability.framework.Capability
import uz.yalla.carto.capability.framework.CapabilityHandle
import uz.yalla.carto.capability.framework.CapabilityKey

public class CartoBuilder internal constructor() {
    internal val capabilities = LinkedHashMap<CapabilityKey<*>, Capability<*>>()

    public fun <H : CapabilityHandle> add(capability: Capability<H>) {
        capabilities[capability.key] = capability
    }
}
