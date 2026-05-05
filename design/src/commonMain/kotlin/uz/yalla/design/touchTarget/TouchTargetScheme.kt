package uz.yalla.design.touchTarget

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Touch-target scheme for the Yalla design system.
 *
 * Accessibility-driven minimum tap-surface sizes. Unlike pure spacing, these
 * values exist to satisfy a hard contract — Android Material requires 48.dp,
 * iOS HIG asks for 44pt; both are covered by [min]. [compact] is an escape
 * hatch for genuinely tight UI areas (toolbar icon clusters, chip rows) where
 * 48.dp is impractical, and should be used sparingly with a justifying
 * comment.
 *
 * Like [RadiusScheme][uz.yalla.design.radius.RadiusScheme], the name carries
 * intent — `min` is the safe default, `compact` is the deliberate exception.
 *
 * Access via `System.touchTarget` inside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme].
 *
 * ## Usage
 *
 * ```kotlin
 * IconButton(
 *     onClick = onClose,
 *     modifier = Modifier.size(System.touchTarget.min),
 * ) { Icon(…) }
 *
 * // Tight chip row — only when 48.dp blows the layout:
 * Chip(
 *     modifier = Modifier.heightIn(min = System.touchTarget.compact),
 * )
 * ```
 *
 * @property min 48.dp — Android Material baseline; also covers iOS 44pt with safe margin.
 * @property compact 40.dp — tight UI areas where [min] is impractical (use sparingly).
 */
@Immutable
data class TouchTargetScheme(
    val min: Dp,
    val compact: Dp
)

/**
 * Standard Yalla [TouchTargetScheme] with platform-safe minimums.
 *
 * Override at the theme level only with strong justification — these values
 * are tied to platform accessibility guidelines, not aesthetic preference.
 */
fun standardTouchTargetScheme(): TouchTargetScheme =
    TouchTargetScheme(
        min = 48.dp,
        compact = 40.dp
    )

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] for providing
 * a [TouchTargetScheme] down the composable tree.
 *
 * Defaults to [standardTouchTargetScheme] so tokens resolve even outside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme].
 */
val LocalTouchTargetScheme = staticCompositionLocalOf { standardTouchTargetScheme() }
