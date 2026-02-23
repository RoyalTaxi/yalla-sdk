package uz.yalla.platform.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.platform.model.IconType
import uz.yalla.platform.toDrawableResource

@Suppress("UNUSED_PARAMETER")
@Composable
actual fun NativeSquircleIconButton(
    iconType: IconType,
    onClick: () -> Unit,
    modifier: Modifier,
    border: BorderStroke?,
    background: Color
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(8.dp),
        border = border,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = System.color.backgroundSecondary,
                contentColor = System.color.iconBase
            ),
        modifier =
            modifier
                .size(48.dp)
                .padding(4.dp)
    ) {
        Image(
            painter = painterResource(iconType.toDrawableResource()),
            contentDescription = null,
            colorFilter = ColorFilter.tint(System.color.iconBase)
        )
    }
}
