package uz.yalla.components.composites.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.painterResource
import uz.yalla.components.primitives.toggle.Toggle
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.img_toggle

@Immutable
data class ToggleableItemColors(
    val titleColor: Color,
    val descriptionColor: Color,
    val iconBackgroundColor: Color,
    val containerColor: Color
)

@Immutable
data class ToggleableItemDimens(
    val contentSpacing: Dp,
    val contentPadding: PaddingValues,
    val iconShape: Shape,
    val iconPadding: Dp,
    val iconSize: Dp,
    val textSpacing: Dp
)

@Immutable
data class ToggleableItemStyles(
    val titleStyle: TextStyle,
    val descriptionStyle: TextStyle
)

object ToggleableItemDefaults {
    @Composable
    fun colors(
        titleColor: Color = System.color.text.base,
        descriptionColor: Color = System.color.text.subtle,
        iconBackgroundColor: Color = System.color.background.secondary,
        containerColor: Color = Color.Transparent
    ) = ToggleableItemColors(
        titleColor = titleColor,
        descriptionColor = descriptionColor,
        iconBackgroundColor = iconBackgroundColor,
        containerColor = containerColor
    )

    @Composable
    fun dimens(
        contentSpacing: Dp = 16.dp,
        contentPadding: PaddingValues = PaddingValues(
            vertical = 4.dp,
            horizontal = 20.dp
        ),
        iconShape: Shape = RoundedCornerShape(10.dp),
        iconPadding: Dp = 16.dp,
        iconSize: Dp = 24.dp,
        textSpacing: Dp = 8.dp
    ) = ToggleableItemDimens(
        contentSpacing = contentSpacing,
        contentPadding = contentPadding,
        iconShape = iconShape,
        iconPadding = iconPadding,
        iconSize = iconSize,
        textSpacing = textSpacing
    )

    @Composable
    fun styles(
        titleStyle: TextStyle = System.font.body.base.bold,
        descriptionStyle: TextStyle = System.font.body.small.medium
    ) = ToggleableItemStyles(
        titleStyle = titleStyle,
        descriptionStyle = descriptionStyle
    )
}

@Composable
fun ToggleableItem(
    title: String,
    description: String? = null,
    painter: Painter? = null,
    checked: Boolean,
    onToggle: (checked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ToggleableItemColors = ToggleableItemDefaults.colors(),
    dimens: ToggleableItemDimens = ToggleableItemDefaults.dimens(),
    styles: ToggleableItemStyles = ToggleableItemDefaults.styles()
) {
    Surface(
        modifier = modifier,
        color = colors.containerColor,
        enabled = enabled,
        onClick = { onToggle(checked.not()) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            painter?.let {
                Surface(
                    shape = dimens.iconShape,
                    color = colors.iconBackgroundColor
                ) {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(dimens.iconPadding)
                            .size(dimens.iconSize)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.textSpacing, Alignment.CenterVertically),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = colors.titleColor,
                    style = styles.titleStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                description?.let { desc ->
                    Text(
                        text = desc,
                        color = colors.descriptionColor,
                        style = styles.descriptionStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Toggle(
                checked = checked,
                onCheckedChange = onToggle,
                enabled = enabled
            )
        }
    }
}

@Composable
fun ToggleableItem(
    title: String,
    description: String? = null,
    imageUrl: String?,
    checked: Boolean,
    onToggle: (checked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ToggleableItemColors = ToggleableItemDefaults.colors(),
    dimens: ToggleableItemDimens = ToggleableItemDefaults.dimens(),
    styles: ToggleableItemStyles = ToggleableItemDefaults.styles()
) {
    val fallback = painterResource(Res.drawable.img_toggle)

    ToggleableItem(
        title = title,
        description = description,
        painter = imageUrl?.takeIf { it.isNotBlank() }?.let { url ->
            rememberAsyncImagePainter(
                model = url,
                placeholder = fallback,
                error = fallback,
                fallback = fallback
            )
        } ?: fallback,
        checked = checked,
        onToggle = onToggle,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        dimens = dimens,
        styles = styles
    )
}
