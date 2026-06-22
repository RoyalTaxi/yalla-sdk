package uz.yalla.design.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.LocalIsDark

internal fun ThemedImage.resourceFor(isDark: Boolean): DrawableResource = if (isDark) dark else light

@Composable
public fun rememberThemedPainter(image: ThemedImage): Painter = painterResource(image.resourceFor(LocalIsDark.current))
