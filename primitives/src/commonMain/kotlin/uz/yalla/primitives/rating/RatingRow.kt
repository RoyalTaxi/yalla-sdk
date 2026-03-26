package uz.yalla.primitives.rating

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.resources.icons.Star
import uz.yalla.resources.icons.YallaIcons

@Immutable
data class RatingRowColors(
    val filled: Color,
    val empty: Color,
)

@Immutable
data class RatingRowDimens(
    val starSize: Dp,
    val starPadding: Dp,
    val starSpacing: Dp,
    val starCount: Int,
)

object RatingRowDefaults {

    @Composable
    fun colors(
        filled: Color = System.color.background.brand,
        empty: Color = System.color.icon.disabled,
    ): RatingRowColors = RatingRowColors(
        filled = filled,
        empty = empty,
    )

    fun dimens(
        starSize: Dp = 50.dp,
        starPadding: Dp = 10.dp,
        starSpacing: Dp = 6.dp,
        starCount: Int = 5,
    ): RatingRowDimens = RatingRowDimens(
        starSize = starSize,
        starPadding = starPadding,
        starSpacing = starSpacing,
        starCount = starCount,
    )
}

@Composable
fun RatingRow(
    rating: Int?,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: RatingRowColors = RatingRowDefaults.colors(),
    dimens: RatingRowDimens = RatingRowDefaults.dimens(),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.starSpacing),
    ) {
        repeat(dimens.starCount) { index ->
            val isFilled = rating != null && index < rating
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                onClick = { onRatingChange(index + 1) },
            ) {
                Image(
                    painter = rememberVectorPainter(YallaIcons.Star),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        if (isFilled) colors.filled else colors.empty
                    ),
                    modifier = Modifier
                        .padding(dimens.starPadding)
                        .size(dimens.starSize),
                )
            }
        }
    }
}
