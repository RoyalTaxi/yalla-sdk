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
 * Modifier that constrains content to a square based on available space.
 *
 * ## Usage
 *
 * ```kotlin
 * Box(
 *     modifier = Modifier
 *         .fillMaxWidth()
 *         .squareSize()
 * ) {
 *     // Content will be square
 * }
 * ```
 *
 * @param position Alignment position within the square (0.0 = start, 0.5 = center, 1.0 = end)
 */
@Stable
fun Modifier.squareSize(position: Float = 0.5f): Modifier =
    this.then(
        when {
            position == 0.5f -> this.createSquareSizeModifier(0.5f)
            else -> this.createSquareSizeModifier(position)
        }
    )

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

private fun Modifier.createSquareSizeModifier(position: Float): Modifier = then(SquareSize(position = position))
