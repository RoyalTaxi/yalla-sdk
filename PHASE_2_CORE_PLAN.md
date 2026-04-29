# Phase 2 — `core` Cleanup Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply [CLEANUP_CRITERIA.md](CLEANUP_CRITERIA.md) criteria 1-11 to the `core` module on the `cleanup/phase-2-3-4` branch. Output: a leaner, idiomatic, testable, well-documented `core` ready for `data` to be cleaned against.

**Architecture:** Two-stage flow per the criteria-doc handshake. **Stage A** is a single audit wave that produces `CORE_AUDIT.md`, a structured inventory used by every later wave. **Stage B** runs the per-module work shape from criterion 9 against that inventory: delete → deps → restructure → quality → promote/demote flag → KDoc → tests → verify → MODULE.md. A decision gate sits between A and B; Islom reviews the audit, approves actions, and only then does Stage B run.

**Tech stack:** Kotlin Multiplatform, kotlinx.coroutines, kotlinx.serialization, kotlinx.datetime, kermit. Tests: JUnit 4 + `kotlin.test` + Turbine. Hand-written fakes only.

**Inventory of `core` as of plan-time (54 files):**
- Production (commonMain): 30 files across `contract/location`, `error`, `geo`, `location`, `order`, `payment`, `preferences`, `profile`, `result`, `session`, `settings`, `util`.
- Tests (commonTest): 19 files across `geo`, `location`, `order`, `payment`, `profile`, `result`, `session`, `settings`, `util`. **No tests for `error`, `preferences`, `contract/location`.**
- Phase-1 left an orphan: `contract/location/` was not flattened when `contract/preferences/* → preferences/*` was. Flattening it is a known wave-4 task.
- `MODULE.md` is in the older `# Package …` style; phase-1 form is `What this is / What this is NOT / Usage / Notes / Depends on`. MODULE.md rewrite is wave 10.

---

## Pre-work — Branch and baseline

**Files:** none modified.

- [ ] **Step 1: Verify branch is `cleanup/phase-2-3-4`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && git rev-parse --abbrev-ref HEAD
  ```
  Expected: `cleanup/phase-2-3-4`. If not, `git checkout cleanup/phase-2-3-4`.

- [ ] **Step 2: Baseline-build `core`.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew :core:compileKotlinMetadata :core:allTests --rerun-tasks
  ```
  Expected: BUILD SUCCESSFUL. All existing tests pass. Capture the test count from output for later comparison.

- [ ] **Step 3: Record the baseline test count.**

  Add a one-line note to the working scratch (commit message of wave 11): "Baseline core tests on `cleanup/phase-2-3-4`: N passing." This is the floor — wave 8 raises it.

---

## Wave 1 — Audit `core` (Stage A)

**Goal:** Produce `CORE_AUDIT.md` at repo root with concrete findings keyed to each criterion. The audit drives every later wave; nothing destructive happens until Islom approves the audit (decision gate after this wave).

**Files:**
- Create: `CORE_AUDIT.md` (repo root, deleted at end of phase 4 with `CLEANUP_CRITERIA.md`).

- [ ] **Step 1: Dispatch a fresh Opus subagent to audit `core` against criteria 1, 2, 4, 6, 9-3, 11.**

  Use the `Agent` tool with `subagent_type: general-purpose`, `model: opus`, `description: "Audit yalla-sdk core module"`, and the prompt below. The subagent is read-only — it must not edit files.

  Prompt to use verbatim:
  ```
  Audit /Users/islom/StudioProjects/yalla-sdk/core against the criteria in
  /Users/islom/StudioProjects/yalla-sdk/CLEANUP_CRITERIA.md and write the
  output to /Users/islom/StudioProjects/yalla-sdk/CORE_AUDIT.md.

  The audit MUST be structured into the following sections, in this order:

  ## 1. AI-blob deletions (criterion 2)
  Per file, list:
    - bucket 2-1 (docs/config bloat — KDoc paraphrasing signatures, banner comments)
    - bucket 2-2 (comment redundancy)
    - bucket 2-3 (single-use abstractions — interface with one impl, helper called once,
      factory wrapping single constructor, delegate-everything wrapper)
    - bucket 2-4 (dead code — unused params, unreachable branches, orphan types,
      tests for behavior that no longer exists)
    - bucket 2-5 (speculative generalization — generics resolving to one type,
      config knobs with one default, dead `when` branches)
  For each finding: file path + line range + 1-sentence reason.

  ## 2. Module dependency graph (criterion 4)
  - Read core/build.gradle.kts and list each `implementation`, `api`, `compileOnly`
    declaration.
  - Identify whether any of those are unused (no import in commonMain references them).
  - If you find a same-phase or upward-phase dep (other SDK modules), flag it.
  - Output: a "Depends on" block for MODULE.md and a list of removable deps.

  ## 3. Restructure candidates (criterion 9-3)
  - Files in `core/contract/location/*` (orphan from phase 1's flatten — confirm and list).
  - Any other nested package that exists for organization-only reasons (no `internal`
    visibility narrowing).
  - God files: list every file >300 lines or >5 distinct responsibilities (responsibility
    = top-level type or top-level function group).

  ## 4. Quality / rewrite candidates (criterion 11)
  Per file, flag any of:
    - non-idiomatic Kotlin (callback chain, manual JSON, ArrayList literal, Any? where
      a sealed type fits, String IDs that should be value classes)
    - architecture violations (try/catch in business logic, mapper as class, service
      named Api, custom MVI, Arrow types, manual Authorization headers)
    - untestable shape that blocks criterion 6 (god class with hard-wired deps)
  For each finding: file path + line range + suggested target pattern from
  CLEANUP_CRITERIA criterion 11. Mark finding as "rewrite >100 lines" if applicable
  (those need a gate per criterion 11).

  ## 5. Promote/demote candidates (criterion 1)
  Apply the lego test to every public type:
    - "Brick that snaps together" → stays in core (SDK).
    - "Wired-up assembly" → flag for demotion to YallaClient.
    - Borderline → flag with one-sentence rationale, no decision.
  Look in particular for: hardcoded Ildam product copy (Russian/Uzbek strings), business
  rules ("if user is type X then Y"), screen-shaped types.
  Note: `core` is logic atoms, so demotions are unlikely but not impossible.

  ## 6. Missing tests (criterion 6)
  For each public function in commonMain, check if a corresponding test exists in
  commonTest. List functions with NO test. List `Either.Failure` / sealed-error variants
  not exercised. List Orbit ContainerHost intent → state-transition gaps (none expected
  in core, but verify).

  Known gap from plan-time inventory: `error/`, `preferences/`, `contract/location/`
  packages have no test directories. Confirm and list specific symbols.

  ## 7. MODULE.md staleness (criterion 5)
  - Compare current core/MODULE.md against phase-1 form (see bom/MODULE.md and
    resources/MODULE.md as reference).
  - List sections to add/remove/rewrite.

  Output requirements:
  - Every finding has a file path. Use absolute paths from /Users/islom/StudioProjects/yalla-sdk/.
  - Line ranges where applicable.
  - No editorial commentary outside the structured findings.
  - Plain markdown, no frontmatter.
  - Estimated time investment per finding ("~5 min", "~30 min", "~2h") so wave
    sequencing has rough budgets.

  Do NOT modify any source files. Output is one file: CORE_AUDIT.md.
  ```

- [ ] **Step 2: Verify `CORE_AUDIT.md` was created and has all 7 sections.**

  Run:
  ```bash
  ls -la /Users/islom/StudioProjects/yalla-sdk/CORE_AUDIT.md && \
    grep -c "^## " /Users/islom/StudioProjects/yalla-sdk/CORE_AUDIT.md
  ```
  Expected: file exists, `grep -c` returns `7` (or higher if subsections used). If <7, prompt the agent to fill in missing sections.

- [ ] **Step 3: Sanity-skim findings.**

  Read the audit. Look for: any "rewrite >100 lines" candidates (these need a gate), any unexpected promote/demote flags, any obvious AI-blob false positives that don't match criterion 2's five buckets. Add a `## 8. Reviewer notes` section at the bottom of `CORE_AUDIT.md` with anything you'd push back on before Islom reviews.

- [ ] **Step 4: Commit the audit.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add CORE_AUDIT.md && \
    git commit -m "$(cat <<'EOF'
  docs(core): add CORE_AUDIT.md — phase-2 inventory

  Output of wave 1 of PHASE_2_CORE_PLAN.md. Drives waves 2-10. Deleted
  at the end of phase 4 with CLEANUP_CRITERIA.md.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
  EOF
  )"
  ```

---

## Decision gate — Islom reviews `CORE_AUDIT.md`

**Files:** none modified.

- [ ] **Step 1: Stop. Tell Islom: "Wave 1 done. `CORE_AUDIT.md` committed as `<sha>`. Review it and tell me which findings to apply, which to skip, and any rewrite-gate decisions for >100-line items."**

- [ ] **Step 2: Wait for Islom's response.** He will either:
  - Approve in bulk ("apply all"), in which case waves 2-10 run as written.
  - Approve selectively (line-by-line in the audit), in which case waves 2-10 skip the rejected items.
  - Reject specific findings with reasons, in which case the agent updates `CORE_AUDIT.md` to reflect the rejection (commit a follow-up).

- [ ] **Step 3: Append `CORE_AUDIT.md` with a `## 9. Approval` section listing what was approved/rejected/deferred.** Commit:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add CORE_AUDIT.md && \
    git commit -m "docs(core): record wave-1 audit decisions"
  ```

---

## Wave 2 — Delete bloat (criterion 2)

**Files:** every file flagged in `CORE_AUDIT.md` section 1 ("AI-blob deletions"). Exact paths come from the audit; no list pre-baked here.

- [ ] **Step 1: Open `CORE_AUDIT.md` section 1.** Work through every approved finding from the decision gate.

- [ ] **Step 2: Delete bucket 2-1 (docs/config bloat).** Remove KDoc that paraphrases signatures, banner comments, copy-paste comments. Where a public symbol's only KDoc was a paraphrase, leave the symbol KDoc-less for now — wave 7 fills it back in with a real one.

- [ ] **Step 3: Delete bucket 2-2 (comment redundancy).** Same approach.

- [ ] **Step 4: Delete bucket 2-3 (single-use abstractions).** For each interface with one impl, replace consumers' references with the concrete type and delete the interface file. For each helper called once, inline it. For each delegate-everything wrapper, replace with the wrapped type directly.

- [ ] **Step 5: Delete bucket 2-4 (dead code).** Remove unused parameters (cascade to callers — every call site updates), unreachable branches, orphan types, dead tests.

- [ ] **Step 6: Delete bucket 2-5 (speculative generalization).** Replace generics with concrete types where audit found one resolution. Remove unused config knobs. Collapse `when` exhaustiveness over branches that don't matter.

- [ ] **Step 7: Compile core.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew :core:compileKotlinMetadata
  ```
  Expected: BUILD SUCCESSFUL. If failures: read the compile error, fix, re-run.

- [ ] **Step 8: Run core tests.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew :core:allTests
  ```
  Expected: all existing tests still pass. If a test fails because the test was for behavior that no longer exists (covered in bucket 2-4), the test should have been deleted in step 5 — fix the wave-2 omission, don't loosen the test.

- [ ] **Step 9: Commit.**

  Stage only the files changed in this wave:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add core/src && \
    git commit -m "$(cat <<'EOF'
  refactor!(core): delete AI-blob per CORE_AUDIT.md section 1

  Removes: KDoc paraphrasing signatures, redundant comments, single-use
  abstractions, dead code, speculative generalization. Per criterion 2.

  - <list each bucket and a count>
  - Tests: <baseline N> still passing.

  Breaking: <only if a public type was deleted; list each>.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
  EOF
  )"
  ```

  If the wave produced more than 5-7 logical fixes, split into multiple commits along bucket boundaries (one commit for 2-1+2-2 docs, one for 2-3 abstractions, one for 2-4+2-5 dead/speculative). Compile + test between commits.

---

## Wave 3 — Module dependency rules (criterion 4)

**Files:**
- Modify: `core/build.gradle.kts` (remove unused deps per audit section 2).
- Modify: `core/MODULE.md` (add "Depends on" section — final-form rewrite is wave 10, this wave just drops the section in).

- [ ] **Step 1: Apply audit section 2 deltas to `core/build.gradle.kts`.** Remove any flagged unused dependency. Verify each one really is unused (`grep -r "kermit" core/src/commonMain/` etc.) before deleting.

- [ ] **Step 2: Append a "Depends on" block to `core/MODULE.md`** with the recovered DAG (e.g., "kotlinx.coroutines.core, kotlinx.serialization.json, kotlinx.datetime (api), kermit (api). No SDK-internal deps."). The full MODULE.md rewrite to phase-1 form happens in wave 10.

- [ ] **Step 3: Compile + test.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew :core:compileKotlinMetadata :core:allTests
  ```
  Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add core/build.gradle.kts core/MODULE.md && \
    git commit -m "refactor(core): document deps + drop unused

  Per criterion 4 of CLEANUP_CRITERIA.md.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
  ```

---

## Wave 4 — Restructure (criterion 9-3)

**Files:**
- Move: `core/src/commonMain/kotlin/uz/yalla/core/contract/location/*` → `core/src/commonMain/kotlin/uz/yalla/core/location/`.
- Update: every importer of `uz.yalla.core.contract.location.*` (within core, and any consumer SDK module — most likely `data`).
- Additional restructures from `CORE_AUDIT.md` section 3 (god-file splits, other flatten candidates).

- [ ] **Step 1: Flatten `contract/location` per phase-1 precedent.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git mv core/src/commonMain/kotlin/uz/yalla/core/contract/location/*.kt \
           core/src/commonMain/kotlin/uz/yalla/core/location/ && \
    rmdir core/src/commonMain/kotlin/uz/yalla/core/contract/location && \
    rmdir core/src/commonMain/kotlin/uz/yalla/core/contract 2>/dev/null
  ```

- [ ] **Step 2: Update package declarations in moved files.**

  For each file moved in step 1:
  ```kotlin
  // before:
  package uz.yalla.core.contract.location
  // after:
  package uz.yalla.core.location
  ```

  Use a single grep + sed if comfortable, otherwise `Edit` per file. Verify:
  ```bash
  grep -r "package uz.yalla.core.contract" /Users/islom/StudioProjects/yalla-sdk/core/
  ```
  Expected: no output.

- [ ] **Step 3: Update import sites across the SDK.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    grep -r "uz.yalla.core.contract.location" --include="*.kt" -l
  ```
  For each file in the output, rewrite the import from `uz.yalla.core.contract.location.X` to `uz.yalla.core.location.X`. (No alias collisions expected; verify after each edit.)

- [ ] **Step 4: Apply other restructure findings from `CORE_AUDIT.md` section 3.** God-file splits, other flatten candidates. Each split is its own logical change with its own compile + test cycle.

- [ ] **Step 5: Compile entire SDK** (not just core — phase-2 deps may break here).
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew compileKotlinMetadata
  ```
  Expected: BUILD SUCCESSFUL across all modules.

- [ ] **Step 6: Run all tests, not just core.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew allTests
  ```
  Expected: all green.

- [ ] **Step 7: Commit per logical restructure** (flatten = one commit, each god-file split = one commit). Use `refactor!:` prefix because import paths change.

  Example for the flatten:
  ```bash
  git add core/src && \
    git commit -m "refactor!(core): flatten contract/location/* to location/*

  Completes the phase-1 flatten precedent (contract/preferences/* was
  already done in c184119cd). Per criterion 9-3.

  Breaking: import path changed. Affects: <list other modules touched>.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
  ```

---

## Wave 5 — Quality pass (criterion 11)

**Files:** every file flagged in `CORE_AUDIT.md` section 4 ("Quality / rewrite candidates").

- [ ] **Step 1: Triage rewrite candidates.** For each entry in audit section 4:
  - **Small** (<100 lines, no API shape change): apply directly without a gate. Use the suggested target pattern from criterion 11.
  - **>100 lines OR public-shape change**: write a one-paragraph rationale, present to Islom, wait for approval before applying. The rationale lists: the file, the trigger from criterion 11, the pattern to converge on, and the breakage if any.

- [ ] **Step 2: Apply each approved rewrite.** Behavior preserves; only internals reshape. Common patterns to apply:
  - Mapper as class → `internal object Mapper { fun fromDto(…): Domain }`.
  - try/catch in business logic → `Either<DataError, T>` (note: `core` is upstream of `data`, so this should be rare; flag if found).
  - String IDs → value classes.
  - `Any?` returns → sealed types.
  - ArrayList/HashMap literals → `mutableListOf`/`mutableMapOf`.
  - Manual JSON → kotlinx.serialization (already a dep).

- [ ] **Step 3: Compile + test after every rewrite.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew :core:compileKotlinMetadata :core:allTests
  ```

- [ ] **Step 4: Commit per rewrite.** Use `refactor!:` if public shape changes, `refactor:` if not. Detailed body explaining the trigger and the pattern. Do NOT batch unrelated rewrites; each rewrite is its own commit so it can be reverted independently if it breaks something downstream.

- [ ] **Step 5: After all rewrites, run full SDK build to catch downstream breakage.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew build
  ```

---

## Wave 6 — Promote/demote flags (criterion 1)

**Files:**
- Create or append: `MIGRATION_LIST.md` at repo root (deleted at end of phase 4 with `CLEANUP_CRITERIA.md`).

This wave produces the migration list that YallaClient consumes after the single alpha tag (criterion 8). No code moves yet.

- [ ] **Step 1: Open `CORE_AUDIT.md` section 5.** For each entry approved at the decision gate:
  - **Promotion** (YallaClient → core) — append to `MIGRATION_LIST.md` under "## To promote into core" with: source path in YallaClient (best-effort guess), target path in core, brief reason.
  - **Demotion** (core → YallaClient) — append to "## To demote from core" with: current path, suggested YallaClient destination, reason. Even if `core` is logic atoms, surface anything borderline.
  - **Borderline** — append to "## To decide" for Islom's later call.

- [ ] **Step 2: Commit the migration list seed.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add MIGRATION_LIST.md && \
    git commit -m "docs(core): seed MIGRATION_LIST.md from CORE_AUDIT.md section 5

  Per criterion 1 of CLEANUP_CRITERIA.md. Applied at the end of the
  cleanup branch against YallaClient.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
  ```

---

## Wave 7 — KDoc bar (criterion 5)

**Files:** every public symbol in `core/src/commonMain` whose KDoc was either missing or deleted in wave 2-1 (and a quick re-pass on remaining KDoc to enforce the "adds info beyond signature" bar).

The existing KDoc on `error/DataError.kt` is a good positive example — variant-by-variant, explains the WHEN and the distinction from sibling variants. The `Unauthorized` doc explains "distinct from `Network.Guest` because…", which is exactly the kind of value the criterion targets. Keep that style.

- [ ] **Step 1: List all public symbols missing KDoc.**

  Run:
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk/core/src/commonMain/kotlin && \
    grep -rn "^\(public\)\? *\(fun\|class\|object\|interface\|sealed\|enum\|val\|var\) " . --include="*.kt" | \
    awk -F: '{print $1":"$2}'
  ```

  Then for each line, look one above for `*/` (KDoc closer). Anything without a `*/` directly above is missing. (A simpler heuristic: dispatch a quick subagent to produce the list.)

- [ ] **Step 2: Add KDoc per symbol.** Each must add information the signature doesn't carry — behavior, side effects, threading, ownership, hidden constraints, error semantics. KDoc that paraphrases the signature gets deleted, not written. If a symbol has no non-obvious behavior worth documenting (e.g., a value class wrapping a String), skip it — the criterion is about the API surface, not blanket coverage.

  Wait: criterion 5 says "Every public symbol gets KDoc." That's mechanical. Re-reading: "but the KDoc must add information the signature does not already convey." So: every public symbol gets KDoc, AND each KDoc adds info. If you can't write something that adds info, that's a signal the symbol may be redundant — flag in `CORE_AUDIT.md` for re-evaluation under criterion 2.

- [ ] **Step 3: Re-pass remaining KDoc** to ensure none of it paraphrases the signature. Examples to delete:
  - `/** The user id. */` above `val userId: String` — paraphrase, delete.
  - `/** Returns the order. */` above `fun order(): Order` — paraphrase, delete.
  - `/** Calls /auth/login. Token persisted. On Unauthorized, server rejected the credentials. */` above `suspend fun login(…): Either<DataError, Session>` — adds info, keep.

- [ ] **Step 4: Compile.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew :core:compileKotlinMetadata
  ```

- [ ] **Step 5: Commit.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add core/src && \
    git commit -m "docs(core): KDoc every public symbol per criterion 5

  Each KDoc adds info beyond the signature (behavior, side effects,
  threading, ownership, error semantics).

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
  ```

---

## Wave 8 — Test backfill (criterion 6)

**Files:** new test files under `core/src/commonTest/kotlin/uz/yalla/core/<package>/` for every gap from `CORE_AUDIT.md` section 6.

Known gaps from plan-time inventory (will be confirmed by audit):
- `error/` — no test directory.
- `preferences/` — no test directory.
- `contract/location/` (now `location/` after wave 4) — partial coverage; verify per-symbol.

- [ ] **Step 1: For each missing test, write a failing test first** (TDD discipline per Islom's CLAUDE.md).

  Example for `DataError`:
  ```kotlin
  // core/src/commonTest/kotlin/uz/yalla/core/error/DataErrorTest.kt
  package uz.yalla.core.error

  import kotlin.test.Test
  import kotlin.test.assertEquals
  import kotlin.test.assertNotEquals

  class DataErrorTest {
      @Test
      fun unauthorizedIsSingleton() {
          assertEquals(DataError.Unauthorized, DataError.Unauthorized)
      }

      @Test
      fun forbiddenWithSameReasonAreEqual() {
          assertEquals(
              DataError.Forbidden(reason = "no permission"),
              DataError.Forbidden(reason = "no permission")
          )
      }

      @Test
      fun forbiddenWithDifferentReasonsAreNotEqual() {
          assertNotEquals(
              DataError.Forbidden(reason = "a"),
              DataError.Forbidden(reason = "b"),
          )
      }
      // … one test per variant covering equality + per-property invariants
      // … verify network sub-hierarchy is exhaustively tested
  }
  ```

- [ ] **Step 2: Run the test, confirm it fails for the right reason.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew :core:allTests --tests "uz.yalla.core.error.DataErrorTest"
  ```
  If the test passes immediately (e.g., for trivial value-equality), that's fine — kotlin.test data-class equality is part of the "test" because it documents the contract.

- [ ] **Step 3: Add tests for every `Either.Failure` variant** in `core/result/Either.kt` if any are uncovered. The existing `EitherTest.kt` and `EitherExtensionsTest.kt` are the references for style.

- [ ] **Step 4: Run the full core test suite.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew :core:allTests
  ```
  Expected: count is higher than baseline (Pre-work step 3). All passing.

- [ ] **Step 5: Verify the bar is met.** For each public function in `core/src/commonMain`, grep the test directory for a corresponding test. Any gap is a wave-8 omission — return to step 1 for that gap.

- [ ] **Step 6: Commit per package.** Don't batch all tests into one commit — backfill for `error/` is its own commit, `preferences/` is its own, etc.

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add core/src/commonTest/kotlin/uz/yalla/core/error && \
    git commit -m "test(core/error): add DataErrorTest covering all variants

  Per criterion 6 of CLEANUP_CRITERIA.md. <baseline N → N+M> tests.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
  ```

---

## Wave 9 — Final compile + test verification

**Files:** none modified.

- [ ] **Step 1: Full SDK build (not just core).**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew build
  ```
  Expected: BUILD SUCCESSFUL across all 12 modules. If a downstream module (`data`, `foundation`, etc.) fails because of a core-side change, fix it now in a focused commit — don't defer.

- [ ] **Step 2: Confirm test count rose.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew :core:allTests | grep -E "(passed|failed)"
  ```
  Expected: zero failures, count > baseline.

- [ ] **Step 3: Quote the actual numbers in your wave-11 summary.** Per Islom's CLAUDE.md: "Quote the actual output ('232 → 245 tests, all passing'), not 'tests should pass.'"

---

## Wave 10 — MODULE.md to phase-1 form

**Files:**
- Modify: `core/MODULE.md`.

Reference: `bom/MODULE.md` and `resources/MODULE.md` for the canonical phase-1 form.

- [ ] **Step 1: Rewrite `core/MODULE.md`** to the structure:
  ```
  # Module core
  > One-line tagline.

  ## What this is
  ## What this is NOT
  ## Usage
  ## Notes
  ## Depends on
  ```

  - **What this is:** core types, `Either<L, R>`, `DataError` hierarchy, domain enums (order, settings, payment, profile), shared utilities. The brick stack atoms.
  - **What this is NOT:** any networking (`data`), any UI (`primitives`/`composites`), any platform-specific (`platform`). No mappers from external DTOs (those live in `data`).
  - **Usage:** `implementation("uz.yalla.sdk:core")`. Then a 3-4 line example showing `Either` + `DataError` use.
  - **Notes:** anything still surprising after the cleanup (e.g., the `DataError` naming origin — see existing KDoc comment about ADR-022; surface here too if relevant for consumers).
  - **Depends on:** the dep block from wave 3.

- [ ] **Step 2: Delete the old `# Package …` blocks.** The new form is module-level only; per-package docs live in KDoc on each package's index file or in package-info-style annotations (Kotlin doesn't have those — KDoc on a sentinel file works, but skip unless audit flagged it).

- [ ] **Step 3: Commit.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git add core/MODULE.md && \
    git commit -m "docs(core): rewrite MODULE.md to phase-1 form

  Aligns with bom/ and resources/. Per criterion 5 of CLEANUP_CRITERIA.md.

  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
  ```

---

## Wave 11 — Phase-2-core summary + readiness for `data`

**Files:** none modified. This wave produces a session summary and confirms readiness.

- [ ] **Step 1: Run the full SDK build one final time.**
  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && ./gradlew build
  ```
  Must pass.

- [ ] **Step 2: Tally outputs.** From the wave commits, gather:
  - Lines deleted vs added (net should be negative — cleanup is deletion-heavy).
  - Test count change (baseline N → final M).
  - Number of files removed, added, moved.
  - Promotion/demotion flags surfaced in `MIGRATION_LIST.md`.
  - Rewrites applied + line counts.

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    git diff --stat $(git merge-base HEAD main)..HEAD -- core/ && \
    cat MIGRATION_LIST.md
  ```

- [ ] **Step 3: Tell Islom: "Phase-2 `core` cleanup complete. Summary: <stats>. `MIGRATION_LIST.md` has <N> entries. Ready to plan phase-2 `data`?"**

  No commit for this wave. Phase-2 `data` is its own plan (`PHASE_2_DATA_PLAN.md`), written via `superpowers:writing-plans` after Islom approves moving on.

---

## Self-review checklist (run before handoff)

- [x] Spec coverage — every criterion 1-11 has at least one wave that applies it.
- [x] No placeholders — every `<…>` is a runtime variable explicitly explained, not a TBD.
- [x] Type/method consistency — file paths use absolute `/Users/islom/StudioProjects/yalla-sdk/…`, package names match across waves, `CORE_AUDIT.md` is the consistent inventory across waves 2-10.
- [x] Each wave has a verify step (`compileKotlinMetadata` / `allTests` / `build`) before commit.
- [x] Decision gate after wave 1 — Islom approves audit before any destructive work.
- [x] Rewrite gate inside wave 5 — >100-line rewrites surface to Islom before commit.
- [x] Versioning policy from criterion 8 honored — no alpha tag during the work; commits stay on `cleanup/phase-2-3-4`.
- [x] Test bar from criterion 6 enforced in wave 8 (every public function tested) and verified in wave 9.

---

## Out of scope (do NOT do in this plan)

- `data` module cleanup. Separate plan after this lands.
- Any other module (design, foundation, etc.).
- Public API visibility tightening (criterion 3 — left alone).
- Reintroducing `docs/` or `docs/adr/`.
- Publishing an alpha. Criterion 8 — single bump at end of phase 4.
