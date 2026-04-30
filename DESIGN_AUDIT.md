# DESIGN_AUDIT.md

Audit output for wave 1 of phase-3 `design` cleanup. Drives waves 2-10. Findings keyed to `CLEANUP_CRITERIA.md`. All paths absolute under `/Users/islom/StudioProjects/yalla-sdk/`.

---

## 1. AI-blob deletions (criterion 2)

### `design/src/commonMain/kotlin/uz/yalla/design/color/Color.kt`

- **2-1** lines 1-14 — file-header KDoc on the package. The "should not be referenced directly from UI code — use [ColorScheme] via `System.color` instead" sentence carries info; the rest is a "raw color token definitions … primitive hex color values" preamble. Trim to one sentence. (~2 min)
- **2-2** lines 23, 26, 29, 32, 35, 38, 41, 44, 47, 50, 53, 56, 59, 62, 65, 68, 71, 74, 77, 80, 83, 86, 89, 92, 99, 102, 105, 108, 111, 114, 117, 120, 123, 126, 129, 132, 135, 138, 141, 144, 147, 150, 153, 156, 159, 162, 165, 168, 175, 178, 181, 184, 187, 190, 197, 210 — every single per-token KDoc is a one-liner restating the property name. `/** Primary text color for light theme. */` above `LightTextBase`, `/** Muted/secondary text color for light theme. */` above `LightTextSubtle`, repeated 56× for every Light/Dark/Accent/Gradient declaration. Pure paraphrase. **Delete all 56 per-token KDocs.** (~10 min)
- **2-4** lines 21-213 (entire file beyond package decl) — verified via `grep -rn "\b<token>\b"` for every public token in the file: **every single raw token (`LightTextBase`, `LightTextSubtle`, … `DarkIconSubtle`, `PinkSun`, `Color1`-`Color5`, `SplashBackground`, `SunsetNight`) is referenced only by `ColorScheme.kt` (the `light()` / `dark()` factories) and `ColorSchemeTest.kt`/`YallaThemeTest.kt`.** Zero consumers anywhere in the SDK or YallaClient. The intended public surface is `System.color.text.base`-style accessor; the raw tokens are an implementation detail of the schemes. **Demote ALL tokens to `internal`** (or move to a `private`/`internal` companion in `ColorScheme.kt`). Removes 47 public symbols from the API surface and ~140 lines of file-internal exposure. Behavior-preserving. (~30 min — also touches `LightTextBase`/`DarkTextBase` references in `YallaThemeTest.kt:7-8` if kept public for tests, easiest to make tests assert the scheme accessors directly.)

### `design/src/commonMain/kotlin/uz/yalla/design/color/ColorScheme.kt`

- **2-1** line 37 — `@since 0.0.1` on `ColorScheme` class. (~30 sec)
- **2-1** lines 30-37 — class KDoc properties block (`@property text Text color tokens.` … `@property gradient Brush-based gradient tokens.`). Each `@property` paraphrases the field name. The class-level paragraph (lines 8-29) carries info; the property block doesn't. (~3 min)
- **2-1** lines 48-57, 66-74, 82-90, 98-107, 116-126, 136-146, 156-162 — KDoc on each nested data class (`Text`, `Background`, `Border`, `Button`, `Icon`, `Accent`, `Gradient`). Most `@property` blocks paraphrase (`@property base Primary text color — headings, body, labels.`); a few carry distinguishing info (`@property base Primary screen/card background.` vs. `@property brand Brand-colored background (e.g. promotional banners).` is borderline). Sweep the obvious paraphrase, keep the disambiguating ones (`Border.disabled` vs `Border.filled` is OK; `Accent.color1`-`color5` is paraphrase since the names are already opaque). (~10 min)
- **2-1** line 56, 73, 89, 106, 125, 145, 161 — `@since 0.0.1` ceremony tags on every nested data class. (~2 min)
- **2-1** lines 169-176 — `light()` factory KDoc. The "Maps light-mode raw color tokens from [Color.kt][uz.yalla.design.color] into the semantic [ColorScheme] structure" sentence is paraphrase of the body. The "Accent and gradient tokens are shared across themes" line is the only info-dense bit. (~2 min)
- **2-1** line 176 — `@since 0.0.1` on `light()`. (~30 sec)
- **2-1** lines 227-234 — `dark()` factory KDoc. Same shape. (~2 min)
- **2-1** line 234 — `@since 0.0.1` on `dark()`. (~30 sec)
- **2-1** lines 285-292 — `LocalColorScheme` KDoc. The "Defaults to [light] theme. Overridden by [YallaTheme]" line is info-dense; the second sentence repeats the first. (~1 min)
- **2-1** line 291 — `@since 0.0.1`. (~30 sec)

### `design/src/commonMain/kotlin/uz/yalla/design/font/Font.kt`

- **2-1** lines 12-19, 22-29, 32-39 — KDoc on each `expect val` (`boldFont`, `mediumFont`, `normalFont`). The "Each platform (Android / iOS) provides the actual font file." sentence repeats across all three. The "Used by [FontScheme.Title] styles and [FontScheme.Body.Weighty.bold]" cross-ref is the only useful bit; collapse to a single class-level KDoc + drop the per-`expect val` blocks. (~5 min)
- **2-1** lines 18, 28, 38, 52 — four `@since 0.0.1` ceremony tags. (~2 min)
- **2-1** lines 42-52 — `rememberFontScheme` KDoc. The "Builds all title, body, and custom text styles using the platform-specific font resources" line is paraphrase of the body. The "this composable is called by [YallaTheme]" line is info-dense; keep that. (~2 min)

### `design/src/commonMain/kotlin/uz/yalla/design/font/FontScheme.kt`

- **2-1** lines 27-30 — `@property title Title/heading text styles.` block paraphrases. The class-level paragraph (lines 7-26) is info-dense (the Usage block). (~2 min)
- **2-1** line 30 — `@since 0.0.1` on `FontScheme`. (~30 sec)
- **2-1** lines 37-43 — `Title` data class KDoc (`@property xLarge Extra-large title — 30sp, splash headers.` etc.). Each `@property` adds the size hint, which is borderline — the size lives in the actual `TextStyle` and surfacing it in KDoc creates a stale-info risk if values diverge. Borderline. **Recommend trim** since the actual values live in `Font.kt:60-77`. (~2 min)
- **2-1** line 43, 58, 75, 88, 101 — five `@since 0.0.1` ceremony tags. (~2 min)
- **2-1** lines 51-58 — `Body` data class KDoc. Same shape, same recommendation as Title. (~2 min)
- **2-1** lines 66-75 — `Weighty` KDoc paraphrases. Headline paragraph carries info; per-`@property` doesn't. (~2 min)
- **2-1** lines 84-88 — `Custom` KDoc. `@property carNumber License plate style — 12sp, Nummernschild font.` is the disambiguating line; class header is paraphrase. (~1 min)
- **2-1** lines 95-101 — `LocalFontScheme` KDoc. The "Has no default value — throws if accessed outside a [YallaTheme]. This is intentional" paragraph is info-dense. Keep. (~0 min)
- **2-1** line 101 — `@since 0.0.1`. (~30 sec)
- **2-4** lines 108-141 — **`FontScheme.Body.numeric` extension property.** Verified via `grep -rn "numeric\|FONT_FEATURE_TABULAR_NUMERALS"` across SDK + YallaClient: **zero callers anywhere outside its own definition.** The KDoc shows an "Animated price display" use case but no consumer exists yet. ~30 lines of speculative public API. **Bucket 2-4 (dead code) and bucket 2-5 (speculative generalization).** (~5 min decision; **flag for Islom** — same shape as `core` G2 keep-pending-consumer.)
- **2-1** line 138 — `@since 0.0.15`. (~30 sec)

### `design/src/commonMain/kotlin/uz/yalla/design/image/ThemedImage.kt`

- **2-1** lines 30-49 — class KDoc on the `enum class ThemedImage`. The Usage block carries info; the `@property light Drawable resource for light mode.` and `@property dark Drawable resource for dark mode.` block is pure paraphrase. (~2 min)
- **2-1** line 48 — `@since 0.0.1`. (~30 sec)
- **2-2** lines 54, 57, 60, 63, 66, 69, 72, 75, 78, 81, 84, 87 — every entry has a one-liner KDoc that restates the entry name with "illustration" / "icon" suffix. `/** Login screen illustration. */` above `Login(...)`. 12 lines of comment redundancy. (~3 min)

### `design/src/commonMain/kotlin/uz/yalla/design/image/ThemedPainter.kt`

- **2-1** lines 8-23 — `themedPainter` KDoc. The Usage block carries info; the "Returns a [Painter] for the given [ThemedImage], automatically selecting the light or dark drawable variant based on the current theme" sentence paraphrases the function body. (~2 min)
- **2-1** line 23 — `@since 0.0.1`. (~30 sec)

### `design/src/commonMain/kotlin/uz/yalla/design/motion/MotionScheme.kt`

- **2-1** lines 51-58 — `@property duration` / `@property easing` / `@property spring` / `@property stagger` block on `MotionScheme`. The class-level paragraphs (lines 13-49) are information-dense — the Usage block, the ADR-021 "values inside a token vs. shape of a token" semi-stable rule. Keep the body, drop the property paraphrase block. (~3 min)
- **2-1** line 59, 81, 103, 124, 149, 166, 206 — seven `@since 0.0.17` ceremony tags. (~3 min)
- **2-1** lines 68-81, 92-103, 113-124, 134-149 — KDoc on each nested data class (`Duration`, `Easing`, `Spring`, `Stagger`). Headline paragraphs carry info ("instant is feedback-level (under perception threshold for animation)" etc.); the `@property` blocks below paraphrase ("100ms — state changes so fast they feel like feedback"). Borderline — the duration values *are* in the property descriptions (`100ms`, `200ms`, etc.) which surfaces stale-info risk if `standardMotionScheme()` diverges from the doc. **Recommend trim** since the values are at lines 169-196. (~5 min)
- **2-1** lines 159-166 — `standardMotionScheme()` factory KDoc. "Values match the catalog in the YallaClient refactor spec (section 9) and ADR-021" — references a spec/ADR; `docs/adr/` is gone per criterion 5 / CLAUDE.md. Same stale meta-commentary as `core/error/DataError.kt:28-31` ADR-022 finding. (~2 min)
- **2-1** lines 199-206 — `LocalMotionScheme` KDoc. "Defaults to [standardMotionScheme] so composables that render outside a [YallaTheme]…" — info-dense; keep. (~0 min)
- **2-4** entire file — verified via `grep -rn "MotionScheme\|standardMotionScheme\|LocalMotionScheme\|System\.motion"`: **zero callers across SDK + YallaClient outside design itself.** The four KDoc samples within the file are the only references. 208 lines of speculative public API. **Bucket 2-4 + bucket 2-5.** Two readings:
  1. **Truly dead** — `MotionScheme` was added recently (`@since 0.0.17`), shipped with the catalog but no consumer migrated yet; YallaClient uses raw `tween` / `spring` calls. Delete the module.
  2. **Staged** — values live in spec; consumers haven't caught up. Keep.
  Same shape as `core` G2. **Flag for Islom; recommend keep-with-caveat** since the YallaClient refactor spec referenced in the KDoc is real product intent, but **this is the longest dead-code surface in the audit (208 lines)**. (~30 min decision)

### `design/src/commonMain/kotlin/uz/yalla/design/radius/RadiusScheme.kt`

- **2-1** lines 33-39 — `@property xs 4.dp — input fields, tight elements.` block on `RadiusScheme`. Same stale-info risk as MotionScheme: values live both in KDoc and in `standardRadiusScheme()` body. **Recommend trim.** (~2 min)
- **2-1** line 39, 56, 74 — three `@since 0.0.13` ceremony tags. (~1 min)
- **2-1** lines 50-56 — `standardRadiusScheme()` factory KDoc. "Standard Yalla [RadiusScheme] with t-shirt sizes mapped to conventional values" paraphrases the body. (~1 min)
- **2-1** lines 67-74 — `LocalRadiusScheme` KDoc. Same as `LocalMotionScheme`; the "Defaults to … so tokens resolve even outside a [YallaTheme]" line is info-dense. Keep. (~0 min)

### `design/src/commonMain/kotlin/uz/yalla/design/space/SpaceScheme.kt`

- **2-1** lines 34-42 — `@property` block on `SpaceScheme`. Same stale-info risk; values also live in `standardSpaceScheme()` body. **Recommend trim.** (~3 min)
- **2-1** line 42, 71, 96, 128 — four `@since 0.0.13` ceremony tags. (~2 min)
- **2-1** lines 54-71 — `Scale` data class KDoc with `@property xxs 2.dp — hairline gaps, dividers.` etc. Same shape, same recommendation. (~3 min)
- **2-1** lines 87-96 — `standardSpaceScheme()` factory KDoc. The "## Usage" callout block (lines 89-93) is keeper; the framing paragraph paraphrases. (~1 min)
- **2-1** lines 120-128 — `LocalSpaceScheme` KDoc. Like the other `Local*Scheme` blocks; keep the "tokens accessible even outside a [YallaTheme]" sentence, drop ceremony. (~0 min)

### `design/src/commonMain/kotlin/uz/yalla/design/theme/Theme.kt`

- **2-1** lines 35-67 — `YallaTheme` KDoc. The Usage block carries info; the "Sets up [ColorScheme], [FontScheme], and ripple configuration, then bridges them into Material3's [MaterialTheme]" line is paraphrase of the body. The "Material3 color scheme mapping exists so standard M3 components (TextField, Button, etc.) pick up Yalla brand colors without additional configuration" is info-dense — keep that. The `@param` block (lines 61-66) is largely paraphrase. (~5 min)
- **2-1** line 67 — `@since 0.0.1` on `YallaTheme`. (~30 sec)
- **2-1** lines 135-149 — `System` object KDoc. The Usage block carries info; the "Provides convenient access to color, font, and dark-mode state without manually reading CompositionLocals" is paraphrase. (~2 min)
- **2-1** line 149 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 152-180 — per-property KDoc on each `System.color`/`System.font`/etc accessor. Each is `/** Current [ColorScheme] provided by the nearest [YallaTheme]. */`-shape paraphrase. **Delete all six.** (~2 min)
- **2-1** line 32 — `private val LocalIsDark` has a one-liner that paraphrases the name. (~30 sec)

### `design/src/androidMain/kotlin/uz/yalla/design/font/Font.android.kt`

- **2-1** lines 9, 12, 15 — three one-liner KDocs (`/** Android actual: Roboto Bold. */` etc.). Comment redundancy — the property name + actual value is already self-describing. Drop. (~1 min)

### `design/src/iosMain/kotlin/uz/yalla/design/font/Font.ios.kt`

- **2-1** lines 9, 12, 15 — same shape (`/** iOS actual: SF Pro Bold. */`). (~1 min)

### Cross-cutting bucket counts (design only)

- **2-1 (paraphrase / ceremony):** ~25 KDoc blocks affected; ~21 `@since` tags total across commonMain. ~150-180 lines of paraphrase across `Color.kt`, `ColorScheme.kt`, `FontScheme.kt`, `Font.kt`, `MotionScheme.kt`, `RadiusScheme.kt`, `SpaceScheme.kt`, `Theme.kt`, `ThemedImage.kt`, `ThemedPainter.kt`, plus the platform-actual one-liners. Wave-2 sweep, **~50 min total**.
- **2-2 (comment redundancy):** 56 per-token one-liners in `Color.kt` + 12 per-entry one-liners in `ThemedImage.kt`. ~70 lines total. Wave-2 sweep, **~15 min**.
- **2-3 (single-use abstractions):** none. Every public type (`ColorScheme`, `FontScheme`, `MotionScheme`, `RadiusScheme`, `SpaceScheme`, `ThemedImage`, `System`) is consumed by at least one other module. No interfaces with one impl, no factory wrappers around single constructors.
- **2-4 (dead code):** **two findings, both flagged for Islom.**
  1. **`FontScheme.Body.numeric` extension property** (`FontScheme.kt:108-141`) — zero callers anywhere. ~30 lines.
  2. **Entire `motion/` package** (`MotionScheme.kt` 208 lines + `LocalMotionScheme` + `standardMotionScheme()`) — zero callers anywhere. **The longest dead-code surface in the audit.**
  3. **All raw color tokens in `Color.kt`** are unused outside the `ColorScheme.kt` factories and tests — **demote to `internal`** rather than delete (the values are still load-bearing inside the schemes).
- **2-5 (speculative generalization):** the `MotionScheme.Spring` `bouncy`/`gentle`/`snappy`/`stiff` slot is a four-preset configuration knob with one default value (`standardMotionScheme()` provides the only consumer of the data class as well). Rolls into the 2-4 finding above; if `MotionScheme` is kept, the four-preset shape is fine; if deleted, no decision needed.

---

## 2. Module dependency graph (criterion 4)

`design/build.gradle.kts` declarations:

| line | declaration | scope | libs key |
| ---- | ----------- | ----- | -------- |
| 8 | `api(projects.resources)` | commonMain | `resources` (SDK-internal) |
| 9 | `implementation(compose.runtime)` | commonMain | `compose-runtime` |
| 10 | `implementation(compose.foundation)` | commonMain | `compose-foundation` |
| 11 | `implementation(compose.material3)` | commonMain | `compose-material3` |
| 12 | `implementation(compose.ui)` | commonMain | `compose-ui` |
| 13 | `implementation(compose.components.resources)` | commonMain | `compose-components-resources` |
| 17 | `implementation(kotlin("test"))` | commonTest | — |
| 19 | `implementation(compose.uiTest)` | commonTest | `compose-uiTest` |
| 23 | `implementation(compose.uiTooling)` | androidMain | `compose-uiTooling` |
| 24 | `implementation(libs.androidx.core.ktx)` | androidMain | `androidx-core-ktx` |

### Verification grep results — `api()` exposure check

For each `api()` declaration, I grep'd `design/src/commonMain` for any reference to symbols from that lib in a public type signature.

- **`projects.resources`** (line 8) — `Res` (drawable accessor) is referenced in `ThemedImage.kt:55-88` enum entries (`Res.drawable.img_light_blurry_logo` etc.). `DrawableResource` is the public type of `ThemedImage.light` / `.dark` (lines 51-52). `FontResource` is the public type of `boldFont`/`mediumFont`/`normalFont` `expect val`s (`Font.kt:20, 30, 40`). All three are types from `compose.components.resources` (Compose Multiplatform Resources), but they're surfaced through `:resources`'s public re-exports of the auto-generated `Res` accessors. **`api(projects.resources)` is correct because `ThemedImage.entries[*].light: DrawableResource` and `expect val boldFont: FontResource` are public types consumers must compile against.** Keep `api`.

### Verification grep results — `implementation()` exposure check

For each `implementation()` declaration, verified that no public type signature in `design/src/commonMain` exposes types from that lib.

- **`compose.runtime`** (line 9) — `@Composable`, `@Immutable`, `staticCompositionLocalOf`, `CompositionLocalProvider` are used. `staticCompositionLocalOf<T>` returns `ProvidableCompositionLocal<T>` which is the *type* of the public `LocalColorScheme`, `LocalFontScheme`, `LocalSpaceScheme`, `LocalRadiusScheme`, `LocalMotionScheme` vals. `@Composable` annotation appears on the public `themedPainter`, `rememberFontScheme`, `YallaTheme`, and every `System.*` accessor. **`compose.runtime` types are in public signatures.** Should be `api`, not `implementation`. **Promote to `api`.** (~5 min) Without this, downstream modules (`primitives`, `composites`, YallaClient) likely already declare `compose.runtime` themselves so the leak is invisible — but the dependency graph should be honest. Verify by attempting to compile a downstream module that doesn't declare `compose.runtime` against demoted design; should fail.
- **`compose.foundation`** (line 10) — only `isSystemInDarkTheme()` is imported (`Theme.kt:3`). It's called once inside `YallaTheme`'s default param (line 72: `isDark: Boolean = isSystemInDarkTheme()`). The default param is a function call; the param type is just `Boolean`. **Not in any public type signature.** Keep `implementation`. The ~6 MB foundation lib being a transitive `implementation` of design feels heavy for one default param; flag — but no action.
- **`compose.material3`** (line 11) — `MaterialTheme`, `LocalRippleConfiguration`, `RippleConfiguration`, `ExperimentalMaterial3Api`, `lightColorScheme`, `darkColorScheme` all used inside `YallaTheme`'s body. None in public type signatures. Keep `implementation`.
- **`compose.ui`** (line 12) — `Color` (graphics) is the type of every `LightTextBase`/`DarkTextBase`/etc. raw token (`Color.kt`). `TextStyle` is the type of every field in `FontScheme.Title`, `FontScheme.Body.Weighty`, `FontScheme.Custom`. `Dp` is the type of every field in `RadiusScheme` and `SpaceScheme.Scale`. `Brush` is in `ColorScheme.Gradient.splash`/`.sunsetNight`. `Painter` is the return type of `themedPainter()`. `Easing`, `SpringSpec<Float>` are in `MotionScheme.Easing`/`.Spring`. **`compose.ui` types saturate the public API.** Should be `api`, not `implementation`. **Promote to `api`.** (~5 min) Same downstream-already-declares pattern as `compose.runtime`; flip for honesty.
- **`compose.components.resources`** (line 13) — `Font(...)` factory (used in `Font.kt:63` etc.), `painterResource(...)` (used in `ThemedPainter.kt:28`), `DrawableResource` (public type of `ThemedImage.light`/`.dark`), `FontResource` (public type of `boldFont`/`mediumFont`/`normalFont`). The `DrawableResource`/`FontResource` types are re-exported via `:resources`'s `api` already (because `:resources` declares `api(compose.components.resources)`). The redundant `implementation` on design's side is correct because we use the `Font(...)` and `painterResource(...)` *functions* internally too — those aren't surfaced by `:resources`'s API. Keep `implementation`.

### Verification grep results — `implementation()` usage check

For each declared dep, verified actual usage.

- **`kotlin("test")`** (line 17) — used by every test file. Keep.
- **`compose.uiTest`** (line 19) — `runComposeUiTest`, `ExperimentalTestApi` used in `YallaThemeTest.kt:4-5`. Keep.
- **`compose.uiTooling`** (androidMain line 23) — `grep -rn "import androidx\.compose\.ui\.tooling\|@Preview" design/src/androidMain` returns **zero matches.** Declared but never imported. **Drop the dep entirely.** (~2 min) (Convention plugin's design intent for `compose.uiTooling` is to enable `@Preview` in androidMain, but design has no androidMain composables — only `Font.android.kt` with three FontResource constants.)
- **`androidx.core.ktx`** (androidMain line 24) — `grep -rn "import androidx\.core" design/src/androidMain` returns **zero matches.** Declared but never imported. **Drop the dep entirely.** (~2 min)

### Recommended `Depends on` block for `design/MODULE.md`

```
## Depends on

- `resources` — `Res.drawable.*`, `Res.font.*`, `DrawableResource`, `FontResource` 
  in `ThemedImage` enum, `boldFont`/`mediumFont`/`normalFont` `expect val`s, and 
  the bundled Nummernschild font for `FontScheme.Custom.carNumber`.
- `compose-runtime` — `@Composable`, `@Immutable`, `CompositionLocal` types in 
  the public `Local*Scheme`, `themedPainter`, `rememberFontScheme`, `YallaTheme`, 
  `System` surface.
- `compose-ui` — `Color`, `TextStyle`, `Dp`, `Brush`, `Painter`, `Easing`, 
  `SpringSpec` in the public scheme types, `themedPainter` return, motion specs.

Internal-only (`implementation`-scoped):
`compose-foundation` (single `isSystemInDarkTheme()` call inside `YallaTheme`),
`compose-material3` (M3 bridge inside `YallaTheme`),
`compose-components-resources` (Font(...) / painterResource(...) helpers).

Drop: `compose-uiTooling`, `androidx-core-ktx` (androidMain — declared, never used).
```

(Promote `compose.runtime` and `compose.ui` from `implementation` to `api`. Drop `compose.uiTooling` and `androidx.core.ktx` entirely. The four remaining `implementation()` lines stay as-is.)

### SDK-internal deps confirmation

- One SDK-internal dep: `:resources`. As expected — `design` is the visual brick layer and consumes raw `:resources` outputs.
- No cycles. design → resources is a one-way edge; resources has no SDK-internal deps.
- No surprising imports.

---

## 3. Restructure candidates (criterion 9-3)

### `wc -l` summary (commonMain + platforms)

```
293  commonMain/color/ColorScheme.kt          (longest)
213  commonMain/color/Color.kt
208  commonMain/motion/MotionScheme.kt
181  commonMain/theme/Theme.kt
160  commonMain/font/Font.kt
150  commonMain/font/FontScheme.kt
130  commonMain/space/SpaceScheme.kt
 89  commonMain/image/ThemedImage.kt
 76  commonMain/radius/RadiusScheme.kt
 29  commonMain/image/ThemedPainter.kt
 16  androidMain/font/Font.android.kt
 16  iosMain/font/Font.ios.kt
```

### God-file candidates (>300 lines or >5 distinct responsibilities)

- **No file >300 lines.** Criterion 11's god-file threshold not triggered. `ColorScheme.kt` at 293 is the closest, but it's a single concern (semantic color scheme + Light/Dark factories + CompositionLocal) and ~140 lines of that is the two factory bodies which are necessarily long because they enumerate every token slot.
- **`Color.kt` (213 lines)** is 47 raw token declarations + 2 brushes + 56 paraphrase comments — all one concern (raw color values). Not a god file. The 56 paraphrase KDocs are bucket 2-2 (section 1), not a structural problem.
- **`MotionScheme.kt` (208 lines)** — five top-level types/values (`MotionScheme`, four nested data classes, `standardMotionScheme()`, `LocalMotionScheme`). One concern (motion tokens). Not a god file. (If the file is dropped per section 1's bucket 2-4 finding, this finding is moot.)
- **`Theme.kt` (181 lines)** — `YallaTheme` composable (the canonical root), `System` object (the token accessor), `LocalIsDark` (private CompositionLocal). Three responsibilities, all tightly coupled (the `System` accessors read the locals that `YallaTheme` provides). Cohesive. Not a god file. Keep as-is.

### Nested-package check

- `design/src/commonMain/kotlin/uz/yalla/design/`
  - `color/` — 2 files: `Color.kt` (raw tokens), `ColorScheme.kt` (semantic scheme + factories + Local). Cohesive.
  - `font/` — 2 files: `Font.kt` (`expect val` font resources + `rememberFontScheme()`), `FontScheme.kt` (data class + Local + numeric extension). Cohesive.
  - `image/` — 2 files: `ThemedImage.kt` (enum), `ThemedPainter.kt` (the composable resolver). Cohesive.
  - `motion/` — 1 file: `MotionScheme.kt`. **Single-file package.** The prompt asks about this specifically.
  - `radius/` — 1 file: `RadiusScheme.kt`. **Single-file package.**
  - `space/` — 1 file: `SpaceScheme.kt`. **Single-file package.**
  - `theme/` — 1 file: `Theme.kt`. **Single-file package.**

The four single-file packages (`motion/`, `radius/`, `space/`, `theme/`) are NOT organization-only nesting — each carries a coherent token type. `motion/MotionScheme.kt` could plausibly grow (if e.g. consumer-specific motion presets are added later), and the per-package convention matches `color/`/`font/`/`image/` (which have 2 files but the same per-concern grouping). **Keep all package boundaries as-is.**

### Recommendation

**No restructure needed.** Per-token-type packaging is consistent and idiomatic; no god files; no organization-only nesting. (~0 min)

---

## 4. Quality / rewrite candidates (criterion 11)

### `design/src/commonMain/kotlin/uz/yalla/design/theme/Theme.kt`

- **Tokens via `staticCompositionLocalOf` — verified compliant.** `System` object (lines 151-181) reads each token via `LocalColorScheme.current`, `LocalFontScheme.current`, `LocalSpaceScheme.current`, `LocalRadiusScheme.current`, `LocalMotionScheme.current`, `LocalIsDark.current`. Each accessor is a `@Composable` getter. **This is the documented pattern; matches CLAUDE.md's `LocalAppColors.current` recommendation exactly.** No deviation. Keep.
- **`@Stable` / `@Immutable` annotations:** `MotionScheme` and its four nested data classes are annotated `@Immutable` (`MotionScheme.kt:61, 83, 105, 126, 151`). **`ColorScheme`, `FontScheme`, `SpaceScheme`, `RadiusScheme` and their nested data classes have NO `@Stable` / `@Immutable` annotation.** This is a Compose recompilation correctness gap. Without the annotation, Compose treats these data classes as potentially-unstable types — any composable that reads `System.color` or `System.font` is forced to recompose whenever its parent recomposes, even when the scheme value is structurally identical.
  - **Fix:** add `@Immutable` to `ColorScheme`, `FontScheme`, `SpaceScheme`, `RadiusScheme` and every nested data class within them. Compose's stability inference *may* infer them as stable already (data classes with all-`val` fields whose types are themselves stable), but explicit annotation removes ambiguity and matches `MotionScheme`'s precedent.
  - **Estimated impact:** ~12 annotations across 4 files. Net delta ~12 lines added. **Behavior-preserving** (only affects compiler-emitted stability metadata). Sub-100-line. (~20 min)
  - Suggested target pattern from criterion 11: matches the "tokens are `data class`es" target; this fills a gap in the implementation, not a redesign.

### `design/src/commonMain/kotlin/uz/yalla/design/theme/Theme.kt` — Material3 bridge duplication

- Lines 92-117 — the M3 `darkColorScheme(...)` and `lightColorScheme(...)` calls duplicate every parameter (`primary = colorScheme.button.active`, `onPrimary = colorScheme.text.white`, etc.) across the two branches. **The `if (isDark)` switch is a one-bit difference**: `darkColorScheme` vs `lightColorScheme` builder. Could collapse into:
  ```kotlin
  val builder = if (isDark) ::materialDarkColorScheme else ::materialLightColorScheme
  val materialColorScheme = builder(...).copy(
      primary = colorScheme.button.active,
      onPrimary = colorScheme.text.white,
      ...
  )
  ```
  ~14 lines saved. Sub-100-line. (~10 min) **Borderline — current code is repetitive but explicit; the rewrite hides the builder selection one level deeper. Flag, defer.**

### `design/src/commonMain/kotlin/uz/yalla/design/font/FontScheme.kt`

- Lines 108-141 — `FontScheme.Body.numeric` extension property + `FONT_FEATURE_TABULAR_NUMERALS` const. Already covered in section 1 bucket 2-4 — zero callers. Quality-pass also notes: **the extension is gated by an `internal const` literal** (`FONT_FEATURE_TABULAR_NUMERALS = "tnum"`), which would be reasonable defensive engineering if the numeric variant were used. Without consumers, both should go. (~5 min if approved)

### `design/src/commonMain/kotlin/uz/yalla/design/color/Color.kt`

- Lines 21-213 — covered by section 1 bucket 2-4. **47 public raw tokens that should be `internal`.** Demoting them is a public-API change; alpha versioning lets it land via `refactor!:` (criterion 3). Sub-100-line. (~30 min)
- Suggested target pattern from criterion 11: this is a general "tightening" pass aligned with the "tokens via `staticCompositionLocalOf` only" target — consumers should never reach for raw tokens.

### `design/src/commonMain/kotlin/uz/yalla/design/motion/MotionScheme.kt`

- Lines 1-208 — covered by section 1 bucket 2-4. **REWRITE >100 LINES — NEEDS GATE.** Two paths:
  1. **Delete** the entire `motion/` package (208 lines + the `LocalMotionScheme` slot in `Theme.kt` + the `motionScheme: MotionScheme = standardMotionScheme()` param in `YallaTheme`). Behavior-preserving: nothing reads it. **~225 lines removed.** Public API change → `refactor!:`. The YallaClient refactor spec referenced in the KDoc would land the consumers later; if/when it does, the API can be re-introduced.
  2. **Keep** as forward-staging. Cost: 208 lines of dead public surface and the runtime allocation in every `YallaTheme(...)` call (one `MotionScheme` data class instantiation per theme).
  **Recommend Islom decide.** Same shape as `core` G2 (`ServiceBrand`/`PaymentCard`/`Client` keep-pending-consumer). Difference: those are 30-40 lines each, this is 225.

### Architecture violations — full pass

- **`try { … } catch { … }` in business logic** — none. design is pure declarative tokens + composables.
- **Mappers as classes / DTO extension functions** — N/A; design has no DTOs.
- **Service classes / `Api` naming** — N/A; design has no networking.
- **`InMemoryTokenProvider` / manual `Authorization` header / `AuthEventBus`** — N/A.
- **Custom MVI** — N/A; design has no state machines.
- **Hardcoded product copy in primitives** — verified via `grep -rn '[А-Яа-яЁё]\|[ʻ]' design/src/commonMain` → zero matches. The bundled font (`Nummernschild`) is product-specific (Yalla cars use European-license-plate styling) but it's a font *resource*, not text — bricks not assemblies.
- **String-typed identifiers that should be value classes** — N/A in design; tokens use `Color`/`TextStyle`/`Dp`/`Duration` which are already strongly typed.

### Untestable shape

- **`YallaTheme`** mixes Compose-side wiring (CompositionLocalProvider, MaterialTheme bridge) with default-resolution logic. The current test (`YallaThemeTest.kt`) uses `runComposeUiTest` which spins up a Compose UI test runner — appropriate for the Compose Local switching. No untestable shape; ~5 tests cover the locals-provided-to-System path.
- **`rememberFontScheme()`** is a `@Composable` whose result depends on `expect val`s that resolve to platform font resources. Not directly testable in `commonTest` (font loading needs a Compose context); the existing `FontSchemeEqualityTest.kt` exercises the data class directly with `TextStyle(fontSize = ...)` constructors and skips font loading. That's the standard expect/actual testability gap; not a god-class problem.

### Summary of section 4 rewrite candidates

| Item | Lines | Gate? |
| ---- | ----- | ----- |
| Add `@Immutable` to ColorScheme/FontScheme/SpaceScheme/RadiusScheme | ~12 | no |
| M3 `darkColorScheme`/`lightColorScheme` bridge collapse (deferred) | ~14 | no |
| Demote raw color tokens to `internal` (`Color.kt`) | ~50 net | no (sub-100) |
| `FontScheme.Body.numeric` removal | ~30 | no |
| **`motion/` package removal (option 1)** | **~225** | **yes, recommend** |

---

## 5. Promote/demote candidates (criterion 1)

Applied lego test to every public type in `design/src/commonMain`.

### Bricks (stays in design — vast majority)

`ColorScheme` (+ nested), `light()`, `dark()`, `LocalColorScheme`, `FontScheme` (+ nested), `rememberFontScheme()`, `LocalFontScheme`, `boldFont`/`mediumFont`/`normalFont` (`expect val`s + actuals), `RadiusScheme`, `standardRadiusScheme()`, `LocalRadiusScheme`, `SpaceScheme` (+ `Scale`), `standardSpaceScheme()`, `LocalSpaceScheme`, `MotionScheme` (+ nested), `standardMotionScheme()`, `LocalMotionScheme`, `ThemedImage` enum, `themedPainter()`, `YallaTheme`, `System`.

All pass the lego test:
- **No hardcoded product copy** — verified `grep -rn '[А-Яа-яЁё]\|[ʻ]' design/src/commonMain` → 0 matches.
- **No screen-shaped or ViewModel-shaped types** — all types are tokens or theme-scope composables.
- **No Ildam-specific business orchestration.**

### Token values that look product-specific

- The `LightButtonActive = Color(0xFF562DF8)` purple, `LightTextLink = Color(0xFF562DF8)` purple, `LightIconSecondary = Color(0xFF562DF8)`, `DarkBackgroundBrandBase = Color(0xFF562DF8)`, the `SplashBackground` gradient (purple `0xFF7957FF → 0xFF562DF8 → 0xFF3812CE`), and the `SunsetNight` gradient (`0xFFFF234B → 0xFF2F00EC` pink-to-purple) are **Yalla brand colors**. Per the prompt, "Yalla branding IS the product, so this is OK as long as it's not leaking business semantics." These tokens leak only the visual brand (color values), not business meaning — there's no `OperatorPurple` vs. `ClientPurple` split, no driver-vs-passenger color routing. **Keep in design.** This is the visual-language brick and the Yalla brand IS the value prop.
- The bundled font `Nummernschild` (used in `FontScheme.Custom.carNumber`) is European-license-plate styling, product-specific to taxi apps. Same reasoning: it's the visual language, not business behavior. Keep.
- **No tokens carry business semantics (operator-vs-client-vs-driver, premium-vs-standard tier, status-coded color names like "active-driver-green").** Verified by reading every token name in `Color.kt`.

### Borderline — flag for Islom

- **`ThemedImage.entries` (`ThemedImage.kt:50-89`)** — 12 entries. The names are mostly generic (`CloseCircle`, `Login`, `Logout`, `MapPin`, `NotificationMute`, `OrderHistory`, `OrderSearch`, `Safety`, `ShieldCheck`, `TariffCard`, `TrashCan`, `BlurryLogo`). One name leaks product domain: **`OrderHistory`** and **`OrderSearch`** are taxi-app-specific (a generic design-system would use `History`/`Search`/`EmptyState`). **`TariffCard`** is borderline (general fintech) but in this codebase pairs to ride-fare cards. **Recommend keep** — the entries are all *illustrations* with light/dark variants, and the names map to specific drawable assets in `:resources` that the SDK ships. Renaming them creates churn for zero brick-vs-assembly clarity gain. (~0 min)
- **`SplashBackground`** name encodes the consumer (a "splash" screen). A pure design module would name it by gradient direction/feel (`PrimaryGradient` / `BrandHero`). But the consumer is `primitives/indicator/SplashOverlay.kt:158` which uses it via `System.color.gradient.splash`, so the name *is* keyed to the slot, not the implementation. Keep.

### Demotion candidates

None. design matches its description as the visual-language brick layer.

### Promotion candidates (YallaClient → design)

- **None observed.** YallaClient's design-time tokens live in the SDK already (verified by the `import uz.yalla.design.*` grep across YallaClient: only 4 unique imports, all from `theme/` and `image/`). YallaClient does not appear to define product-side `*Scheme` extensions or local color tokens that should be promoted.
- (Caveat: this audit didn't open every YallaClient file. If product-side has scattered ad-hoc `Color(0xFFxxxxxx)` literals that should be tokens, they'd promote to design. Out of scope for the audit prompt; flag for Islom.)

### Notes about hardcoded strings

- No Russian/Uzbek string literals in design.
- No string-typed identifiers; all tokens are `Color`/`TextStyle`/`Dp`/`Duration`.

### Verdict for `MIGRATION_LIST.md`

- "## To promote into design" — empty.
- "## To demote from design" — empty (no assemblies in the module).
- "## To decide" — borderline `ThemedImage.OrderHistory`/`OrderSearch`/`TariffCard` naming (recommend keep).

---

## 6. Missing tests (criterion 6)

### Inventory by package

#### `design/color/`

- `ColorScheme` data class — covered by `ColorSchemeTest.kt`. 14 tests. Round-trip per group: text/background/border/button/icon for both Light and Dark; shared accent + gradient across themes; copy-with-modified-text. **No gap.**
- `light()` / `dark()` factories — covered by `ColorSchemeTest.kt`. **No gap.**
- `LocalColorScheme` default — covered indirectly by `YallaThemeTest.kt:18-26` (asserting `System.color` resolves). **No gap.**

#### `design/font/`

- `FontScheme` data class — covered by `FontSchemeEqualityTest.kt`. 3 tests. Structural equality positive + negative (different title, different custom). **No gap on data-class behavior.**
- `boldFont`/`mediumFont`/`normalFont` `expect val`s — platform-specific; not testable in commonTest. The actuals are one-line FontResource references, practically untestable (and low-value to test — the actual is a literal). Not a gap.
- `rememberFontScheme()` — covered indirectly by `YallaThemeTest.kt:29-37` (`System.font` resolves to a non-null FontScheme). **No gap.**
- **`FontScheme.Body.numeric` extension** (lines 140-141) — **no test.** Even if kept (section 1 bucket 2-4), there's no test that asserts `numeric.fontFeatureSettings == "tnum"` or `numeric.fontSize == base.medium.fontSize`. ~3 lines of test if kept; deletion candidate otherwise. **Gap (small, ~5 min if added).**

#### `design/image/`

- `ThemedImage` enum — covered by `ThemedImageTest.kt`. 2 tests. Distinct light/dark resources per entry; entry count == 12. **No gap on enum integrity.**
- **`themedPainter()` composable** — **no test.** The light-vs-dark variant selection is the load-bearing behavior. The current `ThemedImageTest.kt` only asserts the enum has distinct resources, not that `themedPainter` picks the right one based on `System.isDark`. Worth ~2-3 tests in `androidUnitTest` or via `runComposeUiTest`:
  - `themedPainter(image)` returns a Painter from the `light` resource when `isDark = false`.
  - `themedPainter(image)` returns a Painter from the `dark` resource when `isDark = true`.
  - These are tricky to assert because `painterResource()` returns a `Painter` opaque to value comparison; the workaround is to wrap a recomposition that captures `image.dark` vs `image.light` decision via a side-channel. **~30 min, ~3 tests.** Worth doing because variant selection is the only behavior in the file.
  - Alternative: factor the `if (System.isDark) image.dark else image.light` selector out into a non-`@Composable` helper that takes `isDark: Boolean` and returns `DrawableResource`. Trivial to test in commonTest. (~10 min refactor + 2 tests.)
  - **Gap. ~30 min.**

#### `design/motion/`

- `MotionScheme` data class — **no test file.** Confirmed: `ls design/src/commonTest/kotlin/uz/yalla/design/motion` → not present. Plan-time inventory in the prompt called this out specifically.
  - **Gaps:**
    - Equality / round-trip (sample → `copy()` → equal/non-equal). ~3 tests.
    - `standardMotionScheme()` returns expected duration values (`100ms`/`200ms`/`350ms`/`500ms`/`800ms` per `Duration`; `30ms`/`50ms`/`75ms` per `Stagger`). ~2 tests.
    - `LocalMotionScheme` default == `standardMotionScheme()`. ~1 test.
  - **~6 tests, ~30 min.**
  - **If `MotionScheme` is deleted per section 1 bucket 2-4 (and section 4 rewrite gate), this gap evaporates.** Defer the test-writing decision until the gate decision lands.

#### `design/radius/`

- `RadiusScheme` — covered by `RadiusSchemeTest.kt`. 3 tests. Standard values + structural equality positive/negative. **No gap on data-class behavior.**
- `LocalRadiusScheme` default — covered indirectly by no test, but the data class is a passive record; the default-value test would assert `LocalRadiusScheme.current == standardRadiusScheme()` outside a `YallaTheme`, which is an `runComposeUiTest` exercise. ~5 min if added; low-value. Optional. Not a gap.

#### `design/space/`

- `SpaceScheme` + `SpaceScheme.Scale` — covered by `SpaceSchemeTest.kt`. 5 tests. Semantic values + scale values + structural equality positive/negative on both layers. **No gap.**
- Same `LocalSpaceScheme` default-value caveat as radius. Optional, low-value.

#### `design/theme/`

- `YallaTheme` — covered by `YallaThemeTest.kt`. 5 tests. ColorScheme via System; FontScheme via System; light scheme when not dark; dark scheme when dark; `System.isDark` flag.
- **Gaps:**
  - **`SpaceScheme`, `RadiusScheme`, `MotionScheme` resolution via System** — the test only asserts `System.color` and `System.font`; `System.space`, `System.radius`, `System.motion` are untested. ~3 tests, ~10 min.
  - **Material3 bridge** — the `MaterialTheme` setup (lines 92-117 of `Theme.kt`) maps Yalla tokens to M3 slots (`primary = colorScheme.button.active`, etc.). No test verifies the bridge — i.e., that an M3 `Button { … }` rendered inside `YallaTheme` picks up `LightButtonActive` as its primary color. This is the single most behaviorally-rich part of the file. ~1 test using `runComposeUiTest` + a snapshot or color-readback; ~30 min.
  - **`LocalIsDark` correctness** — partially covered (`shouldProvideIsDarkFlag`) but not the corresponding `false` case. ~2 min if added; trivial.
  - **Custom scheme overrides** — the `YallaTheme(colorScheme = …)` param lets callers inject custom schemes. No test verifies that an injected `ColorScheme` overrides the default `if (isDark) dark() else light()`. ~1 test, ~10 min.
- **Total gap: ~7 tests, ~50 min.**

### Summary by package

| Package | Effort | Gap |
| ------- | ------ | --- |
| `color/` | 0 min | none |
| `font/` | ~5 min, 1 test (numeric extension if kept) | conditional |
| `image/` | ~30 min, 2-3 tests | `themedPainter` light/dark selection |
| `motion/` | ~30 min, 6 tests (if scheme kept) | conditional on G-decision |
| `radius/` | 0 min | none actionable |
| `space/` | 0 min | none actionable |
| `theme/` | ~50 min, 7 tests | space/radius/motion via System; M3 bridge; custom overrides |

**Total wave-8 effort estimate: ~60 min unconditional + up to ~35 min conditional.** Brings design test count from 6 to 8-9 files + assertions in 1 (theme).

---

## 7. MODULE.md staleness (criterion 5)

Current `design/MODULE.md` (33 lines) uses the old `# Module / # Package …` format with an "Architecture" section. Phase-1 form (per `bom/MODULE.md`, post-cleanup `core/MODULE.md`, post-cleanup `data/MODULE.md`) is:

```
# Module <name>
> One-line tagline.

## What this is
## What this is NOT
## Usage
## Notes
## Depends on
```

### Sections to add

- **`> One-line tagline.`** — currently the second line is "Yalla SDK design system — colors, typography, themed images, and theme composition." which is informational but not a tagline. Suggested: `> Visual language — color, font, space, radius, motion tokens + Yalla theme.`
- **`## What this is`** — replace the current "Architecture" + four `# Package` blurbs with a tight 6-8 bullet list:
  - `ColorScheme` (+ `light()`/`dark()` factories) — semantic groups: text, background, border, button, icon, accent, gradient.
  - `FontScheme` — `Title` / `Body` (with `Weighty` regular/medium/bold) / `Custom`. Loaded per-platform (Roboto on Android, SF Pro on iOS) via `expect`/`actual` font resources.
  - `SpaceScheme` — semantic layer (`screenEdge`, `sheetEdge`, `itemGap`, `sectionGap`, `heroGap`, `inlineGap`) + escape-hatch `Scale` (xxs..massive).
  - `RadiusScheme` — t-shirt scale (`xs`..`xl`) + named slots (`sheet`).
  - `MotionScheme` — duration / easing / spring / stagger tokens. (Mark as **forward-staged** if Islom keeps; remove this bullet if dropped per section 4.)
  - `ThemedImage` enum + `themedPainter()` — light/dark drawable variant selection.
  - `YallaTheme` composable — wires every `Local*Scheme` + bridges Yalla tokens to Material3.
  - `System` object — token accessor; `System.color.text.base`, `System.font.body.base.medium`, `System.space.screenEdge`, etc.
- **`## What this is NOT`** — explicitly:
  - **Not** a UI primitives module — no `Button`, `TextField`, `Card`, `Sheet`. Those live in `primitives` / `composites`.
  - **Not** a string/icon resources module — those live in `:resources`.
  - **Not** a feature module — no screens, no navigation, no business orchestration.
  - **Not** a Material3 wrapper — the M3 `MaterialTheme` bridge inside `YallaTheme` exists so M3 components pick up Yalla brand colors automatically; consumers should still prefer `System.*` over `MaterialTheme.colorScheme.*`.
- **`## Usage`** — 6-10 lines showing typical SDK consumer wiring:
  ```kotlin
  @Composable
  fun App() {
      YallaTheme {
          Text(
              text = "Welcome",
              color = System.color.text.base,
              style = System.font.title.large,
          )
          Spacer(Modifier.height(System.space.itemGap))
          Image(
              painter = themedPainter(ThemedImage.Login),
              contentDescription = null,
          )
      }
  }
  ```
- **`## Notes`** — fold in:
  - The Yalla-brand-color note (Yalla branding IS the product; tokens carry visual brand, not business semantics).
  - The Material3 bridge rationale (why `MaterialTheme` is wrapped inside `YallaTheme`).
  - The `LocalFontScheme` no-default `error("…")` rationale (font loading needs a composable context, can't ship a static fallback).
  - The motion / numeric / raw-color-tokens dead-code caveats (until the gate decisions land in section 1/4).
- **`## Depends on`** — the block from section 2.

### Sections to remove

- **Lines 8-15 (`## Architecture`)** — the four-bullet "follows a token-based design system" intro paragraph. Replaced by `## What this is`.
- **Lines 16-34 (`# Package uz.yalla.design.color` through `# Package uz.yalla.design.image`)** — all four per-package blurbs. Per-package KDoc lives on the source, not in MODULE.md. Drop entirely. Matches the precedent set when core's MODULE.md had its 11 per-package blurbs removed and data's MODULE.md had its four removed.
- **Lines 4-7** — "Provides the visual foundation for all Yalla UI components through a structured token system accessible via the [System][uz.yalla.design.theme.System] object." The "Provides the visual foundation for all Yalla UI components" framing is exactly the "brand voice" / aspirational pattern the prompt flagged. Drop. (The accessor reference moves to `## Usage`.)

### Sections to rewrite

- **Lines 1-7** — opening paragraph + ungrouped explanation. Replace with the phase-1 tagline + `What this is` form.

### Cross-check from prompt

- The current `design/MODULE.md` doesn't reference any moved/flattened package, so no stale-package cleanup needed.
- The `[Color.kt][uz.yalla.design.color]` and `[ColorScheme][uz.yalla.design.color.ColorScheme]` etc. dokka-link cross-refs in the current "Architecture" section are not used in the phase-1 form (per `core/MODULE.md`, `data/MODULE.md`). They go away with the section.

Total wave-10 effort: full rewrite of `design/MODULE.md` from scratch on phase-1 form. **~25 min** — the same length as data's, slightly longer than core's because the visual-brand notes need careful framing.

---

## 8. Reviewer notes

### Pushback on specific findings

- **Section 1 — bucket 2-4 on `motion/`.** I flagged the entire `motion/` package (208 lines + theme integration ~17 lines + LocalMotionScheme = ~225 lines) as dead because no consumer references `System.motion` anywhere. Two readings:
  1. **Truly dead** — `MotionScheme` was added in `0.0.17` and shipped with the catalog but no consumer migrated yet. YallaClient uses raw `tween(durationMillis = 300, ...)` and `spring(...)` calls.
  2. **Forward-staged** — the KDoc references "the YallaClient refactor spec (section 9) and ADR-021"; these are real product intent that hasn't shipped. Deleting now means re-introducing later.
  
  My recommendation: **gate on Islom**. If the YallaClient migration that consumes `System.motion` is on the near-term roadmap, keep the surface and write the missing tests (section 6). If it's vapor, delete and re-add when needed. The 208-line cost is non-trivial but not catastrophic; the cost of *re-introducing* `MotionScheme` later is small (one wave-shaped commit). **Lean delete.**

- **Section 1 — bucket 2-4 on `Color.kt` raw tokens.** I flagged 47 raw color tokens (`LightTextBase`, `DarkTextBase`, etc.) as zero-importer-outside-design. **They're not dead — `light()` and `dark()` use them.** They *are* unnecessarily public. The cleanup is **demote to `internal`**, not delete. Side note: this conflicts with `ColorSchemeTest.kt:12-141` and `YallaThemeTest.kt:7-8` which import `LightTextBase`/`DarkTextBase` directly. **Tests would need refactoring** to assert via the scheme accessor (e.g. `assertEquals(0xFF101828.toInt(), light().text.base.toArgb())` or by snapshotting hex literals inline). ~30 min tests + 30 min source = ~60 min total for the demote. Sub-100-line public-API change → `refactor!:`.

- **Section 1 — bucket 2-4 on `FontScheme.Body.numeric`.** Same as motion — flagged for review. The KDoc gives a believable use case (animated price displays); the `tnum` OpenType feature is real and useful. But zero callers exist *today*. Same recommendation: gate on Islom. The cost-of-keep is 30 lines plus an opaque `internal const`; the cost-of-delete + later-re-add is one targeted commit.

- **Section 4 — `@Immutable` annotations on the four un-annotated schemes.** Compose's stability inference *probably* infers `ColorScheme`/`FontScheme`/`SpaceScheme`/`RadiusScheme` as stable already (data classes with all-`val` fields whose types are themselves stable). Adding explicit `@Immutable` is belt-and-suspenders. But the *inconsistency* with `MotionScheme` (which has `@Immutable` everywhere) is the real signal — pick one rule and apply uniformly. Recommend explicit-annotation everywhere for clarity.

- **Section 2 — `compose.runtime` and `compose.ui` `implementation` → `api` promotion.** I argued the public types of `Local*Scheme`, `themedPainter`, `YallaTheme`, `System.*` accessors come from `compose.runtime` (`@Composable`, `ProvidableCompositionLocal`) and `compose.ui` (`Color`, `TextStyle`, `Dp`, etc.). Strictly correct, but **downstream modules already declare `compose.runtime` + `compose.ui` themselves** because they're UI modules (verified — `primitives`, `composites`, `platform` all apply `KmpComposeConventionPlugin` which transitively pulls these in). The promotion fixes a subtle "dishonest graph" issue but doesn't unblock anything. **Lean toward promote anyway** for correctness; this is a `refactor!:` because it changes the published Maven `pom.xml` deps.

### Cross-cutting patterns

- **`@since 0.0.X` ceremony tags affect 21 KDoc blocks across the module** (verified via `grep -rn "@since" design/src --include="*.kt"`). Not paraphrase but noise — none of the consumers track them, and SDK is alpha (criterion 3). Drop in the wave-2 KDoc sweep. Consistent with core G5 and data wave-2 finding. (~5 min sweep)

- **Every `Scheme` data class shares the same `Light`/`Dark`/`standard*()` companion shape.**
  - `ColorScheme` has explicit `light()` and `dark()` top-level factories (no companion).
  - `FontScheme` has `rememberFontScheme()` top-level (no `light`/`dark` distinction; fonts are theme-agnostic in this codebase).
  - `SpaceScheme`, `RadiusScheme`, `MotionScheme` each have a single `standard*Scheme()` top-level factory.
  - **The naming convention is inconsistent**: `light()`/`dark()` (lowercase plain functions, theme-keyed) vs. `standard*()` (the rest, no theme variant). The prompt asked to confirm "every Scheme data class shares the same Light/Dark companion shape" — **they don't**. The colors are the only theme-bifurcated tokens; everything else is theme-agnostic by current product design. **This is correct as-is** — fonts/space/radius/motion don't currently differ between Light and Dark — but worth documenting in MODULE.md `## Notes` so future contributors know the convention.

- **`@Immutable` is applied only to `MotionScheme` and its nested data classes** (5 annotations in `MotionScheme.kt`). The other four schemes have **none**. Either (a) Compose's stability inference covers them (likely true) and `MotionScheme`'s explicit annotations are redundant, or (b) they need explicit `@Immutable` to match `MotionScheme`'s precedent. **Not both.** The inconsistency is the real signal; pick one rule. (Section 4 finding.)

- **`KDoc Usage blocks with `## Usage` headers pattern** appears in `ColorScheme.kt:17-27`, `FontScheme.kt:13-25`, `MotionScheme.kt:22-43`, `RadiusScheme.kt:20-31`, `SpaceScheme.kt:21-29`, `Theme.kt:46-58, 142-146`. **All carry information density** (working code samples that demonstrate the API). **Keep all of them**; don't sweep them in the bucket 2-1 KDoc cleanup. The pattern is a project convention worth documenting in CLAUDE.md or in MODULE.md notes.

- **Stale `System.color.<token>` references in OTHER modules' KDocs.** Cross-cutting note: `platform/src/commonMain/kotlin/uz/yalla/platform/indicator/NativeLoadingIndicator.kt:17` references `System.color.accent.primary`; `platform/src/commonMain/kotlin/uz/yalla/platform/button/NativeCircleIconButton.kt:20` references `System.color.background.primary`; `platform/src/commonMain/kotlin/uz/yalla/platform/sheet/NativeSheet.kt:18` references `System.color.background.primary`; `composites/src/commonMain/kotlin/uz/yalla/composites/item/AddressItem.kt:140, 218` reference `System.color.icon.brand`. **None of these tokens exist on `ColorScheme`** (verified via a `grep -rn "color\.accent\.\w+\|color\.background\.primary\|color\.icon\.brand"` against design's `ColorScheme.kt`). They're stale doc references — the actual ColorScheme has `accent.pinkSun`/`accent.color1..5`, `background.base`/`background.brand`/`background.secondary`/`background.tertiary`, `icon.secondary` (not `icon.brand`). **Out of scope for design audit**, but worth flagging to future `platform`/`composites` audits — they're bucket 2-4 dead-link doc references.

### Concerns with the criteria as applied to design

- **Criterion 6's state-machine bar doesn't apply.** Design has no Orbit `ContainerHost`. The "every intent → state transition tested" line is a no-op for design. Mention this in wave-9 verification but don't try to invent state machines. Same as core/data.

- **Criterion 11's god-class threshold is ungenerous to design.** The longest file (`ColorScheme.kt` at 293 lines) is fundamentally a long enumeration — every entry in `light()` and `dark()` is structurally required, not redundant. Splitting into `LightColorScheme.kt` / `DarkColorScheme.kt` would invent two-file boilerplate to satisfy a line-count rule. **The criterion correctly didn't trigger** (>300 lines threshold, this is at 293), but the spirit reads as "would Islom split this if it grew to 320 lines?" — answer is no, it would be a forced split. Keep the criterion's threshold as-is; the file is a legit long-list, not a god class.

- **Criterion 11's "tokens via `staticCompositionLocalOf`" target as documented in CLAUDE.md** — design **already does this correctly**. Every `Local*Scheme` uses `staticCompositionLocalOf`; `System` reads via `Local*.current` getters; no static singleton fallback. This was the canonical implementation, and no deviation needs flagging. The prompt called it out as a check item; affirmative result.

- **Criterion 1 (lego test) flagged "Yalla branding IS the product"** — the brand colors and the `Nummernschild` font *are* product-specific in the literal sense. The criterion's carve-out ("Yalla branding IS the product, so this is OK") is the right reading; brand visual language belongs in design. The borderline check on `ThemedImage.OrderHistory`/`OrderSearch` names found taxi-app-flavored names but no demotion candidate. **The criterion holds well for design.**

---

## Summary stats

- **Section 1 findings:** 23 file-level findings across 12 source files (10 commonMain + 2 platforms). Mix of ~150-180 lines of paraphrase KDoc + 21 `@since` tags + 56 raw-color one-liners + 12 `ThemedImage` one-liners + 2 dead-code candidates (numeric extension, motion package) + 1 demote-to-internal candidate (raw color tokens, ~47 symbols).
- **Section 2 findings:** 2 unused deps (`compose.uiTooling`, `androidx.core.ktx` in androidMain). 2 wrong-scope deps (`compose.runtime`, `compose.ui` should be `api`). 0 unused deps in commonMain or test sets.
- **Section 3 findings:** 0 god files (longest is 293 lines). 0 organization-only nesting. 4 single-file packages, all idiomatic per-concern groupings; no flatten candidates.
- **Section 4 findings:** 5 quality candidates. 1 `@Immutable` consistency gap (~12 lines), 1 small bridge collapse (deferred), 1 demote-to-internal sweep (~50 lines), 1 dead extension removal (~30 lines), 1 dead module removal (~225 lines, **gate**). 1 architecture violation found (none — no try/catch, no DTO mappers, no AuthEventBus).
- **Section 5 findings:** 0 promotion, 0 demotion, 1 borderline note (`ThemedImage.OrderHistory`/`OrderSearch`/`TariffCard` names — recommend keep).
- **Section 6 findings:** 4 packages with gaps (`font/numeric`, `image/themedPainter`, `motion/MotionScheme`, `theme/space-radius-motion-via-System + M3 bridge + custom overrides`). ~12-13 missing tests, ~95 min total effort (~60 min unconditional + ~35 min conditional on motion gate).
- **Section 7 findings:** 1 full MODULE.md rewrite + 4 stale package blurbs + 1 stale "Architecture" section to drop + 1 brand-voice paragraph to drop.
- **Longest single rewrite candidate:** **`motion/` package removal at ~225 lines** (208 lines of `MotionScheme.kt` + ~17 lines of theme integration in `Theme.kt`). **Crosses the 100-line gate. NEEDS GATE.**
- **Blocking issues:** none. Audit is fully derivable from the source; no questions block wave-2.

---

