package uz.yalla.components.composites.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_car_business

@Composable
fun HistoryCard(
    origin: String,
    destination: String,
    time: String,
    status: String,
    price: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    statusColor: Color = System.color.text.base
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(System.color.background.secondary),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Locations(
                    origin = origin,
                    destination = destination
                )

                Spacer(modifier = Modifier.weight(1f))

                Details(
                    time = time,
                    status = status,
                    statusColor = statusColor
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = price,
                    color = System.color.text.base,
                    style = System.font.body.base.bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(Res.drawable.img_car_business),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .height(56.dp)
                        .width(128.dp)
                        .graphicsLayer { rotationY = 180f }
                )
            }
        }
    }
}

@Composable
private fun PairLayout(
    leading: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        leading()
        content()
    }
}

@Composable
private fun Locations(
    origin: String,
    destination: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PairLayout(
            leading = {
                LocationPoint(color = System.color.background.brand)
            },
            content = {
                Text(
                    text = origin,
                    color = System.color.text.base,
                    style = System.font.body.base.bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )

        PairLayout(
            leading = {
                LocationPoint(color = System.color.icon.red)
            },
            content = {
                Text(
                    text = destination,
                    color = System.color.text.subtle,
                    style = System.font.body.caption
                )
            }
        )
    }
}

@Composable
private fun Details(
    time: String,
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    PairLayout(
        modifier = modifier,
        leading = {
            Text(
                text = time,
                color = System.color.text.base,
                style = System.font.body.caption
            )
        },
        content = {
            Text(
                text = status,
                color = statusColor,
                style = System.font.body.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}

@Composable
private fun LocationPoint(color: Color) {
    Box(
        modifier = Modifier
            .size(14.dp)
            .background(
                color = color,
                shape = CircleShape
            )
            .padding(4.dp)
            .background(
                shape = CircleShape,
                color = System.color.icon.white
            )
    )
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HistoryCard(
            origin = "Mustaqillik avenue, 12",
            destination = "Yunusobod, 19-mavze, 42-uy",
            time = "14:32",
            status = "Completed",
            price = "45 000 sum",
            onClick = {}
        )

        HistoryCard(
            origin = "Tashkent International Airport",
            destination = "Hilton Tashkent City",
            time = "09:15",
            status = "Cancelled",
            price = "62 000 sum",
            onClick = {}
        )
    }
}
