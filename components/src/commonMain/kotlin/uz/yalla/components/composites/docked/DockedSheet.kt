package uz.yalla.components.composites.docked

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import uz.yalla.design.theme.System

private const val HiddenFraction = 0.65f
private const val SlideDurationMillis = 250

@OptIn(FlowPreview::class)
@Composable
fun DockedSheet(
    modifier: Modifier = Modifier,
    hidden: Boolean = false,
    onPaddingChanged: ((PaddingValues) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val statusBarTopPx = WindowInsets.statusBars.getTop(density)
    var heightPx by remember { mutableIntStateOf(0) }

    val offset by animateFloatAsState(
        targetValue = if (hidden && heightPx > 0) heightPx * HiddenFraction else 0f,
        animationSpec = tween(durationMillis = SlideDurationMillis)
    )

    val currentOnPaddingChanged by rememberUpdatedState(onPaddingChanged)
    LaunchedEffect(statusBarTopPx) {
        var seeded = false
        snapshotFlow { heightPx }
            .filter { it > 0 }
            .debounce { if (seeded) 100L else 0L }
            .collect { measuredHeight ->
                seeded = true
                currentOnPaddingChanged?.invoke(
                    PaddingValues(
                        top = with(density) { statusBarTopPx.toDp() },
                        bottom = with(density) { measuredHeight.toDp() }
                    )
                )
            }
    }

    Card(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = System.color.background.base),
        modifier = modifier
            .graphicsLayer { translationY = offset }
            .onSizeChanged { heightPx = it.height }
    ) {
        content()
    }
}
