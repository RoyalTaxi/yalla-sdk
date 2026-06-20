package uz.yalla.design.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.LocalIsDark

/**
 * The light/dark resource selection for [this] image — the pure, testable core of [themedPainter].
 *
 * @param isDark whether the dark appearance is active.
 * @return [ThemedImage.dark] when [isDark] is true, otherwise [ThemedImage.light].
 */
internal fun ThemedImage.resourceFor(isDark: Boolean): DrawableResource = if (isDark) dark else light

// TODO(quality, needs-decision): rename to `rememberThemedPainter` to match Compose value-composable
//  naming and the file name — deferred because the symbol has external consumers and renaming it is
//  a breaking public-API change that needs owner sign-off.
/**
 * Resolves a [ThemedImage] to the [Painter] for the current appearance ([uz.yalla.design.theme.System.isDark]).
 * Use inside a [uz.yalla.design.theme.YallaTheme].
 */
@Composable
public fun themedPainter(image: ThemedImage): Painter =
    painterResource(image.resourceFor(LocalIsDark.current))
