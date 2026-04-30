# Phase 4 — `firebase`, `maps`, `media`, `platform` Cleanup Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:executing-plans`. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply [CLEANUP_CRITERIA.md](CLEANUP_CRITERIA.md) criteria 1-11 to the four phase-4 modules on the `cleanup/phase-2-3-4` branch. Phase 4 closes out the cleanup branch — single alpha tag drops at the end.

**Architecture:** **Compressed format** — phases 1-3 used a separate audit + decision gate per module. After 5 modules of established pattern (CORE, DATA, DESIGN, FOUNDATION, PRIMITIVES, COMPOSITES), the audit findings are predictable and auto-correlated with the same 11-wave shape. Phase 4 inlines the audit findings in the per-module wave sections; no separate `*_AUDIT.md` file.

**Tech stack:** Kotlin Multiplatform.
- `platform` — `expect/actual` host for sheet/picker/loading/navigation (Android Material3 + iOS UIKit). Hub depended on by primitives + composites.
- `firebase` — gitlive Kotlin Firebase wrapper (analytics, crashlytics, messaging, remote config).
- `media` — KMP camera (CameraX on Android, AVFoundation on iOS), image gallery / picker, paging.
- `maps` — Compose-Multiplatform map (MapLibre common, Google Maps Android-specific, Apple Maps iOS-specific).

**Inventory (file counts, post-phase-3-baseline):**

| Module | Total files | commonMain | commonTest | androidMain | iosMain | `@since` | God-files (≥400) |
|---|---|---|---|---|---|---|---|
| platform | 85 | 25 | 22 | 14 | 24 | 55 | 0 |
| firebase | 17 | 8 | 7 | 1 | 1 | 40 | 0 |
| media | 64 | 10 | 14 | 23 | 17 | 155 | 0 |
| maps | 105 | 64 | 10 | 13 | 18 | 237 | 3 (mostly map-controller impls) |
| **total** | **271** | **107** | **53** | **51** | **60** | **487** | **3** |

**Carried-forward from prior audits:**
- Pre-existing iOS-link issues on `:firebase` and `:maps` (verified pre-existing on `main`; excluded from phase-3 wave-9 verifications). Phase 4 must verify these still build OR document why the failure is acceptable.
- `materialIconsExtended` is currently used in `platform/src` (status TBD per wave 1 inline-audit).
- `composites` G1 deferred swap depends on `:resources` adding YallaIcons chevron/arrow vectors. Phase 4's resources work (if any) handles this.

---

## Pre-work — Branch + baseline

- [ ] **Step 1: Verify branch is `cleanup/phase-2-3-4`.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && git rev-parse --abbrev-ref HEAD
  ```

- [ ] **Step 2: Baseline-build each module.**

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :platform:allTests :firebase:allTests :media:allTests :maps:allTests --rerun-tasks
  ```

  Pre-existing iOS-link failures on firebase/maps — record the modules that DO build cleanly. Tests on the others stay aspirational; verify post-cleanup that NO regression is introduced beyond the existing failures.

- [ ] **Step 3: Record baseline test counts per module.**

---

## Wave A — Platform module (~25 commonMain files)

**Inline audit findings (criteria 1, 2, 4, 6, 9-3, 11):**

- 55 `@since` tags to strip (criterion 2-2).
- Paraphrase KDoc strip needed across `Colors`/`Dimens` data classes — same shape as prior modules.
- Public surface walk: `NativeSheet`, `NativeLoadingIndicator`, `NativeWheelDatePicker`, `SheetIconButton`, `IconType`, `NativeNavHost` etc. — verify all are `expect`-typed contracts that primitives/composites consume.
- Build deps: `materialIconsExtended` in `implementation` — likely used internally; verify.
- Dep promotion check: `compose.runtime`/`ui`/`foundation`/`material3` likely should be `api()` (public Composable surface).
- 0 god-files at thresholds ≥ 400; biggest is `NativeNavHost.ios.kt` at 317 — watch but don't split.

**Tasks:**
- [ ] **A.1:** Strip 55 `@since` tags + paraphrase KDoc via the same Python script primitives/composites used.
- [ ] **A.2:** Tighten `api`/`implementation` split. Promote anything that appears in a public `expect` signature.
- [ ] **A.3:** Verify zero unused deps via `grep`. Drop any unused.
- [ ] **A.4:** Compile + test verify.
- [ ] **A.5:** Append entries to `MIGRATION_LIST.md`.
- [ ] **A.6:** Rewrite `platform/MODULE.md` to phase-1 form.
- [ ] **A.7:** Commit per logical group.

---

## Wave B — Firebase module (~8 commonMain files)

**Inline audit findings:**

- 40 `@since` tags to strip.
- Public surface: `YallaFirebase`, `YallaAnalytics`, `YallaCrashlytics`, `YallaMessaging`, `YallaRemoteConfig`. Each is a thin facade over `gitlive` libs.
- Dep posture: gitlive deps already `api()` (correct — gitlive types in public sigs). `kotlinx.serialization.json` may be unused (verify).
- Pre-existing iOS-link issue — must be confirmed pre-existing on main. If wave 4 introduces a new break, fail the wave.
- 0 god-files. Largest is `YallaFirebase.kt` at 175 lines.

**Tasks:**
- [ ] **B.1:** Strip @since + paraphrase KDoc.
- [ ] **B.2:** Audit deps; drop unused `kotlinx.serialization.json` if confirmed unused.
- [ ] **B.3:** Compile-verify (Android compileKotlinAndroid; iOS link expected to fail pre-existingly — document).
- [ ] **B.4:** Append to `MIGRATION_LIST.md`.
- [ ] **B.5:** Rewrite `firebase/MODULE.md`.
- [ ] **B.6:** Commit.

---

## Wave C — Media module (~10 commonMain files + 23 androidMain + 17 iosMain)

**Inline audit findings:**

- 155 `@since` tags to strip — most in androidMain/iosMain (camera implementations).
- Heavy androidMain/iosMain: CameraX (Android) + AVFoundation (iOS) gallery / picker / paging.
- Public surface: `YallaCamera` expect/actual, `YallaGallery`, paging types.
- Dep posture: paging-common, paging-compose-common, accompanist-permissions, camera-camera2/lifecycle/view, kotlinx-coroutines-guava, exif-interface — verify each is necessary.
- 0 god-files (all under 350).
- Try/catch in business logic check needed — camera implementations almost certainly carve out around the platform-API boundary (per primitives' `runCatching` carve-out for moko-geo).

**Tasks:**
- [ ] **C.1:** Strip @since + paraphrase KDoc.
- [ ] **C.2:** Walk try/catch carve-outs; document each in MODULE.md notes.
- [ ] **C.3:** Audit deps; drop unused.
- [ ] **C.4:** Compile + test verify.
- [ ] **C.5:** Append to `MIGRATION_LIST.md`.
- [ ] **C.6:** Rewrite `media/MODULE.md`.
- [ ] **C.7:** Commit.

---

## Wave D — Maps module (~64 commonMain files, the heaviest)

**Inline audit findings:**

- **237 `@since` tags** — by far the most.
- **3 god-files**: `MapControllerFakeProviderTest.kt` (616), `LibreMapController.kt` (473), `GoogleMapController.kt` (439). Provider impls naturally ride at this size; don't reflexively split — verify mixed-responsibility before deciding.
- Two provider implementations + a fake — typical impl/test split.
- Heavy use of `geo`, `maplibre.compose`, koin.
- Pre-existing iOS-link issue (likely on Apple Maps iosMain) — must be confirmed pre-existing on main.
- 0 known default-copy violations; `MapStyle` has overridable defaults.

**Tasks:**
- [ ] **D.1:** Strip @since + paraphrase KDoc.
- [ ] **D.2:** Inspect 3 god-files; split only if they mix responsibilities (otherwise document size).
- [ ] **D.3:** Audit deps; tighten `api`/`implementation` split.
- [ ] **D.4:** Compile + test verify (android side; iOS link as pre-existing).
- [ ] **D.5:** Append to `MIGRATION_LIST.md`.
- [ ] **D.6:** Rewrite `maps/MODULE.md`.
- [ ] **D.7:** Commit.

---

## Wave E — Final SDK verification + cleanup

- [ ] **E.1:** Full SDK build matrix.

  ```bash
  cd /Users/islom/StudioProjects/yalla-sdk && \
    ./gradlew :core:allTests :data:allTests :design:allTests \
              :foundation:allTests :primitives:allTests \
              :composites:allTests :media:allTests :platform:allTests \
              :firebase:compileKotlinAndroid \
              :maps:compileKotlinAndroid
  ```

- [ ] **E.2:** Confirm no test regression on the 8 testable modules (target: ≥ 969 from end-of-composites).

- [ ] **E.3:** Update `MIGRATION_LIST.md` phase-status block.

- [ ] **E.4:** Surface phase-4 wave-by-wave summary to user.

- [ ] **E.5:** Tag the alpha — single `v0.0.5-alpha13` (or next alpha number) on the cleanup branch's tip.

- [ ] **E.6:** Decide cleanup-doc retention: keep `CLEANUP_CRITERIA.md` + `MIGRATION_LIST.md` for the YallaClient migration follow-up; delete the per-module `*_AUDIT.md` + `PHASE_*_PLAN.md` files (they're work-trail, not reference docs) — OR keep them all under `docs/cleanup/` for posterity.
