package uz.yalla.carto.capability.source

import kotlinx.coroutines.flow.StateFlow
import uz.yalla.carto.model.CartoRoute

public interface LineSource {
    public val lines: StateFlow<List<CartoRoute>>
}
