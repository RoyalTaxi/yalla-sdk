# Module primitives

Reusable Compose Multiplatform UI primitives for the Yalla SDK.

This module provides the building blocks for Yalla application screens — buttons, fields,
indicators, pins, top bars, OTP inputs, and text transformations. Each component follows the
gold standard `Colors` + `Dimens` + `Defaults` pattern for full theme customization.

## Architecture

Components follow the **Colors + Dimens + Defaults** pattern:
- **`{Component}Colors`** — `@Immutable` data class holding all color values
- **`{Component}Dimens`** — `@Immutable` data class holding all dimension/shape values
- **`{Component}Defaults`** — object with factory functions (`colors()`, `dimens()`) that read the
  current theme via [System][uz.yalla.design.theme.System] and return overridable config objects
- **Composable function** — stateless, accepts parameters in order: required → modifier → behavioral → styling → slots → content

See [COMPONENT_STANDARD.md](../COMPONENT_STANDARD.md) for the full gold standard specification.

# Package uz.yalla.primitives.button

Button components for primary, secondary, gradient, text, icon, navigation, gender selection,
bottom sheet, support, location enablement, and countdown-sensitive actions.

# Package uz.yalla.primitives.dialog

Modal dialogs including [LoadingDialog] for blocking loading states.

# Package uz.yalla.primitives.field

Text input fields: [PrimaryField] (outlined), [NumberField] (phone input), and [DateField]
(read-only date picker trigger).

# Package uz.yalla.primitives.indicator

Progress and loading indicators: [DotsIndicator] for pagers, [LoadingIndicator] spinner,
[StripedProgressBar] animated progress bar, and [SplashOverlay] full-screen loading overlay.

# Package uz.yalla.primitives.model

Shared enums and value types used across primitives, including [ButtonSize].

# Package uz.yalla.primitives.navigation

Backward-compatibility type aliases for toolbar actions re-exported from the `platform` module.

# Package uz.yalla.primitives.otp

OTP and PIN code input components: [PinRow] (row of digit boxes) and [PinView] (single digit field).

# Package uz.yalla.primitives.pin

Map pin composables: [LocationPin] (animated pin with address label and timeout) and
[SearchPin] (Lottie-based search animation).

# Package uz.yalla.primitives.topbar

Top bar composables: [TopBar] (standard) and [LargeTopBar] (prominent title variant).

# Package uz.yalla.primitives.transformation

Visual transformations for text fields: [PhoneVisualTransformation], [NumberVisualTransformation],
and [MaskFormatter] utility.

# Package uz.yalla.primitives.util

Utility extensions: [squareSize] layout modifier and [GenderKind.resource] string resource mapping.

## Depends on

- `design` (api — `System.color.*` / `System.font.*` token surface)
- `resources` (api — `StringResource` + `YallaIcons` in public composables)
- `compose.runtime` (api — `@Composable`)
- `compose.ui` (api — `Modifier`, `ImageVector`, `Color`, `Shape`, `Dp`)
- `compose.foundation` (api — layout primitives in public surfaces)
- `compose.material3` (api — `Surface`, `Icon` in public composables)
- `core` (implementation — domain enums for option types)
- `platform` (implementation — `NativeLoadingIndicator` etc.)
- `compose.components.resources`, `compose.ui.tooling.preview`, `compottie`, `compottie.resources`, `constraintlayout` (implementation — internal-only)
- No SDK-internal dep beyond `core` / `design` / `resources` / `platform`.
