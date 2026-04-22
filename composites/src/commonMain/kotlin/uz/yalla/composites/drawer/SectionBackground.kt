package uz.yalla.composites.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [SectionBackground].
 *
 * @param background Background color of the section.
 * @since 0.0.1
 */
@Immutable
data class SectionBackgroundColors(val background: Color)

/**
 * Dimension configuration for [SectionBackground].
 *
 * @param shape Shape of the section container.
 * @since 0.0.1
 */
@Immutable
data class SectionBackgroundDimens(val shape: Shape)

/**
 * Container with rounded corners and secondary background.
 *
 * ## Usage
 *
 * ```kotlin
 * SectionBackground {
 *     Navigable(title = { Text("Option 1") }, onClick = { })
 *     Navigable(title = { Text("Option 2") }, onClick = { })
 * }
 * ```
 *
 * @param modifier Applied to the container
 * @param colors Color configuration, defaults to [SectionBackgroundDefaults.colors]
 * @param dimens Dimension configuration, defaults to [SectionBackgroundDefaults.dimens]
 * @param content Column content
 * @since 0.0.1
 */
@Composable
fun SectionBackground(
    modifier: Modifier = Modifier,
    colors: SectionBackgroundColors = SectionBackgroundDefaults.colors(),
    dimens: SectionBackgroundDimens = SectionBackgroundDefaults.dimens(),
    content: @Composable ColumnScope.() -> Unit,
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

/**
 * Default configuration values for [SectionBackground].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 * @since 0.0.1
 */
object SectionBackgroundDefaults {
    /** Creates theme-aware default colors. */
    @Composable
    fun colors(background: Color = System.color.background.secondary): SectionBackgroundColors =
        SectionBackgroundColors(background = background)

    /** Creates default dimensions. */
    fun dimens(shape: Shape = RoundedCornerShape(16.dp)): SectionBackgroundDimens =
        SectionBackgroundDimens(shape = shape)
}
