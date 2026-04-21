# yalla-sdk v1.0 Public Launch

> Spec date: 2026-04-21
> Status: Draft (brainstorm complete, awaiting Islom review)
> Scope: All 11 published yalla-sdk modules + BOM
> Impact: Public release under Apache 2.0; YallaClient continues as primary consumer

## Problem

yalla-sdk today is a Yalla-internal KMP library hosted on GitHub Packages at version `0.0.8-alpha04`. Eleven modules publish under `uz.yalla.sdk:*` coordinates. YallaClient consumes them. External visibility is zero.

The audit run on 2026-04-21 surfaced the shape of the codebase and the gap between where it is and where a Yalla-external developer would find it credible:

- **Foundation layer** (core, data, resources) is mostly solid, but the `data` module has significant debt: 6 preference implementation classes are untested, `createHttpClient` leaks an unmanaged `SupervisorJob`, `StaticPreferencesImpl` appears incomplete, redirect mappings (301–399 → `DataError.Network.Client`) are unusual, `GuestModeGuard` whitelist is hardcoded. Resources is missing `values-uz` despite Uzbek being the primary market locale.
- **Bridge layer** (design, foundation, platform) is mostly healthy. `LocationManager` and `SwitchingMapProvider` share the same scope-ownership pattern as `createHttpClient` — caller responsible for close, easy to leak. Newly-added `LocationServices` (expect/actual, 0.0.8) uses Koin `GlobalContext.get()` without guards. `platform` has 13 `expect`/`actual` pairs with near-zero test coverage across composites; `NativeSheet.onFullyExpanded`, `SystemBarColors` overload semantics, `ObserveSmsCode` on iOS, and `PlatformConfig` setup complexity all have contract asymmetries between Android and iOS.
- **UI layer** (primitives, composites) is half-migrated to the Gold Standard (`COMPONENT_STANDARD.md`). `primitives` is clean. `composites` still has 6 Items with `title: String` bundled parameters (banned), 1 `Navigable` drawer with the same issue, `EmptyStateState` bundles `Painter + String + String`. Fifteen components have zero usage in YallaClient (`AUDIT_RESULTS.md`). `Snackbar` (40 call-site usages) is untested.
- **Services** (maps, media, firebase): `SwitchingMapProvider` scope leak; map overlays duplicated between Google and MapLibre with no shared interface; iOS Compose `MapApplier` is bespoke and fragile; `YallaGallery` has a large Android/iOS parity gap (Paging3 vs PHPicker stub); Firebase lacks init guards on Android.
- **Publishing**: `org.jetbrains.kotlinx.binary-compatibility-validator` is not wired; `./gradlew apiCheck` does not exist. The `audit-api` skill is a manual fallback. `gradle/libs.versions.toml` has a stale `yalla-sdk = "0.0.1-alpha08"` entry that isn't used for publishing but exists as noise.

The SDK has been described internally as aspiring to "standard for Uzbekistan KMP market" and "Google-level quality." The current shape does not yet back those labels.

## Vision

Version 1.0 of yalla-sdk is a **publicly-readable, Apache-2.0-licensed, Yalla-opinionated KMP ride-hailing SDK** that a ride-hailing developer in Uzbekistan can clone, read, fork, and — if they accept Yalla's backend conventions and configure a GitHub personal access token — consume directly from GitHub Packages. Source is open; distribution is authenticated; promotion is zero. Primary consumer is and remains YallaClient.

Success means that a senior engineer at a comparable ride-hailing startup in the region can read the source, understand it without Yalla-internal context, and either adopt it wholesale or cherry-pick the patterns. YallaClient is the proof-of-scale. The code and the docs speak for themselves.

## Goals

1. All 11 published modules + BOM ship at `1.0.0` together on a single version train.
2. Public-API surface is frozen, documented, and enforced via `apiCheck` (binary-compatibility-validator) in CI.
3. 100% public-API KDoc coverage. Line-coverage floor of 60% per module. Visual-regression goldens captured for every component across light + dark + press states. Performance baselines tracked in CI.
4. Zero unmanaged-scope leaks in long-lived objects: `LocationManager`, `SwitchingMapProvider`, and `createHttpClient` adopt caller-owned lifecycles (`close()` contract or injected `CoroutineScope`).
5. ADR-005 string→slot migration completed for the 7 affected composites (`ListItem`, `IconItem`, `NavigableItem`, `SelectableItem`, `PricingItem`, `AddressItem`, `Navigable` drawer), plus `EmptyStateState` decomposition — all before API freeze.
6. Fifteen unused components from `AUDIT_RESULTS.md` removed from the tree.
7. Missing primary-market locale (`values-uz`) added.
8. Source published under Apache 2.0 on the public `RoyalTaxi/yalla-sdk` repo. Distribution via GitHub Packages (`https://maven.pkg.github.com/RoyalTaxi/yalla-sdk`). Dokka reference docs hosted on GitHub Pages.
9. OSS-hygiene files in place at repo root: `LICENSE`, `README.md` (public-facing), `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, `SECURITY.md`, `CHANGELOG.md`, issue + PR templates, `SUPPORT.md`, `CODEOWNERS`.
10. Live-verify discipline applied to every non-trivial change: wire the local SDK into YallaClient (composite build or `mavenLocal()`), launch an emulator via the mobile/android MCPs, pixel-perfect-verify against design intent before marking the change done.

## Non-Goals

- **Sonatype / Maven Central distribution.** Explicitly excluded from 1.0. May be revisited in 1.x if someone actually needs frictionless adoption.
- **Backend-agnostic abstraction.** Consumer model A is locked — Yalla's `ApiResponse<T>` envelope, preference schema, session-event bus, and Uzbek defaults all stay Yalla-shaped.
- **Sample apps.** No HelloTaxi, no driver/passenger/dispatcher demos. YallaClient is the integration witness.
- **External paid security audit.** `SECURITY.md` + disclosure process at 1.0; a paid audit is deferred to 1.x.
- **Community localization flow.** uz/ru/en are in; any broader i18n flow is 1.x.
- **Marketing / launch promotion.** No launch post, no Twitter, no dev.to article, no Discord, no Telegram. Passive distribution.
- **New features.** 1.0 is cleanup + hardening + publish, not feature-add.

## Consumer Model

**Model A: Yalla-first, others welcome.** Public API stays opinionated around Yalla's backend envelope, preference schema, and Uzbekistan-specific defaults. Other developers can consume the SDK if they adopt the conventions. The SDK is honest about its origin: it is not backend-neutral, and it is not trying to be Firebase.

## Quality Bar

Level 2 (foundational standard) plus most of Level 3 (ecosystem-grade), minus multi-demo apps and external paid audit:

- 100% public-API KDoc
- Line coverage ≥60% per module
- `apiCheck` wired and enforced in CI
- Visual-regression goldens captured with 0% pixel-diff tolerance on pinned emulator/simulator images
- Performance benchmarks tracked in CI (Compose recomposition counts, cold-start, map interop overhead)
- Accessibility pass on primitives + composites (`Role` semantics, 48dp/44pt touch targets, content descriptions)
- detekt + ktlint clean
- Dokka docs site published on GitHub Pages
- `SECURITY.md` + vulnerability-disclosure process live
- Published API stability guarantee with deprecation-cycle SLA

---

## 1. Mission & Scope

**Mission.** Take yalla-sdk from a Yalla-internal KMP library to a publicly-readable, Apache-2.0-licensed SDK distributed via GitHub Packages under the `uz.yalla.sdk` group ID, serving as a reference implementation for Uzbekistan KMP ride-hailing development. Primary consumer is YallaClient. Other teams may consume with a GitHub personal access token and compatible conventions; we are not optimizing for frictionless adoption.

**In 1.0 (shipped together):** `core`, `data`, `resources`, `design`, `foundation`, `platform`, `primitives`, `composites`, `maps`, `media`, `firebase`, `bom`. Coordinates: `uz.yalla.sdk:<module>:1.0.0` (plus `uz.yalla.sdk:bom:1.0.0`).

**Out of 1.0:** Sonatype / Maven Central distribution, multi-demo apps, HelloTaxi sample, external paid security audit, community localization flow, new features.

**Success signal.** All three release gates pass. GitHub Packages has `1.0.0` artifacts for all 12 artifacts (11 modules + BOM). Dokka docs live at `https://royaltaxi.github.io/yalla-sdk/`. YallaClient's `gradle/libs.versions.toml` pins `uz.yalla.sdk = "1.0.0"` and YallaClient's CI passes. First 1.0.x patch cadence established.

---

## 2. Release Gates & Cadence

### Version Ladder

Single train. All 11 modules + BOM move together.

| Phase | Visibility | API rules |
|---|---|---|
| `0.x-alphaNN` | Yalla-only (GitHub Packages, as today) | Breaking changes allowed. Feature work, cleanup, refactors. |
| `0.x-betaNN` | Yalla-only (GitHub Packages) | API freeze. Additive only. `apiCheck` enforced in CI. |
| `1.0.0-rcN` | Yalla-only (GitHub Packages) | Bug fixes only. No new APIs, no API changes. |
| `1.0.0` | Public (Apache-2.0 source, artifacts on GitHub Packages behind auth) | Frozen. Breaking changes require major-version bump + deprecation cycle. |
| `1.0.N` | Public | Patch fixes only. Non-breaking. |
| `1.1.0` | Public | Additive only. New APIs OK, no removals. Deprecations OK. |
| `2.0.0` | Public | Breaking allowed; deprecation cycle required. |

### Gate 1 — alpha → beta

All of:

- 15 unused components removed (per `AUDIT_RESULTS.md`).
- ADR-005 migration done (7 affected composites + `EmptyStateState` decomposition).
- 3 scope leaks fixed (`LocationManager`, `SwitchingMapProvider`, `createHttpClient`).
- `StaticPreferencesImpl` completed (or the `StaticPreferences` contract removed if dead).
- `values-uz/strings.xml` added. Islom self-reviews translations.
- 6 preference-impl classes have unit tests.
- Line coverage ≥60% per module.
- 100% public-API KDoc.
- `apiCheck` wired and green.
- Visual-regression goldens captured (light + dark + press/disabled states, pinned emulator/simulator images).
- Performance baseline captured and tracked.
- detekt + ktlint clean in CI.
- Accessibility pass done (`Role`, touch targets, content descriptions on every interactive component).

### Gate 2 — beta → rc

All Gate 1 gates hold, plus:

- Full E2E automation matrix green on the mobile/android MCPs across the target device set (defined during alpha).
- No API changes for ≥3 consecutive commits on the beta branch.
- Dokka docs site live on GitHub Pages.

### Gate 3 — rc → 1.0

- `SECURITY.md` + vulnerability-disclosure process live (disclosure email, 72h triage SLA, P0 patch 7 days, P1 patch 30 days).
- `LICENSE` (Apache 2.0) in place; repo licensed-labeled on GitHub.
- YallaClient's `gradle/libs.versions.toml` pins `uz.yalla.sdk = "1.0.0"`; YallaClient's full CI passes consuming 1.0.0 artifacts from GitHub Packages.
- Zero P1 / P2 regressions open against the SDK.

### Gate Failure = Backward Motion

A P1 or public-API regression in rc sends us back to beta. We do not ship around regressions.

### No Time-Based Production Soak

Per the battle-tested-via-exhaustive-automation model: residual OEM-tail / long-duration / 3rd-party-variance bugs caught post-1.0 ship as 1.0.x patches with rapid cadence. No staged rollout, no 4-week soak window. The three classes of bugs automation does not catch well (OEM fragmentation, long-duration resource issues, 3rd-party SDK variance under field conditions) are expected; we eliminate as many as possible via exhaustive automation and patch the rest fast.

---

## 3. Cross-Cutting Infrastructure

### Build + Publish

- **Wire `org.jetbrains.kotlinx.binary-compatibility-validator`** in the root `build.gradle.kts`. Generate `api/` baseline files for every published module. `apiCheck` becomes a CI gate; the manual `audit-api` skill becomes a convenience only.
- **Keep existing `.github/workflows/publish.yml`** — already publishes to GitHub Packages. No Sonatype, no Maven Central, no GPG signing, no DNS coordination.
- **Split the publish workflow** for clarity: `publish-alpha.yml` (push to `main` triggers alpha/beta/rc publishes at the current unified version), `publish-release.yml` (triggered on `v1.*.*` tag, publishes the same pipeline but with `1.x.y` coordinates).
- **POM metadata** — add `name`, `description`, `url`, `licenses`, `scm`, `developers` to the convention plugin's publish configuration. Not strictly required by GitHub Packages but valuable for Dokka, tooling, and any future mirroring.
- **Clean up stale catalog entry** — remove `yalla-sdk = "0.0.1-alpha08"` from `gradle/libs.versions.toml` (per known inconsistency flagged in `.claude/rules/publishing.md`).

### CI

- **`ci.yml`** — runs on every PR and push: `ktlintCheck`, `detekt`, `apiCheck`, `test` (unit + KMP common), Roborazzi visual regression on Android, iOS snapshot equivalents (library selection is a Week-1 alpha investigation task — candidates: `shot`, `snapshot-testing-kmp`, or a bespoke XCTest + UIKitView capture harness), Macrobenchmark on Android, Compose recomposition sanity checks.
- **`benchmarks.yml`** — scheduled + manual dispatch. Cold-start, recomposition counts, map load. Posts trend to GitHub Actions summary. Runs on a dedicated runner label to reduce noise.

### Documentation

- **Dokka v2** configured at root level with per-module `MODULE.md` aggregation.
- **GitHub Pages** target: `https://royaltaxi.github.io/yalla-sdk/`. Automated deploy on release publish.
- **`docs/` rewrite for external audience**: rewrite `00-START-HERE.md` from Yalla-internal onboarding to a public getting-started guide. Keep `01-ARCHITECTURE.md`, `02-COMPONENT-GUIDE.md`, `03-PATTERNS.md`, `04-PUBLISHING.md`, `05-TESTING.md`, `06-DECISIONS.md`. Add `MIGRATION.md` (empty at 1.0; populated at 1.x and 2.x).
- **`docs/obsidian/`** excluded from Dokka + Pages output (Yalla-internal session logs, not part of the public SDK story).

### Open-Source Meta (repo root)

- `LICENSE` — Apache 2.0 text, dated.
- `README.md` — public-facing rewrite: what this is, how to add (including the GitHub token auth snippet), link to docs, one minimal code snippet per module surface, coverage / license / release badges.
- `CONTRIBUTING.md` — contribution flow, style, commit convention, PR checklist.
- `CODE_OF_CONDUCT.md` — Contributor Covenant.
- `SECURITY.md` — disclosure email (provision `security@yalla.uz` or confirm existing Yalla security inbox during Gate 3 pre-work), 72h triage, P0 patch 7d / P1 patch 30d.
- `CHANGELOG.md` — Keep-a-Changelog format. Populated back to 0.x where tractable; fully populated for 1.0 onward.
- `.github/ISSUE_TEMPLATE/` — bug, feature, docs issue templates.
- `.github/PULL_REQUEST_TEMPLATE.md` — checklist (tests, apiCheck, docs, visual-verified).
- `SUPPORT.md` — link to docs, GitHub Issues stance, honest "best-effort, no contractual support" framing.
- `CODEOWNERS` — Islom owns all modules at 1.0; expand as co-maintainers join.

### i18n

- Add `values-uz/strings.xml` (Uzbek Latin). Islom self-reviews translations during normal PR review.
- Audit `values-be` — Belarusian locale present but target market doesn't need it. Delete if leftover; if it was intended as a Karakalpak placeholder, rename correctly.

### Tooling Tightening

- **detekt**: enable the `detekt-formatting` ruleset; audit existing `@Suppress(...)` annotations at Gate 1 (every suppression requires a one-line inline justification; unjustified suppressions get resolved or escalated before beta).
- **ktlint**: already `ktlint_official`. No change.
- **Spotless**: already configured. No change.

---

## 4. Per-Module Work Scope

Sizing legend: **XS** (hours), **S** (~1 day), **M** (~2–3 days), **L** (~4–7 days), **XL** (1+ week). Assumes Claude producing + Islom reviewing at pace; bottleneck is Islom's review throughput.

### core — Size: S

- Add `@Serializable` where domain models need to travel over Ktor (audit current usage; likely `Order`, `Address`, `PaymentCard`, `Profile`).
- Add `getOrNull()`, `getOrThrow()`, `fold()` extensions to `Either` (non-breaking additions).
- Tests for untested domain models (`Order`, `Address`, `Route`, `PaymentCard`, `Client`) and the 6 preference-contract interfaces.
- Resolve `StaticPreferences` contract: complete it or remove it if dead.
- Declare `@RequiresOptIn` markers `ExperimentalYallaApi` and `InternalYallaApi` in `core`. Annotate any genuinely experimental surface across all modules.

### data — Size: L

- **Complete `StaticPreferencesImpl`** (or remove the contract).
- **Fix `createHttpClient` scope ownership** — replace the unmanaged `SupervisorJob` with a caller-owned lifecycle. Design choice: `HttpClientFactory.create(scope: CoroutineScope)` or an `install(scope)` contract wired through Koin.
- Unit tests for all 6 preference-impl classes (using in-memory DataStore + fake Settings).
- Review redirect handling: 301–399 mapped to `DataError.Network.Client` is unusual. Either remap to `Server` / dedicated `Redirect` type, or keep with explicit KDoc justifying.
- Make `GuestModeGuard` whitelist configurable via `NetworkConfig` (current behavior: hardcoded list of 6 endpoints).
- Integration tests for `createHttpClient`: 401 handling, retry + backoff, guest mode, connection-error surfacing, request timeout.

### resources — Size: M

- Add `values-uz/strings.xml`. Islom self-reviews translations.
- Audit `values-be`. Delete if leftover, fix if intended.
- Write `MODULE.md` (currently missing).
- Harden Valkyrie task ordering: replace regex name-filter with a proper `dependsOn` graph.

### design — Size: XS

- Add KDoc to the platform-specific `Font.android.kt` / `Font.ios.kt` actuals.
- Add `FontScheme` equality test.

### foundation — Size: M

- **Fix `LocationManager` scope ownership** — same pattern as `createHttpClient`. Inject scope or make `close()` contract explicit.
- Add error handling to `LocationServices.openLocationSettings()` + `.isLocationServicesEnabled()` on Android (Koin NPE risk from `GlobalContext.get()`) + iOS.
- Guard unproduction-ready language options (`LanguageOption.UzbekCyrillic`, `LanguageOption.English`) — either remove from the enumerable list or gate behind `@RequiresOptIn`.
- Tests for `LocationManager` lifecycle, `ObserveAsEvents`, `ChangeLanguage`.

### platform — Size: L

- **Add tests for every `expect`/`actual` pair** — 13 expect declarations exist; only 5 test files today. Each of `NativeSheet`, `NativeSwitch`, `NativeWheelDatePicker`, `NativeCircleIconButton`, `NativeLoadingIndicator`, `SystemBarColors`, `rememberHapticController`, `rememberInAppBrowser`, `getAppSignature`, `ObserveSmsCode`, `statusBarHeight`, `rememberAppUpdateState` gets at least behavioral Android tests (click fires, state toggles, semantics correct) and iOS tests where snapshot infra supports.
- Resolve `NativeSheet.onFullyExpanded` Android/iOS contract asymmetry — either fire on Android via `ModalBottomSheet` state changes or remove from the expect signature and make it iOS-only via a side channel.
- Resolve `SystemBarColors` dual overload confusion — pick one canonical shape, document.
- Resolve `ObserveSmsCode` iOS stub — implement via `UITextContentType.oneTimeCode`, or mark the whole expect Android-only and document clearly.
- Resolve `PlatformConfig` asymmetry — `IosPlatformConfig` is factory-heavy, `AndroidPlatformConfig` is empty. Either make Android take an equivalent config object (even if empty today, for future-proofing), or document that iOS requires explicit factory injection.
- Add KDoc on both `statusBarHeight()` actuals explaining the computation.

### primitives — Size: M

- Add tests for the 9+ untested components: `SecondaryButton`, `TextButton`, `BottomSheetButton`, `GenderButton`, `NavigationButton`, `IconButton`, `SensitiveButton`, `TopBar`, `LargeTopBar`, `LoadingIndicator`, `StripedProgressBar`, `SplashOverlay`, `LocationPin`, `SearchPin`, `LoadingDialog`. Priority: `TopBar` (5 YallaClient usages) + `LargeTopBar` (8 usages) first.
- Capture visual-regression goldens for every primitive (light + dark + press/disabled states).
- Accessibility: confirm every interactive primitive declares `Role`, meets 48dp/44pt touch targets, has content descriptions on icons.

### composites — Size: L

- **ADR-005 string→slot migration** for the 6 Items: `ListItem`, `IconItem`, `NavigableItem`, `SelectableItem`, `PricingItem`, `AddressItem`. Breaking. Requires YallaClient call-site updates in lockstep.
- **`Navigable` drawer** string→slot migration (16+ YallaClient usages).
- **`EmptyStateState` decomposition** — unbundle `Painter + String + String` into proper slots.
- `CarNumberState` — decide: unbundle or leave with `@RequiresOptIn`. Lower priority (display-only).
- Delete 3 still-present unused composites: `ContentCard`, `AddressCard`, `RouteView`.
- Tests for `Snackbar` + `SnackbarHost` (40 untested usages), `Navigable` (16 untested), `EmptyState` (3 untested), `CarNumber`, `SectionBackground`.
- Visual-regression goldens on every composite, light + dark.

### maps — Size: L

- **Fix `SwitchingMapProvider` scope ownership** — same pattern as `createHttpClient` / `LocationManager`.
- Introduce a shared interface for overlay components (`RouteLayer`, `LocationsLayer`, `LocationIndicator`) instead of parallel implementations under `provider/google/component/` and `provider/libre/component/`. Reduces maintenance burden and enforces Google/Libre parity.
- Tests for provider switching (preference change triggers correct provider instance).
- Tests for `MapController` core ops (camera move, marker add/remove, polyline draw) against a fake provider.
- KDoc the iOS `MapApplier` / `MapNode` bespoke machinery — load-bearing, needs context for future maintainers.
- Stress-test iOS `GMSMapView` interop for UIKitView lifecycle leaks.

### media — Size: L

- **Resolve `YallaGallery` Android/iOS parity gap** — Android uses Paging3 + MediaStore; iOS is a thin `PHPicker` wrapper. Decision: narrow the common API to what both platforms can deliver identically, move rich features behind `@ExperimentalYallaGalleryApi` (keep the existing marker). Honest about the parity limitation; surfaces the richer Android features to callers who opt in.
- EXIF orientation handling in camera capture (currently stripped on Android).
- Make the CameraX executor per-instance, not module-global.
- Tests for compression correctness (output size ≤ target per preset across input sizes, output is a valid JPEG, iteration count bounded).
- Tests for picker result handling (happy path, cancel, permission-denied).

### firebase — Size: S

- Add Android init guard: fail loudly if `google-services.json` is missing or `FirebaseApp.getInstance()` returns null. Currently silent.
- Add guard on lazy property access — calling `analytics` / `crashlytics` / `messaging` before `YallaFirebase.initialize()` should throw a clear `IllegalStateException`, not NPE.
- Tests for analytics event logging (via fake delegate), crashlytics exception recording, messaging token retrieval.
- Prominent KDoc + MODULE.md on the iOS Swift-side `FirebaseApp.configure()` requirement.

### bom — Size: XS

- Verify every published module appears in the `constraints` block.
- Otherwise unchanged; auto-tracks the unified version.

### docs — Size: M (first-class deliverable)

Elevated to first-class because there is no sample app.

- Rewrite `00-START-HERE.md` for external audience (currently Yalla-internal onboarding).
- Getting-started: cold-add-to-a-new-project walkthrough, minimal code snippet per module surface (theme setup, map rendering, OTP sheet, camera capture).
- Tighten `02-COMPONENT-GUIDE.md` post-ADR-005 migration.
- Write `MIGRATION.md` scaffold (populated at 1.x+).
- Audit `06-DECISIONS.md`; formalize any open ADRs.
- Dokka landing-page content and per-module index pages.

### Rollup

| Module | Size | Top risks |
|---|---|---|
| core | S | Stable; tests gap |
| data | L | Scope-ownership refactor + missing impl + 0 preference-impl tests |
| resources | M | Uzbek locale quality |
| design | XS | — |
| foundation | M | Scope-ownership + language-option guards |
| platform | L | 13 expect/actual pairs, weak tests, 3 contract asymmetries |
| primitives | M | Test coverage + visual goldens |
| composites | L | ADR-005 breaking migration (7 components) + deletions + goldens |
| maps | L | Scope-ownership + iOS interop audit + overlay interface |
| media | L | Gallery parity decision + EXIF + per-instance executor |
| firebase | S | Init guards only |
| bom | XS | — |
| docs | M | Getting-started rewrite (load-bearing without sample) |

**Aggregate raw sizing:** 35–45 person-days of focused work before review roundtrips. Parallelizable across modules; real bottlenecks are (a) Islom's review bandwidth and (b) the ADR-005 migration requiring SDK + YallaClient in lockstep.

---

## 5. Post-1.0 Operating Model

### Versioning Policy (strict SemVer)

- **1.0.N (patch)** — bug fixes only. No new public API. Binary- and source-compatible with every `1.0.*` before it. `apiCheck` must be clean.
- **1.N.0 (minor)** — additive only. New APIs, new modules, new components allowed. No removals, no signature changes. Deprecations OK with `@Deprecated(level = WARNING)`.
- **2.0.0 (major)** — breaking allowed. Requires an ADR in `docs/06-DECISIONS.md` and a `MIGRATION.md` entry.

### Deprecation Cycle

Deprecate in `N.x` with `@Deprecated(level = WARNING, replaceWith = ...)`. Escalate to `ERROR` one minor later if forcing migration is warranted. Remove only in `(N+1).0`. Minimum one minor version between deprecation and removal.

### Patch Cadence

No fixed schedule. Triage SLA:

- **P1** (crash, data loss, security): patch within 7 days.
- **P2** (significant regression, broken feature): patch within 30 days.
- **P3**: next minor.

Disclosed via `CHANGELOG.md` and GitHub Releases.

### Issue Triage

- **Security** (disclosure email): 72h triage acknowledgment; P0 patch within 7 days; public disclosure after patch.
- **Bugs** (GitHub Issues): best-effort. No contractual support. Honest stance in `CONTRIBUTING.md`: "we fix what breaks Yalla or is cheap to fix; community PRs welcome for the rest."
- **Feature requests**: mostly closed as out-of-scope unless aligned with Yalla's roadmap. Documented: "open source does not mean open roadmap."

### Yalla-Internal Reconciliation

YallaClient consumes from the same GitHub Packages endpoint as public adopters. Post-1.0 it pins released versions, not snapshots. Urgent Yalla feature work lands on `main` → alpha/beta/rc cadence → ships as `1.N.0`. No private fork, no Yalla-only branch.

Emergency escape hatch: `1.0.x-hotfix` published to GitHub Packages (identical endpoint), rolled forward into a public `1.0.x` patch within one week. Documented in `docs/04-PUBLISHING.md`.

### Maintainership

- Islom is the sole maintainer at 1.0. Documented in `README.md` + `CODEOWNERS`.
- No CLA. Contributors retain copyright; PRs merged under Apache 2.0 grant. Documented in `CONTRIBUTING.md`.
- Expand `CODEOWNERS` per module as co-maintainers appear.

### Community Surfaces

- GitHub Issues: on.
- GitHub Discussions: off at 1.0 (no bandwidth). Enable when a co-maintainer exists.
- Dependabot: on for the SDK's own dependencies.
- No Discord, Slack, Telegram. Consistent with passive-distribution stance.

---

## 6. Risks & Residual Uncertainty

### Technical Risks

1. **iOS Compose interop in `maps`.** The bespoke `MapApplier` / `MapNode` machinery is load-bearing. A Compose Multiplatform 1.10 → 1.11 bump could break internals and cost weeks. **Mitigation:** pin Compose MP at 1.10.0 through 1.0; bump deliberately in a 1.x minor with full emulator/simulator re-run.
2. **Scope-leak lifecycle fixes are breaking for YallaClient.** `LocationManager`, `SwitchingMapProvider`, `createHttpClient` all change contract from init-and-forget to caller-owned. **Mitigation:** migrate SDK + YallaClient in lockstep within alpha, before API freeze.
3. **iOS snapshot testing in KMP is less mature than Android Roborazzi.** Options: `shot`, `snapshot-testing-kmp`, rolling our own via XCTest + UIKitView capture. **Mitigation:** investigate during week 1 of alpha. If no clean KMP option exists, accept iOS at higher tolerance (~0.5%) for 1.0 and upgrade in 1.x.
4. **`binary-compatibility-validator` coverage on KMP Native targets** has historical gaps. **Mitigation:** verify coverage during alpha. If Native isn't covered, keep the `audit-api` skill as a supplement for iOS API surface.

### Process Risks

1. **Islom's review bandwidth is the real bottleneck.** Claude can produce 35–45 person-days of code faster than Islom can read it. Unreviewed Claude-generated tests equal noise, not coverage. **Mitigation:** queue work in module-sized chunks; Claude does not open a new module until the previous one is reviewed + merged; review backlog surfaced explicitly each turn.
2. **ADR-005 migration breakage.** The string→slot refactor touches 10+ YallaClient call sites per audit. A single wrong call-site silently breaks a screen. **Mitigation:** feature-branch the migration in both repos, run YallaClient's full CI + the live-verify emulator loop on every touched screen before merging.

### Quality Risks

1. **Pixel-perfect bar may slip on first pass.** Subpixel rendering differences across emulator images, font hinting variations, anti-aliasing quirks. **Mitigation:** "pixel-perfect" operationalized as *0% diff against goldens captured on a pinned emulator/simulator image in CI*. Design deviations get explicit goldens updates in the same PR.
2. **Perf benchmarks noisy on shared CI runners.** **Mitigation:** Macrobenchmark on a dedicated runner label. Alert on regressions >10% vs rolling baseline, not strict floors.

### Scope-Creep Risks

1. **Consumer-model drift.** If mid-stream we decide to abstract Yalla's envelope after all, scope roughly 2x's. **Mitigation:** spec locks consumer model A; revisiting reopens the spec.
2. **"While we're in there" temptation.** **Mitigation:** no new features in 1.0. Only the items in Section 4.
3. **ADR-005 scope creep.** **Mitigation:** signature change only, no behavior change, no Items-API redesign.

### Post-1.0 Risks (Noted, Not Mitigated Pre-1.0)

1. **Solo-maintainer fragility.** If Islom loses availability, the SDK stalls. Honest limitation documented in README.
2. **Zero community adoption.** Expected under passive-distribution stance. Design choice, not risk.

### Residual Uncertainty

- Whether the full 11-module `apiCheck` baseline is green on first run, or the tool catches historical API drift needing retroactive cleanup. Find out on day 1 when we first run it.
- Whether pixel-perfect on iOS is achievable in CI without real devices. May force an architectural change (manual real-device verification via MCP for iOS goldens, automated CI only for Android).
- Whether YallaClient's own CI is strong enough to catch ADR-005 regressions. If not, part of the migration work becomes hardening YallaClient's test suite.

---

## 7. Working Discipline

### Live-Verify Loop

Every non-trivial SDK change follows this loop:

1. Land the change in the `yalla-sdk` working tree.
2. Publish the affected module(s) to `mavenLocal()` at a dev version, OR wire YallaClient's `settings.gradle.kts` with a Gradle composite build against the local yalla-sdk checkout.
3. Launch an Android emulator via the `android-skills` + `mobile` MCPs (iOS simulator via the `mobile` MCP where applicable).
4. Navigate YallaClient to the surfaces that exercise the changed SDK code.
5. Visually verify pixel-perfect against design intent: dimensions, colors (against `System.color.*` tokens), typography, spacing, elevation, dark-mode parity, press states.
6. Only then mark the change done and proceed.

Trivial diffs (KDoc, unit tests exercising existing logic, comment fixes) skip the loop.

### Pixel-Perfect Standard

- Primitives + composites: goldens captured at Gate 1. Tolerance **0% pixel diff** on stable goldens. Any diff fails CI, requires explicit triage and a goldens update in the same PR.
- Intentional visual changes require before/after screenshots in the PR body plus updated goldens.
- Dark mode is first-class: every component has light + dark + press/disabled state goldens.
- Emulator/simulator matrix: minimum 1 Android phone + 1 Android tablet + iPhone + iPad. OS/image versions pinned in CI.

### Module-Sized Chunking

Islom's review bandwidth is the critical path. Claude does not open work on a new module until the previous module is:

- Merged to the SDK feature branch.
- Reviewed by Islom.
- Verified via the live-verify loop if non-trivial.

Review backlog surfaced explicitly each turn.

### No New Features in 1.0

Section 4 is exhaustive. Anything not listed is out of scope for 1.0. New features are tracked for the 1.1 backlog and shipped after 1.0.

---

## Appendix A: Locked Decisions

| # | Decision | Locked at |
|---|---|---|
| 1 | Consumer model: **A** (Yalla-first, others welcome) | Brainstorm — distribution framing |
| 2 | Quality bar: Level 2 + most of Level 3, minus multi-demo apps | Brainstorm — quality bar |
| 3 | Release staging: single 1.0 train, all 11 modules + BOM together | Brainstorm — staging |
| 4 | Battle-tested = automation-primary, not production soak | Brainstorm — gates |
| 5 | HelloTaxi sample dropped; YallaClient is integration witness | Brainstorm — samples |
| 6 | Live-verify loop applies spec-wide (every non-trivial change) | Brainstorm — discipline |
| 7 | No marketing / no demos / no external promotion | Brainstorm — distribution |
| 8 | Distribution: **GitHub Packages only** (no Sonatype, no Maven Central in 1.0) | Brainstorm — distribution |
| 9 | Group ID: `uz.yalla.sdk` preserved | Default confirmed |
| 10 | License: Apache 2.0 | Default confirmed |
| 11 | Native-speaker review handled by Islom during normal PR review | Brainstorm — external gates |
| 12 | No deadline; quality not sacrificed | User explicit |
| 13 | Maintainership: Islom sole maintainer at 1.0 | Section 5 |

## Appendix B: Pre-Existing Repo Inconsistencies to Resolve

Flagged in `.claude/rules/publishing.md`:

1. **`gradle/libs.versions.toml`** carries `yalla-sdk = "0.0.1-alpha08"`, which is stale. Publishing reads `gradle.properties` → `yalla.sdk.version` instead. A handful of `[libraries]` entries reference it but are dead for actual publishing. **Action:** delete during Gate 1 cleanup.
2. **`docs/04-PUBLISHING.md`** labels breaking changes as "Minor" while the example (`0.0.7 → 0.0.8`) is a third-segment bump (SemVer Patch). Documentation inconsistency. **Action:** rewrite the table during the docs rewrite pass.

## Appendix C: Out-of-Scope Work (Parked for 1.x+)

- Multi-demo apps (driver, passenger, dispatcher)
- HelloTaxi starter sample
- External paid security audit
- Maven Central distribution
- Backend-agnostic API abstraction (consumer model B or C)
- Community localization contribution flow
- Marketing / launch promotion
- GitHub Discussions
- Non-Islom maintainers
- Web KMP target
- Visual-regression tooling for iOS at 0% tolerance (if alpha investigation shows it is not achievable in CI without real-device access)
