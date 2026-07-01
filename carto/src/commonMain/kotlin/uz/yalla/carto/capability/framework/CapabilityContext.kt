package uz.yalla.carto.capability.framework

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import uz.yalla.carto.camera.CameraArbiter
import uz.yalla.carto.state.MapEvent

public class CapabilityContext internal constructor(
    public val scope: CoroutineScope,
    public val events: SharedFlow<MapEvent>,
    public val frames: SharedFlow<Long>,
    public val arbiter: CameraArbiter,
    private val resolve: (CapabilityKey<*>) -> CapabilityHandle?
) {
    public fun <H : CapabilityHandle> handle(key: CapabilityKey<H>): () -> H? =
        {
            @Suppress("UNCHECKED_CAST")
            resolve(key) as? H
        }
}
