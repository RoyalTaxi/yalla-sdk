package uz.yalla.primitives.indicator

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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

/**
 * Color configuration for [DotsIndicator].
 *
 * @param selected Color of the selected dot.
 * @param unselected Color of unselected dots.
 * @since 0.0.1
 */
@Immutable
data class DotsIndicatorColors(
    val selected: Color,
    val unselected: Color,
)

/**
 * Dimension configuration for [DotsIndicator].
 *
 * @param dotSize Size of each dot.
 * @param selectedWidth Width of the selected dot.
 * @param dotSpacing Spacing between dots.
 * @param animationDurationMillis Duration of the dot width animation.
 * @since 0.0.1
 */
@Immutable
data class DotsIndicatorDimens(
    val dotSize: Dp,
    val selectedWidth: Dp,
    val dotSpacing: Dp,
    val animationDurationMillis: Int,
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
 *     pageCount = 5,
 *     currentPage = pagerState.currentPage,
 * )
 * ```
 *
 * @param pageCount Total number of pages.
 * @param currentPage Currently selected page (0-indexed).
 * @param modifier Applied to indicator row.
 * @param colors Color configuration, defaults to [DotsIndicatorDefaults.colors].
 * @param dimens Dimension configuration, defaults to [DotsIndicatorDefaults.dimens].
 *
 * @see DotsIndicatorDefaults for default values
 * @since 0.0.1
 */
@Composable
fun DotsIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    colors: DotsIndicatorColors = DotsIndicatorDefaults.colors(),
    dimens: DotsIndicatorDimens = DotsIndicatorDefaults.dimens(),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.dotSpacing),
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage

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
 * @since 0.0.1
 */
object DotsIndicatorDefaults {
    /** Creates color configuration for [DotsIndicator]. */
    @Composable
    fun colors(
        selected: Color = System.color.background.brand,
        unselected: Color = System.color.background.tertiary,
    ) = DotsIndicatorColors(
        selected = selected,
        unselected = unselected,
    )

    /** Creates dimension configuration for [DotsIndicator]. */
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
    YallaTheme {
        Box(
            modifier =
                Modifier
                    .background(Color.White)
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            DotsIndicator(
                pageCount = 5,
                currentPage = 2,
            )
        }
    }
}
