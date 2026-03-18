package uz.yalla.design.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System

/**
 * Returns a [Painter] for the given [ThemedImage], automatically selecting the
 * light or dark drawable variant based on the current theme.
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
 * @since 0.0.1
 */
@Composable
fun themedPainter(image: ThemedImage): Painter {
    val resource = if (System.isDark) image.dark else image.light
    return painterResource(resource)
}
