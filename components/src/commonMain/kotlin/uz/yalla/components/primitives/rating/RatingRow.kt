package uz.yalla.components.primitives.rating

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import uz.yalla.design.theme.YallaTheme

@Composable
public fun RatingRow(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Rating(
        rating = rating,
        onRatingChange = onRatingChange,
        modifier = modifier
    )
}

@Preview
@Composable
private fun Preview() =
    YallaTheme {
        RatingRow(rating = 3, onRatingChange = {})
    }
