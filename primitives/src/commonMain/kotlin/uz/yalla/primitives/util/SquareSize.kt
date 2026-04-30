package uz.yalla.primitives.util

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import kotlin.math.max
import kotlin.math.min

/**
 * Constrains the content to a square whose side is `min(maxWidth, maxHeight)` of the
 * incoming constraints. If `minWidth > maxHeight` or `minHeight > maxWidth` (an
 * overconstrained layout), the modifier falls back to measuring without modification.
 *
 * [position] controls where the content is placed within the square when it is smaller
 * than the resolved size: 0.0 = start/top, 0.5 = centered (default), 1.0 = end/bottom.
 */
@Stable
fun Modifier.squareSize(position: Float = 0.5f): Modifier =
    this.then(SquareSize(position = position))

private class SquareSize(
    private val position: Float
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val maxSquare = min(constraints.maxWidth, constraints.maxHeight)
        val minSquare = max(constraints.minWidth, constraints.minHeight)
        val squareExists = (minSquare <= maxSquare)

        val resolvedConstraints =
            constraints
                .takeUnless { squareExists }
                ?: constraints.copy(maxWidth = maxSquare, maxHeight = maxSquare)

        val placeable = measurable.measure(resolvedConstraints)

        return if (squareExists) {
            val size = max(placeable.width, placeable.height)
            layout(size, size) {
                val x = ((size - placeable.width) * position).toInt()
                val y = ((size - placeable.height) * position).toInt()
                placeable.placeRelative(x, y)
            }
        } else {
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SquareSize) return false
        return position == other.position
    }

    override fun hashCode(): Int = position.hashCode()
}

