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
import uz.yalla.design.haptic.Haptic
import uz.yalla.design.haptic.HapticController

/**
 * iOS actual for [rememberNativeHapticController].
 *
 * - [Haptic.Selection] -> light impact.
 * - [Haptic.Confirm] -> notification `success` (two-tap engine pattern).
 * - [Haptic.Warn] -> notification `warning` (one heavy thump).
 * - [Haptic.Error] -> notification `error` (three-tap engine pattern).
 * - [Haptic.Hero] -> heavy impact followed by a light pulse 100 ms later (composition).
 *
 * Generators are recreated per call rather than cached because UIKit recommends
 * preparing close to the trigger and the cost is negligible.
 */
@Composable
actual fun rememberNativeHapticController(): HapticController =
    remember {
        HapticController { haptic ->
            when (haptic) {
                Haptic.Selection -> impact(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)

                Haptic.Confirm -> notify(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)

                Haptic.Warn -> notify(UINotificationFeedbackType.UINotificationFeedbackTypeWarning)

                Haptic.Error -> notify(UINotificationFeedbackType.UINotificationFeedbackTypeError)

                Haptic.Hero -> {
                    impact(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
                    dispatch_after(
                        dispatch_time(DISPATCH_TIME_NOW, 100_000_000),
                        dispatch_get_main_queue()
                    ) {
                        impact(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
                    }
                }
            }
        }
    }

private fun impact(style: UIImpactFeedbackStyle) {
    val generator = UIImpactFeedbackGenerator(style = style)
    generator.prepare()
    generator.impactOccurred()
}

private fun notify(type: UINotificationFeedbackType) {
    val generator = UINotificationFeedbackGenerator()
    generator.prepare()
    generator.notificationOccurred(type)
}
