package uz.yalla.carto.render.google

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import uz.yalla.carto.model.CartoCircle
import uz.yalla.carto.model.CartoMarker
import uz.yalla.carto.model.CartoRoute
import uz.yalla.carto.model.LinePattern
import uz.yalla.carto.model.MapAnchor
import uz.yalla.carto.model.ZBand
import uz.yalla.carto.state.MarkerPose
import kotlin.math.roundToInt

internal fun ZBand.zIndex(): Float = ordinal.toFloat() * 1000f

private fun anchorU(anchor: MapAnchor): Float =
    when (anchor) {
        MapAnchor.LEFT -> 0.0f
        MapAnchor.RIGHT -> 1.0f
        else -> 0.5f
    }

private fun anchorV(anchor: MapAnchor): Float =
    when (anchor) {
        MapAnchor.TOP -> 0.0f
        MapAnchor.BOTTOM -> 1.0f
        else -> 0.5f
    }

internal fun CartoMarker.toGoogleIosMarker(): GoogleIosMarker =
    GoogleIosMarker(id, point.lat, point.lng, iconKey, anchorU(anchor), anchorV(anchor), rotation, flat, zBand.zIndex() + zIndex)

internal fun MarkerPose.toGoogleIosPose(): GoogleIosPose =
    GoogleIosPose(id, point.lat, point.lng, bearing, iconKey, anchorU(anchor), anchorV(anchor))

internal fun CartoRoute.toGoogleIosRoute(): GoogleIosRoute =
    GoogleIosRoute(id, points, colorArgb, widthDp, pattern == LinePattern.DASHED, zBand.zIndex())

internal fun CartoCircle.toGoogleIosCircle(): GoogleIosCircle =
    GoogleIosCircle(id, center.lat, center.lng, radiusMeters, fillArgb, strokeArgb, strokeWidthDp, zBand.zIndex())

internal fun rasterizeIcons(
    icons: Map<String, Painter>,
    density: Density,
    layoutDirection: LayoutDirection
): Map<String, UIImage> =
    buildMap {
        icons.forEach { (key, painter) ->
            painter.toUIImage(density, layoutDirection)?.let { put(key, it) }
        }
    }

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun Painter.toUIImage(
    density: Density,
    layoutDirection: LayoutDirection
): UIImage? {
    val width = intrinsicSize.width.orDefault().roundToInt()
    val height = intrinsicSize.height.orDefault().roundToInt()
    val bitmap = ImageBitmap(width, height)
    CanvasDrawScope().draw(density, layoutDirection, Canvas(bitmap), Size(width.toFloat(), height.toFloat())) {
        with(this@toUIImage) { draw(size) }
    }
    val bytes = Image.makeFromBitmap(bitmap.asSkiaBitmap()).encodeToData(EncodedImageFormat.PNG)?.bytes ?: return null
    if (bytes.isEmpty()) return null
    val data = memScoped { NSData.create(bytes = allocArrayOf(bytes), length = bytes.size.toULong()) }
    return UIImage(data = data, scale = density.density.toDouble())
}

private fun Float.orDefault(): Float = if (isFinite() && this > 0f) this else 96f
