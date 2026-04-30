# Phase 3 — `foundation` Cleanup Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply [CLEANUP_CRITERIA.md](CLEANUP_CRITERIA.md) criteria 1-11 to the `foundation` module on the `cleanup/phase-2-3-4` branch. `foundation` is the glue layer between `core`'s pure types and the UI brick stack — `BaseViewModel`, `LoadingController`, `ObserveAsEvents`, `LocationManager`, locale switching, settings option models, `DefaultDataErrorMapper`. After cleanup, `primitives` and `composites` snap onto a tighter ViewModel and location surface.

**Architecture:** Same two-stage flow as the prior cleanup plans. **Stage A** is wave 1's audit, producing `FOUNDATION_AUDIT.md`. A decision gate sits between A and B. **Stage B** runs the per-module work shape from criterion 9: delete → deps → restructure → quality → promote/demote → KDoc → tests → verify → MODULE.md rewrite. Wave 5's quality pass has explicit checkpoints for the patterns from `CLAUDE.md` (Orbit `BaseComponent`, partial extension files, hand-rolled fakes, `internal object` mappers, and architectural seams between `foundation` and `core`).

**Tech stack:** Kotlin Multiplatform, Compose Multiplatform (`runtime`, `foundation`, `material3`, `components.resources`), Koin (core + compose-viewmodel), Orbit (core + viewmodel + compose), kermit, androidx.lifecycle (viewmodel-compose, runtime-compose), moko-geo (`libs.geo` + `libs.geo.compose`), connectivity-device. Tests: JUnit 4 + `kotlin.test` + Turbine + hand-written fakes.

**Inventory of `foundation` as of plan-time (36 files):**
- commonMain (22 production):
  - `animation/` (1): `StaggerReveal.kt` — stagger-reveal helper.
  - `infra/` (5): `BaseViewModel`, `LoadingController`, `ObserveAsEvents`, `DataErrorMapper` (interface), `DefaultDataErrorMapper` (the consumer of `DataError.Network.*`).
  - `locale/` (3): `ChangeLanguage` (`expect`), `LocaleProvider`, `ObserveLocale`.
  - `location/` (7): `Location` (foundation's domain location type, distinct from `core/geo/GeoPoint`), `LocationManager`, `LocationMappers`, `LocationProvider` (foundation-side, distinct from `core/location/LocationProvider`), `LocationServices` (`expect`), `LocationState`, `LocationTrackerFactory` (`expect`).
  - `settings/` (5): `LanguageOption`, `MapOption`, `OptionModel`, `Selectable`, `ThemeOption`.
  - `infra/DataErrorMapper` + `DefaultDataErrorMapper`: producer-pair for the deleted `DataError.Network.Server`-busy etc. localized strings (already simplified after core G1 deleted the semantic variants).
- commonTest (7): `BaseViewModelTest`, `DefaultDataErrorMapperTest`, `LoadingControllerTest`, `ObserveAsEventsTest`, `ChangeLanguageTest`, `LocationMappersTest`, `SettingsOptionsTest`.
- androidMain (3): `ChangeLanguage.android`, `LocationServices.android`, `LocationTrackerFactory.android`.
- iosMain (3): mirrored.
- iosTest (2): `FakePermissionsController`, `LocationManagerTrackingTest` — moko-permissions test harness.
- `MODULE.md`: still uses the `# Module / # Package …` style with an "Architecture" section; phase-1 form rewrite is wave 10.
- `build.gradle.kts`: 1 `api()` (`projects.core`) + 16 `implementation()` deps. Larger surface than design (5) or core (3).

**Carried forward from prior audits:**
- **`LocationManager.kt`** still uses `kermit.Logger`. The `kermit` dep was added explicitly to foundation in the phase-2 core wave-3 cascade (commit `dad6c17c3`). Keep as-is unless the audit flags a logging idiom violation.
- **`DefaultDataErrorMapper.kt`** lost its dead `when` branches in core wave-2 commit `482eb16df`. The current shape (Network branches only) should be considered the new baseline.
- **`LocationMappers.kt`** picked up `.raw` unwrap calls in core wave-5c (commit `7bd4125a6`) as part of the value-class identifier rollout. The current shape is the new baseline.

**Carried forward from CORE_AUDIT.md / DESIGN_AUDIT.md (deferred items):**
- None. Phase-2 G3 (value-class identifiers) was applied in phase-2 data; phase-3 design's gate items were all closed in their wave 5.

---

## Pre-work — Branch and baseline

**Files:** none modified.

- [ ] **Step 1: Verify branch is `cleanup/phase-2-3-4`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && git rev-parse --abbrev-ref HEAD
  ```
  Expected: `cleanup/phase-2-3-4`. If not, `git checkout cleanup/phase-2-3-4`.

- [ ] **Step 2: Baseline-build `foundation`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :foundation:compileKotlinIosSimulatorArm64 :foundation:allTests --rerun-tasks
  ```
  Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Record the baseline.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    grep -hE "tests=" foundation/build/test-results/iosSimulatorArm64Test/TEST-*.xml | \
    grep -oE 'tests="[0-9]+"' | grep -oE '[0-9]+' | \
    awk '{s+=$1} END {print "foundation tests:", s}'
  ```
  Expected: 56 (per the phase-2 wave-9 verification). Note for wave 11.

---

## Wave 1 — Audit `foundation` (Stage A)

**Goal:** Produce `FOUNDATION_AUDIT.md` at repo root with concrete findings keyed to each criterion. Drives waves 2-10. Deletes at end of phase 4 with `CLEANUP_CRITERIA.md`.

**Files:**
- Create: `FOUNDATION_AUDIT.md` (repo root).

- [ ] **Step 1: Dispatch a fresh Opus subagent to audit `foundation` against criteria 1, 2, 4, 6, 9-3, 11.**

  Use the `Agent` tool with `subagent_type: general-purpose`, `model: opus`, `description: "Audit yalla-sdk foundation module"`. Read-only — must not edit any source.

  Prompt to use verbatim:
  ```
  Audit /Users/islom/StudioProjects/yalla-sdk/foundation against the criteria
  in /Users/islom/StudioProjects/yalla-sdk/CLEANUP_CRITERIA.md and write
  the output to /Users/islom/StudioProjects/yalla-sdk/FOUNDATION_AUDIT.md.

  Read CLEANUP_CRITERIA.md, CORE_AUDIT.md, DATA_AUDIT.md, and DESIGN_AUDIT.md
  first — every finding ties to a specific criterion, and the prior audits
  set the format precedent (8 sections + summary stats + reviewer notes).
  Match that structure closely.

  The audit MUST be structured into 8 sections:

  ## 1. AI-blob deletions (criterion 2)
  Per file in commonMain + androidMain + iosMain + iosTest, list buckets
  2-1 through 2-5 with file path + line range + 1-sentence reason.
  Pay special attention to:
  - @since X.Y.Z tags (phase 2 + design swept these from those modules;
    do the same here)
  - Per-property KDoc that paraphrases data class fields
  - Aspirational paragraphs (e.g., "Foundation serves as the intentional
    glue layer..." style framing in MODULE.md or class-level KDocs)
  - Single-use abstractions (interfaces with one impl in this module
    that aren't opened for external extension)
  - Dead expect declarations with no actual implementations
  - Speculative generalization in helper utilities

  For 2-3 detection: for each `interface`/abstract class in foundation,
  grep the entire SDK + (best-effort) YallaClient for implementations.
  One impl + not opened for extension by external consumers → flag
  bucket 2-3. Note that `DataErrorMapper` is an interface with one impl
  (`DefaultDataErrorMapper`); investigate whether YallaClient or any SDK
  consumer provides a custom impl, or whether this is a single-use
  abstraction kept "for testability".

  For 2-4 detection: for each public function/property in
  foundation/src/commonMain, grep the entire repo for callers. Public
  symbols with zero callers are candidates unless they're explicit public
  API entry points (factory, plugin, top-level helper).

  ## 2. Module dependency graph (criterion 4)
  - List every line in foundation/build.gradle.kts: `api()` vs
    `implementation()` declarations across commonMain / androidMain /
    iosMain / commonTest source sets. Foundation has 17 deps total —
    larger surface than any module audited so far.
  - For each `api()` declaration (currently only `projects.core`), verify
    whether more should be promoted (anything that appears in a public
    type signature). Examples to check:
    - `compose.runtime` — `BaseViewModel` exposes `@Composable` methods;
      probably needs `api`.
    - `orbit.viewmodel` — `BaseViewModel` extends `ContainerHost<S, E>`;
      consumers need this in scope.
    - `androidx.lifecycle.viewmodel.compose` — `BaseViewModel` extends
      `ViewModel`?
    - `geo` / `geo.compose` — does any public type expose moko-geo
      types directly?
  - For each `implementation()` dep, verify any usage at all. Flag unused.
  - Output a recommended "Depends on" block formatted like
    DESIGN_AUDIT.md §2.

  ## 3. Restructure candidates (criterion 9-3)
  - Run `wc -l` on every commonMain/androidMain/iosMain/iosTest .kt file.
    List files >300 lines as god-file candidates. For each, count
    distinct top-level types/responsibilities; mark "split candidate"
    only when >5 distinct concerns or >300 lines.
  - Look for organization-only nesting. Foundation has 5 sub-packages
    (animation, infra, locale, location, settings) — flag any that's
    suspicious.
  - The `infra/` package mixes ViewModel (`BaseViewModel`,
    `LoadingController`, `ObserveAsEvents`) with error mapping
    (`DataErrorMapper`, `DefaultDataErrorMapper`). Flag whether
    `infra/` should split into `infra/viewmodel/` + `infra/error/` for
    coherence — recommend keep or split.

  ## 4. Quality / rewrite candidates (criterion 11)
  Per file (focus on commonMain), check for:
  - Non-idiomatic Kotlin: callback chain, ArrayList literals, `Any?`
    where a sealed type fits, etc.
  - Architecture violations per CLAUDE.md:
    - **`try { … } catch { … }` in business logic** — foundation is the
      "glue layer," NOT a network/storage adapter. Try/catch in
      foundation is a candidate violation. Confirm per-occurrence.
    - **Mappers as classes / DTO extension functions** — `LocationMappers.kt`
      is in `foundation/`. Per CLAUDE.md, mappers should be `internal
      object Mapper { fun fromDto(...): Domain }` and live in `data/`.
      Investigate whether `LocationMappers` is actually a DTO mapper
      or a domain↔domain coercion (foundation's `Location` type vs.
      `core/geo/GeoPoint`).
    - **Service classes named `Api`** — foundation should have none;
      confirm.
    - **Custom MVI** — `BaseViewModel` should use Orbit
      (`ContainerHost<State, Effect>`), not custom MVI.
    - **Arrow types instead of project's `Either` + `DataError`** —
      confirm none.
    - **`InMemoryTokenProvider` / manual `Authorization` header /
      `AuthEventBus`** — foundation has no networking, so N/A.
  - Untestable shape: god class with hard-wired deps; side-effecting
    constructor (especially `LocationManager` — investigate).
  - Compose recomposition correctness: `@Composable` methods that
    return state should follow `staticCompositionLocalOf` /
    `Local*.current` pattern (same checkpoint as design wave 5).

  Specific file-level checks:
  - `infra/BaseViewModel.kt`: is `LoadingController` mixed into the same
    file or properly separated? Does it follow Orbit's
    `ContainerHost<State, Effect>` pattern? What is the relationship
    with `androidx.lifecycle.ViewModel`?
  - `infra/DefaultDataErrorMapper.kt`: shape verified after the wave-2
    `DataError.Network.*`-only simplification (commit `482eb16df`).
    Should be ~17 lines mapping each Network variant to a string
    resource. Flag if anything else lurks.
  - `location/LocationManager.kt`: does its constructor take
    side-effecting deps (e.g., `LocationTrackerFactory.create()` called
    in init)? Does it leak coroutines? Is there a clean `dispose()` /
    cancellation surface?
  - `location/LocationMappers.kt`: confirm whether the mappers are
    DTO↔domain (mover candidate to `data/`) or `Location`↔`GeoPoint`
    (domain↔domain, foundation-appropriate).
  - `locale/ChangeLanguage.kt` (expect): does the actual implementation
    leak platform types into commonMain via the expect signature?

  For each finding: file path + line range + suggested target pattern from
  CLEANUP_CRITERIA criterion 11. Estimate line-impact ("~30 lines",
  "~200 lines"). Mark "REWRITE >100 LINES — NEEDS GATE" prominently for
  items requiring Islom's approval.

  ## 5. Promote/demote candidates (criterion 1)
  Apply the lego test to every public type in foundation/src/commonMain.
  Foundation is the brick-glue layer — clean bricks expected, but check
  for product-shaped types accidentally living here:
  - Hardcoded Russian/Uzbek strings in commonMain (grep:
    `[А-Яа-яЁё]\|[ʻ]` against foundation/src/commonMain).
  - Settings option *names* that look product-specific (e.g., a
    "PrioritySupport" tier vs. a generic capability flag — flag).
  - Types that look screen-shaped or feature-orchestration-shaped (none
    expected; foundation should be VM infrastructure + location + locale
    + settings options).

  Critical: **the foundation `LocationProvider` interface vs. core's
  `LocationProvider` interface**. Two separate types with the same name —
  investigate whether one should be deleted. The phase-2 core wave-4
  flatten moved `core.contract.location.LocationProvider` to
  `core.location.LocationProvider`; foundation's
  `foundation.location.LocationProvider` may now be a redundant wrapper.

  Also: foundation's **`Location` type** vs. core's **`GeoPoint`** —
  same coordinates wrapper or genuinely distinct? `LocationMappers.kt`
  bridges them; verify the bridge is non-trivial enough to warrant
  two types.

  ## 6. Missing tests (criterion 6)
  Foundation has 7 commonTest files + 2 iosTest files. Inventory:
  - For each public function in commonMain, check if a corresponding
    test exists. List functions with no test.
  - For `BaseViewModel`: state-machine intent → state-transition tests
    per criterion 6's Orbit `ContainerHost` bar. List gaps.
  - For `LocationManager`: tracking lifecycle (start/stop/dispose),
    permission state, coroutine cancellation. Existing iosTest covers
    tracking on iOS; commonMain coverage gap?
  - For `LocaleProvider`/`ObserveLocale`: locale-change reactivity test.
  - For `DefaultDataErrorMapper`: every `DataError.Network.*` variant
    mapped (post-wave-2 simplification — should already cover all
    surviving variants). Confirm.
  - Group findings by package; estimate effort per package.

  Note: `iosTest` in this module uses moko-permissions's
  `FakePermissionsController` for `LocationManager`. Same harness can't
  run in commonTest because moko-permissions has platform-specific
  dependencies. Document this as the standard expect/actual testability
  carve-out.

  ## 7. MODULE.md staleness (criterion 5)
  Compare current foundation/MODULE.md against phase-1 form. Reference
  docs:
  - /Users/islom/StudioProjects/yalla-sdk/bom/MODULE.md (canonical)
  - /Users/islom/StudioProjects/yalla-sdk/core/MODULE.md (post-phase-2)
  - /Users/islom/StudioProjects/yalla-sdk/data/MODULE.md (post-phase-2)
  - /Users/islom/StudioProjects/yalla-sdk/design/MODULE.md (post-phase-3)
  List sections to add/remove/rewrite for the wave-10 rewrite.

  Note: foundation/MODULE.md has both "Architecture" framing and
  per-package blurbs. Drop both per phase-1 precedent.

  ## 8. Reviewer notes
  - Pushback on specific findings.
  - Cross-cutting patterns (e.g., "every settings Option type shares
    the same Selectable + label/icon pattern — confirm consistency").
  - Concerns with the criteria as applied to foundation (e.g.,
    criterion 11's "no try/catch in business logic" — foundation has
    `catch (_: Exception)` in `LocationManager` for permission denial;
    flag whether that's a sanctioned exception-to-the-rule per the
    pragmatic carve-out for permission APIs).

  Output requirements:
  - Every finding has an absolute path under
    /Users/islom/StudioProjects/yalla-sdk/.
  - Line ranges where applicable, exact (not "~lines 50-80" but
    "lines 50-80").
  - No editorial commentary outside the structured findings.
  - Plain markdown, no frontmatter.
  - Estimated time investment per finding ("~5 min", "~30 min", "~2h").

  Do NOT modify any source files. Output is ONE file:
  /Users/islom/StudioProjects/yalla-sdk/FOUNDATION_AUDIT.md.

  When done, report: total finding count per section, the longest
  single rewrite candidate (lines), any blocking issues encountered,
  and any questions about the foundation/core boundary that need
  Islom's input before wave-2 lands.
  ```

- [ ] **Step 2: Verify `FOUNDATION_AUDIT.md` was created with 8 sections.**

  Run:
  ```bash
  ls -la /Users/islom/StudioProjects/yalla-sdk/FOUNDATION_AUDIT.md && \
    grep -c "^## [0-9]" /Users/islom/StudioProjects/yalla-sdk/FOUNDATION_AUDIT.md
  ```
  Expected: file exists, `grep -c` returns `8`.

- [ ] **Step 3: Sanity-skim findings.**

  Read the audit. Look for: any rewrite >100-line gate items, any
  false-positive AI-blob flags, the foundation/core `LocationProvider`
  duplication question (likely a key gate decision), the
  `LocationMappers` location question, the `DataErrorMapper` single-impl
  question.

- [ ] **Step 4: Commit the audit.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add FOUNDATION_AUDIT.md && \
    git commit -m "$(cat <<'EOF'
  docs(foundation): add FOUNDATION_AUDIT.md — phase-3 inventory

  Output of wave 1 of PHASE_3_FOUNDATION_PLAN.md. Drives waves 2-10.
  Deleted at the end of phase 4 with CLEANUP_CRITERIA.md.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
  EOF
  )"
  ```

---

## Decision gate — Islom reviews `FOUNDATION_AUDIT.md`

**Files:** none modified.

- [ ] **Step 1: Stop. Tell Islom: "Wave 1 done. `FOUNDATION_AUDIT.md` committed as `<sha>`. Review it and tell me which findings to apply, which to skip, and any rewrite-gate decisions for >100-line items. Likely gate items: foundation vs. core `LocationProvider` duplication, `LocationMappers` location, `DataErrorMapper` interface vs. concrete."**

- [ ] **Step 2: Wait for Islom's response.** Bulk approve, selective approve, or reject with reasons.

- [ ] **Step 3: Append `FOUNDATION_AUDIT.md` with `## 9. Approval` listing what was approved/rejected/deferred.** Commit:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add FOUNDATION_AUDIT.md && \
    git commit -m "docs(foundation): record wave-1 audit decisions"
  ```

---

## Wave 2 — Delete bloat (criterion 2)

**Files:** every file flagged in `FOUNDATION_AUDIT.md` section 1.

Same shape as prior waves: split deletions across logical commits, compile + test between commits.

- [ ] **Step 1: `@since` sweep.** Reuse the same Python script from prior waves (`331c47bc7`, `ee8c95b8a`, `25d0eb9f4`), pointed at `foundation/src/commonMain`, `foundation/src/androidMain`, `foundation/src/iosMain`, and `foundation/src/iosTest`. Compile + test:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :foundation:compileKotlinIosSimulatorArm64 :foundation:allTests
  ```
  Commit:
  ```
  docs(foundation): drop @since tags across module

  Removed N @since lines from M files. Alpha versioning means no
  consumer tracks them; they're noise per criterion 2-1.

  Tests: <baseline> passing (unchanged).
  ```

- [ ] **Step 2: Paraphrase KDoc sweep.** Dispatch a Sonnet subagent with the same prompt structure used in prior waves (the design wave-2 paraphrase agent), pointed at foundation's file list from `FOUNDATION_AUDIT.md` §1. Apply rules:
  - Delete property paraphrase, aspirational MODULE.md framing, redundant getter/setter doc, single-line per-entry KDoc.
  - Keep usage blocks, threading rules, side effects, ownership notes (especially scope-ownership in `LocationManager` if any), Material3-interop notes.
  - Preserve the architectural rationale that explains the foundation/core split where it appears.
  Compile + test. Commit:
  ```
  docs(foundation): strip paraphrase KDoc per criterion 2-1
  ```

- [ ] **Step 3: Delete bucket 2-3, 2-4, 2-5 findings** per audit. Likely candidates:
  - Possibly the `DataErrorMapper` interface if Islom approves removing the single-use abstraction.
  - Possibly the foundation `LocationProvider` interface if Islom approves dropping the redundant wrapper.
  - Any dead utility flagged in audit §1.

  Each deletion is its own commit with `refactor!:` prefix where the public shape changes. Compile + test per logical group.

- [ ] **Step 4: Final wave-2 verification.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :foundation:compileKotlinIosSimulatorArm64 :foundation:allTests
  ```
  All green.

---

## Wave 3 — Module dependency rules (criterion 4)

**Files:**
- Modify: `foundation/build.gradle.kts` — apply audit §2 deltas. Likely larger than design's wave-3 because foundation has 17 deps.
- Modify: `foundation/MODULE.md` — append a "Depends on" block (full rewrite is wave 10).

- [ ] **Step 1: Apply audit §2 deltas.**
  - Drop any unused deps the audit flagged (note: foundation already has the `kermit` dep added in core's wave-3 cascade — keep it).
  - For each `implementation()` flagged for promotion to `api()`, switch. Likely candidates per the audit prompt:
    - `compose.runtime` (if any public composable exposes Compose types)
    - `orbit.viewmodel` (if `BaseViewModel : ContainerHost<S, E>`)
    - `androidx.lifecycle.viewmodel.compose` (if `BaseViewModel : ViewModel`)
    - `geo` (if `LocationManager` exposes moko-geo types in public signatures)

- [ ] **Step 2: Append "Depends on" block to foundation/MODULE.md** per audit §2.

- [ ] **Step 3: Compile downstream.** `foundation` feeds `primitives`, `composites`, `media`, `platform`, `firebase`, `maps`:

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :foundation:compileKotlinIosSimulatorArm64 \
              :primitives:compileKotlinIosSimulatorArm64 \
              :composites:compileKotlinIosSimulatorArm64 \
              :media:compileKotlinIosSimulatorArm64 \
              :platform:compileKotlinIosSimulatorArm64 \
              :firebase:compileKotlinIosSimulatorArm64 \
              :maps:compileKotlinIosSimulatorArm64
  ```
  All green. Cascade explicit deps to downstream if needed.

- [ ] **Step 4: Run tests.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :foundation:allTests :primitives:allTests :composites:allTests :media:allTests :platform:allTests
  ```

- [ ] **Step 5: Commit.**
  ```
  refactor(foundation): tighten api/implementation split, drop unused deps

  Per criterion 4 of CLEANUP_CRITERIA.md.
  ```

---

## Wave 4 — Restructure (criterion 9-3)

**Files:** per `FOUNDATION_AUDIT.md` §3 findings.

Likely candidates:
- Possibly split `infra/` into `infra/viewmodel/` + `infra/error/` if audit recommends.
- Possibly merge or remove the foundation `LocationProvider` if Islom approved.
- God-file splits if any flagged.

- [ ] **Step 1: Apply approved restructures.** Use `git mv` to preserve history. Update package declarations + import sites.

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

- [ ] **Step 3: Run tests** for foundation + downstream that imports foundation's changed paths.

- [ ] **Step 4: Commit per logical restructure** with `refactor!:` prefix where import paths change. Skip wave entirely if audit found nothing.

---

## Wave 5 — Quality pass (criterion 11)

**Files:** every file flagged in `FOUNDATION_AUDIT.md` §4.

Architectural patterns to verify (CLAUDE.md):

1. **Orbit `ContainerHost<State, Effect>`** — `BaseViewModel` should follow this; flag if it deviates into custom MVI.
2. **`internal object Mapper { fun fromDto(): Domain }`** — `LocationMappers.kt` shape; verify whether mappers are DTO↔domain (move to `data/`) or domain↔domain (foundation-appropriate, keep).
3. **`try { … } catch { … }` in business logic** — flagged per-occurrence. Permission-API exception is a known sanctioned carve-out; everything else gets scrutinized.
4. **`internal` by default** — visibility of helper types. Note that criterion 3 says public-API tightening is alpha-deferred; this wave only touches visibility if it's a clear single-use abstraction.

- [ ] **Step 1: Triage rewrite candidates.** For each entry in §4:
  - **Small** (<100 lines, no API shape change): apply directly.
  - **>100 lines OR public-shape change**: write a one-paragraph rationale, present to Islom, wait for approval before applying.

- [ ] **Step 2: Apply approved rewrites.** Behavior preserved — only internals reshape (with the exception of explicit `refactor!:` items the gate approved). Compile + test after each rewrite.

- [ ] **Step 3: Commit per rewrite.** `refactor!:` if public shape changes, `refactor:` otherwise. Each rewrite is its own commit so it can be reverted independently.

- [ ] **Step 4: Run full SDK build** to catch downstream breakage:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew compileKotlinIosSimulatorArm64
  ```

---

## Wave 6 — Promote/demote flags (criterion 1)

**Files:**
- Modify: `MIGRATION_LIST.md` — append a `## Phase 3 — foundation additions` section per audit §5.

- [ ] **Step 1: Open `FOUNDATION_AUDIT.md` §5.** For each entry approved at the gate:
  - **Promotion** (YallaClient → SDK) — append to `MIGRATION_LIST.md` "To promote into the SDK".
  - **Demotion** (SDK → YallaClient) — append to "To demote from the SDK".
  - **Borderline** — append to "To decide".

- [ ] **Step 2: Append breaking changes** since the last MIGRATION_LIST.md update:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git log --grep="^refactor!" --format="%h %s" \
            $(git log --format="%H" -1 -- MIGRATION_LIST.md)..HEAD
  ```

- [ ] **Step 3: Commit.**
  ```
  docs(foundation): append foundation-phase entries to MIGRATION_LIST.md

  Per criterion 1 + 8 of CLEANUP_CRITERIA.md.
  ```

---

## Wave 7 — KDoc bar (criterion 5)

**Files:** every public symbol in `foundation/src/commonMain` whose KDoc is missing or only paraphrased after wave 2's sweep.

- [ ] **Step 1: Dispatch a fresh Sonnet subagent to identify gaps.** Same prompt structure as prior waves' KDoc gap audits. Output a list of files + line numbers + suggested KDoc text.

  Things to flag specifically:
  - `BaseViewModel` — its `ContainerHost<State, Effect>` integration with `LoadingController`; thread/scope ownership.
  - `LocationManager` — start/stop semantics, scope ownership, permission-failure behavior.
  - `ChangeLanguage` (expect) — what each platform's actual implementation does (Android: `AppCompatDelegate.setApplicationLocales`; iOS: language preference UserDefaults).
  - `Selectable` interface + per-Option types — what makes one Option different from another.

- [ ] **Step 2: Apply suggestions** with `Edit`. Compile + test after every ~5 files.

- [ ] **Step 3: Commit.**
  ```
  docs(foundation): fill KDoc gaps surfaced by criterion-5 audit

  <list affected packages>. Per criterion 5 of CLEANUP_CRITERIA.md.
  ```

---

## Wave 8 — Test backfill (criterion 6)

**Files:** new test files per `FOUNDATION_AUDIT.md` §6 gaps.

- [ ] **Step 1: For each gap, write the failing test first** (TDD).

  Particular targets:
  - **`BaseViewModel`** — Orbit intent → state transition tests if missing. Existing `BaseViewModelTest.kt` covers what? Audit §6 will say.
  - **`LocationManager`** — commonMain coverage gap (iosTest covers tracking on iOS; commonMain needs at least the start/stop/dispose state machine via fakes).
  - **`LocaleProvider`/`ObserveLocale`** — locale-change reactivity.
  - **`StaggerReveal`** — animation helper testability.
  - **Per-Option settings types** — `Selectable` contract per Option.

- [ ] **Step 2: Run failing tests** to confirm they fail for the right reason.

- [ ] **Step 3: Run the full foundation test suite.** Confirm count rose.

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

  Skip `:firebase:linkDebugTestIosSimulatorArm64` and `:maps:linkDebugTestIosSimulatorArm64` — pre-existing iOS-linker infrastructure failures on `main`, NOT regressions from this branch (verified during phase-2 + phase-3 design).

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
  Expected: zero failures everywhere; `foundation` count > baseline 56.

---

## Wave 10 — MODULE.md to phase-1 form

**Files:**
- Modify: `foundation/MODULE.md`.

Reference: `core/MODULE.md`, `data/MODULE.md`, `design/MODULE.md` (post-cleanup, same branch). All three are the canonical post-cleanup examples.

- [ ] **Step 1: Rewrite `foundation/MODULE.md`** to:
  ```
  # Module foundation
  > One-line tagline.

  ## What this is
  ## What this is NOT
  ## Usage
  ## Notes
  ## Depends on
  ```

  - **Tagline** suggestion: `> ViewModel infrastructure, location, locale, settings — the brick glue between core and the UI stack.`
  - **What this is**: enumerate `BaseViewModel`, `LoadingController`, `ObserveAsEvents`, `DataErrorMapper`/`DefaultDataErrorMapper`, `LocationManager`, `Location`, `LocaleProvider`, `ChangeLanguage`, `*Option` settings types, `StaggerReveal`. Note the Orbit `ContainerHost` integration and the `expect`/`actual` platform glue boundary.
  - **What this is NOT**: not pure types (those live in `core`); not networking/persistence (those live in `data`); not UI primitives (those live in `primitives`/`composites`); not feature screens or business orchestration (those live in YallaClient).
  - **Usage**: a 5-10 line example showing a typical `BaseViewModel`-extending class with `LoadingController` + `ObserveAsEvents`; a `LocationManager`-via-Koin example.
  - **Notes**: anything surprising — the foundation/core `LocationProvider` decision (whichever way the gate went), the `LocationMappers` location decision, the `permission-API try/catch carve-out`, the iOS test-only `FakePermissionsController`, the kermit dep cascade from core's wave-3.
  - **Depends on**: from wave 3.

- [ ] **Step 2: Delete the old `# Package …` blocks + the "Architecture" header.**

- [ ] **Step 3: Commit.**
  ```
  docs(foundation): rewrite MODULE.md to phase-1 form

  Aligns with bom/, resources/, core/, data/, design/. Per criterion 5
  of CLEANUP_CRITERIA.md.
  ```

---

## Wave 11 — Phase-3-foundation summary + readiness for `primitives`

**Files:** none modified.

- [ ] **Step 1: Run the full SDK build one final time.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew compileKotlinIosSimulatorArm64
  ```

- [ ] **Step 2: Tally outputs.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git log --oneline cleanup/phase-2-3-4 --not main | head -60 && \
    git diff --stat main..HEAD -- foundation/ | tail -3 && \
    cat MIGRATION_LIST.md | head -150
  ```

- [ ] **Step 3: Tell Islom: "Phase-3 `foundation` cleanup complete. Summary: <stats>. `MIGRATION_LIST.md` now has <N> entries (was <M> before foundation). Ready to plan phase-3 `primitives`?"**

  No commit. The `primitives` plan is its own writing-plans run after Islom approves moving on.

---

## Self-review checklist (run before handoff)

- [x] Spec coverage — every CLEANUP_CRITERIA criterion 1-11 has at least one wave applying it.
- [x] No placeholders — every `<…>` is a runtime-fill variable explicitly explained.
- [x] Type/method consistency — file paths absolute, package names match, `FOUNDATION_AUDIT.md` referenced consistently.
- [x] Each wave has a verify step before commit.
- [x] Decision gate after wave 1; rewrite gate inside wave 5 (>100-line items).
- [x] Versioning policy honored — no alpha tag, commits stay on `cleanup/phase-2-3-4`.
- [x] Test bar from criterion 6 enforced in wave 8 + verified in wave 9.
- [x] Pre-existing iOS-linker failures on `:firebase` and `:maps` excluded from wave-9 verification with the documented rationale.
- [x] Architectural carve-outs from CLAUDE.md (Orbit `ContainerHost`, `internal object` mappers, permission-API try/catch sanctioned exception) are explicit checkpoints in wave 5.
- [x] The foundation/core `LocationProvider` duplication and `LocationMappers` location are explicit gate-decision questions in wave 1's audit prompt and wave 5's quality pass.

---

## Out of scope (do NOT do in this plan)

- Any other phase-3 module (`primitives`, `composites`).
- Any phase-4 module (`firebase`, `maps`, `media`, `platform`).
- Public API visibility tightening across the board (criterion 3 — alpha-deferred).
- Reintroducing `docs/` or `docs/adr/`.
- Publishing an alpha. Criterion 8 — single bump at end of phase 4.
- Rewriting `:maps` / `:firebase` iOS-link infrastructure. Pre-existing on main; not a cleanup concern.
- Touching the design module's `MotionScheme` consumer-pending status (G9 from phase-3 design). Foundation can adopt motion tokens later — outside this plan's scope.
