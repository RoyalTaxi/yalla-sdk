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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import uz.yalla.design.theme.System
import uz.yalla.platform.model.IconType
import uz.yalla.platform.toImageVector

@Composable
fun SheetIconButton(
    iconType: IconType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    containerColor: Color = System.color.background.secondary,
    contentColor: Color = System.color.icon.base,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(8.dp),
        border = border,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
        modifier =
            modifier
                .size(48.dp)
                .padding(4.dp)
    ) {
        Image(
            painter = rememberVectorPainter(iconType.toImageVector()),
            contentDescription = null,
            colorFilter = ColorFilter.tint(contentColor),
        )
    }
}
