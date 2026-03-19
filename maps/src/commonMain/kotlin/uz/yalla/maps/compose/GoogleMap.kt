package uz.yalla.maps.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.yalla.core.settings.ThemeKind
import uz.yalla.maps.model.MapProperties
import uz.yalla.maps.model.MapUiSettings

/**
 * Cross-platform Google Maps composable.
 *
 * Wraps the platform-specific Google Maps SDK on Android and iOS behind a single
 * expect/actual declaration. The [content] lambda runs within a [GoogleMapComposable]
 * scope for emitting markers, polylines, and circles.
 *
 * @param modifier Compose [Modifier] applied to the map view.
 * @param cameraPositionState Mutable camera state driving the map viewport.
 * @param properties Map feature toggles (building, indoor, traffic, etc.).
 * @param uiSettings Map gesture and UI control toggles.
 * @param theme Color scheme preference (light, dark, system).
 * @param contentPadding Safe-area padding applied to map controls and logo.
 * @param onMapLoaded Callback invoked when the map tiles have finished loading.
 * @param content Optional composable content emitted within the map overlay layer.
 * @since 0.0.1
 */
@Composable
expect fun GoogleMap(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    properties: MapProperties = MapProperties(),
    uiSettings: MapUiSettings = MapUiSettings(),
    theme: ThemeKind = ThemeKind.System,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onMapLoaded: (() -> Unit)? = null,
    content: (
        @Composable
        @GoogleMapComposable () -> Unit
    )? = null,
)
