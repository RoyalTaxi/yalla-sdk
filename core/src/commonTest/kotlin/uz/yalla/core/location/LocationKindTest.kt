package uz.yalla.core.location

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Characterization of the location enum decoders: [PointKind.from] and [PlaceKind.from].
 *
 * Both normalize (trim + lowercase) via the shared `normalizedId()` helper before matching, like
 * every other `from` decoder in the SDK, then fall back to their own default ([PointKind.Point] /
 * [PlaceKind.Other]) on any unrecognized or null input.
 */
class LocationKindTest {
    // --- PointKind: normalized match, default POINT ---

    @Test
    fun pointKindDecodesKnownIds() {
        assertEquals(PointKind.Start, PointKind.from("start"))
        assertEquals(PointKind.Point, PointKind.from("point"))
        assertEquals(PointKind.Stop, PointKind.from("stop"))
    }

    @Test
    fun pointKindNormalizesCaseAndWhitespace() {
        // Now matches its siblings: case/whitespace-insensitive so a backend "START" or " start "
        // resolves to the real waypoint instead of silently mislabeling it POINT.
        assertEquals(PointKind.Start, PointKind.from("START"))
        assertEquals(PointKind.Start, PointKind.from(" start "))
        assertEquals(PointKind.Stop, PointKind.from("Stop"))
    }

    @Test
    fun pointKindFallsBackToPointForUnknownAndNull() {
        assertEquals(PointKind.Point, PointKind.from(null))
        assertEquals(PointKind.Point, PointKind.from(""))
        assertEquals(PointKind.Point, PointKind.from("waypoint"))
    }

    @Test
    fun pointKindIdsAreStable() {
        assertEquals("start", PointKind.Start.id)
        assertEquals("point", PointKind.Point.id)
        assertEquals("stop", PointKind.Stop.id)
    }

    // --- PlaceKind: normalized, default Other ---

    @Test
    fun placeKindDecodesKnownIds() {
        assertEquals(PlaceKind.Home, PlaceKind.from("home"))
        assertEquals(PlaceKind.Work, PlaceKind.from("work"))
        assertEquals(PlaceKind.Other, PlaceKind.from("other"))
    }

    @Test
    fun placeKindNormalizesCaseAndWhitespace() {
        assertEquals(PlaceKind.Home, PlaceKind.from("  HOME  "))
        assertEquals(PlaceKind.Work, PlaceKind.from("Work"))
    }

    @Test
    fun placeKindFallsBackToOtherForUnknownAndNull() {
        assertEquals(PlaceKind.Other, PlaceKind.from(null))
        assertEquals(PlaceKind.Other, PlaceKind.from(""))
        assertEquals(PlaceKind.Other, PlaceKind.from("school"))
    }

    @Test
    fun placeKindIdsAreStable() {
        assertEquals("home", PlaceKind.Home.id)
        assertEquals("work", PlaceKind.Work.id)
        assertEquals("other", PlaceKind.Other.id)
    }
}
