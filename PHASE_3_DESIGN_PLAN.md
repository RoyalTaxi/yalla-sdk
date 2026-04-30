# Phase 3 — `design` Cleanup Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply [CLEANUP_CRITERIA.md](CLEANUP_CRITERIA.md) criteria 1-11 to the `design` module on the `cleanup/phase-2-3-4` branch. `design` is the visual-language layer of the brick stack — color/font/space/radius/motion tokens + the `YallaTheme` composable. After cleanup, downstream UI modules (`primitives`, `composites`, `foundation`) snap onto a tighter, idiomatic-Compose token surface.

**Architecture:** Same two-stage flow as the phase-2 plans. **Stage A** is wave 1's audit, producing `DESIGN_AUDIT.md`. A decision gate sits between A and B. **Stage B** runs the per-module work shape from criterion 9: delete → deps → restructure → quality → promote/demote → KDoc → tests → verify → MODULE.md rewrite. Compose-Desktop conventions from `CLAUDE.md` (`staticCompositionLocalOf` tokens, `data class` defaults, `Light`/`Dark` companion objects) are explicit checkpoints in wave 5.

**Tech stack:** Kotlin Multiplatform, Compose Multiplatform (`runtime`, `foundation`, `material3`, `ui`, `components.resources`), `compose.uiTest` for tests. Resources comes via `api(projects.resources)`. Tests: JUnit 4 + `kotlin.test` + Compose UI test, hand-written fakes only.

**Inventory of `design` as of plan-time (18 files):**
- commonMain (11 production):
  - `color/` (2): `Color.kt` (raw hex tokens), `ColorScheme.kt` (semantic groups).
  - `font/` (2): `Font.kt` (`expect`), `FontScheme.kt`.
  - `image/` (2): `ThemedImage.kt`, `ThemedPainter.kt`.
  - `motion/` (1): `MotionScheme.kt` (added in `a9daf28a8` for v0.0.17-alpha01).
  - `radius/` (1): `RadiusScheme.kt`.
  - `space/` (1): `SpaceScheme.kt`.
  - `theme/` (1): `Theme.kt` — the `System` object accessor + `YallaTheme` composable.
- commonTest (6): `ColorSchemeTest`, `FontSchemeEqualityTest`, `ThemedImageTest`, `RadiusSchemeTest`, `SpaceSchemeTest`, `YallaThemeTest`.
- androidMain (1): `Font.android.kt` (actual).
- iosMain (1): `Font.ios.kt` (actual).
- `MODULE.md`: still old `# Package` style; phase-1 form rewrite is wave 10.
- `build.gradle.kts`: 1 `api()` (resources), 5 `implementation()` Compose deps.

**Carried forward from phase-2 audits:**
- None. Phase-2's deferred items (value-class identifiers G3) all landed in phase-2 data. The phase-3 plan starts clean against `cleanup/phase-2-3-4`.

---

## Pre-work — Branch and baseline

**Files:** none modified.

- [ ] **Step 1: Verify branch is `cleanup/phase-2-3-4`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && git rev-parse --abbrev-ref HEAD
  ```
  Expected: `cleanup/phase-2-3-4`. If not, `git checkout cleanup/phase-2-3-4`.

- [ ] **Step 2: Baseline-build `design`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :design:compileKotlinIosSimulatorArm64 :design:allTests --rerun-tasks
  ```
  Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Record the baseline.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    grep -hE "tests=" design/build/test-results/iosSimulatorArm64Test/TEST-*.xml | \
    grep -oE 'tests="[0-9]+"' | grep -oE '[0-9]+' | \
    awk '{s+=$1} END {print "design tests:", s}'
  ```
  Note the number for wave 11's summary. Floor — wave 8 raises it.

---

## Wave 1 — Audit `design` (Stage A)

**Goal:** Produce `DESIGN_AUDIT.md` at repo root with concrete findings keyed to each criterion. Drives waves 2-10. Deletes at end of phase 4 with `CLEANUP_CRITERIA.md`.

**Files:**
- Create: `DESIGN_AUDIT.md` (repo root).

- [ ] **Step 1: Dispatch a fresh Opus subagent to audit `design` against criteria 1, 2, 4, 6, 9-3, 11.**

  Use the `Agent` tool with `subagent_type: general-purpose`, `model: opus`, `description: "Audit yalla-sdk design module"`. Read-only — must not edit any source.

  Prompt to use verbatim:
  ```
  Audit /Users/islom/StudioProjects/yalla-sdk/design against the criteria
  in /Users/islom/StudioProjects/yalla-sdk/CLEANUP_CRITERIA.md and write
  the output to /Users/islom/StudioProjects/yalla-sdk/DESIGN_AUDIT.md.

  Read CLEANUP_CRITERIA.md, CORE_AUDIT.md, and DATA_AUDIT.md first — every
  finding ties to a specific criterion, and the prior audits set the format
  precedent (8 sections + summary stats + reviewer notes). Match that
  structure closely.

  The audit MUST be structured into 8 sections:

  ## 1. AI-blob deletions (criterion 2)
  Per file in commonMain + androidMain + iosMain, list buckets 2-1
  through 2-5 with file path + line range + 1-sentence reason.
  Pay special attention to:
  - @since X.Y.Z tags (phase-2 swept these from core + data; do the same here)
  - Per-property KDoc that paraphrases data class fields
  - Per-token KDoc that just spells out the token name
  - "Brand voice" / aspirational paragraphs (e.g. "Provides the visual
    foundation for all Yalla UI components" type framing)
  - Test-only abstractions never instantiated outside their tests

  ## 2. Module dependency graph (criterion 4)
  List every line in design/build.gradle.kts. Verify which deps are
  exposed in public type signatures (return types, parameter types,
  supertype slots). The `api(projects.resources)` is correct because
  ThemedImage/ThemedPainter return Compose Resource types — verify.
  For each compose.* `implementation()`, verify it isn't accidentally
  exposed in a public signature that would force consumers to declare it.
  Output a recommended "Depends on" block.

  ## 3. Restructure candidates (criterion 9-3)
  - wc -l on every commonMain/androidMain/iosMain file. >300 lines is
    the god-file threshold (criterion 11).
  - Look for organization-only nesting. Each token type is in its own
    package (color/, font/, motion/, radius/, space/, theme/, image/).
    That's already flat per-concern; flag if any sub-nesting is
    organization-only.
  - The `motion/` package has only `MotionScheme.kt`. Some token
    packages have just one file. That's fine if each carries a
    coherent token type — flag only if a package's contents are
    genuinely orphan.

  ## 4. Quality / rewrite candidates (criterion 11)
  Compose-Desktop specific checks from CLAUDE.md:
  - Tokens via staticCompositionLocalOf — design tokens should be
    accessed via Local* CompositionLocals like LocalAppColors.current,
    LocalAppTokens.current, LocalStrings.current, NOT via static
    singletons (AppColors.Primary). Verify that the `System` object
    (in theme/Theme.kt) follows the CompositionLocal pattern.
  - Tokens are `data class`es with sensible defaults. Theme variants
    live in `Light` / `Dark` companion objects.
  - Compose recomposition correctness: `@Composable` functions that
    return tokens (e.g., `System.color`) should not box stable types,
    should not allocate per-recomposition, etc.
  - The `theme/Theme.kt` is the canonical site — read its structure
    and call out any deviation from the documented pattern.

  Per-file check for:
  - Non-idiomatic Kotlin (callback chain, ArrayList literals, Any?
    where a sealed type fits, etc.).
  - Architecture violations: hardcoded product-specific copy in
    primitives that should be parameterized (color names with Ildam
    branding inside the SDK is OK, but a hardcoded "Принять заказ"
    string would not be).
  - Untestable shape (god class, side-effecting constructor).

  Mark "REWRITE >100 LINES — NEEDS GATE" prominently for items
  requiring Islom's approval.

  ## 5. Promote/demote candidates (criterion 1)
  Apply the lego test to every public type. design is the visual
  language — clean bricks expected. But check for:
  - Hardcoded Russian/Uzbek strings in commonMain (grep:
    `[А-Яа-яЁё]\|[ʻ]` against design/src/commonMain).
  - Token values that look product-specific (e.g., a "Yalla blue"
    that has Ildam-internal meaning vs. a generic primary color —
    Yalla branding IS the product, so this is OK as long as it's not
    leaking business semantics).
  - UI components that look screen-shaped or state-machine-like
    (none expected; design is tokens-only).
  Output the lego verdict.

  ## 6. Missing tests (criterion 6)
  design already has 6 test files. Inventory:
  - For each public token (Color, Font, Motion, Radius, Space) and
    public composable (YallaTheme, themedPainter, etc.), verify a
    test exists. List gaps.
  - For YallaTheme: is the Light/Dark switch tested? Are the
    CompositionLocal providers asserted?
  - For ThemedImage/ThemedPainter: is the light/dark variant
    selection tested?
  - Group findings by package; estimate effort.

  ## 7. MODULE.md staleness (criterion 5)
  Compare current design/MODULE.md against phase-1 form. Reference
  docs:
  - bom/MODULE.md (canonical phase-1 form)
  - core/MODULE.md (post-cleanup phase-2 example)
  - data/MODULE.md (post-cleanup phase-2 example)
  Note: design/MODULE.md still uses `# Package …` blurbs +
  "Architecture" section. List sections to add/remove/rewrite.

  ## 8. Reviewer notes
  - Pushback on specific findings.
  - Cross-cutting patterns (e.g., "every token data class shares the
    same Light/Dark companion shape — confirm consistency").
  - Concerns with the criteria as applied to design (e.g., criterion
    6's state-machine bar doesn't apply, this is tokens-only).

  Output requirements:
  - Every finding has an absolute path under
    /Users/islom/StudioProjects/yalla-sdk/.
  - Line ranges where applicable.
  - No editorial commentary outside the structured findings.
  - Plain markdown, no frontmatter.
  - Estimated time investment per finding ("~5 min", "~30 min", "~2h").

  Do NOT modify any source files. Output is ONE file:
  /Users/islom/StudioProjects/yalla-sdk/DESIGN_AUDIT.md.

  Report at end: total finding count per section, the longest single
  rewrite candidate (lines), any blocking issues.
  ```

- [ ] **Step 2: Verify `DESIGN_AUDIT.md` was created with 8 sections.**

  Run:
  ```bash
  ls -la /Users/islom/StudioProjects/yalla-sdk/DESIGN_AUDIT.md && \
    grep -c "^## [0-9]" /Users/islom/StudioProjects/yalla-sdk/DESIGN_AUDIT.md
  ```
  Expected: file exists, `grep -c` returns `8`.

- [ ] **Step 3: Sanity-skim findings.**

  Read the audit. Look for any rewrite >100-line gate items, any false-positive AI-blob flags, any architectural pushbacks (especially around Compose recomposition behavior — if the audit flags something as "rewrite to staticCompositionLocalOf" but the existing code already follows the pattern, push back).

- [ ] **Step 4: Commit the audit.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add DESIGN_AUDIT.md && \
    git commit -m "$(cat <<'EOF'
  docs(design): add DESIGN_AUDIT.md — phase-3 inventory

  Output of wave 1 of PHASE_3_DESIGN_PLAN.md. Drives waves 2-10. Deleted
  at the end of phase 4 with CLEANUP_CRITERIA.md.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
  EOF
  )"
  ```

---

## Decision gate — Islom reviews `DESIGN_AUDIT.md`

**Files:** none modified.

- [ ] **Step 1: Stop. Tell Islom: "Wave 1 done. `DESIGN_AUDIT.md` committed as `<sha>`. Review it and tell me which findings to apply, which to skip, and any rewrite-gate decisions for >100-line items."**

- [ ] **Step 2: Wait for Islom's response.** Bulk approve, selective approve, or reject with reasons.

- [ ] **Step 3: Append `DESIGN_AUDIT.md` with `## 9. Approval` listing what was approved/rejected/deferred.** Commit:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add DESIGN_AUDIT.md && \
    git commit -m "docs(design): record wave-1 audit decisions"
  ```

---

## Wave 2 — Delete bloat (criterion 2)

**Files:** every file flagged in `DESIGN_AUDIT.md` section 1.

Same shape as phase-2 wave 2: split deletions across logical commits, compile + test between commits.

- [ ] **Step 1: `@since` sweep.** Reuse the same Python script from core's `331c47bc7` and data's `ee8c95b8a`, pointed at `design/src/commonMain`, `design/src/androidMain`, `design/src/iosMain`. Compile + test:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :design:compileKotlinIosSimulatorArm64 :design:allTests
  ```
  Commit:
  ```
  docs(design): drop @since tags across module

  Removed N @since lines from M files. Alpha versioning means no
  consumer tracks them; they're noise per criterion 2-1.

  Tests: <baseline> passing (unchanged).
  ```

- [ ] **Step 2: Paraphrase KDoc sweep.** Dispatch a Sonnet subagent with the same prompt structure used in core's wave 2 (commit `62b144900`) and data's wave 2 (commit `7c3266b62`), but pointed at `design/`'s file list from `DESIGN_AUDIT.md` §1. Apply rules:
  - Delete property paraphrase, ceremony banners, redundant getter doc.
  - Keep usage blocks, threading notes, side effects, semantic groupings ("text — used for body copy, headings; not for chrome"), Material3-interop notes.
  - Preserve the `theme/Theme.kt` `System` object's KDoc paragraph that explains the CompositionLocal pattern (criterion 5: info-dense).
  Compile + test. Commit:
  ```
  docs(design): strip paraphrase KDoc per criterion 2-1
  ```

- [ ] **Step 3: Delete bucket 2-3, 2-4, 2-5 findings** per audit. Likely small/zero given design is mostly tokens. Compile + test per logical group. Commit per group.

- [ ] **Step 4: Final wave-2 verification.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :design:compileKotlinIosSimulatorArm64 :design:allTests
  ```
  All green.

---

## Wave 3 — Module dependency rules (criterion 4)

**Files:**
- Modify: `design/build.gradle.kts` — apply audit §2 deltas (likely small; `design` already has only 1 `api()` and 5 `implementation()`).
- Modify: `design/MODULE.md` — append a "Depends on" block (full rewrite is wave 10).

- [ ] **Step 1: Apply audit §2 deltas.** For each `api()` flagged as internal-only (none expected — `api(projects.resources)` is needed because `ThemedImage`/`ThemedPainter` return Compose Resource types), demote. Drop unused.

- [ ] **Step 2: Append "Depends on" block to design/MODULE.md** with the recovered DAG.

- [ ] **Step 3: Compile downstream.** `design` feeds `primitives`, `composites`, `foundation`:

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :design:compileKotlinIosSimulatorArm64 \
              :foundation:compileKotlinIosSimulatorArm64 \
              :primitives:compileKotlinIosSimulatorArm64 \
              :composites:compileKotlinIosSimulatorArm64
  ```
  All green. Cascade explicit deps to downstream if needed (foundation consequence-pattern from core's wave 3).

- [ ] **Step 4: Run tests.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :design:allTests :foundation:allTests
  ```

- [ ] **Step 5: Commit.**
  ```
  refactor(design): tighten api/implementation split, drop unused deps

  Per criterion 4 of CLEANUP_CRITERIA.md.
  ```

---

## Wave 4 — Restructure (criterion 9-3)

**Files:** per `DESIGN_AUDIT.md` §3 findings. Likely no-op or tiny — design is already structured per-concern with each token type in its own package.

- [ ] **Step 1: Apply approved restructures.** For each flagged split, use `git mv` to preserve history, update package declarations + import sites, compile + test.

- [ ] **Step 2: Compile entire SDK** if any imports cross module boundaries:
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
              :media:compileKotlinIosSimulatorArm64
  ```

- [ ] **Step 3: Run tests** for `design` + downstream that imports `design`'s changed paths.

- [ ] **Step 4: Commit per logical restructure** with `refactor!:` prefix where import paths change. Skip wave entirely if audit found nothing.

---

## Wave 5 — Quality pass (criterion 11)

**Files:** every file flagged in `DESIGN_AUDIT.md` §4.

Compose-Desktop-specific patterns this wave verifies (per CLAUDE.md):

1. **`staticCompositionLocalOf` for token access.** `theme/Theme.kt`'s `System` object should expose tokens via `Local*` CompositionLocals (e.g., `LocalAppColors.current`), not as static singletons. Audit §4 will identify any deviation.
2. **`data class` tokens with sensible defaults + `Light`/`Dark` companion objects** for theme variants.
3. **Recomposition correctness.** `@Composable` token accessors should not allocate per recomposition; tokens should be `Stable`/`Immutable` where appropriate.

- [ ] **Step 1: Triage rewrite candidates.** For each entry:
  - **Small** (<100 lines, no API shape change): apply directly.
  - **>100 lines OR public-shape change**: write a one-paragraph rationale, present to Islom, wait for approval before applying.

- [ ] **Step 2: Apply approved rewrites.** Behavior preserved — only internals reshape (with the exception of any explicit `refactor!:` items the gate approved).

  Common patterns to apply:
  - Static singleton → `staticCompositionLocalOf<T>` + provided in `YallaTheme`.
  - `class FooScheme(val a: Int, val b: Int) { companion object { val Default = ... } }` → `data class FooScheme(val a: Int, val b: Int) { companion object { val Light = ...; val Dark = ... } }` (only if the audit flags inconsistency).
  - Add `@Stable` / `@Immutable` annotations where the type's contract supports them.

- [ ] **Step 3: Compile + test after each rewrite.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :design:compileKotlinIosSimulatorArm64 :design:allTests
  ```

- [ ] **Step 4: Commit per rewrite.** `refactor!:` if the public shape changes, `refactor:` otherwise. Each rewrite is its own commit so it can be reverted independently if downstream breaks.

- [ ] **Step 5: Run full SDK build** to catch downstream breakage:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew compileKotlinIosSimulatorArm64
  ```

---

## Wave 6 — Promote/demote flags (criterion 1)

**Files:**
- Modify: `MIGRATION_LIST.md` — append a `## Phase 3 — design additions` section per audit §5.

- [ ] **Step 1: Open `DESIGN_AUDIT.md` §5.** For each entry approved at the gate:
  - **Promotion** (YallaClient → SDK) — append to `MIGRATION_LIST.md` under "To promote into the SDK".
  - **Demotion** (SDK → YallaClient) — append to "To demote from the SDK".
  - **Borderline** — append to "To decide".

- [ ] **Step 2: Append breaking changes** since the last MIGRATION_LIST.md update:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git log --grep="^refactor!" --format="%h %s" \
            $(git log --format="%H" -1 -- MIGRATION_LIST.md)..HEAD
  ```
  to list new `refactor!:` commits.

- [ ] **Step 3: Commit.**
  ```
  docs(design): append design-phase entries to MIGRATION_LIST.md

  Per criterion 1 + 8 of CLEANUP_CRITERIA.md.
  ```

---

## Wave 7 — KDoc bar (criterion 5)

**Files:** every public symbol in `design/src/commonMain` whose KDoc is missing or only paraphrased after wave 2's sweep.

- [ ] **Step 1: Dispatch a fresh Sonnet subagent to identify gaps.** Same prompt structure as core's wave 7 (the "KDoc gap audit for core" agent) and data's wave 7. Output a list of files + line numbers + suggested KDoc text.

  Particular things to flag:
  - `System` object in `theme/Theme.kt` — its role as the design-token accessor.
  - Each token-scheme `companion object Light`/`Dark` — what makes one different from the other beyond the obvious palette change.
  - `themedPainter` / `ThemedImage` — when to use one over the other, recomposition behavior with theme switches.

- [ ] **Step 2: Apply the suggestions** with `Edit`. Compile + test after every ~5 files.

- [ ] **Step 3: Commit.**
  ```
  docs(design): fill KDoc gaps surfaced by criterion-5 audit

  <list affected packages>. Per criterion 5 of CLEANUP_CRITERIA.md.
  ```

---

## Wave 8 — Test backfill (criterion 6)

**Files:** new test files per `DESIGN_AUDIT.md` §6 gaps.

- [ ] **Step 1: For each gap, write the failing test first** (TDD).

  Example for a missing `MotionScheme` companion-object identity test (if the audit flags it):
  ```kotlin
  // design/src/commonTest/kotlin/uz/yalla/design/motion/MotionSchemeEqualityTest.kt
  package uz.yalla.design.motion

  import kotlin.test.Test
  import kotlin.test.assertEquals
  import kotlin.test.assertNotEquals

  class MotionSchemeEqualityTest {
      @Test
      fun shouldBeEqualWhenAllFieldsMatch() {
          assertEquals(MotionScheme.Default, MotionScheme.Default)
      }

      @Test
      fun shouldDifferWhenFieldsDiffer() {
          // Use whatever fields MotionScheme has; example placeholder:
          // assertNotEquals(
          //     MotionScheme(durationFast = 100, durationSlow = 300),
          //     MotionScheme(durationFast = 200, durationSlow = 300),
          // )
      }
  }
  ```
  Tailor to the actual MotionScheme API surface.

  Existing precedent: `FontSchemeEqualityTest` and `RadiusSchemeTest` show
  the equality + companion-object test pattern. Match that style.

- [ ] **Step 2: Run the failing test** to confirm it fails for the right reason.

- [ ] **Step 3: Run the full design test suite.** Confirm count rose.

- [ ] **Step 4: Commit per logical group** (one commit per file/package).

---

## Wave 9 — Final compile + test verification

**Files:** none modified.

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
  Expected: BUILD SUCCESSFUL across all 12 modules.

- [ ] **Step 2: Run tests for every testable module.**

  Skip `:firebase:linkDebugTestIosSimulatorArm64` and `:maps:linkDebugTestIosSimulatorArm64` — those are pre-existing iOS-linker infrastructure failures on `main`, NOT regressions from this branch (verified during phase-2 cleanup). Run the rest:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :core:allTests :data:allTests :foundation:allTests \
              :design:allTests :primitives:allTests \
              :composites:allTests :media:allTests :platform:allTests
  ```

- [ ] **Step 3: Tally per-module test counts.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    for m in core data foundation design primitives composites media platform; do
      count=$(grep -hE "tests=" $m/build/test-results/iosSimulatorArm64Test/TEST-*.xml 2>/dev/null | grep -oE 'tests="[0-9]+"' | grep -oE '[0-9]+' | awk '{s+=$1} END {print s+0}')
      fails=$(grep -hE "tests=" $m/build/test-results/iosSimulatorArm64Test/TEST-*.xml 2>/dev/null | grep -oE 'failures="[0-9]+"' | grep -oE '[0-9]+' | awk '{s+=$1} END {print s+0}')
      printf "%-12s tests=%4d failures=%d\n" "$m" "$count" "$fails"
    done
  ```
  Expected: zero failures everywhere; `design` count > baseline (Pre-work step 3).

---

## Wave 10 — MODULE.md to phase-1 form

**Files:**
- Modify: `design/MODULE.md`.

Reference: `core/MODULE.md` (post-cleanup, same branch — `ebf30bc6a`) and `data/MODULE.md` (`8906cc9e9`). Those are the post-phase-2 canonical examples.

- [ ] **Step 1: Rewrite `design/MODULE.md`** to:
  ```
  # Module design
  > One-line tagline.

  ## What this is
  ## What this is NOT
  ## Usage
  ## Notes
  ## Depends on
  ```

  - **Tagline** suggestion: `> Visual language — color, font, motion, radius, space, theme.`
  - **What this is**: list the seven token surfaces (`color`, `font`, `motion`, `radius`, `space`), the `image` package (`ThemedImage`, `themedPainter`), and the `theme` package (`System` object + `YallaTheme` composable). Note that tokens are accessed via `System.color`/`System.font`/etc. inside Compose content, with `Local*` CompositionLocals as the underlying mechanism.
  - **What this is NOT**: not Material3 itself (interops with it); not a UI primitive layer (those live in `primitives`); not feature-specific copy or branding (those live in YallaClient).
  - **Usage**: a 5-line example showing `YallaTheme { Box(modifier = Modifier.background(System.color.surface)) { … } }`.
  - **Notes**: anything surprising — e.g., the Material3 interop seam, the Light/Dark companion-object pattern, the ADR-anchored decisions if any are still relevant after phase-1's docs/ removal.
  - **Depends on**: from wave 3.

- [ ] **Step 2: Delete the old `# Package …` blocks + the "Architecture" header.**

- [ ] **Step 3: Commit.**
  ```
  docs(design): rewrite MODULE.md to phase-1 form

  Aligns with bom/, resources/, core/, data/. Per criterion 5 of
  CLEANUP_CRITERIA.md.
  ```

---

## Wave 11 — Phase-3-design summary + readiness for `foundation`

**Files:** none modified.

- [ ] **Step 1: Run the full SDK build one final time.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew compileKotlinIosSimulatorArm64
  ```

- [ ] **Step 2: Tally outputs.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git log --oneline cleanup/phase-2-3-4 --not main | head -50 && \
    git diff --stat main..HEAD -- design/ | tail -3 && \
    cat MIGRATION_LIST.md | head -120
  ```

- [ ] **Step 3: Tell Islom: "Phase-3 `design` cleanup complete. Summary: <stats>. `MIGRATION_LIST.md` now has <N> entries (was <M> before design). Ready to plan phase-3 `foundation`?"**

  No commit. The `foundation` plan is its own writing-plans run after Islom approves moving on.

---

## Self-review checklist (run before handoff)

- [x] Spec coverage — every CLEANUP_CRITERIA criterion 1-11 has at least one wave applying it.
- [x] No placeholders — every `<…>` is a runtime-fill variable explicitly explained.
- [x] Type/method consistency — file paths absolute, package names match, `DESIGN_AUDIT.md` referenced consistently.
- [x] Each wave has a verify step before commit.
- [x] Decision gate after wave 1; rewrite gate inside wave 5 (>100-line items).
- [x] Versioning policy honored — no alpha tag, commits stay on `cleanup/phase-2-3-4`.
- [x] Test bar from criterion 6 enforced in wave 8 + verified in wave 9.
- [x] Pre-existing iOS-linker failures on `:firebase` and `:maps` excluded from wave-9 verification with the documented rationale.
- [x] Compose-Desktop-specific concerns from CLAUDE.md (`staticCompositionLocalOf`, `Light`/`Dark` companions, recomposition correctness) are explicit checkpoints in wave 5.

---

## Out of scope (do NOT do in this plan)

- Any other phase-3 module (`foundation`, `primitives`, `composites`).
- Any phase-4 module (`firebase`, `maps`, `media`, `platform`).
- Public API visibility tightening (criterion 3 — left alone in alpha).
- Reintroducing `docs/` or `docs/adr/`.
- Publishing an alpha. Criterion 8 — single bump at end of phase 4.
- Rewriting `:maps` / `:firebase` iOS-link infrastructure. Pre-existing on main; not a cleanup concern.
