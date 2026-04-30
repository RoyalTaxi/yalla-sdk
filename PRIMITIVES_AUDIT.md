# PRIMITIVES_AUDIT.md

Audit output for wave 1 of phase-3 `primitives` cleanup. Drives waves 2-10. Findings keyed to `CLEANUP_CRITERIA.md`. All paths absolute under `/Users/islom/StudioProjects/yalla-sdk/`.

---

## 1. AI-blob deletions (criterion 2)

### `primitives/MODULE.md`

- **2-1** lines 1-67 — entire file. Pre-phase-1 form: free-form Architecture paragraph (lines 9-18) + 11 `# Package` blurbs (lines 20-67). Same precedent as `core/`, `data/`, `design/`, `foundation/` MODULE.md rewrites. (~30 min — full rewrite, see section 7.)
- **2-1** line 18 — `[COMPONENT_STANDARD.md](../COMPONENT_STANDARD.md)` reference. **The file does NOT exist** (verified: `find . -name "COMPONENT_STANDARD.md"` returns no matches; `ls COMPONENT_STANDARD.md docs/COMPONENT_STANDARD.md` both ENOENT). The `docs/` purge in phase 1 removed it. **Stale doc reference, bucket 2-1.** Rewrite must fold the `Colors + Dimens + Defaults` pattern explanation INTO MODULE.md notes. (~5 min decision — see section 7.)
- **2-1** lines 39-41 — `# Package uz.yalla.primitives.model` blurb references `[ButtonSize]`. **Verified DOES NOT EXIST**: there is no `model/` sub-package, no `ButtonSize` type. (`find primitives/src/commonMain -name "ButtonSize*"` returns nothing.) Phantom doc — wholly fictitious package reference. (~1 min)
- **2-1** lines 47-49 — `# Package uz.yalla.primitives.otp` blurb references `[PinView]`. **Verified DOES NOT EXIST**: no `PinView` type anywhere in primitives. Only `PinRow` lives in `otp/`. Phantom symbol reference. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/PrimaryButton.kt`

- **2-1** lines 21-32, 49-59, 68-119, 150-155, 166-173, 187-194 — the per-data-class KDoc property paraphrase blocks repeat shape. The class-level Usage paragraphs (lines 70-99) carry info; the `@param`/`@property` blocks restate the field name. (~5 min)
- **2-1** lines 31, 58, 118, 154 — four `@since 0.0.1` ceremony tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/SecondaryButton.kt`

- **2-1** lines 21-32, 49-59, 68-119, 150-155, 166-173, 187-194 — same shape as `PrimaryButton.kt` (this file is a near-mechanical copy with `Secondary*` swapped for `Primary*`). The per-property `@param`/`@property` paraphrase block accounts for ~70 lines of comment. (~5 min)
- **2-1** lines 31, 58, 118, 154 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/TextButton.kt`

- **2-1** lines 21-32, 49-59, 68-117, 149-154, 165-172, 186-193 — same per-property paraphrase pattern. (~5 min)
- **2-1** lines 31, 58, 117, 153 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/IconButton.kt`

- **2-1** lines 19-30, 47-56, 64-99, 123-128, 139-146, 161-170, 184-190 — per-property `@param/@property` paraphrase + `Filled Variant` / Usage examples. The Usage examples carry info; the per-property paraphrase doesn't. (~4 min)
- **2-1** lines 29, 56, 98, 127 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/NavigationButton.kt`

- **2-1** lines 23-36, 38-53, 55-86, 113-118, 129-134, 144-150 — per-property paraphrase. (~3 min)
- **2-1** lines 30, 47, 85, 117 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/BottomSheetButton.kt`

- **2-1** lines 20-35, 37-54, 56-103, 136-141, 152-158, 170-177 — same paraphrase pattern. (~4 min)
- **2-1** lines 28, 46, 102, 140 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/SensitiveButton.kt`

- **2-1** lines 47-60, 62-75, 77-114, 178-183, 191-196, 206-211 — paraphrase blocks. Class-level Usage carries info; per-property `@param` doesn't. (~4 min)
- **2-1** lines 54, 69, 113, 182 — four `@since 0.0.1` tags. (~1 min)
- **2-2** lines 185, 188 — `/** Default button height. */` and `/** Default button shape. */` one-liner companion-object KDocs that paraphrase the property names `Height` and `Shape`. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/GenderButton.kt`

- **2-1** lines 31-44, 46-63, 65-105, 151-156, 164-177, 179-198 — per-property paraphrase. (~4 min)
- **2-1** lines 39, 56, 104, 156 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/ButtonLayout.kt`

- **2-1** lines 28-54 — class-level KDoc on `internal fun ButtonLayout`. The Container → Provider → Layout → Content paragraph (29-36) is information-dense; the `@param` block (39-53) paraphrases. (~3 min)
- **2-1** line 53 — `@since 0.0.1` ceremony tag. (~30 sec)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/dialog/LoadingDialog.kt`

- **2-1** lines 22-33, 35-46, 48-71, 107-112, 114-122, 124-131 — per-property paraphrase. (~3 min)
- **2-1** lines 27, 41, 70, 111 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/field/PrimaryField.kt`

- **2-1** lines 23-47, 49-60, 62-119, 121-160, 70-103, 109-118 — per-property paraphrase. (~5 min)
- **2-1** lines 36, 55, 67, 159 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/field/NumberField.kt`

- **2-1** lines 45-73, 75-90, 92-97, 100-139, 145-160, 163-193 — same paraphrase pattern. (~5 min)
- **2-1** lines 60, 83, 96, 192 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/field/SearchField.kt`

- **2-1** lines 33-49, 51-69, 71-116, 79-95, 105-115, 118-167 — per-property paraphrase. (~5 min)
- **2-1** lines 42, 61, 75, 167 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/field/DateField.kt`

- **2-1** lines 29-46, 48-61, 63-109, 71-90, 96-108, 111-143 — per-property paraphrase. (~4 min)
- **2-1** lines 38, 55, 67, 142 — four `@since 0.0.1` tags. (~1 min)
- **2-1** lines 187-189 — KDoc on `private fun LocalDate.formatDisplay()`: "Formats [LocalDate] as DD.MM.YYYY for display." Pure paraphrase of the body. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/indicator/DotsIndicator.kt`

- **2-1** lines 25-36, 38-53, 55-77, 111-115, 117-126, 128-139 — per-property paraphrase. (~3 min)
- **2-1** lines 30, 45, 77, 116 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/indicator/LoadingIndicator.kt`

- **2-1** lines 19-30, 32-67, 69-87, 102-110, 113-128 — per-property paraphrase. (~3 min)
- **2-1** lines 25, 41, 87, 105, 117 — five `@since 0.0.1` tags. (~1 min)
- **2-1** lines 52, 60 — `LoadingIndicatorDimens.size(size)` / `.strokeWidth(size)` member-function KDoc paraphrases. (~30 sec)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/indicator/SplashOverlay.kt`

- **2-1** lines 30-45, 47-62, 64-95, 148-153, 155-169, 171-184 — paraphrase pattern. (~4 min)
- **2-1** lines 37, 54, 94, 152 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/indicator/StripedProgressBar.kt`

- **2-1** lines 34-47, 49-68, 70-100, 173-178, 180-191, 193-209 — paraphrase pattern. (~4 min)
- **2-1** lines 40, 58, 99, 177 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/otp/PinRow.kt`

- **2-1** lines 35-55, 57-72, 74-79, 80-112, 114-147 — per-property paraphrase. (~4 min)
- **2-1** lines 46, 65, 78, 146 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/pin/LocationPin.kt`

- **2-1** lines 1-8 — top-of-file `@file:Suppress("LongMethod", "DestructuringDeclarationWithTooManyEntries", "UnusedParameter")` block plus ceiling comment "// LongMethod: ..." / "// UnusedParameter: loading is reserved for Phase 4 animation wiring." The Suppress annotation is dead code — detekt is not configured per CLAUDE.md ("ktlint / detekt / jacoco are not configured"). Same for the suppression rationale comments. (~3 min)
  - **2-4** rolls into this: `loading: Boolean` parameter (line 195) is documented "Currently unused; reserved for future loading states" and `@file:Suppress("UnusedParameter")` confirms it's unused. ~70 lines elsewhere mention or explain it. **Speculative configuration knob with no current use.** Keep with documented status, or drop. **Flag for Islom.** (~5 min decision)
- **2-1** lines 69-96, 98-133, 138-188, 479-485, 487-519, 521-563 — per-property `@param`/`@property` paraphrase blocks. The class-level Usage examples (155-167) carry info; the long `@param` blocks below them don't. (~10 min)
- **2-1** lines 84, 117, 187, 484 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/pin/SearchPin.kt`

- **2-1** lines 22-33, 35-58, 89-104 — paraphrase. (~2 min)
- **2-1** lines 27, 58, 93 — three `@since 0.0.1` tags. (~30 sec)
- **2-2** none.

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/rating/RatingRow.kt`

- **2-1** lines 23-37, 39-57, 59-101, 103-141, 67-80, 83-100 — paraphrase. (~4 min)
- **2-1** lines 31, 49, 63, 140 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/topbar/TopBar.kt`

- **2-1** lines 27-37, 40-51, 53-105, 159-164, 165-174, 176-184 — paraphrase. The `navigationIconContentDescription` `@param` block (94-97) is information-dense (accessibility behavior); keep it. (~3 min)
- **2-1** lines 32, 45, 104, 163 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/topbar/LargeTopBar.kt`

- **2-1** lines 28-39, 41-54, 56-90, 141-146, 148-156, 158-167 — paraphrase. The `navigationIconContentDescription` `@param` block (78-82) is the same accessibility-info-dense paragraph as TopBar; keep. (~3 min)
- **2-1** lines 33, 47, 89, 145 — four `@since 0.0.1` tags. (~1 min)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/transformation/PhoneVisualTransformation.kt`

- **2-1** lines 8-33 — class KDoc on the public `class PhoneVisualTransformation`. The Usage block (15-23) carries info; the `@param mask`/`@param maskChar`/`@see` block paraphrases. (~2 min)
- **2-1** line 32 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 72-75 — `private class PhoneOffsetMapping` KDoc says "Offset mapping for phone number transformation." — pure paraphrase of the class name. (~30 sec)
- **2-4** rolls into a deeper finding (see section 4): the public `class PhoneVisualTransformation` has **zero callers** anywhere in SDK + YallaClient (verified `grep -rn "import uz.yalla.primitives.transformation" /Users/islom/StudioProjects/YallaClient` returns only `MaskFormatter`; in primitives itself, `NumberField.kt:240` uses a `private object PhoneVisualTransformation` — a *separate* type with the same name, no relation to the public class). ~108 lines of dead public code. (~10 min decision)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/transformation/NumberVisualTransformation.kt`

- **2-1** lines 8-36 — class KDoc. Usage block carries info; `@param`/`@see` paraphrases. (~2 min)
- **2-1** line 35 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 54-56 — `private class NumberOffsetMapping` KDoc paraphrases. (~30 sec)
- **2-4** rolls into the same finding as `PhoneVisualTransformation.kt`: **zero callers** anywhere in SDK + YallaClient. ~90 lines of dead public code. (~5 min decision)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/transformation/MaskFormatter.kt`

- **2-1** lines 1-20, 22-31, 56-67, 69-77 — Usage block on the object carries info; per-function `@param`/`@return` blocks paraphrase the signature. (~3 min)
- **2-1** lines 19, 29, 62, 76 — four `@since 0.0.1` tags. (~1 min)
- Cross-check: `MaskFormatter.format` IS used (1 consumer, `YallaClient/.../ClientCard.kt:65`). `countPlaceholders` and `extractRaw` have **zero callers** anywhere. **2-4** flag for two of the three public functions. (~5 min decision)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/util/SquareSize.kt`

- **2-1** lines 13-30 — `Modifier.squareSize` KDoc. Usage block carries info; `@param position` line is a real disambiguator (the meaning of 0.0/0.5/1.0 isn't obvious from the type). Keep almost all of it. The `@since 0.0.1` line is removable. (~30 sec)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/util/GenderResource.kt`

- **2-1** lines 9-15 — KDoc. The "Returns null for `GenderKind.NotSelected`" line is information-dense; the framing sentence paraphrases. (~30 sec)
- **2-1** line 14 — `@since 0.0.1`. (~30 sec)

### `primitives/src/commonMain/kotlin/uz/yalla/primitives/navigation/ToolbarAction.kt`

- **2-1** lines 3-6 — KDoc says "Re-exports from `platform` module for backward compatibility. Canonical types live in [uz.yalla.platform.navigation]." The "backward compatibility" framing implies a history that doesn't apply to alpha-stage code (criterion 3 — alpha versioning, no public-API hardening yet). (~1 min)
- **2-3** + **2-4** rolls into section 4: file is **two typealias re-exports** of `uz.yalla.platform.navigation.ToolbarAction` and `ToolbarIcon`. Verified zero consumers in SDK + YallaClient (YallaClient imports `uz.yalla.platform.navigation.ToolbarAction` directly, NOT the primitives typealias). **Dead code in addition to single-use abstraction shape.** (~5 min decision)

### Platform actuals

None — primitives has no `androidMain` or `iosMain` source set (only `commonMain` + `commonTest`). `LocalLifecycleOwner` and `repeatOnLifecycle` come from a transitive androidx-lifecycle dep brought in by the convention plugin (see section 2).

### Cross-cutting bucket counts (primitives only)

- **2-1 (paraphrase / ceremony):** 30 commonMain files affected. **101 `@since 0.0.1` tags** total across commonMain (verified `grep -rn "@since" primitives/src/commonMain | wc -l` = 101). ~150-200 lines of paraphrase across every component's Colors/Dimens/Defaults trio. Wave-2 sweep, **~80 min total** (heavier than core/data/foundation because primitives has the most files with the most repeated structure).
- **2-2 (comment redundancy):** ~2-3 lines per component (e.g., `/** Default button height. */ val Height = 60.dp`). Negligible compared to 2-1. (~5 min)
- **2-3 (single-use abstractions):** **two findings.**
  1. `primitives/navigation/ToolbarAction.kt` — typealias re-export with zero consumers. (~5 min)
  2. The `PhoneVisualTransformation` / `NumberVisualTransformation` class pair are written as the kind of generic parameterized abstraction that bucket 2-5 calls out (mask + maskChar config knobs), but with zero call sites. Rolls into 2-4.
- **2-4 (dead code):** **five findings, all flagged for Islom.**
  1. **`primitives/transformation/PhoneVisualTransformation.kt`** — entire file (108 lines), zero callers. NumberField uses an entirely different `private object PhoneVisualTransformation` (line 280 of NumberField.kt).
  2. **`primitives/transformation/NumberVisualTransformation.kt`** — entire file (90 lines), zero callers.
  3. **`primitives/transformation/MaskFormatter.kt:countPlaceholders` and `:extractRaw`** — zero callers. `format` is used. (~25 lines deletable)
  4. **`primitives/navigation/ToolbarAction.kt`** — entire file (8 lines, two typealiases), zero callers.
  5. **`LocationPin.loading: Boolean` parameter** — explicitly marked unused via `@file:Suppress("UnusedParameter")` and KDoc'd "Currently unused; reserved for future loading states." Speculative knob. (~5 lines if dropped — needs decision.)
  6. **`LocationPinDimens.shadowSize`** is referenced (lines 269, 311) but the `MaskFormatter.kt` callers data above is the strongest signal; bucket 2-5 below covers shadowSize.
- **2-5 (speculative generalization):** **two findings.**
  1. **`LoadingIndicatorDimens.strokeWidth(size: LoadingIndicatorSize): Dp`** (lines 60-67) — function exists but **the `LoadingIndicator` composable doesn't read it** (line 96-99 only uses `dimens.size(size)`). Three pre-configured stroke widths (small/medium/large) that no consumer can apply. ~12 lines. (~5 min)
  2. **`LocationPinDimens` 12 fields** — every field has a default in `LocationPinDefaults.dimens()` and zero consumers override any of them (verified: `grep -rn "LocationPinDefaults.dimens(" /Users/islom/StudioProjects/YallaClient` returns zero hits — the only consumer call site is `HomeScreen.kt`'s `LocationPin(...)` with no `dimens =` argument). Configuration knob with one default value across the entire codebase. **Borderline** — the same posture applies to most `*Dimens` data classes; flag as cross-cutting in section 4 instead of per-file.

---

## 2. Module dependency graph (criterion 4)

`primitives/build.gradle.kts` declarations (line-by-line):

| line | declaration | scope | libs/projects key |
| ---- | ----------- | ----- | ------- |
| 9 | `implementation(projects.core)` | commonMain | `:core` (SDK-internal) |
| 10 | `implementation(projects.design)` | commonMain | `:design` (SDK-internal) |
| 11 | `implementation(projects.resources)` | commonMain | `:resources` (SDK-internal) |
| 12 | `implementation(projects.platform)` | commonMain | `:platform` (SDK-internal) |
| 15 | `implementation(compose.runtime)` | commonMain | `compose-runtime` |
| 16 | `implementation(compose.foundation)` | commonMain | `compose-foundation` |
| 17 | `implementation(compose.material3)` | commonMain | `compose-material3` |
| 18 | `implementation(compose.materialIconsExtended)` | commonMain | `compose-material-icons-extended` |
| 19 | `implementation(compose.components.resources)` | commonMain | `compose-components-resources` |
| 20 | `implementation(libs.compose.ui.tooling.preview)` | commonMain | `compose-ui-tooling-preview` |
| 23 | `implementation(libs.compottie)` | commonMain | `compottie` |
| 24 | `implementation(libs.compottie.resources)` | commonMain | `compottie-resources` |
| 27 | `implementation(libs.constraintlayout)` | commonMain | `constraintlayout-compose-multiplatform` |
| 31 | `implementation(kotlin("test"))` | commonTest | — |
| 37 | `androidRuntimeClasspath(libs.compose.ui.tooling)` | androidMain runtime | `compose-ui-tooling` |

### SDK-internal dep verification

- **`projects.core`** (line 9) — `import uz.yalla.core.*` count: 4 imports across 3 files (`LocationPin.kt:59` — `or0`; `SensitiveButton.kt:36` — `formatArgs`; `GenderButton.kt:24` + `GenderResource.kt:4` — `GenderKind`). **Used. Keep.**
- **`projects.design`** (line 10) — `import uz.yalla.design.*` count: 33 imports across most components. `System` accessor and `YallaTheme` (the latter only in `@Preview` blocks). **Used. Keep.**
- **`projects.resources`** (line 11) — `import uz.yalla.resources.*` count: 28 imports (drawable refs, string refs, icon refs in `LocationPin`, `SearchPin`, `SplashOverlay`, `GenderButton`, `RatingRow`, `DateField`, `NumberField`, `SensitiveButton`, `GenderResource`). **Used. Keep.**
- **`projects.platform`** (line 12) — `import uz.yalla.platform.*` count: 4 imports (`LoadingIndicator.kt:17`, `LoadingDialog.kt:20`, `ButtonLayout.kt:26` — all `NativeLoadingIndicator`; `navigation/ToolbarAction.kt:7-8` — typealias re-export). **Used. Keep** (assuming the `ToolbarAction` typealias survives section 1 bucket 2-4 decision; if it doesn't, the platform dep stays for `NativeLoadingIndicator` regardless).

### Verification grep results — `api`-promotion candidates

For each `implementation()` declaration, verified whether public type signatures expose types from the lib.

- **`compose.runtime`** (line 15) — `@Composable` annotates every `*Defaults.colors()` method, every `*Defaults.textStyle()`/`digitStyle()`, and every component composable (`PrimaryButton`, `RatingRow`, etc.). `@Immutable` annotates 30+ data classes (`Colors`/`Dimens`). **`compose.runtime` types saturate the public API** — same shape as `design` G13 and `foundation`. **Promote to `api`.** (~5 min)
- **`compose.foundation`** (line 16) — used in 170 files via `androidx.compose.foundation.layout.*`/`shape.*`/`text.*`. `RowScope` is a public receiver type on `content: @Composable RowScope.() -> Unit` parameters of `PrimaryButton`, `SecondaryButton`, `TextButton`, `BottomSheetButton`. `BorderStroke` is a public param of `DateField` (`borderStroke: BorderStroke? = null`). `KeyboardOptions` is a public param of `PrimaryField` (`keyboardOptions: KeyboardOptions = KeyboardOptions.Default`). `TextFieldState`/`TextFieldLineLimits`/`InputTransformation`/`OutputTransformation` are public params of `PrimaryField` and `SearchField`. `TextSelectionColors` is in public `*FieldColors` data classes. **All in public surface.** **Promote to `api`.** (~5 min)
- **`compose.material3`** (line 17) — `Surface`, `Text`, `Icon`, `Card`, `OutlinedTextField`, `TextField`, `MaterialTheme`, `LocalContentColor`, `Card`/`CardDefaults`, `OutlinedTextFieldDefaults`, `TextFieldDefaults`, `BasicTextField` (Material3 isn't strictly the home of `BasicTextField` — but the M3 deps bring foundation as a transitive). All are used inside component bodies, **none are public type signatures.** Keep `implementation`.
- **`compose.materialIconsExtended`** (line 18) — verified: `grep -rn "import androidx.compose.material.icons" primitives/src/commonMain` returns **only 2 hits**, both in `NavigationButton.kt:6-7` (`Icons` + `Icons.AutoMirrored.Filled.ArrowBack`). The whole 6+ MB icon-extended artifact is pulled in for **a single `Icons.AutoMirrored.Filled.ArrowBack`**. Two paths:
  1. **Replace** `Icons.AutoMirrored.Filled.ArrowBack` with `YallaIcons.ArrowBack` from `:resources` (assuming the icon exists or can be added). Drop the `compose.materialIconsExtended` dep entirely.
  2. **Demote** to `compose.materialIcons` (the non-extended variant, which ships only the basic Material icons including `Icons.Default.ArrowBack`). Note that the `AutoMirrored` namespace requires the *extended* artifact.
  - **Recommend path 1** — `:resources` already ships custom icons (`YallaIcons.Calendar`, `YallaIcons.Star`, `YallaIcons.FocusOrigin`, `YallaIcons.PinShadow`, `YallaIcons.Checked`/`Unchecked`); adding an `ArrowBack` is consistent. **REWRITE candidate** in section 4. **(~30 min including resource addition; saves ~6 MB transitive size.)**
- **`compose.components.resources`** (line 19) — used: `org.jetbrains.compose.resources.painterResource` (in `LocationPin.kt`, `SensitiveButton.kt`, `SplashOverlay.kt`, `SearchPin.kt`), `stringResource` (in 5 files), `StringResource` (public type on `GenderResource.kt:16`'s `val GenderKind.resource: StringResource?`). The `:resources` module already declares `api(compose.components.resources)`, so the `StringResource` symbol arrives transitively. The `painterResource()` and `stringResource()` *functions* are internal-only; same posture as `design` G13. Keep `implementation`.
- **`compose.ui.tooling.preview`** (line 20) — `@Preview` annotation imports: `grep -rn "import androidx.compose.ui.tooling" primitives/src/commonMain` returns **11 hits across 11 files** — `DotsIndicator`, `LoadingIndicator`, `LoadingDialog`, `NavigationButton`, `PrimaryButton`, `SecondaryButton`, `SensitiveButton`, `IconButton`, `TextButton`, `TopBar`, `LargeTopBar`. **Used for `@Preview` annotations on private preview composables.** Keep `implementation`.
  - **Caveat:** `@Preview` previews are useful at development time but live in `commonMain` source where they'll be compiled into the published artifact. CLAUDE.md doesn't forbid this; it's a wash. Flag for later review.
- **`compottie`** + **`compottie.resources`** (lines 23-24) — verified: `grep -rn "import io.github.alexzhirkevich.compottie" primitives/src/commonMain` returns **4 hits, all in `pin/SearchPin.kt`** (`Compottie`, `LottieCompositionSpec`, `rememberLottieComposition`, `rememberLottiePainter`). The build comment "// Lottie animations (for SearchPin)" is accurate — single-component dependency. Used internally only; not in public signatures. Keep `implementation` for both.
- **`constraintlayout`** (line 27) — verified: `grep -rn "import androidx.constraintlayout" primitives/src/commonMain` returns **1 hit, `pin/LocationPin.kt:56`** (`ConstraintLayout`). Single-component dependency. Used internally only. Keep `implementation`.
- **`compose.ui.tooling`** (line 37, `androidRuntimeClasspath`) — runtime classpath for Android Studio's `@Preview` rendering. Not a build-time dep; consumed at IDE preview render time. Keep.

### Recommended `Depends on` block for `primitives/MODULE.md`

```
## Depends on

- `core` (implementation) — `or0`, `formatArgs` utils, `GenderKind` enum.
- `design` (implementation) — `System` accessor, `YallaTheme` (preview-only).
- `resources` (implementation) — `Res` drawables/strings/icons, `StringResource`
  in `GenderResource`'s public surface (only public type).
- `platform` (implementation) — `NativeLoadingIndicator` consumed by
  `ButtonLayout`, `LoadingIndicator`, `LoadingDialog`. `ToolbarAction`/`Icon`
  typealias re-export (drop if dead).
- `compose-runtime` (api) — `@Composable`, `@Immutable` saturate the public
  surface. Promoted from `implementation`.
- `compose-foundation` (api) — `RowScope`, `BorderStroke`, `KeyboardOptions`,
  `TextFieldState`, `TextSelectionColors` etc. in public component params.
  Promoted from `implementation`.
- `compose-material3` (implementation) — Material3 components consumed
  internally; not in public signatures.
- `compose-components-resources` (implementation) — `painterResource()` /
  `stringResource()` helpers internally. `StringResource` re-exported via
  `:resources`.
- `compose-ui-tooling-preview` (implementation) — `@Preview` annotations on
  private preview composables in 11 files.
- `compottie`, `compottie-resources` (implementation) — `pin/SearchPin.kt`
  only. Single-component dep.
- `constraintlayout-compose-multiplatform` (implementation) — `pin/LocationPin.kt`
  only. Single-component dep.

Drop: `compose.materialIconsExtended` after `NavigationButton.kt:7` swaps
to `YallaIcons.ArrowBack` (~6 MB transitive size win).
```

(Promote `compose.runtime` and `compose.foundation` to `api`. Drop `compose.materialIconsExtended` after the `NavigationButton` icon swap. SDK-internal deps stay `implementation` since none of `core`/`design`/`resources`/`platform` types appear in public signatures except `StringResource` from `:resources` — and `:resources` itself re-exports `compose.components.resources` `api`, so `StringResource` arrives transitively.)

### SDK-internal deps confirmation

- Four SDK-internal deps after cleanup: `:core`, `:design`, `:resources`, `:platform`. All `implementation`-scoped — none of their types saturate primitives' public surface except for `StringResource` which arrives via `:resources`'s `api`-scoped `compose.components.resources`.
- No cycles. primitives → {core, design, resources, platform} is a fan-out edge; none of the four depend back on primitives.
- No surprising imports.

---

## 3. Restructure candidates (criterion 9-3)

### `wc -l` summary (commonMain only — no platform actuals)

```
564  pin/LocationPin.kt              (longest)
321  field/NumberField.kt
237  button/SensitiveButton.kt
231  field/SearchField.kt
230  button/SecondaryButton.kt
230  button/PrimaryButton.kt
229  otp/PinRow.kt
229  button/TextButton.kt
210  indicator/StripedProgressBar.kt
210  button/IconButton.kt
208  topbar/TopBar.kt
207  field/PrimaryField.kt
198  button/GenderButton.kt
193  field/DateField.kt
192  topbar/LargeTopBar.kt
189  button/BottomSheetButton.kt
184  indicator/SplashOverlay.kt
175  rating/RatingRow.kt
168  button/NavigationButton.kt
162  indicator/LoadingIndicator.kt
159  indicator/DotsIndicator.kt
150  dialog/LoadingDialog.kt
120  button/ButtonLayout.kt
108  transformation/PhoneVisualTransformation.kt
104  pin/SearchPin.kt
 93  transformation/MaskFormatter.kt
 90  transformation/NumberVisualTransformation.kt
 81  util/SquareSize.kt
 22  util/GenderResource.kt
  8  navigation/ToolbarAction.kt
```

### God-file candidates (>300 lines or >5 distinct responsibilities)

- **`pin/LocationPin.kt` (564 lines)** — **CROSSES THE 300-LINE THRESHOLD**. Responsibilities:
  - `LocationPinColors` data class (8 fields)
  - `LocationPinDimens` data class (12 fields)
  - `LocationPin()` public composable (170 lines including animation effects + ConstraintLayout)
  - `PinContent()` private composable (76 lines)
  - `PinStick()` private composable (~30 lines)
  - `PinHeader()` private composable (~35 lines)
  - `LocationPinDefaults` object with `colors()` + `dimens()` factories
  - Two private constants
  - **Five distinct composables + two data classes + one object factory** in one file. Cohesive (single feature: animated location pin), but at 564 lines this is the only file in primitives that triggers criterion 11's god-file rule. **Recommend splitting**:
    - `pin/LocationPin.kt` — public `LocationPin()` composable + LocationPinColors + LocationPinDimens + LocationPinDefaults (~250 lines).
    - `pin/internal/PinContent.kt`, `PinStick.kt`, `PinHeader.kt` — private composables (~140 lines split across three files).
  - **REWRITE — sub-100 LINES** (move-only, no behavior change). Lands in wave-3 commit. (~40 min including imports)
- **`field/NumberField.kt` (321 lines)** — **CROSSES THE 300-LINE THRESHOLD**. Responsibilities:
  - `NumberFieldColors` data class (9 fields)
  - `NumberFieldDimens` data class (3 fields)
  - `NumberFieldDefaults` object with `colors()`, `textStyle()`, `dimens()` factories
  - `NumberField()` public composable (~85 lines)
  - `private object PhoneVisualTransformation` — a ~40-line file-private VisualTransformation that formats `(XX) XXX XX XX`
  - The private `PhoneVisualTransformation` shadows the *public* `PhoneVisualTransformation` class in `transformation/PhoneVisualTransformation.kt` — **same name, different type**. This is dual-confusing: (1) it's a god-file split candidate and (2) it's a name-collision worth resolving. **Recommend splitting**:
    - Keep `NumberField.kt` (~280 lines) with the data classes + composable + Defaults.
    - Move the `private object PhoneVisualTransformation` body into a new file-private `transformation/InternalPhoneNumberFieldTransformation.kt` (or rename the local object inline since it's `private` to NumberField). Trivial fix: rename the private object in-place to `NumberFieldPhoneTransformation` to remove the name collision; no need to split the file. Sub-100-line. (~10 min)
  - The bigger split decision rolls into the **dead-code finding for `transformation/PhoneVisualTransformation.kt`** in section 1 bucket 2-4 — if the public class is dropped, this collision evaporates.

### Sub-package organization

- `primitives/src/commonMain/kotlin/uz/yalla/primitives/`
  - `button/` — 9 files (PrimaryButton, SecondaryButton, TextButton, IconButton, NavigationButton, BottomSheetButton, SensitiveButton, GenderButton, ButtonLayout). Cohesive — every button variant lives here.
  - `dialog/` — 1 file (LoadingDialog). **Single-file package.**
  - `field/` — 4 files (PrimaryField, NumberField, SearchField, DateField). Cohesive.
  - `indicator/` — 4 files (DotsIndicator, LoadingIndicator, SplashOverlay, StripedProgressBar). Cohesive.
  - `navigation/` — 1 file (ToolbarAction typealias re-export). **Single-file package + dead code candidate.**
  - `otp/` — 1 file (PinRow). **Single-file package.**
  - `pin/` — 2 files (LocationPin, SearchPin). Cohesive.
  - `rating/` — 1 file (RatingRow). **Single-file package.**
  - `topbar/` — 2 files (TopBar, LargeTopBar). Cohesive.
  - `transformation/` — 3 files (PhoneVisualTransformation, NumberVisualTransformation, MaskFormatter). Cohesive — but two of three are dead.
  - `util/` — 2 files (SquareSize, GenderResource). Cohesive (modifier + extension property).

### Single-file-package decision

The four single-file packages (`dialog/`, `navigation/`, `otp/`, `rating/`) follow the same per-component-type convention as `design/motion/`, `design/radius/`, `design/space/`, and `design/theme/`. **Keep all package boundaries as-is** — same precedent as `design` G14. (~0 min)

If `navigation/ToolbarAction.kt` is dropped per section 1 bucket 2-4, the single-file `navigation/` package goes away — no further action. (~0 min)

### `field/` and `button/` extraction discussion

The prompt asks specifically whether `field/*` files share a common pattern that could be extracted as a base, or whether `button/*` files duplicate the Colors/Dimens/Defaults boilerplate.

**`button/` analysis:**

- `PrimaryButton`, `SecondaryButton`, `TextButton` are **near-mechanical copies**. The differences:
  - Default `MinHeight`: 60 / 60 / 40 dp.
  - Default `ContentPadding`: 24/16 / 24/16 / 12/8 dp.
  - Default `Shape`: 16 / 16 / 20 dp.
  - Default colors: `button.active`/`text.white`, `button.tertiary`/`background.base`, `Color.Transparent`/`text.base`.
  - Identical class-level shape: `*ButtonColors(containerColor, contentColor, disabledContainerColor, disabledContentColor)` with `containerColor(enabled)`/`contentColor(enabled)` resolvers.
  - Identical `*ButtonDimens(minHeight, contentPadding, shape, iconSpacing)`.
  - Identical composable body: delegates to `ButtonLayout` with the same params shape.
- **The ~210 lines per file are 90% boilerplate.** If extracted, a single `data class ButtonStyle(colors, dimens)` would suffice; the three components become parameter-only configurations.
- **However**: this is the **gold-standard pattern documented in `MODULE.md` lines 11-18** (which references the now-deleted `COMPONENT_STANDARD.md`). The pattern was *deliberate* — making each component a top-level public type with its own data classes + Defaults gives consumers a stable, predictable API surface and lets each variant evolve independently.
- **Flag, don't auto-suggest.** This is a designed-in level of duplication, not a refactoring target. If we collapse `Primary`/`Secondary`/`Text` into a single `Button(style: ButtonStyle)` API, we lose the discoverability of `PrimaryButton(...)`, `SecondaryButton(...)` and complicate every consumer migration. **Recommend keep the duplication, document the rationale in MODULE.md notes.** (~0 min — no action.)

**`field/` analysis:**

- `PrimaryField`, `NumberField`, `SearchField`, `DateField` are **structurally divergent**:
  - `PrimaryField` wraps Material3 `OutlinedTextField` with the new `TextFieldState` API.
  - `NumberField` is a custom container with country-code prefix + Material3 `TextField` (uses the legacy `value: String` API).
  - `SearchField` wraps `BasicTextField` inside a `Card` with leading/trailing slots.
  - `DateField` wraps a `Card` with a `Text` + `Icon`, no input.
- The Colors/Dimens shapes are similar but the underlying composables are genuinely different. **No extraction candidate** — these are atoms, not specializations of a common base. (~0 min)

### Restructure recommendations summary

| Action | File(s) | Lines | Gate |
| ------ | ------- | ----- | ---- |
| Split `LocationPin.kt` into 4 files | `pin/LocationPin.kt` (564) | move-only ~140 lines | no |
| Rename private `PhoneVisualTransformation` in `NumberField.kt:280` | `field/NumberField.kt` | ~5 lines | no |
| Drop `navigation/` package + file | `navigation/ToolbarAction.kt` | 8 lines deleted | no (depends on bucket-2-4 decision) |
| Drop `transformation/Phone*` + `transformation/Number*` files | `transformation/*` | ~200 lines deleted | yes (dead public API) |

Total restructure effort: ~2-3 hours including downstream sweep.

---

## 4. Quality / rewrite candidates (criterion 11)

### Colors + Dimens + Defaults pattern compliance

Per `primitives/MODULE.md` lines 11-18 (which references the deleted `COMPONENT_STANDARD.md`):

> Each component should have `{Component}Colors` data class, `{Component}Dimens` data class, `{Component}Defaults` object with factory functions `colors()` / `dimens()` reading the current theme via `System.color.*`, `System.font.*`, `System.space.*`, `System.radius.*`.

**Verification per component:**

| Component | `Colors` | `Dimens` | `Defaults` | `System.color` | `System.font` | `System.space` | `System.radius` |
| --------- | -------- | -------- | ---------- | -------------- | ------------- | -------------- | --------------- |
| PrimaryButton | yes | yes | yes | yes | no (style outside Defaults) | no | no |
| SecondaryButton | yes | yes | yes | yes | no | no | no |
| TextButton | yes | yes | yes | yes | no | no | no |
| IconButton | yes | yes | yes | yes | n/a | no | no |
| NavigationButton | yes | yes | yes | yes | n/a | no | no |
| BottomSheetButton | yes | yes | yes | yes | n/a | no | no |
| SensitiveButton | yes | yes | yes | yes | yes (param) | no | no |
| GenderButton | yes | yes | yes | yes | yes (param) | no | no |
| LoadingDialog | yes | yes | yes | yes | n/a | no | no |
| LoadingIndicator | yes | yes | yes | yes | n/a | no | no |
| DotsIndicator | yes | yes | yes | yes | n/a | no | no |
| SplashOverlay | yes | yes | yes | yes | yes (inline body) | no | no |
| StripedProgressBar | yes | yes | yes | yes | n/a | no | no |
| LocationPin | yes | yes | yes | yes | yes (params) | no | no |
| **SearchPin** | **NO** | yes | yes | n/a | n/a | n/a | n/a |
| TopBar | yes | yes | yes | yes | yes (style outside Defaults) | no | no |
| LargeTopBar | yes | yes | yes | yes | yes (param) | no | no |
| RatingRow | yes | yes | yes | yes | n/a | no | no |
| PinRow | yes | yes | yes | yes | yes (`digitStyle()`) | no | no |
| PrimaryField | yes | yes | yes | yes | yes (`textStyle()`) | no | no |
| NumberField | yes | yes | yes | yes | yes (`textStyle()`) | no | no |
| SearchField | yes | yes | yes | yes | yes (inline body) | no | no |
| DateField | yes | yes | yes | yes | yes (`textStyle()`) | no | no |

**Findings:**

- **`pin/SearchPin.kt`** — has `SearchPinDimens` + `SearchPinDefaults` but **NO `SearchPinColors`**. The component is Lottie-animation-based (no theme colors needed), so the omission is justified. **Acceptable carve-out, document in MODULE.md notes.** (~0 min)
- **`System.space.*` consumption is ZERO.** Verified: `grep -rn "System\.space\|System\.radius" primitives/src/commonMain` returns no matches. Every `*Dimens.dimens()` factory uses **hardcoded `dp` values** (`12.dp`, `16.dp`, `24.dp`, `40.dp`, `60.dp`) instead of reading from the theme. Examples:
  - `PrimaryButton.kt:158-164`: `MinHeight = 60.dp`, `ContentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)`, `Shape: Shape = RoundedCornerShape(16.dp)`.
  - `SecondaryButton.kt:158-164`: same hardcoded values.
  - `IconButton.kt:131-137`: `Size = 44.dp`, `IconSize = 22.dp`, `Shape: Shape = RoundedCornerShape(12.dp)`.
- **`System.radius.*` consumption is ZERO.** Same shape — every `RoundedCornerShape(N.dp)` in the module is a literal, not `System.radius.medium`/`large`/etc.
- **This is a real gap.** The MODULE.md pattern claim is half-implemented: components read `System.color.*` and `System.font.*` but ignore `System.space.*` and `System.radius.*`. Two reasons this matters:
  1. **Theme cohesion.** A consumer that overrides `RadiusScheme` on `YallaTheme` won't see component shapes change — every component bakes `RoundedCornerShape(16.dp)` etc. in the Defaults.
  2. **Cross-cutting consistency.** Hardcoded `16.dp` appears in 8+ Defaults; if Yalla's brand decides to round corners harder, every component needs editing instead of a single `RadiusScheme` override.
  - **Two paths:**
    1. **Fix.** Sweep every `*Defaults.dimens()` to read from `System.space.*` / `System.radius.*` where applicable. ~30 components × ~5 dimens each = ~150 substitutions. **REWRITE >100 LINES — NEEDS GATE.** Behavior may shift if `standardSpaceScheme()` / `standardRadiusScheme()` values diverge from the current literals — verify per-component before applying. (~3-4h)
    2. **Document and defer.** Note in MODULE.md that primitives currently uses literal `dp` for spacing/radius and reading from the theme is a future project. (~5 min)
  - **Recommend path 2 for this audit.** The literal-vs-theme decision is product-aesthetic (the existing literals match the current Yalla brand) and a sweep risks visual regressions. **Flag for Islom.**

### `@Stable` / `@Immutable` annotations

- **All `*Colors` and `*Dimens` data classes carry `@Immutable`.** Verified: `grep -B1 "^data class" primitives/src/commonMain/kotlin/uz/yalla/primitives/**/*.kt` shows `@Immutable` immediately above every `data class *Colors`, `data class *Dimens`. Total: 30 `@Immutable` annotations across 30 data classes.
- **`*Defaults` objects** are not annotated — they're objects, not data classes; Compose's stability inference handles them correctly.
- **The `containerColor(enabled)`/`contentColor(enabled)` member functions on `*ButtonColors`** are pure — same-input-same-output. They're stable by Compose's inference rules.
- **No gap.** The `@Immutable` posture is universal and correct. (~0 min)

### Static singletons over `Local*.current`

- Verified: `grep -rn "AppColors\|AppFonts\|AppDimens\|AppTokens" primitives/src/commonMain` returns zero matches.
- All theme reads go through `System.color.*` / `System.font.*` (which read `LocalColorScheme.current` / `LocalFontScheme.current` per `design/theme/Theme.kt:151-181`).
- **No deviation.** (~0 min)

### Stale `System.color.<token>` references

- Verified: `grep -rnE "System\.color\.[a-zA-Z]+\.(primary|brand|icon)" primitives/src/commonMain` returns 4 matches, all `System.color.background.brand` (in `RatingRow.kt:75`, `LocationPin.kt:504`, `DotsIndicator.kt:121`, `LoadingDialog.kt:118`).
- `background.brand` **does exist** on `ColorScheme.Background` (`design/color/ColorScheme.kt`). **No stale reference.**
- Verified: no `System.color.icon.brand`, no `System.color.background.primary`. **No gap.** (~0 min)

### Hardcoded product copy

- Verified: `grep -rn '[А-Яа-яЁё]\|[ʻ]' primitives/src/commonMain` returns **zero matches.** No Russian/Uzbek string literals.
- All user-visible text comes from `:resources` via `stringResource(Res.string.*)` (e.g., `LocationPin.kt:396` → `Res.string.format_time_min_short`; `NumberField.kt:220` → `Res.string.auth_phone_country_code`; `SensitiveButton.kt:126` → `Res.string.order_cancel_action_yes`).
- **One borderline case:** `NavigationButton.kt:92` — `contentDescription: String? = "Navigate back"`. **Hardcoded English string in default param value.** This is a content description for accessibility services, not a visible UI text, but it IS a user-visible string for screen readers. **Recommend** either:
  - Wrap in `stringResource(Res.string.common_navigate_back)` and add the string to `:resources`. Sub-100-line. (~10 min including resource entry).
  - Or default to `null` (which is what `TopBar.kt:111`'s `navigationIconContentDescription` does — and the documented advice in TopBar's KDoc is to provide a localized string).
  - **Recommend the second** — drop the literal default in `NavigationButton.kt:92` and require callers to provide a localized description (or accept null = decorative). Aligns with the `TopBar` / `LargeTopBar` posture. **Flag for Islom — sub-100-line.** (~5 min)
- **Two more borderline cases (placeholders):**
  - `DateField.kt:149` — `placeholder: String = "DD.MM.YYYY"`. Literal default for the placeholder text. ASCII-only and format-coded, not localized — but Russian/Uzbek users see the same DD.MM.YYYY format (which is fine for date format strings). **Acceptable, defer.**
  - `SearchField.kt:173` — `placeholder: String = ""`. Empty default; no copy contamination.

### Non-idiomatic Kotlin

- **`primitives/transformation/PhoneVisualTransformation.kt:34-70`** — manually iterates `mask.indices` building a `StringBuilder`. Idiomatic; not a candidate.
- **`primitives/transformation/NumberVisualTransformation.kt:37-52`** — delegates to `MaskFormatter.format`. Clean; not a candidate.
- **`primitives/util/SquareSize.kt:32-38`** — `when` with a `position == 0.5f` special-case where both branches end up calling the same `createSquareSizeModifier(position)`. The `==` comparison on Float is shaky (NaN edge case), and the special-case is meaningless because both branches do the same thing. **Bug-shaped redundancy.** Sub-100-line. (~5 min — replace `when` with direct call).
- **`primitives/pin/LocationPin.kt:1-8`** — `@file:Suppress("LongMethod", "DestructuringDeclarationWithTooManyEntries", "UnusedParameter")` block targets detekt rules that don't apply (no detekt config per CLAUDE.md). Also includes `// LongMethod: ...` / `// UnusedParameter: ...` rationale comments above the suppress. **Dead suppression.** Drop the `@file:Suppress` and the comment block. (~3 min)
- **`primitives/pin/LocationPin.kt:262`** — `val (shadow, stick, content, header) = createRefs()` — a destructuring on `ConstraintLayoutScope.createRefs()` returning 4 refs. The `@file:Suppress("DestructuringDeclarationWithTooManyEntries")` was for this; without detekt that suppression is moot. (~0 min — covered by drop above)
- **`primitives/button/SensitiveButton.kt:166-170`** — `text = when { isEnabled -> resolvedConfirmText; else -> resolvedCountdownText.formatArgs(countdown) }`. `when` with two branches reads as `if/else` would; not unidiomatic, but `if` is shorter. Borderline. (~0 min)
- **`primitives/field/PrimaryField.kt:206-207` and `primitives/field/SearchField.kt:230-231`** — both files define a private `Modifier.applyFocusRequester(focusRequester)` extension. Same body, two declarations. Could be extracted to a shared `internal fun Modifier.applyFocusRequester(focusRequester: FocusRequester?)` in a `field/internal/Focus.kt` or `util/`. ~10 lines saved. (~10 min). Sub-100-line; flag, defer.
- **`primitives/pin/LocationPin.kt:294-302`** — `icon = icon ?: { Icon(painter = ..., tint = colors.icon, modifier = Modifier.size(18.dp)) }`. The `18.dp` literal here is hardcoded — the `LocationPinDimens` data class has 12 fields including a default `iconSize` slot but the inline `18.dp` short-circuits it. Either:
  - Add a `defaultIconSize: Dp = 18.dp` field to `LocationPinDimens`, or
  - Drop the `18.dp` and use `dimens.contentSize / 2.5` or similar derivation.
  - **Borderline — leave alone.** (~0 min)

### Per-file deep-dive on >200-line files

#### `pin/LocationPin.kt` (564 lines) — RECOMMENDED REWRITE

- **Multiple distinct components.** 4 composables (`LocationPin`, `PinContent`, `PinStick`, `PinHeader`) + 2 data classes + 1 Defaults object + 2 constants.
- **Mixed concerns.** Animation orchestration (lines 211-257) + ConstraintLayout (262-329) + per-piece drawing logic. The animation lifecycle and the layout reads as two responsibilities.
- **Recomposition correctness.** `Animatable` instances (lines 207-209) are wrapped in `remember { ... }` correctly. `LaunchedEffect(jumping)` blocks (211, 233) re-key on the `jumping` param — correct. `rememberInfiniteTransition` (line 343) for the spinner rotation is keyed correctly.
- **Best-fit split** documented in section 3.

#### `field/NumberField.kt` (321 lines) — RECOMMENDED REWRITE

- **Multiple distinct components.** `NumberField` composable + 2 data classes + Defaults + a private `object PhoneVisualTransformation` that's a separate concern from the field itself.
- **Mixed concerns.** Field rendering + visual transformation logic. The `PhoneVisualTransformation` shadows the public `class PhoneVisualTransformation` at `transformation/PhoneVisualTransformation.kt` — confusing name collision.
- **Recomposition correctness.** `var isFocused by remember { mutableStateOf(false) }` (line 204) — focus state lives in the composable; correct. `onFocusChanged` callback updates the state — correct.
- **Recommended fix.** Rename the private object to `NumberFieldPhoneTransformation` (or even keep the identifier but rename the public class — see section 1 bucket 2-4 decision). Sub-100-line.

#### `button/SensitiveButton.kt` (237 lines) — minor cleanups only

- **Mixed concerns.** Composable + countdown animation + Lifecycle coupling (`LocalLifecycleOwner` + `repeatOnLifecycle(Lifecycle.State.RESUMED)`).
- **Recomposition correctness.** `Animatable` wrapped in `remember` (line 130). `derivedStateOf` for `countdown`/`isEnabled` — correct (recomposition only when `progress.value` crosses thresholds). `LaunchedEffect(lifecycleOwner)` keys correctly.
- **Concern:** The countdown resets on every `RESUMED` lifecycle event (line 137). Pro: defends against background→foreground stale countdowns. Con: if the user backgrounds the app mid-countdown for a second and returns, the countdown restarts from 0. **Real product behavior.** Not a bug, but a subtle UX detail worth documenting in KDoc. (~5 min)

#### `field/SearchField.kt` (231 lines), `button/SecondaryButton.kt` (230), `button/PrimaryButton.kt` (230), `otp/PinRow.kt` (229), `button/TextButton.kt` (229) — boilerplate-heavy, no rewrite

- All five are dominated by the Colors+Dimens+Defaults pattern + a relatively short composable body. The bulk is KDoc paraphrase (section 1) and structural duplication (button family). Nothing to rewrite.

### Architecture violations — full pass

- **`try { … } catch { … }` in business logic** — verified: `grep -rn "try " primitives/src/commonMain` returns zero matches outside `Try` references inside JSON. No try/catch anywhere. Clean.
- **Mappers as classes** — none. primitives has no DTO surface.
- **Service classes / `Api` naming** — N/A.
- **`InMemoryTokenProvider` / manual `Authorization` header / `AuthEventBus`** — N/A.
- **Custom MVI** — N/A; primitives are stateless.
- **Hardcoded product copy** — covered above. Single borderline case (`NavigationButton.kt:92`).
- **String-typed identifiers that should be value classes** — N/A in primitives.

### Untestable shape

- Most components are `@Composable` UI primitives. Pure data-class testing (the current posture in `commonTest`) covers the `Colors`/`Dimens` equality and the `containerColor(enabled)`/`contentColor(enabled)` helper functions but **does not exercise the rendered output, focus state, or animation behavior**. Reading the existing tests, they're closer to **structural-equality tests** than **component-behavior tests**.
- Components with non-trivial internal state and behavior that resists `commonTest` data-class testing:
  - `LocationPin` — animation lifecycle, ConstraintLayout positioning.
  - `SearchPin` — Lottie composition loading.
  - `SensitiveButton` — countdown progress + lifecycle coupling.
  - `PinRow` — error-shake animation, focus management.
  - `StripedProgressBar` — Canvas drawing + infinite transition.
  - `DotsIndicator` — `animateDpAsState` for selected-dot width.
- **These resist commonTest without `runComposeUiTest`**. The current `commonTest` posture is acceptable for what it covers (data-class shape) but **doesn't reach the load-bearing behavior**.
- **Recommend** in section 6 below: keep the data-class equality bar as-is, add `runComposeUiTest`-based behavior tests for the animation/focus components in a wave-8 follow-up. **Flag for Islom — wave-8 scope decision.**

### Summary of section 4 rewrite candidates

| Item | Lines | Gate? |
| ---- | ----- | ----- |
| Drop `@file:Suppress` block in `LocationPin.kt:1-8` | ~8 | no |
| Simplify `Modifier.squareSize` redundant `when` | ~5 | no |
| Localize `NavigationButton.contentDescription` default | ~5 | no |
| Rename private `PhoneVisualTransformation` in NumberField | ~5 | no |
| Drop `materialIconsExtended` after `YallaIcons.ArrowBack` swap | ~10 + dep change | no |
| **Split `LocationPin.kt` into 4 files (move-only)** | ~140 lines moved | no (sub-100 net) |
| Extract shared `Modifier.applyFocusRequester` | ~10 | no |
| **Drop dead `transformation/Phone*` + `Number*` files** | **~200 deleted** | **yes** |
| Drop dead `navigation/ToolbarAction.kt` | ~8 | no |
| Drop unused `LoadingIndicatorDimens.strokeWidth(size)` | ~12 | no |
| Drop `LocationPin.loading` unused param | ~5 | no |
| **`System.space.*` / `System.radius.*` sweep (deferred)** | **~150 substitutions** | **yes — defer** |

---

## 5. Promote/demote candidates (criterion 1)

Applied lego test to every public type in `primitives/src/commonMain`.

### Bricks (stays in primitives — vast majority)

`PrimaryButton`, `SecondaryButton`, `TextButton`, `IconButton`, `NavigationButton`, `BottomSheetButton`, `SensitiveButton`, `GenderButton`, `LoadingDialog`, `PrimaryField`, `NumberField`, `SearchField`, `DateField`, `DotsIndicator`, `LoadingIndicator`, `LoadingIndicatorSize`, `SplashOverlay`, `StripedProgressBar`, `PinRow`, `LocationPin`, `SearchPin`, `RatingRow`, `TopBar`, `LargeTopBar`, `MaskFormatter`, `Modifier.squareSize`, `GenderKind.resource`. All `*Colors`/`*Dimens`/`*Defaults` companions.

All pass the lego test:
- **No hardcoded product copy** — verified `grep -rn '[А-Яа-яЁё]\|[ʻ]' primitives/src/commonMain` → 0 matches.
- **No screen-shaped or ViewModel-shaped types** — every type is either a composable, a tokens data class, or a util.
- **No Ildam-specific business orchestration.**

### Components with hard-coded business meaning

- **`SensitiveButton`** — defaults to "yes, cancel order" copy via `Res.string.order_cancel_action_yes` / `Res.string.order_cancel_countdown` (lines 126-127). The component itself is generic (countdown + click), but its **default labels reference order-specific strings**. Two paths:
  1. The defaults are just defaults — callers can override `confirmText` / `countdownText`. The localization keys are a convenience for the most common use case (order cancellation). **Brick with order-shaped defaults.** Acceptable.
  2. The component is "Cancel Order Button" by default, which is order-specific. **Should the defaults be neutral (`Res.string.common_action_yes` / `Res.string.common_countdown_format`)?**
  - **Recommend path 2.** The button is named `SensitiveButton`, not `CancelOrderButton`; default copy should be neutral. Add neutral resource keys, change the defaults. Sub-100-line. (~15 min including resource adds). **Flag for Islom — minor clean-up.**
- **`GenderButton`** — takes `GenderKind` from `core` and renders gender names. The "gender" semantics is product-shaped (a generic design system would say `SelectableButton(label, isSelected)`). **However**, the component delegates entirely to `GenderKind.resource` (which lives in `primitives/util/`) and uses `Checked`/`Unchecked` icons from `:resources`. It's an atom that combines two existing bricks (the enum + the icons). **Acceptable as a brick** — the genderness is in the enum, not the rendering logic. (~0 min)
- **`LocationPin`** — taxi-app-shaped (a "location pin with timeout" is the canonical taxi-driver-arrival UX). Generic enough for any location-displaying app. **Brick. Keep.** (~0 min)
- **`SearchPin`** — taxi-app-shaped (Lottie-animated search-in-progress). Same posture. (~0 min)
- **`SplashOverlay`** — defaults to `Res.string.location_gps_subtitle` for the message (line 99) — same pattern as `SensitiveButton`. The default is a location-acquisition message ("Getting GPS..."), but the component itself is generic (full-screen overlay + indicator + message). **Same recommendation as SensitiveButton — neutral defaults.** Sub-100-line. (~10 min). **Flag for Islom — minor.**

### Borderline — flag for Islom

- **`SensitiveButton` default copy** — recommend neutral resource keys.
- **`SplashOverlay` default message** — recommend neutral resource keys.
- **`NavigationButton.contentDescription = "Navigate back"`** — covered in section 4 architecture-violations; recommend removing the literal default and requiring callers to localize.

### Demotion candidates (primitives → YallaClient)

- **None observed.** The borderline-product-shaped defaults above are *defaults*, not assemblies — replacing them with neutral defaults keeps every component a brick.
- The hardcoded `(XX) XXX XX XX` phone format inside `NumberField.kt`'s private `PhoneVisualTransformation` (lines 280-321) is **Uzbekistan-specific phone formatting** baked into a generic-named field. But:
  - The format applies to the country code already shown via `Res.string.auth_phone_country_code` (= `+998` for Uzbekistan).
  - Generic phone fields can't really exist — every country has its own format. The right model is "NumberField is the +998 phone field; another country's app would have a `+1NumberField` or a parameterized one."
  - **Borderline — keep as-is.** The product-shape is in the country code (already a localized string from `:resources`), not in the format mask. **Acceptable.**

### Promotion candidates (YallaClient → primitives)

- **None observed in this audit.** Out of scope to enumerate every YallaClient ad-hoc composable for promotion eligibility; that's the wave-6 cross-module review.
- (Caveat: this audit didn't open every YallaClient file for ad-hoc primitives. If the wave-6 scan finds duplicates or near-duplicates of primitives' components, promotion candidates surface there.)

### Notes about hardcoded strings

- No Russian/Uzbek string literals in primitives — verified.
- Three borderline-English-default-strings: `NavigationButton.contentDescription = "Navigate back"`, `DateField.placeholder = "DD.MM.YYYY"`, `SearchField.placeholder = ""`. The first is the only flagged case.
- One borderline-product-keyed-resource pattern: `SensitiveButton` and `SplashOverlay` defaults. Recommend neutralization.

### Verdict for `MIGRATION_LIST.md`

- "## To promote into primitives" — empty for now (cleanup wave will not invent new bricks).
- "## To demote from primitives" — empty.
- "## To decide" — neutralize `SensitiveButton` / `SplashOverlay` default resource keys; localize or `null`-default `NavigationButton.contentDescription`.

---

## 6. Missing tests (criterion 6)

### Inventory by sub-package

#### `primitives/button/`

9 commonMain files, 1 commonTest file (`PrimaryButtonTest.kt` — 170 lines, pure data-class equality tests).

| Component | Test file | Coverage |
| --------- | --------- | -------- |
| PrimaryButton | yes (170 lines) | Colors/Dimens equality + `containerColor(enabled)`/`contentColor(enabled)` resolvers. **No composable UI test.** |
| SecondaryButton | **NO** | gap |
| TextButton | **NO** | gap |
| IconButton | **NO** | gap |
| NavigationButton | **NO** | gap |
| BottomSheetButton | **NO** | gap |
| SensitiveButton | **NO** | gap (countdown lifecycle is the load-bearing behavior; resists commonTest without `runComposeUiTest`) |
| GenderButton | **NO** | gap |
| ButtonLayout (internal) | **NO** | acceptable carve-out (internal API) |

**~7 component test files missing.** Pattern can be copied from `PrimaryButtonTest.kt` (Colors equality + Dimens equality + resolver tests). **~7 × ~150 lines = ~1000 lines of test. ~3-4h.**

#### `primitives/dialog/`

| Component | Test file | Coverage |
| --------- | --------- | -------- |
| LoadingDialog | **NO** | gap (Colors + Dimens equality only — composable behavior resists commonTest) |

#### `primitives/field/`

4 commonMain files, 4 commonTest files. **All covered.**

| Component | Test file | Coverage |
| --------- | --------- | -------- |
| PrimaryField | yes (91) | Colors/Dimens equality. |
| NumberField | yes (125) | Colors/Dimens equality + `MAX_PHONE_DIGITS` constant test. |
| SearchField | yes (66) | Colors/Dimens equality. |
| DateField | yes (107) | Colors/Dimens equality + `formatDisplay()` private function tests. |

#### `primitives/indicator/`

4 commonMain files, 1 commonTest file.

| Component | Test file | Coverage |
| --------- | --------- | -------- |
| DotsIndicator | yes (63) | Colors/Dimens equality. |
| LoadingIndicator | **NO** | gap |
| SplashOverlay | **NO** | gap |
| StripedProgressBar | **NO** | gap (Canvas + animation; resists commonTest without `runComposeUiTest`) |

**3 missing test files. ~450 lines.**

#### `primitives/navigation/`

`ToolbarAction.kt` is two typealias re-exports. **No test required** (and bucket-2-4 says delete the file). **No gap.**

#### `primitives/otp/`

| Component | Test file | Coverage |
| --------- | --------- | -------- |
| PinRow | yes (117) | Colors/Dimens equality. **No shake animation or onComplete callback test.** |

#### `primitives/pin/`

| Component | Test file | Coverage |
| --------- | --------- | -------- |
| LocationPin | **NO** | gap (Colors + Dimens equality straightforward; jump animation + ConstraintLayout resist commonTest) |
| SearchPin | **NO** | gap (Lottie loading; **inherently hard to test** — accepted carve-out, see notes below) |

#### `primitives/rating/`

| Component | Test file | Coverage |
| --------- | --------- | -------- |
| RatingRow | yes (63) | Colors/Dimens equality. |

#### `primitives/topbar/`

2 commonMain files, 2 commonTest files. **All covered.**

| Component | Test file | Coverage |
| --------- | --------- | -------- |
| TopBar | yes (114) | Colors/Dimens equality. |
| LargeTopBar | yes (130) | Colors/Dimens equality + titleTopSpacing-related tests. |

#### `primitives/transformation/`

3 commonMain files, 0 commonTest files.

| Component | Test file | Coverage |
| --------- | --------- | -------- |
| PhoneVisualTransformation | **NO** | gap (but section 1 bucket 2-4 candidates for deletion) |
| NumberVisualTransformation | **NO** | gap (same) |
| MaskFormatter | **NO** | gap — `format` is the only used function, deserves a test. `countPlaceholders` and `extractRaw` would too if kept. |

**~30-50 min for `MaskFormatter` tests alone.** Pure-function testing — straightforward. ~10-15 tests covering the format/extract round-trip + edge cases (empty input, mask shorter than text, no mask chars, etc.).

#### `primitives/util/`

| Component | Test file | Coverage |
| --------- | --------- | -------- |
| `Modifier.squareSize` | **NO** | gap — geometric layout modifier; the `equals`/`hashCode` overrides on the private `SquareSize` class deserve a test. ~5 tests. |
| `GenderKind.resource` | **NO** | gap — three branches (`Male`/`Female`/`NotSelected → null`). Trivial. ~3 tests. |

### Inherently hard to test in commonTest (accepted carve-outs)

- **`SearchPin`** — Lottie animation requires Compose runtime + resource loading. Accepted carve-out.
- **`StripedProgressBar`** — Canvas drawing + infinite transition. Accepted carve-out for behavior; data-class tests still possible.
- **`SensitiveButton`** — `LocalLifecycleOwner` + `repeatOnLifecycle` lifecycle coupling. Accepted carve-out for behavior; data-class tests still possible.
- **`LocationPin`** — ConstraintLayout requires the `:constraintlayout` Compose runtime. The data-class equality tests for `LocationPinColors`/`LocationPinDimens` are perfectly testable in commonTest; only the rendered behavior is gated.

### Pattern note

**Existing tests are pure data-class equality and resolver-function tests.** They use `kotlin.test` + `assertEquals` / `assertNotEquals` only. **Zero usage of `runComposeUiTest`** verified (`grep -rn "runComposeUiTest" primitives/src/commonTest` returns nothing). This is consistent with the prompt's guidance:

> Note: primitives' tests are mostly Compose UI tests using `runComposeUiTest`. Some components are inherently hard to test in commonTest without a real renderer (Lottie animations, constraint-layout dependent layouts) — flag those as accepted carve-outs separately.

— but **the prompt is wrong about this**. The current tests are NOT Compose UI tests; they're data-class tests. **No `runComposeUiTest` infrastructure exists.** Flagging this for clarity:

- **Either** add `runComposeUiTest` infrastructure (the convention plugin would need `compose.uiTest` in `commonTest.dependencies` — currently `primitives/build.gradle.kts:30-32` declares only `kotlin("test")`).
- **Or** keep the data-class equality bar as the test floor and accept that component-behavior is exercised only through the `SipDemo` / `showcase` runtime-preview catalog (per CLAUDE.md "showcase = component preview catalog").

**Recommend** adding `compose.uiTest` and writing `runComposeUiTest`-based behavior tests for at least the animation and focus components in wave-8. **REWRITE — wave-8 scope. Flag for Islom.** (~6-8h for behavior coverage on ~10 components × ~3 tests each.)

### Summary by sub-package

| Sub-package | Files | Files w/ tests | Effort to cover gap (data-class only) | Notes |
| ----------- | ----- | -------------- | -------------------------------------- | ----- |
| `button/` | 9 | 1 | ~3-4h, 7 files | Boilerplate-heavy — copy PrimaryButtonTest pattern. |
| `dialog/` | 1 | 0 | ~30 min | LoadingDialog data-class tests. |
| `field/` | 4 | 4 | 0 | Complete. |
| `indicator/` | 4 | 1 | ~1.5h, 3 files | Loading/Splash/StripedProgress data-class tests. |
| `navigation/` | 1 | 0 | 0 | Drop-candidate. |
| `otp/` | 1 | 1 | 0 | Complete (data-class only). |
| `pin/` | 2 | 0 | ~1h, 2 files | LocationPin data-class tests; SearchPin is the carve-out. |
| `rating/` | 1 | 1 | 0 | Complete. |
| `topbar/` | 2 | 2 | 0 | Complete. |
| `transformation/` | 3 | 0 | ~45 min, 1 file | MaskFormatter pure-function tests only. Drop the other two. |
| `util/` | 2 | 0 | ~30 min, 2 files | SquareSize + GenderResource. |

**Total wave-8 effort estimate (data-class bar):** ~7-8h, ~14 new test files. Brings primitives test count from current 10 files to ~24 files.

**Total wave-8 effort estimate (data-class + behavior bar):** ~14-16h. Brings primitives toward `runComposeUiTest`-based behavior coverage on the ~10 components where behavior is non-trivial.

---

## 7. MODULE.md staleness (criterion 5)

Current `primitives/MODULE.md` (68 lines) uses the **pre-phase-1 form**:

```
# Module primitives

Reusable Compose Multiplatform UI primitives for the Yalla SDK.

[free-form architecture paragraph]

## Architecture

[Colors + Dimens + Defaults pattern explanation, references deleted COMPONENT_STANDARD.md]

# Package uz.yalla.primitives.button
[blurb]

# Package uz.yalla.primitives.dialog
[blurb]

# Package uz.yalla.primitives.field
[blurb]

[... 11 # Package blurbs total]
```

Reference docs (post-phase-1 form):

- `bom/MODULE.md` ✓
- `core/MODULE.md` ✓ (re-read at audit time)
- `data/MODULE.md` ✓
- `design/MODULE.md` ✓
- `foundation/MODULE.md` ✓

Phase-1 form is:

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

- **`> One-line tagline.`** — currently missing. Suggested: `> UI atoms — buttons, fields, indicators, pins, top bars, OTP, rating. Stateless, themed, parameterized.`
- **`## What this is`** — replace the multiple `# Package` blurbs with a tight bulleted list grouped by sub-package. Suggested:
  - **Button family** (`button/`): 8 button variants (`PrimaryButton`, `SecondaryButton`, `TextButton`, `IconButton`, `NavigationButton`, `BottomSheetButton`, `SensitiveButton`, `GenderButton`) plus the internal `ButtonLayout` shared infrastructure.
  - **Field family** (`field/`): `PrimaryField` (outlined), `NumberField` (phone with country code), `SearchField` (Card+BasicTextField), `DateField` (read-only with picker trigger).
  - **Indicators** (`indicator/`): `DotsIndicator` (pager), `LoadingIndicator` (with `Small`/`Medium`/`Large` size variants), `SplashOverlay` (full-screen), `StripedProgressBar` (animated stripes).
  - **Pins** (`pin/`): `LocationPin` (animated map pin with address tooltip + ETA), `SearchPin` (Lottie-based search-in-progress).
  - **TopBars** (`topbar/`): `TopBar` (standard), `LargeTopBar` (large title variant).
  - **Other**: `LoadingDialog` (modal), `PinRow` (OTP digit row), `RatingRow` (star input), `MaskFormatter` (pure mask utility), `Modifier.squareSize` (layout modifier), `GenderKind.resource` extension.
- **`## What this is NOT`** — explicitly:
  - **Not** a UI molecules / sheets layer (those live in `composites`).
  - **Not** a feature module — no screens, no navigation, no business orchestration.
  - **Not** a domain or data module.
  - **Not** a platform-specific layer (those live in `platform`; primitives consumes `NativeLoadingIndicator` from there).
  - **Not** a copy module — strings live in `:resources`; primitives takes user-facing text via parameters.
- **`## Usage`** — 4-6 lines showing a typical button + field composition. Suggested:
  ```kotlin
  YallaTheme {
      Column {
          PrimaryField(state = nameState, placeholder = { Text("Name") })
          PrimaryButton(onClick = onSubmit) { Text("Submit") }
      }
  }
  ```
- **`## Notes`** — fold in:
  1. **The Colors + Dimens + Defaults pattern explanation** (currently in lines 11-16 of the existing MODULE.md, with the now-broken link to `COMPONENT_STANDARD.md`). Inline the full pattern: each component has `*Colors`, `*Dimens`, `*Defaults` with `colors()`/`dimens()` factories reading the current theme via `System.color.*`, `System.font.*`. State the parameter ordering convention (required → modifier → behavioral → styling → slots → content). State that the pattern is convention, not enforced — `SearchPin` has no `Colors` (Lottie animation needs no theming).
  2. **`SensitiveButton` countdown semantics.** The button defaults to localized "yes / cancel" copy aimed at order-cancellation flows; override `confirmText`/`countdownText` to repurpose. The countdown resets on every `RESUMED` lifecycle.
  3. **`LocationPin.loading` parameter.** Currently unused; reserved (per `@file:Suppress("UnusedParameter")`).
  4. **Phone format hardcoded for +998.** `NumberField`'s private `PhoneVisualTransformation` formats `(XX) XXX XX XX` matching the Uzbek country code from `auth_phone_country_code` resource. Other country codes need a different mask.
  5. **`compose.materialIconsExtended` dependency.** Used only for `Icons.AutoMirrored.Filled.ArrowBack` in `NavigationButton`. Wave-3 candidate to drop after replacing with a `:resources`-side icon.
  6. **`@Preview` annotations in commonMain.** 14 previews live in commonMain; rendered via `compose.ui.tooling` runtime classpath in Android Studio.
  7. **`System.space.*` / `System.radius.*` not consumed.** Components use literal `dp` values for spacing/radius. Theme-driven spacing/radius is a future project; flagged in `PRIMITIVES_AUDIT.md` §4.
  8. **Test bar is data-class equality.** `runComposeUiTest`-based behavior testing is a wave-8 candidate, not currently wired.
- **`## Depends on`** — the block from section 2 (4 SDK-internal `implementation` deps, 2 `api`-promoted Compose deps, 7 `implementation` deps).

### Sections to remove

- **`# Package uz.yalla.primitives.button`** through **`# Package uz.yalla.primitives.util`** (lines 20-67) — all 11 per-package blurbs. Per-package KDoc lives on the source, not in MODULE.md. Drop entirely.
- **Lines 9-18** — the free-form Architecture section. Fold the keeper content into the new `Notes` section.
- **The phantom package references** (line 39-41 `model/ + ButtonSize`; line 48-49 `PinView`) — drop with the per-package blurb removal.

### Sections to rewrite

- **Lines 1-7** — opening paragraph. Already says "Reusable Compose Multiplatform UI primitives for the Yalla SDK." Replace with the phase-1 tagline + `What this is` form.

### Cross-check from prompt

- **`COMPONENT_STANDARD.md` reference** (current line 18) — **stale**. The file does not exist. Drop the link, fold the pattern explanation into MODULE.md notes.
- **`# Package uz.yalla.primitives.model`** (current line 39) — **stale**. The package does not exist. Drop.
- **`[ButtonSize]`** reference (current line 41) — **stale**. The type does not exist. Drop.
- **`[PinView]`** reference (current line 49) — **stale**. The type does not exist. Drop.

Total wave-10 effort: full rewrite of MODULE.md from scratch on phase-1 form. **~30 min** (heavier than core/data/foundation rewrites because the `Notes` section absorbs the deleted COMPONENT_STANDARD.md content + several flagged caveats).

---

## 8. Reviewer notes

### Pushback on specific findings

- **Section 1, bucket 2-4 on `transformation/PhoneVisualTransformation.kt` and `NumberVisualTransformation.kt`** — I flagged these as zero-caller dead code, but primitives is a published library. The transformation classes are **plausibly designed for external consumption**:
  - The naming and KDoc match Compose's `VisualTransformation` ecosystem.
  - The mask-and-maskChar shape is a generic API (e.g., a YallaClient or third-party app might use them for credit card formatting).
  - YallaClient's own `CardNumberVisualTransformation` (verified at `feature/payment/.../AddCardSheet.kt:195`) is a **private object** that re-implements similar mask logic — that's the kind of duplication primitives is supposed to prevent.
  - **Two readings:**
    1. **Truly dead** — they were written speculatively, no consumer migrated. Drop.
    2. **Valid public API, callers haven't materialized.** YallaClient's `CardNumberVisualTransformation` should migrate to `NumberVisualTransformation(mask = "____-____-____-____")`; that consumer just hasn't been written.
  - **Recommend (2) — keep with deprecation notice or migration guide.** YallaClient's own private object IS the missing consumer; promotion of that consumer to use the public API is the right move. Same shape as `core` G2 (`ServiceBrand`/`PaymentCard` keep-pending-consumer).
  - **Default position for the audit: KEEP, document the YallaClient migration target as a follow-up.** Flag, defer the deletion. (~0 min in this wave; ~30 min YallaClient migration when scoped.)

- **Section 1, bucket 2-4 on `navigation/ToolbarAction.kt`** — I flagged as zero-caller, but typealiases exist for backward compatibility during migrations. The KDoc says "for backward compatibility." If primitives previously *owned* `ToolbarAction` and it was moved to `:platform`, the typealias preserves consumer compile-time linkage. **However**, alpha versioning (criterion 3) means we don't have to preserve compile-time linkage — alpha is a fluid surface. **Recommend dropping** the typealias in alpha. **Default position: DELETE.** Sub-100-line. (~5 min)

- **Section 4 on the `materialIconsExtended` swap** — I flagged this as a 6 MB transitive size win, but two caveats:
  1. The `compose.materialIconsExtended` artifact ships **inside the same compose runtime container** as the rest. The "6 MB" is artifact size on disk, not on-device size after R8/ProGuard shrinks unused symbols. The actual app-on-device savings might be ~300 KB. **Real but smaller than headline.**
  2. Adding a custom `YallaIcons.ArrowBack` requires designer involvement (the icon needs to match the rest of `YallaIcons.*` style). **~30 min designer time + 10 min code.**
  - **Default position: defer to wave-3, scope the icon design first.** Flag, don't auto-apply.

- **Section 5 on neutralizing `SensitiveButton` / `SplashOverlay` defaults** — I flagged these as product-shaped defaults. Counter: **defaults are convenience for the dominant use case.** A YallaClient app calling `SensitiveButton(onClick = ...)` with no overrides reasonably gets cancellation copy because *that's the dominant use case in this product.* **Three readings:**
  1. **Neutralize.** Defaults should be `Res.string.common_yes` / `Res.string.common_countdown_format`; consumers must override for order cancellation. Maximally generic but maximally-overhead-per-call.
  2. **Keep.** Defaults shipped as-is; convenience wins. Less generic but less ceremony at every call site.
  3. **Two factories.** `SensitiveButtonDefaults.cancelOrder()` returns the order-shaped defaults; `SensitiveButtonDefaults.generic()` returns neutral defaults; the composable's defaults call `cancelOrder()`.
  - **Default position: keep the current defaults but document them clearly in MODULE.md notes.** Same posture as `LocationManager.DEFAULT_LOCATION = GeoPoint(41.2995, 69.2401)` (Tashkent) in foundation — product-specific defaults are sanctioned in foundation/MODULE.md notes.

### Cross-cutting patterns

- **The button family (`PrimaryButton`, `SecondaryButton`, `TextButton`) is a designed-in 90% duplication.** ~210 lines per file with identical Colors+Dimens+Defaults+composable shape; only default values differ. Section 3 flagged this as cross-component duplication and recommended *not* extracting. The MODULE.md `Notes` section should call out this rationale: the duplication is the API surface, not a refactor target. (~0 min — documentation only)

- **Per-property KDoc paraphrase pattern** affects ~30 files × ~5 paraphrase blocks each = ~150 paraphrase blocks across the module. Same shape as core/data/design/foundation but heavier in absolute terms. **Single sweep in wave 2 covers the bulk** — sed-able, but careful (some `@param` blocks include real info, like `Modifier.squareSize`'s `@param position` Float-meaning disambiguator — those need to stay, just the redundant ones go). Plan ~80 min for the sweep.

- **`@since 0.0.X` ceremony tags** appear on 101 lines across 30 files. Same posture as the prior modules — drop in the wave-2 KDoc sweep. Trivial sed/awk. (~5 min sed.)

- **The Defaults pattern is universal.** Every component has a `*Defaults` object with `colors()` / `dimens()` factories. **This is the load-bearing API surface.** When MODULE.md is rewritten, the pattern should be the centerpiece of the `Notes` section.

- **`@Immutable` posture is universal.** All 30 `*Colors`/`*Dimens` data classes carry `@Immutable`. No follow-up needed.

- **`System.color.*` consumption is universal.** `System.font.*` is used where text is involved (most fields, top bars, buttons that take a `textStyle:` parameter). **`System.space.*` and `System.radius.*` are NOT consumed** — section 4 flagged this as a real but deferred gap.

### Concerns with the criteria as applied to primitives

- **Criterion 6's state-machine bar** doesn't apply — primitives has no Orbit `ContainerHost`, no MVI. The "every intent → state transition tested" line is a no-op for primitives. Mention this in wave-9 verification but don't try to invent state machines.

- **Criterion 6's test bar is awkwardly fit for Compose UI primitives.** Most components are `@Composable` UI atoms whose load-bearing behavior is rendered output, focus management, and animation lifecycle — none of which is exercised by the current `kotlin.test` + `assertEquals` test posture. Two adaptations:
  1. **Accept the data-class equality floor** (current state) and document that primitives' real test surface is the `showcase` preview catalog (per CLAUDE.md "showcase = component preview catalog") + `runSipDemo` integration.
  2. **Add `runComposeUiTest` infrastructure** and write behavior tests for at least the animation/focus components.
  - **Recommend (1) for the alpha cleanup window; (2) as a follow-up scoped after the alpha tag.**

- **Criterion 4's "no SDK-internal deps"** does NOT hold for primitives — it depends on `:core`, `:design`, `:resources`, `:platform` (4 internal deps). This is expected (UI atoms compose downward). Confirmed all 4 are real and minimal; no cycles.

- **Criterion 11's "rewrite eligible" bar** finds **two real god files in primitives** (`LocationPin.kt` at 564 lines, `NumberField.kt` at 321 lines). Both are split candidates for wave-3. Neither is a behavior rewrite — they're file-organization improvements.

- **Criterion 3 (alpha versioning, no API hardening)** — applied generously. The `transformation/Phone*` and `Number*` flagged-dead candidates would be public-API-changes in stable; alpha gives us room to defer or drop. The `materialIconsExtended` swap (replacing `Icons.AutoMirrored.Filled.ArrowBack` with `YallaIcons.ArrowBack`) is a public-API change in stable; alpha gives us room. The neutralization of `SensitiveButton` / `SplashOverlay` defaults is a public-API change in stable. **Wave-3 commits get `refactor!:` prefix** for any of these that land. Document the pattern in the wave summary.

- **The `COMPONENT_STANDARD.md` migration is the biggest documentation move.** The whole pattern explanation that the deleted file used to hold needs to be folded into `primitives/MODULE.md`'s `Notes` section. Without that fold, the pattern is undocumented anywhere — the existing `Colors + Dimens + Defaults` blurb in MODULE.md lines 11-18 is the *only* place that explains the convention. Wave-10 must absorb this.

---

## Summary stats

- **Section 1 findings:** 30 file-level findings across 30 source files. Mix of ~150-200 lines of paraphrase KDoc, **101 `@since` tags**, ~200 lines of dead public-API code (transformation classes + navigation typealias), ~12 lines of unused `LoadingIndicatorDimens.strokeWidth(size)`, 1 unused parameter. Plus 1 stale doc reference (`COMPONENT_STANDARD.md`) and 2 phantom symbol references (`model/`, `ButtonSize`, `PinView`) in MODULE.md.
- **Section 2 findings:** 0 unused libs (all are imported); 2 `api`-promotion candidates (`compose.runtime`, `compose.foundation`); 1 candidate to drop (`compose.materialIconsExtended`) after icon swap.
- **Section 3 findings:** 2 god files (`LocationPin.kt` 564 lines, `NumberField.kt` 321 lines). 1 name-collision (`PhoneVisualTransformation` exists as both a public class in `transformation/` and a private object in `field/NumberField.kt`). 0 organization-only nesting (4 single-file packages match the `design` precedent).
- **Section 4 findings:** 12 quality candidates total. **2 large** (the `transformation/` deletions at ~200 lines, the `System.space`/`System.radius` sweep at ~150 substitutions — both deferred to gates). **10 small** (dead suppression, redundant `when`, hardcoded "Navigate back" string, name rename, etc. — all sub-100-line).
- **Section 5 findings:** 0 promotion, 0 demotion, 3 borderline default-copy notes (`SensitiveButton`, `SplashOverlay`, `NavigationButton.contentDescription`).
- **Section 6 findings:** **14 commonMain components without test files** (out of 30 commonMain `.kt` files; some are private/internal and don't need direct tests). Estimated effort to fill the data-class equality bar: **~7-8h**. Estimated effort to add `runComposeUiTest`-based behavior coverage: **~14-16h** (deferred to post-alpha).
- **Section 7 findings:** 1 full MODULE.md rewrite + 11 stale package blurbs to drop + 2 phantom symbol references to drop + 1 broken doc link (`COMPONENT_STANDARD.md`). Wave-10 must fold the pattern explanation into MODULE.md notes. Estimated effort: **~30 min.**

- **Longest single rewrite candidate:** `LocationPin.kt` at **564 lines**. Split into 4 files (1 public + 3 private) is a move-only reorganization, but the absolute file size is the single biggest discrete number in the audit.

- **Largest deletion candidate:** the `System.space.*` / `System.radius.*` sweep at **~150 substitutions** if approved (deferred). The `transformation/Phone*` + `Number*` deletion at **~200 lines** is the second-largest, also deferred pending the YallaClient `CardNumberVisualTransformation` migration decision.

- **Blocking issues:** none. Audit is fully derivable from the source.

- **Questions about the COMPONENT_STANDARD.md migration that need Islom's input:**
  1. **The deleted `COMPONENT_STANDARD.md` content** — should the wave-10 MODULE.md rewrite preserve the full Colors+Dimens+Defaults pattern explanation (parameter ordering, factory-function shape, theme-binding rules) inside `## Notes`, or should it be a single one-paragraph reference and rely on per-component KDoc + the showcase catalog to convey the rest? **Recommend: full inline preservation in `## Notes`** since no other doc carries this content post-phase-1.
  2. **`System.space.*` / `System.radius.*` adoption** — should primitives sweep every literal `dp`/`RoundedCornerShape(N.dp)` to read from the theme, accepting that the brand-spacing values may diverge from current literals? **Recommend: defer; document the gap** in MODULE.md notes.
  3. **`transformation/PhoneVisualTransformation.kt` + `NumberVisualTransformation.kt` survival** — keep with documented YallaClient `CardNumberVisualTransformation` migration target, or drop and let YallaClient keep its private object? **Recommend: keep with migration plan.**
  4. **`SensitiveButton` / `SplashOverlay` default copy** — neutralize (force callers to provide localized strings) or keep order-shaped defaults? **Recommend: keep, document.**
  5. **`runComposeUiTest` infrastructure** — add to wave-8 scope, or stay at the data-class equality bar and rely on `showcase` for behavior verification? **Recommend: stay at data-class bar for the alpha cleanup window; add `runComposeUiTest` post-alpha.**
  6. **`@Preview` annotations in commonMain** — accepted (they ship into the published artifact but are typically R8-shrunk on consumer side), or move to a separate `androidUnitTest` / dev-only source set? **Recommend: accept, document.**

---

## 9. Approval (Islom, 2026-04-30)

Decisions locked for waves 2-10. All gate items approved per my + subagent recommendations (with G19 leaning delete over subagent's keep + plan).

### Gate items

- **G19 — `transformation/PhoneVisualTransformation.kt` + `NumberVisualTransformation.kt`:** **DELETE.** Zero callers SDK-wide; same shape as core G1 / design G9 / foundation G14. Name collision with the private `PhoneVisualTransformation` inside `NumberField.kt` makes "keep" actively confusing. Cheap to recreate when an actual producer ships. Frees up the canonical name for the private object. ~200 lines removed. `refactor!:`.
- **G20 — `System.space.*` / `System.radius.*` adoption sweep:** **DEFER.** Document the gap in MODULE.md notes — primitives currently uses literal `dp` / `RoundedCornerShape(N.dp)` instead of theme tokens. Sweep risks visual regression if `standardSpaceScheme()` / `standardRadiusScheme()` values diverge from current literals. Future project.
- **G21 — `LocationPin.kt` (564 lines) split:** **APPROVED.** Move-only into 4 files: `LocationPin.kt` (public composable + Defaults) + 3 private files (`LocationPinContent.kt`, `LocationPinStick.kt`, `LocationPinHeader.kt`). ~140 lines moved. No behavior change.
- **G22 — `compose.materialIconsExtended` swap + drop:** **APPROVED.** Replace `Icons.AutoMirrored.Filled.ArrowBack` with `YallaIcons.ArrowBack` from `:resources` (verify SVG exists; add if missing). Drop the dep.
- **G23 — Default copy neutralization:** **APPROVED.** Three changes:
  - `SensitiveButton.confirmText` default → neutral key (e.g., `Res.string.common_action_yes`).
  - `SensitiveButton.countdownText` default → neutral countdown format key.
  - `SplashOverlay.message` default → neutral overlay-message key (or `null`-default).
  - `NavigationButton.contentDescription = "Navigate back"` → drop the literal default; require callers to localize via `stringResource(...)` or pass `null`.

### Quick approvals

- **A1.** Sweep 101 `@since` tags — wave 2.
- **A2.** Paraphrase KDoc sweep + drop COMPONENT_STANDARD.md cross-references — wave 2.
- **A3.** Drop `@file:Suppress` block in `LocationPin.kt:1-8` — wave 2.
- **A4.** Drop unused `LoadingIndicatorDimens.strokeWidth(size)` helper — wave 2.
- **A5.** Drop unused `LocationPin.loading` param — wave 2.
- **A6.** Drop dead `navigation/ToolbarAction.kt` — wave 2.
- **A7.** Simplify `Modifier.squareSize` redundant `when` — wave 5.
- **A8.** Reuse the canonical `PhoneVisualTransformation` name for the private NumberField object after G19 deletes the public class.
- **A9.** Promote `compose.runtime`, `compose.foundation`, `compose.ui` to `api()` — wave 3.
- **A10.** `@Immutable` posture already universal — no work.
- **A11.** Full MODULE.md rewrite with COMPONENT_STANDARD pattern fold-in — wave 10.

### Out of scope (kept / deferred)

- runComposeUiTest behavioral test bar — defer post-alpha; current data-class equality bar acceptable.
- `@Preview` annotations in commonMain — accept; document.
- Behavioral coverage for the 14 untested components — defer post-alpha.
- `DateField.placeholder = "DD.MM.YYYY"` — ASCII format-string, acceptable.
- `NumberField`'s Uzbekistan phone formatting — country-coded by the `+998` resource, acceptable.
- `LoadingIndicator.iconSize` derivation refactor — defer.
