package uz.yalla.carto

import kotlinx.coroutines.CoroutineScope
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.state.MapStyle

public fun composeCarto(
    scope: CoroutineScope,
    provider: CartoProvider,
    style: MapStyle = MapStyle.PlatformDefault,
    isDark: Boolean = false,
    initialCamera: CameraView = CameraView.DEFAULT,
    clippable: Boolean = false,
    block: CartoBuilder.() -> Unit
): CartoMap {
    val builder = CartoBuilder().apply(block)
    return CartoMapImpl(scope, provider, initialCamera, builder.capabilities, style, isDark, clippable)
}
