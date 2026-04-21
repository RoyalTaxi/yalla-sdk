# Changelog

All notable changes to the Yalla SDK are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versioning follows [SemVer](https://semver.org/spec/v2.0.0.html) **post-1.0**; pre-1.0 is full-risk mode (every third-segment bump may be breaking, per `.claude/rules/publishing.md`).

## [Unreleased]

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
