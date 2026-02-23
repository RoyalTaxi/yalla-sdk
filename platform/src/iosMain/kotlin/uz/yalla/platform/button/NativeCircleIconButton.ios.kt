package uz.yalla.platform.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIColor
import uz.yalla.platform.LocalCircleIconButtonFactory
import uz.yalla.platform.model.IconType
import uz.yalla.platform.toAssetName

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeCircleIconButton(
    iconType: IconType,
    onClick: () -> Unit,
    modifier: Modifier,
    alpha: Float,
    border: BorderStroke?,
    background: Color
) {
    val factory = LocalCircleIconButtonFactory.current ?: return
    val borderWidth = border?.width?.value?.toDouble() ?: 0.0
    val borderColor = (border?.brush as? SolidColor)?.value?.toArgb()?.toLong() ?: 0L
    val iconName = iconType.toAssetName()

    val backgroundModifier =
        if (background.isSpecified) {
            Modifier.background(background, CircleShape)
        } else {
            Modifier
        }

    Box(modifier = modifier.size(48.dp).then(backgroundModifier)) {
        key(iconType) {
            UIKitViewController(
                factory = { factory(iconName, onClick, borderWidth, borderColor) },
                update = { controller ->
                    controller.view.alpha = alpha.toDouble()
                    controller.view.backgroundColor = UIColor.clearColor
                    controller.view.setOpaque(false)
                    controller.view.layer.setOpaque(false)
                    controller.view.layer.backgroundColor = UIColor.clearColor.CGColor
                },
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
