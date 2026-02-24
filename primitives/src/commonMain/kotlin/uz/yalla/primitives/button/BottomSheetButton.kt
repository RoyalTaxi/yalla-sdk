package uz.yalla.primitives.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [BottomSheetButton] component.
 *
 * @property text Button label text.
 * @property painter Icon painter.
 * @property enabled Whether button is clickable.
 */
data class BottomSheetButtonState(
    val text: String,
    val painter: Painter,
    val enabled: Boolean = true,
)

/**
 * Default configuration values for [BottomSheetButton].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object BottomSheetButtonDefaults {
    /**
     * Color configuration for [BottomSheetButton].
     *
     * @param container Button background color.
     * @param text Text color.
     * @param icon Icon tint color.
     */
    data class BottomSheetButtonColors(
        val container: Color,
        val text: Color,
        val icon: Color
    )

    @Composable
    fun colors(
        container: Color = System.color.buttonTertiary,
        text: Color = System.color.backgroundBase,
        icon: Color = Color.Unspecified
    ) = BottomSheetButtonColors(
        container = container,
        text = text,
        icon = icon
    )

    /**
     * Text style configuration for [BottomSheetButton].
     *
     * @param label Style applied to the button text.
     */
    data class BottomSheetButtonStyle(
        val label: TextStyle
    )

    @Composable
    fun style(label: TextStyle = System.font.body.base.medium) =
        BottomSheetButtonStyle(
            label = label
        )

    /**
     * Dimension configuration for [BottomSheetButton].
     *
     * @param shape Button shape.
     * @param height Button height.
     * @param iconSpacing Spacing between icon and text.
     */
    data class BottomSheetButtonDimens(
        val shape: Shape,
        val height: Dp,
        val iconSpacing: Dp
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        height: Dp = 60.dp,
        iconSpacing: Dp = 8.dp
    ) = BottomSheetButtonDimens(
        shape = shape,
        height = height,
        iconSpacing = iconSpacing
    )
}

/**
 * Button for bottom sheet actions with icon and text.
 *
 * ## Usage
 *
 * ```kotlin
 * BottomSheetButton(
 *     state = BottomSheetButtonState(
 *         text = "Call Driver",
 *         painter = painterResource(Res.drawable.ic_phone),
 *     ),
 *     onClick = { callDriver() },
 *     modifier = Modifier.weight(1f),
 * )
 * ```
 *
 * @param state Button state containing text, painter, and enabled.
 * @param onClick Invoked on click.
 * @param modifier Applied to button.
 * @param colors Color configuration, defaults to [BottomSheetButtonDefaults.colors].
 * @param style Text style configuration, defaults to [BottomSheetButtonDefaults.style].
 * @param dimens Dimension configuration, defaults to [BottomSheetButtonDefaults.dimens].
 *
 * @see BottomSheetButtonState for state configuration
 * @see BottomSheetButtonDefaults for default values
 */
@Composable
fun BottomSheetButton(
    state: BottomSheetButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: BottomSheetButtonDefaults.BottomSheetButtonColors = BottomSheetButtonDefaults.colors(),
    style: BottomSheetButtonDefaults.BottomSheetButtonStyle = BottomSheetButtonDefaults.style(),
    dimens: BottomSheetButtonDefaults.BottomSheetButtonDimens = BottomSheetButtonDefaults.dimens()
) {
    Button(
        enabled = state.enabled,
        shape = dimens.shape,
        contentPadding = PaddingValues.Zero,
        colors = ButtonDefaults.buttonColors(colors.container),
        modifier = modifier.height(dimens.height),
        onClick = onClick
    ) {
        Icon(
            tint = colors.icon,
            painter = state.painter,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(dimens.iconSpacing))

        Text(
            text = state.text,
            color = colors.text,
            style = style.label
        )
    }
}
