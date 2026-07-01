package uz.yalla.carto.state

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import uz.yalla.carto.model.CartoCircle
import uz.yalla.carto.model.CartoMarker
import uz.yalla.carto.model.CartoRoute

internal data class MapSources(
    val markerSources: List<StateFlow<List<CartoMarker>>>,
    val lineSources: List<StateFlow<List<CartoRoute>>>,
    val circleSources: List<StateFlow<List<CartoCircle>>>,
    val poses: SharedFlow<MarkerPose>
)
