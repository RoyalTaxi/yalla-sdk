package uz.yalla.composites.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [ListItem].
 *
 * @param container Background color when enabled.
 * @param title Title text color when enabled.
 * @param subtitle Subtitle text color when enabled.
 * @param disabledContainer Background color when disabled.
 * @param disabledTitle Title text color when disabled.
 * @param disabledSubtitle Subtitle text color when disabled.
 * @since 0.0.1
 */
@Immutable
data class ListItemColors(
    val container: Color,
    val title: Color,
    val subtitle: Color,
    val disabledContainer: Color,
    val disabledTitle: Color,
    val disabledSubtitle: Color,
)

/**
 * Dimension configuration for [ListItem].
 *
 * @param shape Card corner shape.
 * @param contentPadding Padding around the row content.
 * @param contentSpacing Horizontal spacing between leading content, text column, and trailing content.
 * @param titleSubtitleSpacing Vertical spacing between title and subtitle.
 * @param titleMaxLines Maximum lines for the title text.
 * @param subtitleMaxLines Maximum lines for the subtitle text.
 * @since 0.0.1
 */
@Immutable
data class ListItemDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val contentSpacing: Dp,
    val titleSubtitleSpacing: Dp,
    val titleMaxLines: Int,
    val subtitleMaxLines: Int,
)

/**
 * General-purpose list item -- the building block for all item variants.
 *
 * Renders a horizontal row with optional leading content, a title/subtitle column,
 * and optional trailing content. When [onClick] is provided, the item is rendered
 * as a clickable [Card][androidx.compose.material3.Card]; otherwise it is non-interactive.
 *
 * All composed items ([IconItem], [NavigableItem], etc.) build on top of this.
 *
 * ## Usage
 *
 * ```kotlin
 * ListItem(
 *     title = "Notifications",
 *     subtitle = "Manage push notifications",
 *     onClick = { navigateToNotifications() },
 *     leadingContent = { Icon(YallaIcons.Bell, null) },
 *     trailingContent = { Badge(count = 3) },
 * )
 * ```
 *
 * @param title Primary text displayed in bold.
 * @param modifier Applied to the root card.
 * @param subtitle Optional secondary text displayed below the title.
 * @param enabled Whether the item responds to clicks (only relevant when [onClick] is set).
 * @param onClick Optional click handler. When null, the item is non-clickable.
 * @param leadingContent Optional composable rendered before the text column.
 * @param trailingContent Optional composable rendered after the text column.
 * @param colors Color configuration, defaults to [ListItemDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ListItemDefaults.dimens].
 *
 * @see ListItemDefaults
 * @see IconItem
 * @see NavigableItem
 * @since 0.0.1
 */
@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(),
    dimens: ListItemDimens = ListItemDefaults.dimens(),
) {
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(dimens.contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
        ) {
            leadingContent?.invoke()

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimens.titleSubtitleSpacing),
            ) {
                Text(
                    text = title,
                    style = System.font.body.base.bold,
                    color = if (enabled) colors.title else colors.disabledTitle,
                    maxLines = dimens.titleMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = System.font.body.small.medium,
                        color = if (enabled) colors.subtitle else colors.disabledSubtitle,
                        maxLines = dimens.subtitleMaxLines,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            trailingContent?.invoke()
        }
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = dimens.shape,
            colors = CardDefaults.cardColors(
                containerColor = colors.container,
                disabledContainerColor = colors.disabledContainer,
            ),
        ) {
            content()
        }
    } else {
        Card(
            modifier = modifier,
            shape = dimens.shape,
            colors = CardDefaults.cardColors(containerColor = colors.container),
        ) {
            content()
        }
    }
}

/**
 * Default configuration values for [ListItem].
 *
 * @since 0.0.1
 */
object ListItemDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = Color.Transparent,
        title: Color = System.color.text.base,
        subtitle: Color = System.color.text.subtle,
        disabledContainer: Color = Color.Transparent,
        disabledTitle: Color = System.color.text.subtle.copy(alpha = 0.5f),
        disabledSubtitle: Color = System.color.text.subtle.copy(alpha = 0.5f),
    ): ListItemColors = ListItemColors(
        container = container,
        title = title,
        subtitle = subtitle,
        disabledContainer = disabledContainer,
        disabledTitle = disabledTitle,
        disabledSubtitle = disabledSubtitle,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RectangleShape,
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        contentSpacing: Dp = 12.dp,
        titleSubtitleSpacing: Dp = 4.dp,
        titleMaxLines: Int = 1,
        subtitleMaxLines: Int = 2,
    ): ListItemDimens = ListItemDimens(
        shape = shape,
        contentPadding = contentPadding,
        contentSpacing = contentSpacing,
        titleSubtitleSpacing = titleSubtitleSpacing,
        titleMaxLines = titleMaxLines,
        subtitleMaxLines = subtitleMaxLines,
    )
}
