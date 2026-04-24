package uz.yalla.design.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Motion tokens for the Yalla design system.
 *
 * The catalog is split into four groups — durations, easings, springs, and
 * stagger delays — so animation code reads like a vocabulary rather than a
 * grab-bag of magic numbers. Access via `System.motion` inside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme].
 *
 * ## Usage
 *
 * ```kotlin
 * val alpha by animateFloatAsState(
 *     targetValue = if (visible) 1f else 0f,
 *     animationSpec = tween(
 *         durationMillis = System.motion.duration.standard.inWholeMilliseconds.toInt(),
 *         easing = System.motion.easing.emphasized,
 *     ),
 * )
 *
 * val offset by animateDpAsState(
 *     targetValue = targetY,
 *     animationSpec = System.motion.spring.gentle,
 * )
 *
 * LaunchedEffect(Unit) {
 *     items.forEachIndexed { index, item ->
 *         delay(System.motion.stagger.list * index)
 *         // ... reveal
 *     }
 * }
 * ```
 *
 * Custom schemes can be constructed directly for previews or per-screen
 * deviation; [standardMotionScheme] returns the Yalla defaults. Per ADR-021,
 * the default values are semi-stable: tuning the *value* inside a token is
 * ABI-safe and only needs a patch bump; changing the *shape* of a token
 * (e.g. `Duration` → `Long`) is a breaking change.
 *
 * @property duration Named animation durations, typed as [Duration]. Use
 * `.inWholeMilliseconds.toInt()` when handing to Compose's `tween`.
 * @property easing Named [Easing] curves. Material 3's standard + emphasized
 * are the baselines; Yalla overrides only where the reference frames
 * (Linear / Arc / Raycast) ship measurably different feel.
 * @property spring Named [SpringSpec] presets keyed by feel.
 * @property stagger Named stagger delays as [Duration], for cascading item
 * reveals in lists and grids.
 * @since 0.0.17
 */
@Immutable
data class MotionScheme(
    val duration: Duration,
    val easing: Easing,
    val spring: Spring,
    val stagger: Stagger,
) {
    /**
     * Named animation durations.
     *
     * Scale: instant is feedback-level (under perception threshold for
     * animation), quick is micro-interactions, standard is the default
     * screen-level transition, slow is enter/exit for large elements,
     * contemplative is reserved for signature moments only.
     *
     * @property instant 100ms — state changes so fast they feel like feedback, not animation.
     * @property quick 200ms — micro-interactions, press states, icon toggles.
     * @property standard 350ms — default screen transitions, sheet reveals.
     * @property slow 500ms — large enter/exit, hero transitions.
     * @property contemplative 800ms — signature moments only.
     * @since 0.0.17
     */
    @Immutable
    data class Duration(
        val instant: kotlin.time.Duration,
        val quick: kotlin.time.Duration,
        val standard: kotlin.time.Duration,
        val slow: kotlin.time.Duration,
        val contemplative: kotlin.time.Duration,
    )

    /**
     * Named [Easing] curves for timed animations.
     *
     * Material 3's standard + emphasized are the baselines — keep them for
     * M3 components and pair Yalla-specific surfaces with the pair that
     * matches the motion's semantic direction (entrance / exit).
     *
     * @property standard Default for most property animations. Material 3 standard.
     * @property emphasized Entries that need to feel confident. Material 3 emphasized.
     * @property entrance Items arriving on screen. Fast-start, soft-land.
     * @property exit Items leaving screen. Soft-start, fast-finish.
     * @since 0.0.17
     */
    @Immutable
    data class Easing(
        val standard: androidx.compose.animation.core.Easing,
        val emphasized: androidx.compose.animation.core.Easing,
        val entrance: androidx.compose.animation.core.Easing,
        val exit: androidx.compose.animation.core.Easing,
    )

    /**
     * Named spring presets.
     *
     * Pick by *feel*: does the animation need to overshoot for delight, snap
     * firm for drag-release, or settle calm for ambient movement? Tuned so
     * each preset is visually distinct at a glance.
     *
     * @property bouncy Playful overshoot. Delight-moment use only.
     * @property gentle Calm snap without overshoot. Default for most springs.
     * @property snappy Fast settle for drag-release.
     * @property stiff Instant settle. Use for large elements that should feel anchored.
     * @since 0.0.17
     */
    @Immutable
    data class Spring(
        val bouncy: SpringSpec<Float>,
        val gentle: SpringSpec<Float>,
        val snappy: SpringSpec<Float>,
        val stiff: SpringSpec<Float>,
    )

    /**
     * Named stagger delays for cascading entries.
     *
     * Multiplied by index to offset each item's start:
     *
     * ```kotlin
     * items.forEachIndexed { index, item ->
     *     delay(System.motion.stagger.list * index)
     *     // reveal item
     * }
     * ```
     *
     * @property list 30ms — default for lists entering on first render.
     * @property grid 50ms — grid tiles revealing.
     * @property cards 75ms — stacked card entries, hero carousels.
     * @since 0.0.17
     */
    @Immutable
    data class Stagger(
        val list: kotlin.time.Duration,
        val grid: kotlin.time.Duration,
        val cards: kotlin.time.Duration,
    )
}

/**
 * Returns the Yalla-standard [MotionScheme].
 *
 * Values match the catalog in the YallaClient refactor spec (section 9) and
 * ADR-021. Consumers override individual tokens by passing a modified
 * [MotionScheme.copy] into `YallaTheme(motionScheme = ...)`.
 *
 * @since 0.0.17
 */
fun standardMotionScheme(): MotionScheme = MotionScheme(
    duration = MotionScheme.Duration(
        instant = 100.milliseconds,
        quick = 200.milliseconds,
        standard = 350.milliseconds,
        slow = 500.milliseconds,
        contemplative = 800.milliseconds,
    ),
    easing = MotionScheme.Easing(
        // Material 3 standard.
        standard = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f),
        // Material 3 emphasized.
        emphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f),
        // Fast-start, soft-land — items arriving.
        entrance = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f),
        // Soft-start, fast-finish — items leaving.
        exit = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f),
    ),
    spring = MotionScheme.Spring(
        bouncy = spring(dampingRatio = 0.5f, stiffness = 400f),
        gentle = spring(dampingRatio = 0.8f, stiffness = 250f),
        snappy = spring(dampingRatio = 0.7f, stiffness = 700f),
        stiff = spring(dampingRatio = 1.0f, stiffness = Spring.StiffnessHigh),
    ),
    stagger = MotionScheme.Stagger(
        list = 30.milliseconds,
        grid = 50.milliseconds,
        cards = 75.milliseconds,
    ),
)

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] for providing [MotionScheme].
 *
 * Defaults to [standardMotionScheme] so composables that render outside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme] (e.g. IDE previews) still
 * get usable motion tokens.
 *
 * @since 0.0.17
 */
val LocalMotionScheme = staticCompositionLocalOf { standardMotionScheme() }
