package uz.yalla.data.local

import uz.yalla.core.geo.GeoPoint
import kotlin.test.Test
import kotlin.test.assertEquals

class ParseGeoPointTest {
    @Test
    fun shouldParseValidCoordinates() {
        val point = parseGeoPoint("41.3111,69.2797")

        assertEquals(41.3111, point.lat)
        assertEquals(69.2797, point.lng)
    }

    @Test
    fun shouldReturnZeroForNullInput() {
        val point = parseGeoPoint(null)

        assertEquals(GeoPoint.Zero, point)
    }

    @Test
    fun shouldReturnZeroForBlankInput() {
        val point = parseGeoPoint("   ")

        assertEquals(GeoPoint.Zero, point)
    }

    @Test
    fun shouldReturnZeroForEmptyString() {
        val point = parseGeoPoint("")

        assertEquals(GeoPoint.Zero, point)
    }

    @Test
    fun shouldUseFallbackWhenPrimaryIsNull() {
        val point = parseGeoPoint(raw = null, fallbackRaw = "41.0,69.0")

        assertEquals(41.0, point.lat)
        assertEquals(69.0, point.lng)
    }

    @Test
    fun shouldUseFallbackWhenPrimaryIsBlank() {
        val point = parseGeoPoint(raw = "  ", fallbackRaw = "41.0,69.0")

        assertEquals(41.0, point.lat)
        assertEquals(69.0, point.lng)
    }

    @Test
    fun shouldReturnZeroForMalformedInput() {
        val point = parseGeoPoint("not-a-coordinate")

        assertEquals(0.0, point.lat)
        assertEquals(0.0, point.lng)
    }

    @Test
    fun shouldHandleMissingLongitude() {
        val point = parseGeoPoint("41.0")

        assertEquals(41.0, point.lat)
        assertEquals(0.0, point.lng)
    }

    @Test
    fun shouldParseNegativeCoordinates() {
        val point = parseGeoPoint("-33.8688,151.2093")

        assertEquals(-33.8688, point.lat)
        assertEquals(151.2093, point.lng)
    }
}
