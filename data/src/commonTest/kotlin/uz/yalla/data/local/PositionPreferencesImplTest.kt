package uz.yalla.data.local

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import uz.yalla.core.geo.GeoPoint
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [PositionPreferencesImpl].
 *
 * Uses [UnconfinedTestDispatcher] so `scope.launch { dataStore.edit { ... } }`
 * runs eagerly — by the time a setter returns, the in-memory DataStore has
 * observed the write.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PositionPreferencesImplTest {

    @Test
    fun shouldDefaultLastMapPositionToZeroOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals(GeoPoint.Zero, impl.lastMapPosition.first())
    }

    @Test
    fun shouldPersistLastMapPositionAsLatLngString() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setLastMapPosition(GeoPoint(lat = 41.3111, lng = 69.2797))

        val stored = impl.lastMapPosition.first()
        assertEquals(41.3111, stored.lat)
        assertEquals(69.2797, stored.lng)
    }

    @Test
    fun shouldDefaultLastGpsPositionToZeroWhenBothKeysAreUnset() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals(GeoPoint.Zero, impl.lastGpsPosition.first())
    }

    @Test
    fun shouldFallBackToLastMapPositionWhenGpsUnset() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)
        impl.setLastMapPosition(GeoPoint(lat = 41.0, lng = 69.0))

        val gps = impl.lastGpsPosition.first()

        assertEquals(41.0, gps.lat)
        assertEquals(69.0, gps.lng)
    }

    @Test
    fun shouldPreferGpsValueOverMapFallbackWhenBothSet() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)
        impl.setLastMapPosition(GeoPoint(lat = 41.0, lng = 69.0))
        impl.setLastGpsPosition(GeoPoint(lat = 41.9, lng = 69.9))

        val gps = impl.lastGpsPosition.first()

        assertEquals(41.9, gps.lat)
        assertEquals(69.9, gps.lng)
    }

    @Test
    fun shouldOverwritePreviousMapPositionOnSuccessiveSets() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)
        impl.setLastMapPosition(GeoPoint(lat = 1.0, lng = 2.0))

        impl.setLastMapPosition(GeoPoint(lat = 3.0, lng = 4.0))

        val stored = impl.lastMapPosition.first()
        assertEquals(3.0, stored.lat)
        assertEquals(4.0, stored.lng)
    }

    private fun newImpl(scope: TestScope): PositionPreferencesImpl = PositionPreferencesImpl(
        dataStore = InMemoryDataStore(),
        scope = scope.backgroundScope,
    )
}
