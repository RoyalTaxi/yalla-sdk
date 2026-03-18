package uz.yalla.design.color

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Semantic color scheme for the Yalla design system.
 *
 * Organizes raw color tokens into purpose-based groups (text, background, border, etc.)
 * so UI components reference colors by intent rather than literal value. Each group is
 * a nested immutable data class, enabling structured access like `System.color.text.base`.
 *
 * Two factory functions create the built-in variants: [light] and [dark].
 * Custom schemes can be constructed directly for previews or white-labeling.
 *
 * ## Usage
 *
 * ```kotlin
 * // Inside a @Composable wrapped by YallaTheme:
 * Text(
 *     text = "Hello",
 *     color = System.color.text.base,
 *     style = System.font.body.base.medium,
 * )
 *
 * Box(modifier = Modifier.background(System.color.background.base))
 * ```
 *
 * @property text Text color tokens.
 * @property background Background/surface color tokens.
 * @property border Border and outline color tokens.
 * @property button Button fill color tokens.
 * @property icon Icon tint color tokens.
 * @property accent Decorative accent color tokens.
 * @property gradient Brush-based gradient tokens.
 * @since 0.0.1
 */
data class ColorScheme(
    val text: Text,
    val background: Background,
    val border: Border,
    val button: Button,
    val icon: Icon,
    val accent: Accent,
    val gradient: Gradient,
) {
    /**
     * Text color tokens.
     *
     * @property base Primary text color — headings, body, labels.
     * @property subtle Secondary/muted text — hints, placeholders, timestamps.
     * @property link Hyperlink and interactive text color.
     * @property red Error and destructive action text color.
     * @property white Text on dark/brand surfaces.
     * @since 0.0.1
     */
    data class Text(
        val base: Color,
        val subtle: Color,
        val link: Color,
        val red: Color,
        val white: Color,
    )

    /**
     * Background and surface color tokens.
     *
     * @property base Primary screen/card background.
     * @property brand Brand-colored background (e.g. promotional banners).
     * @property secondary Slightly tinted background for visual separation.
     * @property tertiary Third-level background for nested containers.
     * @since 0.0.1
     */
    data class Background(
        val base: Color,
        val brand: Color,
        val secondary: Color,
        val tertiary: Color,
    )

    /**
     * Border and outline color tokens.
     *
     * @property disabled Border for disabled/inactive inputs.
     * @property filled Border for focused/filled inputs.
     * @property white White border for overlays on dark surfaces.
     * @property error Border color indicating validation errors.
     * @since 0.0.1
     */
    data class Border(
        val disabled: Color,
        val filled: Color,
        val white: Color,
        val error: Color,
    )

    /**
     * Button fill color tokens.
     *
     * @property active Primary CTA button fill.
     * @property disabled Disabled primary button fill.
     * @property secondary Secondary/outlined button fill.
     * @property tertiary Tertiary/text button fill.
     * @property disabledTertiary Disabled tertiary button fill.
     * @since 0.0.1
     */
    data class Button(
        val active: Color,
        val disabled: Color,
        val secondary: Color,
        val tertiary: Color,
        val disabledTertiary: Color,
    )

    /**
     * Icon tint color tokens.
     *
     * @property white Icon on dark/brand surfaces.
     * @property base Primary icon color matching text.base.
     * @property secondary Brand-accent icon color.
     * @property disabled Disabled/inactive icon color.
     * @property red Error/destructive icon color.
     * @property subtle Muted icon color for secondary indicators.
     * @since 0.0.1
     */
    data class Icon(
        val white: Color,
        val base: Color,
        val secondary: Color,
        val disabled: Color,
        val red: Color,
        val subtle: Color,
    )

    /**
     * Decorative accent colors for illustrations, avatars, and badges.
     *
     * @property pinkSun Vibrant pink accent.
     * @property color1 Soft pink accent.
     * @property color2 Soft blue accent.
     * @property color3 Warm amber accent.
     * @property color4 Aqua/teal accent.
     * @property color5 Soft purple accent.
     * @since 0.0.1
     */
    data class Accent(
        val pinkSun: Color,
        val color1: Color,
        val color2: Color,
        val color3: Color,
        val color4: Color,
        val color5: Color,
    )

    /**
     * Gradient brush tokens for decorative surfaces.
     *
     * @property splash Vertical purple gradient for splash/loading screens.
     * @property sunsetNight Diagonal pink-to-purple gradient for promotional elements.
     * @since 0.0.1
     */
    data class Gradient(
        val splash: Brush,
        val sunsetNight: Brush,
    )
}

/**
 * Creates the light theme [ColorScheme].
 *
 * Maps light-mode raw color tokens from [Color.kt][uz.yalla.design.color] into the
 * semantic [ColorScheme] structure. Accent and gradient tokens are shared across themes.
 *
 * @return Light-mode [ColorScheme] instance.
 * @since 0.0.1
 */
fun light() = ColorScheme(
    text = ColorScheme.Text(
        base = LightTextBase,
        subtle = LightTextSubtle,
        link = LightTextLink,
        red = LightTextRed,
        white = LightTextWhite,
    ),
    background = ColorScheme.Background(
        base = LightBackgroundBase,
        brand = LightBackgroundBrandBase,
        secondary = LightBackgroundSecondary,
        tertiary = LightBackgroundTertiary,
    ),
    border = ColorScheme.Border(
        disabled = LightBorderDisabled,
        filled = LightBorderFilled,
        white = LightBorderWhite,
        error = LightBorderError,
    ),
    button = ColorScheme.Button(
        active = LightButtonActive,
        disabled = LightButtonDisabled,
        secondary = LightButtonSecondary,
        tertiary = LightButtonTertiary,
        disabledTertiary = LightButtonDisabledTertiary,
    ),
    icon = ColorScheme.Icon(
        white = LightIconWhite,
        base = LightIconBase,
        secondary = LightIconSecondary,
        disabled = LightIconDisabled,
        red = LightIconRed,
        subtle = LightIconSubtle,
    ),
    accent = ColorScheme.Accent(
        pinkSun = PinkSun,
        color1 = Color1,
        color2 = Color2,
        color3 = Color3,
        color4 = Color4,
        color5 = Color5,
    ),
    gradient = ColorScheme.Gradient(
        splash = SplashBackground,
        sunsetNight = SunsetNight,
    ),
)

/**
 * Creates the dark theme [ColorScheme].
 *
 * Maps dark-mode raw color tokens from [Color.kt][uz.yalla.design.color] into the
 * semantic [ColorScheme] structure. Accent and gradient tokens are shared across themes.
 *
 * @return Dark-mode [ColorScheme] instance.
 * @since 0.0.1
 */
fun dark() = ColorScheme(
    text = ColorScheme.Text(
        base = DarkTextBase,
        subtle = DarkTextSubtle,
        link = DarkTextLink,
        red = DarkTextRed,
        white = DarkTextWhite,
    ),
    background = ColorScheme.Background(
        base = DarkBackgroundBase,
        brand = DarkBackgroundBrandBase,
        secondary = DarkBackgroundSecondary,
        tertiary = DarkBackgroundTertiary,
    ),
    border = ColorScheme.Border(
        disabled = DarkBorderDisabled,
        filled = DarkBorderFilled,
        white = DarkBorderWhite,
        error = DarkBorderError,
    ),
    button = ColorScheme.Button(
        active = DarkButtonActive,
        disabled = DarkButtonDisabled,
        secondary = DarkButtonSecondary,
        tertiary = DarkButtonTertiary,
        disabledTertiary = DarkButtonDisabledTertiary,
    ),
    icon = ColorScheme.Icon(
        white = DarkIconWhite,
        base = DarkIconBase,
        secondary = DarkIconSecondary,
        disabled = DarkIconDisabled,
        red = DarkIconRed,
        subtle = DarkIconSubtle,
    ),
    accent = ColorScheme.Accent(
        pinkSun = PinkSun,
        color1 = Color1,
        color2 = Color2,
        color3 = Color3,
        color4 = Color4,
        color5 = Color5,
    ),
    gradient = ColorScheme.Gradient(
        splash = SplashBackground,
        sunsetNight = SunsetNight,
    ),
)

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] for providing [ColorScheme].
 *
 * Defaults to [light] theme. Overridden by [YallaTheme][uz.yalla.design.theme.YallaTheme]
 * to supply the appropriate color scheme based on the current dark/light mode.
 *
 * @since 0.0.1
 */
val LocalColorScheme = staticCompositionLocalOf { light() }
