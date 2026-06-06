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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter
import uz.yalla.design.theme.System
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DockedHeaderableSheet(
    state: DockedHeaderableSheetState,
    header: @Composable () -> Unit,
    body: @Composable () -> Unit,
    footer: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onPaddingChanged: ((PaddingValues) -> Unit)? = null
) {
    val density = LocalDensity.current
    val statusBarTopPx = WindowInsets.statusBars.getTop(density)
    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = state.anchoredDraggableState,
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

    Box(modifier.fillMaxSize(), Alignment.BottomCenter) {
        Card(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = System.color.background.base),
            modifier = Modifier.anchoredDraggable(
                state = state.anchoredDraggableState,
                orientation = Orientation.Vertical,
                flingBehavior = flingBehavior
            )
        ) {
            DockedHeaderableSheetLayout(
                state = state,
                statusBarTopPx = statusBarTopPx,
                header = header,
                body = body,
                footer = footer
            )
        }
    }
}

@Composable
private fun DockedHeaderableSheetLayout(
    state: DockedHeaderableSheetState,
    statusBarTopPx: Int,
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

        val maxBodyHeight = (constraints.maxHeight - statusBarTopPx - headerPlaceable.height - footerPlaceable.height)
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
