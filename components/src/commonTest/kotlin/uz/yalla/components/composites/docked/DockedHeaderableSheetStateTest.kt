package uz.yalla.components.composites.docked

import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.Density
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

    @Test
    fun collapsedOnlySheetDoesNotEnableDragging() {
        val state = dockedState()

        state.updateHeights(headerHeightPx = 100, bodyHeightPx = 0, footerHeightPx = 50)

        assertFalse(state.draggableEnabled)
    }

    @Test
    fun sheetWithBodyEnablesDragging() {
        val state = dockedState()

        state.updateHeights(headerHeightPx = 100, bodyHeightPx = 200, footerHeightPx = 50)

        assertTrue(state.draggableEnabled)
    }
}

private fun dockedState() =
    DockedHeaderableSheetState(
        initialValue = DockedHeaderableSheetValue.Collapsed,
        snapAnimationSpec = spring(),
        density = Density(1f)
    )
