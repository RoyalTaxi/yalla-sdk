# Phase 2 — Progress Notes

> Created: 2026-04-21. Branch: `feature/v1-phase2-core-data`. Base commit: `a56194d` (main, Phase 1 merge).

## Baseline (Task 0)

- `ktlintCheck`: PASS
- `detekt`: PASS
- `apiCheck`: PASS (covers Native + commonMain via BCV 0.18.1 Klib mode; androidMain-only gated by audit-api skill per ADR-009)
- `yalla.sdk.version`: `0.0.8-alpha04` (will bump to `0.0.9-alpha01` at Task 9 closeout, reflecting two breaking clusters: Either flip + createHttpClient scope)

Any regressions during Phase 2 must be measured against this baseline.

## Task Progress

### Task 1 — Either flip
- SDK commit: f695e1f
- Call-sites flipped (yalla-sdk): 20 (core/src/commonMain + core/src/commonTest + data/src/commonMain)
- Call-sites flipped (YallaClient): 139 across 39 files in 14 modules
- YallaClient scratch branch: chore/sdk-phase2-either-flip
- YallaClient PR: https://github.com/RoyalTaxi/YallaClient/pull/304
- apiCheck post-flip: green (baselines regenerated — Either generics flipped, three additive extensions)
- mavenLocal snapshot tag: 0.0.9-alpha01-phase2-either
- YallaClient :androidApp:assembleDebug: BUILD SUCCESSFUL
- Status: SDK-side done; YallaClient awaits yalla-sdk 0.0.9-alpha01 publish before merge.

### Task 2 — createHttpClient scope ownership
- SDK commit: 894752e
- YallaClient commit: 9354c087c (extends PR #304)
- Contract change: createHttpClient(... scope: CoroutineScope)
- Default Koin binding: process-lifetime scope (SDK)
- YallaClient override: explicit named CoroutineScope binding (`AppQualifier.SDK_HTTP_CLIENT_SCOPE`) passed to all three `single<HttpClient>` definitions; process-lifetime for now, ready to swap for a per-session scope once a session boundary exists
- apiCheck post-refactor: green (data/api/data.klib.api updated — adds `kotlinx.coroutines/CoroutineScope` param to `createHttpClient`)
- mavenLocal snapshot: 0.0.9-alpha01-phase2-httpclient
- YallaClient :androidApp:assembleDebug: BUILD SUCCESSFUL
- Status: SDK-side done; YallaClient holds pending SDK 0.0.9-alpha01 publish.

### Task 3 — preference-impl unit tests
- Commit: 4024d91
- Test files created: 6 (one per impl) + `InMemoryDataStore` harness
- Test count: 60 (Session 12, User 10, Config 9, Interface 12, Position 6, Static 11)
- Harness: `InMemoryDataStore` (MutableStateFlow-backed `DataStore<Preferences>`) + `com.russhwolf.settings.MapSettings` from the new `multiplatform-settings-test` commonTest dependency
- Dispatcher: `runTest(UnconfinedTestDispatcher())` — each `scope.launch { dataStore.edit {...} }` settles before the next test line
- Dual-write coverage: Session's `setGuestMode`/`setDeviceRegistered`/`clearSession` cross-write to `StaticPreferences`; Interface's `setLocaleType`/`setOnboardingStage` cross-write; Static shares state across two instances over the same `Settings`.
- Additive: no public API delta (internal harness only)
- `:data:iosSimulatorArm64Test`: green (100/100 tests pass, 60 new)
- `:data:ktlintCheck` + `:data:detekt` + `:data:apiCheck`: green
- Status: done

### Task 4 — @Serializable + @SerialName pass
- Commit: 6b8d26a
- Models touched: 17
  - Newly `@Serializable`: `Address`, `AddressOption`, `PointRequest`, `Route` (+ `Route.Point`), `SavedAddress` (+ `SavedAddress.Parent`), `PointKind`, `Executor`, `ExtraService`, `ServiceBrand`, `PaymentCard`, `Client` (Profile)
  - Already `@Serializable`, `@SerialName` added on properties/variants: `GeoPoint`, `PlaceKind`, `GenderKind`, `LocaleKind`, `MapKind`, `ThemeKind`
- Wire names pinned to current Kotlin property names for data classes; enum variants pinned to the documented `id`/`code`/`wireValue` (matches the `.from(...)` factory contract)
- Round-trip tests: 17 new in `core/src/commonTest/.../SerializationRoundTripTest.kt` (no prior serialization tests in `core`); each verifies round-trip equality + asserts the on-the-wire property names
- `:core:iosSimulatorArm64Test`: green (17/17 new + all pre-existing)
- apiCheck post-pass: green (baselines regenerated — diff is purely additive `$serializer` + `Companion.serializer()` factories, 165 net-added lines in `core/api/core.klib.api`)
- ktlint + detekt: green
- Deferred (spawn-task-flagged, need custom KSerializers): `order/OrderStatus`, `payment/PaymentKind`, `order/Order`. Rationale: default polymorphic serialization would emit a `type`-discriminator JSON object that doesn't match the existing string-id wire contract (`"new"`, `"cash"`, etc.). Preserving that contract requires hand-written `KSerializer`s + an ADR.
- Wire contract frozen on current Kotlin property names
- Status: done

### Task 5 — 3xx redirect mapping decision
- Commit: a8a4d95
- Decision: Option A (keep mapping + KDoc + ADR-012)
- Alternatives rejected: dedicated Redirect type (breaks exhaustive when)
- Non-breaking
- Status: done

### Task 6 — GuestModeGuard configurable whitelist
- Commit: f3c8ce8
- Default: 6 legacy endpoints preserved in `NetworkConfig.guestAllowedSegments` default
  (`client`, `valid`, `register`, `location-name`, `cost`, `lists`); hoisted from the
  former `private val DEFAULT_GUEST_ALLOWED_SEGMENTS` in `GuestModeGuard.kt` to a
  public top-level `val` alongside `NetworkConfig`
- `createGuestModeGuardPlugin` parameter kept (default now sources from the new
  exported constant), so direct plugin consumers keep the previous signature
- `createHttpClient` threads `config.guestAllowedSegments.toSet()` into the plugin,
  honoring the caller's configuration
- Test: `GuestModeGuardConfigTest` — 5 cases (default size + membership, default
  matches exported constant, custom whitelist allows/blocks, empty whitelist blocks
  all guest requests, empty whitelist is inert when guest mode is off). Existing
  `GuestModeGuardTest` still green
- apiCheck: green; diff is additive (new `guestAllowedSegments` property with
  default + `component6()` + `copy()` overload widened + top-level
  `DEFAULT_GUEST_ALLOWED_SEGMENTS`)
- ktlint + detekt: green
- Non-breaking
- Status: done

### Task 7 — HttpClient integration tests
- Commit: c65b5f2
- New tests: 6 in `data/src/commonTest/.../HttpClientFactoryIntegrationTest.kt`
  (401 + UnauthorizedSessionEvents emit; retry + backoff on IOException for
  idempotent calls; guest mode blocks non-whitelisted endpoint; connection
  error → `DataError.Network.Connection`; socket timeout →
  `DataError.Network.Timeout`; scope cancellation stops preference observers)
- Harness: `HttpClient(MockEngine) { ... }` mirrors `createHttpClient`'s plugin
  install stack (HttpCallValidator, HttpTimeout, GuestModeGuard, defaultRequest,
  ContentNegotiation, DynamicHeaders). Keep-in-sync caveat documented in the
  class KDoc. `createHttpClient` itself can't be called with MockEngine because
  `createHttpEngine()` is an `expect` and the engine parameter is not hoisted.
- Fakes: `FakeSessionPreferences`, `FakeInterfacePreferences`,
  `FakePositionPreferences` — thin `MutableStateFlow`-backed doubles exposing
  only the fields `createHttpClient` observes. Reuses the existing
  `InMemoryDataStore` pattern's spirit, not the class itself (the test needs
  direct flow control, not DataStore semantics).
- Additive core API: `UnauthorizedSessionEvents.drainPendingEventIfExists()` —
  a single new method on the global object, allows tests (and consumers) to
  drain any pending CONFLATED event. KDoc inline. apiCheck diff is one-line
  additive in `core/api/core.klib.api`.
- Known-gap documented inline in the timeout test: Ktor 3.x's
  `HttpRequestTimeoutException` surfaces as `DataError.Network.Connection`
  because it extends `kotlinx.io.IOException` and `safeApiCall` catches
  `IOException` before any more-specific check. The timeout test therefore
  throws `SocketTimeoutException` directly (matching the contract exercised by
  `SafeApiCallTest.shouldReturnTimeoutErrorOnSocketTimeoutException`). Tracking
  the proper `HttpRequestTimeoutException → Timeout` fix in `safeApiCall` as a
  separate follow-up — scoped outside Task 7.
- `:data:iosSimulatorArm64Test`: green (111/111 total, 6 new)
- `:data:ktlintCheck` + `:data:detekt` + `:core:ktlintCheck` + `:core:detekt`
  + `apiCheck`: green
- Additive
- Status: done

