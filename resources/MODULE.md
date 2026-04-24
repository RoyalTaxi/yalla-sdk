# Module resources

> Shared icons, strings, drawables, and fonts for the Yalla SDK.

## What's here

- **Icons** — 70+ SVGs under `src/commonMain/valkyrieResources/`, compiled to `YallaIcons` via Valkyrie.
- **Strings** — `src/commonMain/composeResources/values*/strings.xml`. Locales: default, `en`, `ru`, `uz` (Latin), `be` (Uzbek Cyrillic, see Notes).
- **Fonts** — Inter, Roboto, SFPro, Nummernschild under `src/commonMain/composeResources/font/`.
- **Drawables** — PNG assets under `src/commonMain/composeResources/drawable/`.

## Public API

Auto-generated:

- `uz.yalla.resources.Res` — Compose Resources accessor (`Res.string.*`, `Res.drawable.*`, `Res.font.*`).
- `uz.yalla.resources.icons.YallaIcons` — `ImageVector` for every SVG under `valkyrieResources/`.

## Adding a new string

1. Add key + default translation to `values/strings.xml`.
2. Add translation to each `values-*` variant.
3. Rebuild; `Res.string.<key>` available in dependent modules.

## Adding a new icon

1. Drop SVG into `valkyrieResources/`.
2. Rebuild; `YallaIcons.<PascalCaseName>` available. Valkyrie regenerates from the directory.

## Notes

- `values-uz` (Uzbek Latin): generated via deterministic character-by-character Cyrillic→Latin transliteration (2019 orthography, U+02BB for ʼ/oʻ/gʻ). Review recommended before 1.0.
- `values-be` directory: retained because Compose Resources 1.10.0 doesn't support 4-letter BCP 47 script subtags. Rename to `values-uz-Cyrl` blocked on [CMP-7401](https://youtrack.jetbrains.com/issue/CMP-7401).
