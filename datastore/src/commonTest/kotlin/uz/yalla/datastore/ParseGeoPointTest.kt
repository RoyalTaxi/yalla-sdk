package uz.yalla.datastore

import uz.yalla.core.geo.GeoPoint
import kotlin.test.Test
import kotlin.test.assertEquals

class ParseGeoPointTest {
    @Test
    fun decodesAWellFormedPair() {
        assertEquals(GeoPoint(41.311081, 69.240562), parseGeoPoint("41.311081,69.240562"))
    }

    @Test
    fun usesFallbackWhenRawIsBlankOrNull() {
        assertEquals(GeoPoint(1.0, 2.0), parseGeoPoint(raw = null, fallbackRaw = "1.0,2.0"))
        assertEquals(GeoPoint(1.0, 2.0), parseGeoPoint(raw = "  ", fallbackRaw = "1.0,2.0"))
    }

    @Test
    fun fallsBackToZeroWhenNothingIsStored() {
        assertEquals(GeoPoint.Zero, parseGeoPoint(raw = null, fallbackRaw = null))
        assertEquals(GeoPoint.Zero, parseGeoPoint(raw = "", fallbackRaw = ""))
    }

    @Test
    fun aMissingCommaIsRejectedNotTreatedAsLatitudeOnly() {
        assertEquals(GeoPoint.Zero, parseGeoPoint("40.5"))
    }

    @Test
    fun aHalfValidPairIsRejectedRatherThanFabricated() {
        assertEquals(GeoPoint(1.0, 2.0), parseGeoPoint(raw = "40.38,xyz", fallbackRaw = "1.0,2.0"))
        assertEquals(GeoPoint.Zero, parseGeoPoint(raw = "40.38,xyz", fallbackRaw = null))
    }

    @Test
    fun nonNumericInputFallsThrough() {
        assertEquals(GeoPoint.Zero, parseGeoPoint("abc,def"))
    }

    @Test
    fun extraCommasUseTheFirstTwoComponents() {
        assertEquals(GeoPoint.Zero, parseGeoPoint(raw = "1,2,3", fallbackRaw = null))
    }

    @Test
    fun aCorruptPrimaryUsesTheMapPositionFallback() {
        assertEquals(GeoPoint(55.75, 37.61), parseGeoPoint(raw = "garbage", fallbackRaw = "55.75,37.61"))
    }
}
