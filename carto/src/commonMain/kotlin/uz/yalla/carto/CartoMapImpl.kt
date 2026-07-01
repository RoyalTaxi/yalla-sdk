package uz.yalla.carto

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.yalla.carto.camera.CameraArbiter
import uz.yalla.carto.camera.CameraIntent
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.capability.framework.Capability
import uz.yalla.carto.capability.framework.CapabilityContext
import uz.yalla.carto.capability.framework.CapabilityHandle
import uz.yalla.carto.capability.framework.CapabilityKey
import uz.yalla.carto.model.CartoCircle
import uz.yalla.carto.model.CartoMarker
import uz.yalla.carto.model.CartoRoute
import uz.yalla.carto.state.MapEvent
import uz.yalla.carto.state.MapState
import uz.yalla.carto.state.MapStyle
import uz.yalla.carto.state.MarkerPose
import uz.yalla.carto.state.aggregateSources

internal class CartoMapImpl(
    parentScope: CoroutineScope,
    override val provider: CartoProvider,
    override val initialCamera: CameraView,
    capabilities: LinkedHashMap<CapabilityKey<*>, Capability<*>>,
    initialStyle: MapStyle,
    initialDark: Boolean,
    override val clippable: Boolean
) : CartoMap,
    MapState {
    private val scope = CoroutineScope(parentScope.coroutineContext + SupervisorJob(parentScope.coroutineContext[Job]))
    private val handles = LinkedHashMap<CapabilityKey<*>, CapabilityHandle>()

    private val mutableEvents =
        MutableSharedFlow<MapEvent>(extraBufferCapacity = 32, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val mutableFrames =
        MutableSharedFlow<Long>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val mutableIntents =
        MutableSharedFlow<CameraIntent>(extraBufferCapacity = 16, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val mutableCamera = MutableStateFlow(initialCamera)
    private val mutableReady = MutableStateFlow(false)
    private val mutablePadding = MutableStateFlow(PaddingValues())
    private val mutableDark = MutableStateFlow(initialDark)
    private val mutableStyle = MutableStateFlow(initialStyle)

    override val arbiter: CameraArbiter = CameraArbiter { intent -> mutableIntents.emit(intent) }
    override val camera: StateFlow<CameraView> = mutableCamera.asStateFlow()
    override val isReady: StateFlow<Boolean> = mutableReady.asStateFlow()
    override val state: MapState get() = this

    override val cameraIntents: SharedFlow<CameraIntent> = mutableIntents.asSharedFlow()
    override val padding: StateFlow<PaddingValues> = mutablePadding.asStateFlow()
    override val isDark: StateFlow<Boolean> = mutableDark.asStateFlow()
    override val style: StateFlow<MapStyle> = mutableStyle.asStateFlow()

    override val markerSources: List<StateFlow<List<CartoMarker>>>
    override val lineSources: List<StateFlow<List<CartoRoute>>>
    override val circleSources: List<StateFlow<List<CartoCircle>>>
    override val poses: SharedFlow<MarkerPose>

    init {
        val context =
            CapabilityContext(
                scope,
                mutableEvents.asSharedFlow(),
                mutableFrames.asSharedFlow(),
                arbiter
            ) { handles[it] }
        capabilities.forEach { (key, capability) -> handles[key] = capability.create(context) }
        val sources = aggregateSources(handles.values)
        markerSources = sources.markerSources
        lineSources = sources.lineSources
        circleSources = sources.circleSources
        poses = sources.poses
    }

    @Suppress("UNCHECKED_CAST")
    override fun <H : CapabilityHandle> capability(key: CapabilityKey<H>): H =
        (handles[key] ?: error("Capability ${key.name} was not declared on this map")) as H

    override fun report(event: MapEvent) {
        mutableEvents.tryEmit(event)
    }

    override fun reportCamera(view: CameraView) {
        mutableCamera.value = view
    }

    override fun reportReady(ready: Boolean) {
        mutableReady.value = ready
    }

    override fun reportFrame(frameTimeMs: Long) {
        mutableFrames.tryEmit(frameTimeMs)
    }

    override fun setPadding(padding: PaddingValues) {
        mutablePadding.value = padding
    }

    override fun setColorScheme(isDark: Boolean) {
        mutableDark.value = isDark
    }

    override fun close() {
        scope.cancel()
    }
}
