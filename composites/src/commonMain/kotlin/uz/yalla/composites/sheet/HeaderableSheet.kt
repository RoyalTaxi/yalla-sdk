package uz.yalla.composites.sheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import kotlin.math.roundToInt

/**
 * Color configuration for [HeaderableSheet].
 */
@Immutable
data class HeaderableSheetColors(
    val container: Color,
    val dragHandle: Color
)

/**
 * Dimension configuration for [HeaderableSheet].
 */
@Immutable
data class HeaderableSheetDimens(
    val shape: Shape,
    val cornerRadius: Dp,
    val dragHandleWidth: Dp,
    val dragHandleHeight: Dp,
    val dragHandleContainerHeight: Dp
)

/**
 * Default configuration values for [HeaderableSheet].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object HeaderableSheetDefaults {
    @Composable
    fun colors(
        container: Color = System.color.background.base,
        dragHandle: Color = System.color.background.tertiary
    ) = HeaderableSheetColors(
        container = container,
        dragHandle = dragHandle
    )

    fun dimens(
        cornerRadius: Dp = 28.dp,
        shape: Shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
        dragHandleWidth: Dp = 32.dp,
        dragHandleHeight: Dp = 4.dp,
        dragHandleContainerHeight: Dp = 32.dp
    ) = HeaderableSheetDimens(
        shape = shape,
        cornerRadius = cornerRadius,
        dragHandleWidth = dragHandleWidth,
        dragHandleHeight = dragHandleHeight,
        dragHandleContainerHeight = dragHandleContainerHeight
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
 * @param colors Color configuration, defaults to [HeaderableSheetDefaults.colors].
 * @param dimens Dimension configuration, defaults to [HeaderableSheetDefaults.dimens].
 *
 * @see HeaderableSheetState
 * @see rememberHeaderableSheetState
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeaderableSheet(
    state: HeaderableSheetState,
    header: @Composable () -> Unit,
    body: @Composable () -> Unit,
    footer: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    colors: HeaderableSheetColors = HeaderableSheetDefaults.colors(),
    dimens: HeaderableSheetDimens = HeaderableSheetDefaults.dimens()
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
    header: @Composable () -> Unit,
    body: @Composable () -> Unit,
    footer: @Composable () -> Unit
) {
    Layout(
        content = {
            Box(Modifier.fillMaxWidth()) { header() }
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
