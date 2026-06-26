package uz.yalla.components.composites.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.resources.icons.Origin
import uz.yalla.resources.icons.YallaIcons

@Composable
public fun SavedAddressCard(
    title: String,
    address: String,
    eta: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(System.color.background.secondary),
        modifier = modifier.widthIn(max = 248.dp).height(120.dp)
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .padding(vertical = 16.dp, horizontal = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = rememberVectorPainter(YallaIcons.Origin),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = System.color.text.base,
                    style = System.font.body.base.bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = address,
                color = System.color.text.base,
                style = System.font.body.small.medium
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = eta,
                color = System.color.text.base,
                style = System.font.body.base.bold
            )
        }
    }
}
