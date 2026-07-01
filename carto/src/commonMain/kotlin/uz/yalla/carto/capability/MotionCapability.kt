package uz.yalla.carto.capability

import uz.yalla.carto.CartoBuilder
import uz.yalla.carto.CartoMap
import uz.yalla.carto.capability.framework.Capability
import uz.yalla.carto.capability.framework.CapabilityContext
import uz.yalla.carto.capability.framework.CapabilityKey

public class MotionCapability : Capability<MotionHandle> {
    override val key: CapabilityKey<MotionHandle> get() = Key

    override fun create(context: CapabilityContext): MotionHandle {
        val handle =
            MotionHandle(
                scope = context.scope,
                frames = context.frames
            )
        val resolveMarkers = context.handle(MarkerCapability.key)
        handle.bindMarkers { resolveMarkers()?.flatMarkers }
        return handle
    }

    public companion object {
        public val Key: CapabilityKey<MotionHandle> = CapabilityKey("motion")
    }
}

public fun CartoBuilder.motion() {
    add(MotionCapability())
}

public val CartoMap.motion: MotionHandle get() = capability(MotionCapability.Key)
