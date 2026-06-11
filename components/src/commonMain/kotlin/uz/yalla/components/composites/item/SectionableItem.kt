package uz.yalla.components.composites.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import uz.yalla.resources.icons.ArrowRight
import uz.yalla.resources.icons.Messages
import uz.yalla.resources.icons.Setting
import uz.yalla.resources.icons.YallaIcons
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

@Immutable
data class SectionableItemColors(
    val iconColor: Color,
    val iconBackgroundColor: Color,
    val topDescriptionColor: Color,
    val titleColor: Color,
    val bottomDescriptionColor: Color,
    val descriptionColor: Color,
    val openIconColor: Color,
    val borderColor: Color,
    val containerColor: Color
)

@Immutable
data class SectionableItemDimens(
    val shape: Shape,
    val contentSpacing: Dp,
    val contentInlineSpacing: Dp,
    val contentPadding: PaddingValues,
    val iconSize: Dp,
    val iconContainerSize: Dp,
    val iconContainerShape: Shape,
    val iconPadding: Dp,
    val openIconSize: Dp,
    val borderWidth: Dp
)

@Immutable
data class SectionableItemStyles(
    val topDescriptionStyle: TextStyle,
    val titleStyle: TextStyle,
    val bottomDescriptionStyle: TextStyle
)

object SectionableItemDefaults {
    @Composable
    fun colors(
        iconColor: Color = System.color.icon.base,
        iconBackgroundColor: Color = Color.Transparent,
        topDescriptionColor: Color = System.color.text.subtle,
        titleColor: Color = System.color.text.base,
        bottomDescriptionColor: Color = System.color.text.base,
        descriptionColor: Color = System.color.text.base,
        openIconColor: Color = System.color.icon.base,
        borderColor: Color = Color.Transparent,
        containerColor: Color = Color.Transparent
    ) = SectionableItemColors(
        iconColor = iconColor,
        iconBackgroundColor = iconBackgroundColor,
        topDescriptionColor = topDescriptionColor,
        titleColor = titleColor,
        bottomDescriptionColor = bottomDescriptionColor,
        descriptionColor = descriptionColor,
        openIconColor = openIconColor,
        borderColor = borderColor,
        containerColor = containerColor
    )

    @Composable
    fun dimens(
        shape: Shape = RectangleShape,
        contentSpacing: Dp = 16.dp,
        contentInlineSpacing: Dp = 4.dp,
        contentPadding: PaddingValues = PaddingValues(
            start = 20.dp,
            top = 20.dp,
            end = 8.dp,
            bottom = 20.dp
        ),
        iconSize: Dp = 24.dp,
        iconContainerSize: Dp = 24.dp,
        iconContainerShape: Shape = RectangleShape,
        iconPadding: Dp = 0.dp,
        openIconSize: Dp = 24.dp,
        borderWidth: Dp = 0.dp
    ) = SectionableItemDimens(
        shape = shape,
        contentSpacing = contentSpacing,
        contentInlineSpacing = contentInlineSpacing,
        contentPadding = contentPadding,
        iconSize = iconSize,
        iconContainerSize = iconContainerSize,
        iconContainerShape = iconContainerShape,
        iconPadding = iconPadding,
        openIconSize = openIconSize,
        borderWidth = borderWidth
    )

    @Composable
    fun styles(
        topDescriptionStyle: TextStyle = System.font.body.base.medium,
        titleStyle: TextStyle = System.font.body.large.medium,
        bottomDescriptionStyle: TextStyle = System.font.body.caption
    ) = SectionableItemStyles(
        topDescriptionStyle = topDescriptionStyle,
        titleStyle = titleStyle,
        bottomDescriptionStyle = bottomDescriptionStyle
    )
}

@Composable
fun SectionableItem(
    topDescription: String? = null,
    title: String,
    bottomDescription: String? = null,
    iconPainter: Painter? = null,
    openIconPainter: Painter? = rememberVectorPainter(YallaIcons.ArrowRight),
    trailingView: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SectionableItemColors = SectionableItemDefaults.colors(),
    dimens: SectionableItemDimens = SectionableItemDefaults.dimens(),
    styles: SectionableItemStyles = SectionableItemDefaults.styles()
) {
    Surface(
        modifier = modifier,
        color = colors.containerColor,
        shape = dimens.shape,
        onClick = onClick,
        border = BorderStroke(
            color = colors.borderColor,
            width = dimens.borderWidth
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            iconPainter?.let { painter ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(dimens.iconContainerSize)
                        .background(colors.iconBackgroundColor, dimens.iconContainerShape)
                        .padding(dimens.iconPadding)
                ) {
                    if (colors.iconColor == Color.Unspecified) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier.size(dimens.iconSize)
                        )
                    } else {
                        Icon(
                            painter = painter,
                            contentDescription = null,
                            tint = colors.iconColor,
                            modifier = Modifier.size(dimens.iconSize)
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.contentInlineSpacing),
                modifier = Modifier.weight(1f)
            ) {
                topDescription?.let { text ->
                    Text(
                        text = text,
                        color = colors.topDescriptionColor,
                        style = styles.topDescriptionStyle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }

                Text(
                    text = title,
                    color = colors.titleColor,
                    style = styles.titleStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                bottomDescription?.let { text ->
                    Text(
                        text = text,
                        color = colors.bottomDescriptionColor,
                        style = styles.bottomDescriptionStyle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }

            trailingView?.invoke()

            openIconPainter?.let { icon ->
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = colors.openIconColor,
                    modifier = Modifier.size(dimens.openIconSize)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = System.color.background.secondary,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                SectionableItem(
                    iconPainter = rememberVectorPainter(YallaIcons.Setting),
                    title = "Notifications",
                    onClick = { }
                )

                SectionableItem(
                    iconPainter = rememberVectorPainter(YallaIcons.Setting),
                    title = "Notifications",
                    bottomDescription = "You have new notification",
                    dimens = SectionableItemDefaults.dimens(
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            top = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                    ),
                    onClick = { }
                )

                SectionableItem(
                    iconPainter = rememberVectorPainter(YallaIcons.Messages),
                    topDescription = "You have new notification",
                    title = "Notifications",
                    dimens = SectionableItemDefaults.dimens(
                        contentInlineSpacing = 0.dp,
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            top = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                    ),
                    onClick = { }
                )
            }
        }
    }
}
