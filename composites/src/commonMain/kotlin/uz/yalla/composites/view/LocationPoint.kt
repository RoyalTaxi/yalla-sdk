package uz.yalla.composites.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [LocationPoint].
 *
 * @param label Text color for the location label.
 * @since 0.0.1
 */
@Immutable
data class LocationPointColors(
    val label: Color,
)

/**
 * Dimension configuration for [LocationPoint].
 *
 * @param iconLabelSpacing Spacing between the icon and the label text.
 * @param labelMaxLines Maximum lines for the label text.
 * @since 0.0.1
 */
@Immutable
data class LocationPointDimens(
    val iconLabelSpacing: Dp,
    val labelMaxLines: Int,
)

/**
 * Default configuration values for [LocationPoint].
 *
 * @since 0.0.1
 */
object LocationPointDefaults {

    /**
     * Creates theme-aware default colors for origin points.
     */
    @Composable
    fun colors(
        label: Color = System.color.text.base,
    ): LocationPointColors = LocationPointColors(
        label = label,
    )

    /**
     * Creates theme-aware default colors for destination points.
     */
    @Composable
    fun destinationColors(
        label: Color = System.color.text.subtle,
    ): LocationPointColors = LocationPointColors(
        label = label,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        iconLabelSpacing: Dp = 8.dp,
        labelMaxLines: Int = 1,
    ): LocationPointDimens = LocationPointDimens(
        iconLabelSpacing = iconLabelSpacing,
        labelMaxLines = labelMaxLines,
    )
}

/**
 * Single location point with an icon and label.
 *
 * Used inside [RouteView] to display individual origin and destination points.
 *
 * ## Usage
 *
 * ```kotlin
 * LocationPoint(
 *     icon = painterResource(Res.drawable.ic_origin),
 *     label = "Home",
 *     colors = LocationPointDefaults.colors(),
 * )
 * ```
 *
 * @param icon Location icon painter (origin or destination).
 * @param label Location name text.
 * @param modifier Applied to the root row.
 * @param labelStyle Text style for the label.
 * @param colors Color configuration, defaults to [LocationPointDefaults.colors].
 * @param dimens Dimension configuration, defaults to [LocationPointDefaults.dimens].
 *
 * @see RouteView
 * @see LocationPointDefaults
 * @since 0.0.1
 */
@Composable
fun LocationPoint(
    icon: Painter,
    label: String,
    modifier: Modifier = Modifier,
    labelStyle: TextStyle = System.font.body.small.bold,
    colors: LocationPointColors = LocationPointDefaults.colors(),
    dimens: LocationPointDimens = LocationPointDefaults.dimens(),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = icon,
            contentDescription = null,
        )

        Spacer(Modifier.width(dimens.iconLabelSpacing))

        Text(
            text = label,
            style = labelStyle,
            color = colors.label,
            overflow = TextOverflow.Ellipsis,
            maxLines = dimens.labelMaxLines,
        )
    }
}
