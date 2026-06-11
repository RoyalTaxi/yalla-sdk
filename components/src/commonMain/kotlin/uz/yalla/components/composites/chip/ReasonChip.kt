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
data class ReasonChipColors(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val textColor: Color,
    val selectedTextColor: Color
)

@Immutable
data class ReasonChipDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val contentSpacing: Dp,
    val iconSize: Dp
)

@Immutable
data class ReasonChipStyles(
    val textStyle: TextStyle
)

object ReasonChipDefaults {
    @Composable
    fun colors(
        containerColor: Color = System.color.background.secondary,
        selectedContainerColor: Color = System.color.background.brand,
        textColor: Color = System.color.text.base,
        selectedTextColor: Color = System.color.background.base
    ) = ReasonChipColors(
        containerColor = containerColor,
        selectedContainerColor = selectedContainerColor,
        textColor = textColor,
        selectedTextColor = selectedTextColor
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(12.dp),
        contentPadding: PaddingValues = PaddingValues(12.dp),
        contentSpacing: Dp = 12.dp,
        iconSize: Dp = 24.dp
    ) = ReasonChipDimens(
        shape = shape,
        contentPadding = contentPadding,
        contentSpacing = contentSpacing,
        iconSize = iconSize
    )

    @Composable
    fun styles(
        textStyle: TextStyle = System.font.body.small.bold
    ) = ReasonChipStyles(
        textStyle = textStyle
    )
}

@Composable
fun ReasonChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    colors: ReasonChipColors = ReasonChipDefaults.colors(),
    dimens: ReasonChipDimens = ReasonChipDefaults.dimens(),
    styles: ReasonChipStyles = ReasonChipDefaults.styles()
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
fun ReasonChip(
    text: String,
    selected: Boolean,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ReasonChipColors = ReasonChipDefaults.colors(),
    dimens: ReasonChipDimens = ReasonChipDefaults.dimens(),
    styles: ReasonChipStyles = ReasonChipDefaults.styles()
) {
    val painter = imageUrl?.takeIf { it.isNotBlank() }?.let { rememberAsyncImagePainter(model = it) }
    val painterState by painter?.state?.collectAsState() ?: return ReasonChip(
        text = text,
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        painter = null,
        colors = colors,
        dimens = dimens,
        styles = styles
    )

    ReasonChip(
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
private fun Preview() = YallaTheme {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        ReasonChip(
            text = "Clean car",
            selected = true,
            onClick = {}
        )

        ReasonChip(
            text = "Polite driver",
            selected = false,
            onClick = {}
        )
    }
}
