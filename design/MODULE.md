# Module design

The Yalla design-token surface for Compose Multiplatform: a narrow, semantic facade over the
brand's colors, typography, and themed imagery that every component and screen reads from — without
ever touching a raw hex, a font resource, or a `CompositionLocal`.

## What lives here

- **Theme entry point** — [`YallaTheme`][uz.yalla.design.theme.YallaTheme]: wrap your app (or a
  screen) once; it provides the color, font, ripple, and appearance state.
- **Token surface** — [`System`][uz.yalla.design.theme.System]: the single read path
  (`System.color.*`, `System.font.*`, `System.isDark`), readable only inside a `YallaTheme`.
- **Color tokens** — [`ColorScheme`][uz.yalla.design.color.ColorScheme] and its role-grouped holders
  (`Text`, `Background`, `Border`, `Button`, `Icon`, `Accent`, `Gradient`).
- **Typography tokens** — [`FontScheme`][uz.yalla.design.font.FontScheme] and its holders (`Title`,
  `Body` with the regular/medium/bold triad, `Custom`).
- **Themed images** — [`ThemedImage`][uz.yalla.design.image.ThemedImage] (a light/dark pair) and
  [`rememberThemedPainter`][uz.yalla.design.image.rememberThemedPainter], which resolves it for the current
  appearance.

## Conventions

- **Tokens only:** read colors/typography exclusively through `System.color.*` / `System.font.*`;
  never hardcode a hex or reach for `MaterialTheme.colorScheme`/`typography`.
- **Single appearance source:** `YallaTheme(isDark = …)` is the source of truth for appearance.
  Colors, the ripple, and themed-image variants all follow `isDark`; a provided `colorScheme` must
  agree with it, so the three can never silently diverge.
- **Fail fast on misuse:** reading `System.color`/`System.font` outside a `YallaTheme` throws rather
  than rendering silently-wrong tokens.
- **Token provenance:** values originate from the `yalla-design` token source; the per-platform
  Kotlin
  is hand-finished afterwards (explicit-API modifiers, the iOS bridge `assetName`, the
  bounds-relative
  splash gradient) and is authoritative for this module until the emitter round-trips.

## Public contract

The published surface is pinned by the committed ABI dumps under `design/api/`
(`design.klib.api`, `android/design.api`); any diff is a reviewable breaking change. The internal
scheme factories, the backing `CompositionLocal`s, and the platform font handles are deliberately
`internal` — the only sanctioned surface is `YallaTheme`, `System`, the token holders,
`ThemedImage`, and `rememberThemedPainter`. KDoc on the public surface is the buyer-facing documentation.
