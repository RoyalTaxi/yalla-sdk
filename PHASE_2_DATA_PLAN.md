# Phase 2 — `data` Cleanup Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply [CLEANUP_CRITERIA.md](CLEANUP_CRITERIA.md) criteria 1-11 to the `data` module on the `cleanup/phase-2-3-4` branch, producing a leaner, idiomatic, well-documented infrastructure layer that downstream feature data modules can build on.

**Architecture:** Same two-stage flow as `PHASE_2_CORE_PLAN.md`. Stage A is wave 1's audit, producing `DATA_AUDIT.md`. A decision gate sits between A and B. Stage B runs the per-module work shape from criterion 9: delete → deps → restructure → quality → promote/demote → KDoc → tests → verify → MODULE.md. Phase-2 deferred items from `CORE_AUDIT.md` (identifier value classes per criterion 11) are picked up here at the DTO seam.

**Tech stack:** Kotlin Multiplatform (commonMain + androidMain + iosMain), Ktor (CIO/Android/Darwin engines, ContentNegotiation, kotlinx.serialization.json, Logging), DataStore Preferences, multiplatform-settings, Koin. Tests: JUnit 4 + `kotlin.test` + ktor-client-mock + multiplatform-settings-test + hand-written fakes.

**Inventory of `data` as of plan-time (46 files):**
- commonMain (25 production):
  - `api/` (3): `ApiResponse`, `ApiListResponse`, `ApiErrorResponse` — generic JSON envelopes.
  - `di/` (1): `DataModule` — Koin module.
  - `local/` (8): `DataStoreFactory`, `SettingsFactory`, `PreferenceKeys`, six `*PreferencesImpl` (Config, Interface, Position, Session, Static, User).
  - `network/` (5): `HttpClientFactory`, `HttpEngine`, `NetworkConfig`, `SafeApiCall`, `GuestModeGuard`.
  - `util/` (2): `IoDispatcher`, `Platform`.
- commonTest (15 production-grade tests): `api/ApiResponseTest`, `local/{ConfigPreferencesImpl, InterfacePreferencesImpl, PositionPreferencesImpl, SessionPreferencesImpl, StaticPreferencesImpl, UserPreferencesImpl, ParseGeoPoint, PreferenceKeys}Test` plus `InMemoryDataStore`, `network/{ApiResponse, GuestModeGuard, GuestModeGuardConfig, HttpClientFactoryIntegration, RetryWithBackoff, SafeApiCall, SafeApiCallIntegration}Test`.
- androidMain (5): `DataStoreFactory.android`, `SettingsFactory.android`, `HttpEngine.android`, `IoDispatcher.android`, `Platform.android`.
- iosMain (5): mirrored.
- `MODULE.md`: still old `# Package` style; phase-1 form rewrite is wave 10.
- `build.gradle.kts`: 11 `api` declarations — likely over-exposed; refining the api/implementation split is a wave-3 + wave-5 task.

**Carried forward from core's audit (per `CORE_AUDIT.md` §8 + decisions):**
- **Identifier value classes** (G3-deferred). Apply at the DTO mapping seam: typed `OrderId(val raw: Int)`, `CardId(val raw: String)`, etc. Added to wave 5 below.

---

## Pre-work — Branch and baseline

**Files:** none modified.

- [ ] **Step 1: Verify branch is `cleanup/phase-2-3-4`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && git rev-parse --abbrev-ref HEAD
  ```
  Expected: `cleanup/phase-2-3-4`. If not, `git checkout cleanup/phase-2-3-4`.

- [ ] **Step 2: Baseline-build `data`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :data:compileKotlinIosSimulatorArm64 :data:allTests --rerun-tasks
  ```
  Expected: BUILD SUCCESSFUL. Capture the test count (data should report ~111 from the wave-9 verification of phase-2 core).

- [ ] **Step 3: Record the baseline.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    grep -hE "tests=" data/build/test-results/iosSimulatorArm64Test/TEST-*.xml | \
    grep -oE 'tests="[0-9]+"' | grep -oE '[0-9]+' | \
    awk '{s+=$1} END {print "data tests:", s}'
  ```
  Note the number for wave 11's summary. Floor — wave 8 raises it.

---

## Wave 1 — Audit `data` (Stage A)

**Goal:** Produce `DATA_AUDIT.md` at repo root with concrete findings keyed to each criterion. Drives waves 2-10. Deletes at end of phase 4 with `CLEANUP_CRITERIA.md`.

**Files:**
- Create: `DATA_AUDIT.md` (repo root).

- [ ] **Step 1: Dispatch a fresh Opus subagent to audit `data` against criteria 1, 2, 4, 6, 9-3, 11.**

  Use the `Agent` tool with `subagent_type: general-purpose`, `model: opus`, `description: "Audit yalla-sdk data module"`, and the prompt below. Read-only — must not edit any source.

  Prompt to use verbatim:
  ```
  Audit /Users/islom/StudioProjects/yalla-sdk/data against the criteria
  in /Users/islom/StudioProjects/yalla-sdk/CLEANUP_CRITERIA.md and write
  the output to /Users/islom/StudioProjects/yalla-sdk/DATA_AUDIT.md.

  Read CLEANUP_CRITERIA.md and CORE_AUDIT.md first — every finding ties
  to a specific criterion, and CORE_AUDIT.md has format precedent.

  The audit MUST be structured into 8 sections:

  ## 1. AI-blob deletions (criterion 2)
  Per file in commonMain + androidMain + iosMain, list:
    - bucket 2-1 (paraphrase KDoc, ceremony banners, README cruft)
    - bucket 2-2 (comment redundancy)
    - bucket 2-3 (single-use abstractions — interface with one impl,
      helper called once, factory wrapping single constructor,
      delegate-everything wrapper)
    - bucket 2-4 (dead code — unused params, unreachable branches,
      orphan types, dead tests, expect declarations with no actual)
    - bucket 2-5 (speculative generalization — generics resolving to
      one type, config knobs with one default, dead `when` branches)
  For each finding: file path + line range + 1-sentence reason.

  Detection: for each `interface` in data, grep the SDK + (best-effort)
  YallaClient for implementations. One impl + not opened for extension
  by external consumers → flag bucket 2-3. For each public function,
  grep callers. Zero callers → flag bucket 2-4 unless it's an explicit
  public API entry point (factory, plugin, top-level helper).

  ## 2. Module dependency graph (criterion 4)
  - List every line in data/build.gradle.kts: `api()` vs
    `implementation()` declarations across commonMain / androidMain /
    iosMain / commonTest source sets.
  - For each `api()` declaration, verify whether it's actually exposed
    in any public type signature (return type, parameter type, supertype)
    in data/src/commonMain. Flag every `api()` that's used internally
    only — those should be `implementation()`.
  - For each declared dep, verify any usage at all. Flag unused.
  - Output a recommended "Depends on" block formatted like
    CORE_AUDIT.md §2.
  - Confirm no SDK-internal dep beyond `core` (data should depend only
    on core, plus third-party libs). Flag any unexpected dep.

  ## 3. Restructure candidates (criterion 9-3)
  - Run `wc -l` on every commonMain/androidMain/iosMain .kt file.
    List files >300 lines as god-file candidates. For each, count
    distinct top-level types/responsibilities; mark "split candidate"
    only when >5 distinct concerns or >300 lines.
  - Identify any nested package that exists for organization-only
    reasons (no `internal` visibility narrowing implied by the nesting).
  - Cross-cutting: the `local/` package has 8 files; consider whether
    the six `*PreferencesImpl` belong in a sub-package
    (`local/preferences/`) for visual separation — or whether the flat
    layout is fine. Flag preference, don't auto-decide.

  ## 4. Quality / rewrite candidates (criterion 11)
  Per file (focus on commonMain), check for:
    - Non-idiomatic Kotlin: callback chain, manual JSON, ArrayList /
      HashMap literals, `Any?` returns where a sealed type fits,
      String IDs that should be value classes (this is the deferred
      G3 item from CORE_AUDIT.md), String constants where enums fit.
    - Architecture violations per CLAUDE.md:
      - try/catch in business logic (data is allowed try/catch around
        network calls — that's its job — but flag try/catch around
        non-network logic).
      - Mappers as classes / DTO extension functions instead of
        `internal object` mappers.
      - Service classes named `Api`. (data has no Service classes yet,
        but flag if any DataModule wires a "Foo*Api" type; per
        CLAUDE.md the convention is `*Service`.)
      - `InMemoryTokenProvider`, manual `Authorization` header,
        `AuthEventBus`. Per CLAUDE.md the right approach is Ktor's
        `Auth` plugin (`loadTokens`/`refreshTokens`) + `SessionStore`
        + `SessionExpiredSignal` (which in this codebase is named
        `UnauthorizedSessionEvents`, kept per G4).
    - Untestable shape (god class with hard-wired deps; side-effecting
      constructors; statics that prevent fakes).
  For each finding: file path + line range + suggested target pattern
  from CLEANUP_CRITERIA criterion 11. Estimate line-impact.
  Mark "REWRITE >100 LINES — NEEDS GATE" prominently for items
  requiring Islom's approval.

  Apply the deferred core-G3 work specifically here. List every
  `id: Int` / `cardId: String` / etc. in data DTOs that crosses the
  data → domain seam, and propose value-class targets. Estimate the
  total line impact (will likely be the gate item this phase).

  ## 5. Promote/demote candidates (criterion 1)
  Apply the lego test to every public type in data/src/commonMain.
  data is supposed to be infrastructure (HTTP, DataStore, response
  envelopes). Demotions are unlikely; promotions even less. But
  check for:
    - Types / functions that look like domain types
      (RideStatus-shaped) accidentally living in data.
    - Hardcoded business rules / product copy / Russian or Uzbek
      strings.
    - Things that look like a wired-up assembly rather than a brick.
  For each finding: file path + brief reason + suggested destination
  (core, YallaClient, or "borderline — Islom decides").

  ## 6. Missing tests (criterion 6)
  data already has 15 test files. Inventory:
  - For each public function in commonMain, check if a corresponding
    test exists. List functions with no test.
  - For each `*PreferencesImpl`, confirm the existing impl test
    covers: round-trip per property, default value, clear semantics.
  - For HttpClientFactory: is there a test asserting the Auth plugin
    wires `loadTokens` and `refreshTokens` correctly?
  - For SafeApiCall: is every `DataError.Network.*` mapping path
    tested? Cross-check against the existing `SafeApiCallTest`.
  - For androidMain / iosMain expect-actual splits: which
    actual implementations are tested? Note that platform-specific
    behavior is often hard to test in commonTest; note
    untestable platform code separately from "missing test" gaps.
  - Group findings by package; estimate effort per package.

  ## 7. MODULE.md staleness (criterion 5)
  Compare current data/MODULE.md against phase-1 form. Reference docs:
  - /Users/islom/StudioProjects/yalla-sdk/bom/MODULE.md
  - /Users/islom/StudioProjects/yalla-sdk/resources/MODULE.md
  - /Users/islom/StudioProjects/yalla-sdk/core/MODULE.md (post-cleanup;
    this is the most recent reference since it's also a "common
    package" module)
  List sections to add/remove/rewrite for the wave-10 rewrite.

  ## 8. Reviewer notes
  After completing sections 1-7, look at your findings and add:
  - Any pushback you'd give Islom on specific findings.
  - Any cross-cutting patterns (e.g., "every *PreferencesImpl shares
    the same Flow.distinctUntilChanged → Settings.put pattern; could
    extract a base").
  - Concerns with the criteria as applied to data (e.g., "criterion 6's
    state-machine bar doesn't apply — data has no Orbit ContainerHosts").

  Output requirements:
  - Every finding has an absolute path under
    /Users/islom/StudioProjects/yalla-sdk/.
  - Line ranges where applicable.
  - No editorial commentary outside the structured findings.
  - Plain markdown, no frontmatter.
  - Estimated time investment per finding ("~5 min", "~30 min", "~2h").

  Do NOT modify any source files. Output is ONE file:
  /Users/islom/StudioProjects/yalla-sdk/DATA_AUDIT.md.

  When done, report: total finding count per section, the longest
  single rewrite candidate (lines), and any blocking issues.
  ```

- [ ] **Step 2: Verify `DATA_AUDIT.md` was created and has 8 sections.**

  Run:
  ```bash
  ls -la /Users/islom/StudioProjects/yalla-sdk/DATA_AUDIT.md && \
    grep -c "^## [0-9]" /Users/islom/StudioProjects/yalla-sdk/DATA_AUDIT.md
  ```
  Expected: file exists, `grep -c` returns `8`. If <8, prompt the agent to fill in missing sections.

- [ ] **Step 3: Sanity-skim findings.**

  Read the audit. Look for: any rewrite >100-lines candidates (especially the value-class identifier rollout — it crosses 100 lines almost by definition and needs an explicit gate decision), any obvious AI-blob false positives, any architectural pushbacks (e.g., the `api()` vs `implementation()` resolution may surface real DI-via-api needs that the audit shouldn't blanket-flag).

- [ ] **Step 4: Commit the audit.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add DATA_AUDIT.md && \
    git commit -m "$(cat <<'EOF'
  docs(data): add DATA_AUDIT.md — phase-2 inventory

  Output of wave 1 of PHASE_2_DATA_PLAN.md. Drives waves 2-10. Deleted
  at the end of phase 4 with CLEANUP_CRITERIA.md.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
  EOF
  )"
  ```

---

## Decision gate — Islom reviews `DATA_AUDIT.md`

**Files:** none modified.

- [ ] **Step 1: Stop. Tell Islom: "Wave 1 done. `DATA_AUDIT.md` committed as `<sha>`. Review it and tell me which findings to apply, which to skip, and any rewrite-gate decisions for >100-line items (the value-class identifier rollout is the expected gate item this phase)."**

- [ ] **Step 2: Wait for Islom's response.** As with core: bulk approval, selective approval, or rejection with reasons.

- [ ] **Step 3: Append `DATA_AUDIT.md` with `## 9. Approval` listing what was approved/rejected/deferred.** Commit:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add DATA_AUDIT.md && \
    git commit -m "docs(data): record wave-1 audit decisions"
  ```

---

## Wave 2 — Delete bloat (criterion 2)

**Files:** every file flagged in `DATA_AUDIT.md` section 1.

Same shape as `PHASE_2_CORE_PLAN.md` wave 2: split the deletions across logical commits (5-7 fixes each) and compile + test between commits.

- [ ] **Step 1: `@since` sweep.** Run the same Python script used in core's wave 2 (commit `331c47bc7`) but pointed at `data/src/commonMain`, `data/src/androidMain`, `data/src/iosMain`. The script is in the commit history; copy it from the git log of `331c47bc7`. Run it, verify the output, compile + test, commit.

  Use this commit message template:
  ```
  docs(data): drop @since tags across module

  Removed N @since lines from M files. Alpha versioning means no
  consumer tracks them; they're noise per criterion 2-1 of
  CLEANUP_CRITERIA.md.

  Tests: <baseline> passing (unchanged).
  ```

- [ ] **Step 2: Paraphrase KDoc sweep.** Dispatch a Sonnet subagent with the same prompt structure as in core's wave 2 (the "Strip paraphrase KDoc in core" agent), but pointed at `data/`'s file list from `DATA_AUDIT.md` §1. Apply rules:
  - Delete property paraphrase, ceremony banners, redundant getter/setter doc.
  - Keep usage blocks, threading rules, side effects, error semantics, units, ranges, format examples.
  - Preserve any KDoc that distinguishes a type from a sibling.
  Compile + test. Commit:
  ```
  docs(data): strip paraphrase KDoc per criterion 2-1
  ```

- [ ] **Step 3: Delete bucket 2-3 (single-use abstractions).** For each interface flagged with one impl in `DATA_AUDIT.md`, replace consumers' references with the concrete type and delete the interface. For each helper called once, inline. Compile + test after each deletion (single-use abstractions can have unexpected ripples). Commit per logical change. Use `refactor!:` prefix if a public type is deleted.

- [ ] **Step 4: Delete bucket 2-4 (dead code).** Remove unused parameters (cascade through callers), unreachable branches, orphan types. Pay special attention to `expect`/`actual` — an `expect` with no actual implementations is dead; the audit should flag these. Compile + test. Commit.

- [ ] **Step 5: Delete bucket 2-5 (speculative generalization).** Replace generics with concrete types where audit found one resolution. Remove unused config knobs (e.g., `NetworkConfig` fields that no consumer reads). Collapse `when` exhaustiveness over branches that don't matter. Compile + test. Commit.

- [ ] **Step 6: Final wave-2 verification.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :data:compileKotlinIosSimulatorArm64 :data:allTests
  ```
  All green.

---

## Wave 3 — Module dependency rules (criterion 4)

**Files:**
- Modify: `data/build.gradle.kts` — apply audit §2 deltas. The big targets:
  - Demote `api(...)` → `implementation(...)` for any third-party lib not exposed in a public commonMain signature.
  - Drop any unused `api()` or `implementation()` declaration.
- Modify: `data/MODULE.md` — append a "Depends on" block (full rewrite is wave 10).

- [ ] **Step 1: Apply audit §2 deltas to `data/build.gradle.kts`.** For each `api(...)` flagged as internal-only, change to `implementation(...)`. For each unused dep, delete the line. Verify each removal with a grep before committing.

- [ ] **Step 2: Append the "Depends on" block to `data/MODULE.md`** matching the audit §2 recommended block. Wave 10 will fold it into the phase-1 form.

- [ ] **Step 3: Compile every downstream module** (data is a leaf in core's brick stack; data's downstream is `foundation`, `primitives`, `composites`, `firebase`, `maps`, `media`, `platform` — anything that uses HTTP / preferences). The audit's `api`-demotions may force these to declare the dep explicitly:

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :data:compileKotlinIosSimulatorArm64 \
              :foundation:compileKotlinIosSimulatorArm64 \
              :primitives:compileKotlinIosSimulatorArm64 \
              :composites:compileKotlinIosSimulatorArm64 \
              :firebase:compileKotlinIosSimulatorArm64 \
              :maps:compileKotlinIosSimulatorArm64 \
              :media:compileKotlinIosSimulatorArm64 \
              :platform:compileKotlinIosSimulatorArm64
  ```
  Expected: BUILD SUCCESSFUL.

  If a downstream fails because it relied on data's transitive `api()`, add the explicit `implementation(...)` to that downstream's `build.gradle.kts` (consequence-fix, same pattern used in core's wave 3 for `kermit` → foundation). Document in commit body.

- [ ] **Step 4: Run tests.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :core:allTests :data:allTests :foundation:allTests
  ```
  All green.

- [ ] **Step 5: Commit.**
  ```bash
  git add data/build.gradle.kts data/MODULE.md \
          <any-downstream-build-files> && \
    git commit -m "$(cat <<'EOF'
  refactor(data): tighten api/implementation split, drop unused deps

  Per criterion 4 of CLEANUP_CRITERIA.md. Demoted N api() declarations
  to implementation() (used internally only); dropped M unused deps.
  Cascaded explicit deps to <downstream-modules> where they relied on
  the transitive api() resolution.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
  EOF
  )"
  ```

---

## Wave 4 — Restructure (criterion 9-3)

**Files:**
- Per `DATA_AUDIT.md` §3 findings.
- Possible: split `local/*PreferencesImpl.kt` into `local/preferences/*PreferencesImpl.kt` if the audit recommends it AND Islom approves.
- God-file splits if any are flagged.

- [ ] **Step 1: For each restructure approved at the gate, do the move.** Use `git mv` so history is preserved.

  Example for the conditional preferences flatten/expand:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    mkdir -p data/src/commonMain/kotlin/uz/yalla/data/local/preferences && \
    git mv data/src/commonMain/kotlin/uz/yalla/data/local/{Config,Interface,Position,Session,Static,User}PreferencesImpl.kt \
           data/src/commonMain/kotlin/uz/yalla/data/local/preferences/
  ```
  Only if the audit flags this AND Islom approves. Skip otherwise.

- [ ] **Step 2: For each moved file, update the package declaration** (e.g., `package uz.yalla.data.local` → `package uz.yalla.data.local.preferences`). Use `Edit` per file or sed across the moved set:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    for f in data/src/commonMain/kotlin/uz/yalla/data/local/preferences/*PreferencesImpl.kt; do
      sed -i '' 's|^package uz\.yalla\.data\.local$|package uz.yalla.data.local.preferences|' "$f"
    done
  ```

- [ ] **Step 3: Update import sites.** Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    grep -rln "uz\.yalla\.data\.local\.\(Config\|Interface\|Position\|Session\|Static\|User\)PreferencesImpl" --include="*.kt"
  ```
  For each file in the output, sed-rewrite:
  ```bash
  sed -i '' 's|uz\.yalla\.data\.local\.\([A-Z][a-z]*PreferencesImpl\)|uz.yalla.data.local.preferences.\1|g' "$f"
  ```

- [ ] **Step 4: Apply other restructure findings.** Each split = its own commit with compile + test cycle.

- [ ] **Step 5: Compile entire SDK** (other modules likely import data types):
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :core:compileKotlinIosSimulatorArm64 \
              :data:compileKotlinIosSimulatorArm64 \
              :foundation:compileKotlinIosSimulatorArm64 \
              :primitives:compileKotlinIosSimulatorArm64 \
              :composites:compileKotlinIosSimulatorArm64 \
              :design:compileKotlinIosSimulatorArm64 \
              :media:compileKotlinIosSimulatorArm64 \
              :platform:compileKotlinIosSimulatorArm64 \
              :firebase:compileKotlinIosSimulatorArm64 \
              :maps:compileKotlinIosSimulatorArm64
  ```

- [ ] **Step 6: Run tests.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :data:allTests :foundation:allTests
  ```

- [ ] **Step 7: Commit per logical restructure.** Use `refactor!:` prefix because import paths change.

---

## Wave 5 — Quality pass (criterion 11)

**Files:** every file flagged in `DATA_AUDIT.md` §4. The expected gate item this phase is the **identifier value-class rollout** carried over from core's G3 deferral.

- [ ] **Step 1: Triage rewrite candidates.** For each entry in audit §4:
  - **Small** (<100 lines, no API shape change): apply directly.
  - **>100 lines OR public-shape change**: write a one-paragraph rationale, present to Islom, wait for approval before applying.

  The value-class rollout will almost certainly be >100 lines (audit's pre-estimate was 200-400). It needs:
  - `OrderId`, `ExecutorId`, `CardId`, `AddressId`, `ServiceBrandId`, etc. — value classes added to **`core`** (not data — value classes are domain primitives, brick layer). Touches `core/order/Order.kt`, `core/order/Executor.kt`, `core/payment/PaymentCard.kt`, etc.
  - DTO mapping in `data` updated to wrap raw `Int`/`String` into the value classes at the deserialization boundary.
  - All consumers in `core`, `foundation`, downstream — updated.

  Submit the rationale, wait for approval. The rationale should also list the rejection alternative ("keep raw types for the alpha; revisit at 1.0").

- [ ] **Step 2: Apply approved rewrites.** Behavior preserved — only internals reshape (with the exception of the value classes, which DO change the public type signature, hence `refactor!:`).

  For the value-class rollout (assuming approved):
  - Add value classes to `core/<package>/` files alongside existing types. Each value class has `@JvmInline value class FooId(val raw: Int)` (or `String`) plus `@Serializable` if it crosses the wire.
  - Update DTO mapping at the data → core seam to wrap.
  - Update every consumer.
  - Tests for each value class (criterion 6).

  Compile + test after each rewrite.

  Commit per rewrite. Use `refactor!:` if the public signature changes. Each rewrite is its own commit so it can be reverted independently.

- [ ] **Step 3: Run full SDK build to catch downstream breakage.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew compileKotlinIosSimulatorArm64
  ```

---

## Wave 6 — Promote/demote flags (criterion 1)

**Files:**
- Modify: `MIGRATION_LIST.md` — append a `## To promote into core / data` and `## To demote from core / data` section header per audit §5.

- [ ] **Step 1: Open `DATA_AUDIT.md` §5.** For each entry approved:
  - **Promotion** — append to `MIGRATION_LIST.md` "To promote into the SDK".
  - **Demotion** — append to "To demote from the SDK".
  - **Borderline** — append to "To decide".

- [ ] **Step 2: Append breaking changes** (every `refactor!:` commit on `cleanup/phase-2-3-4` since `MIGRATION_LIST.md` was last updated in core's wave 6) to the "Breaking changes shipped to SDK alpha" section.

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git log --grep="^refactor!" --format="%h %s" \
            $(git log --format="%H" -1 -- MIGRATION_LIST.md)..HEAD
  ```
  to list what's new since the last MIGRATION_LIST commit.

- [ ] **Step 3: Commit.**
  ```bash
  git add MIGRATION_LIST.md && \
    git commit -m "$(cat <<'EOF'
  docs(data): append data-phase entries to MIGRATION_LIST.md

  Per criterion 1 + 8 of CLEANUP_CRITERIA.md. <N> breaking changes,
  <P> promotions, <D> demotions, <B> borderline.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
  EOF
  )"
  ```

---

## Wave 7 — KDoc bar (criterion 5)

**Files:** every public symbol in `data/src/commonMain` with missing or paraphrase-only KDoc after wave 2's sweep.

- [ ] **Step 1: Dispatch a fresh Sonnet subagent to identify gaps.** Same prompt structure as core's wave 7. Output a list of files + line numbers + suggested KDoc text.

- [ ] **Step 2: Apply the suggestions** with `Edit`. Each suggestion is a small targeted edit. Compile + test after every ~5 files.

- [ ] **Step 3: Commit.**
  ```bash
  git add data/src/commonMain && \
    git commit -m "docs(data): fill KDoc gaps surfaced by criterion-5 audit

  <list affected packages>. Per criterion 5 of CLEANUP_CRITERIA.md.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
  ```

---

## Wave 8 — Test backfill (criterion 6)

**Files:** new test files under `data/src/commonTest/kotlin/uz/yalla/data/<package>/` for each gap from `DATA_AUDIT.md` §6.

Existing coverage is decent (15 test files). Likely gaps:
- Value-class round-trip tests (if wave 5 added them).
- Auth-plugin token-refresh path in `HttpClientFactory` if not yet tested.
- Any `*PreferencesImpl` paths the existing impl tests don't reach (corner cases for `clearSession`, GeoPoint serialization edge cases — `ParseGeoPointTest` exists, verify scope).

- [ ] **Step 1: For each gap, write a failing test first.**

  TDD discipline: the test asserts the expected behavior; if it fails for the wrong reason, the implementation work goes alongside the test.

  Example for an Auth-plugin refresh-tokens test:
  ```kotlin
  // data/src/commonTest/kotlin/uz/yalla/data/network/HttpClientFactoryAuthTest.kt
  package uz.yalla.data.network

  import io.ktor.client.engine.mock.MockEngine
  import io.ktor.client.engine.mock.respond
  import io.ktor.http.HttpStatusCode
  import io.ktor.http.headersOf
  import kotlin.test.Test
  import kotlin.test.assertEquals
  import kotlinx.coroutines.test.runTest

  class HttpClientFactoryAuthTest {
      @Test
      fun shouldAttachBearerTokenWhenTokenAvailable() = runTest {
          // arrange: a SessionStore with a known token, a MockEngine
          // act: any request through the client
          // assert: the request includes Authorization: Bearer <token>
          // ... implementation depends on the SessionStore API surface
      }
  }
  ```
  Tailor to the actual API surface in `HttpClientFactory.kt` — read it first.

- [ ] **Step 2: Run the test, confirm it fails for the right reason.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :data:allTests --tests "uz.yalla.data.network.HttpClientFactoryAuthTest"
  ```

- [ ] **Step 3: Run the full data test suite. Confirm count rose.**

- [ ] **Step 4: Commit per package.** Don't batch — each gap's tests get their own commit.

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

  Skip `:firebase:linkDebugTestIosSimulatorArm64` and `:maps:linkDebugTestIosSimulatorArm64` — those are pre-existing iOS-linker infrastructure failures on `main`, NOT regressions from this branch (verified during core's wave 9). Run the rest:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :core:allTests :data:allTests :foundation:allTests \
              :design:allTests :primitives:allTests \
              :composites:allTests :media:allTests :platform:allTests
  ```

- [ ] **Step 3: Tally per-module test counts.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    for m in core foundation data design primitives composites media platform; do
      count=$(grep -hE "tests=" $m/build/test-results/iosSimulatorArm64Test/TEST-*.xml 2>/dev/null | grep -oE 'tests="[0-9]+"' | grep -oE '[0-9]+' | awk '{s+=$1} END {print s+0}')
      fails=$(grep -hE "tests=" $m/build/test-results/iosSimulatorArm64Test/TEST-*.xml 2>/dev/null | grep -oE 'failures="[0-9]+"' | grep -oE '[0-9]+' | awk '{s+=$1} END {print s+0}')
      printf "%-15s tests=%4d failures=%d\n" "$m" "$count" "$fails"
    done
  ```
  Expected: zero failures everywhere; data count > baseline (Pre-work step 3).

---

## Wave 10 — MODULE.md to phase-1 form

**Files:**
- Modify: `data/MODULE.md`.

Reference: `core/MODULE.md` (post-cleanup, on the same branch — committed in `ebf30bc6a`) — that's the current canonical example for a "common-package" module.

- [ ] **Step 1: Rewrite `data/MODULE.md`** to:
  ```
  # Module data
  > One-line tagline.

  ## What this is
  ## What this is NOT
  ## Usage
  ## Notes
  ## Depends on
  ```

  - **Tagline** suggestion: `> Network, local persistence, and HTTP plumbing for the SDK.`
  - **What this is**: enumerate the api envelopes (`ApiResponse`, `ApiListResponse`, `ApiErrorResponse`), the `*PreferencesImpl` set, `DataStoreFactory`, `SettingsFactory`, `HttpClientFactory`, `SafeApiCall`, `GuestModeGuard`, `IoDispatcher`, `Platform`, `DataModule` Koin module.
  - **What this is NOT**: feature-specific business logic; mappers for feature DTOs (those live in feature-specific data modules built on this); Service classes (none yet, will be added in feature data modules); UI; platform-specific business logic — only HTTP/storage primitives' platform glue.
  - **Usage**: minimal Koin wiring example; one-line `safeApiCall` example.
  - **Notes**: anything surprising — Auth plugin's interaction with `UnauthorizedSessionEvents`, the multiplatform `expect/actual` IoDispatcher convention, the Settings vs DataStore split (if there is one — verify).
  - **Depends on**: from wave 3.

- [ ] **Step 2: Delete the old `# Package …` blocks** if any remain after wave 3's interim append.

- [ ] **Step 3: Commit.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add data/MODULE.md && \
    git commit -m "docs(data): rewrite MODULE.md to phase-1 form

  Aligns with bom/, resources/, core/. Per criterion 5 of
  CLEANUP_CRITERIA.md.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
  ```

---

## Wave 11 — Phase-2-data summary + readiness for phase 3

**Files:** none modified.

- [ ] **Step 1: Run the full SDK build one final time.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew compileKotlinIosSimulatorArm64
  ```

- [ ] **Step 2: Tally outputs.** From the wave commits:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git log --oneline cleanup/phase-2-3-4 --not main | head -40 && \
    git diff --stat main..HEAD -- data/ core/ foundation/ | tail -3 && \
    cat MIGRATION_LIST.md | head -100
  ```

- [ ] **Step 3: Tell Islom: "Phase-2 `data` cleanup complete. Summary: <stats>. `MIGRATION_LIST.md` now has <N> entries (was <M> after core). Phase 2 (core + data) is done. Ready to plan phase-3 (design, foundation, primitives, composites)?"**

  No commit. Phase-3 plans are written one at a time as each phase-3 module starts.

---

## Self-review checklist (run before handoff)

- [x] Spec coverage — every CLEANUP_CRITERIA criterion 1-11 has at least one wave applying it; the carried-over G3 (value-class identifiers) is captured in wave 5.
- [x] No placeholders — every `<…>` is a runtime-fill variable explicitly explained.
- [x] Type/method consistency — file paths absolute, package names match, `DATA_AUDIT.md` referenced consistently.
- [x] Each wave has a verify step before commit.
- [x] Decision gate after wave 1; rewrite gate inside wave 5 (>100-line value-class rollout).
- [x] Versioning policy honored — no alpha tag, commits stay on `cleanup/phase-2-3-4`.
- [x] Test bar from criterion 6 enforced in wave 8 + verified in wave 9.
- [x] Pre-existing iOS-linker failures on `:firebase` and `:maps` are explicitly excluded from wave-9 verification with the rationale (verified pre-existing during core's execution).

---

## Out of scope (do NOT do in this plan)

- Any phase-3 module (`design`, `foundation`, `primitives`, `composites`).
- Any phase-4 module (`firebase`, `maps`, `media`, `platform`).
- Public API visibility tightening (criterion 3 — left alone in alpha).
- Reintroducing `docs/` or `docs/adr/`.
- Publishing an alpha. Criterion 8 — single bump at end of phase 4.
- Rewriting `:maps` / `:firebase` iOS-link infrastructure. Pre-existing on main; not a cleanup concern.
