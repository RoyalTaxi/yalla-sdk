package uz.yalla.components.composites.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.icons.Case
import uz.yalla.resources.icons.Home
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.places_add_hint

@Composable
fun PlaceCard(
    title: String,
    painter: Painter,
    location: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = System.color.background.secondary,
        modifier = modifier
            .heightIn(120.dp)
    ) {
        Box {
            Text(
                text = title,
                color = System.color.text.base,
                style = System.font.title.base,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (location == null) System.color.background.tertiary
                else System.color.background.brand.copy(.15f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = if (location == null) System.color.icon.subtle
                    else System.color.background.brand,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Text(
                text = location ?: stringResource(Res.string.places_add_hint),
                color = if (location == null) System.color.text.subtle
                else System.color.text.base,
                style = System.font.body.caption,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    )
            )
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlaceCard(
            title = "Home",
            painter = rememberVectorPainter(YallaIcons.Home),
            location = "Andijan Region, Asaka City, Chirvon 8",
            onClick = {},
            modifier = Modifier.weight(1f)
        )

        PlaceCard(
            title = "Work",
            painter = rememberVectorPainter(YallaIcons.Case),
            onClick = {},
            modifier = Modifier.weight(1f)
        )
    }
}
