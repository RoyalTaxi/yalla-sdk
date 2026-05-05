package uz.yalla.primitives.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Vertical stick connecting a [LocationPin]'s body to the ground. Clipped to
 * [clipHeight] so it appears to retract during the pin's jump animation.
 */
@Composable
internal fun PinStick(
    clipHeight: Dp,
    colors: LocationPinColors,
    dimens: LocationPinDimens,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier =
            modifier
                .width(dimens.stickWidth)
                .height(dimens.stickHeight)
    ) {
        Box(
            modifier =
                Modifier
                    .width(dimens.stickWidth)
                    .height(clipHeight)
                    .clipToBounds()
                    .background(
                        shape = CircleShape,
                        color = colors.stick
                    ).border(
                        width = 1.dp,
                        shape = CircleShape,
                        color = colors.stickBorder
                    )
        )
    }
}
