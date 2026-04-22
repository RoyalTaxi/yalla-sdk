# Changelog

All notable changes to the Yalla SDK are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versioning follows [SemVer](https://semver.org/spec/v2.0.0.html) **post-1.0**; pre-1.0 is full-risk mode (every third-segment bump may be breaking, per `.claude/rules/publishing.md`).

## [Unreleased]

## [0.0.13-alpha01] — 2026-04-22

### Added
- **`System.space.*` spacing tokens** (design module). Semantic aliases `screenEdge`, `sheetEdge`, `contentEdge`, `itemGap`, `sectionGap`, `heroGap`, `inlineGap` plus an escape-hatch `scale` (`xxs…massive`). Resolves a consumer-side audit finding of eight distinct raw `.dp` horizontal-padding values across YallaClient's 15 feature modules.
- **`System.radius.*` corner-radius tokens** (design module). Flat t-shirt scale `xs` / `s` / `m` / `l` / `xl` / `sheet` covering inputs → bottom-sheet top corners.
- `YallaTheme` accepts optional `spaceScheme` and `radiusScheme` parameters; defaults are `standardSpaceScheme()` / `standardRadiusScheme()`. Both `LocalSpaceScheme` and `LocalRadiusScheme` fall back to the standard factory outside a theme so previews resolve sensibly.
- `SpaceSchemeTest`, `RadiusSchemeTest` — value + structural-equality coverage.

### Decisions
- ADR-020: Spacing and corner-radius tokens graduate into the design system. Non-breaking, additive to `YallaTheme`; YallaClient sweep of raw `.dp` values is a separate follow-up (not bundled here).

## [0.0.12-alpha01] — 2026-04-22

### Breaking
- **ADR-018: `SwitchingMapProvider` caller-owned `CoroutineScope`.** Constructor now takes explicit `googleProvider`, `libreProvider`, `interfacePreferences`, and `scope` parameters; `close()` removed. Closes the scope-leak trifecta started by ADR-011 (HttpClient) and ADR-013 (LocationManager). `MapDependencies` exposes the scope for Koin wiring.
- **ADR-019: `YallaGallery` common surface narrowed.** `@Composable expect fun YallaGallery(modifier, onImageSelected)` — the intersection of Android's `PickVisualMedia` and iOS's `PHPickerViewController`. Rich Paging3-backed grid moved to a new Android-only `YallaGalleryPagingGrid` composable with the full prior parameter set.
- **`@ExperimentalYallaGalleryApi` retired.** The marker annotation is deleted; both public composables are plain stable API. YallaClient call sites drop any `@OptIn(ExperimentalYallaGalleryApi::class)` usages.
- **`LocationsLayer(arrival, duration, …)` parameters removed** from both Google and Libre provider packages — both impls ignored them. Callers drop the two named args.
- **`OrderStatus` / `PaymentKind` now `@Serializable(with = …)`** via custom `KSerializer`s. Sealed hierarchies serialize as plain String IDs, preserving the wire contract. Round-trip uses `Companion.from(id)` semantics for unknown values (falls back to the pre-existing fallback subtype rather than throwing).
- **`PointKind` enum gains a `Companion.from(wireValue)` fallback + custom serializer.** Unknown wire values deserialize to `POINT` instead of throwing — forward-compatibility.

### Added
- **`MapController` + `SwitchingMapController` test coverage.** 37 test cases in `MapControllerFakeProviderTest` against a recording fake; covers all 14+ public operations, state-flow behavior, provider-switch handoff, close idempotency.
- **`maps.api.overlay`** new `RouteOverlayConfig`, `LocationsOverlayConfig`, `LocationIndicatorConfig` data classes — shared parameter contract for the three overlay composables. Google + Libre impls conform.
- **`firebase` hardening:**
  - Android `initializePlatform()` fails loudly with a diagnostic error when `google-services.json` is missing or `FirebaseApp.getInstance()` throws.
  - `analytics` / `crashlytics` / `messaging` property getters assert `isInitialized` before returning. Pre-init access throws `IllegalStateException` with a clear "call initialize() first" message instead of a cryptic Firebase SDK error.
  - New `YallaFirebasePreInitTest`, `YallaAnalyticsTest`, `YallaCrashlyticsTest`, `YallaMessagingTest` smoke coverage.
- **Media:**
  - Per-instance CameraX `ExecutorService` owned by `YallaCameraState` (previously module-global singleton).
  - `calculateInSampleSize` pure-math tests (11 cases); `GalleryPickerState` layout-config tests (13 cases).
- **Serialization round-trip tests** for `OrderStatus`, `PaymentKind`, `PointKind` including fallback paths.

### Changed
- `.claude/rules/library-api.md` — "no unmanaged scopes" is now the actual enforced pattern across HttpClient (Phase 2), LocationManager (Phase 3), and SwitchingMapProvider (Phase 5). The carve-out in that rule collapses.

### Decisions
- ADR-018: SwitchingMapProvider caller-owned CoroutineScope; scope-leak trifecta closed.
- ADR-019: YallaGallery common surface narrowed to PHPicker-equivalent; Paging3 grid Android-only.

## [0.0.11-alpha01] — 2026-04-22

### Breaking
- **ADR-017 string→slot migration across 8 composites.** `ListItem`, `IconItem`, `NavigableItem`, `SelectableItem`, `PricingItem`, `AddressItem` (both overloads), `Navigable` (drawer), and `EmptyState` now accept `@Composable () -> Unit` slots instead of `String` / `String?` parameters. `EmptyStateState(image: Painter, title: String, description: String?)` is deleted — `EmptyState` takes three separate slots directly. `Navigable`'s `titleStyle` and `descriptionStyle` params are removed; default text style is applied via `ProvideTextStyle` (Material3). Mechanical migration: `title = "x"` → `title = { Text("x") }`.
- **`AddressCard` removed.** No internal SDK or YallaClient call sites (verified via grep). `composites.card.AddressCard` and its test file deleted. Supersedes the `ContentCard` / `RouteView` deletions the spec loosely grouped it with — those two are retained (both have real callers).

### Added
- Structural-equality tests for `Snackbar`, `SnackbarHost`, `Navigable`, `EmptyState`, `SectionBackground` in composites — previously untested.
- Structural-equality tests for `TopBar` + `LargeTopBar` in primitives.
- `navigationIconContentDescription: String? = null` parameter on `TopBar` and `LargeTopBar`. Threads through to `NavigationButton`'s existing `contentDescription` param so screen readers can label the nav icon.

### Changed
- `resources/values-be/` MODULE.md clarification stays from Phase 3 — rename to `values-uz-Cyrl` still blocked by Compose Resources 1.10.0.
- `.github/workflows/publish.yml` gate relaxed to `apiCheck` only (Phase 3 hotfix merged in `da8611e`). iOS-simulator link step was failing on CI for foundation + composites due to moko-geo's transitive `_LocationEssentials` CoreLocation subframework not being on the Xcode SDK path; pod install + Xcode alignment on CI tracked as Phase 5 infra. apiCheck remains the authoritative structural gate.

### Decisions
- ADR-017: composite string params migrated to `@Composable () -> Unit` slots; supersedes ADR-005's migration-plan note.

## [0.0.10-alpha01] — 2026-04-22

### Breaking
- `LocationManager` constructor now requires a caller-owned `CoroutineScope`. The `close()` method is removed — cancel the scope instead. Same ownership-inversion pattern as `createHttpClient` (ADR-011). See ADR-013.
- `LanguageOption.UzbekCyrillic` and `LanguageOption.English` removed from the sealed hierarchy. `LocaleKind.UzCyrillic` and `LocaleKind.En` removed from the enum. `LanguageOption.from(kind)` is now exhaustive over `Uz` + `Ru`. Persisted `InterfacePreferences.localeType` values of `"en"` / `"uz-Cyrl"` fall through to `Uz` silently via the existing `LocaleKind.from(code)` fallback. See ADR-014.
- `SystemBarColors(statusBarColor: Color, navigationBarColor: Color)` overload removed. Use `SystemBarColors(darkIcons: Boolean)` only — theme already owns bar colors. See ADR-015b.
- `ObserveSmsCode` moved from `platform` commonMain to androidMain-only public surface. iOS SMS OTP autofill is a native keyboard feature (`UITextContentType.oneTimeCode`), not a callback API. See ADR-015c.

### Added
- `FontScheme` structural-equality unit test.
- `LocationManager` lifecycle tests (start/stop idempotence, scope-cancel, permission propagation, default fallback).
- `ChangeLanguage` smoke test.
- `values-uz/strings.xml` (Uzbek Latin) generated via deterministic Cyrillic→Latin transliteration (268 keys). Islom-review recommended before 1.0.
- iOS visual-regression scaffold under `iosTests/` using `pointfreeco/swift-snapshot-testing` 1.19.2. Real snapshots wire in Phase 4.
- Behavioral smoke coverage for 8 of 13 `platform` expect/actual pairs (6 non-composable, 2 interface contracts); 11 composable pairs landed as compile-verify TODOs pending Compose UI test harness wiring in Phase 4.

### Changed
- `foundation/LocationServices` Android actual raises a clear error when the Koin global `Context` is missing (previously NPE'd). iOS actual uses the modern `UIApplication.open(url:options:completionHandler:)` API (iOS 10+).
- `foundation/changeLanguage` iOS fallback locale: `"en"` → `"uz"` (English is no longer a `LocaleKind`).
- `NativeSheet.onFullyExpanded` semantics locked via ADR-015a (fires once when sheet settles at fully-expanded detent; both actuals already satisfy this).
- `IosPlatformConfig` / `AndroidPlatformConfig` KDoc: asymmetry documented as accepted (ADR-015d). Android is a marker class; iOS needs factories because UIKit interop requires Swift-side code.
- Root `build.gradle.kts` wires the default `detekt` task to depend on every per-source-set variant — previously the root task was NO-SOURCE on KMP modules. ktlint gets the same generator-dep wiring so builds are reproducible on cold caches.
- `resources/values-be/` directory name retained — Compose Resources 1.10.0 rejects 4-letter script qualifier `Cyrl`; upstream limitation tracked as a follow-up. Content is still Uzbek Cyrillic; MODULE.md clarifies.

### Infrastructure
- `.github/workflows/publish.yml` now runs a `verify` job (`./gradlew apiCheck allTests`) before `publish`. Broken main can no longer ship artifacts. See ADR-016. iOS tests for `firebase` + `maps` are excluded pending the Phase 5 iOS test harness (framework-linking needs CocoaPods wiring that CI doesn't have yet).

### Decisions
- ADR-013: LocationManager caller-owned CoroutineScope.
- ADR-014: LanguageOption + LocaleKind narrowed to production-ready locales.
- ADR-015: Platform expect/actual asymmetry resolutions (a–d).
- ADR-016: publish.yml gates on apiCheck + allTests.

## [0.0.9-alpha01] — 2026-04-21

### Breaking
- `Either<D, E>` flipped to `Either<E, D>` (error-first). Every consumer's generic order changes. See ADR-010.
- `createHttpClient` takes a `CoroutineScope` parameter; caller owns lifecycle. SDK default Koin binding provides a process-lifetime scope; consumers should override with a lifecycle-owned scope. See ADR-011.

### Added
- **From Phase 1** (first published under this version since Phase 1 did not bump):
  - `binary-compatibility-validator` plugin; `apiCheck` enforces public-API stability for Native (iosArm64, iosSimulatorArm64) + commonMain surfaces. androidMain-only surface is gated by the manual `audit-api` skill (BCV 0.18.1 doesn't cover AGP 9.0's `KotlinMultiplatformAndroidLibraryTarget` yet).
  - Root POM metadata (name, description, url, licenses, scm, developers) on every published module.
  - GitHub Pages deployment pipeline for Dokka reference docs at <https://royaltaxi.github.io/yalla-sdk/>.
  - OSS-hygiene files: `LICENSE`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, `SECURITY.md`, `CHANGELOG.md`, `SUPPORT.md`, `CODEOWNERS`, PR + issue templates, public-facing `README.md`.
  - Public `.github/workflows/ci.yml` — PR + push gate for lint on Ubuntu (api-check + test jobs deferred to main-branch CI / developer Macs per the iOS scope decision).
  - `resources/MODULE.md` and `bom/MODULE.md` scaffolds.
- **Phase 2 additions:**
  - `Either.getOrNull()`, `Either.getOrThrow()`, `Either.fold(ifFailure, ifSuccess)` extensions.
  - Unit tests for all 6 preference-impl classes (60 tests) with an `InMemoryDataStore` test harness + `MapSettings`.
  - HttpClient integration tests (6 tests via Ktor MockEngine): 401 emit, IO-error retry, guest-mode blocking, connection error, timeout, scope cancellation.
  - `GuestModeGuardConfigTest` covering the new configurable whitelist.
  - `@Serializable` + `@SerialName` pinned on 17 public domain models in `core` (Address, AddressOption, PointRequest, Route+Route.Point, SavedAddress+Parent, PointKind, Executor, ExtraService, ServiceBrand, PaymentCard, Client, GeoPoint, PlaceKind, GenderKind, LocaleKind, MapKind, ThemeKind). 17 round-trip tests added.
  - `UnauthorizedSessionEvents.drainPendingEventIfExists()` helper (needed by tests).
  - `DEFAULT_GUEST_ALLOWED_SEGMENTS` public constant in `uz.yalla.data.network`.

### Changed
- `NetworkConfig.guestAllowedSegments: List<String>` now configurable (default preserves the legacy 6-endpoint whitelist).
- `GuestModeGuard` accepts its whitelist via `NetworkConfig` (not hardcoded).
- `multiplatform-settings-test` added to catalog + `data:commonTest`.
- **From Phase 1**: five `ktlint_official` rules relaxed via `.editorconfig` (ADR-009): `multiline-expression-wrapping`, `function-signature`, `class-signature`, `argument-list-wrapping`, `no-empty-first-line-in-class-body`. 44 `.kt` files auto-formatted as part of the remediation.

### Removed
- **From Phase 1**: stale `yalla-sdk = "0.0.1-alpha08"` entry removed from `gradle/libs.versions.toml` (dead; publishing reads `gradle.properties`).

### Documentation
- ADR-009 (ktlint rule relaxations), ADR-010 (Either generic flip), ADR-011 (createHttpClient scope ownership), ADR-012 (3xx redirect mapping kept as Client) added.
- `core` semantic-stability pass recorded in Phase 2 progress notes. 36 public declarations reviewed; 6 follow-up candidates documented.
- `.claude/*` docs swept to reflect live apiCheck wiring + AGP-9.0 coverage gap (`audit-api` skill role clarified).

### Deferred to 1.x
- `OrderStatus`, `PaymentKind`, `Order` need hand-written `KSerializer`s to preserve string-ID wire contracts (flagged in Task 4 concerns).
- `safeApiCall` incorrectly maps `HttpRequestTimeoutException` (Ktor 3.x) to `DataError.Network.Connection` because it extends `IOException` (flagged in Task 7 concerns). `SocketTimeoutException` correctly maps to `Timeout`.

### Known Phase-2 concerns captured
- `Order.StatusTime.status: String` should become `OrderStatus`-typed pre-1.0.
- `ExtraService.costType: String` should become an enum pre-1.0.
- `PointKind` is the only enum without a `.from()` fallback — unknown wire values currently throw on decode.
- `DataError.Network` sealed hierarchy adds cost: any new subtype breaks exhaustive `when`.

---

## [0.0.8-alpha04] — 2026-04-21

Released via CI before the launch-spec work began. See git history `b35e248` for contents.
