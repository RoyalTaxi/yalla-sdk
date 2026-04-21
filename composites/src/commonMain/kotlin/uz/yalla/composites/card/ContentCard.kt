package uz.yalla.composites.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [ContentCard].
 *
 * @param container Background color when enabled.
 * @param disabledContainer Background color when disabled.
 * @since 0.0.5-alpha11
 */
@Immutable
data class ContentCardColors(
    val container: Color,
    val disabledContainer: Color,
)

/**
 * Dimension configuration for [ContentCard].
 *
 * @param shape Card corner shape.
 * @param contentPadding Padding inside the card.
 * @since 0.0.5-alpha11
 */
@Immutable
data class ContentCardDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
)

/**
 * Default configuration values for [ContentCard].
 *
 * @since 0.0.1
 */
object ContentCardDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = System.color.background.secondary,
        disabledContainer: Color = System.color.background.secondary
            .copy(alpha = 0.6f),
    ): ContentCardColors = ContentCardColors(
        container = container,
        disabledContainer = disabledContainer,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(16.dp),
    ): ContentCardDimens = ContentCardDimens(
        shape = shape,
        contentPadding = contentPadding,
    )
}

/**
 * General-purpose card container — the building block for all card variants.
 *
 * Provides themed background, shape, optional click handling, and content padding.
 * All composed cards (NavigableCard, SelectionCard, etc.) build on top of this.
 *
 * ## Usage
 *
 * ```kotlin
 * ContentCard {
 *     Text("Card content")
 * }
 * ```
 *
 * ## Clickable
 *
 * ```kotlin
 * ContentCard(onClick = { navigateToDetails() }) {
 *     Text("Tap me")
 * }
 * ```
 *
 * @param modifier Applied to the root card.
 * @param onClick Optional click handler. When null, the card is not clickable.
 * @param enabled Whether the card responds to clicks (only relevant when [onClick] is set).
 * @param border Optional border stroke.
 * @param colors Color configuration, defaults to [ContentCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ContentCardDefaults.dimens].
 * @param content Card content — caller decides the layout (Row, Column, Box, etc.).
 *
 * @see ContentCardDefaults
 * @since 0.0.1
 */
@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    border: BorderStroke? = null,
    colors: ContentCardColors = ContentCardDefaults.colors(),
    dimens: ContentCardDimens = ContentCardDefaults.dimens(),
    content: @Composable () -> Unit,
) {
    Card(
        onClick = onClick ?: {},
        enabled = onClick != null && enabled,
        modifier = modifier,
        shape = dimens.shape,
        border = border,
        colors = CardDefaults.cardColors(
            containerColor = colors.container,
            disabledContainerColor = colors.disabledContainer,
        ),
    ) {
        Box(modifier = Modifier.padding(dimens.contentPadding)) {
            content()
        }
    }
}
