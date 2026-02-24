package uz.yalla.primitives.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [GradientButton] component.
 *
 * @property enabled When false, button is disabled.
 */
data class GradientButtonState(
    val enabled: Boolean = true,
)

/**
 * Button with gradient background support.
 *
 * A customizable button that can display either a solid color
 * or a gradient brush background.
 *
 * ## Usage
 *
 * ```kotlin
 * // Solid color button
 * GradientButton(
 *     state = GradientButtonState(enabled = isValid),
 *     onClick = { submit() },
 * ) {
 *     Text("Submit Order")
 * }
 *
 * // Gradient button
 * GradientButton(
 *     state = GradientButtonState(),
 *     onClick = { submit() },
 *     colors = GradientButtonDefaults.colors(
 *         containerBrush = Brush.horizontalGradient(listOf(Color.Blue, Color.Cyan))
 *     ),
 * ) {
 *     Text("Submit Order")
 * }
 * ```
 *
 * @param state Button state containing enabled.
 * @param onClick Called when button is clicked.
 * @param modifier Modifier for the button.
 * @param colors Color configuration, defaults to [GradientButtonDefaults.colors].
 * @param dimens Dimension configuration, defaults to [GradientButtonDefaults.dimens].
 * @param content Button content.
 *
 * @see GradientButtonState for state configuration
 * @see GradientButtonDefaults for default values
 */
@Composable
fun GradientButton(
    state: GradientButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: GradientButtonDefaults.GradientButtonColors = GradientButtonDefaults.colors(),
    dimens: GradientButtonDefaults.GradientButtonDimens = GradientButtonDefaults.dimens(),
    content: @Composable () -> Unit
) {
    val alpha = if (state.enabled) 1f else dimens.disabledAlpha

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .alpha(alpha)
                .clip(dimens.shape)
                .background(brush = colors.containerBrush, shape = dimens.shape)
                .clickable(
                    enabled = state.enabled,
                    role = Role.Button,
                    onClick = onClick
                ).padding(dimens.contentPadding)
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.content) {
            content()
        }
    }
}

/**
 * Default configuration values for [GradientButton].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object GradientButtonDefaults {
    /**
     * Color configuration for [GradientButton].
     *
     * @param containerBrush Background brush (can be solid or gradient).
     * @param content Content (text/icon) color.
     */
    data class GradientButtonColors(
        val containerBrush: Brush,
        val content: Color,
    )

    /**
     * Creates color configuration for [GradientButton].
     *
     * @param containerBrush Background brush (can be solid or gradient).
     * @param content Content (text/icon) color.
     */
    @Composable
    fun colors(
        containerBrush: Brush =
            Brush.linearGradient(
                colors =
                    listOf(
                        System.color.backgroundBrandBase,
                        System.color.backgroundBrandBase,
                    )
            ),
        content: Color = System.color.textWhite,
    ) = GradientButtonColors(
        containerBrush = containerBrush,
        content = content,
    )

    /**
     * Creates color configuration with solid container color.
     *
     * @param containerColor Solid background color.
     * @param content Content (text/icon) color.
     */
    @Composable
    fun colors(
        containerColor: Color,
        content: Color = System.color.textWhite,
    ) = GradientButtonColors(
        containerBrush = Brush.linearGradient(listOf(containerColor, containerColor)),
        content = content,
    )

    /**
     * Dimension configuration for [GradientButton].
     *
     * @param shape Button shape.
     * @param contentPadding Padding inside the button.
     * @param disabledAlpha Alpha value for disabled state.
     */
    data class GradientButtonDimens(
        val shape: Shape,
        val contentPadding: PaddingValues,
        val disabledAlpha: Float,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(20.dp),
        disabledAlpha: Float = 0.5f,
    ) = GradientButtonDimens(
        shape = shape,
        contentPadding = contentPadding,
        disabledAlpha = disabledAlpha,
    )
}
