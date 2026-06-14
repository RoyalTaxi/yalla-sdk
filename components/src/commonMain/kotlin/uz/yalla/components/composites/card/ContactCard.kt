package uz.yalla.components.composites.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.Phone
import uz.yalla.resources.icons.Telegram
import uz.yalla.resources.icons.YallaIcons

@Composable
public fun ContactCard(
    title: String,
    painter: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(System.color.background.secondary),
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color =
                    System.color.background.brand
                        .copy(.15f)
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = System.color.background.brand,
                    modifier =
                        Modifier
                            .padding(10.dp)
                            .size(24.dp)
                )
            }

            Text(
                text = title,
                color = System.color.text.base,
                style = System.font.title.base
            )
        }
    }
}

@Preview
@Composable
private fun Preview() =
    YallaTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ContactCard(
                title = "Telegram",
                painter = rememberVectorPainter(YallaIcons.Telegram),
                modifier = Modifier.weight(1f),
                onClick = {}
            )

            ContactCard(
                title = "Phone Call",
                painter = rememberVectorPainter(YallaIcons.Phone),
                modifier = Modifier.weight(1f),
                onClick = {}
            )
        }
    }
