package uz.yalla.platform.haptic

import androidx.compose.runtime.Composable
import uz.yalla.design.haptic.HapticController

/**
 * Remembers a platform-native [HapticController] (from `design`) speaking the semantic
 * [uz.yalla.design.haptic.Haptic] vocabulary.
 *
 * - **Android**: delegates to `LocalHapticFeedback`/`HapticFeedbackConstants`, mapping
 *   each [uz.yalla.design.haptic.Haptic] kind to the closest platform constant.
 * - **iOS**: uses `UIImpactFeedbackGenerator` and `UINotificationFeedbackGenerator`,
 *   matching the spec's tap/thump patterns.
 *
 * Wire it into the design system by passing the result to
 * `YallaTheme(hapticController = rememberNativeHapticController()) { … }`.
 *
 * This is the canonical haptic controller. The older
 * [rememberHapticController]/[HapticType] in this same package predates the design-layer
 * vocabulary and stays around for existing callers; new code should target the
 * `design.haptic.Haptic` vocabulary via this function.
 */
@Composable
expect fun rememberNativeHapticController(): HapticController
