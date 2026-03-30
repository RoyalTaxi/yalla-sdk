package uz.yalla.platform.haptic

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberHapticController(): HapticController {
    val view = LocalView.current
    return remember(view) {
        object : HapticController {
            override fun perform(type: HapticType) {
                if (type == HapticType.ErrorRepeat) {
                    performErrorRepeat()
                    return
                }
                val feedbackConstant = when (type) {
                    HapticType.Light -> HapticFeedbackConstants.CLOCK_TICK
                    HapticType.Medium -> HapticFeedbackConstants.CONTEXT_CLICK
                    HapticType.Heavy -> HapticFeedbackConstants.LONG_PRESS
                    HapticType.Success ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.CONFIRM
                        else HapticFeedbackConstants.LONG_PRESS
                    HapticType.Error ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.REJECT
                        else HapticFeedbackConstants.LONG_PRESS
                    HapticType.Warning -> HapticFeedbackConstants.CONTEXT_CLICK
                    HapticType.ErrorRepeat -> error("handled above")
                }
                view.performHapticFeedback(feedbackConstant)
            }

            private fun performErrorRepeat() {
                val constant =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.REJECT
                    else HapticFeedbackConstants.LONG_PRESS
                view.performHapticFeedback(constant)
                view.postDelayed({ view.performHapticFeedback(constant) }, 100)
                view.postDelayed({ view.performHapticFeedback(constant) }, 200)
            }
        }
    }
}
