package uz.yalla.components.composites.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.driver_label_rating
import uz.yalla.resources.driver_label_title
import uz.yalla.resources.icons.Star
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.img_avatar_placeholder

@Composable
public fun DriverCard(
    painter: Painter,
    firstName: String,
    lastName: String,
    rating: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Image(
                painter = painter,
                contentDescription = null,
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .size(80.dp)
                        .padding(bottom = 12.dp)
            )

            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .background(
                            shape = CircleShape,
                            brush = System.color.gradient.sunsetNight
                        )
            ) {
                Text(
                    text = stringResource(Res.string.driver_label_title),
                    color = System.color.text.white,
                    style = System.font.body.small.medium,
                    modifier =
                        Modifier.padding(
                            vertical = 4.dp,
                            horizontal = 10.dp
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$firstName $lastName",
            color = System.color.text.base,
            style = System.font.title.large,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(Res.string.driver_label_rating),
                color = System.color.text.subtle,
                style = System.font.body.small.medium
            )

            Text(
                text = rating,
                color = System.color.text.base,
                style = System.font.body.small.medium
            )

            Image(
                painter = rememberVectorPainter(YallaIcons.Star),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
public fun DriverCard(
    imageUrl: String,
    firstName: String,
    lastName: String,
    rating: String,
    modifier: Modifier = Modifier
) {
    val fallback = painterResource(Res.drawable.img_avatar_placeholder)

    DriverCard(
        painter =
            rememberAsyncImagePainter(
                model = imageUrl,
                placeholder = fallback,
                error = fallback,
                fallback = fallback
            ),
        firstName = firstName,
        lastName = lastName,
        rating = rating,
        modifier = modifier
    )
}

@Preview
@Composable
private fun Preview() =
    YallaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DriverCard(
                painter = painterResource(Res.drawable.img_avatar_placeholder),
                firstName = "Sherzod",
                lastName = "Karimov",
                rating = "4.92"
            )

            DriverCard(
                painter = painterResource(Res.drawable.img_avatar_placeholder),
                firstName = "Konstantin",
                lastName = "Aleksandrovich",
                rating = "5.00"
            )

            DriverCard(
                painter = painterResource(Res.drawable.img_avatar_placeholder),
                firstName = "Ali",
                lastName = "Vali",
                rating = "3.7"
            )
        }
    }
