package uz.yalla.design.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.LocalIsDark

@Composable
public fun themedPainter(image: ThemedImage): Painter {
    val resource = if (LocalIsDark.current) image.dark else image.light
    return painterResource(resource)
}
