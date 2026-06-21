package uz.yalla.design.color

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * The semantic color tokens for one appearance (light or dark).
 *
 * Read it through [uz.yalla.design.theme.System.color] inside a
 * [uz.yalla.design.theme.YallaTheme]; the nested holders ([Text], [Background], [Border],
 * [Button], [Icon], [Accent], [Gradient]) group tokens by role so call sites read intent
 * (`System.color.text.base`) rather than a raw hex value.
 */
@Immutable
public class ColorScheme(
    public val text: Text,
    public val background: Background,
    public val border: Border,
    public val button: Button,
    public val icon: Icon,
    public val accent: Accent,
    public val gradient: Gradient
) {
    /** Foreground text colors keyed by role (primary, subtle, link, error, on-dark). */
    @Immutable
    public class Text(
        public val base: Color,
        public val subtle: Color,
        public val link: Color,
        public val red: Color,
        public val white: Color
    )

    /** Surface/background colors: the base canvas, brand fill, and secondary/tertiary layers. */
    @Immutable
    public class Background(
        public val base: Color,
        public val brand: Color,
        public val secondary: Color,
        public val tertiary: Color
    )

    /** Stroke colors for outlined surfaces (disabled, filled, on-dark, error). */
    @Immutable
    public class Border(
        public val disabled: Color,
        public val filled: Color,
        public val white: Color,
        public val error: Color
    )

    /** Button fill colors keyed by emphasis/state. */
    @Immutable
    public class Button(
        public val active: Color,
        public val disabled: Color,
        public val secondary: Color,
        public val tertiary: Color,
        public val disabledTertiary: Color
    )

    /** Icon tint colors keyed by role/state. */
    @Immutable
    public class Icon(
        public val white: Color,
        public val base: Color,
        public val secondary: Color,
        public val disabled: Color,
        public val red: Color,
        public val subtle: Color
    )

    /** Decorative accent palette (e.g. avatar/category tints). */
    @Immutable
    public class Accent(
        public val pinkSun: Color,
        public val blush: Color,
        public val periwinkle: Color,
        public val amber: Color,
        public val aqua: Color,
        public val lavender: Color
    )

    /** Multi-stop gradients exposed as ready-to-use [Brush]es (`splash`, `sunsetNight`). */
    @Immutable
    public class Gradient(
        public val splash: Brush,
        public val sunsetNight: Brush
    )
}

/**
 * The light-appearance [ColorScheme], built once and shared so `YallaTheme`'s default does not
 * allocate a fresh scheme (and churn `staticCompositionLocalOf` identity) on every recomposition.
 */
internal val LightColorScheme: ColorScheme = light()

/**
 * The dark-appearance [ColorScheme]; see [LightColorScheme] for why it is hoisted.
 */
internal val DarkColorScheme: ColorScheme = dark()

internal fun light(): ColorScheme =
    ColorScheme(
        text =
            ColorScheme.Text(
                base = LightTextBase,
                subtle = LightTextSubtle,
                link = LightTextLink,
                red = LightTextRed,
                white = LightTextWhite
            ),
        background =
            ColorScheme.Background(
                base = LightBackgroundBase,
                brand = LightBackgroundBrandBase,
                secondary = LightBackgroundSecondary,
                tertiary = LightBackgroundTertiary
            ),
        border =
            ColorScheme.Border(
                disabled = LightBorderDisabled,
                filled = LightBorderFilled,
                white = LightBorderWhite,
                error = LightBorderError
            ),
        button =
            ColorScheme.Button(
                active = LightButtonActive,
                disabled = LightButtonDisabled,
                secondary = LightButtonSecondary,
                tertiary = LightButtonTertiary,
                disabledTertiary = LightButtonDisabledTertiary
            ),
        icon =
            ColorScheme.Icon(
                white = LightIconWhite,
                base = LightIconBase,
                secondary = LightIconSecondary,
                disabled = LightIconDisabled,
                red = LightIconRed,
                subtle = LightIconSubtle
            ),
        accent =
            ColorScheme.Accent(
                pinkSun = PinkSun,
                blush = Color1,
                periwinkle = Color2,
                amber = Color3,
                aqua = Color4,
                lavender = Color5
            ),
        gradient =
            ColorScheme.Gradient(
                splash = SplashBackground,
                sunsetNight = SunsetNight
            )
    )

internal fun dark(): ColorScheme =
    ColorScheme(
        text =
            ColorScheme.Text(
                base = DarkTextBase,
                subtle = DarkTextSubtle,
                link = DarkTextLink,
                red = DarkTextRed,
                white = DarkTextWhite
            ),
        background =
            ColorScheme.Background(
                base = DarkBackgroundBase,
                brand = DarkBackgroundBrandBase,
                secondary = DarkBackgroundSecondary,
                tertiary = DarkBackgroundTertiary
            ),
        border =
            ColorScheme.Border(
                disabled = DarkBorderDisabled,
                filled = DarkBorderFilled,
                white = DarkBorderWhite,
                error = DarkBorderError
            ),
        button =
            ColorScheme.Button(
                active = DarkButtonActive,
                disabled = DarkButtonDisabled,
                secondary = DarkButtonSecondary,
                tertiary = DarkButtonTertiary,
                disabledTertiary = DarkButtonDisabledTertiary
            ),
        icon =
            ColorScheme.Icon(
                white = DarkIconWhite,
                base = DarkIconBase,
                secondary = DarkIconSecondary,
                disabled = DarkIconDisabled,
                red = DarkIconRed,
                subtle = DarkIconSubtle
            ),
        accent =
            ColorScheme.Accent(
                pinkSun = PinkSun,
                blush = Color1,
                periwinkle = Color2,
                amber = Color3,
                aqua = Color4,
                lavender = Color5
            ),
        gradient =
            ColorScheme.Gradient(
                splash = SplashBackground,
                sunsetNight = SunsetNight
            )
    )

internal val LocalColorScheme: ProvidableCompositionLocal<ColorScheme> =
    staticCompositionLocalOf {
        error(
            "No ColorScheme provided. Wrap your content with YallaTheme or provide a ColorScheme via " +
                "LocalColorScheme."
        )
    }
