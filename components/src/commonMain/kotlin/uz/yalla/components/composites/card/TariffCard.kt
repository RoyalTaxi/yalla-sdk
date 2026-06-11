package uz.yalla.components.composites.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_car_comfort

@Immutable
data class TariffCardColors(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val titleColor: Color,
    val descriptionColor: Color,
    val selectedBorder: Brush
)

@Immutable
data class TariffCardDimens(
    val shape: Shape,
    val height: Dp,
    val minWidth: Dp,
    val contentPadding: PaddingValues,
    val selectedBorderWidth: Dp,
    val titleDescriptionSpacing: Dp,
    val descriptionImageSpacing: Dp
)

@Immutable
data class TariffCardStyles(
    val titleStyle: TextStyle,
    val descriptionStyle: TextStyle
)

object TariffCardDefaults {
    @Composable
    fun colors(
        containerColor: Color = System.color.background.secondary,
        selectedContainerColor: Color = System.color.background.base,
        titleColor: Color = System.color.text.base,
        descriptionColor: Color = System.color.text.base,
        selectedBorder: Brush = System.color.gradient.sunsetNight
    ) = TariffCardColors(
        containerColor = containerColor,
        selectedContainerColor = selectedContainerColor,
        titleColor = titleColor,
        descriptionColor = descriptionColor,
        selectedBorder = selectedBorder
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(20.dp),
        height: Dp = 120.dp,
        minWidth: Dp = 140.dp,
        contentPadding: PaddingValues = PaddingValues(12.dp),
        selectedBorderWidth: Dp = 2.dp,
        titleDescriptionSpacing: Dp = 6.dp,
        descriptionImageSpacing: Dp = 10.dp
    ) = TariffCardDimens(
        shape = shape,
        height = height,
        minWidth = minWidth,
        contentPadding = contentPadding,
        selectedBorderWidth = selectedBorderWidth,
        titleDescriptionSpacing = titleDescriptionSpacing,
        descriptionImageSpacing = descriptionImageSpacing
    )

    @Composable
    fun styles(
        titleStyle: TextStyle = System.font.body.base.bold,
        descriptionStyle: TextStyle = System.font.body.base.bold
    ) = TariffCardStyles(
        titleStyle = titleStyle,
        descriptionStyle = descriptionStyle
    )
}

@Composable
fun TariffCard(
    title: String,
    description: String? = null,
    selected: Boolean,
    painter: Painter = painterResource(Res.drawable.img_car_comfort),
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: TariffCardColors = TariffCardDefaults.colors(),
    dimens: TariffCardDimens = TariffCardDefaults.dimens(),
    styles: TariffCardStyles = TariffCardDefaults.styles()
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(dimens.height)
            .widthIn(min = dimens.minWidth),
        shape = dimens.shape,
        color = if (selected) colors.selectedContainerColor else colors.containerColor,
        border = if (selected) {
            BorderStroke(
                brush = colors.selectedBorder,
                width = dimens.selectedBorderWidth
            )
        } else null
    ) {
        Column(
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            Text(
                text = title,
                color = colors.titleColor,
                style = styles.titleStyle
            )

            description?.let { desc ->
                Spacer(modifier = Modifier.height(dimens.titleDescriptionSpacing))

                Text(
                    text = desc,
                    color = colors.descriptionColor,
                    style = styles.descriptionStyle
                )
            }

            Spacer(modifier = Modifier.height(dimens.descriptionImageSpacing))

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TariffCard(
    title: String,
    description: String? = null,
    selected: Boolean,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: TariffCardColors = TariffCardDefaults.colors(),
    dimens: TariffCardDimens = TariffCardDefaults.dimens(),
    styles: TariffCardStyles = TariffCardDefaults.styles()
) = TariffCard(
    title = title,
    description = description,
    selected = selected,
    painter = rememberAsyncImagePainter(model = imageUrl),
    onClick = onClick,
    modifier = modifier,
    colors = colors,
    dimens = dimens,
    styles = styles
)

@Preview
@Composable
private fun Preview() = YallaTheme {
    LazyRow(
        contentPadding = PaddingValues(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(2) { order ->
            item {
                TariffCard(
                    title = "Tariff ${order + 1}",
                    description = if (order % 2 == 0) "15 000 sum" else null,
                    selected = order % 2 == 0,
                    onClick = {}
                )
            }
        }
    }
}
