# Module data

> Network, local persistence, and HTTP plumbing for the SDK.

## What this is

- `safeApiCall` + `retryWithBackoff` for HTTP error mapping to
  `Either<DataError.Network, T>`. The sole producer of `DataError.Network.*`
  variants; everything that crosses the wire goes through this.
- `createHttpClient` — `HttpClient` factory. Installs Ktor's `Auth { bearer }`
  plugin (token from `SessionPreferences`; 401 clears the session and
  publishes `UnauthorizedSessionEvents`), the guest-mode allowlist plugin,
  reactive locale/position headers, and lenient JSON content negotiation.
- `createGuestModeGuardPlugin` — Ktor plugin that blocks non-allowlisted
  endpoints when the session is in guest mode.
- Six `*PreferencesImpl` implementing `core`'s preference contracts:
  `Config`, `Interface`, `Position`, `Session`, `Static`, `User`. Five are
  DataStore-backed (async); `StaticPreferencesImpl` is Settings-backed
  (synchronous, for startup-critical reads before DataStore hydrates).
- `createDataStore` / `createSettings` / `createHttpEngine` / `ioDispatcher` /
  `platformName` — `expect`/`actual` platform glue.
- `dataModule` — Koin module wiring every persistence singleton above.
- Generic envelope types: `ApiResponse<T>`, `ApiListResponse<T>`,
  `ApiErrorResponse`. Used as the `T` parameter of `safeApiCall<…>`.

## What this is NOT

- **Not** a domain module — no `Order`, `OrderStatus`, `Either`, `DataError`,
  no value-class identifiers (`OrderId`, `CardId`, etc.). Those live in `core`.
- **Not** a UI module — no Compose, no string resources, no themes.
- **Not** a feature data module — no `*Service` repositories, no DTO mappers,
  no use cases. Those will live in feature-specific data modules in YallaClient
  built on top of this infrastructure layer.
- **Not** a public token store — the `Auth` plugin owns the bearer token
  lifecycle internally; SDK consumers manage tokens via the `core`
  `SessionPreferences` interface, not by reaching into Ktor's plugin state.

## Usage

```kotlin
startKoin {
    modules(dataModule, networkModule)
}

val client: HttpClient by inject()

suspend fun fetchOrder(id: OrderId): Either<DataError.Network, OrderDto> =
    safeApiCall(isIdempotent = true) {
        client.get("orders/${id.raw}")
    }
```

## Notes

- **`createHttpClient` scope ownership.** The `scope` parameter hosts every
  preference-observation coroutine (locale, guest-mode, position). Cancelling
  it stops those observers cleanly; the returned `HttpClient` should be closed
  in lockstep. Do not pass a process-lifetime scope to a short-lived client —
  the observers will outlive it. The default `dataModule` binding uses a
  process-lifetime scope, suited for the `*PreferencesImpl` singletons; for
  per-lifecycle clients (e.g. an Activity-scoped HTTP client) override the
  binding in your own Koin module.
- **`Auth` plugin caching.** Ktor's `Auth { bearer }` reads tokens via
  `loadTokens` lazily and caches them internally. After an external session
  clear (e.g. user-initiated logout), one in-flight request may still carry
  the previous token; the resulting 401 triggers `refreshTokens`, which
  clears the session prefs (idempotently) and publishes
  `UnauthorizedSessionEvents`. End state is identical to the previous
  reactive-cache implementation, but the round-trip happens.
- **`StaticPreferencesImpl` startup timing.** Settings (NSUserDefaults /
  SharedPreferences) reads are synchronous, so a few keys (locale, onboarding
  stage, device-registered, guest-mode) are dual-written here so the splash
  screen can read them before DataStore hydrates. `SessionPreferences.clearSession`
  preserves the static-side values that aren't session-bound.
- **`getLongSafe` legacy migration.** `ConfigPreferencesImpl` reads bonus
  limits / balance via a `getLongSafe` shim that catches `ClassCastException`,
  defending against legacy Android installs where the value was stored as
  `Int`. The catch is JVM-only — Kotlin/Native silently casts through, so
  the path isn't exercised on iOS.
- **`safeApiCall` exception mapping.** Maps Ktor's
  `HttpRequestTimeoutException` (extends `IOException`) to
  `DataError.Network.Timeout` before the generic `IOException` branch, and
  Ktor 3.x's `JsonConvertException` (extends `ContentConvertException`) to
  `DataError.Network.Serialization` before the generic
  `SerializationException` branch.
- **Test seam in `createHttpClient`.** The `engine: HttpClientEngine? = null`
  parameter exists for tests to inject `MockEngine` directly and exercise the
  full plugin stack. Production calls omit it; the platform-resolved
  `createHttpEngine()` is used by default.

## Depends on

- `core` (api)
- `kotlinx.coroutines.core` (api)
- `kotlinx.serialization.json` (api)
- `ktor-client-core` (api)
- `koin-core` (api)
- `datastore-preferences` (api)
- `multiplatform-settings` (api)
- `ktor-client-auth`, `ktor-client-content-negotiation`,
  `ktor-serialization-kotlinx-json`, `ktor-client-logging` (implementation —
  Ktor plugins installed inside `createHttpClient`, not exposed in any public
  signature)
- `ktor-client-android` (androidMain implementation)
- `ktor-client-darwin` (iosMain implementation)
- No SDK-internal dep beyond `core`.
