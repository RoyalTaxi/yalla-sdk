package uz.yalla.components.primitives.rating

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.Star
import uz.yalla.resources.icons.YallaIcons

private val StarPadding = 10.dp
private val StarSpacing = 6.dp

@Composable
public fun Rating(
    rating: Int?,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(StarSpacing)
    ) {
        repeat(5) { index ->
            val isFilled = rating != null && index < rating

            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                onClick = { onRatingChange(index + 1) },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                Image(
                    painter = rememberVectorPainter(YallaIcons.Star),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        if (isFilled) System.color.background.brand
                        else System.color.icon.disabled
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(StarPadding)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Rating(rating = 3, onRatingChange = {})
}
