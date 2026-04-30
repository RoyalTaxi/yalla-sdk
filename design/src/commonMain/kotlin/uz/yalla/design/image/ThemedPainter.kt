package uz.yalla.design.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System

/**
 * Returns a [Painter] for the given [ThemedImage], automatically selecting the
 * light or dark drawable variant based on [System.isDark].
 *
 * Recomposes when the theme toggles because [System.isDark] reads
 * [LocalIsDark][uz.yalla.design.theme.LocalIsDark], a snapshot value provided by
 * [YallaTheme][uz.yalla.design.theme.YallaTheme]. Prefer this over calling
 * `painterResource(image.light)` or `painterResource(image.dark)` directly —
 * those calls hardcode a variant and won't react to runtime theme changes.
 *
 * ## Usage
 *
 * ```kotlin
 * Image(
 *     painter = themedPainter(ThemedImage.MapPin),
 *     contentDescription = "Location",
 * )
 * ```
 *
 * @param image The themed image entry to resolve.
 * @return [Painter] for the theme-appropriate drawable resource.
 */
@Composable
fun themedPainter(image: ThemedImage): Painter {
    val resource = if (System.isDark) image.dark else image.light
    return painterResource(resource)
}
