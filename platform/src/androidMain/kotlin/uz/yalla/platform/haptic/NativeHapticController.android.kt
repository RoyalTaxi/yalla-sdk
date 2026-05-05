package uz.yalla.platform.haptic

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import uz.yalla.design.haptic.Haptic
import uz.yalla.design.haptic.HapticController

/**
 * Android actual for [rememberNativeHapticController].
 *
 * Uses [android.view.View.performHapticFeedback] with the closest platform constant
 * for each [Haptic] kind. On API 30+ (Android R) `Confirm`/`Error` map to the dedicated
 * `CONFIRM`/`REJECT` constants; on older APIs they fall back to `LONG_PRESS`.
 *
 * Multi-tap patterns ([Haptic.Confirm] = two medium, [Haptic.Error] = three heavy) are
 * synthesised via [android.view.View.postDelayed] at 100 ms intervals. [Haptic.Hero]
 * fires a heavy thump followed by a light pulse 100 ms later.
 */
@Composable
actual fun rememberNativeHapticController(): HapticController {
    val view = LocalView.current
    return remember(view) {
        HapticController { haptic ->
            when (haptic) {
                Haptic.Selection -> view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)

                Haptic.Confirm -> {
                    val confirm =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            HapticFeedbackConstants.CONFIRM
                        } else {
                            HapticFeedbackConstants.CONTEXT_CLICK
                        }
                    view.performHapticFeedback(confirm)
                    view.postDelayed({ view.performHapticFeedback(confirm) }, 100)
                }

                Haptic.Warn -> view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

                Haptic.Error -> {
                    val reject =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            HapticFeedbackConstants.REJECT
                        } else {
                            HapticFeedbackConstants.LONG_PRESS
                        }
                    view.performHapticFeedback(reject)
                    view.postDelayed({ view.performHapticFeedback(reject) }, 100)
                    view.postDelayed({ view.performHapticFeedback(reject) }, 200)
                }

                Haptic.Hero -> {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    view.postDelayed(
                        { view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK) },
                        100
                    )
                }
            }
        }
    }
}
