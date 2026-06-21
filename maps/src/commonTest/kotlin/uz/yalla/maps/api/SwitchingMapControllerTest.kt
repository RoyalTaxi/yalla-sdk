package uz.yalla.maps.api

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.settings.MapKind
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.CenterPinState
import uz.yalla.maps.api.model.MapCircle
import uz.yalla.maps.api.model.MapEvent
import uz.yalla.maps.api.model.MapMarker
import uz.yalla.maps.api.model.MapRoute
import uz.yalla.maps.api.model.MapStyle
import uz.yalla.maps.config.MapFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Characterization of [SwitchingMapController] — the Google<->MapLibre runtime switch (a known
 * flicker-prone seam). Pins the behaviours that keep a switch seamless: scene state is cached even
 * with no active backend, switching is idempotent for the same provider, switching to a different
 * provider hands the scene over to the new backend and disposes the old one, and live mutations are
 * forwarded to the active backend.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SwitchingMapControllerTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var factory: FakeMapFactory
    private lateinit var controller: SwitchingMapController

    @BeforeTest
    fun setUp() {
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        factory = FakeMapFactory()
        controller = SwitchingMapController(factory)
    }

    @AfterTest
    fun tearDown() {
        controller.close()
        Dispatchers.resetMain()
    }

    @Test
    fun cachesSceneStateWhenNoBackendIsActive() =
        runTest(dispatcher) {
            val markers = listOf(marker("a"))
            controller.setMarkers(markers)
            controller.lockTarget(GeoPoint(1.0, 2.0), 15f)

            controller.switchTo(MapKind.Google)
            val active = controller.activeBackend.value as FakeMapController

            assertEquals(markers, active.markers)
            assertEquals(GeoPoint(1.0, 2.0), active.lockedTarget)
            assertEquals(15f, active.lockedZoom)
        }

    @Test
    fun switchToActivatesBackendAndAppliesCachedState() =
        runTest(dispatcher) {
            val markers = listOf(marker("a"))
            controller.setMarkers(markers)

            controller.switchTo(MapKind.Google)

            val active = controller.activeBackend.value as FakeMapController
            assertEquals(markers, active.markers)
            assertTrue(controller.isReady.value)
        }

    @Test
    fun switchToIsIdempotentForSameProvider() =
        runTest(dispatcher) {
            controller.switchTo(MapKind.Google)
            controller.switchTo(MapKind.Google)

            assertEquals(1, factory.googleCreated.size)
        }

    @Test
    fun switchToDifferentProviderClosesPreviousAndCarriesScene() =
        runTest(dispatcher) {
            val markers = listOf(marker("a"))
            controller.switchTo(MapKind.Google)
            controller.setMarkers(markers)

            controller.switchTo(MapKind.Libre)

            assertTrue(factory.googleCreated.single().closed)
            val active = controller.activeBackend.value as FakeMapController
            assertNotNull(active)
            assertEquals(markers, active.markers)
            assertSame(factory.libreCreated.single(), active)
        }

    @Test
    fun mutationsAreForwardedToActiveBackend() =
        runTest(dispatcher) {
            controller.switchTo(MapKind.Google)
            val active = factory.googleCreated.single()

            val markers = listOf(marker("x"), marker("y"))
            controller.setMarkers(markers)
            controller.setInteractionEnabled(false)
            controller.setUserLocation(GeoPoint(5.0, 6.0))

            assertEquals(markers, active.markers)
            assertEquals(false, active.interactionEnabled)
            assertEquals(GeoPoint(5.0, 6.0), active.userLocation)
        }

    @Test
    fun styleSurvivesProviderSwitch() =
        runTest(dispatcher) {
            val style = MapStyle.InlineJson(lightJson = "{light}", darkJson = "{dark}")
            controller.switchTo(MapKind.Google)
            controller.setStyle(style, isDark = true)

            controller.switchTo(MapKind.Libre)

            val active = controller.activeBackend.value as FakeMapController
            assertEquals(style, active.lastStyle)
        }

    @Test
    fun firstSwitchAppliesPersistedStyle() =
        runTest(dispatcher) {
            // setStyle is called before any backend exists; it only updates the cached fields. The
            // first switchTo must replay that persisted style onto the freshly-created backend.
            val style = MapStyle.InlineJson(lightJson = "{light}", darkJson = "{dark}")
            controller.setStyle(style, isDark = true)

            controller.switchTo(MapKind.Google)

            val active = controller.activeBackend.value as FakeMapController
            assertEquals(style, active.lastStyle)
        }

    @Test
    fun aggregatedCameraSurvivesPreSeedDefaultEmissions() =
        runTest(dispatcher) {
            // Establish a meaningful aggregated camera via a ready backend.
            controller.switchTo(MapKind.Google)
            val google = factory.googleCreated.single()
            val meaningful = CameraPosition(target = GeoPoint(40.0, 71.0), zoom = 16f)
            google.cameraFlow.value = meaningful
            assertEquals(meaningful, controller.cameraPosition.value)

            // Switch to a backend that is NOT ready yet, so its seed moveTo is parked. While parked,
            // it re-emits DEFAULT more than once; neither may clobber the aggregated camera (the old
            // guard only suppressed the first emission, letting the second flicker to DEFAULT).
            val notReadyFactory = FakeMapFactory(ready = false)
            val pending = SwitchingMapController(notReadyFactory)
            pending.switchTo(MapKind.Google)
            val backend = notReadyFactory.googleCreated.single()
            // Seed the parent's aggregated camera to the meaningful value, mirroring a real switch.
            backend.cameraFlow.value = meaningful
            advanceUntilIdle()
            // Reset to a meaningful aggregated state, then have the not-ready backend emit DEFAULT twice.
            backend.cameraFlow.value = CameraPosition.DEFAULT
            backend.cameraFlow.value = CameraPosition.DEFAULT

            assertEquals(meaningful, pending.cameraPosition.value)
            pending.close()
        }

    @Test
    fun cachedStateIsAppliedOnlyAfterBackendBecomesReady() =
        runTest(dispatcher) {
            val notReadyFactory = FakeMapFactory(ready = false)
            val pending = SwitchingMapController(notReadyFactory)
            val markers = listOf(marker("a"))
            pending.setMarkers(markers)

            pending.switchTo(MapKind.Google)
            val backend = notReadyFactory.googleCreated.single()
            // Backend not ready yet: the seed job is parked on isReady, so cached markers have not
            // been forwarded.
            assertEquals(emptyList(), backend.markers)

            backend.readyFlow.value = true
            advanceUntilIdle()

            assertEquals(markers, backend.markers)
            pending.close()
        }

    @Test
    fun neverReadyBackendEmitsProviderUnavailable() =
        runTest(dispatcher) {
            val notReadyFactory = FakeMapFactory(ready = false)
            val pending = SwitchingMapController(notReadyFactory)
            val events = mutableListOf<MapEvent>()
            val collectJob = launch { pending.events.collect { events += it } }

            pending.switchTo(MapKind.Google)
            advanceTimeBy(6_000L)
            advanceUntilIdle()

            assertTrue(events.contains(MapEvent.ProviderUnavailable), "expected ProviderUnavailable, got $events")
            collectJob.cancel()
            pending.close()
        }

    private fun marker(id: String) = MapMarker(id = id, point = GeoPoint(0.0, 0.0))

    private class FakeMapFactory(
        private val ready: Boolean = true
    ) : MapFactory {
        val googleCreated = mutableListOf<FakeMapController>()
        val libreCreated = mutableListOf<FakeMapController>()

        override fun createGoogleController(): MapController =
            FakeMapController(ready = ready).also { googleCreated += it }

        override fun createLibreController(): MapController =
            FakeMapController(ready = ready).also { libreCreated += it }
    }

    private class FakeMapController(
        ready: Boolean = true
    ) : MapController {
        val cameraFlow = MutableStateFlow(CameraPosition.DEFAULT)
        val centerPinFlow = MutableStateFlow(CenterPinState.INITIAL)
        val readyFlow = MutableStateFlow(ready)

        override val cameraPosition: StateFlow<CameraPosition> = cameraFlow
        override val centerPin: StateFlow<CenterPinState> = centerPinFlow
        override val isReady: StateFlow<Boolean> = readyFlow
        override val events: SharedFlow<MapEvent> = MutableSharedFlow()

        var markers: List<MapMarker> = emptyList()
            private set
        var routes: List<MapRoute> = emptyList()
            private set
        var circles: List<MapCircle> = emptyList()
            private set
        var padding: PaddingValues = PaddingValues()
            private set
        var interactionEnabled: Boolean = true
            private set
        var userLocation: GeoPoint? = null
            private set
        var userLocationEnabled: Boolean = true
            private set
        var lockedTarget: GeoPoint? = null
            private set
        var lockedZoom: Float? = null
            private set
        var lastStyle: MapStyle? = null
            private set
        var closed: Boolean = false
            private set

        override suspend fun moveTo(
            point: GeoPoint,
            zoom: Float
        ) = Unit

        override suspend fun animateTo(
            point: GeoPoint,
            zoom: Float,
            durationMs: Int
        ) = Unit

        override suspend fun animateToWithBearing(
            point: GeoPoint,
            bearing: Float,
            zoom: Float,
            durationMs: Int
        ) = Unit

        override suspend fun fitBounds(
            points: List<GeoPoint>,
            animate: Boolean,
            padding: PaddingValues?
        ) = Unit

        override suspend fun zoomIn() = Unit

        override suspend fun zoomOut() = Unit

        override suspend fun setZoom(zoom: Float) = Unit

        override suspend fun setStyle(
            style: MapStyle,
            isDark: Boolean
        ) {
            lastStyle = style
        }

        override fun setDesiredPadding(padding: PaddingValues) {
            this.padding = padding
        }

        override fun setInteractionEnabled(enabled: Boolean) {
            interactionEnabled = enabled
        }

        override fun setMarkers(markers: List<MapMarker>) {
            this.markers = markers
        }

        override fun setRoutes(routes: List<MapRoute>) {
            this.routes = routes
        }

        override fun setCircles(circles: List<MapCircle>) {
            this.circles = circles
        }

        override fun setUserLocation(point: GeoPoint?) {
            userLocation = point
        }

        override fun setUserLocationEnabled(enabled: Boolean) {
            userLocationEnabled = enabled
        }

        override fun lockTarget(
            point: GeoPoint,
            zoom: Float?
        ) {
            lockedTarget = point
            lockedZoom = zoom
        }

        override fun unlockTarget() {
            lockedTarget = null
            lockedZoom = null
        }

        override fun close() {
            closed = true
        }
    }
}
