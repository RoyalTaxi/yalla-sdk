# Module resources

> Shared icons, strings, drawables, and fonts for the Yalla SDK.

## What's here

- **Icons** — synced from [`RoyalTaxi/yalla-resources`](https://github.com/RoyalTaxi/yalla-resources) into `src/commonMain/valkyrieResources/`, then compiled to `YallaIcons` via Valkyrie.
- **Strings** — generated from [`RoyalTaxi/yalla-resources`](https://github.com/RoyalTaxi/yalla-resources) into `src/commonMain/composeResources/values*/strings.xml`. Locales: default, `en`, `ru`, `uz` (Latin), `be` (Uzbek Cyrillic, see Notes).
- **Fonts** — synced from [`RoyalTaxi/yalla-resources`](https://github.com/RoyalTaxi/yalla-resources) into `src/commonMain/composeResources/font/`.
- **Drawables** — synced PNG assets under `src/commonMain/composeResources/drawable/`.
- **Files** — synced JSON assets under `src/commonMain/composeResources/files/`.

## Public API

Auto-generated:

- `uz.yalla.resources.Res` — Compose Resources accessor (`Res.string.*`, `Res.drawable.*`, `Res.font.*`).
- `uz.yalla.resources.icons.YallaIcons` — `ImageVector` for every SVG under `valkyrieResources/`.

## Adding a new string

1. Add the key and translations to `strings/catalog.json` in `yalla-resources`.
2. Run `python3 tools/yalla_resources.py sync` from the `yalla-resources` checkout.
3. Commit the generated `values*/strings.xml` changes here.
4. Rebuild; `Res.string.<key>` is available in dependent modules.

Do not edit generated `strings.xml` files by hand.

## Adding a new icon

1. Drop SVG into `assets/icons/` in `yalla-resources`.
2. Run `python3 tools/yalla_resources.py sync` from the `yalla-resources` checkout.
3. Commit the synced `valkyrieResources/` changes here.
4. Rebuild; `YallaIcons.<PascalCaseName>` is available. Valkyrie regenerates from the directory.

Do not edit synced icon files by hand.

## Adding a new drawable, font, or file

1. Add the source file under `assets/drawable/`, `assets/font/`, or `assets/files/` in `yalla-resources`.
2. Run `python3 tools/yalla_resources.py sync` from the `yalla-resources` checkout.
3. Commit the synced `composeResources/` changes here.
4. Rebuild; Compose Resources regenerates the corresponding `Res.drawable`, `Res.font`, or `Res.getUri()` accessors.

Do not edit synced asset files by hand.

## Notes

- `values-uz` (Uzbek Latin): generated via deterministic character-by-character Cyrillic→Latin transliteration (2019 orthography, U+02BB for ʼ/oʻ/gʻ). Review recommended before 1.0.
- `values-be` directory: retained because Compose Resources 1.10.0 doesn't support 4-letter BCP 47 script subtags. Rename to `values-uz-Cyrl` blocked on [CMP-7401](https://youtrack.jetbrains.com/issue/CMP-7401).
