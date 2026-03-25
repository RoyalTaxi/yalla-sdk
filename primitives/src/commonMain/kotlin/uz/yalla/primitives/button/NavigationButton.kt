package uz.yalla.primitives.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
 * Color configuration for [NavigationButton].
 *
 * Use [NavigationButtonDefaults.colors] to create with theme-aware defaults.
 *
 * @param containerColor Background color.
 * @param contentColor Icon color.
 */
@Immutable
data class NavigationButtonColors(
    val containerColor: Color,
    val contentColor: Color,
)

/**
 * Dimension configuration for [NavigationButton].
 *
 * Use [NavigationButtonDefaults.dimens] to create with standard values.
 *
 * @param size Button size (width and height).
 * @param iconSize Icon size.
 * @param shape Container shape.
 */
@Immutable
data class NavigationButtonDimens(
    val size: Dp,
    val iconSize: Dp,
    val shape: Shape,
)

/**
 * Navigation back button for screen headers.
 *
 * Use in top bars to navigate back to the previous screen.
 * This button renders a single icon (arrow back by default) inside a shaped surface.
 *
 * ## Usage
 * ```kotlin
 * NavigationButton(onClick = onBack)
 * ```
 *
 * ## With Custom Icon
 * ```kotlin
 * NavigationButton(
 *     onClick = onClose,
 *     icon = Icons.Default.Close,
 *     contentDescription = "Close",
 * )
 * ```
 *
 * @param onClick Called when this button is clicked.
 * @param modifier [Modifier] applied to the root container.
 * @param icon [ImageVector] to display. Defaults to back arrow.
 * @param contentDescription Accessibility description. Defaults to "Navigate back".
 * @param colors [NavigationButtonColors] that define container and content colors.
 *   See [NavigationButtonDefaults.colors].
 * @param dimens [NavigationButtonDimens] that define dimensions and shape.
 *   See [NavigationButtonDefaults.dimens].
 *
 * @see NavigationButtonDefaults
 */
@Composable
fun NavigationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    contentDescription: String? = "Navigate back",
    colors: NavigationButtonColors = NavigationButtonDefaults.colors(),
    dimens: NavigationButtonDimens = NavigationButtonDefaults.dimens(),
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(dimens.size),
        shape = dimens.shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(dimens.iconSize),
            )
        }
    }
}

/**
 * Default configuration values for [NavigationButton].
 *
 * Provides theme-aware [colors] and standard [dimens] that can be individually overridden.
 */
object NavigationButtonDefaults {
    /** Default button size. */
    val Size = 40.dp

    /** Default icon size. */
    val IconSize = 24.dp

    /** Default button shape. */
    val Shape: Shape = RoundedCornerShape(12.dp)

    /**
     * Creates [NavigationButtonColors] with theme-aware defaults.
     *
     * @param containerColor Background color.
     * @param contentColor Icon color.
     */
    @Composable
    fun colors(
        containerColor: Color = System.color.background.tertiary,
        contentColor: Color = System.color.icon.base,
    ): NavigationButtonColors = NavigationButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )

    /**
     * Creates [NavigationButtonDimens] with standard values.
     *
     * @param size Button size (width and height).
     * @param iconSize Icon size.
     * @param shape Container shape.
     */
    fun dimens(
        size: Dp = Size,
        iconSize: Dp = IconSize,
        shape: Shape = NavigationButtonDefaults.Shape,
    ): NavigationButtonDimens = NavigationButtonDimens(
        size = size,
        iconSize = iconSize,
        shape = shape,
    )
}

@Preview
@Composable
private fun NavigationButtonPreview() {
    NavigationButton(onClick = {})
}
