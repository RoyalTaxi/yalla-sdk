package uz.yalla.primitives.indicator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.img_logo_splash
import uz.yalla.resources.location_gps_subtitle

/**
 * Full-screen splash overlay with logo and loading indicator.
 *
 * Blocks all touch input while displayed, typically used during
 * app initialization or location acquisition.
 *
 * ## Usage
 *
 * ```kotlin
 * Box {
 *     MainContent()
 *
 *     if (isLoading) {
 *         SplashOverlay()
 *     }
 * }
 *
 * // With custom message
 * SplashOverlay(
 *     message = "Connecting..."
 * )
 * ```
 *
 * @param modifier Modifier for the overlay container.
 * @param message Loading message displayed below the indicator.
 * @param colors Color configuration, defaults to [SplashOverlayDefaults.colors].
 * @param dimens Dimension configuration, defaults to [SplashOverlayDefaults.dimens].
 *
 * @see SplashOverlayDefaults for default values
 */
@Composable
fun SplashOverlay(
    modifier: Modifier = Modifier,
    message: String = stringResource(Res.string.location_gps_subtitle),
    colors: SplashOverlayDefaults.SplashOverlayColors = SplashOverlayDefaults.colors(),
    dimens: SplashOverlayDefaults.SplashOverlayDimens = SplashOverlayDefaults.dimens(),
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.background)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                        }
                    }
                }
    ) {
        Image(
            painter = painterResource(Res.drawable.img_logo_splash),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimens.bottomPadding)
        ) {
            CircularProgressIndicator(
                color = colors.indicator,
                trackColor = colors.indicatorTrack,
                strokeCap = StrokeCap.Round,
                strokeWidth = dimens.indicatorStrokeWidth,
                gapSize = dimens.indicatorGapSize,
            )

            Text(
                text = message,
                style = System.font.body.base.medium,
                color = colors.text,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Default values for [SplashOverlay].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object SplashOverlayDefaults {
    /**
     * Color configuration for [SplashOverlay].
     *
     * @param background Background brush (gradient) of the overlay.
     * @param indicator Progress indicator color.
     * @param indicatorTrack Progress indicator track color.
     * @param text Loading message text color.
     */
    data class SplashOverlayColors(
        val background: Brush,
        val indicator: Color,
        val indicatorTrack: Color,
        val text: Color,
    )

    @Composable
    fun colors(
        background: Brush = System.color.splashBackground,
        indicator: Color = System.color.backgroundBase,
        indicatorTrack: Color = System.color.backgroundBase.copy(alpha = .5f),
        text: Color = System.color.backgroundBase,
    ): SplashOverlayColors =
        SplashOverlayColors(
            background = background,
            indicator = indicator,
            indicatorTrack = indicatorTrack,
            text = text,
        )

    /**
     * Dimension configuration for [SplashOverlay].
     *
     * @param contentSpacing Spacing between indicator and message text.
     * @param bottomPadding Bottom padding for the loading section.
     * @param indicatorStrokeWidth Stroke width of the progress indicator.
     * @param indicatorGapSize Gap size in the progress indicator.
     */
    data class SplashOverlayDimens(
        val contentSpacing: Dp,
        val bottomPadding: Dp,
        val indicatorStrokeWidth: Dp,
        val indicatorGapSize: Dp,
    )

    @Composable
    fun dimens(
        contentSpacing: Dp = 24.dp,
        bottomPadding: Dp = 64.dp,
        indicatorStrokeWidth: Dp = 3.dp,
        indicatorGapSize: Dp = 3.dp,
    ): SplashOverlayDimens =
        SplashOverlayDimens(
            contentSpacing = contentSpacing,
            bottomPadding = bottomPadding,
            indicatorStrokeWidth = indicatorStrokeWidth,
            indicatorGapSize = indicatorGapSize,
        )
}
