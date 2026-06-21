package uz.yalla.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import uz.yalla.design.color.ColorScheme
import uz.yalla.design.color.DarkColorScheme
import uz.yalla.design.color.LightColorScheme
import uz.yalla.design.color.LocalColorScheme
import uz.yalla.design.font.FontScheme
import uz.yalla.design.font.LocalFontScheme
import uz.yalla.design.font.rememberFontScheme
import androidx.compose.material3.darkColorScheme as materialDarkColorScheme
import androidx.compose.material3.lightColorScheme as materialLightColorScheme

internal val LocalIsDark = staticCompositionLocalOf { false }

/**
 * The single theming entry point: wrap your app (or a screen) in `YallaTheme` once, then read
 * tokens through [System] (`System.color.*`, `System.font.*`, `System.isDark`).
 *
 * @param isDark the appearance to render; defaults to the system setting. This is the single
 *   source of truth for appearance — colors, the ripple, and themed images all follow it.
 * @param colorScheme the color tokens to provide; defaults to the [isDark]-matching scheme. A
 *   custom scheme must agree with [isDark] (its `background.base` must match the matching default)
 *   so colors, ripple, and image variants can never silently diverge.
 * @param fontScheme the typography tokens to provide; defaults to [rememberFontScheme].
 * @param content the themed content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun YallaTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    colorScheme: ColorScheme = if (isDark) DarkColorScheme else LightColorScheme,
    fontScheme: FontScheme = rememberFontScheme(),
    content: @Composable () -> Unit
) {
    // Make the illegal state unrepresentable: a caller cannot pass a colorScheme whose appearance
    // disagrees with isDark, which would otherwise render dark colors with light images + ripple.
    val expectedScheme = if (isDark) DarkColorScheme else LightColorScheme
    require(colorScheme.background.base == expectedScheme.background.base) {
        "YallaTheme: colorScheme appearance disagrees with isDark=$isDark. Provide the matching " +
            "scheme so colors, ripple, and themed images stay consistent."
    }

    val rippleConfiguration =
        remember(isDark) {
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
        }

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
        LocalRippleConfiguration provides rippleConfiguration
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            content = content
        )
    }
}

/**
 * The canonical token surface. Read design tokens through `System` inside a [YallaTheme]:
 * `System.color.*` for colors, `System.font.*` for typography, and `System.isDark` for the current
 * appearance (e.g. to pick a themed image). Reading any of these outside a `YallaTheme` fails fast.
 */
public object System {
    /** The active [ColorScheme] (semantic color tokens). */
    public val color: ColorScheme
        @Composable
        get() = LocalColorScheme.current

    /** The active [FontScheme] (semantic typography tokens). */
    public val font: FontScheme
        @Composable
        get() = LocalFontScheme.current

    /** Whether the active theme is the dark appearance. */
    public val isDark: Boolean
        @Composable
        get() = LocalIsDark.current
}
