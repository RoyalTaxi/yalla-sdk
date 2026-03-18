package uz.yalla.platform.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIColor
import platform.UIKit.UIViewController
import uz.yalla.platform.model.IconType
import uz.yalla.platform.toAssetName

/**
 * Shared internal composable for iOS native icon buttons.
 *
 * Fixes BUG-1: the `onClick` lambda is captured via [rememberUpdatedState]
 * so that recompositions with a new callback are always reflected,
 * even though UIKitViewController's `factory` block only runs once.
 *
 * @param factory creates the native UIViewController — callers pass the appropriate
 *   [CircleIconButtonFactory] or [SquircleIconButtonFactory] call
 * @param alpha view alpha; only meaningful for circle buttons (default 1f)
 * @param useKey when true, wraps the UIKitViewController in `key(iconType)` to
 *   recreate it when the icon changes (circle buttons need this)
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
internal fun NativeIconButton(
    iconType: IconType,
    onClick: () -> Unit,
    modifier: Modifier,
    border: BorderStroke?,
    background: Color,
    backgroundShape: Shape,
    factory: (icon: String, onClick: () -> Unit, borderWidth: Double, borderColor: Long) -> UIViewController,
    alpha: Float = 1f,
    useKey: Boolean = false,
) {
    // BUG-1 fix: always capture the latest onClick so the native button
    // never fires a stale closure captured at factory-creation time.
    val currentOnClick = rememberUpdatedState(onClick)

    val borderWidth = border?.width?.value?.toDouble() ?: 0.0
    val borderColor = (border?.brush as? SolidColor)?.value?.toArgb()?.toLong() ?: 0L
    val iconName = iconType.toAssetName()

    val backgroundModifier =
        if (background.isSpecified) {
            Modifier.background(background, backgroundShape)
        } else {
            Modifier
        }

    Box(modifier = modifier.size(48.dp).then(backgroundModifier)) {
        val content: @Composable () -> Unit = {
            UIKitViewController(
                factory = { factory(iconName, { currentOnClick.value() }, borderWidth, borderColor) },
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
        if (useKey) key(iconType) { content() } else content()
    }
}
