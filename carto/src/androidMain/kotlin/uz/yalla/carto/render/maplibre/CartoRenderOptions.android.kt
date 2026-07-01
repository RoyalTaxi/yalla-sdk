package uz.yalla.carto.render.maplibre

import org.maplibre.compose.map.RenderOptions

internal actual fun cartoRenderOptions(clippable: Boolean): RenderOptions =
    RenderOptions(
        renderMode =
            if (clippable) {
                RenderOptions.RenderMode.TextureView
            } else {
                RenderOptions.RenderMode.SurfaceView
            }
    )
