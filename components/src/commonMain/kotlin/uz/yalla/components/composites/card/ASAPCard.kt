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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.icons.Checked
import uz.yalla.resources.icons.Thunder
import uz.yalla.resources.icons.Unchecked
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.intercity_setup_fastest_subtitle
import uz.yalla.resources.intercity_setup_fastest_title

@Composable
public fun ASAPCard(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(System.color.background.secondary),
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                painter = rememberVectorPainter(YallaIcons.Thunder),
                contentDescription = null,
                tint = System.color.background.brand,
                modifier = Modifier.size(24.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(Res.string.intercity_setup_fastest_title),
                    color = System.color.text.base,
                    style = System.font.body.base.bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(Res.string.intercity_setup_fastest_subtitle),
                    color = System.color.text.subtle,
                    style = System.font.body.small.medium,
                )
            }

            Icon(
                imageVector = if (enabled) YallaIcons.Checked else YallaIcons.Unchecked,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ASAPCard(
            enabled = true,
            onClick = {}
        )

        ASAPCard(
            enabled = false,
            onClick = {}
        )
    }
}
