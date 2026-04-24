package uz.yalla.design.font

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

/**
 * Typography scheme for the Yalla design system.
 *
 * Organizes text styles into purpose-based groups — titles, body text (with weight
 * variants), and custom styles — so UI components reference typography by intent.
 * Access via `System.font` inside a [YallaTheme][uz.yalla.design.theme.YallaTheme].
 *
 * ## Usage
 *
 * ```kotlin
 * Text(
 *     text = "Welcome",
 *     style = System.font.title.large,
 * )
 *
 * Text(
 *     text = "Body copy",
 *     style = System.font.body.base.medium,
 * )
 * ```
 *
 * @property title Title/heading text styles.
 * @property body Body text styles with weight variants.
 * @property custom Special-purpose text styles.
 * @since 0.0.1
 */
data class FontScheme(
    val title: Title,
    val body: Body,
    val custom: Custom
) {
    /**
     * Title/heading text styles, all using the bold font family.
     *
     * @property xLarge Extra-large title — 30sp, splash headers.
     * @property large Large title — 22sp, screen headers.
     * @property base Base title — 20sp, section headers.
     * @since 0.0.1
     */
    data class Title(
        val xLarge: TextStyle,
        val large: TextStyle,
        val base: TextStyle
    )

    /**
     * Body text styles organized by size, each with weight variants.
     *
     * @property caption Caption text — 13sp, medium weight. Used for labels and footnotes.
     * @property large Large body — 18sp with [Weighty] variants.
     * @property base Base body — 16sp with [Weighty] variants. Default for paragraphs.
     * @property small Small body — 14sp with [Weighty] variants. Secondary text.
     * @since 0.0.1
     */
    data class Body(
        val caption: TextStyle,
        val large: Weighty,
        val base: Weighty,
        val small: Weighty
    ) {
        /**
         * Weight variants for a given body text size.
         *
         * All three variants share the same font size and line height but differ in
         * font weight, allowing consistent sizing with visual emphasis control.
         *
         * @property regular Normal weight — everyday paragraph text.
         * @property medium Medium weight — subtly emphasized text, form labels.
         * @property bold Bold weight — strongly emphasized text, key values.
         * @since 0.0.1
         */
        data class Weighty(
            val regular: TextStyle,
            val medium: TextStyle,
            val bold: TextStyle
        )
    }

    /**
     * Special-purpose text styles outside the standard title/body hierarchy.
     *
     * @property carNumber License plate style — 12sp, Nummernschild font.
     * @since 0.0.1
     */
    data class Custom(
        val carNumber: TextStyle
    )
}

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] for providing [FontScheme].
 *
 * Has no default value — throws if accessed outside a [YallaTheme][uz.yalla.design.theme.YallaTheme].
 * This is intentional: font loading requires a composable context, so a static default is not feasible.
 *
 * @since 0.0.1
 */
val LocalFontScheme =
    staticCompositionLocalOf<FontScheme> {
        error("No FontScheme provided. Wrap your content with YallaTheme or provide a FontScheme via LocalFontScheme.")
    }

/**
 * Tabular-numerals variant of [FontScheme.Body.base] medium.
 *
 * Enables the OpenType `tnum` feature so digit glyphs render at equal width,
 * regardless of actual glyph width in the font. Use wherever numbers animate
 * or tick up/down — price displays during ride-cost changes, timer readouts,
 * distance meters — so digit-width jitter doesn't yank the surrounding
 * layout on every frame.
 *
 * Implementation is a derived property, not a constructor slot on
 * [FontScheme.Body]: keeping it out of the data class preserves binary
 * compatibility (no `copy()` or constructor signature churn) while giving
 * consumers the same `System.font.body.numeric` ergonomics they get on
 * existing slots.
 *
 * ## Usage
 *
 * ```kotlin
 * AnimatedVisibility(visible = tripInProgress) {
 *     Text(
 *         text = "${formattedCost} UZS",
 *         style = System.font.body.numeric,
 *     )
 * }
 * ```
 *
 * Works with any font in [FontScheme] whose file includes the `tnum`
 * OpenType feature — Inter, Roboto, and SF Pro (the three families this
 * module ships) all include it.
 *
 * @since 0.0.15
 */
val FontScheme.Body.numeric: TextStyle
    get() = base.medium.copy(fontFeatureSettings = FONT_FEATURE_TABULAR_NUMERALS)

/**
 * OpenType feature string for tabular numerals.
 *
 * Kept as a named constant so the string literal doesn't scatter across
 * consumer code the moment they need to compose it with other features
 * (e.g. `"tnum, ss01"` for tabular numerals with a stylistic set).
 */
internal const val FONT_FEATURE_TABULAR_NUMERALS = "tnum"
