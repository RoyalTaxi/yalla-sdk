package uz.yalla.design.radius

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Corner-radius scheme for the Yalla design system.
 *
 * A flat t-shirt scale of rounded-corner values used across primitives and
 * composites. Unlike [SpaceScheme][uz.yalla.design.space.SpaceScheme], radii
 * don't need a separate semantic layer — the size label itself carries intent
 * (`s` for chips, `l` for buttons, `sheet` for bottom-sheet top corners).
 *
 * Access via `System.radius` inside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme].
 *
 * ## Usage
 *
 * ```kotlin
 * Surface(shape = RoundedCornerShape(System.radius.l)) { … }
 *
 * // Bottom-sheet top corner:
 * BottomSheetScaffold(
 *     sheetShape = RoundedCornerShape(
 *         topStart = System.radius.sheet,
 *         topEnd = System.radius.sheet,
 *     ),
 *     // …
 * )
 * ```
 *
 * @property xs 4.dp — input fields, tight elements.
 * @property s 8.dp — chips, pills, small badges.
 * @property m 12.dp — cards, list items.
 * @property l 16.dp — buttons, selectable items (the de-facto standard).
 * @property xl 24.dp — large feature cards.
 * @property sheet 40.dp — bottom-sheet top corner.
 * @since 0.0.13
 */
data class RadiusScheme(
    val xs: Dp,
    val s: Dp,
    val m: Dp,
    val l: Dp,
    val xl: Dp,
    val sheet: Dp,
)

/**
 * Standard Yalla [RadiusScheme] with t-shirt sizes mapped to conventional values.
 *
 * Override at the theme level when white-labeling or experimenting with a
 * sharper/softer aesthetic.
 *
 * @since 0.0.13
 */
fun standardRadiusScheme(): RadiusScheme = RadiusScheme(
    xs = 4.dp,
    s = 8.dp,
    m = 12.dp,
    l = 16.dp,
    xl = 24.dp,
    sheet = 40.dp,
)

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] for providing
 * a [RadiusScheme] down the composable tree.
 *
 * Defaults to [standardRadiusScheme] so tokens resolve even outside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme].
 *
 * @since 0.0.13
 */
val LocalRadiusScheme = staticCompositionLocalOf { standardRadiusScheme() }
