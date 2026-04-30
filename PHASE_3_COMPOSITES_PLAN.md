# Phase 3 — `composites` Cleanup Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply [CLEANUP_CRITERIA.md](CLEANUP_CRITERIA.md) criteria 1-11 to the `composites` module on the `cleanup/phase-2-3-4` branch. `composites` is the LEGO-assembly layer of the brick stack — pre-built combinations of primitives + design tokens shaped into cards, items, sheets, drawers, snackbars, and views. The Colors + Dimens + Defaults conventions established in `primitives/MODULE.md` are explicit checkpoints in wave 5.

**Architecture:** Same two-stage flow as the prior cleanup plans. Stage A is wave 1's audit, producing `COMPOSITES_AUDIT.md`. A decision gate sits between A and B. Stage B runs the per-module work shape from criterion 9: delete → deps → restructure → quality → promote/demote → KDoc → tests → verify → MODULE.md rewrite.

**Tech stack:** Kotlin Multiplatform, Compose Multiplatform (`runtime`, `foundation`, `material3`, `materialIconsExtended`, `components.resources`, `ui.tooling.preview`), Compottie (Lottie), ConstraintLayout, Coil (image loading), Cupertino (iOS-style controls), kotlinx.serialization, kotlinx.datetime, connectivity-device, datetime-wheel-picker (androidMain). Tests: JUnit 4 + `kotlin.test`.

**Inventory of `composites` as of plan-time (78 files):**

- commonMain (~50 production):
  - `card/` — `ContentCard`, `AvatarCard`, `BannerCard`, `FeedCard`, `InfoCard`, `NavigableCard`, `SelectionCard`, `SummaryCard`, `ToggleCard` (9 cards).
  - `drawer/` — `DrawerItemIcon`, `Navigable`, `SectionBackground` (3 drawer atoms).
  - `item/` — `AddressItem`, `IconItem`, `ListItem`, `NavigableItem`, `PlaceButton`, `PricingItem`, `SelectableItem`, `ValueItem` (8 list-item variants).
  - `sheet/` — `Sheet`, `BottomSheetCard`, `ExpandableSheet`, `ExpandableSheetState`, `HeaderableSheet`, `SheetHeader`, `SheetNestedScrollConnection`, `SheetSnackbarHost`, `ActionSheet`, `ActionPickerSheet`, `ConfirmationSheet`, `DatePickerSheet` + expect/actual (android/ios), `DeviceConnectivityState`, `FormSheet`, `OtpSheet`, `SelectionSheet` (~16 files — the heaviest package).
  - `snackbar/` — `Snackbar`, `SnackbarController`, `SnackbarHost` (3 files).
  - `view/` — `CarNumber`, `EmptyState`, `LocationPoint`, `RouteView` (4 display views).
  - `util/` — `PaymentResource` extensions.
- commonTest (~25 test files): cards, sheets, items, drawer, snackbar coverage. PHASE-2 verification recorded **208 tests** baseline.
- `MODULE.md` follows the old multi-`# Package` Dokka shape (same pattern primitives had pre-wave-10) — wave 10 will rewrite to phase-1 form.
- `build.gradle.kts`: ~21 deps (1 SDK api + 5 SDK implementation + 5 compose + 2 coil + 2 compottie + 1 constraintlayout + 1 cupertino + 1 kotlinx.serialization + 1 kotlinx.datetime + 1 connectivity + 2 androidMain).

**Carried forward from prior audits:**
- **DESIGN_AUDIT.md §8** flagged stale `System.color.icon.brand` refs in `composites/.../AddressItem.kt:140, 218` — that token does not exist on `ColorScheme`. Wave 2 must verify whether this still compiles or was already fixed during phase-2.
- **`materialIconsExtended` still on the dep list** — primitives dropped it in wave 3. Composites needs an equivalent audit: replace `Icons.*` calls with `YallaIcons.*` and remove the dep, or document why a specific icon isn't in `YallaIcons`.
- **First module to use Coil + Cupertino** — public-API leakage check: do any `Painter` / `Image` parameters expose Coil types? Do any sheets expose Cupertino types in public signatures?
- **`kotlinx.serialization` use** — first appearance in the UI layer. The audit must answer "what state is being serialized in a UI module, and should it live in `core` instead?"

---

## Pre-work — Branch and baseline

**Files:** none modified.

- [ ] **Step 1: Verify branch is `cleanup/phase-2-3-4`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && git rev-parse --abbrev-ref HEAD
  ```
  Expected: `cleanup/phase-2-3-4`.

- [ ] **Step 2: Baseline-build `composites`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :composites:compileKotlinIosSimulatorArm64 :composites:allTests --rerun-tasks
  ```
  Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Record the baseline.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    grep -hE "tests=" composites/build/test-results/iosSimulatorArm64Test/TEST-*.xml | \
    grep -oE 'tests="[0-9]+"' | grep -oE '[0-9]+' | \
    awk '{s+=$1} END {print "composites tests:", s}'
  ```
  Expected: 208 (per phase-3 primitives wave-9 verification). Note for wave 11.

---

## Wave 1 — Audit `composites` (Stage A)

**Files:**
- Create: `COMPOSITES_AUDIT.md` (repo root).

- [ ] **Step 1: Dispatch a fresh Opus subagent to audit `composites` against criteria 1, 2, 4, 6, 9-3, 11.**

  Use the `Agent` tool with `subagent_type: general-purpose`, `model: opus`. Read-only.

  Prompt to use verbatim:
  ```
  Audit /Users/islom/StudioProjects/yalla-sdk/composites against the criteria
  in /Users/islom/StudioProjects/yalla-sdk/CLEANUP_CRITERIA.md and write
  the output to /Users/islom/StudioProjects/yalla-sdk/COMPOSITES_AUDIT.md.

  Read CLEANUP_CRITERIA.md, then skim FOUNDATION_AUDIT.md, DESIGN_AUDIT.md,
  and PRIMITIVES_AUDIT.md to match their format precedent (8 sections +
  summary stats + reviewer notes).

  The audit MUST be structured into 8 sections.

  ## 1. AI-blob deletions (criterion 2)
  Per file in commonMain + commonTest, list buckets 2-1 through 2-5.
  - 2-1 paraphrase KDoc (says-the-obvious).
  - 2-2 ceremony tags (@since, @author).
  - 2-3 dead public API (no callers in this repo OR in YallaClient).
  - 2-4 god-files (>= 400 lines or mixed responsibilities).
  - 2-5 try/catch in business logic that swallows errors.

  ## 2. Public surface review (criterion 4 / criterion 6)
  Walk every public symbol. Flag:
  - 4-1 anything that looks internal-only (no consumer in YallaClient or in
    other SDK modules) and could be `internal`.
  - 4-2 missing or wrong `@Immutable`/`@Stable` annotations on data classes
    that ship as `*Colors` / `*Dimens` / state-holders.
  - 6-1 KDoc gaps on public symbols (composables, classes, top-level funcs,
    objects).
  - 6-2 stale doc references — links to deleted files, mentions of removed
    types/params, references to design tokens that no longer exist on
    `ColorScheme`/`FontScheme`/etc.

  ## 3. Component-pattern conformance (criterion 9-3)
  For every `@Composable` public function, verify it follows the
  Colors + Dimens + Defaults convention established by primitives:
  - 9-3-a missing `{Component}Colors` / `{Component}Dimens` / `{Component}Defaults`.
  - 9-3-b parameter ordering wrong (must be: required → modifier → behavioral
    → styling → slots → content).
  - 9-3-c default String/StringResource params that lock product copy
    (analog of primitives wave-5 G23). Composables that bake
    `stringResource(Res.string.xxx)` into a default should expose
    a required `String` instead.

  ## 4. Dependency hygiene (criterion 1 / criterion 11)
  - 1-1 unused deps in build.gradle.kts (run `./gradlew :composites:dependencies`
    + grep for actual import lines).
  - 1-2 deps that should be `api` but are `implementation` (e.g. types in
    public surface).
  - 1-3 deps that should be `implementation` but are `api` (no public
    surface needs them).
  - 1-4 `materialIconsExtended` audit: list every `Icons.*` reference, check
    whether `YallaIcons.*` has an equivalent. If yes → swap and drop the dep.
    If no → list each Icons.* usage so we can decide one-by-one.
  - 1-5 Coil public-API leakage: any `Painter` / `AsyncImagePainter` /
    `ImageRequest` exposed in public composable params?
  - 1-6 Cupertino public-API leakage: any iOS-style types in public sigs?
  - 1-7 kotlinx.serialization audit: what is being serialized? Should the
    serializable type live in `core` instead?

  ## 5. Cross-module boundary check (criterion 1)
  - 5-1 any composite that depends on a feature concept (Order, User, etc.)
    rather than primitive params (String, callbacks, sealed UI types).
  - 5-2 any composite that bypasses primitives and re-implements a
    primitive component (e.g. an inline button instead of PrimaryButton).
  - 5-3 any composable that hoists state internally and breaks the
    stateless-atom posture (state lives outside, hoisted by caller).

  ## 6. Test coverage gap analysis (criterion 11)
  For every public composite, classify into one of:
  - 11-a kept-as-is (existing tests cover behavior, code quality fine).
  - 11-b rewrite-eligible (poor test design — over-mocked, snapshot-only,
    no behavior assertions — but functionality covered).
  - 11-c untested but should be (no test, behavior matters — list the
    missing tests).
  - 11-d untested and OK (display-only views with no logic).

  ## 7. God-file split candidates (criterion 2-4)
  Scan for files >= 400 lines or files mixing 2+ responsibilities. List
  each with proposed split (file names + responsibilities). Sheet package
  is the most likely offender — flag the largest sheets first.

  ## 8. Decisions
  - List every "kill / keep / migrate / rewrite" call, with file + line refs
    and the reasoning. The user gates on this list. Use the same
    A1/A2/.../G1/G2/... numbering convention as PRIMITIVES_AUDIT.md
    (A = small / mechanical, G = larger / judgment-needed).

  ## Reviewer notes
  Anything that doesn't fit the buckets above. Stale doc refs,
  surprising findings, follow-ups for composites' callers in YallaClient.

  ## Summary
  - X file findings.
  - Y `@since` tags.
  - Z lines dead public API.
  - N god-files (after primitives wave 4 split).
  - System.color.* / System.font.* / System.space.* / System.radius.* /
    System.motion.* consumption count.

  Tools: Read, Grep, Glob, Bash. Do NOT modify any source. Output ONLY
  to COMPOSITES_AUDIT.md.

  Cross-reference YallaClient at /Users/islom/StudioProjects/YallaClient
  when checking call-sites for criterion 4-1. Treat code in YallaClient
  as the only consumer of composites's public API outside this monorepo.
  ```

- [ ] **Step 2: After the audit completes, read `COMPOSITES_AUDIT.md` end-to-end and confirm:**
  - 8 sections present.
  - All `A*` and `G*` items are decision-grade (kill / keep / migrate / rewrite).
  - At least one entry per criterion (1, 2, 4, 6, 9-3, 11).
  - No findings deferred to "later phase" without a recorded reason.

- [ ] **Step 3: Commit `COMPOSITES_AUDIT.md`.**

  ```bash
  git add COMPOSITES_AUDIT.md
  git commit -m "docs(composites): add COMPOSITES_AUDIT.md — phase-3 inventory"
  ```

---

## Decision gate — Islom reviews `COMPOSITES_AUDIT.md`

**Files:** none modified yet.

- [ ] **Step 1:** Surface every `A*` / `G*` decision back to the user. Recommend kill / keep / migrate per item; the user has final say.

- [ ] **Step 2:** Record decisions inline in `COMPOSITES_AUDIT.md` (commit on the same SHA as the user's response).

- [ ] **Step 3:** Translate every "delete" into a wave-2 step, every "migrate" into a wave-2/3 step, every "rewrite" into a wave-7 step.

---

## Wave 2 — Delete dead code + AI-blob (criterion 2)

**Files:** depends on audit. Likely targets:
- Delete: any `A*` "kill" item (unused composables, dead utility extensions, ceremony tags).
- Modify: every commonMain `*.kt` for paraphrase-KDoc strip + `@since` removal.

- [ ] **Step 1: Strip every `@since` tag in `composites/src/commonMain/`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    grep -rln '@since' composites/src/commonMain/ | \
    xargs sed -i '' -e '/^[[:space:]]*\*[[:space:]]*@since /d'
  ```

- [ ] **Step 2: Strip 2-1 paraphrase KDoc per audit list.**

  Per audit's per-file 2-1 list, edit each KDoc block: drop sentences that say only what the symbol's name says.

- [ ] **Step 3: Delete every audit-confirmed `A*`-kill file/symbol.**

  For each:
  - Delete the file or symbol.
  - Run `grep -rn '<deleted-symbol>' composites/src/ YallaClient/` to verify zero references.
  - If any reference remains, escalate to user — do not silently keep.

- [ ] **Step 4: Verify the module still compiles.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :composites:compileKotlinIosSimulatorArm64
  ```
  Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Commit per logical group (`docs(composites)` for KDoc strip, `refactor!(composites)` for deletions).**

---

## Wave 3 — Tighten `api` / `implementation` split (criterion 1)

**Files:**
- Modify: `composites/build.gradle.kts`.

- [ ] **Step 1: Apply audit §4 dep promotions/demotions.**

  Standard primitives-style split: `core` stays `api` (domain types in public sigs); `design` and `resources` may need to be promoted to `api` if `System.*` accessors or `StringResource` show up in public Defaults factories. `compose.runtime`, `compose.ui`, `compose.foundation`, `compose.material3` likely need promotion to `api`.

- [ ] **Step 2: Drop `materialIconsExtended` if audit §4-1-4 confirms all icons can be swapped to `YallaIcons.*`.**

  Per swap, run `grep -rn 'Icons.\(Filled\|AutoMirrored\)' composites/src/commonMain/` to confirm zero remaining references before dropping the dep.

- [ ] **Step 3: Verify the module still compiles.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :composites:compileKotlinIosSimulatorArm64
  ```
  Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit `refactor!(composites): tighten api/implementation split`.**

---

## Wave 4 — God-file splits (criterion 2-4)

**Files:** depends on audit §7. Likely sheet-package targets.

- [ ] **Step 1: For each god-file in audit §7, split per the proposed file structure.**

  Visibility rule: private functions cannot be cross-file even within the same package — promote shared helpers to `internal` when splitting.

- [ ] **Step 2: Verify the module still compiles.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :composites:compileKotlinIosSimulatorArm64 :composites:allTests
  ```
  Expected: BUILD SUCCESSFUL + all tests green.

- [ ] **Step 3: Commit `refactor(composites): split <file> into <N> files`.**

---

## Wave 5 — Default-copy neutralization + Component-pattern conformance (criteria 9-3 + G-class)

**Files:** depends on audit §3 and §8 G-class items.

- [ ] **Step 1: For every audit 9-3-c "default copy" finding, make the param required.**

  Pattern (matches primitives wave 5): drop `String? = null` defaults that resolve to `stringResource(Res.string.xxx)`; replace with required `String` param. Caller passes localized text.

- [ ] **Step 2: For every 9-3-a missing-Defaults finding, add a `{Component}Defaults` object with `colors()` / `dimens()` factories.**

- [ ] **Step 3: For every 9-3-b wrong-param-ordering finding, fix the parameter list.**

  Order: required → `modifier: Modifier = Modifier` → behavioral (`enabled`, `loading`) → styling (`colors`, `dimens`, `textStyle`) → slots → content.

- [ ] **Step 4: Verify tests still pass.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :composites:allTests
  ```
  Expected: tests green (some may need signature-update edits).

- [ ] **Step 5: Commit `refactor!(composites): neutralize default copy + conform component pattern`.**

---

## Wave 6 — Append composites entries to MIGRATION_LIST.md

**Files:**
- Modify: `MIGRATION_LIST.md`.

- [ ] **Step 1: For every breaking change introduced in waves 2-5, append a row to `MIGRATION_LIST.md` under a `### composites` heading.**

  Format matches the prior phase entries (one row per breaking change: before / after / call-site search command).

- [ ] **Step 2: Update phase status block at the top of `MIGRATION_LIST.md`** — mark `composites` done, `firebase` next.

- [ ] **Step 3: Commit `docs(composites): append composites-phase entries to MIGRATION_LIST.md`.**

---

## Wave 7 — Fill KDoc gaps surfaced by criterion 6

**Files:** depends on audit §2-6-1.

- [ ] **Step 1: For every public symbol flagged with missing KDoc in audit §2, add a KDoc block.**

  Style: info-dense (param semantics, sentinel values, what NOT to assume). Match the primitives wave-7 tone.

- [ ] **Step 2: For every stale doc reference in §2-6-2, fix or remove.**

- [ ] **Step 3: Commit `docs(composites): fill KDoc gaps surfaced by criterion-5 audit`.**

---

## Wave 8 — Test backfill (criterion 11)

**Files:** depends on audit §6.

- [ ] **Step 1: For each 11-c "untested but should be" finding, decide whether to write the test now or defer.**

  Default decision (matches primitives wave 8): defer if existing 208-test baseline is comprehensive; backfill ONLY if a behavior is silently regression-prone. Document decisions in the wave commit message.

- [ ] **Step 2: For each 11-b "rewrite-eligible" test, decide whether to rewrite or leave.**

  Default: leave for now; criterion 11 says "rewrite-eligible," not "rewrite required."

- [ ] **Step 3: Commit if any tests landed; otherwise note "test backfill deferred per audit §6" in the wave-9 commit message.**

---

## Wave 9 — Final compile + test verification

**Files:** none modified.

- [ ] **Step 1: Run the full SDK build matrix.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew \
      :core:allTests :data:allTests :design:allTests \
      :foundation:allTests :primitives:allTests \
      :composites:allTests :media:allTests :platform:allTests \
      compileKotlinJvm \
      :firebase:compileKotlinAndroid :firebase:compileKotlinIosSimulatorArm64 || true
  ```

  Note: `:firebase` and `:maps` iOS-link are pre-existing failures on `main`. Treat only `composites` as in-scope.

- [ ] **Step 2: Confirm composites passes all tests.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    grep -hE "tests=" composites/build/test-results/iosSimulatorArm64Test/TEST-*.xml | \
    grep -oE 'tests="[0-9]+"' | grep -oE '[0-9]+' | \
    awk '{s+=$1} END {print "composites tests:", s}'
  ```
  Expected: ≥ 208 (or whatever the wave-5/8 changes left it at).

- [ ] **Step 3: If anything regressed, fix it before continuing.**

---

## Wave 10 — `composites/MODULE.md` to phase-1 form

**Files:**
- Rewrite: `composites/MODULE.md`.

- [ ] **Step 1: Replace the existing `# Module composites` + multi-`# Package` Dokka block with the phase-1 form** (model on `primitives/MODULE.md`):
  - `# Module composites`
  - `> tagline`
  - `## What this is` — bulleted by package, listing actual file/symbol set after wave-2-4 deletions/splits.
  - `## What this is NOT` — vs primitives (composites are assemblies, not atoms), vs feature modules (no business logic), vs domain modules (no Order/User shape).
  - `## Usage` — gradle dep + a real screen-level example assembling 2-3 composites.
  - `## Notes` — composites-specific patterns (assembly grammar, Coil-internal-only stance, Cupertino-internal-only stance, kotlinx.serialization rationale).
  - `## Depends on` — full dep walk from `composites/build.gradle.kts` post-wave-3.

- [ ] **Step 2: Verify all symbol/path references in MODULE.md exist.** Run actual `grep` checks against the source tree before committing.

- [ ] **Step 3: Commit `docs(composites): rewrite MODULE.md to phase-1 form`.**

---

## Wave 11 — Summary + handoff

**Files:** none modified.

- [ ] **Step 1: Surface a wave-by-wave roll-up to the user** (matching the primitives summary format).

- [ ] **Step 2: Confirm phase-3 module status:**
  - ✅ `core`, `data`, `design`, `foundation`, `primitives`, `composites`.
  - ⬜ Phase 4 modules: `firebase`, `maps`, `media`, `platform`.

- [ ] **Step 3: Wait for the user's "continue" before kicking off phase-4.**
