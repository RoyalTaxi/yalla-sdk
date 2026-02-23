package uz.yalla.components.composite.sheet

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

/**
 * Value states for expandable sheet.
 */
enum class ExpandableSheetValue {
    Collapsed,
    Expanded,
}

/**
 * State holder for expandable bottom sheets.
 *
 * Manages draggable sheet behavior with collapsed and expanded anchors.
 *
 * ## Usage
 *
 * ```kotlin
 * val sheetState = rememberExpandableSheetState(
 *     initialValue = ExpandableSheetValue.Collapsed,
 *     density = LocalDensity.current,
 * )
 *
 * LaunchedEffect(selectedTab) {
 *     if (selectedTab == Tab.Details) {
 *         sheetState.expand()
 *     }
 * }
 * ```
 *
 * @see rememberExpandableSheetState
 */
@OptIn(ExperimentalFoundationApi::class)
@Stable
class ExpandableSheetState internal constructor(
    initialValue: ExpandableSheetValue,
    /** Animation spec for snapping to anchors. */
    val snapAnimationSpec: AnimationSpec<Float>,
    internal val density: Density,
    /** Threshold function for determining snap position. */
    val positionalThreshold: (totalDistance: Float) -> Float = { it * 0.5f },
) {
    internal var collapsedHeightPx by mutableFloatStateOf(0f)
    internal var expandedHeightPx by mutableFloatStateOf(0f)
    internal var footerHeightPx by mutableFloatStateOf(0f)

    private val maxOffsetPx: Float get() = (expandedHeightPx - collapsedHeightPx).coerceAtLeast(0f)
    private val hasOffset: Boolean get() = maxOffsetPx > 0f

    /** The anchored draggable state for gesture handling. */
    val anchoredDraggableState: AnchoredDraggableState<ExpandableSheetValue> =
        AnchoredDraggableState(initialValue = initialValue)

    /** Collapsed content height in Dp. */
    val collapsedHeight: Dp get() = with(density) { collapsedHeightPx.toDp() }

    /** Expanded content height in Dp. */
    val expandedHeight: Dp get() = with(density) { expandedHeightPx.toDp() }

    /** Footer height in Dp. */
    val footerHeight: Dp get() = with(density) { footerHeightPx.toDp() }

    /** Total collapsed sheet height (content + footer). */
    val collapsedSheetHeight: Dp get() = collapsedHeight + footerHeight

    /** Current sheet value. */
    val currentValue: ExpandableSheetValue get() = anchoredDraggableState.currentValue

    /** Target sheet value during animations. */
    val targetValue: ExpandableSheetValue get() = anchoredDraggableState.targetValue

    /** Progress from 0 (collapsed) to 1 (expanded). */
    val fraction: Float by derivedStateOf {
        if (!hasOffset) {
            0f
        } else {
            val offset = anchoredDraggableState.offset
            if (offset.isNaN()) {
                if (currentValue == ExpandableSheetValue.Expanded) 1f else 0f
            } else {
                (1f - (offset / maxOffsetPx)).coerceIn(0f, 1f)
            }
        }
    }

    /** Current content height in Dp. */
    val contentHeight: Dp by derivedStateOf {
        with(density) { contentHeightPx.toDp() }
    }

    /** Current content height in pixels. */
    val contentHeightPx: Float by derivedStateOf {
        if (!hasOffset) {
            collapsedHeightPx
        } else {
            val offset = anchoredDraggableState.offset
            if (offset.isNaN()) {
                if (currentValue == ExpandableSheetValue.Expanded) expandedHeightPx else collapsedHeightPx
            } else {
                expandedHeightPx - offset
            }
        }
    }

    /** Whether sheet is animating. */
    val isAnimating: Boolean get() = anchoredDraggableState.isAnimationRunning

    /** Expand the sheet. */
    suspend fun expand() {
        if (hasOffset) anchoredDraggableState.animateTo(ExpandableSheetValue.Expanded)
    }

    /** Collapse the sheet. */
    suspend fun collapse() {
        if (hasOffset) anchoredDraggableState.animateTo(ExpandableSheetValue.Collapsed)
    }

    /** Toggle between states. */
    suspend fun toggle() {
        if (currentValue == ExpandableSheetValue.Collapsed) expand() else collapse()
    }

    /** Update measured heights and reconfigure anchors. */
    fun updateHeights(
        collapsedHeightPx: Int,
        expandedHeightPx: Int,
        footerHeightPx: Int,
    ) {
        this.collapsedHeightPx = collapsedHeightPx.toFloat()
        this.expandedHeightPx = expandedHeightPx.toFloat()
        this.footerHeightPx = footerHeightPx.toFloat()

        val newMaxOffset = (this.expandedHeightPx - this.collapsedHeightPx).coerceAtLeast(0f)

        if (newMaxOffset > 0f) {
            val newAnchors =
                DraggableAnchors {
                    ExpandableSheetValue.Expanded at 0f
                    ExpandableSheetValue.Collapsed at newMaxOffset
                }
            anchoredDraggableState.updateAnchors(newAnchors, currentValue)
        }
    }

    /** Settle the sheet to nearest anchor based on velocity. */
    suspend fun settle(velocity: Float) {
        val targetValue =
            when {
                velocity < -500f -> ExpandableSheetValue.Expanded
                velocity > 500f -> ExpandableSheetValue.Collapsed
                fraction > 0.5f -> ExpandableSheetValue.Expanded
                else -> ExpandableSheetValue.Collapsed
            }
        anchoredDraggableState.animateTo(targetValue)
    }
}

/**
 * Spring animation for expandable sheet.
 */
val ExpandableSheetSpringSpec: AnimationSpec<Float> =
    spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

/**
 * Create and remember an [ExpandableSheetState].
 *
 * @param initialValue Initial sheet state
 * @param snapAnimationSpec Animation for snapping
 * @param density Current density
 * @param positionalThreshold Threshold for snapping based on position
 */
@Composable
fun rememberExpandableSheetState(
    initialValue: ExpandableSheetValue = ExpandableSheetValue.Collapsed,
    snapAnimationSpec: AnimationSpec<Float> = ExpandableSheetSpringSpec,
    density: Density,
    positionalThreshold: (totalDistance: Float) -> Float = { it * 0.5f },
): ExpandableSheetState =
    remember {
        ExpandableSheetState(
            initialValue = initialValue,
            snapAnimationSpec = snapAnimationSpec,
            density = density,
            positionalThreshold = positionalThreshold,
        )
    }

/**
 * Modifier extension for anchored draggable behavior.
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.expandableSheetDraggable(state: ExpandableSheetState): Modifier =
    this.anchoredDraggable(
        state = state.anchoredDraggableState,
        orientation = Orientation.Vertical,
    )
