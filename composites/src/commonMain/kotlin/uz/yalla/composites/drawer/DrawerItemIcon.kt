package uz.yalla.composites.drawer

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Default configuration values for [DrawerItemIcon].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 * @since 0.0.1
 */
object DrawerItemIconDefaults {
    /**
     * Color configuration for [DrawerItemIcon].
     *
     * @param tint Icon tint color.
     * @since 0.0.1
     */
    data class DrawerItemIconColors(val tint: Color)

    /**
     * Creates theme-aware default colors.
     *
     * @since 0.0.1
     */
    @Composable
    fun colors(tint: Color = System.color.icon.base) =
        DrawerItemIconColors(
            tint = tint
        )

    /**
     * Dimension configuration for [DrawerItemIcon].
     *
     * @param padding Padding around the icon.
     * @since 0.0.1
     */
    data class DrawerItemIconDimens(val padding: Dp)

    /**
     * Creates default dimensions.
     *
     * @since 0.0.1
     */
    @Composable
    fun dimens(padding: Dp = 10.dp) =
        DrawerItemIconDimens(
            padding = padding
        )
}

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
 * @since 0.0.1
 */
@Composable
fun DrawerItemIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    colors: DrawerItemIconDefaults.DrawerItemIconColors = DrawerItemIconDefaults.colors(),
    dimens: DrawerItemIconDefaults.DrawerItemIconDimens = DrawerItemIconDefaults.dimens()
) = Icon(
    painter = painter,
    tint = colors.tint,
    contentDescription = null,
    modifier = modifier.padding(dimens.padding).size(24.dp)
)
