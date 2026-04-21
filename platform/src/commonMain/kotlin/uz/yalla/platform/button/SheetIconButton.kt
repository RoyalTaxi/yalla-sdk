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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.platform.model.IconType
import uz.yalla.platform.toImageVector

/**
 * Small icon button designed for bottom sheet toolbars.
 *
 * A shared (non-expect) composable that renders a 48 dp rounded-square button
 * using the Yalla design system colors. Typically placed in a row at the top of
 * a [NativeSheet][uz.yalla.platform.sheet.NativeSheet].
 *
 * ## Usage
 * ```kotlin
 * Row {
 *     SheetIconButton(iconType = IconType.CLOSE, onClick = onDismiss)
 *     Spacer(Modifier.weight(1f))
 *     SheetIconButton(iconType = IconType.DONE, onClick = onConfirm)
 * }
 * ```
 *
 * @param iconType The icon to display inside the button.
 * @param onClick Callback invoked when the button is tapped.
 * @param modifier Modifier applied to the button container.
 * @param border Optional border stroke.
 * @param containerColor Background fill color. Defaults to the secondary background.
 * @param contentColor Icon tint color. Defaults to the base icon color.
 * @since 0.0.1
 */
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
