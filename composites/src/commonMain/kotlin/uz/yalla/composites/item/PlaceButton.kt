package uz.yalla.composites.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [PlaceButton].
 *
 * @param container Button background color.
 * @param text Text color.
 * @since 0.0.1
 */
@Immutable
data class PlaceButtonColors(
    val container: Color,
    val text: Color,
)

/**
 * Dimension configuration for [PlaceButton].
 *
 * @param shape Button shape.
 * @param contentPadding Padding inside the button.
 * @param iconSpacing Spacing between icons and text.
 * @since 0.0.1
 */
@Immutable
data class PlaceButtonDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val iconSpacing: Dp,
)

/**
 * Default configuration values for [PlaceButton].
 *
 * @since 0.0.1
 */
object PlaceButtonDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = System.color.background.secondary,
        text: Color = System.color.text.base,
    ) = PlaceButtonColors(
        container = container,
        text = text,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(0.dp),
        iconSpacing: Dp = 12.dp,
    ) = PlaceButtonDimens(
        shape = shape,
        contentPadding = contentPadding,
        iconSpacing = iconSpacing,
    )
}

/**
 * Button for saved places with leading and trailing icon slots.
 *
 * Used in the home search bar to let users quickly select favorite places
 * (home, work, etc.).
 *
 * ## Usage
 *
 * ```kotlin
 * PlaceButton(
 *     text = "Home",
 *     onClick = { selectHome() },
 *     leadingIcon = { Icon(YallaIcons.Home, null) },
 *     trailingIcon = { Icon(YallaIcons.Edit, null) },
 * )
 * ```
 *
 * @param text Button text.
 * @param onClick Called when the button is clicked.
 * @param modifier Applied to the root button.
 * @param leadingIcon Optional icon before the text.
 * @param trailingIcon Optional icon after the text.
 * @param colors Color configuration, defaults to [PlaceButtonDefaults.colors].
 * @param dimens Dimension configuration, defaults to [PlaceButtonDefaults.dimens].
 *
 * @see PlaceButtonDefaults
 * @since 0.0.1
 */
@Composable
fun PlaceButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: PlaceButtonColors = PlaceButtonDefaults.colors(),
    dimens: PlaceButtonDimens = PlaceButtonDefaults.dimens(),
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        contentPadding = dimens.contentPadding,
        colors = ButtonDefaults.buttonColors(colors.container),
        shape = dimens.shape,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.iconSpacing),
        ) {
            leadingIcon?.let { icon ->
                icon()
            }

            Text(
                text = text,
                color = colors.text,
                style = System.font.body.base.bold,
                modifier = Modifier.weight(1f),
            )

            trailingIcon?.let { icon ->
                icon()
            }
        }
    }
}
