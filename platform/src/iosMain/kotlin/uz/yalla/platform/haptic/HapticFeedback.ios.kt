package uz.yalla.platform.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

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
