package uz.yalla.carto.render.google

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import uz.yalla.carto.model.MapAnchor
import android.graphics.Canvas as AndroidCanvas

internal fun anchorOf(anchor: MapAnchor): Offset =
    when (anchor) {
        MapAnchor.CENTER -> Offset(0.5f, 0.5f)
        MapAnchor.BOTTOM -> Offset(0.5f, 1.0f)
        MapAnchor.TOP -> Offset(0.5f, 0.0f)
        MapAnchor.LEFT -> Offset(0.0f, 0.5f)
        MapAnchor.RIGHT -> Offset(1.0f, 0.5f)
    }

internal class IconResolver(
    private val density: Density,
    private val layoutDirection: LayoutDirection,
    private val fallback: (String) -> BitmapDescriptor?
) {
    var icons: Map<String, Painter> = emptyMap()

    private val cache = HashMap<String, IconCacheEntry>()

    fun resolve(key: String?): BitmapDescriptor? {
        if (key.isNullOrEmpty()) return null
        val painter = icons[key]
        cache[key]
            ?.takeIf { it.painter === painter }
            ?.let { return it.descriptor }
        val descriptor = painter?.rasterize(density, layoutDirection) ?: fallback(key)
        if (descriptor == null) {
            cache.remove(key)
        } else {
            cache[key] = IconCacheEntry(painter, descriptor)
        }
        return descriptor
    }
}

private data class IconCacheEntry(
    val painter: Painter?,
    val descriptor: BitmapDescriptor
)

@Composable
internal fun rememberIconResolver(icons: Map<String, Painter>): (String?) -> BitmapDescriptor? {
    val context = LocalContext.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val resolver =
        remember(context, density, layoutDirection) {
            IconResolver(density, layoutDirection) { drawableDescriptor(context, it) }
        }
    resolver.icons = icons
    return resolver::resolve
}

private fun drawableDescriptor(
    context: Context,
    key: String
): BitmapDescriptor? {
    val resId = context.resources.getIdentifier(key, "drawable", context.packageName)
    return if (resId == 0) null else BitmapDescriptorFactory.fromResource(resId)
}

private fun Painter.rasterize(
    density: Density,
    layoutDirection: LayoutDirection
): BitmapDescriptor {
    val width = intrinsicSize.width.orDefault().toInt()
    val height = intrinsicSize.height.orDefault().toInt()
    val picture = Picture()
    val recordingCanvas = picture.beginRecording(width, height)
    CanvasDrawScope().draw(density, layoutDirection, Canvas(recordingCanvas), Size(width.toFloat(), height.toFloat())) {
        with(this@rasterize) { draw(size) }
    }
    picture.endRecording()
    val bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Bitmap.createBitmap(picture).copy(Bitmap.Config.ARGB_8888, false)
        } else {
            val fallback = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            AndroidCanvas(fallback).drawPicture(picture)
            fallback
        }
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

private fun Float.orDefault(): Float = if (isFinite() && this > 0f) this else 96f
