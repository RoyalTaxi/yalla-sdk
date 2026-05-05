package uz.yalla.design.elevation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Elevation scheme for the Yalla design system.
 *
 * A neutral, Material 3-aligned tier ladder of `Dp` values used for both
 * shadow elevation and tonal-elevation surfaces. The scheme is intentionally a
 * single abstraction (no separate "shadow" vs "tonal" axes) so consumers pick
 * a tier by *intent* rather than by rendering mechanism.
 *
 * Like [RadiusScheme][uz.yalla.design.radius.RadiusScheme], the level label
 * carries intent — `level0` for resting surfaces, `level3` for sheets and
 * top-app-bars when scrolled, `level5` for FAB and overlay layers. Reach for
 * the tier that matches the *role* of the surface, never a raw `.dp`.
 *
 * Access via `System.elevation` inside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme].
 *
 * ## Usage
 *
 * ```kotlin
 * Surface(
 *     shadowElevation = System.elevation.level2,
 *     tonalElevation = System.elevation.level1,
 * ) { … }
 *
 * // Bottom-sheet / scrolled top-app-bar:
 * ModalBottomSheet(
 *     tonalElevation = System.elevation.level3,
 * ) { … }
 * ```
 *
 * @property level0 0.dp — resting surface (no elevation).
 * @property level1 1.dp — cards at rest.
 * @property level2 3.dp — elevated cards (hover/selected).
 * @property level3 6.dp — bottom sheets, top app bars when scrolled.
 * @property level4 8.dp — modals, dialogs.
 * @property level5 12.dp — overlays, FAB.
 */
@Immutable
data class ElevationScheme(
    val level0: Dp,
    val level1: Dp,
    val level2: Dp,
    val level3: Dp,
    val level4: Dp,
    val level5: Dp
)

/**
 * Standard Yalla [ElevationScheme] mapping the six Material 3 tiers to their
 * canonical `Dp` values.
 *
 * Override at the theme level when white-labeling for a flatter/sharper look.
 */
fun standardElevationScheme(): ElevationScheme =
    ElevationScheme(
        level0 = 0.dp,
        level1 = 1.dp,
        level2 = 3.dp,
        level3 = 6.dp,
        level4 = 8.dp,
        level5 = 12.dp
    )

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] for providing
 * an [ElevationScheme] down the composable tree.
 *
 * Defaults to [standardElevationScheme] so tokens resolve even outside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme].
 */
val LocalElevationScheme = staticCompositionLocalOf { standardElevationScheme() }
