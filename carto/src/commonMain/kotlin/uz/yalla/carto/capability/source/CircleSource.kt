package uz.yalla.carto.capability.source

import kotlinx.coroutines.flow.StateFlow
import uz.yalla.carto.model.CartoCircle

public interface CircleSource {
    public val circles: StateFlow<List<CartoCircle>>
}
