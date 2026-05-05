package uz.yalla.design.haptic

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Semantic haptic vocabulary for the Yalla design system.
 *
 * Unlike other token schemes (color, font, radius, …), haptics are events, not values —
 * so the design layer owns the *vocabulary* and the [HapticController] interface, while
 * the actual taptic-engine / vibrator delivery lives in `platform`.
 *
 * Per `docs/MOTION.md`:
 *
 * | Kind | Pattern | When to use |
 * |---|---|---|
 * | [Selection] | single light tap | toggle, row-select, stepper increment |
 * | [Confirm] | two medium taps | action completed successfully |
 * | [Warn] | one heavy thump | action completed with caveats |
 * | [Error] | three quick heavy taps | action blocked |
 * | [Hero] | composition pattern | signature moments — booking confirmed, payment succeeded |
 *
 * Access via `System.haptic.perform(Haptic.Confirm)` inside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme].
 */
enum class Haptic {
    /** Single light tap — toggle, row-select, stepper increment. */
    Selection,

    /** Two medium taps — action completed successfully. */
    Confirm,

    /** One heavy thump — action completed with caveats. */
    Warn,

    /** Three quick heavy taps — action blocked. */
    Error,

    /** Signature moment — composition pattern (e.g. booking confirmed, payment succeeded). */
    Hero
}

/**
 * Controller that fires a haptic for a given [Haptic] kind.
 *
 * Implemented by `platform`'s `rememberNativeHapticController()` and provided by
 * [YallaTheme][uz.yalla.design.theme.YallaTheme] via [LocalHapticController]. Consumers
 * read it through `System.haptic`.
 */
fun interface HapticController {
    /** Perform the haptic associated with [haptic]. */
    fun perform(haptic: Haptic)
}

/**
 * No-op [HapticController] used as the default for [LocalHapticController] before
 * [YallaTheme][uz.yalla.design.theme.YallaTheme] provides a real one (e.g. previews).
 */
internal val NoopHapticController: HapticController = HapticController { /* no-op */ }

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] holding the active
 * [HapticController]. [YallaTheme][uz.yalla.design.theme.YallaTheme] provides a
 * platform-native implementation; outside the theme this falls back to a no-op so
 * previews and tests don't crash.
 */
val LocalHapticController = staticCompositionLocalOf<HapticController> { NoopHapticController }
