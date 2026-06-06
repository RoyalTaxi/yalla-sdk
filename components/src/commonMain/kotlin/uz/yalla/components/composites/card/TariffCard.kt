package uz.yalla.components.composites.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_car_comfort

@Composable
fun TariffCard(
    title: String,
    description: String? = null,
    selected: Boolean,
    painter: Painter = painterResource(Res.drawable.img_car_comfort),
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.widthIn(140.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) System.color.background.base else System.color.background.secondary,
        border = BorderStroke(
            brush = System.color.gradient.sunsetNight,
            width = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                color = System.color.text.base,
                style = System.font.body.base.bold
            )

            Spacer(modifier = Modifier.heightIn(4.dp))

            description?.let { desc ->
                Text(
                    text = desc,
                    color = System.color.text.base,
                    style = System.font.body.small.medium
                )

                Spacer(modifier = Modifier.heightIn(8.dp))
            }

            Spacer(modifier = Modifier.heightIn(4.dp))

            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(96.dp)
                    .height(44.dp)
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
    modifier: Modifier = Modifier
) = TariffCard(
    title = title,
    description = description,
    selected = selected,
    painter = rememberAsyncImagePainter(model = imageUrl),
    onClick = onClick,
    modifier = modifier
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
