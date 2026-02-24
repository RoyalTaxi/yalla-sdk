package uz.yalla.composites.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * General purpose content card with customizable content.
 *
 * Flexible container for grouped content sections.
 *
 * ## Usage
 *
 * ```kotlin
 * ContentCard {
 *     Text("Card Title", style = System.font.title.base)
 *     Spacer(Modifier.height(8.dp))
 *     Text("Card description goes here")
 * }
 * ```
 *
 * ## Clickable
 *
 * ```kotlin
 * ContentCard(onClick = { navigateToDetails() }) {
 *     ListItem(headlineText = "Settings")
 * }
 * ```
 *
 * @param modifier Applied to card.
 * @param onClick Optional click handler.
 * @param enabled Whether card is enabled when clickable.
 * @param colors Color configuration, defaults to [ContentCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ContentCardDefaults.dimens].
 * @param content Card content.
 *
 * @see ContentCardDefaults for default values
 */
@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: ContentCardDefaults.ContentCardColors = ContentCardDefaults.colors(),
    dimens: ContentCardDefaults.ContentCardDimens = ContentCardDefaults.dimens(),
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            shape = dimens.shape,
            colors =
                CardDefaults.cardColors(
                    containerColor = colors.container,
                    disabledContainerColor = colors.disabledContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(dimens.contentPadding),
                content = content,
            )
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = dimens.shape,
            colors =
                CardDefaults.cardColors(
                    containerColor = colors.container,
                ),
        ) {
            Column(
                modifier = Modifier.padding(dimens.contentPadding),
                content = content,
            )
        }
    }
}

/**
 * Default configuration values for [ContentCard].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object ContentCardDefaults {
    /**
     * Color configuration for [ContentCard].
     *
     * @param container Background color when enabled.
     * @param disabledContainer Background color when disabled.
     */
    data class ContentCardColors(
        val container: Color,
        val disabledContainer: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundSecondary,
        disabledContainer: Color = System.color.backgroundSecondary.copy(alpha = 0.6f),
    ) = ContentCardColors(
        container = container,
        disabledContainer = disabledContainer,
    )

    /**
     * Dimension configuration for [ContentCard].
     *
     * @param shape Card shape.
     * @param contentPadding Padding inside the card.
     */
    data class ContentCardDimens(
        val shape: Shape,
        val contentPadding: Dp,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: Dp = 16.dp,
    ) = ContentCardDimens(
        shape = shape,
        contentPadding = contentPadding,
    )
}
