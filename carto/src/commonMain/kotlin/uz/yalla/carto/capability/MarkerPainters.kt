package uz.yalla.carto.capability

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb

public const val USER_LOCATION_DOT_ICON_KEY: String = "yalla-icon-user-location"

public fun userLocationDotPainter(
    primary: Color,
    secondary: Color
): Painter =
    object : Painter() {
        override val intrinsicSize: Size = Size(48f, 48f)

        override fun DrawScope.onDraw() {
            val radius = (size.minDimension - 2f) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                brush = Brush.verticalGradient(listOf(primary, secondary)),
                radius = radius,
                center = center
            )
            drawCircle(
                brush = Brush.verticalGradient(listOf(secondary, primary)),
                radius = radius,
                center = center,
                style = Stroke(width = 2f)
            )
        }
    }

public fun routeDotPainter(
    fill: Color,
    centerFill: Color = Color.White
): Painter =
    object : Painter() {
        override val intrinsicSize: Size = Size(56f, 56f)

        override fun DrawScope.onDraw() {
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                color = fill,
                radius = size.minDimension / 2f,
                center = center
            )
            drawCircle(
                color = centerFill,
                radius = size.minDimension / 2f - 15f,
                center = center
            )
        }
    }

public fun userLocationRingFillArgb(color: Color): Int = color.copy(alpha = 0.2f).toArgb()

public fun userLocationRingStrokeArgb(color: Color): Int = color.copy(alpha = 0.4f).toArgb()
