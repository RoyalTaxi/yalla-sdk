package uz.yalla.maps.provider.common

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter

/**
 * Programmatic [Painter] that renders the user-location indicator dot.
 *
 * Draws a filled circle with a vertical gradient from `#3400FF` (top) to `#886BFF` (bottom)
 * and a 2 px border with the reversed gradient. Replaces the former `ic_user_location.svg`
 * which used `<circle>` elements that Valkyrie could not convert to
 * [ImageVector][androidx.compose.ui.graphics.vector.ImageVector].
 *
 * The intrinsic size is 48 x 48 px, but the painter scales to any [DrawScope] size.
 *
 * @since 0.0.1
 * @see uz.yalla.maps.provider.common.MapDimens.UserLocationSize
 */
internal object UserLocationPainter : Painter() {

    /** Canvas dimension in pixels. */
    private const val SIZE = 48f

    /** Border stroke width in pixels. */
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
