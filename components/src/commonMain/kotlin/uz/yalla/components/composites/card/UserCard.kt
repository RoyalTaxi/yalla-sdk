package uz.yalla.components.composites.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_avatar_placeholder

@Composable
public fun UserCard(
    painter: Painter,
    firstName: String,
    lastName: String,
    number: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.widthIn(max = 200.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(80.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "$firstName $lastName",
            color = System.color.text.base,
            style = System.font.title.large,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = number,
            color = System.color.text.base,
            style = System.font.body.small.medium
        )
    }
}

@Composable
public fun UserCard(
    imageUrl: String,
    firstName: String,
    lastName: String,
    number: String,
    modifier: Modifier = Modifier
): Unit = UserCard(
    painter = rememberAsyncImagePainter(
        model = imageUrl,
        placeholder = painterResource(Res.drawable.img_avatar_placeholder),
        error = painterResource(Res.drawable.img_avatar_placeholder),
        fallback = painterResource(Res.drawable.img_avatar_placeholder)
    ),
    firstName = firstName,
    lastName = lastName,
    number = number,
    modifier = modifier
)

@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UserCard(
            painter = painterResource(Res.drawable.img_avatar_placeholder),
            firstName = "Islom",
            lastName = "Sheraliyev",
            number = "+998 90 123 45 67"
        )

        UserCard(
            painter = painterResource(Res.drawable.img_avatar_placeholder),
            firstName = "Ekaterina",
            lastName = "Konstantinopolskaya",
            number = "+998 90 987 65 43"
        )

        UserCard(
            painter = painterResource(Res.drawable.img_avatar_placeholder),
            firstName = "Ali",
            lastName = "Vali",
            number = "+1 415 555 0123 4567 8901"
        )
    }
}
