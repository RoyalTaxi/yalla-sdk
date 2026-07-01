package uz.yalla.carto

import kotlinx.coroutines.test.runTest
import uz.yalla.carto.capability.circles
import uz.yalla.carto.capability.markers
import uz.yalla.carto.capability.userLocation
import uz.yalla.carto.model.CartoMarker
import uz.yalla.core.geo.GeoPoint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AggregationTest {
    @Test
    fun mapStateAggregatesSourcesAcrossCapabilities() =
        runTest {
            val map =
                composeCarto(backgroundScope, CartoProvider.Libre) {
                    markers()
                    circles()
                    userLocation()
                }
            map.markers.set(listOf(CartoMarker("m", GeoPoint(1.0, 1.0))))
            map.userLocation.setPoint(GeoPoint(2.0, 2.0))

            assertEquals(2, map.state.markerSources.size)
            assertEquals(2, map.state.circleSources.size)

            val renderedMarkers = map.state.markerSources.flatMap { it.value }
            assertTrue(renderedMarkers.any { it.id == "m" })
            assertTrue(renderedMarkers.any { it.id == "yalla-user-location-dot" })
        }
}
