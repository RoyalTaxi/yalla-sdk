package uz.yalla.carto.capability.source

import kotlinx.coroutines.flow.SharedFlow
import uz.yalla.carto.state.MarkerPose

public interface PoseSource {
    public val poses: SharedFlow<MarkerPose>
}
