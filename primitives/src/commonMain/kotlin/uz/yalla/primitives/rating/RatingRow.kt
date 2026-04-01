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

/**
 * Color configuration for [RatingRow].
 *
 * Controls the visual appearance of filled (selected) and empty (unselected) stars.
 * Use [RatingRowDefaults.colors] to create with theme-aware defaults.
 *
 * @param filled Tint color applied to stars at or below the current rating.
 * @param empty Tint color applied to stars above the current rating.
 * @since 0.0.1
 */
@Immutable
data class RatingRowColors(
    val filled: Color,
    val empty: Color,
)

/**
 * Dimension configuration for [RatingRow].
 *
 * Controls the size, padding, spacing, and count of star icons.
 * Use [RatingRowDefaults.dimens] to create with standard values.
 *
 * @param starSize Size (width and height) of each star icon.
 * @param starPadding Internal padding inside each star's clickable surface.
 * @param starSpacing Horizontal spacing between adjacent stars.
 * @param starCount Total number of stars displayed in the row.
 * @since 0.0.1
 */
@Immutable
data class RatingRowDimens(
    val starSize: Dp,
    val starPadding: Dp,
    val starSpacing: Dp,
    val starCount: Int,
)

/**
 * Default configuration values for [RatingRow].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be individually overridden.
 * @since 0.0.1
 */
object RatingRowDefaults {

    /**
     * Creates theme-aware color configuration for [RatingRow].
     *
     * @param filled Tint color for selected stars.
     * @param empty Tint color for unselected stars.
     */
    @Composable
    fun colors(
        filled: Color = System.color.background.brand,
        empty: Color = System.color.icon.disabled,
    ): RatingRowColors = RatingRowColors(
        filled = filled,
        empty = empty,
    )

    /**
     * Creates dimension configuration for [RatingRow].
     *
     * @param starSize Size of each star icon.
     * @param starPadding Internal padding inside each star surface.
     * @param starSpacing Horizontal spacing between stars.
     * @param starCount Total number of stars.
     */
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

/**
 * Interactive star rating input row.
 *
 * Displays a horizontal row of star icons that the user can tap to set a rating.
 * Stars at or below the current [rating] are filled; stars above are empty.
 * Each star is wrapped in a circular [Surface] for accessible click handling.
 *
 * ## Usage
 *
 * ```kotlin
 * var rating by remember { mutableStateOf<Int?>(null) }
 *
 * RatingRow(
 *     rating = rating,
 *     onRatingChange = { rating = it },
 * )
 * ```
 *
 * ## Custom Star Count
 *
 * ```kotlin
 * RatingRow(
 *     rating = rating,
 *     onRatingChange = { rating = it },
 *     dimens = RatingRowDefaults.dimens(starCount = 10, starSize = 30.dp),
 * )
 * ```
 *
 * @param rating Current rating value (1-based), or `null` if no rating is selected.
 * @param onRatingChange Called with the new rating (1-based) when a star is tapped.
 * @param modifier [Modifier] applied to the row container.
 * @param colors [RatingRowColors] that define filled and empty star tints.
 *   See [RatingRowDefaults.colors].
 * @param dimens [RatingRowDimens] that define star size, padding, spacing, and count.
 *   See [RatingRowDefaults.dimens].
 *
 * @see RatingRowDefaults for default values
 * @since 0.0.1
 */
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
