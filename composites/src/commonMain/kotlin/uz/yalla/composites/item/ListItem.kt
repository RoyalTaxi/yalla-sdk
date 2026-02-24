package uz.yalla.composites.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [ListItem] component.
 *
 * @property title Primary text.
 * @property subtitle Optional secondary text.
 * @property enabled Whether item is enabled.
 */
data class ListItemState(
    val title: String,
    val subtitle: String? = null,
    val enabled: Boolean = true
)

/**
 * Standard list item with title, subtitle, and action slots.
 *
 * Versatile list item for settings, menus, and general lists.
 *
 * ## Usage
 *
 * ```kotlin
 * ListItem(
 *     state = ListItemState(
 *         title = "Profile Settings",
 *         subtitle = "Update your personal info"
 *     ),
 *     leadingContent = { Icon(Icons.Default.Person, null) },
 *     trailingContent = { Icon(Icons.Default.ChevronRight, null) },
 *     onClick = { navigateToProfile() },
 * )
 * ```
 *
 * @param state Item state containing title, subtitle, and enabled.
 * @param modifier Applied to item.
 * @param leadingContent Optional leading slot.
 * @param trailingContent Optional trailing slot.
 * @param onClick Optional click handler.
 * @param colors Color configuration, defaults to [ListItemDefaults.colors].
 * @param style Text style configuration, defaults to [ListItemDefaults.style].
 * @param dimens Dimension configuration, defaults to [ListItemDefaults.dimens].
 *
 * @see ListItemDefaults for default values
 */
@Composable
fun ListItem(
    state: ListItemState,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    colors: ListItemDefaults.ListItemColors = ListItemDefaults.colors(),
    style: ListItemDefaults.ListItemStyle = ListItemDefaults.style(),
    dimens: ListItemDefaults.ListItemDimens = ListItemDefaults.dimens(),
) {
    val content: @Composable () -> Unit = {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(dimens.contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
        ) {
            leadingContent?.invoke()

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimens.titleSubtitleSpacing),
            ) {
                Text(
                    text = state.title,
                    style = style.title,
                    color = if (state.enabled) colors.title else colors.disabledTitle,
                    maxLines = dimens.titleMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )

                if (state.subtitle != null) {
                    Text(
                        text = state.subtitle,
                        style = style.subtitle,
                        color = if (state.enabled) colors.subtitle else colors.disabledSubtitle,
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
            enabled = state.enabled,
            shape = dimens.shape,
            colors =
                CardDefaults.cardColors(
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
            colors =
                CardDefaults.cardColors(
                    containerColor = colors.container,
                ),
        ) {
            content()
        }
    }
}

/**
 * Default configuration values for [ListItem].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object ListItemDefaults {
    /**
     * Color configuration for [ListItem].
     *
     * @param container Background color when enabled.
     * @param title Title text color when enabled.
     * @param subtitle Subtitle text color when enabled.
     * @param disabledContainer Background color when disabled.
     * @param disabledTitle Title text color when disabled.
     * @param disabledSubtitle Subtitle text color when disabled.
     */
    data class ListItemColors(
        val container: Color,
        val title: Color,
        val subtitle: Color,
        val disabledContainer: Color,
        val disabledTitle: Color,
        val disabledSubtitle: Color,
    )

    @Composable
    fun colors(
        container: Color = Color.Transparent,
        title: Color = System.color.textBase,
        subtitle: Color = System.color.textSubtle,
        disabledContainer: Color = Color.Transparent,
        disabledTitle: Color = System.color.textSubtle.copy(alpha = 0.5f),
        disabledSubtitle: Color = System.color.textSubtle.copy(alpha = 0.5f),
    ) = ListItemColors(
        container = container,
        title = title,
        subtitle = subtitle,
        disabledContainer = disabledContainer,
        disabledTitle = disabledTitle,
        disabledSubtitle = disabledSubtitle,
    )

    /**
     * Text style configuration for [ListItem].
     *
     * @param title Title text style.
     * @param subtitle Subtitle text style.
     */
    data class ListItemStyle(
        val title: TextStyle,
        val subtitle: TextStyle,
    )

    @Composable
    fun style(
        title: TextStyle = System.font.body.base.bold,
        subtitle: TextStyle = System.font.body.small.medium,
    ) = ListItemStyle(
        title = title,
        subtitle = subtitle,
    )

    /**
     * Dimension configuration for [ListItem].
     *
     * @param shape Item shape.
     * @param contentPadding Padding around content.
     * @param contentSpacing Spacing between elements.
     * @param titleSubtitleSpacing Spacing between title and subtitle.
     * @param titleMaxLines Max lines for title.
     * @param subtitleMaxLines Max lines for subtitle.
     */
    data class ListItemDimens(
        val shape: Shape,
        val contentPadding: PaddingValues,
        val contentSpacing: Dp,
        val titleSubtitleSpacing: Dp,
        val titleMaxLines: Int,
        val subtitleMaxLines: Int,
    )

    @Composable
    fun dimens(
        shape: Shape = RectangleShape,
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        contentSpacing: Dp = 12.dp,
        titleSubtitleSpacing: Dp = 4.dp,
        titleMaxLines: Int = 1,
        subtitleMaxLines: Int = 2,
    ) = ListItemDimens(
        shape = shape,
        contentPadding = contentPadding,
        contentSpacing = contentSpacing,
        titleSubtitleSpacing = titleSubtitleSpacing,
        titleMaxLines = titleMaxLines,
        subtitleMaxLines = subtitleMaxLines,
    )
}

@Preview
@Composable
private fun ListItemPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        ListItem(
            state =
                ListItemState(
                    title = "Profile Settings",
                    subtitle = "Update your personal info"
                ),
            onClick = {},
        )
    }
}
