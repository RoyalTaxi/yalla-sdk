package uz.yalla.platform.toggle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UISwitch
import platform.darwin.NSObject
import platform.objc.sel_registerName

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
actual fun NativeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
) {
    val handler = remember { SwitchEventHandler() }
    handler.callback = onCheckedChange

    UIKitView(
        modifier = modifier,
        factory = {
            val uiSwitch = UISwitch()
            handler.switch = uiSwitch
            uiSwitch.addTarget(
                target = handler,
                action = sel_registerName("handleValueChanged"),
                forControlEvents = UIControlEventValueChanged,
            )
            uiSwitch
        },
        update = { uiSwitch ->
            if (uiSwitch.isOn() != checked) {
                uiSwitch.setOn(checked, animated = true)
            }
            uiSwitch.enabled = enabled
        },
        properties = UIKitInteropProperties(
            interactionMode = UIKitInteropInteractionMode.NonCooperative,
            isNativeAccessibilityEnabled = false,
            placedAsOverlay = true,
        ),
    )
}

@ExperimentalForeignApi
private class SwitchEventHandler : NSObject() {
    var callback: ((Boolean) -> Unit)? = null
    var switch: UISwitch? = null

    @ObjCAction
    fun handleValueChanged() {
        val isOn = switch?.isOn() ?: return
        callback?.invoke(isOn)
    }
}
