package uz.yalla.carto.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import uz.yalla.carto.CartoBuilder
import uz.yalla.carto.CartoMap
import uz.yalla.carto.CartoProvider
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.composeCarto
import uz.yalla.carto.state.MapStyle

@Composable
public fun rememberCartoMap(
    provider: CartoProvider,
    style: MapStyle = MapStyle.PlatformDefault,
    isDark: Boolean = false,
    initialCamera: () -> CameraView = { CameraView.DEFAULT },
    clippable: Boolean = false,
    builder: CartoBuilder.() -> Unit
): CartoMap {
    val scope = rememberCoroutineScope()
    val map = remember(provider) { composeCarto(scope, provider, style, isDark, initialCamera(), clippable, builder) }
    LaunchedEffect(isDark) { map.setColorScheme(isDark) }
    DisposableEffect(map) { onDispose { map.close() } }
    return map
}
