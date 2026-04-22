package uz.yalla.foundation.location

import dev.icerock.moko.geo.LocationTracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import uz.yalla.core.geo.GeoPoint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * iosTest — tests for [LocationManager] tracking lifecycle.
 *
 * Lives in iosTest because [LocationTracker] is an `expect class` whose iOS actual requires a
 * [dev.icerock.moko.permissions.PermissionsController], which on iOS is a typealias for
 * [dev.icerock.moko.permissions.ios.PermissionsControllerProtocol] (an interface). This lets us
 * pass a [FakePermissionsController] without any real platform location infrastructure.
 *
 * CLLocationManager.startUpdatingLocation() is called on the real iOS stack but never delivers
 * locations on the simulator, so collection just suspends — that is intentional and harmless.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LocationManagerTrackingTest {

    private val fakePermissionsController = FakePermissionsController()

    private fun makeTracker() = LocationTracker(
        permissionsController = fakePermissionsController,
    )

    // --- startTracking() idempotence ---

    @Test
    fun startTrackingIdempotence_secondCallIsNoOp() = runTest {
        val tracker = makeTracker()
        val scope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val lm = LocationManager(tracker, scope)

        // First call: coroutine launches, calls tracker.startTracking() (providePermission no-op +
        // CLLocationManager.startUpdatingLocation()), then sets _isTracking = true, then suspends
        // collecting locations (never delivered on simulator).
        lm.startTracking()
        advanceUntilIdle()

        assertTrue(lm.isTracking.value, "Expected isTracking=true after first startTracking()")

        // Second call: the guard `if (_isTracking.value) return` fires — no new coroutine launched.
        lm.startTracking()
        advanceUntilIdle()

        assertTrue(lm.isTracking.value, "Expected isTracking=true after idempotent second call")

        scope.cancel()
    }

    // --- stopTracking() before start is a no-op ---

    @Test
    fun stopTrackingBeforeStartIsNoOp() = runTest {
        val tracker = makeTracker()
        val scope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val lm = LocationManager(tracker, scope)

        // Guard: if (!_isTracking.value) return — no tracker.stopTracking() call.
        lm.stopTracking()
        advanceUntilIdle()

        assertFalse(lm.isTracking.value, "isTracking must stay false when stopTracking called before start")

        scope.cancel()
    }

    // --- scope cancellation stops tracking ---

    @Test
    fun scopeCancellationStopsTracking() = runTest {
        val tracker = makeTracker()
        val scope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val lm = LocationManager(tracker, scope)

        lm.startTracking()
        advanceUntilIdle()
        assertTrue(lm.isTracking.value, "Precondition: isTracking=true after startTracking()")

        // Cancel the owner scope: the launched coroutine receives CancellationException,
        // caught by runCatching.onFailure → _isTracking.value = false.
        scope.cancel()
        advanceUntilIdle()

        assertFalse(lm.isTracking.value, "isTracking must be false after owner scope is cancelled")
    }

    // --- updatePermissionState propagates to permissionState flow ---

    @Test
    fun updatePermissionStatePropagatesValue() {
        val tracker = makeTracker()
        val scope = TestScope(UnconfinedTestDispatcher())
        val lm = LocationManager(tracker, scope)

        assertFalse(
            lm.permissionState.value != null,
            "permissionState must be null before first update"
        )

        lm.updatePermissionState(LocationPermissionState.GRANTED)
        assertEquals(LocationPermissionState.GRANTED, lm.permissionState.value)

        lm.updatePermissionState(LocationPermissionState.DENIED)
        assertEquals(LocationPermissionState.DENIED, lm.permissionState.value)

        lm.updatePermissionState(null)
        assertFalse(
            lm.permissionState.value != null,
            "permissionState must revert to null after null update"
        )

        scope.cancel()
    }

    // --- getCurrentLocationOrDefault() returns DEFAULT_LOCATION when no fix ---

    @Test
    fun getCurrentLocationOrDefaultReturnsDefaultWhenNoFix() {
        val customDefault = GeoPoint(0.0, 0.0)
        val tracker = makeTracker()
        val scope = TestScope(UnconfinedTestDispatcher())
        val lm = LocationManager(tracker, scope, defaultLocation = customDefault)

        // No startTracking() called — extendedLocation is null — should return the default.
        val result = lm.getCurrentLocationOrDefault()
        assertEquals(customDefault, result)

        scope.cancel()
    }

    @Test
    fun getCurrentLocationOrDefaultReturnsSdkDefaultWhenNoCustomDefault() {
        val tracker = makeTracker()
        val scope = TestScope(UnconfinedTestDispatcher())
        val lm = LocationManager(tracker, scope)

        val result = lm.getCurrentLocationOrDefault()
        assertEquals(LocationManager.DEFAULT_LOCATION, result)

        scope.cancel()
    }

    // --- getCurrentLocation() returns null before any fix ---

    @Test
    fun getCurrentLocationReturnsNullBeforeAnyFix() {
        val tracker = makeTracker()
        val scope = TestScope(UnconfinedTestDispatcher())
        val lm = LocationManager(tracker, scope)

        assertFalse(
            lm.getCurrentLocation() != null,
            "getCurrentLocation() must be null when no fix has arrived"
        )

        scope.cancel()
    }
}
