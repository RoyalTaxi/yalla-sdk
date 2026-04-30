package uz.yalla.primitives.indicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.platform.indicator.NativeLoadingIndicator

/** Color configuration for [LoadingIndicator]. */
@Immutable
data class LoadingIndicatorColors(
    val indicator: Color,
    val track: Color,
)

/** Dimension configuration for [LoadingIndicator]. */
@Immutable
data class LoadingIndicatorDimens(
    val smallSize: Dp,
    val mediumSize: Dp,
    val largeSize: Dp,
    val smallStrokeWidth: Dp,
    val mediumStrokeWidth: Dp,
    val largeStrokeWidth: Dp,
) {
    /** Resolves the indicator diameter for the given [size] variant. */
    fun size(size: LoadingIndicatorSize): Dp =
        when (size) {
            LoadingIndicatorSize.Small -> smallSize
            LoadingIndicatorSize.Medium -> mediumSize
            LoadingIndicatorSize.Large -> largeSize
        }

    /** Resolves the stroke width for the given [size] variant. */
    fun strokeWidth(size: LoadingIndicatorSize): Dp =
        when (size) {
            LoadingIndicatorSize.Small -> smallStrokeWidth
            LoadingIndicatorSize.Medium -> mediumStrokeWidth
            LoadingIndicatorSize.Large -> largeStrokeWidth
        }
}

/**
 * Circular loading indicator.
 *
 * ## Usage
 *
 * ```kotlin
 * if (isLoading) {
 *     LoadingIndicator()
 * }
 * ```
 *
 * @param modifier Applied to indicator.
 * @param size Indicator size variant.
 * @param colors Color configuration, defaults to [LoadingIndicatorDefaults.colors].
 * @param dimens Dimension configuration, defaults to [LoadingIndicatorDefaults.dimens].
 *
 * @see SplashOverlay for full-screen loading overlay
 * @see LoadingIndicatorDefaults for default values
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: LoadingIndicatorSize = LoadingIndicatorSize.Medium,
    colors: LoadingIndicatorColors = LoadingIndicatorDefaults.colors(),
    dimens: LoadingIndicatorDimens = LoadingIndicatorDefaults.dimens(),
) {
    NativeLoadingIndicator(
        modifier = modifier.size(dimens.size(size)),
        color = colors.indicator,
    )
}

/**
 * Size variants for [LoadingIndicator].
 */
enum class LoadingIndicatorSize {
    Small,
    Medium,
    Large,
}

/**
 * Default configuration values for [LoadingIndicator].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object LoadingIndicatorDefaults {
    /** Creates color configuration for [LoadingIndicator]. */
    @Composable
    fun colors(
        indicator: Color = System.color.button.active,
        track: Color = System.color.background.tertiary,
    ) = LoadingIndicatorColors(
        indicator = indicator,
        track = track,
    )

    /** Creates dimension configuration for [LoadingIndicator]. */
    fun dimens(
        smallSize: Dp = 20.dp,
        mediumSize: Dp = 36.dp,
        largeSize: Dp = 48.dp,
        smallStrokeWidth: Dp = 2.dp,
        mediumStrokeWidth: Dp = 3.dp,
        largeStrokeWidth: Dp = 4.dp,
    ) = LoadingIndicatorDimens(
        smallSize = smallSize,
        mediumSize = mediumSize,
        largeSize = largeSize,
        smallStrokeWidth = smallStrokeWidth,
        mediumStrokeWidth = mediumStrokeWidth,
        largeStrokeWidth = largeStrokeWidth,
    )
}

@Preview
@Composable
private fun LoadingIndicatorPreview() {
    YallaTheme {
        Box(
            modifier =
                Modifier
                    .background(Color.White)
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            LoadingIndicator()
        }
    }
}
