package uz.yalla.carto.state

import kotlinx.coroutines.flow.MutableSharedFlow
import uz.yalla.carto.capability.framework.CapabilityHandle
import uz.yalla.carto.capability.source.CircleSource
import uz.yalla.carto.capability.source.LineSource
import uz.yalla.carto.capability.source.MarkerSource
import uz.yalla.carto.capability.source.PoseSource

internal fun aggregateSources(handles: Collection<CapabilityHandle>): MapSources =
    MapSources(
        markerSources = handles.filterIsInstance<MarkerSource>().map { it.markers },
        lineSources = handles.filterIsInstance<LineSource>().map { it.lines },
        circleSources = handles.filterIsInstance<CircleSource>().map { it.circles },
        poses = handles.filterIsInstance<PoseSource>().firstOrNull()?.poses ?: MutableSharedFlow<MarkerPose>()
    )
