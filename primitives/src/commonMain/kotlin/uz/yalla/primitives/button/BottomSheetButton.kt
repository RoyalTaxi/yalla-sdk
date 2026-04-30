package uz.yalla.primitives.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.primitives.button.BottomSheetButtonDefaults.colors
import uz.yalla.primitives.button.BottomSheetButtonDefaults.dimens

/**
 * Color configuration for [BottomSheetButton].
 *
 * Use [BottomSheetButtonDefaults.colors] to create with theme-aware defaults.
 *
 * @param containerColor Background color.
 * @param contentColor Text and icon color.
 * @param iconColor Icon tint color. Use [Color.Unspecified] for original painter colors.
 */
@Immutable
data class BottomSheetButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val iconColor: Color,
)

/**
 * Dimension configuration for [BottomSheetButton].
 *
 * Use [BottomSheetButtonDefaults.dimens] to create with standard values.
 *
 * @param minHeight Minimum button height (touch target).
 * @param contentPadding Padding between container and content.
 * @param shape Container shape.
 * @param iconSpacing Space between icon and content.
 */
@Immutable
data class BottomSheetButtonDimens(
    val minHeight: Dp,
    val contentPadding: PaddingValues,
    val shape: Shape,
    val iconSpacing: Dp,
)

/**
 * Button for bottom sheet actions with a leading icon and text.
 *
 * Delegates to [ButtonLayout] for consistent Container -> Provider -> Layout -> Content structure.
 *
 * ## Usage
 * ```kotlin
 * BottomSheetButton(
 *     onClick = { callDriver() },
 *     icon = painterResource(Res.drawable.ic_phone),
 * ) {
 *     Text("Call Driver")
 * }
 * ```
 *
 * ## In a Row
 * ```kotlin
 * Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
 *     BottomSheetButton(
 *         onClick = { callDriver() },
 *         icon = painterResource(Res.drawable.ic_phone),
 *         modifier = Modifier.weight(1f),
 *     ) {
 *         Text("Call")
 *     }
 *     BottomSheetButton(
 *         onClick = { sendMessage() },
 *         icon = painterResource(Res.drawable.ic_chat),
 *         modifier = Modifier.weight(1f),
 *     ) {
 *         Text("Message")
 *     }
 * }
 * ```
 *
 * @param onClick Called when this button is clicked. Not called when disabled.
 * @param icon [Painter] for the leading icon.
 * @param modifier [Modifier] applied to the root container.
 * @param enabled Controls the enabled state. When `false`, the button does not respond to input.
 * @param colors [BottomSheetButtonColors] that define container, content, and icon colors.
 *   See [BottomSheetButtonDefaults.colors].
 * @param dimens [BottomSheetButtonDimens] that define dimensions and shape.
 *   See [BottomSheetButtonDefaults.dimens].
 * @param content The button content, typically a [Text].
 *
 * @see BottomSheetButtonDefaults
 */
@Composable
fun BottomSheetButton(
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: BottomSheetButtonColors = colors(),
    dimens: BottomSheetButtonDimens = dimens(),
    content: @Composable RowScope.() -> Unit,
) {
    ButtonLayout(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        loading = false,
        shape = dimens.shape,
        containerColor = colors.containerColor,
        contentColor = colors.contentColor,
        contentPadding = dimens.contentPadding,
        minHeight = dimens.minHeight,
        iconSpacing = dimens.iconSpacing,
        leadingIcon = {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = colors.iconColor,
            )
        },
        content = content,
    )
}

/**
 * Default configuration values for [BottomSheetButton].
 *
 * Provides theme-aware [colors] and standard [dimens] that can be individually overridden.
 */
object BottomSheetButtonDefaults {
    /** Default minimum button height. */
    val MinHeight = 60.dp

    /** Default content padding. */
    val ContentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 16.dp)

    /** Default button shape. */
    val Shape: Shape = RoundedCornerShape(16.dp)

    /**
     * Creates [BottomSheetButtonColors] with theme-aware defaults.
     *
     * @param containerColor Background color.
     * @param contentColor Text color.
     * @param iconColor Icon tint color. Use [Color.Unspecified] for original painter colors.
     */
    @Composable
    fun colors(
        containerColor: Color = System.color.button.tertiary,
        contentColor: Color = System.color.background.base,
        iconColor: Color = Color.Unspecified,
    ): BottomSheetButtonColors = BottomSheetButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        iconColor = iconColor,
    )

    /**
     * Creates [BottomSheetButtonDimens] with standard values.
     *
     * @param minHeight Minimum button height.
     * @param contentPadding Padding between container and content.
     * @param shape Container shape.
     * @param iconSpacing Space between icon and content.
     */
    fun dimens(
        minHeight: Dp = MinHeight,
        contentPadding: PaddingValues = ContentPadding,
        shape: Shape = Shape,
        iconSpacing: Dp = 8.dp,
    ): BottomSheetButtonDimens = BottomSheetButtonDimens(
        minHeight = minHeight,
        contentPadding = contentPadding,
        shape = shape,
        iconSpacing = iconSpacing,
    )
}
