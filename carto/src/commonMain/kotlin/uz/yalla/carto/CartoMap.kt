package uz.yalla.carto

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.flow.StateFlow
import uz.yalla.carto.camera.CameraArbiter
import uz.yalla.carto.camera.CameraIntent
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.capability.framework.CapabilityHandle
import uz.yalla.carto.capability.framework.CapabilityKey
import uz.yalla.carto.state.MapState
import uz.yalla.core.geo.GeoPoint

public interface CartoMap {
    public val provider: CartoProvider
    public val arbiter: CameraArbiter
    public val camera: StateFlow<CameraView>
    public val isReady: StateFlow<Boolean>
    public val state: MapState

    public fun <H : CapabilityHandle> capability(key: CapabilityKey<H>): H

    public fun setPadding(padding: PaddingValues)

    public fun setColorScheme(isDark: Boolean)

    public fun close()
}

public suspend fun CartoMap.moveCamera(
    point: GeoPoint,
    zoom: Float? = null,
    animate: Boolean = true
) {
    arbiter.submit(
        CameraIntent(
            target = point,
            zoom = zoom ?: camera.value.zoom,
            animate = animate
        )
    )
}
