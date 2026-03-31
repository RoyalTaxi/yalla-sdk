package uz.yalla.maps.provider.common

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter

/**
 * Programmatic painter for user location dot.
 * Replaces the old ic_user_location.svg which used circle elements
 * that Valkyrie couldn't convert to ImageVector.
 *
 * SVG spec: 15x15 circle with gradient fill (#3400FF → #886BFF)
 * and gradient stroke (#886BFF → #3400FF, 2px width).
 */
internal object UserLocationPainter : Painter() {

    private const val SIZE = 48f
    private const val STROKE_WIDTH = 2f

    private val gradientStart = Color(0xFF3400FF)
    private val gradientEnd = Color(0xFF886BFF)

    override val intrinsicSize = Size(SIZE, SIZE)

    override fun DrawScope.onDraw() {
        val drawCenter = this.center
        val drawSize = minOf(size.width, size.height)
        val radius = (drawSize - STROKE_WIDTH) / 2

        // Fill gradient: top to bottom #3400FF → #886BFF
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(gradientStart, gradientEnd),
                startY = 0f,
                endY = size.height
            ),
            radius = radius,
            center = drawCenter
        )

        // Stroke gradient: top to bottom #886BFF → #3400FF (reversed)
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(gradientEnd, gradientStart),
                startY = 0f,
                endY = size.height
            ),
            radius = radius,
            center = drawCenter,
            style = Stroke(width = STROKE_WIDTH)
        )
    }
}
