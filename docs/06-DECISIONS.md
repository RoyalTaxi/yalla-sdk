# Architecture Decisions

> Why things are the way they are. Read this before proposing changes.

## ADR-001: Either over try-catch

**Decision**: All fallible operations return `Either<D, E>` instead of throwing exceptions.

**Why**: Exceptions are invisible in the type system. A function that throws gives no compile-time
signal about failure modes. `Either` forces callers to handle both success and failure paths.

**Consequence**: Every repository function returns `Either`. Use `onSuccess`/`onFailure` for side effects,
`mapSuccess`/`mapFailure` for transformations.

---

## ADR-002: Kotlin Multiplatform with expect/actual

**Decision**: Share code via KMP. Platform-specific code uses `expect`/`actual`.

**Why**: One codebase for Android + iOS. Native performance, native UI where needed.
expect/actual provides compile-time safety — missing implementations are caught at build time.

**Consequence**: Common code can't reference Android or iOS APIs directly. Platform code lives
in `androidMain`/`iosMain` source sets.

---

## ADR-003: Colors + Dimens + Defaults pattern (not State bundling)

**Decision**: Components use separate `Colors`/`Dimens` classes with a `Defaults` factory object.
Content is passed as individual parameters, not bundled into State classes.

**Why**: State bundling (putting text, icon, and loading flag into one data class) couples
content to the component. Separate parameters are more flexible and follow Compose conventions
(see Jetpack Compose `Button`, `TextField`, `Card` APIs).

**Consequence**: Components have more parameters but are more composable. Colors and Dimens are
`@Immutable` to help the Compose compiler skip recomposition.

---

## ADR-004: Google Maps + MapLibre dual support

**Decision**: The maps module supports both Google Maps and MapLibre at runtime.

**Why**: MapLibre provides offline maps and custom tile servers (important for Uzbekistan
where Google Maps coverage is limited). Google Maps provides traffic and street view.
Users choose their preferred provider.

**Consequence**: `SwitchingMapProvider` and `SwitchingMapController` handle runtime switching.
Both providers must implement the same `MapController` interface. State is preserved during switches.

---

## ADR-005: String parameters in composites (current, slot migration planned)

**Decision**: Some composite components (ListItem, ActionSheet, etc.) accept `String` parameters
instead of `@Composable` slot lambdas.

**Why**: This was the initial API design. Slots are more flexible but require more boilerplate
at call sites. The current API works correctly.

**Plan**: Migrate to slots in the next major version (breaking change). This is tracked as P2
in `SDK_STATUS.md`. Don't change these APIs without coordinating the YallaClient migration.

---

## ADR-006: In-repo documentation over external tools

**Decision**: All documentation lives in the repository (`docs/`, `MODULE.md`, `SDK_STATUS.md`,
`COMPONENT_STANDARD.md`). No Notion, Confluence, or external wikis.

**Why**: Documentation that lives with the code stays in sync with the code. External docs
drift. New developers clone the repo and have everything they need.

**Consequence**: Documentation must be updated in the same PR as code changes. Review includes
doc review.

---

## ADR-007: Design tokens in a separate module

**Decision**: Colors and fonts live in `design/`, not scattered across component modules.

**Why**: Single source of truth for the visual language. Changing a brand color updates everywhere.
Components reference `System.color.*` and `System.font.*` — never hardcoded values.

**Exception**: `maps/` module has hardcoded overlay colors because it can't depend on `design/`
(different dependency level). These are documented in `MapStyles.kt`.

---

## ADR-008: safeApiCall with retry for idempotent calls

**Decision**: `safeApiCall` supports automatic retry with exponential backoff for idempotent calls.

**Why**: Mobile networks are unreliable. GET requests are safe to retry. POST requests are not
(could create duplicate orders). The `isIdempotent` flag controls this.

**Consequence**: Always set `isIdempotent = true` for GET/HEAD requests. Never for POST/PUT/DELETE
unless you've verified idempotency on the server side.

---

## ADR-009: Disable five ktlint_official rules that conflict with Compose patterns

**Decision**: Disable `multiline-expression-wrapping`, `function-signature`, `class-signature`, `argument-list-wrapping`, and `no-empty-first-line-in-class-body` in `.editorconfig` for this codebase.

**Why**: The `ktlint_official` code style includes these rules. In practice, on Compose-heavy Kotlin code (every primitive + composite + many platform actuals), they fight each other during `ktlintFormat`: one rule's preferred wrapping triggers another's violation, and the format pass does not converge even after three consecutive iterations (~901 violations found, 535 residual after auto-fix). Forcing the rules by manual reformat would touch ~80 files across every UI module, with meaningful regression risk on layout-sensitive call sites.

The existing `.editorconfig` already disables nine ktlint_official rules (`trailing-comma-on-call-site`, `trailing-comma-on-declaration-site`, `class-naming`, `filename`, `no-wildcard-imports`, `function-expression-body`, `string-template-indent`, `function-type-modifier-spacing`, plus `function-naming` for `@Composable`). Adding five more is consistent with that precedent.

**Consequence**: Five rules no longer enforced. The remaining ktlint_official rules continue to enforce 120-column max, indentation, naming, and import ordering. CI gates on `./gradlew ktlintCheck` per Phase 1 plan. If a future PR wants to re-enable any disabled rule, it must include a full-codebase reformat in the same commit and pass CI, with an ADR update.

Decided: 2026-04-21. Part of Phase 1 of the v1.0 launch.

---

## ADR-010: Either generic parameter order flipped to `<E, D>` (error-first)

**Decision**: Flip `Either<out D, out E>` to `Either<out E, out D>` — error first, success second. Applies to the sealed interface declaration and every extension (`mapSuccess`, `mapFailure`, `fold`, `getOrNull`, `getOrThrow`, etc.).

**Why**: Ecosystem convention is error-first. Arrow's `Either<A, B>` is `<Left=Error, Right=Success>`. Every Kotlin engineer who has used Arrow reads `Either<X, Y>` with `X` as the error type. Our previous order (`<Data, Error>`) inverted that and would confuse every non-Yalla contributor permanently after 1.0. Fix it now, while pre-1.0 full-risk mode allows breaking changes.

**Consequence**: Every call-site in `core`, `data`, and YallaClient flips. Cumulative breaking: `0.0.8-alpha04` → `0.0.9-alpha01`. YallaClient's scratch branch `chore/sdk-phase2-either-flip` merges to YallaClient's `dev` branch in lockstep with this SDK PR.

Decided: 2026-04-21. Part of Phase 2 of the v1.0 launch.

---

## ADR-011: `createHttpClient` scope ownership moves to caller

**Decision**: `createHttpClient` now takes a `CoroutineScope` parameter. The SDK does not internally construct a `CoroutineScope(ioDispatcher + SupervisorJob())`. Caller owns lifecycle; when the caller cancels the scope, the client's background flows (header polling, 401 reaction) stop cleanly.

**Why**: The previous signature leaked an unmanaged scope for the process lifetime. `HttpClient.close()` does not cancel that scope, because the scope is unreachable from the returned `HttpClient`. This ADR inverts ownership: caller scope → SDK uses it → caller cancels it → SDK cleans up.

**Consequence**: Breaking change for YallaClient — its Koin `single<HttpClient>(...)` calls must supply a scope. YallaClient's `chore/sdk-phase2-either-flip` branch absorbs this alongside the Either flip (or a second scratch branch if separation is cleaner). After this ADR, long-running tests can construct and tear down an `HttpClient` inside a `runTest { ... }` block without leaking coroutines.

Decided: 2026-04-21. Part of Phase 2 of the v1.0 launch.

---

## ADR-012: HTTP 3xx responses map to `DataError.Network.Client`

**Decision**: 3xx HTTP status codes that reach `safeApiCall` map to `DataError.Network.Client`. Rationale: Ktor's `HttpClient` follows redirects automatically when `Location` is present; a 3xx surfacing from `safeApiCall` means the server returned an unfollowable redirect (missing/invalid `Location`), which is a client-facing protocol issue (caller's request triggered an unreachable redirect) rather than a server error.

**Why**: Option considered and rejected: a dedicated `DataError.Network.Redirect` type. That would be semantically cleaner but introduces a sealed-hierarchy addition that breaks exhaustive `when` at every caller. Under full-risk pre-1.0 mode we could accept the break, but the marginal benefit of a redirect-specific type doesn't justify touching every downstream `when`.

**Consequence**: Non-breaking. KDoc on the mapping documents the rationale so future readers don't re-litigate.

Decided: 2026-04-21. Part of Phase 2 of the v1.0 launch.

---

## ADR-013: `LocationManager` caller-owned `CoroutineScope`

**Decision**: `LocationManager`'s primary constructor takes a `CoroutineScope` parameter. The SDK does not internally construct `CoroutineScope(SupervisorJob() + Dispatchers.Main)`. Caller owns lifecycle; cancelling the scope stops all in-flight tracking. The `close()` method is removed — there is nothing for the SDK to close when it doesn't own the scope.

Signature:

```kotlin
class LocationManager(
    val locationTracker: LocationTracker,
    private val scope: CoroutineScope,
    private val defaultLocation: GeoPoint = DEFAULT_LOCATION,
) : LocationProvider
```

**Why**: Same root cause as ADR-011 (`createHttpClient`): the previous signature leaked an unmanaged scope for the process lifetime. Callers who forgot to call `close()` leaked the scope silently. This ADR inverts ownership: caller scope → SDK uses it → caller cancels it → SDK cleans up.

**Consequence**: Breaking. Every YallaClient `LocationManager(...)` construction must pass a scope — typically a process-lifetime single in the Koin graph:

```kotlin
val locationModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single { LocationManager(get(), get()) }
}
```

YallaClient's `chore/sdk-phase3-bridge` branch carries the call-site migration in lockstep.

Decided: 2026-04-22. Part of Phase 3 of the v1.0 launch.
