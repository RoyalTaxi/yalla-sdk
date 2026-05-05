package uz.yalla.design.stroke

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Stroke-width scheme for the Yalla design system.
 *
 * The flat scale of line widths used for borders, dividers, and focus rings.
 * Replaces the ad-hoc `1.dp` / `1.5.dp` / `2.dp` literals scattered around
 * primitives (and the `magic-dp-allow` divider escape hatches in the client).
 *
 * Like [RadiusScheme][uz.yalla.design.radius.RadiusScheme], the name carries
 * intent — `hairline` for default chrome, `regular` for emphasized borders,
 * `focus` for the keyboard/IME focus ring, `selected` for the active card or
 * chip outline. Pick by role, never by raw value.
 *
 * Access via `System.stroke` inside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme].
 *
 * ## Usage
 *
 * ```kotlin
 * Box(
 *     Modifier.border(
 *         width = System.stroke.hairline,
 *         color = System.color.border.base,
 *     ),
 * )
 *
 * // Focus ring on a text field:
 * OutlinedTextField(
 *     // …
 *     colors = TextFieldDefaults.colors(
 *         focusedIndicatorColor = System.color.border.focus,
 *     ),
 *     // width via Modifier.border(System.stroke.focus, …) when custom-drawn.
 * )
 * ```
 *
 * @property hairline 1.dp — default border / divider.
 * @property regular 1.5.dp — emphasized border.
 * @property focus 2.dp — focus ring on text fields.
 * @property selected 2.dp — selected card / chip border.
 */
@Immutable
data class StrokeScheme(
    val hairline: Dp,
    val regular: Dp,
    val focus: Dp,
    val selected: Dp
)

/**
 * Standard Yalla [StrokeScheme] with the four canonical line widths.
 *
 * Override at the theme level when white-labeling for a heavier/lighter
 * stroke aesthetic.
 */
fun standardStrokeScheme(): StrokeScheme =
    StrokeScheme(
        hairline = 1.dp,
        regular = 1.5.dp,
        focus = 2.dp,
        selected = 2.dp
    )

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] for providing
 * a [StrokeScheme] down the composable tree.
 *
 * Defaults to [standardStrokeScheme] so tokens resolve even outside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme].
 */
val LocalStrokeScheme = staticCompositionLocalOf { standardStrokeScheme() }
