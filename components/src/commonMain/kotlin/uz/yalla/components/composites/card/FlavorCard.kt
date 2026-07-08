package uz.yalla.components.composites.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_service_intercity_small
import uz.yalla.resources.img_service_taxi_small
import uz.yalla.resources.service_intercity
import uz.yalla.resources.service_taxi

@Composable
public fun TaxiFlavorItem(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.padding(8.dp).height(32.dp)
        )

        Text(
            text = title,
            color = System.color.text.base,
            style = System.font.title.base,
            modifier = Modifier.padding(vertical = 8.dp).padding(end = 16.dp)
        )
    }
}

@Composable
public fun TaxiFlavorBadge(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String
) {
    TaxiFlavorItem(
        modifier =
            modifier
                .clip(CircleShape)
                .background(System.color.background.base),
        icon = icon,
        title = title
    )
}

@Preview
@Composable
private fun Preview() =
    YallaTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier =
                Modifier
                    .background(System.color.background.secondary)
                    .padding(16.dp)
        ) {
            TaxiFlavorItem(
                icon = painterResource(Res.drawable.img_service_taxi_small),
                title = stringResource(Res.string.service_taxi)
            )

            TaxiFlavorBadge(
                icon = painterResource(Res.drawable.img_service_intercity_small),
                title = stringResource(Res.string.service_intercity)
            )
        }
    }
