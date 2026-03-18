/**
 * Raw color token definitions for the Yalla design system.
 *
 * This file contains the primitive hex color values used by [ColorScheme] to build
 * semantic color groups. Tokens are organized by theme (light / dark), followed by
 * shared accent colors and gradient brushes.
 *
 * These values should not be referenced directly from UI code — use [ColorScheme]
 * via `System.color` instead, so theme switching works automatically.
 *
 * @see ColorScheme
 * @see light
 * @see dark
 */
package uz.yalla.design.color

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// region Light Theme Colors

/** Primary text color for light theme. */
val LightTextBase = Color(0xFF101828)

/** Muted/secondary text color for light theme. */
val LightTextSubtle = Color(0xFF98A2B3)

/** Hyperlink text color for light theme. */
val LightTextLink = Color(0xFF562DF8)

/** Error/destructive text color for light theme. */
val LightTextRed = Color(0xFFF42500)

/** White text on dark/brand surfaces for light theme. */
val LightTextWhite = Color(0xFFFFFFFF)

/** Primary screen background for light theme. */
val LightBackgroundBase = Color(0xFFFFFFFF)

/** Brand-colored background for light theme. */
val LightBackgroundBrandBase = Color(0xFF562DF8)

/** Secondary surface background for light theme. */
val LightBackgroundSecondary = Color(0xFFF7F7F7)

/** Tertiary surface background for light theme. */
val LightBackgroundTertiary = Color(0xFFE9EAEA)

/** Disabled input border for light theme. */
val LightBorderDisabled = Color(0xFFE4E7EC)

/** Focused/filled input border for light theme. */
val LightBorderFilled = Color(0xFF101828)

/** White border for overlays in light theme. */
val LightBorderWhite = Color(0xFFFFFFFF)

/** Error border for light theme. */
val LightBorderError = Color(0xFFF42500)

/** Primary CTA button fill for light theme. */
val LightButtonActive = Color(0xFF562DF8)

/** Disabled primary button fill for light theme. */
val LightButtonDisabled = Color(0xFFC8CBFA)

/** Secondary button fill for light theme. */
val LightButtonSecondary = Color(0xFFF7F7F7)

/** Tertiary button fill for light theme. */
val LightButtonTertiary = Color(0xFF101828)

/** Disabled tertiary button fill for light theme. */
val LightButtonDisabledTertiary = Color(0xFFCBD2E1)

/** White icon tint for light theme. */
val LightIconWhite = Color(0xFFFFFFFF)

/** Primary icon tint for light theme. */
val LightIconBase = Color(0xFF101828)

/** Brand-accent icon tint for light theme. */
val LightIconSecondary = Color(0xFF562DF8)

/** Disabled icon tint for light theme. */
val LightIconDisabled = Color(0xFFC8CBFA)

/** Error/destructive icon tint for light theme. */
val LightIconRed = Color(0xFFF42500)

/** Muted icon tint for light theme. */
val LightIconSubtle = Color(0xFF98A2B3)

// endregion

// region Dark Theme Colors

/** Primary text color for dark theme. */
val DarkTextBase = Color(0xFFFFFFFF)

/** Muted/secondary text color for dark theme. */
val DarkTextSubtle = Color(0xFF747C8B)

/** Hyperlink text color for dark theme. */
val DarkTextLink = Color(0xFF562DF8)

/** Error/destructive text color for dark theme. */
val DarkTextRed = Color(0xFFF42500)

/** White text on dark/brand surfaces for dark theme. */
val DarkTextWhite = Color(0xFFFFFFFF)

/** Primary screen background for dark theme. */
val DarkBackgroundBase = Color(0xFF1A1A20)

/** Brand-colored background for dark theme. */
val DarkBackgroundBrandBase = Color(0xFF562DF8)

/** Secondary surface background for dark theme. */
val DarkBackgroundSecondary = Color(0xFF21222B)

/** Tertiary surface background for dark theme. */
val DarkBackgroundTertiary = Color(0xFF1D1D26)

/** Disabled input border for dark theme. */
val DarkBorderDisabled = Color(0xFF383843)

/** Focused/filled input border for dark theme. */
val DarkBorderFilled = Color(0xFFFFFFFF)

/** White border for overlays in dark theme. */
val DarkBorderWhite = Color(0xFFFFFFFF)

/** Error border for dark theme. */
val DarkBorderError = Color(0xFFF42500)

/** Primary CTA button fill for dark theme. */
val DarkButtonActive = Color(0xFF562DF8)

/** Disabled primary button fill for dark theme. */
val DarkButtonDisabled = Color(0xFF2C2D34)

/** Secondary button fill for dark theme. */
val DarkButtonSecondary = Color(0xFFF7F7F7)

/** Tertiary button fill for dark theme. */
val DarkButtonTertiary = Color(0xFFFFFFFF)

/** Disabled tertiary button fill for dark theme. */
val DarkButtonDisabledTertiary = Color(0xFFCBD2E1)

/** White icon tint for dark theme. */
val DarkIconWhite = Color(0xFFFFFFFF)

/** Primary icon tint for dark theme. */
val DarkIconBase = Color(0xFFFFFFFF)

/** Brand-accent icon tint for dark theme. */
val DarkIconSecondary = Color(0xFF562DF8)

/** Disabled icon tint for dark theme. */
val DarkIconDisabled = Color(0xFF2C2D34)

/** Error/destructive icon tint for dark theme. */
val DarkIconRed = Color(0xFFF42500)

/** Muted icon tint for dark theme. */
val DarkIconSubtle = Color(0xFF98A2B3)

// endregion

// region Accent Colors

/** Vibrant pink accent for highlights and badges. */
val PinkSun = Color(0xFFFF234B)

/** Soft pink accent for decorative elements. */
val Color1 = Color(0xFFFFD1D2)

/** Soft blue accent for decorative elements. */
val Color2 = Color(0xFFCADDFF)

/** Warm amber accent for decorative elements. */
val Color3 = Color(0xFFFFDFAB)

/** Aqua/teal accent for decorative elements. */
val Color4 = Color(0xFFA7FFF9)

/** Soft purple accent for decorative elements. */
val Color5 = Color(0xFFD6C8FF)

// endregion

// region Gradients

/** Vertical purple gradient for splash and loading screens. */
val SplashBackground =
    Brush.verticalGradient(
        colors =
            listOf(
                Color(0xFF7957FF),
                Color(0xFF562DF8),
                Color(0xFF3812CE)
            ),
        startY = 0f,
        endY = 1000f
    )

/** Diagonal pink-to-purple gradient for promotional and decorative elements. */
val SunsetNight = Brush.linearGradient(listOf(Color(0xFFFF234B), Color(0xFF2F00EC)))

// endregion
