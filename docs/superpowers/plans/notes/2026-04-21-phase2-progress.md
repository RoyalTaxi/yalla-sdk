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

### Task 8 — core semantic-stability pass

Reviewed every public `sealed` / `enum` / `data class` / `@Serializable` / top-level `object` declaration in `core/src/commonMain/kotlin/uz/yalla/core/` for post-1.0 shape-stability hazards. No code changes — this task produces stability documentation for future-phase authors and reviewers.

Scope: 36 public declarations enumerated. Non-shape declarations (interfaces in `contract/`, utility functions in `util/`) are out of scope — they don't carry additive-member hazards beyond standard binary-compat discipline.

**Sealed hierarchies:**

| Declaration | Kind | Shape hazards | Mitigation posture |
|---|---|---|---|
| `DataError` | sealed class (top) | Only one direct subtype (`Network`). Adding a new top-level subtype (e.g., `DataError.Cache`, `DataError.Validation`) breaks every exhaustive `when (error: DataError)` downstream. | Treat any new top-level category as breaking. Deprecation-warm-up: ship a no-op stub guarded by `@Deprecated(level=WARNING)` in the minor before enabling — gives YallaClient one release cycle to update `when`s. |
| `DataError.Network` | sealed class (nested) | 8 existing subtypes (`Connection`, `Timeout`, `Server`, `Client`, `ClientWithMessage`, `Serialization`, `Guest`, `Unknown`). Adding a new subtype (hypothetically `RateLimited`, `Redirect`, `Unauthorized`) breaks exhaustive `when` at every YallaClient call-site that branches on `DataError.Network.*`. | Each new subtype requires an ADR + a deprecation warm-up (or route through `Unknown` until consumers explicitly adopt it). ADR-012 already records the conscious choice to keep 3xx mapped to `Client` rather than add a `Redirect` subtype precisely to avoid this break. |
| `Either` | sealed interface | Only two subtypes (`Failure`, `Success`) — closed by design. Never extend. | N/A — closed forever. The sealed-interface declaration shape itself is load-bearing: consumers pattern-match exhaustively. |
| `Either.Failure<E>` | data class | Single prop `error: E`. Adding a required constructor param is a `copy()` break. | Never add required params. Additions (unlikely) must carry defaults. |
| `Either.Success<D>` | data class | Single prop `data: D`. Same rule. | Same. |
| `OrderStatus` | sealed class | 9 `data object`s + 1 `data class Unknown(originalId)`. Serialized via hand-written `.from(id)` using string IDs. Adding a new subtype breaks exhaustive `when` and requires coordinated wire-contract + `.from()` update. | Flagged in Task 4 DONE_WITH_CONCERNS: needs custom `KSerializer` before it can safely be `@Serializable`. Adding a new status (e.g., `Expired`) should route unknown wire IDs through `Unknown` first, then consumers adopt explicit cases when ready. Never remove a status. |
| `OrderStatus.Unknown` | data class | `originalId: String` positional. `copy()` break if reordered. | No reorder. Never add required params. |
| `PaymentKind` | sealed class | 2 subtypes (`Cash`, `Card`). Serialized via hand-written `.from(id, cardId, maskedNumber)`. Adding a new kind (e.g., `GooglePay`, `ApplePay`, `Wallet`) breaks exhaustive `when`. | Same as `OrderStatus`: flagged in Task 4 DONE_WITH_CONCERNS for custom serializer. Any new kind needs coordinated wire-contract update + `.from()` expansion + ADR + deprecation warm-up. |
| `PaymentKind.Card` | data class | `cardId: String`, `maskedNumber: String`. Positional `copy()`. | No reorder. Never add required params. |
| `DataError.Network.ClientWithMessage` | data class | `code: Int`, `message: String`. Positional `copy()`. | No reorder. Never add required params (add with defaults only). Type-changes (e.g., `Int` → `Long`) are breaking. |

**Enums (all `@Serializable` with `@SerialName` pinned per Task 4):**

| Declaration | Kind | Shape hazards | Mitigation posture |
|---|---|---|---|
| `PlaceKind` | enum | 3 values (`Home`, `Work`, `Other`). Wire uses `@SerialName` (`"home"`/`"work"`/`"other"`); ordinal **not** persisted — the `.from()` factory matches on `id`. Adding a value breaks exhaustive `when` but is wire-additive (unknown values fall back to `Other`). | Safe to add values additively. Provide a deprecation warm-up if a value is critical to a `when`. Never reorder for ordinal safety (Kotlin persistence may emit ordinals elsewhere) even though this class isn't directly ordinal-persisted. Never rename a declared value's `@SerialName` — that IS a wire break. |
| `PointKind` | enum | 3 values (`START`, `POINT`, `STOP`). `@SerialName` pinned to `"start"`/`"point"`/`"stop"`. No `.from()` fallback — deserialization of an unknown wire value is a hard fail (JSON decode throws). | Adding a wire value is breaking on the receive side (old SDK can't decode) unless coordinated with server rollout. Same `when`-exhaustiveness rule. Would benefit from adding a `@SerialName`-default `UNKNOWN` value in a future minor if server adds route-waypoint kinds. |
| `GenderKind` | enum | 3 values (`Male`, `Female`, `NotSelected`). Wire: `@SerialName` ids. `.from()` factory falls back to `NotSelected`. | Additive rules. `NotSelected` as a default-bucket is already good. |
| `LocaleKind` | enum | 4 values (`Uz`, `UzCyrillic`, `Ru`, `En`). Wire: `@SerialName` BCP-47 codes. `.from()` normalizes input + falls back to `Uz`. | Safe additive. New locale additions are the most likely evolution — keep the `.from()` fallback intact. |
| `MapKind` | enum | 2 values (`Google`, `Libre`). Wire: `@SerialName`. `.from()` falls back to `Google`. | Safe additive. |
| `ThemeKind` | enum | 3 values (`Light`, `Dark`, `System`). Wire: `@SerialName`. `.from()` falls back to `System`. | Safe additive. |

**Data classes (top-level, all `@Serializable` post-Task-4 unless noted):**

| Declaration | Kind | Shape hazards | Mitigation posture |
|---|---|---|---|
| `GeoPoint` | `@Serializable` data class | 2 props (`lat`, `lng`). `@SerialName` pinned. `init` validator enforces lat/lng ranges — tightening the validator is a runtime-behavior break. | Future additions require defaults + nullable. Never loosen or tighten `require()`-based validation without an ADR (behavioral break). |
| `Address` | `@Serializable` data class | 5 props incl. nullable `id: Int?`. `@SerialName` pinned. | Future additions require defaults. Nullability flips on any prop are breaking. |
| `AddressOption` | `@Serializable` data class | 7 props. `@SerialName` pinned. | Additions with defaults only. Never reorder. |
| `PointRequest` | `@Serializable` data class | 3 props. `@SerialName` pinned. | Additions with defaults only. |
| `Route` | `@Serializable` data class | 3 props incl. `points: List<Point>`. `@SerialName` pinned. | Additions with defaults only. `List<Point>` type is load-bearing — swapping to another collection (e.g., `Set`) is breaking. |
| `Route.Point` | `@Serializable` data class (nested) | 2 props. `@SerialName` pinned. | Same rules. |
| `SavedAddress` | `@Serializable` data class | 8 props incl. nested `Parent`. `@SerialName` pinned. | Additions with defaults only. |
| `SavedAddress.Parent` | `@Serializable` data class (nested) | 1 prop `name: String?`. Nullable. | Nullability flip is breaking. |
| `Executor` (in `order/`) | `@Serializable` data class | 5 props for map-tracking. `@SerialName` pinned. | Additions with defaults only. Separate from `Order.Executor` (full model) — don't conflate. |
| `ExtraService` | `@Serializable` data class | 4 props + companion constants (`COST_TYPE_COST`, `COST_TYPE_PERCENT`) + derived `isPercentCost` prop. `@SerialName` pinned. | Additions with defaults only. The `costType` string-based discriminator is fragile — post-1.0 evolving it to an enum is breaking; do it in a coordinated major. |
| `ServiceBrand` | `@Serializable` data class | 3 props. `@SerialName` pinned. | Additions with defaults only. |
| `PaymentCard` | `@Serializable` data class | 2 props + `toPaymentType()` conversion. `@SerialName` pinned. | Additions with defaults only. The `toPaymentType()` output contract is part of public API — don't change its signature. |
| `Client` (profile) | `@Serializable` data class | 7 props. `@SerialName` pinned. `gender: String` (NOT `GenderKind`) — typed via factory at consumer. | Additions with defaults only. If `gender` is later converted to `GenderKind`, that's breaking. |
| `Order` | data class (NOT `@Serializable`) | 9 props with deeply nested types. NOT currently `@Serializable` — Task 4 flagged as needing custom `KSerializer` to preserve wire contract of `PaymentKind` and `OrderStatus` members. | Blocked on a future ADR for the hand-written serializer. When `@Serializable` is eventually added, every property needs `@SerialName` pinned to its current Kotlin name (same policy as Task 4). Additions require defaults. Positional `copy()` is heavy — 9 positional params — so any insertion mid-list is a break. |
| `Order.Executor` | data class (NOT `@Serializable`) | 9 props, nested `Coords` + `Vehicle`. | Same rules as `Order`. Depends on outer `@Serializable` policy. |
| `Order.Executor.Coords` | data class | 3 props. | Same. |
| `Order.Executor.Vehicle` | data class | 6 props, nested `Color`. | Same. |
| `Order.Executor.Vehicle.Color` | data class | 2 props. | Same. |
| `Order.StatusTime` | data class | 2 props (`status: String`, `time: Long`). `status` is a raw string that shadows `OrderStatus.id` — a split-representation hazard. | Same additive rules. Post-1.0 consider migrating `status` to `OrderStatus` type (breaking but cleaner). Flag as a separate follow-up. |
| `Order.Taxi` | data class (NOT `@Serializable`) | 11 props incl. `routes: List<Route>`, `services: List<ExtraService>`. | Same. 11 positional params is a large `copy()` break surface. |
| `Order.Taxi.Route` | data class | 3 props incl. nested `Coords`. | Same. |
| `Order.Taxi.Route.Coords` | data class | 2 props. | Same. |

**Singleton objects:**

| Declaration | Kind | Shape hazards | Mitigation posture |
|---|---|---|---|
| `UnauthorizedSessionEvents` | `object` singleton | Process-global state carrier: `Channel<Unit>(CONFLATED)` + `events: Flow<Unit>` + `publish()` + `drainPendingEventIfExists()`. Any consumer that holds an `HttpClient` observes the same channel. Post-1.0, swapping this for a per-`HttpClient` instance is breaking — consumers explicitly import `UnauthorizedSessionEvents.events` in their root navigator. | Current shape frozen post-1.0. If a per-instance variant is needed, add it alongside and deprecate the global. `drainPendingEventIfExists()` was added in 0.0.9 (Task 7); the singleton's signature is otherwise unchanged since 0.0.1. |

**Posture summary:**

- **Sealed hierarchies**: add-subtype is always source-breaking for exhaustive `when`. Every new subtype needs either (a) an `@Deprecated(level=WARNING)` stub in the minor before it lands or (b) a graceful-degrade route (e.g., `OrderStatus.Unknown`, `PaymentKind.Cash` fallback, `DataError.Network.Unknown`). Prefer (b) wherever a "fall-through" bucket already exists.
- **Enums**: add-value is source-breaking for `when` but not binary-breaking. Wire-side deserialization of unknown values throws unless a `.from()`-factory fallback exists — five of six enums have that fallback; only `PointKind` does not.
- **Data classes**: all additions must have defaults or be nullable. Never remove a property, never reorder (breaks `componentN()`), never change a type (`Int` → `Long` is wire-breaking for `@Serializable`, API-breaking always).
- **`@Serializable`**: `@SerialName` pinned post-Task-4 on all 17 wire-carried models. Wire format is public API. Any property rename must keep the old `@SerialName` value.
- **`Order` family is the biggest latent risk surface**: 22 nested declarations, 9–11 positional-constructor data classes, string-typed status discriminator (`Order.StatusTime.status`), NOT currently `@Serializable`. The eventual custom-serializer ADR must freeze the wire shape before 1.0.

**Items flagging Phase 2+ follow-up:**

1. **`OrderStatus`, `PaymentKind`, `Order` need hand-written `KSerializer`s** — flagged in Task 4's DONE_WITH_CONCERNS report. Tracked as a separate spawned task (see `docs/obsidian/projects/yalla-sdk` session logs).
2. **`DataError.Network` sealed hierarchy with 8 subtypes**: any new error case (e.g., a hypothetical `RateLimited`, `ServerWithMessage` symmetric to `ClientWithMessage`) is source-breaking. Consumers in YallaClient would need `when` updates. Route new cases through `Unknown` first, or document in 1.x deprecation cycle policy.
3. **`UnauthorizedSessionEvents` is a process-global state carrier** (`object` singleton with `Channel<Unit>`). Post-1.0, replacing it with a per-HttpClient instance would be breaking. Current shape is frozen; additive methods are still safe (like `drainPendingEventIfExists` added in 0.0.9).
4. **`Order.StatusTime.status: String`** shadows `OrderStatus.id` — post-1.0 typing this as `OrderStatus` (like `Order.status` already is) would be breaking but semantically cleaner. Spawn-task candidate before 1.0 freeze.
5. **`PointKind` is the only enum without a `.from()` fallback** — unknown wire values throw `SerializationException` on decode. Before 1.0 consider adding a fallback sentinel or documenting that the server contract is the source of truth.
6. **`ExtraService.costType: String`** uses string-based discriminator (`"cost"` / `"percent"`) instead of an enum. Evolving to an enum post-1.0 would be breaking. Flag for pre-1.0 cleanup.

No items surfaced that SHOULD be code-changed inside Task 8 — the pass is documentation-only, as planned. All follow-ups listed above are pre-1.0 cleanup candidates or post-1.0 deprecation/policy items, not in-scope Phase 2 fixes.

- Status: done

