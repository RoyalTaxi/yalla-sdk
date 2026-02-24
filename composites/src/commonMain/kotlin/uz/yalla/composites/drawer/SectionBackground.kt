package uz.yalla.composites.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Default configuration values for [SectionBackground].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object SectionBackgroundDefaults {
    /**
     * Color configuration for [SectionBackground].
     *
     * @param background Background color of the section.
     */
    data class SectionBackgroundColors(
        val background: Color
    )

    @Composable
    fun colors(background: Color = System.color.backgroundSecondary) =
        SectionBackgroundColors(
            background = background
        )

    /**
     * Dimension configuration for [SectionBackground].
     *
     * @param shape Shape of the section container.
     * @param cornerRadius Corner radius of the shape.
     */
    data class SectionBackgroundDimens(
        val shape: Shape
    )

    @Composable
    fun dimens(shape: Shape = RoundedCornerShape(16.dp)) =
        SectionBackgroundDimens(
            shape = shape
        )
}

/**
 * Container with rounded corners and secondary background.
 *
 * ## Usage
 *
 * ```kotlin
 * SectionBackground {
 *     Navigable(title = "Option 1", onClick = { })
 *     Navigable(title = "Option 2", onClick = { })
 * }
 * ```
 *
 * @param modifier Applied to the container
 * @param colors Color configuration, defaults to [SectionBackgroundDefaults.colors]
 * @param dimens Dimension configuration, defaults to [SectionBackgroundDefaults.dimens]
 * @param content Column content
 */
@Composable
fun SectionBackground(
    modifier: Modifier = Modifier,
    colors: SectionBackgroundDefaults.SectionBackgroundColors = SectionBackgroundDefaults.colors(),
    dimens: SectionBackgroundDefaults.SectionBackgroundDimens = SectionBackgroundDefaults.dimens(),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        content = content,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(dimens.shape)
                .background(colors.background)
    )
}
