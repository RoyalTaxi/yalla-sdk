package uz.yalla.design.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System

@Composable
fun themedPainter(image: ThemedImage): Painter {
    val resource = if (System.isDark) image.dark else image.light
    return painterResource(resource)
}
