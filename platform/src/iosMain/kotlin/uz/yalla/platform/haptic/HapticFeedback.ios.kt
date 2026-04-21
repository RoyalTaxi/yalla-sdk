package uz.yalla.platform.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time

/**
 * iOS actual for [rememberHapticController].
 *
 * Uses `UIImpactFeedbackGenerator` for [HapticType.Light], [HapticType.Medium], and
 * [HapticType.Heavy]; `UINotificationFeedbackGenerator` for [HapticType.Success],
 * [HapticType.Error], [HapticType.Warning], and [HapticType.ErrorRepeat].
 *
 * [HapticType.ErrorRepeat] dispatches three error haptics at 0 ms, 100 ms, and 200 ms
 * via `dispatch_after` on the main queue.
 */
@Composable
actual fun rememberHapticController(): HapticController {
    return remember {
        object : HapticController {
            override fun perform(type: HapticType) {
                when (type) {
                    HapticType.Light -> {
                        val generator = UIImpactFeedbackGenerator(
                            style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight
                        )
                        generator.prepare()
                        generator.impactOccurred()
                    }
                    HapticType.Medium -> {
                        val generator = UIImpactFeedbackGenerator(
                            style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium
                        )
                        generator.prepare()
                        generator.impactOccurred()
                    }
                    HapticType.Heavy -> {
                        val generator = UIImpactFeedbackGenerator(
                            style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy
                        )
                        generator.prepare()
                        generator.impactOccurred()
                    }
                    HapticType.Success -> {
                        val generator = UINotificationFeedbackGenerator()
                        generator.prepare()
                        generator.notificationOccurred(
                            UINotificationFeedbackType.UINotificationFeedbackTypeSuccess
                        )
                    }
                    HapticType.Error -> {
                        val generator = UINotificationFeedbackGenerator()
                        generator.prepare()
                        generator.notificationOccurred(
                            UINotificationFeedbackType.UINotificationFeedbackTypeError
                        )
                    }
                    HapticType.ErrorRepeat -> {
                        val generator = UINotificationFeedbackGenerator()
                        generator.prepare()
                        val errorType = UINotificationFeedbackType.UINotificationFeedbackTypeError
                        generator.notificationOccurred(errorType)
                        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 100_000_000), dispatch_get_main_queue()) {
                            generator.notificationOccurred(errorType)
                        }
                        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 200_000_000), dispatch_get_main_queue()) {
                            generator.notificationOccurred(errorType)
                        }
                    }
                    HapticType.Warning -> {
                        val generator = UINotificationFeedbackGenerator()
                        generator.prepare()
                        generator.notificationOccurred(
                            UINotificationFeedbackType.UINotificationFeedbackTypeWarning
                        )
                    }
                }
            }
        }
    }
}
