package uz.yalla.components.primitive.indicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.platform.indicator.NativeLoadingIndicator

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
 * @see LoadingIndicatorDefaults for default values
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: LoadingIndicatorSize = LoadingIndicatorSize.Medium,
    colors: LoadingIndicatorDefaults.LoadingIndicatorColors = LoadingIndicatorDefaults.colors(),
    dimens: LoadingIndicatorDefaults.LoadingIndicatorDimens = LoadingIndicatorDefaults.dimens(),
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
    /**
     * Color configuration for [LoadingIndicator].
     *
     * @param indicator Spinner color.
     * @param track Track color behind spinner.
     */
    data class LoadingIndicatorColors(
        val indicator: Color,
        val track: Color,
    )

    @Composable
    fun colors(
        indicator: Color = System.color.buttonActive,
        track: Color = System.color.backgroundTertiary,
    ) = LoadingIndicatorColors(
        indicator = indicator,
        track = track,
    )

    /**
     * Dimension configuration for [LoadingIndicator].
     *
     * @param smallSize Size for small variant.
     * @param mediumSize Size for medium variant.
     * @param largeSize Size for large variant.
     * @param smallStrokeWidth Stroke width for small variant.
     * @param mediumStrokeWidth Stroke width for medium variant.
     * @param largeStrokeWidth Stroke width for large variant.
     */
    data class LoadingIndicatorDimens(
        val smallSize: Dp,
        val mediumSize: Dp,
        val largeSize: Dp,
        val smallStrokeWidth: Dp,
        val mediumStrokeWidth: Dp,
        val largeStrokeWidth: Dp,
    ) {
        fun size(size: LoadingIndicatorSize): Dp =
            when (size) {
                LoadingIndicatorSize.Small -> smallSize
                LoadingIndicatorSize.Medium -> mediumSize
                LoadingIndicatorSize.Large -> largeSize
            }

        fun strokeWidth(size: LoadingIndicatorSize): Dp =
            when (size) {
                LoadingIndicatorSize.Small -> smallStrokeWidth
                LoadingIndicatorSize.Medium -> mediumStrokeWidth
                LoadingIndicatorSize.Large -> largeStrokeWidth
            }
    }

    @Composable
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
