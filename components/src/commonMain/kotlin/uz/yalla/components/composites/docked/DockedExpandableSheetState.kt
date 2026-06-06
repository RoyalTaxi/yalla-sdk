package uz.yalla.components.composites.docked

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

enum class DockedExpandableSheetValue {
    Collapsed,
    Expanded,
}

@OptIn(ExperimentalFoundationApi::class)
@Stable
class DockedExpandableSheetState internal constructor(
    initialValue: DockedExpandableSheetValue,
    internal val snapAnimationSpec: AnimationSpec<Float>,
    internal val density: Density,
    internal val positionalThreshold: (totalDistance: Float) -> Float = { it * 0.5f }
) {
    internal var collapsedHeightPx by mutableFloatStateOf(0f)
    internal var expandedHeightPx by mutableFloatStateOf(0f)
    internal var footerHeightPx by mutableFloatStateOf(0f)

    private val maxOffsetPx: Float get() = (expandedHeightPx - collapsedHeightPx).coerceAtLeast(0f)
    private val hasOffset: Boolean get() = maxOffsetPx > 0f

    internal val anchoredDraggableState: AnchoredDraggableState<DockedExpandableSheetValue> = AnchoredDraggableState(initialValue = initialValue)

    val collapsedHeight: Dp get() = with(density) { collapsedHeightPx.toDp() }
    val expandedHeight: Dp get() = with(density) { expandedHeightPx.toDp() }
    val footerHeight: Dp get() = with(density) { footerHeightPx.toDp() }
    val collapsedSheetHeight: Dp get() = collapsedHeight + footerHeight

    val currentValue: DockedExpandableSheetValue get() = anchoredDraggableState.currentValue
    val targetValue: DockedExpandableSheetValue get() = anchoredDraggableState.targetValue
    val isExpanded: Boolean get() = currentValue == DockedExpandableSheetValue.Expanded
    val isCollapsed: Boolean get() = currentValue == DockedExpandableSheetValue.Collapsed

    val fraction: Float by derivedStateOf {
        if (!hasOffset) {
            0f
        } else {
            val offset = anchoredDraggableState.offset
            if (offset.isNaN()) {
                if (isExpanded) 1f else 0f
            } else {
                (1f - (offset / maxOffsetPx)).coerceIn(0f, 1f)
            }
        }
    }

    internal val contentHeight: Dp by derivedStateOf { with(density) { contentHeightPx.toDp() } }

    internal val contentHeightPx: Float by derivedStateOf {
        if (!hasOffset) {
            collapsedHeightPx
        } else {
            val offset = anchoredDraggableState.offset
            if (offset.isNaN()) {
                if (isExpanded) expandedHeightPx else collapsedHeightPx
            } else {
                expandedHeightPx - offset
            }
        }
    }

    val isAnimating: Boolean get() = anchoredDraggableState.isAnimationRunning

    suspend fun expand() {
        if (hasOffset) anchoredDraggableState.animateTo(DockedExpandableSheetValue.Expanded)
    }

    suspend fun collapse() {
        if (hasOffset) anchoredDraggableState.animateTo(DockedExpandableSheetValue.Collapsed)
    }

    suspend fun toggle() {
        if (isCollapsed) expand() else collapse()
    }

    internal fun updateHeights(
        collapsedHeightPx: Int,
        expandedHeightPx: Int,
        footerHeightPx: Int
    ) {
        this.collapsedHeightPx = collapsedHeightPx.toFloat()
        this.expandedHeightPx = expandedHeightPx.toFloat()
        this.footerHeightPx = footerHeightPx.toFloat()

        val newMaxOffset = (this.expandedHeightPx - this.collapsedHeightPx).coerceAtLeast(0f)
        if (newMaxOffset > 0f) {
            val newAnchors = DraggableAnchors {
                DockedExpandableSheetValue.Expanded at 0f
                DockedExpandableSheetValue.Collapsed at newMaxOffset
            }
            anchoredDraggableState.updateAnchors(newAnchors, currentValue)
        }
    }

    internal suspend fun settle(velocity: Float) {
        val target = when {
            velocity < -500f -> DockedExpandableSheetValue.Expanded
            velocity > 500f -> DockedExpandableSheetValue.Collapsed
            fraction > 0.5f -> DockedExpandableSheetValue.Expanded
            else -> DockedExpandableSheetValue.Collapsed
        }
        anchoredDraggableState.animateTo(target)
    }
}

val DockedExpandableSheetSpringSpec: AnimationSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMedium
)

@Composable
fun rememberDockedExpandableSheetState(
    initialValue: DockedExpandableSheetValue = DockedExpandableSheetValue.Collapsed,
    snapAnimationSpec: AnimationSpec<Float> = DockedExpandableSheetSpringSpec,
    density: Density = LocalDensity.current,
    positionalThreshold: (totalDistance: Float) -> Float = { it * 0.5f }
): DockedExpandableSheetState = remember {
    DockedExpandableSheetState(
        initialValue = initialValue,
        snapAnimationSpec = snapAnimationSpec,
        density = density,
        positionalThreshold = positionalThreshold
    )
}
