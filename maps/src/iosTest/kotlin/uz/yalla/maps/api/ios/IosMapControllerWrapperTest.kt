package uz.yalla.maps.api.ios

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import platform.UIKit.UIViewController
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.MapCircle
import uz.yalla.maps.api.model.MapMarker
import uz.yalla.maps.api.model.MapRoute
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Characterization of [IosMapControllerWrapper] — the iOS counterpart of SwitchingMapController.
 * Pins the branchy bridge logic that previously had zero coverage: camera-emit epsilon de-dup,
 * the by-user lock-clear, user-location enable gating, locked-target replay, and the closed-guard
 * that must make every method a no-op after [close].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class IosMapControllerWrapperTest {
    private lateinit var renderer: FakeIosMapRenderer
    private lateinit var wrapper: IosMapControllerWrapper

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        renderer = FakeIosMapRenderer()
        wrapper = IosMapControllerWrapper(renderer)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun emitDedupSuppressesSubThresholdMoveButForwardsRealPan() =
        runTest {
            val listener = renderer.listener!!
            listener.onCameraMove(GeoPoint(40.0, 71.0), zoom = 15f, bearing = 0f, tilt = 0f, isByUser = false)
            val first = wrapper.cameraPosition.value
            assertEquals(GeoPoint(40.0, 71.0), first.target)

            // Sub-epsilon nudge must be de-duped: the public camera does not change.
            listener.onCameraMove(GeoPoint(40.0 + 5e-7, 71.0), zoom = 15f, bearing = 0f, tilt = 0f, isByUser = false)
            assertEquals(first, wrapper.cameraPosition.value)

            // A real pan past the epsilon must be forwarded.
            listener.onCameraMove(GeoPoint(40.01, 71.0), zoom = 15f, bearing = 0f, tilt = 0f, isByUser = false)
            assertEquals(GeoPoint(40.01, 71.0), wrapper.cameraPosition.value.target)
        }

    @Test
    fun byUserCameraMoveClearsLock() =
        runTest {
            wrapper.lockTarget(GeoPoint(41.0, 72.0), 16f)
            renderer.animateToCalls.clear()

            renderer.listener!!.onCameraMove(GeoPoint(40.0, 71.0), 15f, 0f, 0f, isByUser = true)
            // After a by-user move the lock is cleared, so changing padding no longer replays it.
            wrapper.setDesiredPadding(PaddingValues())
            assertTrue(renderer.animateToCalls.isEmpty(), "lock should be cleared; no replay expected")
        }

    @Test
    fun userLocationEnabledFalseForwardsNullThenReEnableReplaysPoint() =
        runTest {
            wrapper.setUserLocation(GeoPoint(40.0, 71.0))
            assertEquals(GeoPoint(40.0, 71.0), renderer.lastUserLocation)

            wrapper.setUserLocationEnabled(false)
            assertNull(renderer.lastUserLocation)

            wrapper.setUserLocationEnabled(true)
            assertEquals(GeoPoint(40.0, 71.0), renderer.lastUserLocation)
        }

    @Test
    fun lockTargetReplaysAnimateTo() =
        runTest {
            wrapper.lockTarget(GeoPoint(41.0, 72.0), 16f)
            assertEquals(1, renderer.animateToCalls.size)
            assertEquals(GeoPoint(41.0, 72.0), renderer.animateToCalls.single())
        }

    @Test
    fun everyMethodIsNoOpAfterClose() =
        runTest {
            wrapper.close()
            wrapper.setMarkers(listOf(MapMarker(id = "a", point = GeoPoint(0.0, 0.0))))
            wrapper.setUserLocation(GeoPoint(1.0, 2.0))
            wrapper.setInteractionEnabled(false)
            wrapper.moveTo(GeoPoint(3.0, 4.0), 12f)

            assertTrue(renderer.markers.isEmpty(), "setMarkers after close must be a no-op")
            assertNull(renderer.lastUserLocation, "setUserLocation after close must be a no-op")
            assertTrue(renderer.moveToCalls.isEmpty(), "moveTo after close must be a no-op")
            assertTrue(renderer.closed, "close forwards to the renderer")
        }
}

private class FakeIosMapRenderer : IosMapRenderer {
    var listener: IosMapListener? = null
        private set
    var markers: List<MapMarker> = emptyList()
        private set
    var lastUserLocation: GeoPoint? = null
        private set
    var closed: Boolean = false
        private set
    val animateToCalls = mutableListOf<GeoPoint>()
    val moveToCalls = mutableListOf<GeoPoint>()

    override fun createViewController(): UIViewController = UIViewController()

    override fun setListener(listener: IosMapListener?) {
        this.listener = listener
    }

    override fun moveTo(
        target: GeoPoint,
        zoom: Float
    ) {
        moveToCalls += target
    }

    override fun animateTo(
        target: GeoPoint,
        zoom: Float,
        durationMs: Int
    ) {
        animateToCalls += target
    }

    override fun animateToWithBearing(
        target: GeoPoint,
        bearing: Float,
        zoom: Float,
        durationMs: Int
    ) = Unit

    override fun fitBounds(
        points: List<GeoPoint>,
        leftPt: Float,
        topPt: Float,
        rightPt: Float,
        bottomPt: Float,
        animate: Boolean
    ) = Unit

    override fun zoomIn() = Unit

    override fun zoomOut() = Unit

    override fun setZoom(zoom: Float) = Unit

    override fun setStyleUrl(url: String) = Unit

    override fun setStyleJson(json: String) = Unit

    override fun setColorScheme(isDark: Boolean) = Unit

    override fun setPaddingPt(
        leftPt: Float,
        topPt: Float,
        rightPt: Float,
        bottomPt: Float
    ) = Unit

    override fun setInteractionEnabled(enabled: Boolean) = Unit

    override fun setMarkers(markers: List<MapMarker>) {
        this.markers = markers
    }

    override fun setRoutes(routes: List<MapRoute>) = Unit

    override fun setCircles(circles: List<MapCircle>) = Unit

    override fun setUserLocation(point: GeoPoint?) {
        lastUserLocation = point
    }

    override fun close() {
        closed = true
    }
}
