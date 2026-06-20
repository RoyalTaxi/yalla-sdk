package uz.yalla.design.color

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// TODO(quality, needs-decision): drop `data` from ColorScheme and its nested holders so adding a
//  token is an additive (non-breaking) change — blocked: removing copy()/componentN from these
//  core public types is a breaking ABI removal in design.klib.api / android/design.api and needs
//  owner sign-off per the breaking-public-API policy.
/**
 * The semantic color tokens for one appearance (light or dark).
 *
 * Read it through [uz.yalla.design.theme.System.color] inside a
 * [uz.yalla.design.theme.YallaTheme]; the nested holders ([Text], [Background], [Border],
 * [Button], [Icon], [Accent], [Gradient]) group tokens by role so call sites read intent
 * (`System.color.text.base`) rather than a raw hex value.
 */
@Immutable
public data class ColorScheme(
    val text: Text,
    val background: Background,
    val border: Border,
    val button: Button,
    val icon: Icon,
    val accent: Accent,
    val gradient: Gradient
) {
    /** Foreground text colors keyed by role (primary, subtle, link, error, on-dark). */
    @Immutable
    public data class Text(
        val base: Color,
        val subtle: Color,
        val link: Color,
        val red: Color,
        val white: Color
    )

    /** Surface/background colors: the base canvas, brand fill, and secondary/tertiary layers. */
    @Immutable
    public data class Background(
        val base: Color,
        val brand: Color,
        val secondary: Color,
        val tertiary: Color
    )

    /** Stroke colors for outlined surfaces (disabled, filled, on-dark, error). */
    @Immutable
    public data class Border(
        val disabled: Color,
        val filled: Color,
        val white: Color,
        val error: Color
    )

    /** Button fill colors keyed by emphasis/state. */
    @Immutable
    public data class Button(
        val active: Color,
        val disabled: Color,
        val secondary: Color,
        val tertiary: Color,
        val disabledTertiary: Color
    )

    /** Icon tint colors keyed by role/state. */
    @Immutable
    public data class Icon(
        val white: Color,
        val base: Color,
        val secondary: Color,
        val disabled: Color,
        val red: Color,
        val subtle: Color
    )

    // TODO(quality, needs-decision): the positional `color1..color5` names are non-semantic on the
    //  public surface — renaming them (or modeling the ordered palette as a List<Color>) is a
    //  breaking ABI change to a published type and needs owner sign-off.
    /** Decorative accent palette (e.g. avatar/category tints). */
    @Immutable
    public data class Accent(
        val pinkSun: Color,
        val color1: Color,
        val color2: Color,
        val color3: Color,
        val color4: Color,
        val color5: Color
    )

    /** Multi-stop gradients exposed as ready-to-use [Brush]es (`splash`, `sunsetNight`). */
    @Immutable
    public data class Gradient(
        val splash: Brush,
        val sunsetNight: Brush
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
                color1 = Color1,
                color2 = Color2,
                color3 = Color3,
                color4 = Color4,
                color5 = Color5
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
                color1 = Color1,
                color2 = Color2,
                color3 = Color3,
                color4 = Color4,
                color5 = Color5
            ),
        gradient =
            ColorScheme.Gradient(
                splash = SplashBackground,
                sunsetNight = SunsetNight
            )
    )

internal val LocalColorScheme: ProvidableCompositionLocal<ColorScheme> =
    staticCompositionLocalOf {
        error("No ColorScheme provided. Wrap your content with YallaTheme or provide a ColorScheme via LocalColorScheme.")
    }
