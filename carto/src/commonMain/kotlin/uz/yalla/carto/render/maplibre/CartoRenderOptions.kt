package uz.yalla.carto.render.maplibre

import org.maplibre.compose.map.RenderOptions

internal expect fun cartoRenderOptions(clippable: Boolean): RenderOptions
