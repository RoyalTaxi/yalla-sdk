package uz.yalla.composites.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [InfoCard].
 *
 * @param container Card background color.
 * @since 0.0.5-alpha11
 */
@Immutable
data class InfoCardColors(
    val container: Color,
)

/**
 * Dimension configuration for [InfoCard].
 *
 * @param shape Card corner shape.
 * @param height Fixed card height.
 * @param contentPadding Padding inside the card.
 * @since 0.0.5-alpha11
 */
@Immutable
data class InfoCardDimens(
    val shape: Shape,
    val height: Dp,
    val contentPadding: PaddingValues,
)

/**
 * Default configuration values for [InfoCard].
 *
 * @since 0.0.5-alpha11
 */
object InfoCardDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = System.color.background.secondary,
    ): InfoCardColors = InfoCardColors(
        container = container,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(20.dp),
        height: Dp = 120.dp,
        contentPadding: PaddingValues = PaddingValues(
            top = 10.dp,
            end = 10.dp,
            bottom = 8.dp,
            start = 16.dp,
        ),
    ): InfoCardDimens = InfoCardDimens(
        shape = shape,
        height = height,
        contentPadding = contentPadding,
    )
}

/**
 * Fixed-height info card with content, trailing icon, and description slots.
 *
 * Built on [ContentCard] with a two-row layout: top row (content + trailing icon)
 * and bottom row (description).
 *
 * ## Usage
 *
 * ```kotlin
 * InfoCard(
 *     onClick = { editPlace() },
 *     trailingIcon = {
 *         Surface(shape = RoundedCornerShape(14.dp), color = bgColor) {
 *             Icon(YallaIcons.Home, null, modifier = Modifier.padding(10.dp))
 *         }
 *     },
 *     description = {
 *         Text("123 Main Street", style = System.font.body.caption, maxLines = 2)
 *     },
 * ) {
 *     Text("Home", style = System.font.title.base)
 * }
 * ```
 *
 * @param onClick Called when the card is clicked.
 * @param modifier Applied to the root card.
 * @param colors Color configuration, defaults to [InfoCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [InfoCardDefaults.dimens].
 * @param trailingIcon Optional icon displayed at the top-right corner.
 * @param description Optional bottom content (e.g., address text or hint).
 * @param content Top-left content (e.g., place name).
 *
 * @see ContentCard
 * @see InfoCardDefaults
 * @since 0.0.5-alpha11
 */
@Composable
fun InfoCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: InfoCardColors = InfoCardDefaults.colors(),
    dimens: InfoCardDimens = InfoCardDefaults.dimens(),
    trailingIcon: @Composable (() -> Unit)? = null,
    description: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    ContentCard(
        modifier = modifier.height(dimens.height),
        onClick = onClick,
        colors = ContentCardDefaults.colors(container = colors.container),
        dimens = ContentCardDefaults.dimens(
            shape = dimens.shape,
            contentPadding = dimens.contentPadding,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight(),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                content()
                trailingIcon?.invoke()
            }

            description?.invoke()
        }
    }
}
