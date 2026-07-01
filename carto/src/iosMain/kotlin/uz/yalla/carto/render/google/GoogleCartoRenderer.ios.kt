package uz.yalla.carto.render.google

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import uz.yalla.carto.camera.CameraIntent
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.render.RECENTER_DURATION_MS
import uz.yalla.carto.state.MapEvent
import uz.yalla.carto.state.MapState
import uz.yalla.carto.state.MapStyle
import uz.yalla.core.geo.GeoPoint

@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun GoogleCartoRenderer(
    factory: GoogleIosRendererFactory,
    state: MapState,
    icons: Map<String, Painter>,
    modifier: Modifier
) {
    val renderer =
        remember(factory, state) {
            factory.create(state.initialCamera.toGoogleCamera(), CameraView.ZOOM_MIN, CameraView.ZOOM_MAX)
        }
    val ready = remember(renderer) { MutableStateFlow(false) }
    val camera = remember(renderer) { MutableStateFlow(state.initialCamera.toGoogleCamera()) }
    DisposableEffect(renderer, state) {
        renderer.setDelegate(GoogleIosStateDelegate(state, ready, camera))
        onDispose {
            renderer.setDelegate(null)
            renderer.dispose()
            state.reportReady(false)
        }
    }
    UIKitView(
        factory = { renderer.view },
        modifier = modifier,
        properties = UIKitInteropProperties(interactionMode = UIKitInteropInteractionMode.NonCooperative)
    )
    GoogleIosCameraController(renderer, state.cameraIntents, ready, camera)
    GoogleIosPaddingRecenter(renderer, state, ready)
    GoogleIosAppearance(renderer, state)
    GoogleIosIcons(renderer, icons)
    GoogleIosContent(renderer, state)
}

private fun CameraView.toGoogleCamera(): GoogleIosCamera = GoogleIosCamera(target.lat, target.lng, zoom, bearing, tilt)

private class GoogleIosStateDelegate(
    private val state: MapState,
    private val ready: MutableStateFlow<Boolean>,
    private val camera: MutableStateFlow<GoogleIosCamera>
) : GoogleIosMapDelegate {
    override fun onReady() {
        state.reportReady(true)
        ready.value = true
    }

    override fun onCameraMove(lat: Double, lng: Double, zoom: Float, bearing: Float, tilt: Float, isByUser: Boolean) {
        state.report(MapEvent.CameraMoved(report(lat, lng, zoom, bearing, tilt), isByUser))
    }

    override fun onCameraIdle(lat: Double, lng: Double, zoom: Float, bearing: Float, tilt: Float, isByUser: Boolean) {
        state.report(MapEvent.CameraIdle(report(lat, lng, zoom, bearing, tilt), isByUser))
    }

    override fun onMapTapped(lat: Double, lng: Double) {
        state.report(MapEvent.MapTapped(GeoPoint(lat, lng)))
    }

    override fun onMapLongPressed(lat: Double, lng: Double) {
        state.report(MapEvent.MapLongPressed(GeoPoint(lat, lng)))
    }

    private fun report(lat: Double, lng: Double, zoom: Float, bearing: Float, tilt: Float): CameraView {
        camera.value = GoogleIosCamera(lat, lng, zoom, bearing, tilt)
        val view = CameraView(GeoPoint(lat, lng), zoom, bearing, tilt)
        state.reportCamera(view)
        return view
    }
}

@Composable
private fun GoogleIosCameraController(
    renderer: GoogleIosMapRenderer,
    intents: SharedFlow<CameraIntent>,
    ready: StateFlow<Boolean>,
    camera: StateFlow<GoogleIosCamera>
) {
    val layoutDirection = LocalLayoutDirection.current
    LaunchedEffect(renderer, intents, ready, layoutDirection) {
        var job: Job? = null
        intents.collect { intent ->
            job?.cancel()
            job =
                launch {
                    ready.first { it }
                    applyIntent(renderer, intent, camera.value, layoutDirection)
                }
        }
    }
}

private fun applyIntent(
    renderer: GoogleIosMapRenderer,
    intent: CameraIntent,
    current: GoogleIosCamera,
    layoutDirection: LayoutDirection
) {
    val bounds = intent.bounds
    if (bounds != null && bounds.size >= 2) {
        renderer.fitBounds(bounds, boundsPaddingPt(intent.boundsPadding, layoutDirection), CameraView.FIT_ZOOM_MAX, intent.animate)
        return
    }
    val target = intent.target ?: return
    val next = GoogleIosCamera(target.lat, target.lng, intent.zoom ?: current.zoom, current.bearing, current.tilt)
    if (intent.animate) renderer.animateCamera(next, intent.durationMs) else renderer.setCamera(next)
}

private fun boundsPaddingPt(
    padding: PaddingValues,
    layoutDirection: LayoutDirection
): Double =
    maxOf(
        padding.calculateLeftPadding(layoutDirection).value,
        padding.calculateTopPadding().value,
        padding.calculateRightPadding(layoutDirection).value,
        padding.calculateBottomPadding().value
    ).toDouble()

@Composable
private fun GoogleIosPaddingRecenter(
    renderer: GoogleIosMapRenderer,
    state: MapState,
    ready: StateFlow<Boolean>
) {
    val isReady by ready.collectAsStateWithLifecycle()
    val padding by state.padding.collectAsStateWithLifecycle()
    val bottomPt = padding.calculateBottomPadding().value.toDouble()
    var seeded by remember(renderer) { mutableStateOf(false) }
    LaunchedEffect(bottomPt, isReady) {
        if (!isReady) return@LaunchedEffect
        renderer.setPadding(bottomPt, if (seeded) RECENTER_DURATION_MS else 0)
        if (bottomPt > 0.0) seeded = true
    }
}

@Composable
private fun GoogleIosAppearance(
    renderer: GoogleIosMapRenderer,
    state: MapState
) {
    val isDark by state.isDark.collectAsStateWithLifecycle()
    val style by state.style.collectAsStateWithLifecycle()
    LaunchedEffect(renderer, isDark, style) {
        renderer.setAppearance(isDark, styleJsonOf(style, isDark))
    }
}

private fun styleJsonOf(
    style: MapStyle,
    isDark: Boolean
): String? =
    when (style) {
        is MapStyle.InlineJson -> if (isDark) style.darkJson else style.lightJson
        else -> null
    }

@Composable
private fun GoogleIosIcons(
    renderer: GoogleIosMapRenderer,
    icons: Map<String, Painter>
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    LaunchedEffect(renderer, icons, density, layoutDirection) {
        renderer.setIcons(rasterizeIcons(icons, density, layoutDirection))
    }
}

@Composable
private fun GoogleIosContent(
    renderer: GoogleIosMapRenderer,
    state: MapState
) {
    val posedIds = remember(renderer) { MutableStateFlow(emptySet<String>()) }
    LaunchedEffect(renderer, state) {
        state.poses.collect { pose ->
            posedIds.value = posedIds.value + pose.id
            renderer.applyPose(pose.toGoogleIosPose())
        }
    }
    LaunchedEffect(renderer, state) {
        combine(flatten(state.markerSources), posedIds) { markers, posed -> markers.filterNot { posed.contains(it.id) } }
            .collect { markers -> renderer.setMarkers(markers.map { it.toGoogleIosMarker() }) }
    }
    LaunchedEffect(renderer, state) {
        flatten(state.lineSources).collect { routes -> renderer.setRoutes(routes.map { it.toGoogleIosRoute() }) }
    }
    LaunchedEffect(renderer, state) {
        flatten(state.circleSources).collect { circles -> renderer.setCircles(circles.map { it.toGoogleIosCircle() }) }
    }
}

private fun <T> flatten(sources: List<StateFlow<List<T>>>): Flow<List<T>> =
    if (sources.isEmpty()) flowOf(emptyList()) else combine(sources) { lists -> lists.flatMap { it } }
