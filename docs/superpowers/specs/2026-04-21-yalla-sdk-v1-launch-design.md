# yalla-sdk v1.0 Public Launch

> Spec date: 2026-04-21
> Status: Draft (brainstorm complete, revised after 4-agent critical review, awaiting Islom final review)
> Scope: All 11 published yalla-sdk modules + BOM
> Impact: Public release under Apache 2.0; YallaClient continues as primary consumer

## Problem

yalla-sdk today is a Yalla-internal KMP library hosted on GitHub Packages at version `0.0.8-alpha04`. Eleven modules publish under `uz.yalla.sdk:*` coordinates. YallaClient consumes them. External visibility is zero.

The audit on 2026-04-21 plus the follow-up critical review surfaced the gap between where the codebase is and where a Yalla-external KMP developer would find it credible:

- **Foundation layer** (core, data, resources): `data` module has `createHttpClient` leaking an unmanaged `SupervisorJob` (`HttpClientFactory.kt:68`), 6 preference implementation classes are untested, redirect mapping (301–399 → `DataError.Network.Client`) is unusual, `GuestModeGuard` whitelist is hardcoded. `StaticPreferencesImpl` is complete (earlier audit claim was stale). Resources is missing `values-uz` despite Uzbek being the primary market locale; `values-be` contains Uzbek Cyrillic text (locale code mislabeled, not leftover Belarusian). `core` publishes `Either<out D, out E>` with inverted generic parameter order relative to ecosystem convention (Arrow is error-first), and 6 preference contract *interfaces* are public — adding a method to any of them after 1.0 is a major-version break.
- **Bridge layer** (design, foundation, platform): `LocationManager` (`LocationManager.kt:61`) and `SwitchingMapProvider` (`SwitchingMapProvider.kt:56`) share the same unmanaged-scope pattern as `createHttpClient`. Newly-added `LocationServices` (`expect`/`actual`, 0.0.8) uses Koin `GlobalContext.get()` without guards. `platform` has 13 `expect`/`actual` pairs with only 5 test files today. Four contract asymmetries between Android and iOS: `NativeSheet.onFullyExpanded` (Android state-based vs iOS synchronous-callback, semantically different), `SystemBarColors` dual overload, `ObserveSmsCode` iOS stub, `PlatformConfig` asymmetry (iOS factory-heavy, Android empty).
- **UI layer** (primitives, composites): `primitives` follow the Gold Standard (`COMPONENT_STANDARD.md`). `composites` still has 6 Items with `title: String` bundled parameters (banned), 1 `Navigable` drawer with the same issue, `EmptyStateState` bundles `Painter + String + String`. `CarNumberState` is clean (earlier audit wrongly flagged it). Of the 15 "unused" components listed in `AUDIT_RESULTS.md`, **only 3 remain in the tree**: `ContentCard`, `AddressCard`, `RouteView`. The other 12 are already deleted. `Snackbar` (40 call-site usages) is untested.
- **Services** (maps, media, firebase): `SwitchingMapProvider` scope leak; map overlays duplicated between Google and MapLibre with no shared interface; iOS Compose `MapApplier` is bespoke and fragile. `YallaGallery` has a large Android/iOS parity gap (Paging3 vs PHPicker stub). Firebase lacks init guards on Android. `@ExperimentalYallaGalleryApi` is the only opt-in marker today and is missing `@Retention(AnnotationRetention.BINARY)` — violates the repo's own `.claude/rules/library-api.md`.
- **API-stability tooling**: `org.jetbrains.kotlinx.binary-compatibility-validator` is not wired. `./gradlew apiCheck` does not exist. **The plugin does not generate API baselines for KMP Native targets** — only JVM/Android. 1.0 iOS binary-compatibility cannot be mechanically enforced by the plugin alone.
- **CI**: `.github/workflows/ci.yml` does not exist. Only `publish.yml` is present (triggered on push to `main`). There is no PR gating today.

The SDK has been described internally as aspiring to "standard for Uzbekistan KMP market" and "Google-level quality." The current shape does not yet back those labels. Five specific 1.0-locking calls (Yalla-shaped public API, `Either` generic order, Native API enforcement gap, expect/actual asymmetries, preference interface extensibility) must be decided before the alpha → beta freeze or they ship as permanent debt.

## Vision

Version 1.0 of yalla-sdk is a **publicly-readable, Apache-2.0-licensed, Yalla-opinionated KMP ride-hailing SDK** that a ride-hailing developer in Uzbekistan can clone, read, fork, and — if they accept Yalla's backend conventions and configure a GitHub personal access token — consume directly from GitHub Packages. Source is open; distribution is authenticated; promotion is zero. Primary consumer is and remains YallaClient.

Success means that a senior engineer at a comparable ride-hailing startup in the region can read the source, understand it without Yalla-internal context, and either adopt it wholesale or cherry-pick the patterns. YallaClient is the proof-of-scale. The code and the docs speak for themselves.

## Goals

1. All 11 published modules + BOM ship at `1.0.0` together on a single version train.
2. Public-API surface is frozen, documented, and enforced via `apiCheck` in CI **for JVM/Android targets**. iOS Native surface is enforced via the `audit-api` skill (manual diff) as the primary gate, supplemented by a version-to-version integration test that compiles a representative surface against every published version.
3. 100% public-API KDoc. Risk-based test coverage (see Quality Bar). Visual-regression goldens captured for every component across light + dark + press states. Performance baselines tracked in CI.
4. Zero unmanaged-scope leaks in long-lived objects: `LocationManager`, `SwitchingMapProvider`, `createHttpClient` adopt caller-owned lifecycles (`close()` contract or injected `CoroutineScope`). **YallaClient's DI architecture verified capable of absorbing the contract change before alpha begins.**
5. ADR-005 string→slot migration completed for the 7 affected composites (`ListItem`, `IconItem`, `NavigableItem`, `SelectableItem`, `PricingItem`, `AddressItem`, `Navigable` drawer), plus `EmptyStateState` decomposition — all before API freeze.
6. Three still-present unused components (`ContentCard`, `AddressCard`, `RouteView`) removed.
7. `Either<out D, out E>` generic parameter order **flipped to `Either<out E, out D>` (error-first, ecosystem-standard)** during alpha — cheap mechanical refactor now, impossible at 1.0 without 2.0.
8. Yalla-shaped public surface gated with `@InternalYallaApi`: `ApiResponse<T>` / `ApiErrorResponse` / `ApiListResponse` / `UnauthorizedSessionEvents` + the 6 `core/contract/preferences/` interfaces. Library surface stays sharp; Yalla-internal shape is honestly labeled.
9. Missing primary-market locale (`values-uz` Latin) added. `values-be` renamed to `values-uz-Cyrl` (it contains Uzbek Cyrillic, not Belarusian).
10. Source published under Apache 2.0 on the public `RoyalTaxi/yalla-sdk` repo. Distribution via GitHub Packages (`https://maven.pkg.github.com/RoyalTaxi/yalla-sdk`). Dokka reference docs hosted on GitHub Pages.
11. OSS-hygiene files in place: `LICENSE`, `README.md` (public-facing), `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, `SECURITY.md`, `CHANGELOG.md`, issue + PR templates, `SUPPORT.md`, `CODEOWNERS`.
12. Live-verify discipline applied to every non-trivial change: wire the local SDK into YallaClient (composite build or `mavenLocal()`), launch an emulator via the mobile/android MCPs, pixel-perfect-verify against design intent before marking the change done.

## Non-Goals

- **Sonatype / Maven Central distribution.** Explicitly excluded from 1.0.
- **Backend-agnostic abstraction.** Consumer model A locked — Yalla's backend shape stays, gated `@InternalYallaApi` so it is honestly labeled rather than paraded as general library surface.
- **Sample apps.** No HelloTaxi, no driver/passenger/dispatcher demos. YallaClient is the integration witness.
- **External paid security audit.** `SECURITY.md` + disclosure process at 1.0; paid audit deferred to 1.x.
- **Community localization flow.** uz/ru/en in; broader i18n is 1.x.
- **Marketing / launch promotion.** Passive distribution.
- **New features.** 1.0 is cleanup + hardening + publish.
- **External snapshot testing tooling at Android-parity 0% diff on iOS.** Investigated in Week 1 of alpha (candidates: `shot`, `snapshot-testing-kmp`, bespoke XCTest + UIKitView harness). Realistic iOS bar for CI is 1% tolerance; 0% diff treated as aspirational.
- **Full Macrobenchmark on shared CI runners.** 1.0 scope is Compose-recomposition sanity checks only; full cold-start/memory benchmarks parked for 1.x or a dedicated runner.

## Consumer Model

**Model A: Yalla-first, others welcome.** Public API stays opinionated around Yalla's backend envelope, preference schema, and Uzbekistan-specific defaults — **but the Yalla-specific shapes are gated with `@InternalYallaApi`**, not shipped as blessed library surface. Any consumer that wants to use them must opt in explicitly and accept their internal nature.

## Quality Bar

Level 2 (foundational standard) plus most of Level 3 (ecosystem-grade), minus multi-demo apps and external paid audit. **Coverage is risk-based per module, not a universal line-coverage floor** (line-coverage on expect/actual composites is a vanity metric):

| Module | Coverage target |
|---|---|
| `core` | ≥80% line + full branch coverage on `Either`, `DataError` hierarchy, enum `from()` factories |
| `design` | ≥80% line (pure logic: token resolution, theme equality) |
| `data` | ≥75% line + **integration tests** for `createHttpClient` (401, retry, guest-mode, connection error) and every preference impl (in-memory DataStore) |
| `foundation` | ≥75% line + behavioral tests for `LocationManager` lifecycle, `ObserveAsEvents`, `ChangeLanguage` |
| `platform` | **No line-coverage target** — 13 expect/actual pairs; Android behavioral test for every pair (click fires, state toggles, semantics correct); iOS snapshot/behavioral where tooling allows |
| `primitives` | Visual-regression goldens for every component × (light, dark, press/disabled) + behavioral tests for every interactive primitive |
| `composites` | Visual-regression goldens for every component + behavioral tests for every interactive composite; integration tests for `Sheet` state transitions, `ExpandableSheetState` |
| `maps` | **No line-coverage target** — integration tests for provider switching, camera ops, marker/polyline lifecycle, overlay rendering on both providers |
| `media` | Behavioral: picker happy/cancel/permission paths; correctness: compression output size within preset bounds + valid JPEG + bounded iterations; EXIF orientation preserved |
| `firebase` | Behavioral: analytics event logging (fake delegate), crashlytics record, messaging token retrieval |
| `resources` | N/A (resources only) |
| `bom` | N/A (no logic) |

Other quality requirements:

- 100% public-API KDoc
- `apiCheck` (binary-compatibility-validator) wired and enforced in CI **for JVM/Android targets only** (plugin does not cover Native)
- `audit-api` skill is the **primary** gate for iOS Native API stability, not a fallback
- Visual-regression goldens: **Android 0.1% tolerance, iOS 1% tolerance** on pinned emulator/simulator images; below-tolerance regressions triaged + re-goldens in the PR
- Performance: Compose recomposition sanity checks tracked in CI; full Macrobenchmark parked for 1.x or dedicated runner
- Accessibility pass on primitives + composites (`Role` semantics, 48dp/44pt touch targets, content descriptions) — acceptance criteria: WCAG 2.1 AA; Android TalkBack on latest 3 OS versions; iOS VoiceOver on latest 3 iOS versions
- detekt + ktlint clean
- Dokka docs site on GitHub Pages
- `SECURITY.md` + vulnerability-disclosure process live
- Published API stability guarantee with deprecation-cycle SLA — one-minor minimum, plan to widen to 3 minors or 6 months if external consumers appear

---

## 1. Mission & Scope

**Mission.** Take yalla-sdk from a Yalla-internal KMP library to a publicly-readable, Apache-2.0-licensed SDK distributed via GitHub Packages under the `uz.yalla.sdk` group ID, serving as a reference implementation for Uzbekistan KMP ride-hailing development. Primary consumer is YallaClient. Other teams may consume with a GitHub personal access token and compatible conventions; we are not optimizing for frictionless adoption.

**In 1.0 (shipped together):** `core`, `data`, `resources`, `design`, `foundation`, `platform`, `primitives`, `composites`, `maps`, `media`, `firebase`, `bom`. Coordinates: `uz.yalla.sdk:<module>:1.0.0` (plus `uz.yalla.sdk:bom:1.0.0`).

**Out of 1.0:** Sonatype / Maven Central, multi-demo apps, HelloTaxi, external paid audit, community localization flow, new features, full Macrobenchmark, 0%-diff iOS goldens.

**Success signal.** All three release gates pass. GitHub Packages has `1.0.0` artifacts for all 12 artifacts. Dokka docs live at `https://royaltaxi.github.io/yalla-sdk/`. YallaClient's `gradle/libs.versions.toml` pins `uz.yalla.sdk = "1.0.0"` and YallaClient's CI passes. First 1.0.x patch cadence established.

---

## 2. Release Gates & Cadence

### Version Ladder Realism

Current version: `0.0.8-alpha04`. Outstanding breaking-change clusters for 1.0 (per `.claude/rules/publishing.md`, each third-segment bump + alpha reset):

1. `Either<D, E>` → `Either<E, D>` generic-order flip (touches all of `core` and `data`)
2. Scope-ownership refactor — `createHttpClient`
3. Scope-ownership refactor — `LocationManager`
4. Scope-ownership refactor — `SwitchingMapProvider`
5. `ApiResponse` / `UnauthorizedSessionEvents` / preference interfaces gated `@InternalYallaApi`
6. ADR-005 string→slot migration on 7 composites
7. `EmptyStateState` decomposition
8. 3 unused-component deletions (minor binary break)
9. `LanguageOption` unproduction-ready value gating
10. `PlatformConfig` asymmetry resolution
11. `NativeSheet.onFullyExpanded` ADR resolution
12. `SystemBarColors` canonical shape pick
13. `ObserveSmsCode` iOS posture decision
14. `YallaGallery` API narrowing
15. `@RequiresOptIn` marker consolidation (retire `ExperimentalYallaGalleryApi`)

Realistic version path: **`0.0.8-alpha04 → 0.0.9-alpha01 → 0.0.10-alpha01 → … → roughly 0.0.23-alpha01 → 0.0.23-beta01 → 1.0.0-rc1 → 1.0.0`**. Ten-plus third-segment bumps during alpha is the honest cost of paying down the debt before freeze. Skipping it = shipping the debt into 1.0 permanently.

### Version Ladder (single train, all 11 modules + BOM move together)

| Phase | Visibility | API rules |
|---|---|---|
| `0.x-alphaNN` | Yalla-only (GitHub Packages, as today) | Breaking changes allowed and expected. Each breaking cluster bumps third segment + resets alpha. |
| `0.x-betaNN` | Yalla-only (GitHub Packages) | API freeze. Additive only. `apiCheck` enforced in CI. |
| `1.0.0-rcN` | Yalla-only (GitHub Packages) | Bug fixes only. No new APIs, no API changes. |
| `1.0.0` | Public (Apache-2.0 source, artifacts on GitHub Packages behind auth) | Frozen. Breaking changes require major-version bump + deprecation cycle. |
| `1.0.N` | Public | Patch fixes only. Non-breaking. |
| `1.1.0` | Public | Additive only. New APIs OK, no removals. |
| `2.0.0` | Public | Breaking allowed; deprecation cycle required. |

### Alpha-Start Prerequisites

Before opening the first alpha branch, these must be confirmed (one day of investigation, parallel with spec finalization):

- **YallaClient DI architecture can absorb the scope-ownership contract change.** Verify: is `LocationManager`, `SwitchingMapProvider`, `HttpClient` currently a Koin singleton with no explicit lifecycle, or is it scoped? If singleton-structured, the scope-ownership refactor cascades into a YallaClient refactor — surface cost before committing.
- **`binary-compatibility-validator` version and Klib/Native coverage.** Verify: is the current plugin version's Klib ABI mode production-ready on iOS arm64 / iosSimulatorArm64, or are we confirmed on `audit-api`-only for iOS enforcement?
- **iOS snapshot tooling landscape.** Shortlist (`shot`, `snapshot-testing-kmp`, bespoke XCTest + UIKitView). Pick one; document trade-offs; accept 1% CI tolerance.

### Gate 1 — alpha → beta

All of:

- 3 still-present unused components (`ContentCard`, `AddressCard`, `RouteView`) removed.
- ADR-005 migration done (7 affected composites + `EmptyStateState` decomposition).
- 3 scope leaks fixed (`LocationManager`, `SwitchingMapProvider`, `createHttpClient`) with YallaClient call sites migrated in lockstep.
- `Either<D, E>` flipped to `Either<E, D>` with ADR in `docs/06-DECISIONS.md` and YallaClient call sites migrated.
- `@InternalYallaApi` marker declared in `core`; applied to `ApiResponse<T>`, `ApiErrorResponse`, `ApiListResponse`, `UnauthorizedSessionEvents`, and the 6 `core/contract/preferences/` interfaces; YallaClient call sites opt in.
- `@RequiresOptIn` marker consolidated to exactly 2: `ExperimentalYallaApi` + `InternalYallaApi` (both in `core`, both `@Retention(AnnotationRetention.BINARY)`). `ExperimentalYallaGalleryApi` retired; call sites migrated to `ExperimentalYallaApi`.
- `StaticPreferences` contract reviewed and either retained with full impl (already complete) or deleted.
- `values-uz/strings.xml` (Latin) added. Islom self-reviews translations.
- `values-be` renamed to `values-uz-Cyrl` (contains Uzbek Cyrillic text, not Belarusian).
- 6 preference-impl classes have unit tests.
- Per-module coverage targets met (see Quality Bar table).
- 100% public-API KDoc.
- `apiCheck` wired and green on JVM/Android targets; `audit-api` skill validated as iOS API-stability gate with a documented workflow.
- Visual-regression goldens captured (light + dark + press/disabled; pinned emulator/simulator images; Android 0.1% tolerance, iOS 1% tolerance).
- Compose recomposition sanity checks tracked.
- detekt + ktlint clean in CI.
- Accessibility pass done per Quality Bar criteria.
- 4 `expect`/`actual` asymmetries each resolved via a micro-ADR in `docs/06-DECISIONS.md`: `NativeSheet.onFullyExpanded`, `SystemBarColors` dual overload, `ObserveSmsCode` iOS posture, `PlatformConfig` asymmetry.
- Semantic-stability pass: every public `sealed`/`enum`/`data class`/`@Serializable` model reviewed for add-member / add-case hazards; `@SerialName` on every serialized property.
- Pre-launch secret scan: no hardcoded API keys, OEM tokens, backend URLs, or test credentials in the tree (repo will become publicly readable).
- `.github/workflows/ci.yml` exists and enforces all of the above on PR + push.

### Gate 2 — beta → rc

All Gate 1 gates hold, plus:

- Full E2E automation matrix green on the mobile/android MCPs across the target device set.
- No API changes for ≥3 consecutive commits on the beta branch.
- Dokka docs site live on GitHub Pages.
- CHANGELOG.md populated for the 1.0.0 release notes (template + content filled).

### Gate 3 — rc → 1.0 (pre-flight, not post-hoc)

- `SECURITY.md` + vulnerability-disclosure process live (disclosure inbox provisioned, 72h triage, P0 patch 7d / P1 patch 30d).
- `LICENSE` (Apache 2.0) in place; repo license-labeled on GitHub.
- **Integration dry-run**: YallaClient feature-branch pins `uz.yalla.sdk` to the current rc version; YallaClient's full CI passes on that branch; zero P1/P2 regressions discovered. This is pre-flight validation, **not** a post-publish gate — the rc proves integration readiness before 1.0 is tagged.
- Zero P1 / P2 regressions open against the SDK at rc.
- Dependency pin decisions recorded (Compose MP, Kotlin, Gradle locked to specific versions for 1.0 branch; bump policy documented in `CONTRIBUTING.md`).

### Gate Failure = Backward Motion

A P1 or public-API regression in rc sends us back to beta. We do not ship around regressions.

### Validation Model — "Automation-Primary + Rapid-Patch"

Validation is exhaustive automation on pinned device/emulator matrices (Roborazzi on Android; iOS snapshot tool selected in Week 1 of alpha from the shortlist `shot` / `snapshot-testing-kmp` / bespoke XCTest + UIKitView; full E2E on the mobile/android MCPs). Three residual bug classes are expected and acknowledged post-1.0: OEM fragmentation tail, long-duration resource issues, third-party SDK variance under field conditions. These ship as 1.0.x patches with rapid cadence (P1 within 7 days). **1.0.x patches are a normal part of this model, not a quality failure.** The spec's earlier framing "no soak" vs "fast patches" was a contradiction; the honest frame is: automation is our primary defence, rapid patches are our secondary defence. Both are load-bearing.

---

## 3. Cross-Cutting Infrastructure

### Build + Publish

- **Wire `org.jetbrains.kotlinx.binary-compatibility-validator`** in the root `build.gradle.kts`. Generate `api/` baseline files per published module **for JVM/Android targets**. Plugin does not cover KMP Native targets; **the `audit-api` skill is the iOS API-stability primary gate**, not a fallback. Documented alpha-start investigation task: verify the plugin's Klib ABI mode on our version; if production-ready for iOS arm64 / iosSimulatorArm64, wire it and promote. If not, `audit-api` stays the iOS gate.
- **Keep existing `.github/workflows/publish.yml` as-is** — single workflow, fires on push to `main`, publishes at `yalla.sdk.version` from `gradle.properties`. No split into alpha/release files (GitHub Packages is a single endpoint; splitting adds ceremony without a gate).
- **POM metadata** — add `name`, `description`, `url`, `licenses`, `scm`, `developers` to the convention plugin's publish configuration. GitHub Packages UI does not render this, but Dokka and any future mirroring benefit. Low-priority but cheap.
- **Clean up stale catalog entry** — remove `yalla-sdk = "0.0.1-alpha08"` from `gradle/libs.versions.toml`.

### CI — `.github/workflows/ci.yml` (creation task, does not exist today)

New workflow, runs on every PR and push to `main`:

- `ktlintCheck`, `detekt` — 5 minutes
- `test` on all targets (unit + KMP common) — 30 minutes
- `apiCheck` (JVM/Android only, after plugin wiring) — 15 minutes
- `audit-api` skill invocation or equivalent manual baseline check (iOS) — 5 minutes
- Roborazzi visual-regression on Android (after library wiring) — 1 day setup, ~10 min per run
- iOS snapshot placeholder (replaced with real tooling once alpha investigation picks one)
- Compose recomposition sanity checks — 1–2 hours setup
- Full Macrobenchmark parked for 1.x or dedicated runner

Aggregate: 2–3 days of GitHub Actions authoring + runner setup.

### Documentation

- **Dokka v2** already applied at root (`build.gradle.kts` line 16). Per-module `MODULE.md` aggregation — wire.
- **GitHub Pages** target: `https://royaltaxi.github.io/yalla-sdk/`. Add `peaceiris/actions-gh-pages` workflow step on release publish.
- **`docs/` rewrite for external audience**: rewrite `00-START-HERE.md`. Keep-in-place-but-review `01-ARCHITECTURE.md`, `02-COMPONENT-GUIDE.md`, `03-PATTERNS.md`, `04-PUBLISHING.md`, `05-TESTING.md`, `06-DECISIONS.md`. Add `MIGRATION.md` scaffold.
- **`docs/obsidian/`** excluded from Dokka + Pages output.

### Open-Source Meta (repo root)

- `LICENSE` — Apache 2.0 text, dated.
- `README.md` — public-facing rewrite: what this is, how to add (with GitHub token auth snippet), link to docs, coverage / license / release badges. Honest positioning: "Yalla-specific ride-hailing SDK, publicly readable, authentication required to consume binaries."
- `CONTRIBUTING.md` — contribution flow, style, commit convention, PR checklist, dependency pin policy, local dev setup SOP.
- `CODE_OF_CONDUCT.md` — Contributor Covenant.
- `SECURITY.md` — disclosure email (provision `security@yalla.uz` or confirm existing Yalla security inbox during Gate 3 pre-work), 72h triage, P0 patch 7d / P1 patch 30d.
- `CHANGELOG.md` — Keep-a-Changelog. Release-notes template for 1.0.0: API highlights, breaking-change summary, migration guide cross-reference.
- `.github/ISSUE_TEMPLATE/` — bug, feature, docs.
- `.github/PULL_REQUEST_TEMPLATE.md` — checklist (tests, apiCheck, docs, visual-verified).
- `SUPPORT.md` — honest "best-effort, no contractual support" framing.
- `CODEOWNERS` — Islom owns all modules at 1.0.

### i18n

- Add `values-uz/strings.xml` (Uzbek Latin). Islom self-reviews translations during normal PR review.
- Rename `values-be` → `values-uz-Cyrl`. Its content is Uzbek Cyrillic; locale code was mislabeled.

### Tooling Tightening

- **detekt**: enable `detekt-formatting` ruleset; audit existing `@Suppress(...)` annotations at Gate 1 (every suppression requires a one-line inline justification; unjustified suppressions resolved or escalated before beta).
- **ktlint**: unchanged (`ktlint_official`).
- **Spotless**: unchanged.

---

## 4. Per-Module Work Scope

Sizing legend: **XS** (hours), **S** (~1 day), **M** (~2–3 days), **L** (~4–7 days), **XL** (1+ week). These are execution-time estimates assuming Claude producing + Islom reviewing at pace. Calendar time is higher — see Rollup.

### core — Size: M (was S; generic-order flip + marker declarations bumps it)

- **Flip `Either<out D, out E>` → `Either<out E, out D>`** (error-first). Mechanical refactor across all of `core` + `data`; includes YallaClient call-site updates in lockstep. Write ADR in `docs/06-DECISIONS.md`.
- Declare **exactly 2** `@RequiresOptIn` markers: `ExperimentalYallaApi`, `InternalYallaApi`. Both `@Retention(AnnotationRetention.BINARY)`. Documented in `library-api.md`.
- Apply `@InternalYallaApi` to the 6 preference contract interfaces (`SessionPreferences`, `UserPreferences`, `ConfigPreferences`, `InterfacePreferences`, `PositionPreferences`, `StaticPreferences`) — they are interfaces, adding methods post-1.0 without the gate is a 2.0.
- Add `@Serializable` to domain models traveling over Ktor. Every serialized property gets explicit `@SerialName` to decouple Kotlin property name from wire name.
- Add `getOrNull()`, `getOrThrow()`, `fold()` extensions to `Either` (non-breaking).
- Tests for untested domain models + preference-contract interface conformance.
- `StaticPreferences` contract: already has impl; verify intentional; keep or delete accordingly.
- Semantic-stability pass: review every `sealed`/`enum` (`DataError`, `OrderStatus`, `GenderKind`, `LocaleKind`, `MapKind`, `ThemeKind`, `PaymentKind`, `PlaceKind`, `PointKind`) for add-case hazards — document "add-case is minor but source-breaking; use `@Deprecated` warm-up" posture.

### data — Size: L

- **Fix `createHttpClient` scope ownership** — replace unmanaged `SupervisorJob` with caller-owned lifecycle (`HttpClientFactory.create(scope: CoroutineScope)` or `install(scope)` via Koin). YallaClient migration in lockstep.
- **Gate `ApiResponse<T>`, `ApiErrorResponse`, `ApiListResponse` with `@InternalYallaApi`.** These encode Yalla's backend envelope; honest labeling.
- **Gate `UnauthorizedSessionEvents` with `@InternalYallaApi`.** It is a global mutable `object`; library-surface honesty requires the marker.
- Unit tests for all 6 preference-impl classes (in-memory DataStore + fake Settings).
- Review redirect handling: 301–399 → `DataError.Network.Client` is unusual. Either remap to `Server` / dedicated `Redirect`, or keep with explicit KDoc justifying.
- Make `GuestModeGuard` whitelist configurable via `NetworkConfig`.
- Integration tests for `createHttpClient`: 401, retry + backoff, guest mode, connection error, request timeout.
- Note: `StaticPreferencesImpl` is complete; no work here.

### resources — Size: M

- Add `values-uz/strings.xml` (Uzbek Latin). Islom self-reviews.
- Rename `values-be` → `values-uz-Cyrl` (contains Uzbek Cyrillic, not Belarusian).
- Write `MODULE.md`.
- Harden Valkyrie task ordering to a `dependsOn` graph.

### design — Size: XS

- KDoc platform `Font.android.kt` / `Font.ios.kt` actuals.
- Add `FontScheme` equality test.

### foundation — Size: M

- **Fix `LocationManager` scope ownership** (caller-owned `close()` or injected scope).
- Error handling on `LocationServices` Android Koin NPE + iOS settings-open.
- Guard/remove unproduction-ready `LanguageOption.UzbekCyrillic`, `LanguageOption.English`.
- Tests for `LocationManager` lifecycle, `ObserveAsEvents`, `ChangeLanguage`.

### platform — Size: L

- Tests for every `expect`/`actual` pair (13). Per module's coverage target: no line-coverage floor, but every pair gets a behavioral test on Android at minimum; iOS where snapshot tooling allows.
- **Four asymmetry micro-ADRs in `docs/06-DECISIONS.md`**, each resolved before beta cut:
  - `NativeSheet.onFullyExpanded` — Android currently state-based (fires on `SheetValue.Expanded`), iOS synchronous-callback (fires on `present()` success). Pick one semantic definition; KDoc it precisely; possibly make the name platform-accurate (e.g. `onPresented` if it fires-on-present, or align Android behavior to fire-when-detent-reached).
  - `SystemBarColors` dual overload — one canonical shape, document why, remove or deprecate the other.
  - `ObserveSmsCode` iOS posture — either implement via `UITextContentType.oneTimeCode`, or mark Android-only and remove from shared expect surface.
  - `PlatformConfig` asymmetry — either widen `AndroidPlatformConfig` to symmetric factory object (source-compatible if defaulted) or document iOS's factory burden as accepted asymmetry.
- KDoc on both `statusBarHeight()` actuals.

### primitives — Size: M

- Tests for 9+ untested components; priority `TopBar` + `LargeTopBar`.
- Capture visual-regression goldens for every primitive across light + dark + press/disabled.
- Accessibility: `Role`, touch targets, content descriptions.

### composites — Size: L

- **ADR-005 string→slot migration** for 6 Items: `ListItem`, `IconItem`, `NavigableItem`, `SelectableItem`, `PricingItem`, `AddressItem`. Breaking; YallaClient lockstep.
- **`Navigable` drawer** string→slot migration (16+ YallaClient usages).
- **`EmptyStateState` decomposition** — `Painter + String + String` → proper slots.
- Delete 3 still-present unused composites: `ContentCard`, `AddressCard`, `RouteView`.
- Tests for `Snackbar` + `SnackbarHost`, `Navigable`, `EmptyState`, `SectionBackground`.
- Visual-regression goldens on every composite × (light, dark).
- **Note**: `CarNumberState` is clean (earlier audit wrongly flagged it). No work here.

### maps — Size: L

- **Fix `SwitchingMapProvider` scope ownership.**
- Shared overlay interface for `RouteLayer`, `LocationsLayer`, `LocationIndicator` (replace parallel Google/Libre impls).
- Tests for provider switching and `MapController` core ops against a fake provider.
- KDoc the iOS `MapApplier` / `MapNode` bespoke machinery.
- iOS `GMSMapView` interop leak stress test.

### media — Size: L

- **Resolve `YallaGallery` Android/iOS parity**: narrow the common API to what both platforms can deliver identically; richer features (Paging3-backed grid) move behind `ExperimentalYallaApi` (after `ExperimentalYallaGalleryApi` is retired).
- Retire `ExperimentalYallaGalleryApi` marker (missing `@Retention(BINARY)` per repo rule); migrate call sites to the consolidated `ExperimentalYallaApi`.
- EXIF orientation in camera capture (currently stripped on Android).
- Per-instance CameraX executor (not module-global).
- Tests for compression correctness + picker paths.

### firebase — Size: S

- Android init guard (fail loudly if `google-services.json` missing or `FirebaseApp.getInstance()` null).
- Guard lazy property access before `initialize()` — `IllegalStateException` with clear message.
- Tests for analytics, crashlytics, messaging.
- Prominent KDoc on iOS Swift-side `FirebaseApp.configure()` requirement.

### bom — Size: S (was XS; version-alignment-only scope + apiCheck + 1.0 hardening)

- Verify every published module appears in `constraints`.
- **Document scope in README + `docs/04-PUBLISHING.md`**: "first-party version alignment only, not compose-bom-style transitive third-party coverage. Consumers bring their own third-party BOMs."
- `apiCheck` / POM-structure validation to catch constraint drift across minor releases.

### docs — Size: M (first-class deliverable; no sample app means docs load-bearing)

- Rewrite `00-START-HERE.md` for external audience.
- Getting-started walkthrough with code snippets per module surface.
- Tighten `02-COMPONENT-GUIDE.md` post-ADR-005.
- Write `MIGRATION.md` scaffold.
- Audit `06-DECISIONS.md`; formalize the 4 asymmetry ADRs + the `Either`-flip ADR + the `@InternalYallaApi` ADR.
- Dokka landing page + per-module index content.

### Rollup

| Module | Size | Top risks |
|---|---|---|
| core | M | Either flip (mechanical but everywhere); @InternalYallaApi gating on 6 interfaces |
| data | L | Scope-ownership refactor + @InternalYallaApi gating + preference-impl tests |
| resources | M | Uzbek locale quality; `values-be` rename coordination |
| design | XS | — |
| foundation | M | Scope-ownership; language-option guards |
| platform | L | 13 expect/actual pairs + 4 micro-ADRs |
| primitives | M | Test coverage + visual goldens |
| composites | L | ADR-005 breaking migration (7 components) + deletions + goldens |
| maps | L | Scope-ownership + iOS interop + overlay interface |
| media | L | Gallery parity + marker consolidation + EXIF + executor |
| firebase | S | Init guards |
| bom | S | Scope documentation + apiCheck |
| docs | M | Getting-started rewrite (load-bearing) |

**Aggregate raw execution sizing:** 40–50 person-days of focused work (up from earlier 35–45 after accounting for `Either` flip, `@InternalYallaApi` gating, marker consolidation, 4 micro-ADRs, BOM work, CI creation from scratch).

**Calendar time is the real number.** Serial-review bottleneck: Claude produces ~1 module per 1–3 days, Islom reviews at 1 module per ~1 week. With 13 logical workstreams (12 modules + docs + CI), calendar time is **8–12 weeks** end-to-end, assuming nothing regresses and Islom stays on pace. The spec's earlier "35–45 person-days" framing misrepresented this. Compression is possible through parallel agent teams for non-overlapping modules, but review bandwidth remains the bottleneck.

---

## 5. Post-1.0 Operating Model

### Versioning Policy (strict SemVer)

- **1.0.N (patch)** — bug fixes only. No new public API. Binary- and source-compatible with every `1.0.*` before. `apiCheck` clean on JVM/Android; `audit-api` clean on iOS.
- **1.N.0 (minor)** — additive only. New APIs, modules, components allowed. No removals, no signature changes. Deprecations OK (WARNING level). Adding members to public sealed hierarchies or enum values requires a `@Deprecated` warm-up cycle for consumers' exhaustive `when`s.
- **2.0.0 (major)** — breaking allowed. Requires ADR + `MIGRATION.md` entry.

### Deprecation Cycle

Deprecate in `N.x` with `@Deprecated(level = WARNING, replaceWith = ...)`. Escalate to `ERROR` one minor later if forcing migration. **Remove only in `(N+1).0`. Minimum one minor between deprecation and removal** — acceptable for a new 1.0 where YallaClient is the sole real consumer. **Widen to 3 minors or 6 months if external consumers appear.** Policy change itself is a 1.x documentation update, not a major bump.

### Patch Cadence (Normal Part of the Model)

No fixed schedule. 1.0.x patches are expected in the first 8 weeks post-launch as OEM-tail / long-session / 3rd-party-variance bugs surface. Triage SLA:

- **P1** (crash, data loss, security): patch within 7 days.
- **P2** (significant regression, broken feature): patch within 30 days.
- **P3**: next minor.

Disclosed via `CHANGELOG.md` and GitHub Releases. **1.0.x is not a quality-failure signal — it is the remediation half of automation-primary validation.**

### Issue Triage

- **Security** (disclosure email): 72h triage acknowledgment; P0 patch within 7 days; public disclosure after patch.
- **Bugs** (GitHub Issues): best-effort. No contractual support. `CONTRIBUTING.md` is honest: "we fix what breaks Yalla or is cheap to fix; community PRs welcome for the rest."
- **Feature requests**: mostly closed as out-of-scope unless aligned with Yalla's roadmap.

### Yalla-Internal Reconciliation

YallaClient consumes from the same GitHub Packages endpoint as public adopters. Post-1.0 it pins released versions. Urgent Yalla feature work goes alpha → beta → rc → `1.N.0`; no private fork.

Emergency hotfix escape: `1.0.x-hotfix` published to GitHub Packages, rolled forward into public `1.0.x` within a week.

### Maintainership

- Islom sole maintainer at 1.0. `README.md` + `CODEOWNERS` reflect this.
- No CLA. Apache-2.0 grant implicit in PRs.
- Expand `CODEOWNERS` per module as co-maintainers appear.

### Community Surfaces

- GitHub Issues: on.
- GitHub Discussions: off at 1.0.
- Dependabot: on for SDK's own deps.
- No Discord, Slack, Telegram.

---

## 6. Risks & Residual Uncertainty

### Technical Risks

1. **iOS Compose interop in `maps`.** Bespoke `MapApplier` / `MapNode` is load-bearing. Compose MP 1.10 → 1.11 bump could break internals. **Mitigation:** pin Compose MP at 1.10.0 through 1.0; bump deliberately in 1.x.
2. **Scope-leak lifecycle fixes are breaking for YallaClient.** **Mitigation:** verify YallaClient DI at alpha-start prerequisite; migrate SDK + YallaClient in lockstep within alpha.
3. **iOS snapshot testing in KMP is less mature than Android Roborazzi.** **Mitigation:** Week-1 alpha investigation of `shot` / `snapshot-testing-kmp` / bespoke XCTest harness; accept 1% CI tolerance; document the picked tool's trade-offs.
4. **`binary-compatibility-validator` does not cover KMP Native.** **Mitigation:** `audit-api` skill is the iOS API-stability gate. Plus a version-to-version integration test that compiles a representative iOS surface against every published version (catches removed symbols the way downstream sees them). Documented in `SECURITY.md` / README: 1.0 iOS stability posture is "source-compatible across 1.x, binary-compatible best-effort, not mechanically enforced by `apiCheck`."

### Process Risks

1. **Islom review bandwidth is the critical path.** Claude produces faster than Islom reads. **Mitigation:** module-sized chunking; no new module opens until previous is merged + reviewed; review backlog surfaced each turn; calendar is 8–12 weeks not 2.
2. **ADR-005 migration breakage** silently breaks a screen in YallaClient. **Mitigation:** feature-branch both repos; YallaClient CI + live-verify emulator on every touched screen before merging.
3. **YallaClient architecture may not absorb scope-ownership contract.** If YallaClient's DI uses SDK objects as Koin singletons without explicit `close()`, the refactor cascades. **Mitigation:** alpha-start prerequisite verification; if singleton-structured, budget additional YallaClient refactor time into the calendar.

### Quality Risks

1. **Pixel-perfect goldens on iOS in CI.** **Mitigation:** tolerance is 1% on iOS (not 0%); sub-1% regressions auto-approved; above-1% triaged + re-goldens in PR; manual real-device verification via MCP for iOS where CI can't reach.
2. **Compose recomposition sanity checks may have noise tolerance.** **Mitigation:** alert on regressions >10% vs rolling baseline; dedicated runner if reliable. Full Macrobenchmark parked.

### Scope-Creep Risks

1. **Consumer-model drift.** Locked to A; revisiting reopens the spec.
2. **"While we're in there."** No new features in 1.0. Only Section 4 items.
3. **ADR-005 scope creep.** Signature change only; no Items API redesign.
4. **Expanding the `@RequiresOptIn` marker set beyond 2.** Any new marker requires an ADR.

### Post-1.0 Risks (Noted, Not Mitigated Pre-1.0)

1. **Solo-maintainer fragility.** Documented in README.
2. **Zero community adoption.** Expected and intentional.

### Residual Uncertainty

- Whether full-matrix `apiCheck` + `audit-api` baseline is green on first run, or catches historical drift needing cleanup.
- Whether iOS snapshot tooling supports 1% tolerance in CI without real devices.
- Whether YallaClient's own CI catches ADR-005 regressions.
- Whether YallaClient DI can absorb scope-ownership contract (pre-alpha verification required).

---

## 7. Working Discipline

### Live-Verify Loop

Every non-trivial SDK change follows this loop:

1. Land the change in the `yalla-sdk` working tree.
2. Publish the affected module(s) to `mavenLocal()`, OR wire YallaClient's `settings.gradle.kts` with a Gradle composite build against the local checkout.
3. Launch an Android emulator via `android-skills` + `mobile` MCPs (iOS simulator via `mobile` MCP where applicable).
4. Navigate YallaClient to surfaces that exercise the changed SDK code.
5. Visually verify pixel-accurate against design intent: dimensions, colors (`System.color.*` tokens), typography, spacing, elevation, dark-mode parity, press states. Bar is 0.1% diff on Android, 1% on iOS.
6. Mark the change done.

Trivial diffs (KDoc, tests exercising existing logic, comment fixes) skip the loop.

### Visual-Regression Tolerance Operationalized

- **Android (Roborazzi)**: 0.1% tolerance on pinned emulator image. Any diff >0.1% fails CI; requires explicit triage + goldens update in the same PR.
- **iOS (tool selected in Week 1 of alpha)**: 1% tolerance on pinned simulator image. Higher-tolerance regressions triaged + re-goldened in PR. Real-device verification via `mobile` MCP for surfaces CI can't reach reliably.
- Intentional visual changes: before/after screenshots in the PR body + updated goldens.
- Dark mode is first-class: every component has light + dark + press/disabled goldens.
- Matrix: minimum 1 Android phone + 1 Android tablet + 1 iPhone + 1 iPad, pinned OS / image versions in CI.

### Module-Sized Chunking

Islom's review bandwidth is the critical path. Claude does not open work on a new module until the previous module is:

- Merged to the SDK feature branch.
- Reviewed by Islom.
- Verified via the live-verify loop if non-trivial.

Review backlog surfaced explicitly each turn.

### No New Features in 1.0

Section 4 is exhaustive. New features land in the 1.1 backlog, shipped after 1.0.

### Semantic-Stability Pass (Gate 1 item)

Explicit review of every public surface for stability hazards:

- Every `sealed` hierarchy: does adding a subclass force downstream `when` updates? Document the "sealed addition requires `@Deprecated` warm-up" posture.
- Every `enum`: does reordering / adding values require migration? All enums use string discriminators for persistence; ordinal changes are safe but add-case is source-breaking.
- Every `@Serializable` public model: `@SerialName` on every property to decouple Kotlin name from wire name. Constructor signatures: positional-arg breakage on `data class` copy() is a real hazard.
- Every public `interface`: gated `@InternalYallaApi` or intentionally extensible? Adding an abstract method to a public interface is major-version break.
- Nullable boundaries and generic variance: any `T? = null` default in the public signature is binary-locked.

---

## Appendix A: Locked Decisions

| # | Decision | Locked at |
|---|---|---|
| 1 | Consumer model: **A** (Yalla-first, others welcome) | Brainstorm — distribution framing |
| 2 | Quality bar: Level 2 + most of Level 3, minus multi-demo apps | Brainstorm — quality bar |
| 3 | Release staging: single 1.0 train, all 11 modules + BOM together | Brainstorm — staging |
| 4 | Battle-tested = automation-primary + rapid-patch; no time-based soak | Brainstorm — gates |
| 5 | HelloTaxi sample dropped; YallaClient is integration witness | Brainstorm — samples |
| 6 | Live-verify loop applies spec-wide (every non-trivial change) | Brainstorm — discipline |
| 7 | No marketing / no demos / no external promotion | Brainstorm — distribution |
| 8 | Distribution: **GitHub Packages only** (no Sonatype, no Maven Central in 1.0) | Brainstorm — distribution |
| 9 | Group ID: `uz.yalla.sdk` preserved | Default confirmed |
| 10 | License: Apache 2.0 | Default confirmed |
| 11 | Native-speaker review handled by Islom during normal PR review | Brainstorm — external gates |
| 12 | No deadline; quality not sacrificed | User explicit |
| 13 | Maintainership: Islom sole maintainer at 1.0 | Section 5 |
| 14 | **`Either<D, E>` flipped to `Either<E, D>` (error-first) during alpha** | Post-critique revision |
| 15 | **`ApiResponse<T>` + `UnauthorizedSessionEvents` + 6 preference interfaces gated `@InternalYallaApi`** | Post-critique revision |
| 16 | **`@RequiresOptIn` marker policy: exactly 2 markers (`ExperimentalYallaApi`, `InternalYallaApi`), both `@Retention(BINARY)`** | Post-critique revision |
| 17 | **`apiCheck` enforces JVM/Android only; `audit-api` skill is iOS API-stability primary gate** | Post-critique revision |
| 18 | **Visual regression tolerance: Android 0.1%, iOS 1%** (not 0% pixel-perfect) | Post-critique revision |
| 19 | **Coverage is risk-based per module, not universal 60% line floor** | Post-critique revision |
| 20 | **Gate 3 is pre-flight dry-run (YallaClient RC integration passes before 1.0 tag)** | Post-critique revision |
| 21 | **Single `publish.yml` workflow; no alpha/release split for GitHub Packages** | Post-critique revision |

## Appendix B: Pre-Existing Repo Inconsistencies to Resolve

Flagged in `.claude/rules/publishing.md`:

1. **`gradle/libs.versions.toml`** carries `yalla-sdk = "0.0.1-alpha08"`, which is stale. Publishing reads `gradle.properties`. **Action:** delete during Gate 1 cleanup.
2. **`docs/04-PUBLISHING.md`** labels breaking changes as "Minor" while the example (`0.0.7 → 0.0.8`) is a third-segment bump. **Action:** rewrite the table during the docs pass.
3. **`values-be`** directory contains Uzbek Cyrillic text under a Belarusian locale code. **Action:** rename to `values-uz-Cyrl` during Gate 1.
4. **`@ExperimentalYallaGalleryApi`** in `media/src/commonMain/kotlin/uz/yalla/media/gallery/` is missing `@Retention(AnnotationRetention.BINARY)` per `.claude/rules/library-api.md` template. **Action:** retire the marker during Gate 1 and migrate call sites to the consolidated `ExperimentalYallaApi`.
5. **`StaticPreferencesImpl` presumed missing in earlier audit — it is in fact complete.** No action; note removed from Gate 1 work.
6. **`CarNumberState` presumed bundled in earlier audit — it is in fact a clean data class.** No action; note removed from ADR-005 scope.

## Appendix C: Out-of-Scope Work (Parked for 1.x+)

- Multi-demo apps (driver, passenger, dispatcher)
- HelloTaxi starter sample
- External paid security audit
- Maven Central distribution (re-evaluate in 1.x if external consumers appear)
- Backend-agnostic API abstraction (consumer model B or C)
- Community localization contribution flow (beyond uz/ru/en)
- Marketing / launch promotion
- GitHub Discussions
- Non-Islom maintainers
- Web KMP target
- 0%-diff iOS goldens in CI
- Full Macrobenchmark (cold-start, memory) on shared CI runners

## Appendix D: Pre-Launch Checklist (Gate 3)

Before the rc → 1.0 cut, confirm:

- [ ] **Secret scan.** No hardcoded API keys, OEM tokens, backend URLs, or test credentials anywhere in the tree. Repo is about to become publicly readable. Tools: `gitleaks`, `trufflehog`, or manual review.
- [ ] **Accessibility acceptance.** WCAG 2.1 AA on primitives + composites; Android TalkBack on latest 3 OS versions; iOS VoiceOver on latest 3 iOS versions. Documented with test evidence.
- [ ] **Dependency pin decisions.** Compose MP version locked for 1.0 branch; Kotlin version locked; Gradle version locked. Bump policy in `CONTRIBUTING.md`.
- [ ] **Release-notes template.** `CHANGELOG.md` has the 1.0.0 entry filled: API highlights, breaking-change summary (none — we're freezing), migration guide cross-reference (from `0.0.8-alpha04` to `1.0.0`).
- [ ] **Disclosure inbox.** `security@yalla.uz` (or confirmed alternative) is live; `SECURITY.md` references it.
- [ ] **CODEOWNERS.** Islom on every module; optionally co-reviewer slots for later.
- [ ] **License labeling.** GitHub repo labeled Apache-2.0; README badge in place.
- [ ] **YallaClient pre-flight.** YallaClient feature-branch pinned to rc; YallaClient CI green; zero integration regressions; live-verify emulator loop passed on every YallaClient screen that touches changed SDK surface.
- [ ] **Dokka site.** `https://royaltaxi.github.io/yalla-sdk/` renders; per-module index present.
- [ ] **`apiCheck` + `audit-api` baselines.** Both green across 11 modules; baselines committed.
- [ ] **Visual-regression goldens.** Captured for every component × (light, dark, press/disabled) × (Android phone, Android tablet, iPhone, iPad); tolerances documented in README.

Any item unchecked blocks the 1.0 tag.
