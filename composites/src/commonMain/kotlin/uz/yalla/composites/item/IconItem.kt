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

@Immutable
data class IconItemColors(
    val container: Color,
    val iconBackground: Color,
    val title: Color,
    val subtitle: Color,
)

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

@Composable
fun IconItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
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

object IconItemDefaults {

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
