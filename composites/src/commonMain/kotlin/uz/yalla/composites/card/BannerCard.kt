package uz.yalla.composites.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [BannerCard].
 */
@Immutable
data class BannerCardColors(
    val contentColor: Color
)

/**
 * Dimension configuration for [BannerCard].
 */
@Immutable
data class BannerCardDimens(
    val shape: Shape,
    val height: Dp,
    val contentPadding: PaddingValues
)

/**
 * Default configuration values for [BannerCard].
 */
object BannerCardDefaults {
    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(contentColor: Color = System.color.text.white): BannerCardColors =
        BannerCardColors(
            contentColor = contentColor
        )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        height: Dp = 148.dp,
        contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 20.dp)
    ): BannerCardDimens =
        BannerCardDimens(
            shape = shape,
            height = height,
            contentPadding = contentPadding
        )
}

/**
 * Card with a background image and content overlay.
 *
 * Built on [ContentCard] with a painted background and [LocalContentColor] provision.
 *
 * ## Usage
 *
 * ```kotlin
 * BannerCard(background = painterResource(Res.drawable.bonus_bg)) {
 *     Column {
 *         Text("Your Bonuses", style = System.font.body.base.medium)
 *         Text("1 ride = 5% cashback", style = System.font.body.small.medium)
 *         Spacer(Modifier.weight(1f))
 *         Text("50,000", style = System.font.title.xLarge)
 *     }
 * }
 * ```
 *
 * @param colors Color configuration, defaults to [BannerCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [BannerCardDefaults.dimens].
 * @param content Overlay content — text color defaults to [BannerCardColors.contentColor].
 *
 * @see ContentCard
 * @see BannerCardDefaults
 */
@Composable
fun BannerCard(
    background: Painter,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    colors: BannerCardColors = BannerCardDefaults.colors(),
    dimens: BannerCardDimens = BannerCardDefaults.dimens(),
    content: @Composable () -> Unit
) {
    ContentCard(
        modifier = modifier.height(dimens.height),
        onClick = onClick,
        dimens =
            ContentCardDefaults.dimens(
                shape = dimens.shape,
                contentPadding = PaddingValues(0.dp)
            )
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .paint(painter = background, contentScale = ContentScale.Crop)
                    .padding(dimens.contentPadding)
        ) {
            CompositionLocalProvider(LocalContentColor provides colors.contentColor) {
                content()
            }
        }
    }
}
