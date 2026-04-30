# Phase 3 — `primitives` Cleanup Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply [CLEANUP_CRITERIA.md](CLEANUP_CRITERIA.md) criteria 1-11 to the `primitives` module on the `cleanup/phase-2-3-4` branch. `primitives` is the UI atom layer of the brick stack — buttons, fields, dialogs, indicators, pins, top bars, OTP inputs, ratings, navigation primitives, transformations, util. Compose-Desktop conventions from `CLAUDE.md` (Colors + Dimens + Defaults pattern, `staticCompositionLocalOf`, `Light`/`Dark` companions) are explicit checkpoints in wave 5.

**Architecture:** Same two-stage flow as the prior cleanup plans. Stage A is wave 1's audit, producing `PRIMITIVES_AUDIT.md`. A decision gate sits between A and B. Stage B runs the per-module work shape from criterion 9: delete → deps → restructure → quality → promote/demote → KDoc → tests → verify → MODULE.md rewrite.

**Tech stack:** Kotlin Multiplatform, Compose Multiplatform (`runtime`, `foundation`, `material3`, `materialIconsExtended`, `components.resources`, `ui.tooling.preview`), Compottie (Lottie animations for `SearchPin`), ConstraintLayout. Tests: JUnit 4 + `kotlin.test` (no Compose UI test harness yet).

**Inventory of `primitives` as of plan-time (40 files):**
- commonMain (30 production):
  - `button/` — primary, secondary, gradient, text, icon, navigation, gender, bottom-sheet, support, location-enable, countdown-sensitive button variants.
  - `dialog/` — modal dialogs including `LoadingDialog`.
  - `field/` — primary text field, date field, number field, search field.
  - `indicator/` — dots, lottie-loading, etc.
  - `navigation/` — navigation primitives.
  - `otp/` — OTP / pin-row inputs.
  - `pin/` — pin display.
  - `rating/` — rating-row.
  - `topbar/` — small + large top bar.
  - `transformation/` — text transformations (visual).
  - `util/` — primitives-shared utilities.
- commonTest (10 test files): `PrimaryButtonTest`, `DateFieldTest`, `NumberFieldTest`, `PrimaryFieldTest`, `SearchFieldTest`, `DotsIndicatorTest`, `PinRowTest`, `RatingRowTest`, `LargeTopBarTest`, `TopBarTest`.
- `MODULE.md` references `COMPONENT_STANDARD.md` at repo root, which **does not exist** (deleted with phase-1's `docs/` purge). Stale reference — wave-2-1 finding.
- `build.gradle.kts`: 14 deps (4 SDK projects + 6 compose + 2 compottie + 1 constraintlayout + 1 androidRuntimeClasspath compose.ui.tooling).

**Carried forward from prior audits:**
- **Stale `System.color.<token>` doc references** flagged in DESIGN_AUDIT.md §8 — `composites/...AddressItem.kt:140, 218` references `System.color.icon.brand` (which doesn't exist on `ColorScheme`). Out of scope for primitives, but the same kind of stale-doc check applies inside primitives' KDoc.

---

## Pre-work — Branch and baseline

**Files:** none modified.

- [ ] **Step 1: Verify branch is `cleanup/phase-2-3-4`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && git rev-parse --abbrev-ref HEAD
  ```
  Expected: `cleanup/phase-2-3-4`.

- [ ] **Step 2: Baseline-build `primitives`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :primitives:compileKotlinIosSimulatorArm64 :primitives:allTests --rerun-tasks
  ```
  Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Record the baseline.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    grep -hE "tests=" primitives/build/test-results/iosSimulatorArm64Test/TEST-*.xml | \
    grep -oE 'tests="[0-9]+"' | grep -oE '[0-9]+' | \
    awk '{s+=$1} END {print "primitives tests:", s}'
  ```
  Expected: 108 (per phase-3 design's wave-9 verification). Note for wave 11.

---

## Wave 1 — Audit `primitives` (Stage A)

**Files:**
- Create: `PRIMITIVES_AUDIT.md` (repo root).

- [ ] **Step 1: Dispatch a fresh Opus subagent to audit `primitives` against criteria 1, 2, 4, 6, 9-3, 11.**

  Use the `Agent` tool with `subagent_type: general-purpose`, `model: opus`. Read-only.

  Prompt to use verbatim:
  ```
  Audit /Users/islom/StudioProjects/yalla-sdk/primitives against the criteria
  in /Users/islom/StudioProjects/yalla-sdk/CLEANUP_CRITERIA.md and write
  the output to /Users/islom/StudioProjects/yalla-sdk/PRIMITIVES_AUDIT.md.

  Read CLEANUP_CRITERIA.md, CORE_AUDIT.md, DATA_AUDIT.md, DESIGN_AUDIT.md,
  and FOUNDATION_AUDIT.md first — every finding ties to a specific
  criterion, and the prior audits set the format precedent (8 sections +
  summary stats + reviewer notes). Match that structure closely.

  The audit MUST be structured into 8 sections matching the prior audits.

  ## 1. AI-blob deletions (criterion 2)
  Per file in commonMain + commonTest, list buckets 2-1 through 2-5.
  Pay special attention to:
  - @since tags (sweep-eligible like the prior modules)
  - Per-property KDoc paraphrase across the Colors / Dimens / Defaults
    data classes
  - Per-component aspirational paragraphs in MODULE.md class-level
    KDocs
  - The MODULE.md references a `COMPONENT_STANDARD.md` at repo root —
    verified DOES NOT EXIST (deleted with phase-1's docs/ purge).
    Stale doc reference, bucket 2-1.
  - Single-use abstractions
  - Dead code

  ## 2. Module dependency graph (criterion 4)
  - List every line in primitives/build.gradle.kts.
  - For each `implementation(projects.*)` declaration, verify usage:
    `grep -rn "import uz.yalla.<module>" primitives/src/commonMain/`.
    `projects.core`, `projects.design`, `projects.resources`,
    `projects.platform` — confirm each is actually imported.
  - For each `implementation(compose.*)` and `implementation(libs.*)`
    declaration, verify usage. Special attention:
    - `compose.materialIconsExtended` — is it actually used? It's
      a heavy artifact.
    - `compose.ui.tooling.preview` — used for @Preview annotations?
    - `compottie` + `compottie.resources` — used in SearchPin (per
      build.gradle.kts comment); verify zero other consumers.
    - `constraintlayout` — used where?
  - For each dep, decide whether it should be `api` (exposed in public
    type signatures) or `implementation`. Recommend the "Depends on"
    block.

  ## 3. Restructure candidates (criterion 9-3)
  - Run `wc -l` on every commonMain .kt file. List files >300 lines as
    god-file candidates.
  - 11 sub-packages — flag any organization-only nesting.
  - Specifically check whether `field/` files all share a common
    pattern that could be extracted as a base, or whether
    `button/` files duplicate the Colors/Dimens/Defaults boilerplate
    in a way that suggests an extraction candidate. Flag, don't
    auto-suggest — the gold-standard pattern documented in
    COMPONENT_STANDARD.md (deleted) was deliberate.

  ## 4. Quality / rewrite candidates (criterion 11)
  Compose-Desktop specific checks from CLAUDE.md:
  - **Colors + Dimens + Defaults pattern compliance.** Per
    primitives/MODULE.md's existing description (which itself
    references the now-deleted COMPONENT_STANDARD.md): each component
    should have `{Component}Colors` data class, `{Component}Dimens`
    data class, `{Component}Defaults` object with factory functions
    `colors()` / `dimens()` reading the current theme via
    `System.color.*`, `System.font.*`, `System.space.*`,
    `System.radius.*`. Verify every component follows this. Flag
    deviations.
  - **`@Stable` / `@Immutable` annotations.** Same posture as
    design G12 + foundation G17 — the `Colors`/`Dimens`/`Defaults`
    data classes should carry `@Immutable`.
  - **Static singletons over `Local*.current`.** Verify no component
    uses static singletons like `AppColors.Primary` instead of
    `LocalColorScheme.current` / `System.color.*` (CLAUDE.md
    anti-pattern).
  - Stale `System.color.<token>` references in component KDocs
    (DESIGN_AUDIT.md §8 noted `System.color.icon.brand`,
    `System.color.background.primary` — neither exists on
    ColorScheme). Grep for `System\.color\.[a-z]+\.(primary|brand)`
    in primitives KDocs; flag every match.
  - **Hardcoded product copy** — primitives is supposed to take any
    text via parameters. Grep for `[А-Яа-яЁё]\|[ʻ]` in
    primitives/src/commonMain. Any match is a product-copy
    contamination — flag for demotion to YallaClient.
  - Non-idiomatic Kotlin: callback chain, ArrayList literals, `Any?`
    where a sealed type fits, etc.

  Per-file deep-dive on the longer files (>200 lines). For each,
  read the file and call out:
  - Multiple distinct components in one file (split candidate).
  - Mixed concerns (data class + composable + factory all entangled).
  - Recomposition correctness — `@Composable` functions that
    allocate state on every call should use `remember` /
    `rememberSaveable`.

  ## 5. Promote/demote candidates (criterion 1)
  Apply the lego test to every public type. Primitives are UI atoms —
  composable building blocks. Check for:
  - Hardcoded Russian/Uzbek strings in commonMain (any match → flag
    for demotion).
  - Components with hard-coded business meaning (e.g., a `RideButton`
    that has Yalla-specific behavior baked in vs. a generic
    `PrimaryButton(label)` that takes any text).
  - Shapes that look feature-orchestration-shaped (none expected;
    primitives are stateless atoms).

  ## 6. Missing tests (criterion 6)
  Primitives has 10 commonTest files covering: PrimaryButton, DateField,
  NumberField, PrimaryField, SearchField, DotsIndicator, PinRow,
  RatingRow, LargeTopBar, TopBar.
  - For each public component in commonMain (30+ files), check if
    a test exists. List untested components.
  - Note: primitives' tests are mostly Compose UI tests using
    `runComposeUiTest`. Some components are inherently hard to test
    in commonTest without a real renderer (Lottie animations,
    constraint-layout dependent layouts) — flag those as accepted
    carve-outs separately.
  - Group findings by sub-package; estimate effort.

  ## 7. MODULE.md staleness (criterion 5)
  Compare current primitives/MODULE.md against phase-1 form. Reference
  docs:
  - bom/MODULE.md, core/MODULE.md, data/MODULE.md, design/MODULE.md,
    foundation/MODULE.md (all post-cleanup).
  Note: primitives/MODULE.md references a `COMPONENT_STANDARD.md`
  that's been deleted (phase 1's docs/ purge). The whole
  "Colors + Dimens + Defaults" pattern explanation should fold INTO
  MODULE.md notes since that doc no longer exists. List sections
  to add/remove/rewrite for the wave-10 rewrite.

  ## 8. Reviewer notes
  - Pushback on findings.
  - Cross-cutting patterns (every button shares a similar
    Colors/Dimens shape — confirm consistency).
  - Concerns with the criteria as applied to primitives (e.g.,
    most components are `@Composable` UI primitives that resist
    unit testing without a Compose runtime — a real test bar
    requires `runComposeUiTest` infrastructure).

  Output requirements:
  - Every finding has an absolute path under
    /Users/islom/StudioProjects/yalla-sdk/.
  - Line ranges where applicable.
  - No editorial commentary outside the structured findings.
  - Plain markdown, no frontmatter.
  - Estimated time investment per finding.

  Do NOT modify any source files. Output is ONE file:
  /Users/islom/StudioProjects/yalla-sdk/PRIMITIVES_AUDIT.md.

  When done, report: total finding count per section, the longest
  single rewrite candidate (lines), any blocking issues, and any
  questions about the COMPONENT_STANDARD.md migration that need
  Islom's input.
  ```

- [ ] **Step 2: Verify `PRIMITIVES_AUDIT.md` was created with 8 sections.**

  ```bash
  ls -la /Users/islom/StudioProjects/yalla-sdk/PRIMITIVES_AUDIT.md && \
    grep -c "^## [0-9]" /Users/islom/StudioProjects/yalla-sdk/PRIMITIVES_AUDIT.md
  ```
  Expected: file exists, `grep -c` returns `8`.

- [ ] **Step 3: Sanity-skim findings.** Look for: any rewrite >100-line gate items, the COMPONENT_STANDARD.md fold-in question, any hardcoded product copy (would surface as a demotion gate item), the stale `System.color.*` doc-ref findings.

- [ ] **Step 4: Commit the audit.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add PRIMITIVES_AUDIT.md && \
    git commit -m "$(cat <<'EOF'
  docs(primitives): add PRIMITIVES_AUDIT.md — phase-3 inventory

  Output of wave 1 of PHASE_3_PRIMITIVES_PLAN.md. Drives waves 2-10.
  Deleted at the end of phase 4 with CLEANUP_CRITERIA.md.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
  EOF
  )"
  ```

---

## Decision gate — Islom reviews `PRIMITIVES_AUDIT.md`

- [ ] **Step 1: Stop. Tell Islom: "Wave 1 done. `PRIMITIVES_AUDIT.md` committed as `<sha>`. Review it and tell me which findings to apply, which to skip, and any rewrite-gate decisions for >100-line items. Likely gate items: COMPONENT_STANDARD.md fold-in into MODULE.md notes, any hardcoded-product-copy demotions, Colors/Dimens/Defaults pattern non-compliance."**

- [ ] **Step 2: Wait for Islom's response.**

- [ ] **Step 3: Append `PRIMITIVES_AUDIT.md` with `## 9. Approval`.** Commit:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add PRIMITIVES_AUDIT.md && \
    git commit -m "docs(primitives): record wave-1 audit decisions"
  ```

---

## Wave 2 — Delete bloat (criterion 2)

**Files:** every file flagged in `PRIMITIVES_AUDIT.md` section 1.

- [ ] **Step 1: `@since` sweep** using the same Python script from prior waves, pointed at `primitives/src/commonMain` + `primitives/src/commonTest`. Compile + test:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :primitives:compileKotlinIosSimulatorArm64 :primitives:allTests
  ```
  Commit: `docs(primitives): drop @since tags across module`.

- [ ] **Step 2: Paraphrase KDoc sweep.** Dispatch a Sonnet subagent with the same prompt structure used in prior waves' paraphrase agents. Apply rules:
  - Delete property paraphrase across Colors/Dimens/Defaults data classes.
  - Drop the COMPONENT_STANDARD.md cross-references in class-level KDocs (the doc is gone; the pattern's explanation will live in MODULE.md notes after wave 10).
  - Keep info-dense usage blocks, behavioral notes, recomposition rules.
  Compile + test. Commit: `docs(primitives): strip paraphrase KDoc per criterion 2-1`.

- [ ] **Step 3: Delete bucket 2-3, 2-4, 2-5 findings** per audit. Likely candidates depend on the audit; commit per logical group with `refactor!:` prefix where public shape changes.

- [ ] **Step 4: Final wave-2 verification.**

---

## Wave 3 — Module dependency rules (criterion 4)

**Files:** `primitives/build.gradle.kts` + `primitives/MODULE.md` (Depends on placeholder).

- [ ] **Step 1: Apply audit §2 deltas.** For each `implementation()` flagged for promotion to `api()`, switch. Drop unused. Likely candidates per the audit prompt:
  - `compose.runtime` (api — `@Composable` annotations on every public function)
  - `compose.ui` (api — `Modifier` parameters everywhere)
  - `compose.foundation` (api — `BasicTextField`, layout primitives if exposed)
  - `compose.material3` (api/implementation — depends on whether exposed in signatures)
  - `compose.materialIconsExtended` (verify usage; drop if unused)
  - `compose.ui.tooling.preview` (typically androidMain only?)
  - `compottie` (verify single-consumer SearchPin)
  - `constraintlayout` (verify usage)
  - `projects.core`, `projects.design`, `projects.resources`, `projects.platform` — verify each via grep; promote to api if types exposed.

- [ ] **Step 2: Append "Depends on" block to primitives/MODULE.md** per audit §2.

- [ ] **Step 3: Compile downstream.** primitives feeds composites + (potentially) media:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :primitives:compileKotlinIosSimulatorArm64 \
              :composites:compileKotlinIosSimulatorArm64 \
              :media:compileKotlinIosSimulatorArm64
  ```
  All green.

- [ ] **Step 4: Run tests** for primitives + composites + media.

- [ ] **Step 5: Commit.** `refactor!:` if pom.xml changes.

---

## Wave 4 — Restructure (criterion 9-3)

**Files:** per `PRIMITIVES_AUDIT.md` §3 findings. Likely small or no-op.

- [ ] **Step 1: Apply approved restructures** with `git mv`.
- [ ] **Step 2: Compile entire SDK** if any imports cross module boundaries.
- [ ] **Step 3: Run tests** for primitives + downstream.
- [ ] **Step 4: Commit per logical restructure.** Skip wave entirely if audit found nothing.

---

## Wave 5 — Quality pass (criterion 11)

**Files:** every file flagged in `PRIMITIVES_AUDIT.md` §4.

Compose-Desktop-specific patterns this wave verifies:
1. **Colors + Dimens + Defaults pattern.** Every component should follow it; flag deviations.
2. **`@Immutable` annotations** on Colors / Dimens data classes.
3. **`Local*.current` access** vs static singletons.
4. **Stale `System.color.<token>` doc references** — fix or remove.
5. **Hardcoded product copy** — flag for demotion.

- [ ] **Step 1: Triage rewrite candidates.** Sub-100 → apply directly. >100 lines → gate decision.

- [ ] **Step 2: Apply approved rewrites.** Commit per rewrite. `refactor!:` if public shape changes.

- [ ] **Step 3: Run full SDK build** to catch downstream breakage.

---

## Wave 6 — Promote/demote flags (criterion 1)

**Files:** `MIGRATION_LIST.md` — append `## Phase 3 — primitives additions`.

- [ ] **Step 1: Open `PRIMITIVES_AUDIT.md` §5.** Apply each approved entry.

- [ ] **Step 2: Append breaking changes** since the last MIGRATION_LIST.md update.

- [ ] **Step 3: Commit.**

---

## Wave 7 — KDoc bar (criterion 5)

**Files:** every public symbol in `primitives/src/commonMain` whose KDoc is missing or paraphrased after wave 2.

- [ ] **Step 1: Dispatch Sonnet subagent** for KDoc gap audit. Same prompt structure as prior waves' KDoc gap audits.

- [ ] **Step 2: Apply suggestions.** Compile + test after every ~5 files.

- [ ] **Step 3: Commit.**

---

## Wave 8 — Test backfill (criterion 6)

**Files:** new test files per `PRIMITIVES_AUDIT.md` §6 gaps.

Realistic constraint: Compose UI tests in commonTest require `compose.uiTest` (which design has but primitives may not — verify in audit §2). Components that aren't testable without a real Compose renderer get the standard expect/actual carve-out.

- [ ] **Step 1: Add `compose.uiTest` to commonTest deps** if needed for the missing tests.

- [ ] **Step 2: For each gap, write the failing test first** (TDD).

- [ ] **Step 3: Run failing tests** to confirm they fail for the right reason.

- [ ] **Step 4: Run full primitives test suite.**

- [ ] **Step 5: Commit per logical group.**

---

## Wave 9 — Final compile + test verification

- [ ] **Step 1: Compile every module.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :core:compileKotlinIosSimulatorArm64 \
              :data:compileKotlinIosSimulatorArm64 \
              :resources:compileKotlinIosSimulatorArm64 \
              :design:compileKotlinIosSimulatorArm64 \
              :foundation:compileKotlinIosSimulatorArm64 \
              :primitives:compileKotlinIosSimulatorArm64 \
              :composites:compileKotlinIosSimulatorArm64 \
              :platform:compileKotlinIosSimulatorArm64 \
              :firebase:compileKotlinIosSimulatorArm64 \
              :maps:compileKotlinIosSimulatorArm64 \
              :media:compileKotlinIosSimulatorArm64 \
              :bom:assemble
  ```

- [ ] **Step 2: Run all testable modules' tests.** Skip `:firebase:link*` and `:maps:link*` (pre-existing iOS-linker failures).

- [ ] **Step 3: Tally per-module test counts.** Expected: zero failures everywhere; primitives count > baseline 108.

---

## Wave 10 — MODULE.md to phase-1 form

**Files:** `primitives/MODULE.md`.

Reference the post-cleanup MODULE.md files: `core/`, `data/`, `design/`, `foundation/`.

- [ ] **Step 1: Rewrite to phase-1 form** with the **Colors + Dimens + Defaults pattern explanation folded in** (since `COMPONENT_STANDARD.md` is gone). Notes section should cover:
  - The Colors + Dimens + Defaults convention (was in the deleted COMPONENT_STANDARD.md).
  - Stateless-atom posture (parameters in/state via remember outside).
  - Lottie / ConstraintLayout dependencies and where they're used.
  - Any hardcoded-product-copy decisions surfaced by the gate.
  - Whatever architectural carve-outs the audit documented.

- [ ] **Step 2: Delete the old `# Package …` blocks and the "Architecture" header.**

- [ ] **Step 3: Commit.**

---

## Wave 11 — Phase-3-primitives summary + readiness for `composites`

- [ ] **Step 1: Run full SDK build one final time.**

- [ ] **Step 2: Tally outputs.**

- [ ] **Step 3: Tell Islom: "Phase-3 `primitives` cleanup complete. Summary: <stats>. Ready to plan phase-3 `composites`?"**

---

## Self-review checklist

- [x] Spec coverage — every CLEANUP_CRITERIA criterion 1-11 has at least one wave applying it.
- [x] No placeholders — every `<…>` is a runtime-fill variable explicitly explained.
- [x] Type/method consistency — file paths absolute, package names match, `PRIMITIVES_AUDIT.md` referenced consistently.
- [x] Each wave has a verify step before commit.
- [x] Decision gate after wave 1; rewrite gate inside wave 5 (>100-line items).
- [x] Versioning policy honored — no alpha tag, commits stay on `cleanup/phase-2-3-4`.
- [x] Pre-existing iOS-linker failures excluded from wave-9.
- [x] Compose-Desktop pattern (Colors + Dimens + Defaults) is an explicit checkpoint in wave 5; the COMPONENT_STANDARD.md fold-in is wave 10's MODULE.md responsibility.

---

## Out of scope (do NOT do in this plan)

- `composites` cleanup (next plan).
- Phase 4 modules.
- Public API visibility tightening (criterion 3 — alpha-deferred).
- Reintroducing `docs/` or `docs/adr/`.
- Publishing an alpha. Criterion 8 — single bump at end of phase 4.
- iOS-link infra failures on `:firebase` / `:maps`. Pre-existing on main.
- Touching design's `MotionScheme` consumer-pending status (G9).
