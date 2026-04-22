package uz.yalla.composites.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [IconItem].
 *
 * @param container Item background color.
 * @param iconBackground Background color of the icon container.
 * @param title Title text color.
 * @param subtitle Subtitle text color.
 * @since 0.0.1
 */
@Immutable
data class IconItemColors(
    val container: Color,
    val iconBackground: Color,
    val title: Color,
    val subtitle: Color,
)

/**
 * Dimension configuration for [IconItem].
 *
 * @param shape Item card shape.
 * @param contentPadding Padding around item content.
 * @param contentSpacing Spacing between icon, text column, and trailing content.
 * @param iconContainerSize Size of the icon container box.
 * @param iconContainerShape Shape of the icon container background.
 * @param iconPadding Padding inside the icon container.
 * @param titleSubtitleSpacing Vertical spacing between title and subtitle.
 * @since 0.0.1
 */
@Immutable
data class IconItemDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val contentSpacing: Dp,
    val iconContainerSize: Dp,
    val iconContainerShape: Shape,
    val iconPadding: Dp,
    val titleSubtitleSpacing: Dp,
)

/**
 * List item with a styled icon container.
 *
 * Built on [ListItem]. Wraps the caller-provided [icon] composable in a sized, shaped,
 * and background-colored container before passing it as [ListItem]'s leading content.
 *
 * The default text style and color for [title] and [subtitle] are injected via
 * [ProvideTextStyle][androidx.compose.material3.ProvideTextStyle]; a plain
 * [Text][androidx.compose.material3.Text] in the slot inherits them automatically.
 *
 * ## Usage
 *
 * ```kotlin
 * IconItem(
 *     title = { Text("Language") },
 *     subtitle = { Text("English") },
 *     onClick = { openLanguagePicker() },
 *     icon = { Icon(YallaIcons.Globe, null) },
 *     trailingContent = { Text("EN") },
 * )
 * ```
 *
 * @param title Primary content; receives [System.font.body.base.bold] style by default.
 * @param onClick Called when the item is clicked.
 * @param modifier Applied to the root item.
 * @param subtitle Optional secondary content.
 * @param enabled Whether the item responds to clicks.
 * @param icon Optional icon composable rendered inside a styled container.
 * @param trailingContent Optional composable rendered at the end of the row.
 * @param colors Color configuration, defaults to [IconItemDefaults.colors].
 * @param dimens Dimension configuration, defaults to [IconItemDefaults.dimens].
 *
 * @see ListItem
 * @see IconItemDefaults
 * @see NavigableItem
 * @since 0.0.1
 */
@Composable
fun IconItem(
    title: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: IconItemColors = IconItemDefaults.colors(),
    dimens: IconItemDimens = IconItemDefaults.dimens(),
) {
    ListItem(
        title = title,
        subtitle = subtitle,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
        leadingContent = icon?.let {
            {
                Box(
                    modifier = Modifier
                        .size(dimens.iconContainerSize)
                        .background(colors.iconBackground, dimens.iconContainerShape)
                        .padding(dimens.iconPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    it()
                }
            }
        },
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            container = colors.container,
            title = colors.title,
            subtitle = colors.subtitle,
        ),
        dimens = ListItemDefaults.dimens(
            shape = dimens.shape,
            contentPadding = dimens.contentPadding,
            contentSpacing = dimens.contentSpacing,
            titleSubtitleSpacing = dimens.titleSubtitleSpacing,
        ),
    )
}

/**
 * Default configuration values for [IconItem].
 *
 * @since 0.0.1
 */
object IconItemDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = Color.Transparent,
        iconBackground: Color = System.color.background.secondary,
        title: Color = System.color.text.base,
        subtitle: Color = System.color.text.subtle,
    ): IconItemColors = IconItemColors(
        container = container,
        iconBackground = iconBackground,
        title = title,
        subtitle = subtitle,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RectangleShape,
        contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        contentSpacing: Dp = 16.dp,
        iconContainerSize: Dp = 24.dp,
        iconContainerShape: Shape = RoundedCornerShape(6.dp),
        iconPadding: Dp = 0.dp,
        titleSubtitleSpacing: Dp = 4.dp,
    ): IconItemDimens = IconItemDimens(
        shape = shape,
        contentPadding = contentPadding,
        contentSpacing = contentSpacing,
        iconContainerSize = iconContainerSize,
        iconContainerShape = iconContainerShape,
        iconPadding = iconPadding,
        titleSubtitleSpacing = titleSubtitleSpacing,
    )
}
