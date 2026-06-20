package uz.yalla.components.composites.docked

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Pins the cubic ease-in-out [bodyAlphaFor] (finding H3): the two cubic halves must meet continuously
 * at the `t == 0.5f` seam, clamp at the 0/1 endpoints, and stay monotonic. Pure float math, so it is
 * unit-tested without a Compose clock.
 */
class DockedHeaderableSheetStateTest {
    private val tolerance = 1e-4f

    @Test
    fun endpointsAreZeroAndOne() {
        assertEquals(0f, bodyAlphaFor(0f), tolerance)
        assertEquals(1f, bodyAlphaFor(1f), tolerance)
    }

    @Test
    fun seamIsContinuousAtHalf() {
        assertEquals(0.5f, bodyAlphaFor(0.5f), tolerance)
        // Just-below and just-above the seam land on either side of 0.5 with no jump.
        assertTrue(bodyAlphaFor(0.4999f) < 0.5f)
        assertTrue(bodyAlphaFor(0.5001f) > 0.5f)
    }

    @Test
    fun isMonotonicAcrossTheRange() {
        var previous = bodyAlphaFor(0f)
        var t = 0.05f
        while (t <= 1f) {
            val current = bodyAlphaFor(t)
            assertTrue(current >= previous, "alpha decreased at t=$t ($current < $previous)")
            previous = current
            t += 0.05f
        }
    }

    @Test
    fun clampsOutOfRangeInput() {
        assertEquals(0f, bodyAlphaFor(-1f), tolerance)
        assertEquals(1f, bodyAlphaFor(2f), tolerance)
    }
}
