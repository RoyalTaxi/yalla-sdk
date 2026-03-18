# Module design

Yalla SDK design system — colors, typography, themed images, and theme composition.

Provides the visual foundation for all Yalla UI components through a structured token system
accessible via the [System][uz.yalla.design.theme.System] object.

## Architecture

The design module follows a token-based design system:
- **Color tokens** ([Color.kt][uz.yalla.design.color]) — raw hex values for light and dark themes
- **Color scheme** ([ColorScheme][uz.yalla.design.color.ColorScheme]) — semantic grouping of colors by purpose
- **Typography** ([FontScheme][uz.yalla.design.font.FontScheme]) — structured text styles with weight variants
- **Theme** ([YallaTheme][uz.yalla.design.theme.YallaTheme]) — composable that wires everything together

# Package uz.yalla.design.color

Color tokens and scheme definitions for light and dark themes.
Provides [ColorScheme] with semantic groups: text, background, border, button, icon, accent, and gradient.

# Package uz.yalla.design.font

Typography definitions and platform-specific font loading.
Provides [FontScheme] with title, body (with weight variants), and custom styles.

# Package uz.yalla.design.theme

Theme composable ([YallaTheme]) and design token accessor ([System]).
Bridges Yalla's design system with Material3 for interoperability.

# Package uz.yalla.design.image

Theme-aware image resources with automatic light/dark variant selection via [ThemedImage] and [themedPainter].
