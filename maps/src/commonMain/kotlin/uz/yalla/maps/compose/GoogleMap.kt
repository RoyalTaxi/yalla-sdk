package uz.yalla.maps.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.yalla.core.kind.ThemeKind
import uz.yalla.maps.model.MapProperties
import uz.yalla.maps.model.MapUiSettings

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
