package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.imageResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun rememberBitmapDescriptor(resource: DrawableResource): BitmapDescriptor {
    val imageBitmap: ImageBitmap = imageResource(resource)
    return remember(imageBitmap) { imageBitmap.toBitmapDescriptor() }
}

internal expect fun ImageBitmap.toBitmapDescriptor(): BitmapDescriptor
