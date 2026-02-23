package uz.yalla.components.primitive.indicator

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [DotsIndicator] component.
 *
 * @property pageCount Total number of pages.
 * @property currentPage Currently selected page (0-indexed).
 */
data class DotsIndicatorState(
    val pageCount: Int,
    val currentPage: Int,
)

/**
 * Animated dots indicator for pagers and carousels.
 *
 * Current page dot expands while others remain small.
 *
 * ## Usage
 *
 * ```kotlin
 * DotsIndicator(
 *     state = DotsIndicatorState(
 *         pageCount = 5,
 *         currentPage = pagerState.currentPage,
 *     ),
 * )
 * ```
 *
 * @param state Indicator state containing page count and current page.
 * @param modifier Applied to indicator row.
 * @param colors Color configuration, defaults to [DotsIndicatorDefaults.colors].
 * @param dimens Dimension configuration, defaults to [DotsIndicatorDefaults.dimens].
 *
 * @see DotsIndicatorState for state configuration
 * @see DotsIndicatorDefaults for default values
 */
@Composable
fun DotsIndicator(
    state: DotsIndicatorState,
    modifier: Modifier = Modifier,
    colors: DotsIndicatorDefaults.DotsIndicatorColors = DotsIndicatorDefaults.colors(),
    dimens: DotsIndicatorDefaults.DotsIndicatorDimens = DotsIndicatorDefaults.dimens(),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.dotSpacing),
    ) {
        repeat(state.pageCount) { index ->
            val isSelected = index == state.currentPage

            val width by animateDpAsState(
                targetValue = if (isSelected) dimens.selectedWidth else dimens.dotSize,
                animationSpec = tween(durationMillis = dimens.animationDurationMillis),
                label = "dotWidth",
            )

            Box(
                modifier =
                    Modifier
                        .size(width = width, height = dimens.dotSize)
                        .clip(CircleShape)
                        .background(if (isSelected) colors.selected else colors.unselected),
            )
        }
    }
}

/**
 * Default configuration values for [DotsIndicator].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object DotsIndicatorDefaults {
    /**
     * Color configuration for [DotsIndicator].
     *
     * @param selected Color of the selected dot.
     * @param unselected Color of unselected dots.
     */
    data class DotsIndicatorColors(
        val selected: Color,
        val unselected: Color,
    )

    @Composable
    fun colors(
        selected: Color = System.color.backgroundBrandBase,
        unselected: Color = System.color.backgroundTertiary,
    ) = DotsIndicatorColors(
        selected = selected,
        unselected = unselected,
    )

    /**
     * Dimension configuration for [DotsIndicator].
     *
     * @param dotSize Size of each dot.
     * @param selectedWidth Width of the selected dot.
     * @param dotSpacing Spacing between dots.
     * @param animationDurationMillis Duration of the dot width animation.
     */
    data class DotsIndicatorDimens(
        val dotSize: Dp,
        val selectedWidth: Dp,
        val dotSpacing: Dp,
        val animationDurationMillis: Int,
    )

    @Composable
    fun dimens(
        dotSize: Dp = 10.dp,
        selectedWidth: Dp = 24.dp,
        dotSpacing: Dp = 4.dp,
        animationDurationMillis: Int = 200,
    ) = DotsIndicatorDimens(
        dotSize = dotSize,
        selectedWidth = selectedWidth,
        dotSpacing = dotSpacing,
        animationDurationMillis = animationDurationMillis,
    )
}

@Preview
@Composable
private fun DotsIndicatorPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        DotsIndicator(
            state =
                DotsIndicatorState(
                    pageCount = 5,
                    currentPage = 2,
                ),
        )
    }
}
