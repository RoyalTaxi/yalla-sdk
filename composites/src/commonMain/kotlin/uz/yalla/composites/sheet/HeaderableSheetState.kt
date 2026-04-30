package uz.yalla.composites.sheet

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
import androidx.compose.ui.unit.dp

/**
 * Discrete states a [HeaderableSheet] can settle into.
 *
 * - [Collapsed]: sheet body is hidden; only header and footer are visible.
 * - [Expanded]: sheet body is fully visible between header and footer.
 *
 * Drag fraction between the two states is exposed via
 * [HeaderableSheetState.fraction] (0f at Collapsed, 1f at Expanded).
 */
enum class HeaderableSheetValue {
    Collapsed,
    Expanded,
}

/**
 * State holder for [HeaderableSheet]. Tracks the anchored-draggable
 * position between [HeaderableSheetValue.Collapsed] and
 * [HeaderableSheetValue.Expanded], and exposes derived metrics
 * ([fraction], [bodyAlpha], [visibleBodyHeight], [totalSheetHeight])
 * that the sheet's measure pass relies on.
 *
 * **Lifecycle:** create via [rememberHeaderableSheetState] from a
 * Composable scope. The internal `AnchoredDraggableState` is constructed
 * eagerly with the supplied [initialValue]; its anchors are populated on
 * first measure via [updateHeights] (called by `HeaderableSheetLayout`).
 *
 * **Threading:** [expand] / [collapse] / [toggle] / [settle] suspend on
 * the spring animation. Call from a Composable scope or a `LaunchedEffect`.
 */
@OptIn(ExperimentalFoundationApi::class)
@Stable
class HeaderableSheetState internal constructor(
    initialValue: HeaderableSheetValue,
    internal val snapAnimationSpec: AnimationSpec<Float>,
    internal val density: Density,
    internal val positionalThreshold: (totalDistance: Float) -> Float = { it * 0.5f },
    internal val velocityThreshold: () -> Float = { with(density) { 125.dp.toPx() } }
) {
    internal var headerHeightPx by mutableFloatStateOf(0f)
    internal var bodyHeightPx by mutableFloatStateOf(0f)
    internal var footerHeightPx by mutableFloatStateOf(0f)

    private val maxOffsetPx: Float
        get() = bodyHeightPx.coerceAtLeast(0f)

    private val hasOffset: Boolean
        get() = maxOffsetPx > 0f

    internal val anchoredDraggableState = AnchoredDraggableState<HeaderableSheetValue>(initialValue)

    val headerHeight: Dp get() = with(density) { headerHeightPx.toDp() }
    val bodyHeight: Dp get() = with(density) { bodyHeightPx.toDp() }
    val footerHeight: Dp get() = with(density) { footerHeightPx.toDp() }
    val collapsedSheetHeight: Dp get() = headerHeight + footerHeight

    val currentValue: HeaderableSheetValue get() = anchoredDraggableState.currentValue
    val targetValue: HeaderableSheetValue get() = anchoredDraggableState.targetValue

    /**
     * Drag fraction in `[0f, 1f]`. `0f` = Collapsed, `1f` = Expanded.
     * Returns `0f` until [updateHeights] supplies non-zero body height.
     */
    val fraction: Float by derivedStateOf {
        if (!hasOffset) return@derivedStateOf 0f
        val offset = anchoredDraggableState.offset
        when {
            offset.isNaN() -> if (currentValue == HeaderableSheetValue.Expanded) 1f else 0f
            else -> (1f - (offset / maxOffsetPx)).coerceIn(0f, 1f)
        }
    }

    /**
     * Cubic-eased alpha curve over [fraction] for fading the body in/out.
     * Use with `Modifier.alpha(state.bodyAlpha)`.
     */
    val bodyAlpha: Float by derivedStateOf {
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
            offset.isNaN() -> if (currentValue == HeaderableSheetValue.Expanded) bodyHeightPx else 0f
            else -> (bodyHeightPx - offset).coerceAtLeast(0f)
        }
    }

    val visibleBodyHeight: Dp by derivedStateOf { with(density) { visibleBodyHeightPx.toDp() } }
    val totalSheetHeight: Dp by derivedStateOf { headerHeight + visibleBodyHeight + footerHeight }

    val isAnimating: Boolean get() = anchoredDraggableState.isAnimationRunning
    val isExpanded: Boolean get() = currentValue == HeaderableSheetValue.Expanded
    val isCollapsed: Boolean get() = currentValue == HeaderableSheetValue.Collapsed

    suspend fun expand() {
        if (hasOffset) anchoredDraggableState.animateTo(HeaderableSheetValue.Expanded)
    }

    suspend fun collapse() {
        if (hasOffset) anchoredDraggableState.animateTo(HeaderableSheetValue.Collapsed)
    }

    suspend fun toggle() = if (isCollapsed) expand() else collapse()

    /**
     * Re-measure callback: feed the latest header / body / footer heights
     * (in px) so the anchor map can be rebuilt. No-op when the body height
     * is zero (anchors require a non-zero offset range).
     */
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
            val anchors =
                DraggableAnchors {
                    HeaderableSheetValue.Expanded at 0f
                    HeaderableSheetValue.Collapsed at newMaxOffset
                }
            anchoredDraggableState.updateAnchors(anchors, currentValue)
        }
    }

    /**
     * Velocity-aware settle: positive velocity > 500 collapses, negative <
     * -500 expands; otherwise snap to whichever side of the 0.5 fraction
     * boundary the offset currently sits on.
     */
    internal suspend fun settle(velocity: Float) {
        val target =
            when {
                velocity < -500f -> HeaderableSheetValue.Expanded
                velocity > 500f -> HeaderableSheetValue.Collapsed
                fraction > 0.5f -> HeaderableSheetValue.Expanded
                else -> HeaderableSheetValue.Collapsed
            }
        anchoredDraggableState.animateTo(target)
    }
}

/**
 * Construct a [HeaderableSheetState] inside a Composable scope. The state
 * is `remember`-cached against the initial parameters; pass it to
 * [HeaderableSheet] as the `state` arg.
 */
@Composable
fun rememberHeaderableSheetState(
    initialValue: HeaderableSheetValue = HeaderableSheetValue.Collapsed,
    snapAnimationSpec: AnimationSpec<Float> =
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
    density: Density = LocalDensity.current
) = remember {
    HeaderableSheetState(
        initialValue = initialValue,
        snapAnimationSpec = snapAnimationSpec,
        density = density
    )
}
