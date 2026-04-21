# Phase 1 — Investigation Notes

> Created: 2026-04-21. Branch: `feature/v1-phase1-foundation`. Base commit: `930509c`.

## Baseline (Task 1)

- `ktlintCheck`: **FAIL** (pre-existing)
- `detekt`: **PASS**
- `test`: **UNRUNNABLE** (see notes below)

Any regressions during Phase 1 must be measured against this baseline.

### `ktlintCheck` — pre-existing failures

Two tasks fail. Both are style-only violations. No code logic changes are required to fix them; `./gradlew ktlintFormat` will resolve them in one shot. Phase 1 must not regress them further; a later task in this phase should run `ktlintFormat` and re-baseline.

| Task | Violations | Rules |
|---|---|---|
| `:core:ktlintCommonMainSourceSetCheck` | 7 | `standard:no-empty-first-line-in-class-body` (7) |
| `:composites:ktlintCommonTestSourceSetCheck` | 123 | `standard:multiline-expression-wrapping` (67), `standard:no-empty-first-line-in-class-body` (56) |

Files affected in `:core` (all under `core/src/commonMain/kotlin/uz/yalla/core/contract/`):
- `location/LocationProvider.kt`
- `preferences/ConfigPreferences.kt`
- `preferences/InterfacePreferences.kt`
- `preferences/PositionPreferences.kt`
- `preferences/SessionPreferences.kt`
- `preferences/StaticPreferences.kt`
- `preferences/UserPreferences.kt`

Files affected in `:composites` are all test files under `composites/src/commonTest/kotlin/uz/yalla/composites/` (card/, item/, sheet/ subdirectories).

### `test` — unrunnable on this host

- The spec calls for `./gradlew test -q`. There is **no `test` task at the root project** (KMP doesn't auto-create one).
- The canonical aggregator per `docs/05-TESTING.md` is `./gradlew allTests`.
- `allTests` fails on this host at `:maps:xcodeVersion` with `MissingXcodeException`. The host has only Command Line Tools (`/Library/Developer/CommandLineTools`), not a full Xcode install, so Kotlin/Native iOS simulator test targets cannot be linked.
- Android unit test tasks (`testDebugUnitTest`) aren't emitted by these modules either; tests run via `iosSimulatorArm64Test` + `testDebugUnitTest` aggregated by `allTests`.
- **Net effect:** no test count can be captured on this host. CI has Xcode and should be the authoritative source of the test-count baseline. Later in Phase 1, once CI is wired up, the baseline row should be updated from the CI run.

### Remediation applied (post-37f62b1)

The `ktlintCheck` failure in the baseline was larger than the first pass reported: 901 violations across 21 rules, 535 residual after `ktlintFormat` (oscillation loop between Compose-hostile rules). Per ADR-009 (see `docs/06-DECISIONS.md`), disabled five conflict rules in `.editorconfig`:
- `multiline-expression-wrapping`
- `function-signature`
- `class-signature`
- `argument-list-wrapping`
- `no-empty-first-line-in-class-body`

Post-remediation: `./gradlew ktlintFormat && ./gradlew ktlintCheck` PASS.

**Test runner note:** `./gradlew test` does not exist on this KMP project (no JVM target declared). The canonical task is `./gradlew allTests`, which requires full Xcode (not just Command Line Tools). CI (macos-latest runners) will execute `allTests`; local verification on non-Xcode dev machines is limited to `./gradlew check` or per-module Android unit tests.

## Alpha-Start Investigations (Task 2)

Three research questions that must resolve before Phase 2 starts. Each one tests an assumption the spec made about how easily alpha work will land.

### A. YallaClient DI audit

Searched YallaClient (`/Users/islom/StudioProjects/YallaClient`, branch `main`, HEAD `162a536e9`) for current wiring of the three SDK objects whose contract Phase 2 plans to change. Key findings:

| SDK object | Current YallaClient wiring | Scope ownership today | Refactor impact |
|---|---|---|---|
| `LocationManager` | `single { LocationManager(locationTracker = get()) }` in `composeApp/src/commonMain/.../di/LocationDiModule.kt:15`. Consumed via `by inject()` from ViewModels (Home, Overlay, MapSheet, Search, Places, Onboarding) and via `koinInject()` in one Compose route. | Koin global-scope singleton, lives for the process lifetime. The SDK class already exposes `close()` (documented at `foundation/src/commonMain/.../LocationManager.kt:50`), but YallaClient never calls it. Internal `CoroutineScope(SupervisorJob() + Dispatchers.Main)` leaks when the process dies. | Koin module edit only. Add a `single { ... }` with a matching teardown hook — Koin 3.5+ supports `onClose { it.close() }` on module declarations, or YallaClient can call `KoinApplication.close()` on app shutdown (Android `Application.onTerminate`, iOS `applicationWillTerminate`). No DI graph restructure needed. |
| `SwitchingMapProvider` | **Wired inside yalla-sdk, not YallaClient.** `maps/src/commonMain/.../di/MapModule.kt:22` declares `single<MapProvider> { SwitchingMapProvider(...) }`. YallaClient pulls this via `uz.yalla.maps.di.mapModule` and only customizes `MapDependencies`. The class already has `close()` (verified at `maps/src/commonMain/.../provider/SwitchingMapProvider.kt:98`), but no one calls it. `SDK_STATUS.md:78` claims "close() added" — true for the class, false for the Koin registration. | Koin singleton inside the SDK's own module, process lifetime. Internal `scope` in `SwitchingMapProvider` leaks on teardown. | **Zero YallaClient-side work** — the Koin registration lives in yalla-sdk's `:maps` module, so Phase 2 just edits `mapModule` (either `onClose` in Koin 3.5+ or rewrite registration to accept a `CoroutineScope` parameter). YallaClient consumers get the fix automatically on SDK bump. |
| `HttpClient` (via `createHttpClient`) | Three qualified `single<HttpClient>(named(PHP/GO/GO_V2))` in `composeApp/src/commonMain/.../di/DataModule.kt:35–78`. Consumed by repository/data-source modules via `by inject()`. | Koin global-scope singleton, process lifetime. `createHttpClient` (at `data/src/commonMain/.../HttpClientFactory.kt:68`) creates a private `CoroutineScope(ioDispatcher + SupervisorJob())` for header caches — **this scope is completely unreachable from the return value**. Ktor's `HttpClient.close()` shuts down the engine but does not cancel that private scope. Triple leak: one per backend qualifier. | **SDK-side contract change required.** Options: (a) return a new `HttpClientHandle` type bundling `client + Job` with a `close()` that cancels both — breaking for call sites; (b) accept an injected `CoroutineScope` parameter — less breaking (Kotlin default param, but semantics change). Either way YallaClient's three `single<HttpClient>(...)` blocks need a `onClose { it.close() }` added. Mechanical edit, not a restructure. |

**Verdict:** `feasible-as-planned`.

**Notes:**
- YallaClient already demonstrates caller-owned scope discipline via `HomeScopeHolder.start()`/`.close()` (visible at `AppScreenProvider.kt:358,363,370` and `iosMain/ScreenFactory.kt:133`). The pattern Phase 2 wants is idiomatic in this codebase — not new territory.
- One pleasant surprise: `SwitchingMapProvider`'s Koin wiring lives inside yalla-sdk itself (`maps/.../MapModule.kt`), so the refactor for that object is 100% SDK-internal. YallaClient doesn't need a line changed for item 2 in the table.
- One gotcha: `createHttpClient` currently has no handle for its internal scope. If we go the `CoroutineScope` parameter route, existing call sites at `DataModule.kt:36,51,66` each need `scope = get()` wired in — trivial if a single `scope` is reused, trickier if we want per-backend isolation. Recommend single shared `scope` to keep the downstream patch small.
- Investigation C in the spec (Section 4 of `2026-04-21-yalla-sdk-v1-launch-design.md`) assumes YallaClient DI can absorb the change in lockstep. Confirmed. No redesign of Phase 2 required.

### B. binary-compatibility-validator coverage

Checked <https://github.com/Kotlin/binary-compatibility-validator> (README + releases page).

- Latest stable version: `0.18.1` (released 2025-07-09, per GitHub releases).
- JVM/Android target support: YES (default behavior, no opt-in).
- KMP Native target support (Klib): YES — via experimental Klib ABI validation opt-in. Apple-target compilation is only performed on Apple hosts; on Linux/Windows runners the validator skips unsupported targets by default (or fails with `strictValidation = true`).
- Opt-in config:

  ```kotlin
  // root build.gradle.kts
  apiValidation {
      @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
      klib {
          enabled = true
          // strictValidation = true  // fail on Linux/Windows runners that can't compile Apple targets
      }
  }
  ```

**Decision:**
- [ ] Use the plugin for JVM/Android only; `audit-api` skill handles iOS.
- [x] Use the plugin for JVM/Android + KMP Native via Klib mode; `audit-api` skill becomes advisory only.

**Caveat on the decision:** the Klib mode is still flagged `@ExperimentalBCVApi` as of 0.18.1, and Apple-target Klib dumps only generate on macOS hosts. CI's publish workflow already runs on macOS (per `.github/workflows/publish.yml`), so this is compatible — but a Linux-only contributor or CI job can't generate iOS dumps. Acceptable: the publish path is the one that matters.

**Follow-up for the spec (do NOT apply in this task):** The v1-launch spec's Section 3 and Section 6 assume option 1. Both sections should be upgraded to reference Klib-mode coverage in a follow-up commit — with `audit-api` remaining as advisory/fallback when the plugin is unavailable (Linux-only dev boxes).

#### Followup — Task 3 wiring outcome (2026-04-21)

Wired `binary-compatibility-validator 0.18.1` at commit `9031d23`. Klib mode enabled as decided. Concrete outcome:

- **11 `.klib.api` baselines generated** — one per published module, covering Native targets (iosArm64, iosSimulatorArm64). Because `commonMain` compiles into the Native targets, every common-source public declaration also lands in the Klib dump. Common-surface regressions WILL be caught.
- **0 `.api` (JVM/Android) baselines generated** — BCV 0.18.1 does not recognize AGP 9.0's `KotlinMultiplatformAndroidLibraryTarget` (applied via `com.android.kotlin.multiplatform.library` in our convention plugin). BCV's JVM-dump task registration only fires for classic `kotlin("jvm")` or legacy `kotlin-android` targets. Upstream issue tracked at <https://github.com/Kotlin/binary-compatibility-validator/issues>.
- **Coverage role change**: the decision box above was written assuming BCV covers JVM/Android and `audit-api` covers iOS. Actual coverage is inverted: **BCV covers Native + commonMain; `audit-api` remains the manual gate for androidMain-only public-API additions** until BCV adds AGP 9.0 KMP-Android awareness.
- **Operational rule** (until upstream fix): any PR that adds or changes a declaration inside `**/src/androidMain/**` must include a manual diff from the `audit-api` skill in the PR body alongside the automatic `apiCheck` result.
- **Local-dev prerequisites** for `./gradlew apiCheck` / `apiDump`: macOS host; either `sudo xcode-select -s /Applications/Xcode.app/Contents/Developer` or `DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer` exported; `cocoapods` on PATH (`brew install cocoapods`). CI macos-latest runners have all of these out of the box.

### C. iOS snapshot testing tool pick

Researched the three candidates via GitHub + web search.

| Tool | KMP support | Maintenance | Setup effort | Supports 1% tolerance | Supports CI (sim only, no real device) |
|---|---|---|---|---|---|
| `shot` (Karumi/Shot) | NO — Android-only by design (README explicitly redirects iOS users to "Swift Snapshot Testing") | Active; latest release Feb 2024 ("JUnit reporter support"), 49 releases, ongoing commits | Low for Android, but **does not solve the iOS problem at all** | Android-only — irrelevant for iOS | N/A (Android only) |
| [`QuickBirdEng/kotlin-snapshot-testing`](https://github.com/QuickBirdEng/kotlin-snapshot-testing) | Claims KMP, but `README` examples and dependency setup only reference Android + JVM (README inspected 2026-04-21: "Android and JVM" only, no iOS target mentioned). No visible iOS-target module. [`dev.petuska/klip`](https://gitlab.com/dev.petuska/klip) is the other KMP candidate — it's a Kotlin-compiler-plugin-based approach, but its canonical GitLab repo's last substantive commit on master is `2e57248` (2024-06-22, a dependabot config), with the previous commits dating to January 2023. The `github.com/mpetuska/klip` mirror referenced in older surveys is now 404. | Partial — QuickBirdEng: **54 commits** on master, latest release [`v2.0.0` (2024-11-21)](https://github.com/QuickBirdEng/kotlin-snapshot-testing/releases/tag/v2.0.0), 4 open issues, no 2026 activity. klip: effectively unmaintained since early 2023 based on commit history. | Low if it actually worked on iOS, but **iOS path is unverified by the project**; degrades to string/serialized representation only, not pixel goldens (QuickBirdEng README: uses `toString` + text-diff strategies) | No (not an image-diff library — compares serialized representations) | N/A — no pixel rendering |
| Bespoke XCTest + UIKitView harness using [`pointfreeco/swift-snapshot-testing`](https://github.com/pointfreeco/swift-snapshot-testing) | N/A (Swift-only, called from the test side) | Very active — latest release [`1.19.2` (2026-03-30)](https://github.com/pointfreeco/swift-snapshot-testing/releases/tag/1.19.2), **4,212 stars**, **30 GitHub Releases** (verified via GitHub API on 2026-04-21); Swift Testing support added in the 1.18.x line | **Moderate** (est. 2–4 days, not 3–5): 1 day to wire up a KMP host target that exposes a Compose `UIKitView`-hostable surface, 1 day to plumb `assertSnapshot(of: view, as: .image(precision: 0.99))` from an iOS test target, 0.5–1 day for CI wiring + first golden set. **Size risk: +50% (3–6 days) if the Compose→UIViewController bridge requires a custom host target we haven't proven.** No comparable published KMP+Compose-iOS snapshot harness was located in research, so this estimate is bottom-up, not benchmarked. | **YES** — `precision: 0.99` is documented 1% tolerance on exact-pixel diff; `perceptualPrecision` (CIE94-based) is the recommended companion and 90–97% faster | YES — the library explicitly supports "device-agnostic snapshots from a single simulator"; Bitrise/GitHub Actions macOS runners both work |

**Decision:** Bespoke XCTest + UIKitView harness using `pointfreeco/swift-snapshot-testing`.

Rationale:
- The two "KMP-native" candidates don't actually solve iOS pixel snapshotting. `shot` is Android-only by the maintainer's own direction ([Karumi/Shot README](https://github.com/Karumi/Shot) redirects iOS users to pointfreeco). `kotlin-snapshot-testing` / `klip` are serialized-representation comparators, not image-diff engines, and neither has shown meaningful iOS-target development activity (QuickBirdEng: no iOS in the README, last release 2024-11-21; klip: last substantive master commit on GitLab 2024-06-22).
- `swift-snapshot-testing` is the de facto iOS snapshot tool — stable API, precision/perceptualPrecision parameters that satisfy the spec's 1% tolerance requirement, and it's actively maintained through 2026 ([1.19.2 shipped 2026-03-30](https://github.com/pointfreeco/swift-snapshot-testing/releases/tag/1.19.2)).
- The bespoke cost (2–4 days, with a +50% size-risk ceiling of 6 days — see table note) is one-time. After the harness lands, adding a new golden is one line: `assertSnapshot(of: YallaButtonHostingController(state: .loading), as: .image(precision: 0.99))`.

**Fallback:** If `precision: 0.99` on the picked simulator (iPhone 15 Pro sim, iOS 17.5 baseline) produces flaky diffs due to Core Animation rasterization variance, degrade to `perceptualPrecision: 0.98` (CIE94 color tolerance) — this is the library's documented escape hatch. If *that* fails, degrade to manual real-device verification via the `mobile` MCP and document the limitation in `SECURITY.md` / README per the spec.

## detekt Suppression Audit (Task 10)

Inventory of every `@Suppress`, `@file:Suppress`, and `@SuppressLint` in production Kotlin sources (excludes `build/` and `.gradle/`). Line numbers are pre-edit, captured before inline justifications were added.

| File | Line | Rule suppressed | Existing justification? | Action |
|---|---|---|---|---|
| `composites/src/androidMain/.../DatePickerSheet.android.kt` | 30 | `FunctionName` | No | justify |
| `composites/src/commonMain/.../DatePickerSheet.kt` | 42 | `FunctionName` | No | justify |
| `composites/src/iosMain/.../DatePickerSheet.ios.kt` | 29 | `FunctionName` | No | justify |
| `media/src/androidMain/.../picker/ImagePicker.kt` | 35 | `@SuppressLint("NewApi","ClassVerificationFailure")` | Implicit (runtime guard `isSystemPickerAvailable()`) | justify |
| `media/src/iosMain/.../camera/CameraDelegates.kt` | 39 | `PARAMETER_NAME_CHANGED_ON_OVERRIDE` | No | justify |
| `media/src/iosMain/.../camera/CameraOrientationHelper.kt` | 44 | `UNUSED_PARAMETER` | KDoc identifies `@ObjCAction` notification handler | justify |
| `media/src/iosMain/.../camera/CameraSessionHelper.kt` | 122 | `UNCHECKED_CAST` | No | justify |
| `media/src/iosMain/.../gallery/YallaGallery.ios.kt` | 130 | `UNCHECKED_CAST` | No | justify |
| `media/src/iosMain/.../gallery/YallaGallery.ios.kt` | 186 | `DEPRECATION` | No | justify |
| `media/src/iosMain/.../picker/ImagePickerLauncher.ios.kt` | 74 | `UNCHECKED_CAST` | No | justify |
| `media/src/iosMain/.../utils/ViewControllerHelper.kt` | 23 | `DEPRECATION` | Function KDoc explains fallback | justify |
| `platform/src/androidMain/.../button/NativeCircleIconButton.android.kt` | 39 | `UNUSED_PARAMETER` | Function KDoc explains `expect` parity | justify |
| `platform/src/androidMain/.../otp/AppSignature.android.kt` | 55 | `DEPRECATION` | Implicit from surrounding `if/else` SDK-version branch | justify |
| `platform/src/commonMain/.../navigation/NavigatorImpl.kt` | 45 | `UNCHECKED_CAST` | No | justify |
| `platform/src/commonMain/.../navigation/NavigatorImpl.kt` | 58 | `UNCHECKED_CAST` | No | justify |
| `platform/src/commonMain/.../navigation/NavigatorImpl.kt` | 63 | `UNCHECKED_CAST` | No | justify |
| `platform/src/iosMain/.../browser/InAppBrowser.ios.kt` | 40 | `UNCHECKED_CAST` | No | justify |
| `platform/src/iosMain/.../navigation/NativeNavHost.ios.kt` | 78 | `ObjectPropertyName`, `ktlint:standard:backing-property-naming` | Property KDoc exists but `@Suppress` itself is bare | justify |
| `platform/src/iosMain/.../navigation/UIKitNavigator.kt` | 78 | `UNCHECKED_CAST` | No | justify |
| `platform/src/iosMain/.../navigation/UIKitNavigator.kt` | 99 | `UNCHECKED_CAST` | No | justify |
| `platform/src/iosMain/.../navigation/UIKitNavigator.kt` | 107 | `UNCHECKED_CAST` | No | justify |
| `platform/src/iosMain/.../sheet/NativeSheet.ios.kt` | 34 | `UNUSED_PARAMETER` | Function KDoc explains unused-on-iOS parity | justify |
| `platform/src/iosMain/.../sheet/NativeSheet.ios.kt` | 113 | `UNCHECKED_CAST` | No | justify |
| `platform/src/iosMain/.../system/SystemBarColors.ios.kt` | 1 (file) | `@file:Suppress("DEPRECATION")` | Function KDoc on each actual explains; file-level directive is bare | justify |
| `platform/src/iosMain/.../update/RememberAppUpdateState.ios.kt` | 64 | `UNCHECKED_CAST` | No | justify |

**Summary:** 25 total (23 declaration-level `@Suppress` + 1 `@file:Suppress` + 1 `@SuppressLint`); 25 justified inline; 0 stale-removed; 0 deferred to Phase 2+.

**Notes on the audit:**
- detekt is wired at the subproject level but currently reports `NO-SOURCE` on every `:<module>:detekt` task — the default source target (`src/main/java`) does not match the KMP layout. So the `@Suppress` annotations in this audit target Kotlin-compiler warnings and ktlint rules, not detekt itself. Wiring detekt to the actual KMP source roots is a separate concern (flag only; not in scope for Task 10).
- `@Suppress("FunctionName")` on the three `DatePickerSheet` Composables: both ktlint (`function-naming_ignore_when_annotated_with = Composable`, `.editorconfig` lines 22–23) and detekt (`FunctionNaming.ignoreAnnotated: [Composable]`, `config/detekt/detekt.yml:29`) already exempt Composables. The Kotlin compiler's own `FunctionName` warning is the only remaining signal these suppressions silence. Left in place with justification because removal risk-adjusted negative (empirically removing them is out-of-scope churn for zero user-visible benefit); consider removing opportunistically during the Phase 2 Composite-module polish pass.
- The file-level `@file:Suppress("DEPRECATION")` on `SystemBarColors.ios.kt` is the one structurally unusual annotation — flagged here per Task 10 guidance. Kept because both actual functions in the file touch the same deprecated `setStatusBarStyle` API; a declaration-level suppression would duplicate the annotation without narrowing scope.
- ktlint's `standard:no-consecutive-comments` rule forbids an EOL `//` comment directly below a KDoc block. For five declaration-level suppressions whose call site already had a KDoc (`ImagePicker.kt`, `NativeCircleIconButton.android.kt`, `InAppBrowser.ios.kt`, `NativeNavHost.ios.kt`, and both sites in `NativeSheet.ios.kt`), the justification was folded into the existing KDoc as an extra paragraph rather than added as a fresh EOL comment. All other sites received a one-line `//` comment immediately above the annotation.
- No entries were "resolve the underlying issue" — every suppression in the inventory is a genuine platform-interop workaround (ObjC type erasure, iOS `keyWindow` fallback, Android `GET_SIGNATURES` pre-P branch, `expect`/`actual` parameter parity, or Compose Composable naming). None is a code smell hiding a refactor.

