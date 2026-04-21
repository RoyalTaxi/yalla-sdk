package uz.yalla.platform.haptic

import androidx.compose.runtime.Composable

/**
 * Type of haptic feedback to perform.
 * @since 0.0.6-alpha05
 */
enum class HapticType {
    /** Subtle tap for minor UI events. */
    Light,

    /** Standard tap for confirmations. */
    Medium,

    /** Strong tap for significant events. */
    Heavy,

    /** Success notification pattern. */
    Success,

    /** Error notification pattern. */
    Error,

    /** Triple error notification pattern (3x with 100ms interval). */
    ErrorRepeat,

    /** Warning notification pattern. */
    Warning,
}

/**
 * Controller for performing haptic feedback.
 * @since 0.0.6-alpha05
 */
interface HapticController {
    /** Perform haptic feedback of the given [type]. */
    fun perform(type: HapticType)
}

/**
 * Remembers a platform-native [HapticController].
 *
 * On Android, uses `View.performHapticFeedback()` and `VibrationEffect`.
 * On iOS, uses `UIImpactFeedbackGenerator` and `UINotificationFeedbackGenerator`.
 *
 * @since 0.0.6-alpha05
 */
@Composable
expect fun rememberHapticController(): HapticController
