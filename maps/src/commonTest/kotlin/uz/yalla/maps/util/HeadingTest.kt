package uz.yalla.maps.util

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Characterization of the heading math behind smooth marker rotation. The wrap-around is the
 * bug-prone part: turning 350° -> 10° must rotate +20° across north, not -340° the long way, and the
 * interpolated value must stay continuous (unwrapped) so the marker doesn't spin.
 */
class HeadingTest {
    private val tolerance = 0.001f

    @Test
    fun normalizeWrapsIntoZeroToThreeSixty() {
        assertEquals(10f, normalizeHeading(370f), tolerance)
        assertEquals(350f, normalizeHeading(-10f), tolerance)
        assertEquals(0f, normalizeHeading(360f), tolerance)
        assertEquals(0f, normalizeHeading(720f), tolerance)
        assertEquals(0f, normalizeHeading(-360f), tolerance)
    }

    @Test
    fun normalizeLeavesInRangeValuesUnchanged() {
        assertEquals(0f, normalizeHeading(0f), tolerance)
        assertEquals(45f, normalizeHeading(45f), tolerance)
        assertEquals(180f, normalizeHeading(180f), tolerance)
    }

    @Test
    fun shortestPathCrossesNorthForwardWhenShorter() {
        assertEquals(370f, shortestHeadingPath(350f, 10f), tolerance)
    }

    @Test
    fun shortestPathGoesBackwardWhenShorter() {
        assertEquals(-10f, shortestHeadingPath(10f, 350f), tolerance)
    }

    @Test
    fun shortestPathKeepsDirectDeltaWithinHalfTurn() {
        assertEquals(180f, shortestHeadingPath(0f, 180f), tolerance)
        assertEquals(-179f, shortestHeadingPath(0f, 181f), tolerance)
    }

    @Test
    fun interpolateAtZeroFractionReturnsStart() {
        assertEquals(350f, interpolateHeading(350f, 10f, 0f), tolerance)
    }

    @Test
    fun interpolateAtFullFractionReturnsUnwrappedTarget() {
        assertEquals(370f, interpolateHeading(350f, 10f, 1f), tolerance)
    }

    @Test
    fun interpolateHalfwayCrossesNorth() {
        assertEquals(360f, interpolateHeading(350f, 10f, 0.5f), tolerance)
    }

    @Test
    fun interpolateClampsFractionOutsideUnitInterval() {
        assertEquals(350f, interpolateHeading(350f, 10f, -1f), tolerance)
        assertEquals(370f, interpolateHeading(350f, 10f, 2f), tolerance)
    }
}
