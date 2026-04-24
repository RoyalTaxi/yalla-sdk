package uz.yalla.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import uz.yalla.design.color.ColorScheme
import uz.yalla.design.color.LocalColorScheme
import uz.yalla.design.color.dark
import uz.yalla.design.color.light
import uz.yalla.design.font.FontScheme
import uz.yalla.design.font.LocalFontScheme
import uz.yalla.design.font.rememberFontScheme
import uz.yalla.design.motion.LocalMotionScheme
import uz.yalla.design.motion.MotionScheme
import uz.yalla.design.motion.standardMotionScheme
import uz.yalla.design.radius.LocalRadiusScheme
import uz.yalla.design.radius.RadiusScheme
import uz.yalla.design.radius.standardRadiusScheme
import uz.yalla.design.space.LocalSpaceScheme
import uz.yalla.design.space.SpaceScheme
import uz.yalla.design.space.standardSpaceScheme
import androidx.compose.material3.darkColorScheme as materialDarkColorScheme
import androidx.compose.material3.lightColorScheme as materialLightColorScheme

/** CompositionLocal tracking whether the current theme is dark mode. */
private val LocalIsDark = staticCompositionLocalOf { false }

/**
 * Root composable that applies the Yalla design system.
 *
 * Sets up [ColorScheme], [FontScheme], and ripple configuration, then bridges them
 * into Material3's [MaterialTheme] so both Yalla tokens (`System.color.*`) and M3
 * components work correctly. Wrap your app or screen content with this composable.
 *
 * The Material3 color scheme mapping exists so standard M3 components (TextField,
 * Button, etc.) pick up Yalla brand colors without additional configuration.
 *
 * ## Usage
 *
 * ```kotlin
 * @Composable
 * fun App() {
 *     YallaTheme {
 *         // Yalla tokens available here:
 *         Text(
 *             text = "Hello Yalla",
 *             color = System.color.text.base,
 *             style = System.font.body.base.medium,
 *         )
 *     }
 * }
 * ```
 *
 * @param isDark Whether to use dark color scheme. Defaults to system setting.
 * @param colorScheme Color tokens to apply. Defaults to [light] or [dark] based on [isDark].
 * @param fontScheme Typography tokens to apply. Defaults to [rememberFontScheme].
 * @param spaceScheme Spacing tokens to apply. Defaults to [standardSpaceScheme].
 * @param radiusScheme Corner-radius tokens to apply. Defaults to [standardRadiusScheme].
 * @param content Composable content wrapped by the theme.
 * @since 0.0.1
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YallaTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    colorScheme: ColorScheme = if (isDark) dark() else light(),
    fontScheme: FontScheme = rememberFontScheme(),
    spaceScheme: SpaceScheme = standardSpaceScheme(),
    radiusScheme: RadiusScheme = standardRadiusScheme(),
    motionScheme: MotionScheme = standardMotionScheme(),
    content: @Composable () -> Unit
) {
    val rippleConfiguration =
        RippleConfiguration(
            color = if (isDark) Color.White else Color.Black,
            rippleAlpha =
                RippleAlpha(
                    pressedAlpha = 0.12f,
                    focusedAlpha = 0.08f,
                    draggedAlpha = 0.12f,
                    hoveredAlpha = 0.08f
                )
        )

    val materialColorScheme =
        if (isDark) {
            materialDarkColorScheme(
                primary = colorScheme.button.active,
                onPrimary = colorScheme.text.white,
                secondary = colorScheme.button.secondary,
                tertiary = colorScheme.button.tertiary,
                background = colorScheme.background.base,
                surface = colorScheme.background.secondary,
                error = colorScheme.text.red,
                onBackground = colorScheme.text.base,
                onSurface = colorScheme.text.base
            )
        } else {
            materialLightColorScheme(
                primary = colorScheme.button.active,
                onPrimary = colorScheme.text.white,
                secondary = colorScheme.button.secondary,
                tertiary = colorScheme.button.tertiary,
                background = colorScheme.background.base,
                surface = colorScheme.background.secondary,
                error = colorScheme.text.red,
                onBackground = colorScheme.text.base,
                onSurface = colorScheme.text.base
            )
        }

    CompositionLocalProvider(
        LocalIsDark provides isDark,
        LocalColorScheme provides colorScheme,
        LocalFontScheme provides fontScheme,
        LocalSpaceScheme provides spaceScheme,
        LocalRadiusScheme provides radiusScheme,
        LocalMotionScheme provides motionScheme,
        LocalRippleConfiguration provides rippleConfiguration
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            content = content
        )
    }
}

/**
 * Accessor for Yalla design tokens inside a composable scope.
 *
 * Provides convenient access to color, font, and dark-mode state without
 * manually reading CompositionLocals. Must be used within a [YallaTheme].
 *
 * ## Usage
 *
 * ```kotlin
 * val textColor = System.color.text.base
 * val bodyStyle = System.font.body.base.medium
 * val isDarkMode = System.isDark
 * ```
 *
 * @since 0.0.1
 */
object System {
    /** Current [ColorScheme] provided by the nearest [YallaTheme]. */
    val color: ColorScheme
        @Composable
        get() = LocalColorScheme.current

    /** Current [FontScheme] provided by the nearest [YallaTheme]. */
    val font: FontScheme
        @Composable
        get() = LocalFontScheme.current

    /** Current [SpaceScheme] provided by the nearest [YallaTheme]. */
    val space: SpaceScheme
        @Composable
        get() = LocalSpaceScheme.current

    /** Current [RadiusScheme] provided by the nearest [YallaTheme]. */
    val radius: RadiusScheme
        @Composable
        get() = LocalRadiusScheme.current

    /** Current [MotionScheme] provided by the nearest [YallaTheme]. */
    val motion: MotionScheme
        @Composable
        get() = LocalMotionScheme.current

    /** Whether the current theme is dark mode. */
    val isDark: Boolean
        @Composable
        get() = LocalIsDark.current
}
