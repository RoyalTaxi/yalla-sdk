package uz.yalla.carto.capability.source

import kotlinx.coroutines.flow.StateFlow
import uz.yalla.carto.model.CartoMarker

public interface MarkerSource {
    public val markers: StateFlow<List<CartoMarker>>
}
