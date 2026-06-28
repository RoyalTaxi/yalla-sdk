package uz.yalla.components.composites.chip

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

@Immutable
public data class ChipColors(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val textColor: Color,
    val selectedTextColor: Color
)

@Immutable
public data class ChipDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val contentSpacing: Dp,
    val iconSize: Dp
)

@Immutable
public data class ChipStyles(
    val textStyle: TextStyle
)

public object ChipDefaults {
    @Composable
    public fun colors(
        containerColor: Color = System.color.background.secondary,
        selectedContainerColor: Color = System.color.background.brand,
        textColor: Color = System.color.text.base,
        selectedTextColor: Color = System.color.text.white
    ): ChipColors =
        ChipColors(
            containerColor = containerColor,
            selectedContainerColor = selectedContainerColor,
            textColor = textColor,
            selectedTextColor = selectedTextColor
        )

    @Composable
    public fun dimens(
        shape: Shape = RoundedCornerShape(12.dp),
        contentPadding: PaddingValues = PaddingValues(12.dp),
        contentSpacing: Dp = 12.dp,
        iconSize: Dp = 24.dp
    ): ChipDimens =
        ChipDimens(
            shape = shape,
            contentPadding = contentPadding,
            contentSpacing = contentSpacing,
            iconSize = iconSize
        )

    @Composable
    public fun styles(textStyle: TextStyle = System.font.body.small.bold): ChipStyles =
        ChipStyles(
            textStyle = textStyle
        )
}

@Composable
public fun Chip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    colors: ChipColors = ChipDefaults.colors(),
    dimens: ChipDimens = ChipDefaults.dimens(),
    styles: ChipStyles = ChipDefaults.styles()
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = dimens.shape,
        color = if (selected) colors.selectedContainerColor else colors.containerColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            painter?.let {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.size(dimens.iconSize)
                )
            }

            Text(
                text = text,
                color = if (selected) colors.selectedTextColor else colors.textColor,
                style = styles.textStyle,
                maxLines = 2
            )
        }
    }
}

@Composable
public fun Chip(
    text: String,
    selected: Boolean,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChipColors = ChipDefaults.colors(),
    dimens: ChipDimens = ChipDefaults.dimens(),
    styles: ChipStyles = ChipDefaults.styles()
) {
    val painter = imageUrl?.takeIf { it.isNotBlank() }?.let { rememberAsyncImagePainter(model = it) }
    val painterState by painter?.state?.collectAsState() ?: return Chip(
        text = text,
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        painter = null,
        colors = colors,
        dimens = dimens,
        styles = styles
    )

    Chip(
        text = text,
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        painter = painter.takeIf { painterState is AsyncImagePainter.State.Success },
        colors = colors,
        dimens = dimens,
        styles = styles
    )
}

@Preview
@Composable
private fun Preview() =
    YallaTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Chip(
                text = "Clean car",
                selected = true,
                onClick = {}
            )

            Chip(
                text = "Polite driver",
                selected = false,
                onClick = {}
            )
        }
    }
