package uz.yalla.design.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.LocalIsDark

/**
 * The light/dark resource selection for [this] image — the pure, testable core of
 * [rememberThemedPainter].
 *
 * @param isDark whether the dark appearance is active.
 * @return [ThemedImage.dark] when [isDark] is true, otherwise [ThemedImage.light].
 */
internal fun ThemedImage.resourceFor(isDark: Boolean): DrawableResource = if (isDark) dark else light

/**
 * Resolves a [ThemedImage] to the [Painter] for the current appearance ([uz.yalla.design.theme.System.isDark]).
 * Use inside a [uz.yalla.design.theme.YallaTheme].
 */
@Composable
public fun rememberThemedPainter(image: ThemedImage): Painter = painterResource(image.resourceFor(LocalIsDark.current))
