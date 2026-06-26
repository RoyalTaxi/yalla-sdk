package uz.yalla.components.composites.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.components.primitives.button.AddAddressButton
import uz.yalla.design.image.ThemedImage
import uz.yalla.design.image.rememberThemedPainter
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.places_form_new

@Composable
public fun AddAddressCardExpanded(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(System.color.background.secondary),
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    text = stringResource(Res.string.places_form_new),
                    style = System.font.body.base.bold,
                    color = System.color.text.base,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Image(
                    painter = rememberThemedPainter(ThemedImage.MapPin),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(80.dp)
                )
            }

            AddAddressButton(
                onClick = onClick,
                layersCount = 4,
                paddingBetween = 28.dp,
                modifier = Modifier.align(Alignment.Bottom).padding(12.dp)
            )
        }
    }
}
