package uz.yalla.components.composite.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Default configuration values for [LocationButton].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object LocationButtonDefaults {
    /**
     * Color configuration for [LocationButton].
     *
     * @param container Button background color.
     * @param text Text color.
     */
    data class LocationButtonColors(
        val container: Color,
        val text: Color
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundSecondary,
        text: Color = System.color.textBase
    ) = LocationButtonColors(
        container = container,
        text = text
    )

    /**
     * Text style configuration for [LocationButton].
     *
     * @param label Style applied to the button text.
     */
    data class LocationButtonStyle(
        val label: TextStyle
    )

    @Composable
    fun style(label: TextStyle = System.font.body.base.bold) =
        LocationButtonStyle(
            label = label
        )

    /**
     * Dimension configuration for [LocationButton].
     *
     * @param shape Button shape.
     * @param contentPadding Padding inside the button.
     * @param iconSpacing Spacing between icons and text.
     */
    data class LocationButtonDimens(
        val shape: Shape,
        val contentPadding: PaddingValues,
        val iconSpacing: Dp
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(0.dp),
        iconSpacing: Dp = 12.dp
    ) = LocationButtonDimens(
        shape = shape,
        contentPadding = contentPadding,
        iconSpacing = iconSpacing
    )
}

/**
 * Simple location button with single text and optional icons.
 *
 * For multi-location display with arrows, use [LocationItem] instead.
 *
 * ## Usage
 *
 * ```kotlin
 * LocationButton(
 *     text = "Where to?",
 *     modifier = Modifier.fillMaxWidth().height(60.dp),
 *     onClick = { openLocationPicker() },
 *     leadingIcon = { LocationDot(color = System.color.buttonActive) }
 * )
 * ```
 *
 * @param text Button label text
 * @param onClick Invoked when button is clicked
 * @param modifier Applied to button
 * @param leadingIcon Optional leading content
 * @param trailingIcon Optional trailing content
 * @param colors Color configuration, defaults to [LocationButtonDefaults.colors]
 * @param style Text style configuration, defaults to [LocationButtonDefaults.style]
 * @param dimens Dimension configuration, defaults to [LocationButtonDefaults.dimens]
 *
 * @see LocationItem for multi-location display
 */
@Composable
fun LocationButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: LocationButtonDefaults.LocationButtonColors = LocationButtonDefaults.colors(),
    style: LocationButtonDefaults.LocationButtonStyle = LocationButtonDefaults.style(),
    dimens: LocationButtonDefaults.LocationButtonDimens = LocationButtonDefaults.dimens()
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        contentPadding = dimens.contentPadding,
        colors = ButtonDefaults.buttonColors(colors.container),
        shape = dimens.shape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.iconSpacing)
        ) {
            leadingIcon?.let { icon ->
                icon()
            }

            Text(
                text = text,
                color = colors.text,
                style = style.label,
                modifier = Modifier.weight(1f)
            )

            trailingIcon?.let { icon ->
                icon()
            }
        }
    }
}
