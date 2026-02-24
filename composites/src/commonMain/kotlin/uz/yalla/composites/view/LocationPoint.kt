package uz.yalla.composites.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [LocationPoint].
 *
 * @property icon Location marker icon.
 * @property label Address or place name.
 */
data class LocationPointState(
    val icon: Painter,
    val label: String,
)

/**
 * Location point display with icon and label.
 *
 * Use for displaying origin/destination points in route views.
 *
 * ## Usage
 *
 * ```kotlin
 * LocationPoint(
 *     state = LocationPointState(
 *         icon = painterResource(Res.drawable.ic_origin),
 *         label = "123 Main Street",
 *     ),
 *     style = LocationPointDefaults.originStyle(),
 * )
 * ```
 *
 * @param state Location point state with icon and label.
 * @param modifier Applied to component.
 * @param style Text and color styling, defaults to [LocationPointDefaults.originStyle].
 * @param dimens Dimension configuration, defaults to [LocationPointDefaults.dimens].
 *
 * @see LocationPointState for state configuration
 * @see LocationPointDefaults for default values
 */
@Composable
fun LocationPoint(
    state: LocationPointState,
    modifier: Modifier = Modifier,
    style: LocationPointDefaults.LocationPointStyle = LocationPointDefaults.originStyle(),
    dimens: LocationPointDefaults.LocationPointDimens = LocationPointDefaults.dimens(),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = state.icon,
            contentDescription = null,
        )

        Spacer(Modifier.width(dimens.iconLabelSpacing))

        Text(
            text = state.label,
            style = style.font,
            color = style.color,
            overflow = TextOverflow.Ellipsis,
            maxLines = dimens.labelMaxLines,
        )
    }
}

/**
 * Default values for [LocationPoint].
 *
 * Provides theme-aware defaults for [originStyle], [destinationStyle], and [dimens] that can be overridden.
 */
object LocationPointDefaults {
    /**
     * Style configuration for [LocationPoint].
     *
     * @param font Text style.
     * @param color Text color.
     */
    data class LocationPointStyle(
        val font: TextStyle,
        val color: Color,
    )

    @Composable
    fun originStyle(
        font: TextStyle = System.font.body.small.bold,
        color: Color = System.color.textBase,
    ): LocationPointStyle =
        LocationPointStyle(
            font = font,
            color = color,
        )

    @Composable
    fun destinationStyle(
        font: TextStyle = System.font.body.caption,
        color: Color = System.color.textSubtle,
    ): LocationPointStyle =
        LocationPointStyle(
            font = font,
            color = color,
        )

    /**
     * Dimension configuration for [LocationPoint].
     *
     * @param iconLabelSpacing Spacing between icon and label.
     * @param labelMaxLines Maximum lines for label text.
     */
    data class LocationPointDimens(
        val iconLabelSpacing: Dp,
        val labelMaxLines: Int,
    )

    @Composable
    fun dimens(
        iconLabelSpacing: Dp = 8.dp,
        labelMaxLines: Int = 1,
    ): LocationPointDimens =
        LocationPointDimens(
            iconLabelSpacing = iconLabelSpacing,
            labelMaxLines = labelMaxLines,
        )
}

@Preview
@Composable
private fun LocationPointPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        Text(
            text = "123 Main Street",
            style = System.font.body.small.bold,
        )
    }
}
