package uz.yalla.components.composites.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_avatar_placeholder
import uz.yalla.resources.img_car_economy

@Immutable
data class ActiveOrderCardColors(
    val containerColor: Color,
    val titleColor: Color,
    val subtitleColor: Color
)

@Immutable
data class ActiveOrderCardDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val photoSize: Dp,
    val photoShape: Shape,
    val contentSpacing: Dp,
    val carImageWidth: Dp,
    val carImageHeight: Dp
)

@Immutable
data class ActiveOrderCardStyles(
    val titleStyle: TextStyle,
    val subtitleStyle: TextStyle
)

object ActiveOrderCardDefaults {
    @Composable
    fun colors(
        containerColor: Color = System.color.background.secondary,
        titleColor: Color = System.color.text.base,
        subtitleColor: Color = System.color.text.base
    ) = ActiveOrderCardColors(
        containerColor = containerColor,
        titleColor = titleColor,
        subtitleColor = subtitleColor
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(16.dp),
        photoSize: Dp = 48.dp,
        photoShape: Shape = RoundedCornerShape(12.dp),
        contentSpacing: Dp = 16.dp,
        carImageWidth: Dp = 64.dp,
        carImageHeight: Dp = 40.dp
    ) = ActiveOrderCardDimens(
        shape = shape,
        contentPadding = contentPadding,
        photoSize = photoSize,
        photoShape = photoShape,
        contentSpacing = contentSpacing,
        carImageWidth = carImageWidth,
        carImageHeight = carImageHeight
    )

    @Composable
    fun styles(
        titleStyle: TextStyle = System.font.body.large.bold,
        subtitleStyle: TextStyle = System.font.body.small.medium
    ) = ActiveOrderCardStyles(
        titleStyle = titleStyle,
        subtitleStyle = subtitleStyle
    )
}

@Composable
fun ActiveOrderCard(
    photoUrl: String?,
    driverName: String,
    vehicleLabel: String,
    stateNumber: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ActiveOrderCardColors = ActiveOrderCardDefaults.colors(),
    dimens: ActiveOrderCardDimens = ActiveOrderCardDefaults.dimens(),
    styles: ActiveOrderCardStyles = ActiveOrderCardDefaults.styles()
) {
    Card(
        onClick = onClick,
        shape = dimens.shape,
        colors = CardDefaults.cardColors(colors.containerColor),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(dimens.contentPadding)
                .height(IntrinsicSize.Min)
        ) {
            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(Res.drawable.img_avatar_placeholder),
                error = painterResource(Res.drawable.img_avatar_placeholder),
                fallback = painterResource(Res.drawable.img_avatar_placeholder),
                modifier = Modifier
                    .size(dimens.photoSize)
                    .clip(dimens.photoShape)
            )

            Spacer(modifier = Modifier.width(dimens.contentSpacing))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = driverName,
                    color = colors.titleColor,
                    style = styles.titleStyle
                )

                Text(
                    text = vehicleLabel,
                    color = colors.subtitleColor,
                    style = styles.subtitleStyle
                )
            }

            Spacer(modifier = Modifier.width(dimens.contentSpacing))

            Box(modifier = Modifier.fillMaxHeight()) {
                CarPaletteCard(
                    stateNumber = stateNumber,
                    modifier = Modifier.align(Alignment.TopEnd)
                )

                Image(
                    painter = painterResource(Res.drawable.img_car_economy),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .height(dimens.carImageHeight)
                        .width(dimens.carImageWidth)
                        .graphicsLayer { rotationY = 180f }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ActiveOrderCardPreview() {
    YallaTheme {
        ActiveOrderCard(
            photoUrl = null,
            driverName = "Islom",
            vehicleLabel = "Серый Song Plus",
            stateNumber = "60LBPSH7",
            onClick = {}
        )
    }
}
