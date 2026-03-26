# UI Component Gold Standard Refactoring

> Spec date: 2026-03-25
> Status: Approved
> Scope: yalla-sdk primitives, composites, platform modules
> Impact: All YallaClient feature modules (migration required)

## Problem

The yalla-sdk UI layer has 82 components across primitives (33), composites (49), and platform modules. While the architecture is sound (clean layering, no circular deps, good design system), the components suffer from:

1. **Content-state bundling** — Buttons bundle text with state: `PrimaryButtonState(text, enabled, loading)`. This violates Google's Compose API Guidelines and makes APIs inflexible.
2. **Zero test coverage** — 82 UI files, 0 tests. No regression detection.
3. **90% code duplication** — PrimaryButton has 5 private helpers; SecondaryButton reimplements them all.
4. **Over-engineered Dimens** — 12 parameters per button for 3 size variants. Audit shows nobody overrides size.
5. **String params instead of slots** — `TopBar(title: String?)` limits content to text only.
6. **13 unused components** — Dead code increasing maintenance surface.
7. **25 hidden components** — Reusable UI scattered across YallaClient feature modules.
8. **Inconsistent patterns** — PinView has dual state (controlled + uncontrolled), Cards duplicate clickable branching.

## Vision

> "Men shu darajada componentlarimizni kitob darajasida ko'rishni xoxlayman."
> — Islom

This SDK is not just internal tooling. It will be given to other developers on the team. It must be **reference-quality** — like composables.com but for Yalla infrastructure. Every component is a chapter in a book: documented, tested, composable, platform-native.

**Plug and play:** When a new feature is needed, developers assemble it from existing building blocks — never starting from zero. A new screen should be 80% SDK components, 20% feature-specific glue.

**Pride test:** Would we be proud to show this code to a senior engineer from Google, Apple, or JetBrains? If not, it's not done.

## Goals

1. **composables.com-level quality** — every component has KDoc with usage examples, MODULE.md with anatomy/variants/when-to-use, and @Preview variants
2. **Google Compose API Guidelines compliance** — parameter ordering, slot APIs, state hoisting, Defaults pattern
3. **Building block architecture** — primitives compose into composites, composites compose into features. Never skip layers, never rebuild from scratch
4. **Shared infrastructure** — eliminate code duplication (ButtonLayout for buttons, shared card base, etc.)
5. **Full test coverage** — behavioral tests for every component (min 5 interactive, 3 display, 4 container)
6. **Platform-native feel** — iOS feels like SwiftUI, Android feels like Material. Compose is invisible to the user
7. **External-consumer ready** — documentation and API quality sufficient for team members who didn't write the code
8. **Remove dead code** — unused components removed, hidden components extracted from features
9. **API stable** — beta/RC version bump, no more breaking changes per alpha bump
10. **Future-target friendly architecture** — code structured so adding a new target (e.g., web) requires platform-specific implementations, not architectural rewrites. No platform assumptions baked into commonMain component APIs

## Non-Goals

- Web target implementation now (architecture supports it, but no web code is written)
- Screenshot testing (infrastructure cost too high for current team size)

## Execution Strategy

Islom has Claude Max plan with effectively unlimited tokens. The refactoring will use **parallel agent teams** for maximum throughput:

- Phase 1-2: Islom + Claude together (template creation, API decisions)
- Phase 3+: Agent blitz — parallel agents each handling a component group, following COMPONENT_STANDARD.md
- Review: Islom reviews each batch before merge
- Target: Complete all phases as fast as possible, working day and night if needed

## Platform-Native Feel

**Core principle:** A senior iOS developer looking at the app should not be able to tell it's built with Compose. Every interaction, every transition, every feedback mechanism must feel native to the platform.

### What "Native Feel" Means Per Platform

| Aspect | iOS Expected Behavior | Android Expected Behavior |
|--------|----------------------|--------------------------|
| **Touch feedback** | Opacity dim on press (no ripple) | Material ripple effect |
| **Scroll** | Rubber-band overscroll, bounce | EdgeEffect glow or stretch |
| **Sheet dismiss** | Swipe down with rubber band, detents | Material BottomSheet dismiss |
| **Loading spinner** | UIActivityIndicatorView style | CircularProgressIndicator |
| **Switch/Toggle** | UISwitch (green track) | Material Switch |
| **Date picker** | Wheel picker (UIDatePicker) | Calendar picker |
| **Navigation** | Push/pop with edge swipe back | Shared element / fade |
| **Haptic feedback** | UIImpactFeedbackGenerator | VibrationEffect |
| **Min touch target** | 44pt | 48dp |
| **Keyboard** | iOS keyboard avoidance, Done/Next bar | IME action handling |

### Component Classification by Platform Strategy

| Strategy | Components | Rationale |
|----------|-----------|-----------|
| **expect/actual (native rendering)** | Sheet, DatePicker, Switch, LoadingIndicator, Navigation, Icon buttons (circle/squircle) | These have strong platform conventions — users immediately notice wrong behavior |
| **Compose with platform-aware defaults** | Buttons, Cards, Fields, Items, TopBars | Custom Yalla design — no native equivalent. But press feedback, min touch target, and text rendering must respect platform |
| **Platform-aware modifiers** | Scroll containers, clickable surfaces | Overscroll, press indication (opacity vs ripple), edge-to-edge |

### Implementation Approach

1. **Press indication:** Create `expect fun Modifier.platformClickable()` that applies opacity-based feedback on iOS and ripple on Android. All interactive components use this instead of raw `clickable`.

2. **Existing expect/actual components** (already done well):
   - `NativeSheet` — UISheetPresentationController on iOS, ModalBottomSheet on Android
   - `NativeLoadingIndicator` — UIActivityIndicatorView on iOS, CircularProgressIndicator on Android
   - `NativeWheelDatePicker` — UIDatePicker on iOS, custom wheel on Android
   - `NativeSwitch` — UISwitch on iOS, Material Switch on Android
   - `NativeCircleIconButton` / `NativeSquircleIconButton` — platform-native icon buttons

3. **New platform adaptations needed:**
   - Press feedback modifier (opacity vs ripple)
   - Overscroll behavior (bounce vs edge effect)
   - Keyboard handling (iOS keyboard avoidance bar)

4. **Per-component audit during gold standard refactoring:**
   Every component going through gold standard MUST answer: "Would a senior iOS developer notice this isn't SwiftUI?" If yes, fix it.

### Phase Integration

Platform-native feel is NOT a separate phase — it's a **cross-cutting concern** applied during every phase:
- Phase 1 (ButtonLayout): `platformClickable` modifier for press feedback
- Phase 2-3 (Buttons): Verify no Material ripple on iOS
- Phase 4 (Fields): Keyboard behavior, text selection handles
- Phase 5 (Cards): Press feedback on interactive cards
- Phase 6 (Sheets/TopBars): Already using NativeSheet, verify transitions

## Design

### Phase 1: Shared Button Infrastructure

**Problem:** PrimaryButton (469 LOC), SecondaryButton (338 LOC), TextButton (305 LOC) share 90% identical logic but each reimplements it.

**Solution:** Extract `ButtonLayout` — an internal composable that all button variants delegate to.

```kotlin
// internal — shared across all button variants
@Composable
internal fun ButtonLayout(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    loading: Boolean,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    contentPadding: PaddingValues,
    minHeight: Dp,
    iconSize: Dp,
    iconSpacing: Dp,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    content: @Composable RowScope.() -> Unit,
)
```

**Internal structure (Container -> Provider -> Layout -> Content):**
```
Surface (click, shape, color, semantics)
  -> CompositionLocalProvider (contentColor)
    -> Row (padding, arrangement, alignment)
      -> if loading: NativeLoadingIndicator
         else: leadingIcon? + content() + trailingIcon?
```

**Impact:** ~500 LOC removed across 3 button files. Single place to fix button behavior.

### Phase 2: PrimaryButton Gold Standard

**Before (current API):**
```kotlin
data class PrimaryButtonState(
    val text: String,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val size: ButtonSize = ButtonSize.Medium,
)

@Composable
fun PrimaryButton(
    state: PrimaryButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: PrimaryButtonDefaults.PrimaryButtonColors = PrimaryButtonDefaults.colors(),
    style: PrimaryButtonDefaults.PrimaryButtonStyle = PrimaryButtonDefaults.style(),
    dimens: PrimaryButtonDefaults.PrimaryButtonDimens = PrimaryButtonDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
)
```

**After (gold standard API):**
```kotlin
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: PrimaryButtonColors = PrimaryButtonDefaults.colors(),
    dimens: PrimaryButtonDimens = PrimaryButtonDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
)
```

**Key changes:**
1. Remove `PrimaryButtonState` — flatten to direct parameters
2. Replace `text: String` with trailing `content: @Composable RowScope.() -> Unit`
3. Remove `style` — merge text style into internal size resolution
4. Remove `size` from public API — audit shows 0 overrides, default Medium always used
5. Simplify `dimens` — no per-size variants (12 params -> 4 params)
6. Move Colors/Dimens to top-level (not nested in Defaults)
7. Add `@Immutable` to Colors and Dimens
8. Delegate to `ButtonLayout` internally

**Migration at call sites:**
```kotlin
// Before
PrimaryButton(
    state = PrimaryButtonState(text = "Submit", loading = isLoading),
    onClick = { submit() },
)

// After
PrimaryButton(onClick = { submit() }, loading = isLoading) {
    Text("Submit")
}
```

**Call site count:** 24 files need migration.

### Phase 3: All Button Variants

Apply same pattern to SecondaryButton (4 usages), TextButton (3 usages), BottomSheetButton (3 usages), GenderButton (2 usages), NavigationButton (1 usage), SensitiveButton (1 usage — CancelSheet), IconButton (already close to gold standard).

All delegate to `ButtonLayout`. Each variant only defines its own `colors()` defaults.

Note: GenderButton and BottomSheetButton have distinct APIs (GenderButton takes `GenderKind`, BottomSheetButton takes `painter`). These share `ButtonLayout` for the container/click/loading logic but keep their unique content slots. SensitiveButton is used for destructive actions (cancel order) — keep as named variant with danger-colored defaults.

### Phase 4: Fields Gold Standard

- PrimaryField (13 usages) — already uses `TextFieldState`, needs: slot for placeholder, `@Immutable` on config classes, tests
- NumberField (3 usages) — similar treatment
- DateField (4 usages) — similar treatment
- PinRow (2 usages) — needs: fully controlled API (value + onValueChange), no internal state
- PinView (0 usages) — **remove**

**Verified 0-usage SDK components (removal candidates in addition to above):**
ActionSheet, ListItem, RadioItem, ActionItem, StripedProgressbar, SplashOverlay, LoadingIndicator (SDK primitive — NativeLoadingIndicator is used instead) — all verified 0 imports in YallaClient.

**Note:** `CountdownActionButton` listed in original audit does not exist as a file — it was a naming confusion. `SensitiveButton` exists and has 1 usage (CancelSheet).

### Phase 5: Cards Gold Standard

- NavigableCard (6 usages) — needs: state unbundling, tests
- PaymentTypeCard (6 usages) — needs: state unbundling, tests
- Other active cards — apply standard
- **Keep:** ContentCard — 0 direct usages but serves as base building block for card composites (per COMPONENT_STANDARD.md Section 2.3)
- **Remove:** AddressCard, ProfileCard, PromotionCard, SelectableCard, SwitchCard (0 usages each, no planned use as building blocks)

### Phase 6: Sheets, Items, Indicators, TopBars

- Sheet (13 usages) — review isVisible + internal state pattern
- TopBar (5 usages) — `title: String?` -> `title: @Composable (() -> Unit)?`
- LargeTopBar (8 usages) — same slot treatment
- Items — state unbundling where applicable
- Indicators — mostly clean, add tests
- SheetIconButton (40 usages!) — move from platform/ to primitives/

### Phase 7: Feature Component Extraction

Extract hidden components from YallaClient features into SDK:
- ValidationSheet (CRITICAL — duplicated in auth + billing)
- DriverCard, DetailsCard, LocationsCard (history)
- SearchLocationField, FoundLocationView (location)
- SavedAddressCard (main)
- Others per AUDIT_RESULTS.md priority list

### Phase 8: Cleanup and Reference-Quality Documentation

- Remove 13+ confirmed unused components
- Every component gets composables.com-level MODULE.md entry:
  - **Description** — what it does, when to use it
  - **Anatomy** — building blocks diagram (which primitives compose it)
  - **Variants** — all named variants with descriptions
  - **When to use** — decision guide (e.g., PrimaryButton vs SecondaryButton vs TextButton)
  - **Platform behavior** — iOS vs Android differences
  - **Examples** — basic, with icons, loading, disabled, custom colors
  - **API Reference** — parameters table with types and defaults
- Root README for SDK — getting started guide for new team members
- Version bump: alpha -> beta
- **Book test:** Can a developer who has never seen this code read the MODULE.md and correctly use any component without asking questions? If not, docs are incomplete.

## Approach: AUDIT -> DESIGN -> BUILD (bottom-up) -> MIGRATE (top-down)

1. **AUDIT** — Done. See `AUDIT_RESULTS.md`.
2. **DESIGN** — This spec + `COMPONENT_STANDARD.md`.
3. **BUILD** — Bottom-up: ButtonLayout -> Primitives -> Composites -> Extracted components. TDD: test first, then implement.
4. **MIGRATE** — Top-down: Update all YallaClient call sites after SDK components are stable.

Why bottom-up BUILD then top-down MIGRATE? Components are designed for ALL use cases (from audit), built with stable APIs, then screens migrate to new APIs. No yo-yo effect.

## Testing Strategy

- Use `runComposeUiTest` (Compose Multiplatform testing API)
- Test in `commonTest` (runs on JVM, fast)
- Focus: behavior + semantics, not pixels
- Minimum per component: see COMPONENT_STANDARD.md section 6.4

## Risk Mitigation

1. **Breaking API changes** — Acceptable in alpha. All call sites are in YallaClient (single consumer). Migration is mechanical.
2. **Build breakage during migration** — Work in feature branch. Atomic commits: SDK change + client migration together.
3. **Scope creep** — Strict phasing. Complete each phase before starting next.
4. **Cross-phase API conflicts** — If a later phase reveals that an earlier phase's API needs changing, the earlier phase is reopened. Since there is only one consumer (YallaClient), API changes propagate mechanically. Each phase's ButtonLayout/component API is designed against ALL known use cases from the audit, minimizing this risk.
5. **`dimens()` Composable annotation removal** — Current `dimens()` factories are `@Composable` but read no composition state. Gold standard changes them to regular functions. This is source-compatible (callers in `@Composable` context won't break), but should be noted in migration commits.

## Success Criteria

- [ ] All active SDK components pass Gold Standard Checklist (COMPONENT_STANDARD.md section 8)
- [ ] Zero content-state bundling in any component
- [ ] 100% of active components have behavioral tests meeting COMPONENT_STANDARD Section 6.4 minimums (5+ interactive, 3+ display, 4+ container)
- [ ] ButtonLayout shared infrastructure eliminates duplication across button variants
- [ ] 13+ unused components removed (confirmed 0-usage via audit)
- [ ] ContentCard retained as internal card building block
- [ ] YallaClient builds successfully on both Android and iOS after all migrations
- [ ] Platform-native press feedback: opacity on iOS, ripple on Android (via `platformClickable`)
- [ ] No Material ripple visible on iOS in any interactive component
- [ ] All existing expect/actual components verified for native feel
- [ ] SDK version bumped to beta
