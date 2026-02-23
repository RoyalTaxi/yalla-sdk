package uz.yalla.components.composite.sheet

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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import uz.yalla.design.theme.System

/**
 * Default configuration values for [BottomSheetCard].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object BottomSheetCardDefaults {
    /**
     * Color configuration for [BottomSheetCard].
     *
     * @param container Card background color.
     */
    data class BottomSheetCardColors(
        val container: Color
    )

    @Composable
    fun colors(container: Color = System.color.backgroundBase) =
        BottomSheetCardColors(
            container = container
        )

    /**
     * Dimension configuration for [BottomSheetCard].
     *
     * @param shape Card shape.
     * @param cornerRadius Corner radius of the card.
     */
    data class BottomSheetCardDimens(
        val shape: Shape,
        val cornerRadius: Dp
    )

    @Composable
    fun dimens(
        cornerRadius: Dp = 38.dp,
        shape: Shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
    ) = BottomSheetCardDimens(
        shape = shape,
        cornerRadius = cornerRadius
    )

    /**
     * Animation configuration for [BottomSheetCard].
     *
     * @param durationMillis Animation duration in milliseconds.
     * @param collapsedFraction Fraction of height to offset when collapsed.
     */
    data class BottomSheetCardAnimation(
        val durationMillis: Int,
        val collapsedFraction: Float
    )

    fun animation(
        durationMillis: Int = 250,
        collapsedFraction: Float = 0.65f
    ) = BottomSheetCardAnimation(
        durationMillis = durationMillis,
        collapsedFraction = collapsedFraction
    )
}

/**
 * Card styled as a bottom sheet with offset animation support.
 *
 * ## Usage
 *
 * ```kotlin
 * val offset by animateSheetOffset(isCollapsed, sheetHeight)
 *
 * BottomSheetCard(
 *     offset = offset,
 *     onHeightChanged = { sheetHeight = it }
 * ) {
 *     // Sheet content
 * }
 * ```
 *
 * @param offset Vertical offset for animation.
 * @param onHeightChanged Called when sheet height changes.
 * @param modifier Applied to card.
 * @param colors Color configuration, defaults to [BottomSheetCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [BottomSheetCardDefaults.dimens].
 * @param content Card content.
 */
@Composable
fun BottomSheetCard(
    offset: Float,
    onHeightChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: BottomSheetCardDefaults.BottomSheetCardColors = BottomSheetCardDefaults.colors(),
    dimens: BottomSheetCardDefaults.BottomSheetCardDimens = BottomSheetCardDefaults.dimens(),
    content: @Composable () -> Unit
) {
    Card(
        shape = dimens.shape,
        colors = CardDefaults.cardColors(containerColor = colors.container),
        modifier =
            modifier
                .graphicsLayer { translationY = offset }
                .onSizeChanged { onHeightChanged(it.height) },
        content = { content() }
    )
}

/**
 * Animates sheet offset based on collapsed state.
 *
 * @param isCollapsed Whether sheet is collapsed.
 * @param sheetHeight Current sheet height in pixels.
 * @param animation Animation configuration, defaults to [BottomSheetCardDefaults.animation].
 * @return Animated offset state.
 */
@Composable
fun animateSheetOffset(
    isCollapsed: Boolean,
    sheetHeight: Int,
    animation: BottomSheetCardDefaults.BottomSheetCardAnimation = BottomSheetCardDefaults.animation()
): State<Float> =
    animateFloatAsState(
        animationSpec = tween(durationMillis = animation.durationMillis),
        targetValue =
            when {
                sheetHeight == 0 -> 0f
                isCollapsed -> sheetHeight * animation.collapsedFraction
                else -> 0f
            }
    )

/**
 * Effect that updates padding based on sheet height changes.
 *
 * @param sheetHeight Mutable sheet height state.
 * @param enabled Whether effect is enabled.
 * @param onPaddingChanged Called with new padding values.
 */
@OptIn(FlowPreview::class)
@Composable
fun SheetPaddingEffect(
    sheetHeight: MutableIntState,
    enabled: Boolean = true,
    onPaddingChanged: (PaddingValues) -> Unit
) {
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.getTop(density)

    LaunchedEffect(statusBarHeight, enabled) {
        if (!enabled) return@LaunchedEffect

        fun updatePadding(height: Int) {
            val bottomPadding = with(density) { height.toDp() }
            val topPadding = with(density) { statusBarHeight.toDp() }
            onPaddingChanged(PaddingValues(top = topPadding, bottom = bottomPadding))
        }

        sheetHeight.intValue.takeIf { it > 0 }?.let(::updatePadding)

        snapshotFlow { sheetHeight.intValue }
            .debounce(100)
            .filter { it > 0 }
            .collect { updatePadding(it) }
    }
}
