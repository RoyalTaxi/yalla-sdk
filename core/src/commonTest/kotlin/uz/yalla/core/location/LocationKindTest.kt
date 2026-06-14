package uz.yalla.core.location

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Characterization of the location enum decoders: [PointKind.from] and [PlaceKind.from].
 *
 * These two intentionally differ, and the difference is the cost-of-silent-break:
 *  - [PlaceKind.from] normalizes (trim + lowercase) like the rest of the codebase.
 *  - [PointKind.from] matches the raw id EXACTLY — no trim, no lowercase — so "START" or " start "
 *    fall through to the [PointKind.POINT] default. Any edit that "helpfully" normalizes PointKind
 *    would change routing decode behavior, so its case-sensitivity is pinned explicitly.
 */
class LocationKindTest {
    // --- PointKind: EXACT match, default POINT, no normalization ---

    @Test
    fun pointKind_decodesExactLowercaseIds() {
        assertEquals(PointKind.START, PointKind.from("start"))
        assertEquals(PointKind.POINT, PointKind.from("point"))
        assertEquals(PointKind.STOP, PointKind.from("stop"))
    }

    @Test
    fun pointKind_isCaseSensitiveAndDoesNotTrim() {
        // Pinned wart/contract: PointKind does NOT normalize. Any non-exact id -> POINT default.
        assertEquals(PointKind.POINT, PointKind.from("START"))
        assertEquals(PointKind.POINT, PointKind.from(" start "))
        assertEquals(PointKind.POINT, PointKind.from("Stop"))
    }

    @Test
    fun pointKind_fallsBackToPointForUnknownAndNull() {
        assertEquals(PointKind.POINT, PointKind.from(null))
        assertEquals(PointKind.POINT, PointKind.from(""))
        assertEquals(PointKind.POINT, PointKind.from("waypoint"))
    }

    @Test
    fun pointKind_idsAreStable() {
        assertEquals("start", PointKind.START.id)
        assertEquals("point", PointKind.POINT.id)
        assertEquals("stop", PointKind.STOP.id)
    }

    // --- PlaceKind: normalized, default Other ---

    @Test
    fun placeKind_decodesKnownIds() {
        assertEquals(PlaceKind.Home, PlaceKind.from("home"))
        assertEquals(PlaceKind.Work, PlaceKind.from("work"))
        assertEquals(PlaceKind.Other, PlaceKind.from("other"))
    }

    @Test
    fun placeKind_normalizesCaseAndWhitespace() {
        assertEquals(PlaceKind.Home, PlaceKind.from("  HOME  "))
        assertEquals(PlaceKind.Work, PlaceKind.from("Work"))
    }

    @Test
    fun placeKind_fallsBackToOtherForUnknownAndNull() {
        assertEquals(PlaceKind.Other, PlaceKind.from(null))
        assertEquals(PlaceKind.Other, PlaceKind.from(""))
        assertEquals(PlaceKind.Other, PlaceKind.from("school"))
    }

    @Test
    fun placeKind_idsAreStable() {
        assertEquals("home", PlaceKind.Home.id)
        assertEquals("work", PlaceKind.Work.id)
        assertEquals("other", PlaceKind.Other.id)
    }
}
