package uz.yalla.datastore

import uz.yalla.core.geo.GeoPoint
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins [parseGeoPoint]'s decode contract — the only non-trivial pure logic in the module and the inverse
 * of the `"lat,lng"` encoding the position setters write. Guards the atomic-parse fix (#14): a value is
 * accepted only when BOTH components parse, so a half-valid string falls through to the fallback (and
 * ultimately [GeoPoint.Zero]) instead of silently fabricating a half-real coordinate.
 */
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
        // "40.5" alone must NOT become (40.5, 0.0); with no fallback it is Null Island.
        assertEquals(GeoPoint.Zero, parseGeoPoint("40.5"))
    }

    @Test
    fun aHalfValidPairIsRejectedRatherThanFabricated() {
        // "40.38,xyz" must NOT become (40.38, 0.0); it falls through to the fallback.
        assertEquals(GeoPoint(1.0, 2.0), parseGeoPoint(raw = "40.38,xyz", fallbackRaw = "1.0,2.0"))
        assertEquals(GeoPoint.Zero, parseGeoPoint(raw = "40.38,xyz", fallbackRaw = null))
    }

    @Test
    fun nonNumericInputFallsThrough() {
        assertEquals(GeoPoint.Zero, parseGeoPoint("abc,def"))
    }

    @Test
    fun extraCommasUseTheFirstTwoComponents() {
        // split(limit = 2) keeps "3,4" as the longitude token, which is not a number -> rejected.
        assertEquals(GeoPoint.Zero, parseGeoPoint(raw = "1,2,3", fallbackRaw = null))
    }

    @Test
    fun aCorruptPrimaryUsesTheMapPositionFallback() {
        // The lastGpsPosition contract: corrupt GPS value, valid map value -> the map value, not Zero.
        assertEquals(GeoPoint(55.75, 37.61), parseGeoPoint(raw = "garbage", fallbackRaw = "55.75,37.61"))
    }
}
