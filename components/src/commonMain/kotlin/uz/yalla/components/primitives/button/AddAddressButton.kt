package uz.yalla.components.primitives.button

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.resources.icons.Add
import uz.yalla.resources.icons.YallaIcons

@Composable
public fun AddAddressButton(
    onClick: () -> Unit,
    layersCount: Int = 5,
    paddingBetween: Dp = 20.dp,
    modifier: Modifier = Modifier,
    colorStops: Array<Pair<Float, Color>> =
        arrayOf(
            0.0f to System.color.background.secondary,
            0.75f to System.color.background.secondary,
            1.0f to System.color.background.tertiary
        )
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(containerColor = System.color.button.active),
        modifier =
            modifier.waveBackground(
                layersCount = layersCount,
                step = paddingBetween,
                colorStops = colorStops
            )
    ) {
        Icon(
            imageVector = YallaIcons.Add,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = System.color.icon.white
        )
    }
}

private fun Modifier.waveBackground(
    layersCount: Int,
    step: Dp,
    colorStops: Array<out Pair<Float, Color>>
): Modifier =
    this.then(
        Modifier.drawWithCache {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 2f
            val stepPx = step.toPx()

            onDrawBehind {
                for (i in (layersCount - 1) downTo 0) {
                    val r = baseRadius + (i + 1) * stepPx
                    val brush =
                        Brush.radialGradient(
                            colorStops = colorStops,
                            center = center,
                            radius = r
                        )
                    drawCircle(
                        brush = brush,
                        radius = r,
                        center = center
                    )
                }
            }
        }
    )
