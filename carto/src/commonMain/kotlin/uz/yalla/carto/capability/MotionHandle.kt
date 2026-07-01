package uz.yalla.carto.capability

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.yalla.carto.capability.framework.CapabilityHandle
import uz.yalla.carto.capability.source.PoseSource
import uz.yalla.carto.model.CartoMarker
import uz.yalla.carto.model.MapAnchor
import uz.yalla.carto.motion.DriverMotionModel
import uz.yalla.carto.state.MarkerPose
import kotlin.math.abs
import kotlin.time.TimeSource

public class MotionHandle internal constructor(
    private val scope: CoroutineScope,
    private val frames: SharedFlow<Long>
) : CapabilityHandle,
    PoseSource {
    private val models = HashMap<String, DriverMotionModel>()
    private val icons = HashMap<String, Pair<String?, MapAnchor>>()
    private val lastPose = HashMap<String, Triple<Double, Double, Float>>()
    private var frameJob: Job? = null
    private val origin = TimeSource.Monotonic.markNow()

    private val mutablePoses =
        MutableSharedFlow<MarkerPose>(extraBufferCapacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val poses: SharedFlow<MarkerPose> = mutablePoses.asSharedFlow()

    internal fun bindMarkers(resolve: () -> StateFlow<List<CartoMarker>>?) {
        flow {
            val flat = resolve() ?: error("motion capability requires the markers capability")
            emitAll(flat)
        }.onEach { onFlatMarkers(it) }.launchIn(scope)
    }

    private fun onFlatMarkers(markers: List<CartoMarker>) {
        val live = markers.associateBy { it.id }
        (models.keys - live.keys).toList().forEach { remove(it) }
        markers.forEach { marker ->
            val model = models.getOrPut(marker.id) { DriverMotionModel() }
            icons[marker.id] = marker.iconKey to marker.anchor
            model.push(marker.point, marker.routeHeading, marker.rotation, now())
        }
        ensureRunning()
    }

    private fun remove(id: String) {
        models.remove(id)
        icons.remove(id)
        lastPose.remove(id)
        if (models.isEmpty()) stop()
    }

    private fun now(): Long = origin.elapsedNow().inWholeMilliseconds

    private fun ensureRunning() {
        if (frameJob != null || models.isEmpty()) return
        frameJob = frames.onEach { tick(now()) }.launchIn(scope)
    }

    private fun stop() {
        frameJob?.cancel()
        frameJob = null
    }

    private fun tick(now: Long) {
        if (models.isEmpty()) {
            stop()
            return
        }
        for ((id, model) in models) {
            advance(id, model, now)
        }
    }

    private fun advance(
        id: String,
        model: DriverMotionModel,
        now: Long
    ) {
        val pose = model.sample(now)
        val previous = lastPose[id]
        val changed =
            previous == null ||
                abs(previous.first - pose.point.lat) >= 1e-6 ||
                abs(previous.second - pose.point.lng) >= 1e-6 ||
                abs(previous.third - pose.bearing) >= 0.1f
        if (!changed) return
        lastPose[id] = Triple(pose.point.lat, pose.point.lng, pose.bearing)
        val icon = icons[id]
        mutablePoses.tryEmit(MarkerPose(id, pose.point, pose.bearing, icon?.first, icon?.second ?: MapAnchor.CENTER))
    }
}
