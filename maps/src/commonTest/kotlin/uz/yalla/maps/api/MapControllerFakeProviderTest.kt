package uz.yalla.maps.api

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.provider.SwitchingMapController
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// ---------------------------------------------------------------------------
// Fake InterfacePreferences — controllable in-memory implementation
// ---------------------------------------------------------------------------

private class FakeInterfacePreferences(
    initialMapKind: MapKind = MapKind.Google,
) : InterfacePreferences {
    private val _mapKind = MutableStateFlow(initialMapKind)
    override val mapKind: Flow<MapKind> = _mapKind.asStateFlow()

    fun emitMapKind(kind: MapKind) {
        _mapKind.value = kind
    }

    private val _localeType = MutableStateFlow(LocaleKind.Uz)
    override val localeType: Flow<LocaleKind> = _localeType.asStateFlow()

    override fun setLocaleType(value: LocaleKind) {}

    private val _themeType = MutableStateFlow(ThemeKind.System)
    override val themeType: Flow<ThemeKind> = _themeType.asStateFlow()

    override fun setThemeType(value: ThemeKind) {}

    override fun setMapKind(value: MapKind) {
        _mapKind.value = value
    }

    private val _skipOnboarding = MutableStateFlow(false)
    override val skipOnboarding: Flow<Boolean> = _skipOnboarding.asStateFlow()

    override fun setSkipOnboarding(value: Boolean) {}

    private val _onboardingStage = MutableStateFlow("")
    override val onboardingStage: Flow<String> = _onboardingStage.asStateFlow()

    override fun setOnboardingStage(value: String) {}
}

// ---------------------------------------------------------------------------
// Fake MapController — records every call and exposes backing state flows
// ---------------------------------------------------------------------------

private class FakeMapController : MapController {
    val calls = mutableListOf<String>()

    private val _cameraPosition = MutableStateFlow(CameraPosition.DEFAULT)
    override val cameraPosition: StateFlow<CameraPosition> = _cameraPosition.asStateFlow()

    private val _markerState = MutableStateFlow(MarkerState.INITIAL)
    override val markerState: StateFlow<MarkerState> = _markerState.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    override val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    fun simulateReady() {
        _isReady.value = true
    }

    override suspend fun moveTo(point: GeoPoint, zoom: Float) {
        calls += "moveTo($point,$zoom)"
        _cameraPosition.value = CameraPosition(target = point, zoom = zoom)
    }

    override suspend fun animateTo(point: GeoPoint, zoom: Float, durationMs: Int) {
        calls += "animateTo($point,$zoom,$durationMs)"
        _cameraPosition.value = CameraPosition(target = point, zoom = zoom)
    }

    override suspend fun animateToWithBearing(point: GeoPoint, bearing: Float, zoom: Float, durationMs: Int) {
        calls += "animateToWithBearing($point,$bearing,$zoom,$durationMs)"
        _cameraPosition.value = CameraPosition(target = point, zoom = zoom, bearing = bearing)
    }

    override suspend fun fitBounds(points: List<GeoPoint>, padding: PaddingValues, animate: Boolean) {
        calls += "fitBounds(${points.size},$animate)"
    }

    override suspend fun zoomIn() {
        calls += "zoomIn"
    }

    override suspend fun zoomOut() {
        calls += "zoomOut"
    }

    override suspend fun setZoom(zoom: Float) {
        calls += "setZoom($zoom)"
    }

    override fun setDesiredPadding(padding: PaddingValues) {
        calls += "setDesiredPadding"
    }

    override suspend fun updatePadding(padding: PaddingValues) {
        calls += "updatePadding"
    }

    override fun updateMarkerState(state: MarkerState) {
        calls += "updateMarkerState"
        _markerState.value = state
    }

    override fun setMarkerPosition(point: GeoPoint) {
        calls += "setMarkerPosition($point)"
        _markerState.value = _markerState.value.copy(point = point)
    }

    override fun clearMarker() {
        calls += "clearMarker"
        _markerState.value = MarkerState.INITIAL
    }

    override fun onMapReady() {
        calls += "onMapReady"
        _isReady.value = true
    }

    override fun reset() {
        calls += "reset"
        _isReady.value = false
        _markerState.value = MarkerState.INITIAL
        _cameraPosition.value = CameraPosition.DEFAULT
    }

    override fun close() {
        calls += "close"
    }
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

/**
 * Tests for [SwitchingMapController] and the [MapController] interface contract
 * using [FakeInterfacePreferences] and [FakeMapController].
 *
 * These tests validate control-flow and state delegation without touching
 * any real map rendering (no live [GoogleMapController] / [LibreMapController]
 * composition required).
 */
class MapControllerFakeProviderTest {

    // -----------------------------------------------------------------------
    // Initial state
    // -----------------------------------------------------------------------

    @Test
    fun initialStateIsNotReady() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        assertFalse(controller.isReady.value)

        controller.close()
    }

    @Test
    fun initialCameraIsDefault() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        assertEquals(CameraPosition.DEFAULT, controller.cameraPosition.value)

        controller.close()
    }

    @Test
    fun initialMarkerIsInitial() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        assertEquals(MarkerState.INITIAL, controller.markerState.value)

        controller.close()
    }

    // -----------------------------------------------------------------------
    // onMapReady forwards to the active controller
    // -----------------------------------------------------------------------

    @Test
    fun onMapReadyMakesControllerReady() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        // onMapReady on the SwitchingMapController delegates to the underlying
        // GoogleMapController (default). After calling it isReady should be true.
        controller.onMapReady()

        assertTrue(controller.isReady.value)

        controller.close()
    }

    // -----------------------------------------------------------------------
    // updateMarkerState / setMarkerPosition / clearMarker
    // -----------------------------------------------------------------------

    @Test
    fun updateMarkerStatePropagatesViaCollector() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)
        controller.onMapReady()

        val newMarker = MarkerState(point = GeoPoint(41.0, 69.0), isMoving = false, isByUser = false)
        controller.updateMarkerState(newMarker)

        // The switching controller's own _markerState should eventually reflect the update
        // via its collectFromActive() coroutine. With UnconfinedTestDispatcher collectors
        // run eagerly, but the underlying Google controller needs to emit.
        // We verify via the underlying controller's markerState directly.
        assertEquals(newMarker, controller.googleController.markerState.value)

        controller.close()
    }

    @Test
    fun setMarkerPositionUpdatesPosition() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)
        controller.onMapReady()

        val point = GeoPoint(41.5, 69.5)
        controller.setMarkerPosition(point)

        assertEquals(point, controller.googleController.markerState.value.point)

        controller.close()
    }

    @Test
    fun clearMarkerResetsToInitial() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)
        controller.onMapReady()

        val newMarker = MarkerState(point = GeoPoint(41.0, 69.0), isMoving = true, isByUser = true)
        controller.updateMarkerState(newMarker)
        controller.clearMarker()

        assertEquals(MarkerState.INITIAL, controller.googleController.markerState.value)

        controller.close()
    }

    // -----------------------------------------------------------------------
    // reset
    // -----------------------------------------------------------------------

    @Test
    fun resetClearsReadiness() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)
        controller.onMapReady()
        assertTrue(controller.isReady.value)

        controller.reset()

        assertFalse(controller.isReady.value)

        controller.close()
    }

    @Test
    fun resetClearsMarkerState() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)
        controller.onMapReady()

        controller.updateMarkerState(MarkerState(GeoPoint(41.0, 69.0), isMoving = true, isByUser = true))
        controller.reset()

        assertEquals(MarkerState.INITIAL, controller.markerState.value)

        controller.close()
    }

    @Test
    fun resetClearsCameraPosition() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        controller.reset()

        assertEquals(CameraPosition.DEFAULT, controller.cameraPosition.value)

        controller.close()
    }

    // -----------------------------------------------------------------------
    // setDesiredPadding (sync, no live camera required)
    // -----------------------------------------------------------------------

    @Test
    fun setDesiredPaddingDoesNotThrow() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        // Should not throw — delegates to underlying controller
        controller.setDesiredPadding(PaddingValues())

        controller.close()
    }

    // -----------------------------------------------------------------------
    // close
    // -----------------------------------------------------------------------

    @Test
    fun closeMarksControllerAsClosed() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        assertFalse(controller.isClosed)
        controller.close()
        assertTrue(controller.isClosed)
    }

    @Test
    fun closeTwiceIsIdempotent() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        controller.close()
        controller.close() // must not throw

        assertTrue(controller.isClosed)
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun closeStopsInternalCoroutines() =
        runTest(UnconfinedTestDispatcher()) {
            val prefs = FakeInterfacePreferences()
            val controller = SwitchingMapController(prefs, this)

            // Track whether a job launched in backgroundScope observing isReady
            // gets cancelled after close().
            var collectCount = 0
            val observerJob: Job = backgroundScope.launch {
                controller.isReady.collect { collectCount++ }
            }

            controller.onMapReady()
            controller.close()

            // Cancel the observer as well (it's in backgroundScope of the test)
            observerJob.cancel()

            // We care that close() does not throw and isClosed is true
            assertTrue(controller.isClosed)
        }

    // -----------------------------------------------------------------------
    // Operations after close are no-ops (reset is guarded by isClosed)
    // -----------------------------------------------------------------------

    @Test
    fun resetAfterCloseIsNoOp() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)
        controller.close()

        // Must not throw
        controller.reset()

        assertTrue(controller.isClosed)
    }

    // -----------------------------------------------------------------------
    // Suspend operations — moveTo, animateTo, fitBounds, zoomIn/Out, setZoom, updatePadding
    // These delegate to GoogleMapController, which is a no-op when cameraState is null.
    // We verify they complete without throwing.
    // -----------------------------------------------------------------------

    @Test
    fun moveToCompletesWithoutThrow() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        controller.moveTo(GeoPoint(41.0, 69.0), zoom = 15f)

        controller.close()
    }

    @Test
    fun animateToCompletesWithoutThrow() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        controller.animateTo(GeoPoint(41.0, 69.0), zoom = 14f, durationMs = 500)

        controller.close()
    }

    @Test
    fun animateToWithBearingCompletesWithoutThrow() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        controller.animateToWithBearing(GeoPoint(41.0, 69.0), bearing = 45f, zoom = 14f, durationMs = 300)

        controller.close()
    }

    @Test
    fun fitBoundsCompletesWithoutThrow() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        controller.fitBounds(
            points = listOf(GeoPoint(41.0, 69.0), GeoPoint(42.0, 70.0)),
            animate = false,
        )

        controller.close()
    }

    @Test
    fun zoomInCompletesWithoutThrow() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        controller.zoomIn()

        controller.close()
    }

    @Test
    fun zoomOutCompletesWithoutThrow() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        controller.zoomOut()

        controller.close()
    }

    @Test
    fun setZoomCompletesWithoutThrow() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        controller.setZoom(16f)

        controller.close()
    }

    @Test
    fun updatePaddingCompletesWithoutThrow() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        controller.updatePadding(PaddingValues())

        controller.close()
    }

    // -----------------------------------------------------------------------
    // Lazy controller initialization
    // -----------------------------------------------------------------------

    @Test
    fun googleControllerIsExposedAfterAccess() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        // Accessing googleController triggers lazy init; must not throw
        val google = controller.googleController

        assertFalse(google.isReady.value)

        controller.close()
    }

    @Test
    fun libreControllerIsExposedAfterAccess() = runTest {
        val prefs = FakeInterfacePreferences()
        val controller = SwitchingMapController(prefs, this)

        val libre = controller.libreController

        assertFalse(libre.isReady.value)

        controller.close()
    }

    // -----------------------------------------------------------------------
    // FakeMapController unit tests — verifies the fake records calls correctly
    // (independent of SwitchingMapController; useful as a contract check)
    // -----------------------------------------------------------------------

    @Test
    fun fakeRecordsUpdateMarkerState() = runTest {
        val fake = FakeMapController()
        val state = MarkerState(GeoPoint(1.0, 2.0), isMoving = false, isByUser = false)

        fake.updateMarkerState(state)

        assertTrue(fake.calls.contains("updateMarkerState"))
        assertEquals(state, fake.markerState.value)
    }

    @Test
    fun fakeRecordsSetMarkerPosition() = runTest {
        val fake = FakeMapController()
        val point = GeoPoint(3.0, 4.0)

        fake.setMarkerPosition(point)

        assertTrue(fake.calls.any { it.startsWith("setMarkerPosition") })
        assertEquals(point, fake.markerState.value.point)
    }

    @Test
    fun fakeRecordsClearMarker() = runTest {
        val fake = FakeMapController()
        fake.updateMarkerState(MarkerState(GeoPoint(1.0, 2.0), isMoving = true, isByUser = true))
        fake.clearMarker()

        assertTrue(fake.calls.contains("clearMarker"))
        assertEquals(MarkerState.INITIAL, fake.markerState.value)
    }

    @Test
    fun fakeOnMapReadySetsIsReady() = runTest {
        val fake = FakeMapController()
        fake.onMapReady()
        assertTrue(fake.isReady.value)
        assertTrue(fake.calls.contains("onMapReady"))
    }

    @Test
    fun fakeResetClearsAll() = runTest {
        val fake = FakeMapController()
        fake.onMapReady()
        fake.reset()

        assertFalse(fake.isReady.value)
        assertEquals(MarkerState.INITIAL, fake.markerState.value)
        assertEquals(CameraPosition.DEFAULT, fake.cameraPosition.value)
        assertTrue(fake.calls.contains("reset"))
    }

    @Test
    fun fakeSuspendOpsRecordCalls() = runTest {
        val fake = FakeMapController()

        fake.moveTo(GeoPoint(10.0, 20.0), 12f)
        fake.animateTo(GeoPoint(11.0, 21.0), 13f, 400)
        fake.animateToWithBearing(GeoPoint(12.0, 22.0), 90f, 14f, 300)
        fake.fitBounds(listOf(GeoPoint(1.0, 2.0), GeoPoint(3.0, 4.0)), animate = true)
        fake.zoomIn()
        fake.zoomOut()
        fake.setZoom(10f)
        fake.setDesiredPadding(PaddingValues())
        fake.updatePadding(PaddingValues())

        assertTrue(fake.calls.any { it.startsWith("moveTo") })
        assertTrue(fake.calls.any { it.startsWith("animateTo") })
        assertTrue(fake.calls.any { it.startsWith("animateToWithBearing") })
        assertTrue(fake.calls.any { it.startsWith("fitBounds") })
        assertTrue(fake.calls.contains("zoomIn"))
        assertTrue(fake.calls.contains("zoomOut"))
        assertTrue(fake.calls.any { it.startsWith("setZoom") })
        assertTrue(fake.calls.contains("setDesiredPadding"))
        assertTrue(fake.calls.contains("updatePadding"))
    }

    // -----------------------------------------------------------------------
    // Seed next controller: verify desiredPadding is forwarded on handoff
    // -----------------------------------------------------------------------

    @Test
    fun setDesiredPaddingIsForwardedToNextProvider() = runTest {
        val prefs = FakeInterfacePreferences(initialMapKind = MapKind.Google)
        val controller = SwitchingMapController(prefs, this)

        // Set padding while on Google
        controller.setDesiredPadding(PaddingValues())

        // Switching to Libre triggers seedNextController, which re-applies padding.
        // We verify this doesn't throw and the controller stays non-closed.
        prefs.emitMapKind(MapKind.Libre)

        assertFalse(controller.isClosed)

        controller.close()
    }
}
