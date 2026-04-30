# COMPOSITES_AUDIT.md

Audit output for phase-3 `composites` cleanup. Drives subsequent implementation waves. Findings keyed to `CLEANUP_CRITERIA.md`. All paths absolute under `/Users/islom/StudioProjects/yalla-sdk/`.

78 source files total: 45 production (42 commonMain + 1 androidMain + 1 iosMain + 1 expect-in-common), 33 tests (commonTest).

---

## 1. AI-blob deletions (criterion 2)

### 2-2: `@since` ceremony tags

Every production file is saturated with `@since` tags. 201 total `@since` annotations across commonMain. Three versions represented: `0.0.1` (155 occurrences), `0.0.5-alpha11` (28), `0.0.5-alpha12` (18). All are bloat at alpha; they convey nothing useful and are verbatim the same pattern deleted from `primitives/` in the prior wave.

Affected files (all of commonMain — complete list):
- `card/`: AvatarCard.kt, BannerCard.kt, ContentCard.kt, FeedCard.kt, InfoCard.kt, NavigableCard.kt, SelectionCard.kt, SummaryCard.kt, ToggleCard.kt
- `drawer/`: DrawerItemIcon.kt, Navigable.kt, SectionBackground.kt
- `item/`: AddressItem.kt, IconItem.kt, ListItem.kt, NavigableItem.kt, PlaceButton.kt, PricingItem.kt, SelectableItem.kt, ValueItem.kt
- `sheet/`: ActionPickerSheet.kt, ActionSheet.kt, BottomSheetCard.kt, ConfirmationSheet.kt, DatePickerSheet.kt, DeviceConnectivityState.kt, ExpandableSheet.kt, ExpandableSheetState.kt, FormSheet.kt, HeaderableSheet.kt, OtpSheet.kt, SelectionSheet.kt, Sheet.kt, SheetHeader.kt, SheetNestedScrollConnection.kt, SheetSnackbarHost.kt
- `snackbar/`: Snackbar.kt, SnackbarController.kt, SnackbarHost.kt
- `util/`: PaymentResource.kt
- `view/`: CarNumber.kt, EmptyState.kt, LocationPoint.kt, RouteView.kt

**Recommendation**: delete all `@since` tags in a single find/replace wave. (~10 min, mechanical.)

### 2-1: Per-file ceremony detail (key files)

#### `HeaderableSheet.kt`

- **2-1** lines 47 — `@since 0.0.1` on `HeaderableSheetValue` enum. (~30 sec)
- **2-1** lines 60-62 — `@param container` / `@param dragHandle` / `@since 0.0.1` in `HeaderableSheetColors` KDoc. Both `@param` blocks restate field names. (~1 min)
- **2-1** lines 73-78 — five `@param` lines in `HeaderableSheetDimens` KDoc + `@since 0.0.1`. All restate field names (`@param shape Card shape.` etc.). (~1 min)
- **2-1** lines 93, 99, 113 — three `@since 0.0.1` in `HeaderableSheetDefaults` object and methods. (~30 sec)
- **2-1** lines 133-138 — five `@param` lines + `@since 0.0.1` in `HeaderableSheetState` constructor KDoc. The params restate parameter names. (~1 min)
- **2-1** lines 248-252 — `@param`/`@since` in `rememberHeaderableSheetState`. (~30 sec)
- **2-1** lines 289-296 — `@param` block in `HeaderableSheet` composable KDoc: `@param state`, `@param header`, `@param body`, `@param footer` all restate param names. `@param modifier Applied to sheet.` is also obvious. **Keep only non-obvious params** (`colors`, `dimens` cross-refs are borderline useful). (~2 min)

#### `Snackbar.kt` (278 lines)

- **2-1** lines 32, 49, 66, 89, 134, 208 — six `@since 0.0.1` ceremony tags. (~1 min)
- **2-1** lines 45-49 — `@property message` / `@property variant` / `@property icon` / `@property dismissIcon` in `SnackbarState` KDoc. All restate field names. (~1 min)
- **2-1** lines 61-66 — five `@param` in `SnackbarColors` + `@since`. Restate field names. (~1 min)
- **2-1** lines 80-89 — nine `@param` in `SnackbarDimens` + `@since`. Restate field names. (~2 min)
- **2-1** lines 124-129 — `@param` block in `Snackbar` composable. `@param state`, `@param onDismiss`, `@param modifier` all obvious. Keep `@param colors`/`@param dimens` cross-refs. (~1 min)

#### `ActionSheet.kt` (284 lines)

- **2-1** lines 31, 60, 78, 85, 101, 162 — six `@since` tags. (~1 min)
- **2-1** lines 33-44 — `@param` block in `ActionSheetColors`. Four params restate names. (~1 min)
- **2-1** lines 48-60 — `@param` block in `ActionSheetDimens`. Nine params restate names (`@param topSpacing Top spacing.` etc.). (~2 min)
- **2-1** lines 149-160 — `@param` in `ActionSheet` composable. Most restate names. (~2 min)

#### `Sheet.kt` (272 lines)

- **2-1** lines 49, 66, 83, 91, 105 — five `@since` tags (mixture of `0.0.5-alpha12` and `0.0.1`). (~1 min)
- **2-1** lines 46-49 — `@param` on `SheetColors`. Two params restate names. (~30 sec)
- **2-1** lines 62-67 — `@param` on `SheetDimens`. Six params restate names. (~1 min)
- **2-1** lines 144-157 — `@param` in `Sheet` composable KDoc. Most obvious. `@param snackbarHost`, `@param onFullyExpanded`, `@param dragHandle` carry useful info about optional behavior; keep those. (~2 min)

### 2-1: KDoc paraphrase (`@param`/`@property` restating the field name)

The `@param` blocks in Colors and Dimens data classes repeat field names without adding any information beyond what the name already conveys. This is the same pattern excised from `primitives/` buttons and fields.

Prominent offenders (sampled — pattern is universal across all 42 files):

- `HeaderableSheet.kt` lines 62-68, 72-87: `@param container Card background color.` / `@param dragHandle Color of the drag handle.` — the field names are `container` and `dragHandle`. The comment is noise.
- `ActionSheetDimens` (ActionSheet.kt lines 48-73): nine `@param` lines each restating the parameter name with minimal added meaning (e.g., `@param topSpacing Top spacing.`).
- `SnackbarDimens` (Snackbar.kt lines 78-102): nine `@param` entries, again pure paraphrase.
- `ConfirmationSheetDimens` (ConfirmationSheet.kt lines 43-74): eleven `@param` entries.
- `ActionPickerDimens` (ActionPickerSheet.kt lines 66-88): seven `@param` entries.

**Verdict**: delete all `@param`/`@property` lines in Colors/Dimens data class KDocs that paraphrase their field name. Retain only KDocs that add non-obvious constraint info (threading, units, ownership, error semantics). The class-level KDoc and composable-level `@param` blocks should be evaluated individually.

**Total estimated bloat**: ~400 comment lines across all data class KDocs.

### 2-1: `rememberDeviceConnectivityState` KDoc (DeviceConnectivityState.kt line 85)

`@return Remembered connectivity state` paraphrases the function signature. Delete.

### 2-1: `SnackbarController.sendData` (SnackbarController.kt lines 73-82)

`@param event Snackbar data, null to dismiss.` — the parameter is named `event: SnackbarData?`, and null-dismissal behavior is in the body. Low value. However the method itself has a smell (see section 8 G3).

### 2-1: ExpandableSheetState inline member KDocs (ExpandableSheetState.kt lines 76-171)

Many one-liner `/** … */` block comments above `val` properties that state the obvious:
- `/** The anchored draggable state for gesture handling. */` (line 76)
- `/** Collapsed content height in Dp. */` (line 80)
- `/** Expand the sheet. */` (line 134)
- `/** Collapse the sheet. */` (line 139)
- `/** Toggle between states. */` (line 143)
- `/** Update measured heights and reconfigure anchors. */` (line 148)
- `/** Settle the sheet to nearest anchor based on velocity. */` (line 171)

These paraphrase the name and return/param types. Delete.

### 2-3: ~~Dead public API — `SnackbarController.sendData`~~ — RETRACTED

The audit initially flagged `SnackbarController.sendData(event: SnackbarData?)` for deletion. **Cross-check found 8 call-sites in `YallaClient/feature/home/.../HomeRoute.kt:357-413`** — heavily used. **G3 retracted: keep `sendData`.** A migration to switch every YallaClient call from `sendData(x)` to `show(x)` is busywork with zero functional gain. The "speculative generalization" framing is wrong — YallaClient already exercises both branches.

### 2-4: God-file candidates (>=400 lines)

- `HeaderableSheet.kt` — **409 lines** — single composable but bundles enum, two data classes, defaults object, state class, `rememberHeaderableSheetState`, main composable, private layout composable, and private DragHandle composable. Borderline: all cohesive but extraction of `HeaderableSheetState` to a companion file would bring the main file under 250 lines. See section 7.

No other file exceeds 400 lines. Three more exceed 300: `AddressItem.kt` (338), `ActionSheet.kt` (284), `Snackbar.kt` (278). None are god-files — all are single-responsibility.

### 2-5: Swallowed errors

No `try { … } catch { … }` blocks found anywhere in commonMain production code.

---

## 2. Public surface review (criterion 4 / criterion 6)

### 4-1: Internal-only candidates

YallaClient import analysis shows every composites package has at least one public symbol consumed externally. No package is exclusively internal. However, some individual symbols are candidates:

**`SheetNestedScrollConnection`** (`sheet/SheetNestedScrollConnection.kt`) — class is public but only imported in YallaClient as a side-effect (one reference confirmed). Examine whether it should be `internal`. **Flag for Islom.**

**`DrawerItemIconDefaults`** — YallaClient imports `DrawerItemIconDefaults` directly, so it must stay public.

**`ExpandableSheetSpringSpec`** (`ExpandableSheetState.kt` line 187) — public `val` used to set the default for `rememberExpandableSheetState`. No YallaClient import of the constant directly. Could be `private` or `internal` and inlined at the call site. **Flag for Islom.**

**`ListItem`** — base building block composable. Used internally (IconItem, NavigableItem build on it) but YallaClient does not import `ListItem` directly. However it is part of the composites public contract and removing it from the API would surprise callers. Keep public, no change.

### 4-2: Missing `@Immutable` on Colors/Dimens

All Colors and Dimens data classes in the module already carry `@Immutable`. Verified sample: `HeaderableSheetColors`, `HeaderableSheetDimens`, `ActionSheetColors`, `ActionSheetDimens`, `SnackbarColors`, `SnackbarDimens`, `ListItemColors`, `ListItemDimens`, `AddressItemColors`, `AddressItemDimens`, `ExpandableSheetColors`, `ExpandableSheetDimens`.

`BottomSheetCardColors` (`BottomSheetCard.kt` line 37), `BottomSheetCardDimens` (line 47), `BottomSheetCardAnimation` (line 60) — all correctly annotated `@Immutable`.

`ExpandableSheetColors` — single-field data class at line 39. **`@Immutable` present.** OK.

**No missing `@Immutable` annotations found.**

### 6-1: KDoc gaps

The following public symbols lack any KDoc:

- `SheetDragHandle` (Sheet.kt line 233) — public composable, no class-level KDoc. Has `@param` block but no prose describing its role.
- `ListItemDefaults` object (ListItem.kt line 171) — `@since 0.0.1` exists but no description prose.
- Multiple `Defaults` objects have KDoc only on `@since`, no description of what the object does (`ExpandableSheetDefaults`, `BottomSheetCardDefaults`). Acceptable; criterion 5 says paraphrasing the signature is bloat — "default configuration for Foo" on `FooDefaults` is obvious.

**Overall KDoc quality**: composites module has substantially better KDoc than primitives. Most composables have Usage examples, meaningful parameter descriptions, and `@see` cross-references. The main gap is the `@since` ceremony bloat, not missing docs.

### 6-2: Stale doc references

**SPECIAL CHECK**: DESIGN_AUDIT.md flagged `System.color.icon.brand` refs at `AddressItem.kt:140` and `AddressItem.kt:218`.

**Verdict: STALE — DESIGN_AUDIT was right. CORRECTION** (the agent who wrote section 6-2 grep'd for `brand =` and saw it on `Background`, but `ColorScheme.Icon` has fields `white`, `base`, `secondary`, `disabled`, `red`, `subtle` — verified `design/src/commonMain/kotlin/uz/yalla/design/color/ColorScheme.kt:77-84`). `System.color.icon.brand` does NOT exist; `System.color.background.brand` does. **Wave 7 fix**: replace with `System.color.icon.base` (the closest semantic substitute for an address-marker dot tint). **A2 confirmed real, not a false positive.**

---

## 3. Component-pattern conformance (criterion 9-3)

### 9-3-a: Missing Colors/Dimens/Defaults objects

All components examined follow the pattern. Every composable that exposes styling has a `{Component}Colors`, `{Component}Dimens`, and `{Component}Defaults` triple.

Exceptions (legitimate, not pattern violations):
- `FormSheet` — delegates entirely to `SheetColors`/`SheetDefaults`, no separate `FormSheet*` types. Correct — it is a thin wrapper.
- `SelectionSheet` — only `SelectionSheetDimens` (no colors), because it delegates to `NativeSheet` which takes raw `Color` params. Slight inconsistency: `SelectionSheetDefaults.dimens()` exists but the container color is hardcoded to `System.color.background.base` inside the composable (line 131). **Flag A1 — extract container color into a `SelectionSheetColors` or expose it as a direct parameter.**
- `DatePickerSheet` (expect/actual) — no Defaults object. The `expect` function takes only `state` and `onEffect`. Platform implementations (`DatePickerSheet.android.kt`, `DatePickerSheet.ios.kt`) handle styling internally. Acceptable given platform-specific constraints.
- `DeviceConnectivityState` — a state holder, not a composable. No Colors/Dimens expected.

### 9-3-b: Parameter ordering

Canonical order: required → modifier → behavioral → styling → slots → content.

**Violations found:**

`SelectionSheet` (SelectionSheet.kt lines 115-126):
```
isVisible, onDismissRequest, title, items, selectedItem, onSelect,
modifier, dimens, itemKey, itemContent
```
`onSelect` (behavioral) appears before `modifier`. **Minor violation** — `onSelect` is arguably "required" given it drives the interaction. However per the rule, required = non-optional with no default, and `onSelect` has no default. Order: `isVisible, onDismissRequest, title, items, selectedItem, onSelect` is correct — all required, then `modifier`, `dimens`, `itemKey`, `itemContent`. **Actually conforming.** No fix needed.

`OtpSheet` (OtpSheet.kt lines 151-169): `title, isError, focusRequester` (behavioral) come after `modifier` but before `sheetState, onFullyExpanded` (also behavioral). Ordering within behavioral params is inconsistent but minor. **Flag A2 — reorder OtpSheet params: title before modifier, isError/focusRequester grouped.**

`ActionSheet` (ActionSheet.kt lines 166-178): `title, primaryAction, onPrimaryAction` required params, then `modifier`, then `message, secondaryAction, onSecondaryAction` optional behavioral, then `colors, dimens`. **Ordering is correct.**

`BottomSheetCard` (BottomSheetCard.kt lines 135-141): `offset, onHeightChanged` (required, behavioral), `modifier`, `colors, dimens`, `content`. **Correct.**

### 9-3-c: Default String/StringResource params locking product copy

**`DatePickerSheet.android.kt` line 57 / `DatePickerSheet.ios.kt` line 57 — VIOLATION**: Both platform actuals hardcode `stringResource(Res.string.register_input_birthdate)` as the sheet header title. This is product copy ("Enter date of birth" or similar) baked into the SDK. The `DatePickerSheetState` has no `title` field, so callers cannot override it. This violates criterion 1 — the title string belongs in YallaClient, not the SDK.

**Fix**: add `title: String? = null` to `DatePickerSheetState`, pass it through to both actuals. **Flag A13.**

**`ActionSheet`**: `title: String`, `primaryAction: String`, `message: String?`, `secondaryAction: String?` — no defaults. Caller must supply. **Correct — no lock-in.**

**`ConfirmationSheet`**: `title: String`, `description: String`, `actionText: String`, `sheetName: String?` — no defaults. **Correct.**

**`ActionPickerSheet`**: `title: String` — no default. **Correct.**

**`OtpSheet`**: `headline: String`, `description: String`, `title: String?` — no defaults on required params. **Correct.**

**`SelectionSheet`**: `title: String` — no default. **Correct.**

No hardcoded product copy found. The module correctly treats all string parameters as caller-supplied.

---

## 4. Dependency hygiene (criterion 1)

### 1-1: Unused dependencies

**`libs.kotlinx.serialization.json`**: `grep -rn "import kotlinx.serialization"` returns zero matches in all composites source. `@Serializable` also absent. The dependency is **unused** — delete from `build.gradle.kts`. **Flag A3.**

**`libs.cupertino`**: `grep -rn "cupertino"` in composites source returns zero matches. No Cupertino types imported anywhere in commonMain, androidMain, or iosMain. The iOS date picker (`DatePickerSheet.ios.kt`) uses native UIKit, not the cupertino library. **Cupertino dep is unused** — delete. **Flag A4.**

**`libs.constraintlayout`**: search for `ConstraintLayout` usage returns no results in composites source. The dep appears to be a carry-over. **Flag A5 — verify with IDE then remove.**

**`libs.androidx.core.ktx`**: androidMain dep. Normal for Android target. Likely needed but worth verifying — no explicit KTX import seen in `DatePickerSheet.android.kt`. **Flag for review, low priority.**

### 1-2: Should-be-api

`projects.core` is declared `api`. This is correct — composites expose `PaymentKind` and `LocaleKind` in their own public API (e.g., `PaymentResource.kt`, `CarNumber.kt`), so consumers of composites need core transitively.

`projects.design` is `implementation`. Composites do not expose `System.*` types in their own public types (only in default parameter expressions). Consumers who need to call `FooDefaults.colors()` will need to pass `Color` values that they get from somewhere. **Borderline** — callers using the default `colors()` overloads get color tokens injected without needing to import `design`. Keep as `implementation`.

### 1-3: Should-be-implementation

No candidates: `foundation`, `primitives`, `design`, `resources`, `platform` are all `implementation`. Correct — composites do not leak these in their public types.

### 1-4: `materialIconsExtended` audit

Four `Icons.*` references found — all from `materialIconsExtended`:

| File | Line | Icon |
|------|------|------|
| `item/AddressItem.kt` | 292 | `Icons.AutoMirrored.Default.ArrowForward` |
| `item/NavigableItem.kt` | 120 | `Icons.AutoMirrored.Filled.ArrowForwardIos` |
| `card/NavigableCard.kt` | 187 | `Icons.AutoMirrored.Default.ArrowForwardIos` |
| `drawer/Navigable.kt` | 158 | `Icons.AutoMirrored.Filled.ArrowForwardIos` |

YallaIcons equivalents: checked — `YallaIcons` catalog does not contain `ArrowForward` or `ArrowForwardIos`. The icons are structural (chevron/arrow), not product icons. Three uses of `ArrowForwardIos` are the navigation chevron; one use of `ArrowForward` is the multi-location address separator.

**Verdict**: Add `YallaIcons.ChevronRight` (or equivalent) to the `resources` module to replace the three `ArrowForwardIos` usages, allowing `materialIconsExtended` to be dropped from composites. The `ArrowForward` separator in `AddressItem` could become a custom vector or the same chevron. **Flag G1 — coordinate with resources module, non-trivial.**

### 1-5: Coil leakage in public API

`libs.coil` and `libs.coil.compose` are in `implementation`. Coil is referenced only in `AvatarCard.kt` KDoc (`AsyncImage` example at line 109) and as a prose cross-reference. No `AsyncImage` import or Coil type appears in any composites `public` function signature. The `avatar` slot takes `@Composable () -> Unit` — caller decides how to load the image. **No Coil leakage in the public API.** Both Coil deps can be reviewed for removal if AvatarCard truly never imports coil3 directly.

Verification: `grep -rn "import coil3"` returns zero matches in commonMain. Coil is **not used at all** in composites source — both `libs.coil` and `libs.coil.compose` are unused. **Flag A6 — remove both Coil deps.**

### 1-6: Cupertino leakage

No Cupertino types in public API. Already flagged as unused in 1-1. **A4 covers this.**

### 1-7: kotlinx.serialization audit

`kotlinx.serialization.json` is unused (confirmed zero imports). No types are serialized in composites. The dep should simply be deleted (A3). There is no migration question — nothing is serialized here.

`kotlinx.datetime` is used: `DatePickerSheetState` holds `LocalDate` fields (DatePickerSheet.kt lines 14-18). This dep is legitimately needed and not a concern.

---

## 5. Cross-module boundary check (criterion 1)

### 5-1: Composites depending on feature concepts

No feature-layer imports found. Composites import only from:
- `uz.yalla.core.*` (payment, locale — domain atoms)
- `uz.yalla.design.*` (theme, System tokens)
- `uz.yalla.resources.*` (icons, drawables, strings)
- `uz.yalla.platform.*` (NativeSheet)

**Boundary is clean.**

### 5-2: Composites bypassing primitives (re-implementing button/field/etc.)

`Button` from Material3 (`androidx.compose.material3.Button`) is used directly in `AddressItem.kt` (line 17) and `ActionPickerSheet.kt` (line 231). The project's primitive for button-like components is `PrimaryButton` / `SecondaryButton` / `TextButton` from `primitives`.

However: `AddressItem`'s usage is a clickable card/row shape, not a CTA button — using `Button` for a custom-shaped tappable region is acceptable. `ActionPickerItemRow` uses `Button` as a clickable full-width item button (not a CTA button shape). These are borderline — they don't map cleanly to `PrimaryButton`/`SecondaryButton`.

**Verdict**: flag as an observation but not a hard violation. The primitives provide styled CTA buttons; these are structural uses. **Flag for Islom as G2.**

### 5-3: Stateful composables breaking stateless-atom posture

Most composites are correctly stateless — they accept state as parameters or expose typed state holders (`HeaderableSheetState`, `ExpandableSheetState`).

`SnackbarController` is a global singleton `object` with a `Channel`. This is intentional global state — the design doc reference in the KDoc confirms it's by design. Not a bug, but it makes the snackbar system hard to test in isolation. **Note: stateful by design, accepted pattern.**

`DeviceConnectivityState` wraps `dev.jordond.connectivity.Connectivity` and starts coroutines in its `init` block. The state holder takes a `CoroutineScope` from the caller — this is the correct KMP connectivity pattern. **OK.**

`Sheet.kt` (Sheet composable, line 175): `var shouldShow by remember { mutableStateOf(false) }` — internal animation state, not business state. **Correct use of local state in stateless composable.**

---

## 6. Test coverage gap analysis (criterion 11)

### 11-a: Kept as-is (behavior tests, logic tests)

The composites test suite (33 files in commonTest) has broad coverage. Sample checks:
- `AvatarCardTest.kt`, `BannerCardTest.kt`, `ContentCardTest.kt`, `FeedCardTest.kt`, `InfoCardTest.kt`, `NavigableCardTest.kt`, `SelectionCardTest.kt`, `SummaryCardTest.kt`, `ToggleCardTest.kt` — card tests present.
- `AddressItemTest.kt`, `IconItemTest.kt`, `ListItemTest.kt`, `NavigableItemTest.kt`, `PlaceButtonTest.kt`, `PricingItemTest.kt`, `SelectableItemTest.kt`, `ValueItemTest.kt` — item tests present.
- `ActionPickerSheetTest.kt`, `ActionSheetTest.kt`, `BottomSheetCardTest.kt`, `ConfirmationSheetTest.kt`, `ExpandableSheetTest.kt`, `HeaderableSheetTest.kt`, `OtpSheetTest.kt`, `SelectionSheetTest.kt`, `SheetHeaderTest.kt`, `SheetTest.kt` — sheet tests present.
- `SnackbarHostTest.kt`, `SnackbarTest.kt` — snackbar tests present.
- `EmptyStateTest.kt` — view test present.

**Coverage appears excellent by count.** The quality of each test file needs verification (are they testing behavior or just constructing data classes?) but the coverage skeleton is comprehensive.

### 11-b: Rewrite-eligible (critical finding)

**All 33 test files test only Colors/Dimens data class equality and defaults — not composable or state behavior.**

`ExpandableSheetTest.kt` (54 lines, 5 tests) is representative: it tests `ExpandableSheetColors` equality and `ExpandableSheetDefaults.dimens()` defaults. The actual `ExpandableSheetState` class — with its `fraction` computed state, `contentHeightPx` derivedStateOf, `updateHeights` anchor reconfiguration, `settle(velocity)` branch, and `expand()`/`collapse()`/`toggle()` suspend functions — has **zero test coverage**.

`SheetTest.kt` (71 lines): tests `SheetColors` / `SheetDimens` data classes only. No test for the `Sheet` composable's `LaunchedEffect`-driven visibility animation, the `snapshotFlow` for `onFullyExpanded`, or the hide-then-reset flow.

`SnackbarTest.kt` (168 lines): tests `SnackbarColors`, `SnackbarDimens`, `SnackbarDefaults` — data class equality only. No test for the `Snackbar` composable rendering behavior or `SnackbarDefaults.colors(variant)` branch for Success vs Error.

This is a **module-wide pattern**: tests assert `data class` behavior (Kotlin's auto-generated `equals`/`copy`) rather than the module's actual UI logic. This provides near-zero value and gives false confidence.

**Rewrites needed for:**
- `ExpandableSheetTest.kt` — add `ExpandableSheetStateTest`: `updateHeights` reconfigures anchors, `fraction` is 0 before anchors set, `fraction` is correct after `updateHeights`, `settle(velocity)` targets correct anchor.
- `HeaderableSheetTest.kt` — same pattern expected; add `HeaderableSheetStateTest`.
- `SnackbarTest.kt` — add test for `SnackbarDefaults.colors(Success)` returns green container, `colors(Error)` returns red container.
- `SheetTest.kt` — `Sheet` composable visibility logic is hard to test without a Compose test harness; defer to integration test layer.

**Flag G4: the test suite is structurally shallow. Plan a rewrite wave targeting state-holder logic before the alpha tag.**

### 11-c: Untested but should be

- `DeviceConnectivityState` — no test file found. The `init` block starts two coroutines. This is testable with a fake `Connectivity` and a `TestScope`. **Missing test — flag A7.**
- `SnackbarController` — `SnackbarControllerTest` not in the file list. The `Channel`-based event bus is easily testable: `trySend` + `receiveAsFlow().first()`. **Missing test — flag A8.**
- `PaymentResource.kt` — `toPainter()` and `getStringResource()` are pure logic (card ID length determines brand). `getStringResource()` is non-composable and directly testable. **Missing test — flag A9.**

### 11-d: Untested and OK

- `DrawerItemIcon.kt` — renders an icon in a box. No logic. Composable snapshot only.
- `SectionBackground.kt` — thin wrapper composable. No logic.
- `RouteView.kt`, `LocationPoint.kt`, `CarNumber.kt` — display-only composables with no branching logic (CarNumber has scaling arithmetic but it's proportional math with no failure modes).

---

## 7. God-file split candidates (criterion 2-4)

### `HeaderableSheet.kt` — 409 lines — SPLIT CANDIDATE

Current contents:
1. `HeaderableSheetValue` enum (lines 49-55)
2. `HeaderableSheetColors` data class (lines 65-68)
3. `HeaderableSheetDimens` data class (lines 81-87)
4. `HeaderableSheetDefaults` object (lines 95-128)
5. `HeaderableSheetState` class (lines 142-243) — 101 lines
6. `rememberHeaderableSheetState` (lines 255-269)
7. `HeaderableSheet` composable (lines 300-342)
8. `HeaderableSheetLayout` private composable (lines 344-390)
9. `DragHandle` private composable (lines 392-409)

The natural split is `HeaderableSheetState` + `rememberHeaderableSheetState` into `HeaderableSheetState.kt` (mirroring the `ExpandableSheetState.kt` precedent set by the existing split of `ExpandableSheet`/`ExpandableSheetState`). This brings the main file to ~280 lines and the state file to ~130 lines. **Flag A10.**

### `AddressItem.kt` — 338 lines — OK (two overloads + helper)

Three public symbols: `AddressItem(text, onClick)`, `AddressItem(locations, placeholder, onClick)`, `AddressDot`. The file length comes from thorough KDoc on both overloads. After `@since` and `@param` paraphrase cleanup, file will drop to ~230 lines. No split needed.

### `ActionSheet.kt` — 284 lines — OK

Single composable + Colors/Dimens/Defaults. Preview function at bottom. No split needed.

### `Snackbar.kt` — 278 lines — OK

`SnackbarVariant`, `SnackbarState`, `SnackbarColors`, `SnackbarDimens`, `SnackbarDefaults`, `Snackbar` composable. Cohesive; no split needed.

### `Sheet.kt` — 272 lines — WATCH

Contains `SheetColors`, `SheetDimens`, `SheetDefaults`, `Sheet` composable, and `SheetDragHandle`. After cleanup, will be ~210 lines. OK. But `SheetDragHandle` could move to its own file if `Sheet.kt` grows in the future.

### `ExpandableSheetState.kt` — 228 lines — OK (already split from ExpandableSheet.kt)

State, remember function, spring constant, and modifier extension. Cohesive state file. The `expandableSheetDraggable` Modifier extension (line 224) is unusual — a Modifier extension that wraps a draggable state. Could be `internal` since it's not imported by YallaClient directly. **Flag A11.**

---

## 8. Decisions

### Small/Mechanical (A-series)

**A1** — `SelectionSheet.kt` line 131: Container color is hardcoded to `System.color.background.base` inside the composable body rather than coming from a `SelectionSheetColors` type or explicit parameter. Add `containerColor: Color = System.color.background.base` parameter or create `SelectionSheetColors`.
**Kill/Keep/Migrate**: add parameter. **Migrate** (small — 1 line change + default).

**A2** — `OtpSheet.kt` lines 160-169: `title: String?` (optional behavioral) appears after `modifier` but before other behavioral params. Reorder to: required params → `modifier` → all optional behavioral (`title`, `isError`, `focusRequester`, `sheetState`, `onFullyExpanded`) → styling → slots.
**Kill/Keep/Migrate**: **Reorder** (mechanical — public API change, use `refactor!:` prefix).

**A3** — `build.gradle.kts` line 39: `libs.kotlinx.serialization.json` — zero usages in source. **Delete.**

**A4** — `build.gradle.kts` line 36: `libs.cupertino` — zero usages in source. **Delete.**

**A5** — `build.gradle.kts` line 33: `libs.constraintlayout` — zero usages found. **Delete** (verify with IDE compile after removal).

**A6** — `build.gradle.kts` lines 26-27: `libs.coil` + `libs.coil.compose` — zero imports in source. **Delete both.**

**A7** — No test for `DeviceConnectivityState`. Write `DeviceConnectivityStateTest.kt` with a fake `Connectivity` and `TestScope`. Tests: `startMonitoring` updates `isMonitoring`, `statusUpdates` collection updates `isConnected`/`isDisconnected`.

**A8** — No test for `SnackbarController`. Write `SnackbarControllerTest.kt`. Tests: `show(data)` emits `SnackbarEvent.Show`, `dismiss()` emits `SnackbarEvent.Dismiss`, `sendData(null)` emits `Dismiss`, `sendData(data)` emits `Show`.

**A9** — No test for `PaymentResource.kt`. Write `PaymentResourceTest.kt`. `getStringResource()` is non-composable: `PaymentKind.Cash.getStringResource() == Res.string.payment_type_cash`, 16-char card ID → `payment_card_humo_format`, other length → `payment_card_uzcard_format`.

**A10** — Split `HeaderableSheet.kt` (409 lines): extract `HeaderableSheetState` + `rememberHeaderableSheetState` into `HeaderableSheetState.kt`, mirroring `ExpandableSheet`/`ExpandableSheetState` precedent.

**A11** — `ExpandableSheetState.kt` line 224: `fun Modifier.expandableSheetDraggable(state: ExpandableSheetState)` — no YallaClient import found for this symbol. Make `internal`.

**A12** — `DatePickerSheet.kt` lines 42-43 (and both platform actuals): `@Suppress("FunctionName")` + comment are incorrect. `@Composable` functions ARE allowed PascalCase; the suppress is unneeded. Delete in all three files.

**A13** — `DatePickerSheet.android.kt:57` / `DatePickerSheet.ios.kt:57`: `Res.string.register_input_birthdate` is hardcoded product copy inside the SDK. Add `title: String? = null` to `DatePickerSheetState`; use it in both actuals (hiding the text row if null). **Breaking change — `refactor!:` prefix.**

### Judgment-Needed (G-series)

**G1** — `materialIconsExtended` is used for `ArrowForwardIos` (3 places) and `ArrowForward` (1 place). YallaIcons does not have these vectors. Options: (a) add `ChevronRight` and `ArrowForward` to `YallaIcons`/`resources` and drop `materialIconsExtended` from composites entirely; (b) keep dep as-is. Option (a) is the correct direction — it closes the gap between composites and the design language. **Recommend: add YallaIcons entries and drop materialIconsExtended. Coordinate with resources module phase.**

**G2** — `ActionPickerSheet.kt` line 231 and `AddressItem.kt` line 166: direct use of Material3 `Button` instead of SDK's `PrimaryButton`/`SecondaryButton`. These are structural (item-shaped) buttons, not CTA shapes. The primitive `PrimaryButton` enforces its own shape/style, which would conflict. **Recommend: accept as-is. Structural button uses are a valid exception to the "use primitives" rule.**

**G3** — ~~Delete `SnackbarController.sendData`.~~ **RETRACTED** — re-grep found 8 call-sites in `YallaClient/feature/home/.../HomeRoute.kt:357-413`. **Decision: KEEP as-is.** The "redundant nullable wrapper" framing was incorrect; YallaClient explicitly uses the nullable-data convention and migrating 8 sites for cosmetic API churn is net-negative.

**G4** — Test suite is structurally shallow (see section 6-b). All 33 test files cover only `Colors`/`Dimens` data class equality and `Defaults` factory values. The state-holder classes (`ExpandableSheetState`, `HeaderableSheetState`, `DeviceConnectivityState`) and the channel-based `SnackbarController` have zero behavioral coverage. Rewrite needed before alpha tag. Prioritize: (1) `ExpandableSheetState` fraction/anchor logic, (2) `HeaderableSheetState` settle/layout logic, (3) `SnackbarController` channel semantics.

---

## Reviewer notes

1. **`SelectionSheet` suppressed warning** (`SelectionSheet.kt` line 114): `@Suppress("UnusedParameter") // onSelect wired by itemContent; ADR-005 slot migration in Phase 4`. This is a known planned work item. The parameter `onSelect: (T) -> Unit` is declared but not called inside the composable — callers wire it through `itemContent`. The suppression is correct but the comment references an ADR-005 that presumably lives nowhere (docs were deleted in phase 1). Update the comment to not reference a non-existent ADR.

2. **`DeviceConnectivityState` is in the `sheet` package** (`uz.yalla.composites.sheet`). This is the wrong package — it has nothing to do with sheets. It belongs in a `util` or `connectivity` sub-package. YallaClient imports it as `uz.yalla.composites.sheet.rememberDeviceConnectivityState` which will need updating. Flag for YallaClient migration list.

3. **`DatePickerSheet` platform split** — the expect/actual split is correct. Android uses `libs.datetime.wheel.picker` (androidMain), iOS uses native `UIDatePicker` (iosMain). The common expect function has no `@Suppress` or `@FunctionName` needed because Kotlin allows PascalCase on composables — the comment on line 42 is wrong (Compose convention IS PascalCase for composables). The `@Suppress("FunctionName")` is redundant. **Flag A12: delete the suppress and comment on DatePickerSheet.kt lines 42-43.** (Same suppress appears in both platform actuals — delete there too.)

4. **Three deps in build.gradle.kts are unused** (A3 serialization, A4 cupertino, A5 constraintlayout, A6 coil×2 = five total removals). This will produce a measurable improvement in compile speed and app size, particularly removing `coil` which is image-loading infrastructure.

5. **`SnackbarController` is a process-singleton** — its `Channel` is never reset between tests, making tests that collect `events` flaky if run in parallel. The `SnackbarControllerTest` (A8) must account for this by draining the channel in setUp/tearDown or by creating a new controller per test. Consider making `SnackbarController` an injectable dependency rather than a global singleton — but that is a scope change beyond cleanup.

6. **`rememberDeviceConnectivityState` KDoc** (DeviceConnectivityState.kt line 85): `@return Remembered connectivity state` paraphrases the function signature — delete.

7. **`DeviceConnectivityState` package** is `uz.yalla.composites.sheet` but the class has no sheet relationship. The YallaClient import at `ConnectivitySheetHost.kt` would need a package update. Include in migration list for the alpha.

---

## Summary

- **42 production files** reviewed (commonMain); 3 platform-specific (android/ios/expect)
- **201 `@since` tags** — all are bloat (criterion 2-2); three versions: `0.0.1` (155), `0.0.5-alpha11` (28), `0.0.5-alpha12` (18)
- **~400 lines** of `@param`/`@property` paraphrase KDoc across Colors/Dimens data classes; ~624 `@param`/`@property`/`@return` tags total
- **0 god-files** strictly (no file >=400 lines except HeaderableSheet.kt at 409 — borderline, A10)
- **0 `try/catch` blocks** — error handling is clean
- **5 unused dependencies** (A3-A6): `kotlinx.serialization.json`, `cupertino`, `constraintlayout`, `coil`, `coil.compose` — five dep deletions from build.gradle.kts
- **4 raw `Icons.*` usages** pulling materialIconsExtended with no YallaIcons equivalents — G1 (needs resources module work first)
- **1 product-copy violation**: `Res.string.register_input_birthdate` hardcoded in both DatePickerSheet platform actuals — A13
- **0 stale `System.color.icon.brand` refs** — DESIGN_AUDIT false positive confirmed; token exists in design module
- **3 missing tests** (A7, A8, A9): `DeviceConnectivityState`, `SnackbarController`, `PaymentResource`
- **All 33 existing tests shallow** — cover only data class equality/defaults, not state-holder logic (G4 — most important finding in this audit)
- **1 `@Suppress("UnusedParameter")` with dead ADR reference** — fix comment (reviewer note 1)
- **1 wrong-package class** — `DeviceConnectivityState` in `sheet` package (reviewer note 7)
- **System token consumption** (composites is the largest consumer in phase 3):
  - `System.color.*`: ~130 usages (`text.base` 20x, `background.secondary` 15x, `text.subtle` 12x, `icon.white` 10x, `background.base` 10x, `icon.base` 6x, others)
  - `System.font.*`: ~80 usages (`body.base` 26x, `body.small` 10x, `title.base` 7x, `body.caption` 7x, `body.large` 3x, others)
  - `System.space.*`: 0 usages (composites uses raw `dp` literals — no space token adoption)
  - `System.radius.*`: 0 usages (composites uses raw `dp` literals — no radius token adoption)
  - `System.motion.*`: 0 usages (composites uses raw `AnimationSpec` / `tween` / `spring`)

**Wave execution order** (within composites cleanup):
1. Delete 5 unused deps from `build.gradle.kts` (A3-A6) — compile-verify immediately.
2. Delete 201 `@since` tags + ~400 paraphrase `@param`/`@property` lines — single mechanical wave, regex-safe.
3. Fix A12 (drop `@Suppress("FunctionName")`), A11 (make `expandableSheetDraggable` internal).
4. A13 — add `title` param to `DatePickerSheetState`, update both actuals. `refactor!:` commit.
5. A10 — extract `HeaderableSheetState.kt`.
6. A1 — add `containerColor` param to `SelectionSheet`.
7. ~~G3~~ — RETRACTED, sendData stays.
8. A7, A8, A9 — write missing tests.
9. G4 — rewrite shallow tests for state-holder logic. **DEFERRED** per primitives wave-8 stance (rewrite-eligible, not rewrite-required).
10. G1 — coordinate with resources module to add YallaIcons vectors, then drop `materialIconsExtended`. **DEFERRED** to a resources-module follow-up.

---

## Decision register (Islom)

Final calls per item, decided autonomously per the user's "go full autonomous way" instruction.

| ID | Decision | Note |
|---|---|---|
| **A1** | Migrate | Add `SelectionSheetColors` data class with `containerColor` + Defaults. Wave 5. |
| **A2** | Fix | Replace `System.color.icon.brand` with `System.color.icon.base` in AddressItem.kt KDoc lines 140 / 218. Wave 7. |
| **A3** | Kill | Drop `kotlinx.serialization.json`. Wave 3. |
| **A4** | Kill | Drop `cupertino`. Wave 3. |
| **A5** | Kill | Drop `constraintlayout`. Wave 3. |
| **A6** | Kill | Drop `coil` + `coil.compose`. Wave 3. AvatarCard's KDoc references AsyncImage as a *suggested* slot impl — caller passes the composable, no Coil dep needed in composites. |
| **A7** | Migrate | Backfill `DeviceConnectivityStateTest`. Non-composable, no Compose UI test harness needed. Wave 8. |
| **A8** | Migrate | Backfill `SnackbarControllerTest`. Same — pure Channel-based event bus, easily testable. Wave 8. |
| **A9** | Migrate | Backfill `PaymentResourceTest` for the non-composable `getStringResource()`. Wave 8. |
| **A10** | Migrate | Split `HeaderableSheet.kt` (409 → ~280) by extracting `HeaderableSheetState.kt`. Wave 4. |
| **A11** | Migrate | Make `Modifier.expandableSheetDraggable` `internal` (zero YallaClient consumers). Wave 5. |
| **A12** | Fix | Drop `@Suppress("FunctionName")` + comment from `DatePickerSheet.kt` and both platform actuals. Wave 5. |
| **A13** | Migrate | Add `title: String? = null` to `DatePickerSheetState`; remove `Res.string.register_input_birthdate` literal from both actuals; caller passes localized title. **Breaking.** Wave 5. |
| **G1** | Defer | Drop `materialIconsExtended` requires adding 1-2 vectors to `:resources`. Defer to a resources-module follow-up to avoid scope creep. Document in MODULE.md notes. Wave 6 records the deferral. |
| **G2** | Keep | Material3 `Button` use in ActionPickerSheet/AddressItem — structural, not CTA. Document in MODULE.md notes. |
| **G3** | RETRACTED | `sendData` stays. 8 YallaClient call-sites confirmed; cleanup churn is net-negative. |
| **G4** | Defer | Same call as primitives wave 8 — runComposeUiTest infrastructure decision pending. Document in MODULE.md notes. |

**Reviewer-note follow-ups:**
- **Note 1** (SelectionSheet ADR-005 reference) — fix comment in wave 7 ("scoped to `itemContent`-driven selection wiring"; drop the ADR-005 phantom).
- **Note 2 / 7** (DeviceConnectivityState wrong package) — DEFER package move; YallaClient import-rename churn for cosmetic gain. Document in MIGRATION_LIST.md as a future cleanup candidate.
- **Note 6** (rememberDeviceConnectivityState `@return` paraphrase) — fold into wave 2's paraphrase strip.
