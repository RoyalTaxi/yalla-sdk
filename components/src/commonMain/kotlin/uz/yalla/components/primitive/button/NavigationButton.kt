package uz.yalla.components.primitive.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [NavigationButton] component.
 *
 * @property icon Icon to display.
 * @property contentDescription Accessibility description.
 */
data class NavigationButtonState(
    val icon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    val contentDescription: String? = "Navigate back"
)

/**
 * Navigation back button for screen headers.
 *
 * Use in top bars to navigate back to the previous screen.
 *
 * ## Usage
 *
 * ```kotlin
 * NavigationButton(
 *     state = NavigationButtonState(),
 *     onClick = onBack
 * )
 * ```
 *
 * ## With Custom Icon
 *
 * ```kotlin
 * NavigationButton(
 *     state = NavigationButtonState(icon = Icons.Default.Close),
 *     onClick = onClose,
 * )
 * ```
 *
 * @param state Button state containing icon and contentDescription.
 * @param onClick Invoked on click.
 * @param modifier Applied to button.
 * @param colors Color configuration, defaults to [NavigationButtonDefaults.colors].
 * @param dimens Dimension configuration, defaults to [NavigationButtonDefaults.dimens].
 *
 * @see NavigationButtonDefaults for default values
 */
@Composable
fun NavigationButton(
    state: NavigationButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: NavigationButtonDefaults.NavigationButtonColors = NavigationButtonDefaults.colors(),
    dimens: NavigationButtonDefaults.NavigationButtonDimens = NavigationButtonDefaults.dimens(),
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(dimens.size),
        shape = dimens.shape,
        color = colors.container,
        contentColor = colors.content,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = state.icon,
                contentDescription = state.contentDescription,
                modifier = Modifier.size(dimens.iconSize),
            )
        }
    }
}

/**
 * Default configuration values for [NavigationButton].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object NavigationButtonDefaults {
    /**
     * Color configuration for [NavigationButton].
     *
     * @param container Background color.
     * @param content Icon color.
     */
    data class NavigationButtonColors(
        val container: Color,
        val content: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundTertiary,
        content: Color = System.color.iconBase,
    ) = NavigationButtonColors(
        container = container,
        content = content,
    )

    /**
     * Dimension configuration for [NavigationButton].
     *
     * @param size Button size.
     * @param iconSize Icon size.
     * @param cornerRadius Corner radius.
     * @param shape Button shape.
     */
    data class NavigationButtonDimens(
        val size: Dp,
        val iconSize: Dp,
        val cornerRadius: Dp,
        val shape: Shape,
    )

    @Composable
    fun dimens(
        size: Dp = 40.dp,
        iconSize: Dp = 24.dp,
        cornerRadius: Dp = 12.dp,
        shape: Shape = RoundedCornerShape(cornerRadius),
    ) = NavigationButtonDimens(
        size = size,
        iconSize = iconSize,
        cornerRadius = cornerRadius,
        shape = shape,
    )
}

@Preview
@Composable
private fun NavigationButtonPreview() {
    NavigationButton(
        state = NavigationButtonState(),
        onClick = {}
    )
}
