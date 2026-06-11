package uz.yalla.components.composites.item

import androidx.compose.runtime.Immutable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import uz.yalla.resources.icons.FocusLocation
import uz.yalla.resources.icons.YallaIcons

@Immutable
data class SearchableItemModel(
    val id: String,
    val title: String,
    val description: String? = null,
    val badge: String? = null
)

@Immutable
data class SearchableItemColors(
    val titleColor: Color,
    val descriptionColor: Color,
    val badgeColor: Color,
    val iconColor: Color,
    val iconBackgroundColor: Color
)

@Immutable
data class SearchableItemDimens(
    val contentSpacing: Dp,
    val contentInlineSpacing: Dp,
    val titleBadgeSpacing: Dp,
    val contentPadding: PaddingValues,
    val iconSize: Dp,
    val iconContainerSize: Dp,
    val iconContainerShape: Shape
)

@Immutable
data class SearchableItemStyles(
    val titleStyle: TextStyle,
    val descriptionStyle: TextStyle,
    val badgeStyle: TextStyle,
    val descriptionMaxLines: Int
)

object SearchableItemDefaults {
    @Composable
    fun colors(
        titleColor: Color = System.color.text.base,
        descriptionColor: Color = System.color.text.subtle,
        badgeColor: Color = System.color.text.subtle,
        iconColor: Color = Color.Unspecified,
        iconBackgroundColor: Color = Color.Transparent
    ) = SearchableItemColors(
        titleColor = titleColor,
        descriptionColor = descriptionColor,
        badgeColor = badgeColor,
        iconColor = iconColor,
        iconBackgroundColor = iconBackgroundColor
    )

    @Composable
    fun dimens(
        contentSpacing: Dp = 16.dp,
        contentInlineSpacing: Dp = 8.dp,
        titleBadgeSpacing: Dp = 12.dp,
        contentPadding: PaddingValues = PaddingValues(
            start = 20.dp,
            top = 10.dp,
            end = 30.dp,
            bottom = 10.dp
        ),
        iconSize: Dp = 44.dp,
        iconContainerSize: Dp = 44.dp,
        iconContainerShape: Shape = RectangleShape
    ) = SearchableItemDimens(
        contentSpacing = contentSpacing,
        contentInlineSpacing = contentInlineSpacing,
        titleBadgeSpacing = titleBadgeSpacing,
        contentPadding = contentPadding,
        iconSize = iconSize,
        iconContainerSize = iconContainerSize,
        iconContainerShape = iconContainerShape
    )

    @Composable
    fun styles(
        titleStyle: TextStyle = System.font.body.base.bold,
        descriptionStyle: TextStyle = System.font.body.small.medium,
        badgeStyle: TextStyle = System.font.body.small.medium,
        descriptionMaxLines: Int = 2
    ) = SearchableItemStyles(
        titleStyle = titleStyle,
        descriptionStyle = descriptionStyle,
        badgeStyle = badgeStyle,
        descriptionMaxLines = descriptionMaxLines
    )
}

@Composable
fun SearchableItem(
    title: String,
    description: String? = null,
    badge: String? = null,
    leadingPainter: Painter? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SearchableItemColors = SearchableItemDefaults.colors(),
    dimens: SearchableItemDimens = SearchableItemDefaults.dimens(),
    styles: SearchableItemStyles = SearchableItemDefaults.styles()
) {
    Surface(
        color = Color.Transparent,
        shape = RectangleShape,
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            leadingPainter?.let { painter ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(dimens.iconContainerSize)
                        .background(colors.iconBackgroundColor, dimens.iconContainerShape)
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
                verticalArrangement = Arrangement.spacedBy(dimens.contentInlineSpacing)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.titleBadgeSpacing)
                ) {
                    Text(
                        text = title,
                        color = colors.titleColor,
                        style = styles.titleStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    badge?.let {
                        Text(
                            text = badge,
                            color = colors.badgeColor,
                            style = styles.badgeStyle
                        )
                    }
                }

                description?.let { desc ->
                    Text(
                        text = desc,
                        color = colors.descriptionColor,
                        style = styles.descriptionStyle,
                        maxLines = styles.descriptionMaxLines,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.background(System.color.background.base)
    ) {
        SearchableItem(
            title = "Andijan region",
            description = "Place for youth affeice and growth under ministory of Information Technology and something something something",
            badge = "10 km",
            leadingPainter = rememberVectorPainter(YallaIcons.FocusLocation),
            onClick = {}
        )

        SearchableItem(
            title = "Andijan region",
            description = "Launch Bar",
            badge = "10 km",
            leadingPainter = rememberVectorPainter(YallaIcons.FocusLocation),
            onClick = {}
        )

        SearchableItem(
            title = "Andijan region, Andijan region, Andijan region, Andijan region",
            description = "Launch Bar",
            badge = "10 km",
            leadingPainter = rememberVectorPainter(YallaIcons.FocusLocation),
            onClick = {}
        )
    }
}
