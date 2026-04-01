package uz.yalla.composites.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [NavigableItem].
 *
 * @param container Item background color.
 * @param iconBackground Background color of the icon container.
 * @param icon Icon tint color.
 * @param title Title text color.
 * @param subtitle Subtitle text color.
 * @param arrow Trailing chevron tint color.
 * @since 0.0.1
 */
@Immutable
data class NavigableItemColors(
    val container: Color,
    val iconBackground: Color,
    val icon: Color,
    val title: Color,
    val subtitle: Color,
    val arrow: Color,
)

/**
 * Dimension configuration for [NavigableItem].
 *
 * @param height Item row height.
 * @param contentPadding Padding around item content.
 * @param contentSpacing Spacing between icon, text column, and trailing content.
 * @param iconContainerSize Size of the icon container box.
 * @param iconContainerShape Shape of the icon container background.
 * @param iconPadding Padding inside the icon container.
 * @param arrowSize Size of the trailing chevron icon.
 * @since 0.0.1
 */
@Immutable
data class NavigableItemDimens(
    val height: Dp,
    val contentPadding: PaddingValues,
    val contentSpacing: Dp,
    val iconContainerSize: Dp,
    val iconContainerShape: Shape,
    val iconPadding: Dp,
    val arrowSize: Dp,
)

/**
 * List item with a trailing chevron indicating navigation.
 *
 * Built on [IconItem] with a default trailing forward-arrow icon. When custom
 * [trailingContent] is provided, it replaces the default chevron.
 *
 * ## Composition hierarchy
 *
 * `NavigableItem` -> [IconItem] -> [ListItem]
 *
 * ## Usage
 *
 * ```kotlin
 * NavigableItem(
 *     title = "Payment Methods",
 *     subtitle = "Manage your cards",
 *     onClick = { navigateToPayments() },
 *     icon = { Icon(YallaIcons.Card, null) },
 * )
 * ```
 *
 * @param title Primary text.
 * @param onClick Called when the item is clicked.
 * @param modifier Applied to the root item.
 * @param subtitle Optional secondary text.
 * @param icon Optional icon composable rendered inside a styled container.
 * @param trailingContent Optional composable replacing the default chevron.
 * @param colors Color configuration, defaults to [NavigableItemDefaults.colors].
 * @param dimens Dimension configuration, defaults to [NavigableItemDefaults.dimens].
 *
 * @see IconItem
 * @see ListItem
 * @see NavigableItemDefaults
 * @since 0.0.1
 */
@Composable
fun NavigableItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: NavigableItemColors = NavigableItemDefaults.colors(),
    dimens: NavigableItemDimens = NavigableItemDefaults.dimens(),
) {
    IconItem(
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        trailingContent = trailingContent ?: {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = colors.arrow,
                modifier = Modifier.size(dimens.arrowSize),
            )
        },
        colors = IconItemDefaults.colors(
            container = colors.container,
            iconBackground = colors.iconBackground,
            title = colors.title,
            subtitle = colors.subtitle,
        ),
        dimens = IconItemDefaults.dimens(
            contentPadding = dimens.contentPadding,
            contentSpacing = dimens.contentSpacing,
            iconContainerSize = dimens.iconContainerSize,
            iconContainerShape = dimens.iconContainerShape,
            iconPadding = dimens.iconPadding,
        ),
    )
}

/**
 * Default configuration values for [NavigableItem].
 *
 * @since 0.0.1
 */
object NavigableItemDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = Color.Transparent,
        iconBackground: Color = System.color.background.base,
        icon: Color = System.color.icon.base,
        title: Color = System.color.text.base,
        subtitle: Color = System.color.text.subtle,
        arrow: Color = System.color.icon.subtle,
    ): NavigableItemColors = NavigableItemColors(
        container = container,
        iconBackground = iconBackground,
        icon = icon,
        title = title,
        subtitle = subtitle,
        arrow = arrow,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        height: Dp = 60.dp,
        contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
        contentSpacing: Dp = 16.dp,
        iconContainerSize: Dp = 44.dp,
        iconContainerShape: Shape = RoundedCornerShape(10.dp),
        iconPadding: Dp = 10.dp,
        arrowSize: Dp = 16.dp,
    ): NavigableItemDimens = NavigableItemDimens(
        height = height,
        contentPadding = contentPadding,
        contentSpacing = contentSpacing,
        iconContainerSize = iconContainerSize,
        iconContainerShape = iconContainerShape,
        iconPadding = iconPadding,
        arrowSize = arrowSize,
    )
}
