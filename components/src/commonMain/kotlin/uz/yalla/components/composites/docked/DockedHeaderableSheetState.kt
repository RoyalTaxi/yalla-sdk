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

enum class DockedHeaderableSheetValue {
    Collapsed,
    Expanded
}

@OptIn(ExperimentalFoundationApi::class)
@Stable
class DockedHeaderableSheetState internal constructor(
    initialValue: DockedHeaderableSheetValue,
    internal val snapAnimationSpec: AnimationSpec<Float>,
    internal val density: Density,
    internal val positionalThreshold: (totalDistance: Float) -> Float = { it * 0.5f }
) {
    internal var headerHeightPx by mutableFloatStateOf(0f)
    internal var bodyHeightPx by mutableFloatStateOf(0f)
    internal var footerHeightPx by mutableFloatStateOf(0f)

    private val maxOffsetPx: Float get() = bodyHeightPx.coerceAtLeast(0f)
    private val hasOffset: Boolean get() = maxOffsetPx > 0f

    internal val anchoredDraggableState = AnchoredDraggableState<DockedHeaderableSheetValue>(initialValue)

    val headerHeight: Dp get() = with(density) { headerHeightPx.toDp() }
    val bodyHeight: Dp get() = with(density) { bodyHeightPx.toDp() }
    val footerHeight: Dp get() = with(density) { footerHeightPx.toDp() }
    val collapsedSheetHeight: Dp get() = headerHeight + footerHeight

    val currentValue: DockedHeaderableSheetValue get() = anchoredDraggableState.currentValue
    val targetValue: DockedHeaderableSheetValue get() = anchoredDraggableState.targetValue
    val isExpanded: Boolean get() = currentValue == DockedHeaderableSheetValue.Expanded
    val isCollapsed: Boolean get() = currentValue == DockedHeaderableSheetValue.Collapsed
    val isAnimating: Boolean get() = anchoredDraggableState.isAnimationRunning

    val fraction: Float by derivedStateOf {
        if (!hasOffset) return@derivedStateOf 0f
        val offset = anchoredDraggableState.offset
        when {
            offset.isNaN() -> if (isExpanded) 1f else 0f
            else -> (1f - (offset / maxOffsetPx)).coerceIn(0f, 1f)
        }
    }

    internal val bodyAlpha: Float by derivedStateOf {
        val t = fraction
        if (t < 0.5f) {
            4f * t * t * t
        } else {
            1f - (-2f * t + 2f).let { it * it * it } / 2f
        }
    }

    internal val visibleBodyHeightPx: Float by derivedStateOf {
        if (!hasOffset) return@derivedStateOf 0f
        val offset = anchoredDraggableState.offset
        when {
            offset.isNaN() -> if (isExpanded) bodyHeightPx else 0f
            else -> (bodyHeightPx - offset).coerceAtLeast(0f)
        }
    }

    suspend fun expand() {
        if (hasOffset) anchoredDraggableState.animateTo(DockedHeaderableSheetValue.Expanded)
    }

    suspend fun collapse() {
        if (hasOffset) anchoredDraggableState.animateTo(DockedHeaderableSheetValue.Collapsed)
    }

    suspend fun toggle() = if (isCollapsed) expand() else collapse()

    internal fun updateHeights(
        headerHeightPx: Int,
        bodyHeightPx: Int,
        footerHeightPx: Int
    ) {
        this.headerHeightPx = headerHeightPx.toFloat()
        this.bodyHeightPx = bodyHeightPx.toFloat()
        this.footerHeightPx = footerHeightPx.toFloat()

        val newMaxOffset = this.bodyHeightPx.coerceAtLeast(0f)
        if (newMaxOffset > 0f) {
            val anchors = DraggableAnchors {
                DockedHeaderableSheetValue.Expanded at 0f
                DockedHeaderableSheetValue.Collapsed at newMaxOffset
            }
            anchoredDraggableState.updateAnchors(anchors, currentValue)
        }
    }
}

@Composable
fun rememberDockedHeaderableSheetState(
    initialValue: DockedHeaderableSheetValue = DockedHeaderableSheetValue.Collapsed,
    snapAnimationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    ),
    density: Density = LocalDensity.current
) = remember {
    DockedHeaderableSheetState(
        initialValue = initialValue,
        snapAnimationSpec = snapAnimationSpec,
        density = density
    )
}
