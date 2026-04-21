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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.platform.model.IconType
import uz.yalla.platform.toImageVector

/**
 * Android actual for [NativeCircleIconButton].
 *
 * Renders as a Material3 [IconButton] with a 48 dp circular touch target.
 * The [alpha] parameter is applied via [Modifier.graphicsLayer]; the [background]
 * parameter is currently unused on Android (the design system defaults are applied
 * via [IconButtonDefaults.iconButtonColors]).
 *
 * **Platform note:** The [alpha], [border], and [background] parameters are declared in
 * the `expect` signature and fully utilized in the iOS actual (which wraps a native
 * `UIButton`). On Android these are partially used or ignored:
 * - [alpha] — applied via `graphicsLayer`
 * - [border] — applied via `Modifier.border` when non-null
 * - [background] — ignored; Android uses design-system defaults from [IconButtonDefaults]
 *
 * The suppress exists because the Kotlin compiler flags [background] as unused in this
 * actual, even though the `expect` declaration requires the parameter for cross-platform parity.
 */
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
                contentColor = System.color.icon.base,
                containerColor = System.color.background.base
            )
    ) {
        Image(
            painter = rememberVectorPainter(iconType.toImageVector()),
            contentDescription = null,
            colorFilter = ColorFilter.tint(System.color.icon.base)
        )
    }
}
