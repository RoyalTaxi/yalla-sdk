# Module design

> Visual language — color, font, motion, radius, space, theme.

## What this is

- **Token surfaces**: `ColorScheme`, `FontScheme`, `SpaceScheme`, `RadiusScheme`,
  `MotionScheme`. Each is an `@Immutable data class` with a default factory
  (`light()`/`dark()` for color, `rememberFontScheme()` for font,
  `standard*Scheme()` for space/radius/motion). All accessed inside Compose
  content via the `System` object — `System.color.text.base`,
  `System.font.body.base.medium`, `System.space.scale.medium`,
  `System.radius.medium`, `System.motion.duration.standard`, `System.isDark`.
- **Theme composable**: `YallaTheme(isDark, …)` wires the tokens into Compose's
  `CompositionLocal` mechanism (`LocalColorScheme`, `LocalFontScheme`,
  `LocalSpaceScheme`, `LocalRadiusScheme`, `LocalMotionScheme`, `LocalIsDark`)
  and bridges to Material3's `MaterialTheme` so M3 components inherit the
  Yalla palette automatically.
- **Themed images**: `ThemedImage` (enum of light/dark `DrawableResource`
  pairs) + `themedPainter(image)` — auto-selects the variant based on
  `System.isDark`, recomposes on theme switch.

## What this is NOT

- **Not** Material3 itself. `design` interops with M3 (so plain `Button`,
  `Card`, etc. inherit Yalla colors), but its public surface is the
  `System.*` token accessors and `YallaTheme`, not M3 directly.
- **Not** a UI primitive layer. Buttons, fields, dialogs, etc. live in
  `primitives` and `composites`.
- **Not** feature-specific copy or branding. Strings live in `resources`;
  product copy lives in YallaClient.
- **Not** a token-mutation API at runtime. Tokens are immutable; theme
  changes happen by re-providing a different `ColorScheme` or by toggling
  `isDark`, not by mutating an existing scheme.

## Usage

```kotlin
implementation("uz.yalla.sdk:design")
```

```kotlin
@Composable
fun WelcomeCard() = YallaTheme(isDark = false) {
    Box(modifier = Modifier.background(System.color.background.base)) {
        Text(
            text = "Welcome",
            color = System.color.text.base,
            style = System.font.title.large,
        )
    }
}
```

## Notes

- **Theme-bifurcation rule.** Only `text`, `background`, `border`, `button`,
  and `icon` color groups differ between `light()` and `dark()`. `accent`
  and `gradient` tokens are theme-invariant by design — same values both
  themes. Font/space/radius/motion are entirely theme-agnostic; there's no
  `lightFontScheme()`/`darkFontScheme()` because the product doesn't
  currently differentiate them. If that changes, the `Local*Scheme`
  CompositionLocals already provide the swap mechanism without an API change.
- **`System.font` vs the rest.** `System.font` is the only accessor that
  throws when read outside `YallaTheme` — `LocalFontScheme` has no static
  default because font loading needs a composable context. The other
  schemes have working defaults so previews render outside `YallaTheme`.
  Wrap previews in `YallaTheme` to avoid surprises.
- **Token visibility.** `Color.kt`'s 56 raw hex tokens (`LightTextBase` etc.)
  are `internal`. Consumers reach colors only via `System.color`. Same shape
  for the other Schemes — no public raw-token escape hatch. Re-introduce
  externally if a real consumer surfaces (none today).
- **`System.motion` consumer status.** `MotionScheme` ships per ADR-021
  (Chunk 0.C of the YallaClient refactor plan in
  `YallaClient/docs/superpowers/plans/2026-04-23-yalla-client-refactor.md`).
  It currently has zero callers anywhere in SDK or YallaClient. The surface
  is preserved against the YallaClient migration that will replace ad-hoc
  `tween(durationMillis = …)` / `spring(…)` calls with `System.motion.*`.
  If that migration doesn't land by the next phase boundary, revisit
  whether to keep, expand (haptic follow-up), or delete.
- **`Nummernschild` font.** Used in `FontScheme.Custom.carNumber` — the
  European license-plate styling for Yalla's vehicle ID display. Product-
  specific by design; bundled as a font resource (a brick), not as text
  (which would be an assembly).
- **Material3 bridge.** `YallaTheme` wraps a `MaterialTheme(...)` that maps
  Yalla's `ColorScheme` into M3's `darkColorScheme()`/`lightColorScheme()`
  builder so `androidx.compose.material3.Button` etc. work out of the box
  with Yalla colors. No M3 typography bridge — Yalla components use
  `System.font.*` directly.

## Depends on

- `resources` (api)
- `compose.runtime` (api)
- `compose.ui` (api)
- `compose.foundation` (implementation)
- `compose.material3` (implementation — Material3 bridge in `YallaTheme`)
- `compose.components.resources` (implementation — resource loading)
- No SDK-internal dep beyond `resources`.
