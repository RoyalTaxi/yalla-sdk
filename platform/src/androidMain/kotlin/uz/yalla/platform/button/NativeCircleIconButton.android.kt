package uz.yalla.platform.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.platform.model.IconType
import uz.yalla.platform.toDrawableResource

@Suppress("UNUSED_PARAMETER")
@Composable
actual fun NativeCircleIconButton(
    iconType: IconType,
    onClick: () -> Unit,
    modifier: Modifier,
    alpha: Float,
    border: BorderStroke?,
    background: Color
) {
    IconButton(
        onClick = onClick,
        modifier =
            modifier
                .size(48.dp)
                .graphicsLayer { this.alpha = alpha }
                .then(if (border != null) Modifier.border(border, CircleShape) else Modifier),
        colors =
            IconButtonDefaults.iconButtonColors(
                contentColor = System.color.iconBase,
                containerColor = System.color.backgroundBase
            )
    ) {
        Image(
            painter = painterResource(iconType.toDrawableResource()),
            contentDescription = null,
            colorFilter = ColorFilter.tint(System.color.iconBase)
        )
    }
}
