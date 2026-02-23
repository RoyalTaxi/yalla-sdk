package uz.yalla.components.primitive.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.ic_focus_location
import uz.yalla.resources.location_gps_title

/**
 * Default configuration values for [EnableLocationButton].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object EnableLocationButtonDefaults {
    /**
     * Color configuration for [EnableLocationButton].
     *
     * @param text Text color.
     * @param icon Icon tint color.
     */
    data class EnableLocationButtonColors(
        val text: Color,
        val icon: Color
    )

    @Composable
    fun colors(
        text: Color = System.color.textWhite,
        icon: Color = System.color.iconWhite
    ) = EnableLocationButtonColors(
        text = text,
        icon = icon
    )

    /**
     * Text style configuration for [EnableLocationButton].
     *
     * @param label Style applied to the button text.
     */
    data class EnableLocationButtonStyle(
        val label: TextStyle
    )

    @Composable
    fun style(label: TextStyle = System.font.body.caption) =
        EnableLocationButtonStyle(
            label = label
        )

    /**
     * Dimension configuration for [EnableLocationButton].
     *
     * @param contentPadding Padding inside the button.
     * @param iconSpacing Spacing between text and icon.
     */
    data class EnableLocationButtonDimens(
        val contentPadding: PaddingValues,
        val iconSpacing: Dp
    )

    @Composable
    fun dimens(
        contentPadding: PaddingValues =
            PaddingValues(
                top = 8.dp,
                bottom = 8.dp,
                start = 22.dp,
                end = 12.dp
            ),
        iconSpacing: Dp = 14.dp
    ) = EnableLocationButtonDimens(
        contentPadding = contentPadding,
        iconSpacing = iconSpacing
    )
}

/**
 * Button prompting user to enable location services.
 *
 * ## Usage
 *
 * ```kotlin
 * EnableLocationButton(
 *     onClick = { requestLocationPermission() }
 * )
 * ```
 *
 * @param onClick Invoked when button is clicked
 * @param modifier Applied to button
 * @param colors Color configuration, defaults to [EnableLocationButtonDefaults.colors]
 * @param style Text style configuration, defaults to [EnableLocationButtonDefaults.style]
 * @param dimens Dimension configuration, defaults to [EnableLocationButtonDefaults.dimens]
 */
@Composable
fun EnableLocationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: EnableLocationButtonDefaults.EnableLocationButtonColors = EnableLocationButtonDefaults.colors(),
    style: EnableLocationButtonDefaults.EnableLocationButtonStyle = EnableLocationButtonDefaults.style(),
    dimens: EnableLocationButtonDefaults.EnableLocationButtonDimens = EnableLocationButtonDefaults.dimens()
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = dimens.contentPadding
    ) {
        Text(
            text = stringResource(Res.string.location_gps_title),
            color = colors.text,
            style = style.label
        )

        Spacer(modifier = Modifier.width(dimens.iconSpacing))

        Icon(
            painter = painterResource(Res.drawable.ic_focus_location),
            contentDescription = null,
            tint = colors.icon
        )
    }
}
