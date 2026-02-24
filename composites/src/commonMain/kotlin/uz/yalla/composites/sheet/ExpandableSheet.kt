package uz.yalla.composites.sheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Default configuration values for [ExpandableSheet].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object ExpandableSheetDefaults {
    /**
     * Color configuration for [ExpandableSheet].
     *
     * @param container Card background color.
     */
    data class ExpandableSheetColors(
        val container: Color
    )

    @Composable
    fun colors(container: Color = System.color.backgroundBase) =
        ExpandableSheetColors(
            container = container
        )

    /**
     * Dimension configuration for [ExpandableSheet].
     *
     * @param shape Card shape.
     * @param cornerRadius Corner radius of the card.
     */
    data class ExpandableSheetDimens(
        val shape: Shape,
        val cornerRadius: Dp
    )

    @Composable
    fun dimens(
        cornerRadius: Dp = 38.dp,
        shape: Shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
    ) = ExpandableSheetDimens(
        shape = shape,
        cornerRadius = cornerRadius
    )
}

/**
 * Expandable bottom sheet with collapsed and expanded content.
 *
 * ## Usage
 *
 * ```kotlin
 * val state = rememberExpandableSheetState()
 *
 * ExpandableSheet(
 *     state = state,
 *     collapsedContent = { CollapsedView() },
 *     expandedContent = { ExpandedView() },
 *     footer = { FooterButtons() }
 * )
 * ```
 *
 * @param state Sheet state for controlling expansion.
 * @param collapsedContent Content shown when collapsed.
 * @param expandedContent Content shown when expanded.
 * @param modifier Applied to the sheet.
 * @param colors Color configuration, defaults to [ExpandableSheetDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ExpandableSheetDefaults.dimens].
 * @param footer Optional footer content.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandableSheet(
    state: ExpandableSheetState,
    collapsedContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    colors: ExpandableSheetDefaults.ExpandableSheetColors = ExpandableSheetDefaults.colors(),
    dimens: ExpandableSheetDefaults.ExpandableSheetDimens = ExpandableSheetDefaults.dimens(),
    footer: (@Composable () -> Unit)? = null
) {
    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density)

    val draggableState = state.anchoredDraggableState

    val flingBehavior =
        AnchoredDraggableDefaults.flingBehavior(
            state = draggableState,
            positionalThreshold = state.positionalThreshold,
            animationSpec = state.snapAnimationSpec
        )

    val cardColors = CardDefaults.cardColors(containerColor = colors.container)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            shape = dimens.shape,
            colors = cardColors,
            modifier =
                Modifier.anchoredDraggable(
                    state = draggableState,
                    orientation = Orientation.Vertical,
                    flingBehavior = flingBehavior
                )
        ) {
            SubcomposeLayout { constraints ->
                val looseConstraints = constraints.loosen()

                val footerPlaceable =
                    footer?.let {
                        subcomposeMeasured(
                            slotId = "footer",
                            constraints = looseConstraints,
                            content = { Box(Modifier.fillMaxWidth()) { it() } }
                        )
                    }
                val footerHeightPx = footerPlaceable?.height ?: 0

                val maxExpandedHeightPx = constraints.maxHeight - statusBarHeightPx - footerHeightPx

                val collapsedPlaceable =
                    subcomposeMeasured(
                        slotId = "collapsed",
                        constraints = looseConstraints,
                        content = { Box(Modifier.fillMaxWidth()) { collapsedContent() } }
                    )

                val expandedConstraints =
                    looseConstraints.copy(
                        maxHeight = maxExpandedHeightPx.coerceAtLeast(0)
                    )

                val expandedPlaceable =
                    subcomposeMeasured(
                        slotId = "expanded",
                        constraints = expandedConstraints,
                        content = { Box(Modifier.fillMaxWidth()) { expandedContent() } }
                    )

                state.updateHeights(
                    collapsedHeightPx = collapsedPlaceable.height,
                    expandedHeightPx = expandedPlaceable.height,
                    footerHeightPx = footerHeightPx
                )

                val contentHeight = state.contentHeight
                val contentHeightPx = state.contentHeightPx.toInt()
                val totalHeight = contentHeightPx + footerHeightPx
                val width = constraints.maxWidth

                layout(width, totalHeight) {
                    subcomposeMeasuredWithAlpha(
                        slotId = "collapsedVisible",
                        width = width,
                        contentHeight = contentHeight,
                        contentHeightPx = contentHeightPx,
                        alpha = 1f - state.fraction,
                        content = collapsedContent
                    )?.placeRelative(0, 0)

                    subcomposeMeasuredWithAlpha(
                        slotId = "expandedVisible",
                        width = width,
                        contentHeight = contentHeight,
                        contentHeightPx = contentHeightPx,
                        alpha = state.fraction,
                        content = expandedContent
                    )?.placeRelative(0, 0)

                    footerPlaceable?.placeRelative(0, contentHeightPx)
                }
            }
        }
    }
}

private fun Constraints.loosen(): Constraints = copy(minWidth = 0, minHeight = 0)

private fun SubcomposeMeasureScope.subcomposeMeasured(
    slotId: String,
    constraints: Constraints,
    content: @Composable () -> Unit
): Placeable = subcompose(slotId, content).first().measure(constraints)

private fun SubcomposeMeasureScope.subcomposeMeasuredWithAlpha(
    slotId: String,
    width: Int,
    contentHeight: Dp,
    contentHeightPx: Int,
    alpha: Float,
    content: @Composable () -> Unit
): Placeable? =
    if (alpha <= 0f) {
        null
    } else {
        subcompose(slotId) {
            Box(
                content = { content() },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(contentHeight)
                        .graphicsLayer { this.alpha = alpha }
            )
        }.first().measure(Constraints.fixed(width, contentHeightPx))
    }
