package uz.yalla.composites.drawer

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [DrawerItemIcon].
 */
@Immutable
data class DrawerItemIconColors(
    val tint: Color
)

/**
 * Dimension configuration for [DrawerItemIcon].
 */
@Immutable
data class DrawerItemIconDimens(
    val padding: Dp
)

/**
 * Icon wrapper for drawer/menu items with standard padding.
 *
 * ## Usage
 *
 * ```kotlin
 * DrawerItemIcon(
 *     painter = painterResource(Res.drawable.ic_settings),
 *     colors = DrawerItemIconDefaults.colors(tint = System.color.icon.base)
 * )
 * ```
 *
 * @param painter Icon painter
 * @param modifier Applied to icon
 * @param colors Color configuration, defaults to [DrawerItemIconDefaults.colors]
 * @param dimens Dimension configuration, defaults to [DrawerItemIconDefaults.dimens]
 */
@Composable
fun DrawerItemIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    colors: DrawerItemIconColors = DrawerItemIconDefaults.colors(),
    dimens: DrawerItemIconDimens = DrawerItemIconDefaults.dimens()
) = Icon(
    painter = painter,
    tint = colors.tint,
    contentDescription = null,
    modifier = modifier.padding(dimens.padding).size(24.dp)
)

/**
 * Default configuration values for [DrawerItemIcon].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object DrawerItemIconDefaults {
    /** Creates theme-aware default colors. */
    @Composable
    fun colors(tint: Color = System.color.icon.base): DrawerItemIconColors = DrawerItemIconColors(tint = tint)

    /** Creates default dimensions. */
    fun dimens(padding: Dp = 10.dp): DrawerItemIconDimens = DrawerItemIconDimens(padding = padding)
}
