# yalla-sdk v1 Phase 4 — UI (primitives + composites) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Land Phase 4 of the v1.0 launch — `primitives` + `composites` UI modules. Migrate 8 composites from string parameters to `@Composable` slot lambdas (ADR-005), delete 2 truly unused composites, add tests for 7 untested surfaces, and scaffold Roborazzi for Android visual regression. Version bump `0.0.10-alpha01` → `0.0.11-alpha01`.

**Architecture:** Single coherent ADR-005 breaking cluster. All 8 string→slot migrations land together (one version bump). Roborazzi scaffold lands as infra-only; real golden capture deferred to a follow-up (Phase 4.5 or Phase 6 walk). Accessibility pass scoped to TopBar / LargeTopBar text labels only — full pass deferred.

**Tech Stack:** KMP (Android + iOS), Kotlin 2.2+, Compose Multiplatform, Roborazzi (new), swift-snapshot-testing (already scaffolded in Phase 3).

**Worktree:** `/Users/islom/StudioProjects/yalla-sdk/.worktrees/v1-phase4-ui` on branch `feature/v1-phase4-ui` (already created from `fc22cf8`).

---

## Scope Decisions (from Phase 3 discovery)

- **ContentCard stays.** Discovery confirmed it's the base layer for `ToggleCard` + `NavigableCard`. Spec claim of "unused" was wrong.
- **AddressCard deletes.** No call sites within the SDK or YallaClient. Test file + KDoc-only reference in `SummaryCard` comment.
- **RouteView stays.** Discovery-agent misread (tool-cache artifact showed only line 1); file is 112 lines with a real `@Composable fun RouteView(...)` used by YallaClient's `HistoryLocationsView.kt`. Drop from deletion list.
- **CarNumberState is clean.** Audit already confirmed; zero Phase 4 work.
- **Visual-regression goldens defer.** 31 primitives × 4 states + every composite × 2 states = hundreds of goldens. Capture is its own phase; Phase 4 scaffolds the framework only.
- **Accessibility pass defers.** Phase 4 does `contentDescription` on TopBar / LargeTopBar only. Full Role + tap-target + icon-description sweep is a post-1.0 quality pass.

---

## Task List

1. ADR-005 migration: `ListItem`, `IconItem`, `NavigableItem`, `SelectableItem`, `PricingItem`, `AddressItem` (both overloads), `Navigable`, `EmptyState` + `EmptyStateState` decomposition → single ADR-017
2. Delete `AddressCard` + `RouteView` (SDK-internal unused)
3. Tests: `Snackbar`, `SnackbarHost`, `Navigable`, `EmptyState`, `SectionBackground` in composites
4. Tests: `TopBar`, `LargeTopBar` in primitives (+ `contentDescription` wiring)
5. Roborazzi scaffold + one reference golden per target module to prove end-to-end (composites + primitives)
6. Version bump `0.0.11-alpha01` + apiDump + CHANGELOG
7. YallaClient lockstep — `chore/sdk-phase4-ui` branch
8. PR + review + merge + publish

---

## Task 1 — ADR-005 migration (single big cluster)

**Breaking.** 8 composites + one `EmptyStateState` data class change. YallaClient has 16+ call sites of `Navigable` alone.

**Files (SDK):**
- `composites/src/commonMain/kotlin/uz/yalla/composites/item/{ListItem,IconItem,NavigableItem,SelectableItem,PricingItem,AddressItem}.kt`
- `composites/src/commonMain/kotlin/uz/yalla/composites/drawer/Navigable.kt`
- `composites/src/commonMain/kotlin/uz/yalla/composites/view/EmptyState.kt`
- `docs/06-DECISIONS.md` — append ADR-017 (supersedes ADR-005's "plan to migrate" note)

### Migration pattern (applied uniformly)

Before:
```kotlin
@Composable
fun ListItem(
    title: String,
    subtitle: String? = null,
    /* ... */
) {
    Column {
        Text(title, style = System.font.body.base.medium, color = colors.title)
        subtitle?.let { Text(it, style = System.font.body.small.regular, color = colors.subtitle) }
    }
}
```

After:
```kotlin
@Composable
fun ListItem(
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
    /* ... */
) {
    Column {
        ProvideTextStyle(System.font.body.base.medium.copy(color = colors.title)) { title() }
        subtitle?.let {
            ProvideTextStyle(System.font.body.small.regular.copy(color = colors.subtitle)) { it() }
        }
    }
}
```

Key points:
- Use `ProvideTextStyle` so callers who pass a plain `Text("...")` slot inherit the default style — avoids boilerplate.
- Remove `titleStyle` / `descriptionStyle` params where they exist — style is now part of the slot's `ProvideTextStyle` default and callers override by applying their own `.copy(...)` inside the slot.
- Colors stay in `<Name>Colors` data class — don't eliminate.
- Disabled-state handling (`enabled` param) now conditions which color is provided into `ProvideTextStyle`.

### Per-target migrations

- [ ] **ListItem**: `title: String` + `subtitle: String?` → slots. Remove `titleStyle` / `descriptionStyle` if present.
- [ ] **IconItem**: `title: String` + `subtitle: String?` → slots.
- [ ] **NavigableItem**: `title: String` + `subtitle: String?` → slots.
- [ ] **SelectableItem**: `title: String` → slot.
- [ ] **PricingItem**: `name: String` + `price: String` → slots. `@Composable () -> Unit` each.
- [ ] **AddressItem (overload 1)**: `text: String` → slot.
- [ ] **AddressItem (overload 2)**: `placeholder: String` → slot. `locations: List<String>` stays — it's data, not a label. If it also needs slotting, that's a follow-up.
- [ ] **Navigable**: `title: String` + `description: String?` → slots. Remove `titleStyle` + `descriptionStyle` params (delegated to `ProvideTextStyle`).
- [ ] **EmptyState + EmptyStateState**: replace

```kotlin
data class EmptyStateState(
    val image: Painter,
    val title: String,
    val description: String? = null,
)

@Composable
fun EmptyState(
    state: EmptyStateState,
    /* ... */
)
```

with

```kotlin
@Composable
fun EmptyState(
    image: @Composable () -> Unit,
    title: @Composable () -> Unit,
    description: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
    colors: EmptyStateColors = EmptyStateDefaults.colors(),
    dimens: EmptyStateDimens = EmptyStateDefaults.dimens(),
)
```

Delete `EmptyStateState` data class entirely (not a State at the Phase-2-semantic level — it's just a param bundle).

### ADR-017

Append to `docs/06-DECISIONS.md`:

```markdown
## ADR-017: Composite string parameters migrated to @Composable slots

**Status:** Accepted. Supersedes the migration-plan note in ADR-005 (2026-04-22).

**Decision:** Every composite that previously accepted `title: String`, `subtitle: String?`, `description: String?`, `text: String`, `name: String`, `price: String`, or `placeholder: String` now accepts a `@Composable () -> Unit` slot. `EmptyStateState(image: Painter, title: String, description: String?)` is deleted; `EmptyState` takes three slots directly. Inside the composite, `ProvideTextStyle` applies the default style so callers writing `Text("...")` don't need to specify the style explicitly.

Full list of migrated surfaces:
- `ListItem` — title, subtitle
- `IconItem` — title, subtitle
- `NavigableItem` — title, subtitle
- `SelectableItem` — title
- `PricingItem` — name, price
- `AddressItem` (both overloads) — text, placeholder
- `Navigable` (drawer) — title, description; `titleStyle` + `descriptionStyle` params removed
- `EmptyState` — image, title, description; `EmptyStateState` deleted

**Why:** String params bundled content with style in a way that couldn't carry annotated strings, localized pluralization, or per-call composition. Slots let callers compose `Text("...") + Icon(...)` freely, apply per-call `Modifier`, animate text style transitions, or swap in a `TextFlowRow`. The framework convention (Material3's `Text`/`Button`/`Card` all use slots) makes Yalla's former String-based API feel foreign to any Compose developer approaching the SDK.

**Consequence:** Breaking at every call site. YallaClient's `chore/sdk-phase4-ui` branch rewrites 16+ `Navigable` sites plus every `ListItem` / `IconItem` / `NavigableItem` / etc. call. The mechanical translation is `title = "Some string"` → `title = { Text("Some string") }`; automatable via IDE replace once the count is bounded.

Decided: 2026-04-22. Part of Phase 4 of the v1.0 launch.
```

### Verify

- [ ] Each migrated composite's demo usage in its own KDoc code block updated to slot form.
- [ ] `./gradlew :composites:compileKotlinIosSimulatorArm64 :composites:compileAndroidMain` passes.
- [ ] `./gradlew :composites:apiDump` regenerates baseline. Diff matches ADR-017 surface changes exactly.
- [ ] `./gradlew :composites:allTests` passes. Existing tests for the 6 items need their own migrations — update each to pass a `@Composable () -> Unit` instead of a `String`.

### Commit

```bash
git add composites/src/commonMain composites/src/commonTest composites/api docs/06-DECISIONS.md
git commit -m "feat(composites)!: migrate 8 composites from String params to @Composable slots (ADR-017)"
```

---

## Task 2 — Delete AddressCard + RouteView

**Breaking if YallaClient uses.** Verify no call sites before deleting.

- [ ] `grep -rn 'AddressCard(' .` — inside yalla-sdk worktree AND YallaClient. If YallaClient has hits, this deletion becomes part of the Task 7 lockstep.
- [ ] Same for `RouteView(`.
- [ ] Delete:
  - `composites/src/commonMain/kotlin/uz/yalla/composites/card/AddressCard.kt`
  - `composites/src/commonMain/kotlin/uz/yalla/composites/view/RouteView.kt` (empty stub)
  - Matching test files if any.
  - Any `RouteViewDimens` / `RouteViewDefaults` orphans in the same folder.
- [ ] `./gradlew :composites:apiDump` — baseline loses these entries.
- [ ] `./gradlew :composites:allTests` — passes without the deleted test files.

### Commit

```bash
git add composites/
git commit -m "feat(composites)!: remove unused AddressCard and RouteView stub"
```

---

## Task 3 — Composite tests: Snackbar, SnackbarHost, Navigable, EmptyState, SectionBackground

**Files:**
- `composites/src/commonTest/kotlin/uz/yalla/composites/snackbar/SnackbarTest.kt`
- `composites/src/commonTest/kotlin/uz/yalla/composites/snackbar/SnackbarHostTest.kt`
- `composites/src/commonTest/kotlin/uz/yalla/composites/drawer/NavigableTest.kt`
- `composites/src/commonTest/kotlin/uz/yalla/composites/view/EmptyStateTest.kt`
- `composites/src/commonTest/kotlin/uz/yalla/composites/drawer/SectionBackgroundTest.kt`

Pattern per existing composites tests: structural equality + defaults + slot composition smoke. Dispatch via subagent.

### Commit

```bash
git add composites/src/commonTest/
git commit -m "test(composites): cover Snackbar, SnackbarHost, Navigable, EmptyState, SectionBackground"
```

---

## Task 4 — Primitives tests + TopBar / LargeTopBar contentDescription

**Files:**
- `primitives/src/commonTest/kotlin/uz/yalla/primitives/topbar/TopBarTest.kt`
- `primitives/src/commonTest/kotlin/uz/yalla/primitives/topbar/LargeTopBarTest.kt`
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/topbar/TopBar.kt` — add `contentDescription` for nav icon
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/topbar/LargeTopBar.kt` — same

Behavioral assertions: title rendered, nav action invoked on click, content description exposed via semantics.

### Commit

```bash
git add primitives/
git commit -m "test(primitives): TopBar + LargeTopBar coverage + contentDescription on nav icons"
```

---

## Task 5 — Roborazzi scaffold (Android-only, reference golden per module)

**Goal:** prove end-to-end pipeline. One golden captured per target module (primitives, composites) as a smoke. Real bulk capture is a follow-up.

- [ ] Add Roborazzi to `gradle/libs.versions.toml`:
  ```toml
  roborazzi = "1.50.0"
  roborazzi = { module = "io.github.takahirom.roborazzi:roborazzi", version.ref = "roborazzi" }
  roborazzi-compose = { module = "io.github.takahirom.roborazzi:roborazzi-compose", version.ref = "roborazzi" }
  ```
- [ ] Add Robolectric to `primitives` + `composites` androidUnitTest dependencies.
- [ ] Create reference tests:
  - `primitives/src/androidUnitTest/kotlin/uz/yalla/primitives/topbar/TopBarSnapshotTest.kt` — captures TopBar in light + dark.
  - `composites/src/androidUnitTest/kotlin/uz/yalla/composites/item/ListItemSnapshotTest.kt` — captures ListItem.
- [ ] `./gradlew :primitives:recordRoborazziDebug :composites:recordRoborazziDebug` — generates initial golden PNGs.
- [ ] `./gradlew :primitives:verifyRoborazziDebug :composites:verifyRoborazziDebug` — must pass against the just-recorded goldens.
- [ ] Commit the golden PNGs under `src/androidUnitTest/screenshots/` (committed like test fixtures).

### Commit

```bash
git add gradle/libs.versions.toml primitives/ composites/
git commit -m "feat(ci): Roborazzi scaffold + reference snapshots for TopBar and ListItem"
```

---

## Task 6 — Version bump + apiDump + CHANGELOG

- [ ] `gradle.properties` — `yalla.sdk.version=0.0.11-alpha01`.
- [ ] `./gradlew apiDump` — confirm baseline diffs match ADR-017 + deletions.
- [ ] `CHANGELOG.md` — add `0.0.11-alpha01` section referencing ADR-017 and deletions.

### Commit

```bash
git add gradle.properties CHANGELOG.md */api/
git commit -m "chore: bump yalla.sdk.version to 0.0.11-alpha01"
```

---

## Task 7 — YallaClient lockstep

Scope is large. Subagent job.

**Branch:** `chore/sdk-phase4-ui` off YallaClient `dev`.

- [ ] Bump `yalla-sdk = "0.0.11-alpha01"` in `gradle/libs.versions.toml`.
- [ ] Grep every ADR-017-migrated composite call site. For each `title = "..."` / `subtitle = "..."` / `description = "..."` / `text = "..."` / `name = "..."` / `price = "..."` / `placeholder = "..."`, rewrite as `title = { Text("...") }` etc.
- [ ] Navigable has 16+ sites (YallaClient drawer + settings screens). Verify count matches expected.
- [ ] If AddressCard / RouteView are used in YallaClient, replace with inline reimplementation or delete the UX path. Coordinate with Islom if a replacement is non-trivial.
- [ ] `EmptyStateState(image, title, description)` → separate `image`, `title`, `description` slots at each call site.
- [ ] Build YallaClient: `./gradlew :composeApp:compileKotlinIosArm64 :composeApp:assembleDebug`.

### Commit + PR

```bash
git commit -m "chore(sdk): adopt yalla-sdk 0.0.11-alpha01 (Phase 4 ADR-017 slot migration)"
git push -u origin chore/sdk-phase4-ui
gh pr create --base dev --head chore/sdk-phase4-ui --title "..."
```

---

## Task 8 — SDK PR + review + merge + publish

- [ ] Push `feature/v1-phase4-ui`.
- [ ] Open PR against `main` with ADR-017 summary + lockstep reference.
- [ ] Dispatch code reviewer subagent (same pattern as Phase 3).
- [ ] Address findings.
- [ ] Merge squash; publish.yml runs `apiCheck + allTests` then `publish`.
- [ ] Verify `0.0.11-alpha01` visible on GitHub Packages.
- [ ] Merge YallaClient PR once CI resolves `0.0.11-alpha01`.
- [ ] Update memory `project-v1-launch.md` with Phase 4 shipped state.
- [ ] Stop at phase boundary (or continue to Phase 5 if Islom signals).

---

## Completion criteria (all must be true)

- [ ] All 8 tasks complete.
- [ ] `0.0.11-alpha01` published to GitHub Packages.
- [ ] YallaClient Phase 4 PR merged to `dev`.
- [ ] Memory updated.
- [ ] Phase 4 follow-ups list the deferred visual-regression bulk capture + accessibility-pass sweep.
