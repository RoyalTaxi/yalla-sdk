package uz.yalla.components.composites.docked

import androidx.compose.animation.core.snap
import androidx.compose.ui.unit.Density
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins the pure snap-target math of [DockedExpandableSheetState.settleTarget] (finding M4): the
 * configured [positionalThreshold] must actually drive the release/fling snap, not a hardcoded `0.5f`,
 * and the velocity threshold must win over position. Constructed without a Compose clock — `settleTarget`
 * is a pure function of velocity, fraction, and the configured threshold.
 */
class DockedExpandableSheetStateTest {
    private fun state(
        positionalThreshold: (Float) -> Float = { it * 0.5f }
    ): DockedExpandableSheetState =
        DockedExpandableSheetState(
            initialValue = DockedExpandableSheetValue.Collapsed,
            snapAnimationSpec = snap(),
            density = Density(1f),
            positionalThreshold = positionalThreshold
        ).also { it.updateHeights(collapsedHeightPx = 100, expandedHeightPx = 500, footerHeightPx = 0) }

    @Test
    fun fastUpwardFlingExpandsRegardlessOfPosition() {
        val s = state()
        assertEquals(
            DockedExpandableSheetValue.Expanded,
            s.settleTarget(velocity = -600f, currentFraction = 0f)
        )
    }

    @Test
    fun fastDownwardFlingCollapsesRegardlessOfPosition() {
        val s = state()
        assertEquals(
            DockedExpandableSheetValue.Collapsed,
            s.settleTarget(velocity = 600f, currentFraction = 1f)
        )
    }

    @Test
    fun defaultHalfThresholdSnapsByPosition() {
        val s = state()
        assertEquals(
            DockedExpandableSheetValue.Expanded,
            s.settleTarget(velocity = 0f, currentFraction = 0.6f)
        )
        assertEquals(
            DockedExpandableSheetValue.Collapsed,
            s.settleTarget(velocity = 0f, currentFraction = 0.4f)
        )
    }

    @Test
    fun customThirtyPercentThresholdDrivesTheSnap() {
        // With { it * 0.3f } a 0.4 drag should now expand (it would collapse under the old hardcoded 0.5f).
        val s = state(positionalThreshold = { it * 0.3f })
        assertEquals(
            DockedExpandableSheetValue.Expanded,
            s.settleTarget(velocity = 0f, currentFraction = 0.4f)
        )
        // And below 0.3 it still collapses.
        assertEquals(
            DockedExpandableSheetValue.Collapsed,
            s.settleTarget(velocity = 0f, currentFraction = 0.2f)
        )
    }

    @Test
    fun equalHeightsHaveNoOffsetSoFractionIsZero() {
        val s =
            DockedExpandableSheetState(
                initialValue = DockedExpandableSheetValue.Collapsed,
                snapAnimationSpec = snap(),
                density = Density(1f),
                positionalThreshold = { it * 0.5f }
            ).also { it.updateHeights(collapsedHeightPx = 300, expandedHeightPx = 300, footerHeightPx = 0) }
        assertEquals(0f, s.fraction)
    }
}
