package uz.yalla.composites.sheet

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import kotlin.math.roundToInt

/**
 * Values for headerable sheet state.
 */
enum class HeaderableSheetValue { Collapsed, Expanded }

/**
 * Default configuration values for [HeaderableSheet].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object HeaderableSheetDefaults {
    /**
     * Color configuration for [HeaderableSheet].
     *
     * @param container Card background color.
     * @param dragHandle Color of the drag handle.
     */
    data class HeaderableSheetColors(
        val container: Color,
        val dragHandle: Color
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundBase,
        dragHandle: Color = System.color.backgroundTertiary
    ) = HeaderableSheetColors(
        container = container,
        dragHandle = dragHandle
    )

    /**
     * Dimension configuration for [HeaderableSheet].
     *
     * @param shape Card shape.
     * @param cornerRadius Corner radius of the card.
     * @param dragHandleWidth Width of the drag handle.
     * @param dragHandleHeight Height of the drag handle.
     * @param dragHandleContainerHeight Height of the drag handle container.
     */
    data class HeaderableSheetDimens(
        val shape: Shape,
        val cornerRadius: Dp,
        val dragHandleWidth: Dp,
        val dragHandleHeight: Dp,
        val dragHandleContainerHeight: Dp
    )

    @Composable
    fun dimens(
        cornerRadius: Dp = 38.dp,
        shape: Shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
        dragHandleWidth: Dp = 36.dp,
        dragHandleHeight: Dp = 5.dp,
        dragHandleContainerHeight: Dp = 16.dp
    ) = HeaderableSheetDimens(
        shape = shape,
        cornerRadius = cornerRadius,
        dragHandleWidth = dragHandleWidth,
        dragHandleHeight = dragHandleHeight,
        dragHandleContainerHeight = dragHandleContainerHeight
    )
}

/**
 * State for [HeaderableSheet].
 *
 * @param initialValue Initial sheet state.
 * @param snapAnimationSpec Animation spec for snapping.
 * @param density Screen density.
 * @param positionalThreshold Threshold for position-based snapping.
 * @param velocityThreshold Threshold for velocity-based snapping.
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

    val fraction: Float by derivedStateOf {
        if (!hasOffset) return@derivedStateOf 0f
        val offset = anchoredDraggableState.offset
        when {
            offset.isNaN() -> if (currentValue == HeaderableSheetValue.Expanded) 1f else 0f
            else -> (1f - (offset / maxOffsetPx)).coerceIn(0f, 1f)
        }
    }

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
 * Remember a [HeaderableSheetState].
 *
 * @param initialValue Initial sheet state.
 * @param snapAnimationSpec Animation spec for snapping.
 * @param density Screen density.
 * @return Remembered sheet state.
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

/**
 * Bottom sheet with separate header, body, and footer sections.
 *
 * The body section collapses/expands while header and footer remain visible.
 *
 * ## Usage
 *
 * ```kotlin
 * val sheetState = rememberHeaderableSheetState()
 *
 * HeaderableSheet(
 *     state = sheetState,
 *     header = { HeaderContent() },
 *     body = { BodyContent() },
 *     footer = { FooterContent() }
 * )
 * ```
 *
 * @param state Sheet state.
 * @param header Header content (always visible).
 * @param body Body content (collapses/expands).
 * @param footer Footer content (always visible).
 * @param modifier Applied to sheet.
 * @param colors Color configuration, defaults to [HeaderableSheetDefaults.colors].
 * @param dimens Dimension configuration, defaults to [HeaderableSheetDefaults.dimens].
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeaderableSheet(
    state: HeaderableSheetState,
    header: @Composable () -> Unit,
    body: @Composable () -> Unit,
    footer: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    colors: HeaderableSheetDefaults.HeaderableSheetColors = HeaderableSheetDefaults.colors(),
    dimens: HeaderableSheetDefaults.HeaderableSheetDimens = HeaderableSheetDefaults.dimens()
) {
    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density)
    val flingBehavior =
        AnchoredDraggableDefaults.flingBehavior(
            state = state.anchoredDraggableState,
            positionalThreshold = state.positionalThreshold,
            animationSpec = state.snapAnimationSpec
        )

    val cardColors = CardDefaults.cardColors(containerColor = colors.container)

    Box(modifier.fillMaxSize(), Alignment.BottomCenter) {
        Card(
            shape = dimens.shape,
            colors = cardColors,
            modifier =
                Modifier.anchoredDraggable(
                    state = state.anchoredDraggableState,
                    orientation = Orientation.Vertical,
                    flingBehavior = flingBehavior
                )
        ) {
            HeaderableSheetLayout(
                state = state,
                statusBarHeightPx = statusBarHeightPx,
                colors = colors,
                dimens = dimens,
                header = header,
                body = body,
                footer = footer
            )
        }
    }
}

@Composable
private fun HeaderableSheetLayout(
    state: HeaderableSheetState,
    statusBarHeightPx: Int,
    colors: HeaderableSheetDefaults.HeaderableSheetColors,
    dimens: HeaderableSheetDefaults.HeaderableSheetDimens,
    header: @Composable () -> Unit,
    body: @Composable () -> Unit,
    footer: @Composable () -> Unit
) {
    Layout(
        content = {
            Box(Modifier.fillMaxWidth()) {
                header()
                DragHandle(
                    colors = colors,
                    dimens = dimens,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
            Box(Modifier.fillMaxWidth().alpha(state.bodyAlpha)) { body() }
            Box(Modifier.fillMaxWidth()) { footer() }
        }
    ) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)

        val headerPlaceable = measurables[0].measure(loose)
        val footerPlaceable = measurables[2].measure(loose)

        val maxBodyHeight =
            (constraints.maxHeight - statusBarHeightPx - headerPlaceable.height - footerPlaceable.height)
                .coerceAtLeast(0)

        val bodyPlaceable = measurables[1].measure(loose.copy(maxHeight = maxBodyHeight))

        state.updateHeights(headerPlaceable.height, bodyPlaceable.height, footerPlaceable.height)

        val visibleBodyHeight = state.visibleBodyHeightPx.roundToInt()
        val totalHeight = headerPlaceable.height + visibleBodyHeight + footerPlaceable.height

        layout(constraints.maxWidth, totalHeight) {
            headerPlaceable.placeRelative(0, 0)
            if (visibleBodyHeight > 0) bodyPlaceable.placeRelative(0, headerPlaceable.height)
            footerPlaceable.placeRelative(0, headerPlaceable.height + visibleBodyHeight)
        }
    }
}

@Composable
private fun DragHandle(
    colors: HeaderableSheetDefaults.HeaderableSheetColors,
    dimens: HeaderableSheetDefaults.HeaderableSheetDimens,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(width = dimens.dragHandleWidth, height = dimens.dragHandleContainerHeight)
    ) {
        Box(
            modifier =
                Modifier
                    .background(shape = CircleShape, color = colors.dragHandle)
                    .size(width = dimens.dragHandleWidth, height = dimens.dragHandleHeight)
        )
    }
}
