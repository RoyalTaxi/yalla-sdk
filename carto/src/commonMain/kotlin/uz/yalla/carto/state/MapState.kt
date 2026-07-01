package uz.yalla.carto.state

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import uz.yalla.carto.camera.CameraIntent
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.model.CartoCircle
import uz.yalla.carto.model.CartoMarker
import uz.yalla.carto.model.CartoRoute

public interface MapState : MapSink {
    public val markerSources: List<StateFlow<List<CartoMarker>>>
    public val lineSources: List<StateFlow<List<CartoRoute>>>
    public val circleSources: List<StateFlow<List<CartoCircle>>>
    public val poses: SharedFlow<MarkerPose>

    public val cameraIntents: SharedFlow<CameraIntent>
    public val padding: StateFlow<PaddingValues>
    public val isDark: StateFlow<Boolean>
    public val style: StateFlow<MapStyle>
    public val initialCamera: CameraView
    public val clippable: Boolean
}
