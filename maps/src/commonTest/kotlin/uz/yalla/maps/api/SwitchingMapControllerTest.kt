package uz.yalla.maps.api

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

            val snapshot = controller.snapshotScene()

            assertEquals(markers, snapshot.markers)
            assertEquals(GeoPoint(1.0, 2.0), snapshot.lockedTarget)
            assertEquals(15f, snapshot.lockedZoom)
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

    private fun marker(id: String) = MapMarker(id = id, point = GeoPoint(0.0, 0.0))

    private class FakeMapFactory : MapFactory {
        val googleCreated = mutableListOf<FakeMapController>()
        val libreCreated = mutableListOf<FakeMapController>()

        override fun createGoogleController(): MapController = FakeMapController().also { googleCreated += it }

        override fun createLibreController(): MapController = FakeMapController().also { libreCreated += it }
    }

    private class FakeMapController : MapController {
        override val cameraPosition: StateFlow<CameraPosition> = MutableStateFlow(CameraPosition.DEFAULT)
        override val centerPin: StateFlow<CenterPinState> = MutableStateFlow(CenterPinState.INITIAL)
        override val isReady: StateFlow<Boolean> = MutableStateFlow(true)
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

        override fun snapshotScene(): MapController.SceneSnapshot =
            MapController.SceneSnapshot(
                cameraPosition = cameraPosition.value,
                markers = markers,
                routes = routes,
                circles = circles,
                padding = padding,
                lockedTarget = lockedTarget,
                lockedZoom = lockedZoom
            )

        override fun close() {
            closed = true
        }
    }
}
