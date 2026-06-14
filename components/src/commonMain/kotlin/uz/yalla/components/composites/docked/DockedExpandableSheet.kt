package uz.yalla.components.composites.docked

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter
import uz.yalla.design.theme.System

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun DockedExpandableSheet(
    state: DockedExpandableSheetState,
    collapsedContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    footer: (@Composable () -> Unit)? = null,
    onPaddingChanged: ((PaddingValues) -> Unit)? = null
) {
    val density = LocalDensity.current
    val statusBarTopPx = WindowInsets.statusBars.getTop(density)
    val draggableState = state.anchoredDraggableState

    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = draggableState,
        positionalThreshold = state.positionalThreshold,
        animationSpec = state.snapAnimationSpec
    )

    val currentOnPaddingChanged by rememberUpdatedState(onPaddingChanged)
    LaunchedEffect(statusBarTopPx) {
        snapshotFlow { state.collapsedSheetHeight }
            .filter { it > 0.dp }
            .collect { collapsed ->
                currentOnPaddingChanged?.invoke(
                    PaddingValues(
                        top = with(density) { statusBarTopPx.toDp() },
                        bottom = collapsed
                    )
                )
            }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = System.color.background.base),
            modifier = Modifier.anchoredDraggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                flingBehavior = flingBehavior
            )
        ) {
            SubcomposeLayout { constraints ->
                val looseConstraints = constraints.loosen()

                val footerPlaceable = footer?.let {
                    subcomposeMeasured(
                        slotId = "footer",
                        constraints = looseConstraints,
                        content = { Box(Modifier.fillMaxWidth()) { it() } }
                    )
                }
                val footerHeightPx = footerPlaceable?.height ?: 0

                val maxExpandedHeightPx = constraints.maxHeight - statusBarTopPx - footerHeightPx

                val collapsedPlaceable = subcomposeMeasured(
                    slotId = "collapsed",
                    constraints = looseConstraints,
                    content = { Box(Modifier.fillMaxWidth()) { collapsedContent() } }
                )

                val expandedConstraints = looseConstraints.copy(maxHeight = maxExpandedHeightPx.coerceAtLeast(0))

                val expandedPlaceable = subcomposeMeasured(
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
): Placeable? = if (alpha <= 0f) {
    null
} else {
    subcompose(slotId) {
        Box(
            content = { content() },
            modifier = Modifier
                .fillMaxWidth()
                .height(contentHeight)
                .graphicsLayer { this.alpha = alpha }
        )
    }.first().measure(Constraints.fixed(width, contentHeightPx))
}
