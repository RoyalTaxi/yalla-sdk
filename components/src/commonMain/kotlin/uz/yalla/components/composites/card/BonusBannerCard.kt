package uz.yalla.components.composites.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_banner_bonus

@Immutable
data class BonusBannerCardDimens(
    val shape: Shape,
    val contentPadding: PaddingValues
)

object BonusBannerCardDefaults {
    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(20.dp)
    ) = BonusBannerCardDimens(
        shape = shape,
        contentPadding = contentPadding
    )
}

@Composable
fun BonusBannerCard(
    title: String,
    subtitle: String,
    amount: String,
    modifier: Modifier = Modifier,
    background: Painter = painterResource(Res.drawable.img_banner_bonus),
    dimens: BonusBannerCardDimens = BonusBannerCardDefaults.dimens()
) {
    Box(
        modifier = modifier.clip(dimens.shape)
    ) {
        Image(
            painter = background,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .matchParentSize()
                .padding(dimens.contentPadding)
        ) {
            Text(
                text = title,
                style = System.font.body.base.medium,
                color = System.color.text.white
            )
            Text(
                text = subtitle,
                style = System.font.body.small.medium,
                color = System.color.text.white
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = amount,
                style = System.font.title.xLarge,
                color = System.color.text.white
            )
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    BonusBannerCard(
        title = "Bonuslar",
        subtitle = "1 tanga = 1 so'm",
        amount = "30000",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
