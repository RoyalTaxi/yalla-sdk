# DATA_AUDIT.md

Audit output for wave 1 of phase-2 `data` cleanup. Drives waves 2-10. Findings keyed to `CLEANUP_CRITERIA.md`. All paths absolute under `/Users/islom/StudioProjects/yalla-sdk/`.

---

## 1. AI-blob deletions (criterion 2)

### `data/src/commonMain/kotlin/uz/yalla/data/api/ApiResponse.kt`

- **2-1** lines 5-22 — full property paraphrase + `@since 0.0.1` + `@see ApiListResponse` + `@see ApiErrorResponse` cross-refs. The single information-dense sentence is "Maps the JSON structure `{ "result": { ... } }`"; the rest restates the obvious. (~3 min)
- **2-1** line 17, 18 — `@param T the type of the wrapped result` / `@property result the response payload, or null if absent` are pure paraphrase. (~1 min)

### `data/src/commonMain/kotlin/uz/yalla/data/api/ApiListResponse.kt`

- **2-1** lines 5-22 — same shape as `ApiResponse.kt`. The wire-format sentence is the only keeper. (~3 min)

### `data/src/commonMain/kotlin/uz/yalla/data/api/ApiErrorResponse.kt`

- **2-1** lines 5-17 — paraphrase + `@since 0.0.1`. The "Deserialized by `safeApiCall` to extract error messages from 4xx responses" sentence carries info; the property-level paraphrase doesn't. (~2 min)

### `data/src/commonMain/kotlin/uz/yalla/data/di/DataModule.kt`

- **2-1** lines 22-52 — class-level KDoc paraphrases the `module {}` body. The "All five async preferences implementations share a single `[DataStore]` instance" line is keeper; the bullet list of registered types is restating `single<X> { … }` declarations. (~5 min)
- **2-1** line 51 — `@since 0.0.4` ceremony tag. (~30 sec)
- **2-1** line 31, 49-50 — ADR-011 reference + `@see createDataStore`/`@see createSettings` survive (info-dense — they explain the per-lifecycle scope rationale). Keep.
- Class-level scope-ownership paragraph (lines 28-32) is information-dense (explains why default is process-lifetime). Keep.

### `data/src/commonMain/kotlin/uz/yalla/data/local/ConfigPreferencesImpl.kt`

- **2-1** lines 14-28 — class KDoc carries info (clear-on-logout semantics, `getLongSafe` rationale). Keep.
- **2-1** line 27 — `@since 0.0.1` ceremony tag. (~30 sec)
- **2-1** lines 118-127 — `getLongSafe` KDoc paraphrases the body; "may have been stored as Int by an older app version" is keeper, the rest is removable. (~2 min)
- **2-1** line 126 — `@since 0.0.1` ceremony tag. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/local/InterfacePreferencesImpl.kt`

- **2-1** lines 17-33 — class KDoc carries info (dual-write to StaticPreferences, survive-session-clear). Keep.
- **2-1** line 32 — `@since 0.0.1` ceremony tag. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/local/PositionPreferencesImpl.kt`

- **2-1** lines 13-27 — class KDoc carries info (lat/lng string format, fallback chain, survive-session-clear). Keep.
- **2-1** line 26 — `@since 0.0.1` ceremony tag. (~30 sec)
- **2-1** lines 56-67 — `parseGeoPoint` KDoc paraphrases the body; "Returns `GeoPoint(0.0, 0.0)` when both `raw` and `fallbackRaw` are null/blank/unparseable" is the only info-dense line. (~2 min)
- **2-1** line 66 — `@since 0.0.1` ceremony tag. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/local/SessionPreferencesImpl.kt`

- **2-1** lines 14-32 — class KDoc carries info (clearSession semantics, dual-write StaticPreferences). Keep.
- **2-1** line 31 — `@since 0.0.1` ceremony tag. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/local/StaticPreferencesImpl.kt`

- **2-1** lines 9-27 — class KDoc carries info (the sync-startup rationale paragraph, lines 17-21, is one of the keepers in the module). Keep.
- **2-1** line 26 — `@since 0.0.7` ceremony tag. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/local/UserPreferencesImpl.kt`

- **2-1** lines 13-26 — class KDoc carries info (clear-on-logout, `PaymentKind.from` rehydration). Keep.
- **2-1** line 25 — `@since 0.0.1` ceremony tag. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/local/PreferenceKeys.kt`

- **2-1** lines 9-29 — class KDoc carries info (collision rationale, domain grouping). Keep.
- **2-1** line 28 — `@since 0.0.1` ceremony tag. (~30 sec)
- **2-2** lines 34, 37, 40, 43, 50, 53, 56, 59, 62, 65, 72, 75, 78, 81, 84, 87, 90, 93, 96, 99, 102, 105, 145, 148, 151, 154, 157, 160, 167, 170 — every key has a one-liner like `/** OAuth access token for authenticated API requests. */` that is the property name spelled out. ~30 lines of comment redundancy. (~10 min)
- **2-1** lines 110-114 — `SESSION_KEYS` doc carries info (clearSession contract); keep.
- **2-1** line 160 — `Default value for [ONBOARDING_STAGE] when no stage has been persisted yet` is paraphrase of the constant name. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/local/DataStoreFactory.kt`

- **2-1** lines 6-23 — class KDoc on the `expect fun`. The platform-routing line ("Android stores data in the app's internal files directory. iOS stores data in the documents directory.") is mostly redundant with the `@actual` impls' KDocs. The "registered as a Koin singleton" line is info-dense. (~3 min)
- **2-1** line 22 — `@since 0.0.1` ceremony tag. (~30 sec)
- **2-1** lines 17-21 — five `@see` cross-refs to every `*PreferencesImpl` consumer. Adds no information; the relationship is obvious from `dataModule.kt`. (~1 min)

### `data/src/commonMain/kotlin/uz/yalla/data/local/SettingsFactory.kt`

- **2-1** lines 5-16 — same shape as `DataStoreFactory.kt`. Platform-routing paraphrase. (~2 min)
- **2-1** line 15 — `@since 0.0.7` ceremony tag. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/network/HttpClientFactory.kt`

- **2-1** lines 34-69 — long KDoc on `createHttpClient`. Mostly information-dense (scope ownership rationale at lines 47-54 is keeper; the cache/header explanation is keeper). The `@param`/`@return`/`@see` ceremony block (lines 56-68) repeats the signature. (~5 min)
- **2-1** line 69 — `@since 0.0.5` ceremony tag. (~30 sec)
- **2-2** none.

### `data/src/commonMain/kotlin/uz/yalla/data/network/SafeApiCall.kt`

- **2-1** lines 23-49 — class KDoc on `safeApiCall`. The error-mapping table (lines 30-41) is information-dense and load-bearing for callers. The `@param`/`@return`/`@see` ceremony block below it is the standard cruft. (~3 min)
- **2-1** line 49 — `@since 0.0.1` ceremony tag. (~30 sec)
- **2-1** lines 126-145 — `retryWithBackoff` KDoc. The behavioral paragraph (lines 128-132) is info-dense; the `@param times`/`@param initialDelay`/etc. block paraphrases. (~3 min)
- **2-1** line 144 — `@since 0.0.1` ceremony tag. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/network/GuestModeGuard.kt`

- **2-1** lines 7-15, 18-40 — both KDocs are mostly info-dense. The cross-references explain how `GuestBlockedException` flows to `DataError.Network.Guest` via `safeApiCall`, and the configurable-whitelist paragraph is real product knowledge. Keep.
- **2-1** lines 14, 39 — two `@since 0.0.1` ceremony tags. (~1 min)

### `data/src/commonMain/kotlin/uz/yalla/data/network/NetworkConfig.kt`

- **2-1** lines 3-25 — class KDoc carries info (per-property semantics, default rationale for `guestAllowedSegments`). Keep.
- **2-1** line 24 — `@since 0.0.1` ceremony tag. (~30 sec)
- **2-1** lines 35-44 — `DEFAULT_GUEST_ALLOWED_SEGMENTS` doc carries info (legacy whitelist provenance, ordered list rationale). Keep.
- **2-1** line 43 — `@since 0.0.9` ceremony tag. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/network/HttpEngine.kt`

- **2-1** lines 5-16 — `expect fun createHttpEngine` KDoc. The platform-routing paragraph ("Android uses the Ktor Android engine backed by `HttpURLConnection`, iOS uses the Ktor Darwin engine backed by `NSURLSession`.") is mostly carried by the `@actual` impls. The "Called by `createHttpClient`" line is the single keeper. (~2 min)
- **2-1** line 15 — `@since 0.0.5` ceremony tag. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/util/IoDispatcher.kt`

- **2-1** lines 5-19 — same shape. Platform-routing paraphrase + `@since` ceremony. (~2 min)
- **2-1** line 17 — `@since 0.0.1` ceremony tag. (~30 sec)

### `data/src/commonMain/kotlin/uz/yalla/data/util/Platform.kt`

- **2-1** lines 3-12 — KDoc paraphrases the actual values ("Returns `\"android\"` or `\"ios\"`"). The `User-Agent-OS` consumer cross-ref is info-dense. (~2 min)
- **2-1** line 11 — `@since 0.0.5` ceremony tag. (~30 sec)

### `data/src/androidMain/kotlin/uz/yalla/data/local/DataStoreFactory.android.kt`

- **2-1** lines 14-24 — KDoc on the `actual fun`. The "stores in the app's internal files directory" line is keeper. The repeated routing info that's already in `commonMain` is removable. (~2 min)
- **2-1** line 23 — `@since 0.0.1` ceremony tag. (~30 sec)

### `data/src/androidMain/kotlin/uz/yalla/data/local/SettingsFactory.android.kt`

- **2-1** lines 11-20 — same shape. (~1 min)
- **2-1** line 19 — `@since 0.0.7` ceremony tag. (~30 sec)

### `data/src/androidMain/kotlin/uz/yalla/data/network/HttpEngine.android.kt`

- **2-1** lines 6-14 — entire KDoc is restating "uses the Ktor Android engine". The `commonMain` `expect` already says this. (~1 min)
- **2-1** line 14 — `@since 0.0.5` ceremony tag. (~30 sec)

### `data/src/androidMain/kotlin/uz/yalla/data/util/IoDispatcher.android.kt`

- **2-1** lines 6-14 — KDoc paraphrases "delegates to `Dispatchers.IO`". (~1 min)
- **2-1** line 13 — `@since 0.0.1` ceremony tag. (~30 sec)

### `data/src/androidMain/kotlin/uz/yalla/data/util/Platform.android.kt`

- **2-1** lines 3-10 — entire 7-line KDoc says `actual val platformName: String = "android"`. Pure paraphrase. (~1 min)
- **2-1** line 9 — `@since 0.0.5` ceremony tag. (~30 sec)

### `data/src/iosMain/kotlin/uz/yalla/data/local/DataStoreFactory.ios.kt`

- **2-1** lines 14-24 — same shape as Android counterpart. Platform-routing paraphrase. (~2 min)
- **2-1** line 23 — `@since 0.0.1` ceremony tag. (~30 sec)

### `data/src/iosMain/kotlin/uz/yalla/data/local/SettingsFactory.ios.kt`

- **2-1** lines 7-16 — same shape. (~1 min)
- **2-1** line 15 — `@since 0.0.7` ceremony tag. (~30 sec)

### `data/src/iosMain/kotlin/uz/yalla/data/network/HttpEngine.ios.kt`

- **2-1** lines 6-14 — paraphrase. (~1 min)
- **2-1** line 14 — `@since 0.0.5` ceremony tag. (~30 sec)

### `data/src/iosMain/kotlin/uz/yalla/data/util/IoDispatcher.ios.kt`

- **2-1** lines 6-15 — paraphrase. The "iOS does not provide a dedicated IO dispatcher" sentence is the one keeper. (~2 min)
- **2-1** line 15 — `@since 0.0.1` ceremony tag. (~30 sec)

### `data/src/iosMain/kotlin/uz/yalla/data/util/Platform.ios.kt`

- **2-1** lines 3-10 — paraphrase. (~1 min)
- **2-1** line 9 — `@since 0.0.5` ceremony tag. (~30 sec)

### Cross-cutting bucket counts (data only)

- **2-1 (paraphrase / ceremony):** ~31 files. Approx 30 `@since` tags across commonMain + androidMain + iosMain. ~80-100 lines of paraphrase across `Api*Response.kt`, `*Factory.kt`, `*Engine.kt`, `IoDispatcher.kt`, `Platform.kt` and the actual-side counterparts. Wave-2 sed/sweep, **~45 min total**.
- **2-2 (comment redundancy):** ~30 lines of `// <property name spelled out>` style comments in `PreferenceKeys.kt`. Wave-2 sweep, **~10 min**.
- **2-3 (single-use abstractions):** none. data has no interfaces, no factory wrappers, no delegate-everything classes (the six `*PreferencesImpl` classes implement public `core` interfaces — that's the boundary, not single-use).
- **2-4 (dead code):** none. Every public function is consumed by `dataModule` / by YallaClient (`createHttpClient`, `safeApiCall`, `dataModule`, `NetworkConfig`, `ApiResponse`, `ApiListResponse`, `ioDispatcher` — verified via `grep -rh "^import uz.yalla.data\." YallaClient/ | sort -u`). `GuestBlockedException` is `internal` and consumed by `safeApiCall`. `parseGeoPoint` is `internal` and consumed by `PositionPreferencesImpl` only — but that's the only consumer it needs.
- **2-5 (speculative generalization):** `NetworkConfig.deviceType` and `NetworkConfig.deviceMode` (lines 30-31) default to `"client"`/`"mobile"` and YallaClient never overrides either. Two config knobs with one value across the entire SDK + YallaClient. Borderline — could become hardcoded headers, but adding a second device type later is ~1 line if they stay. **Flag, defer to Islom.** (~5 min decision)

---

## 2. Module dependency graph (criterion 4)

`data/build.gradle.kts` declarations:

| line | declaration | scope | libs key |
| ---- | ----------- | ----- | -------- |
| 8 | `api(projects.core)` | commonMain | `core` (SDK-internal) |
| 10 | `api(libs.kotlinx.serialization.json)` | commonMain | `kotlinx-serialization-json` |
| 11 | `api(libs.ktor.client.core)` | commonMain | `ktor-client-core` |
| 12 | `api(libs.ktor.client.content.negotiation)` | commonMain | `ktor-client-content-negotiation` |
| 13 | `api(libs.ktor.serialization.kotlinx.json)` | commonMain | `ktor-serialization-kotlinx-json` |
| 14 | `api(libs.ktor.client.logging)` | commonMain | `ktor-client-logging` |
| 15 | `api(libs.kotlinx.coroutines.core)` | commonMain | `kotlinx-coroutines-core` |
| 16 | `api(libs.koin.core)` | commonMain | `koin-core` |
| 18 | `api(libs.datastore.preferences)` | commonMain | `androidx-datastore-preferences` |
| 19 | `api(libs.multiplatform.settings)` | commonMain | `multiplatform-settings` |
| 23 | `implementation(libs.ktor.client.android)` | androidMain | `ktor-client-android` |
| 24 | `implementation(libs.koin.android)` | androidMain | `koin-android` |
| 28 | `implementation(libs.kotlinx.coroutines.test)` | commonTest | — |
| 29 | `implementation(libs.ktor.client.mock)` | commonTest | — |
| 30 | `implementation(libs.multiplatform.settings.test)` | commonTest | — |
| 34 | `implementation(libs.ktor.client.darwin)` | iosMain | `ktor-client-darwin` |

### Verification grep results — `api()` exposure check

For each `api()` declaration, I grep'd `data/src/commonMain` for any reference to symbols from that lib in a public type signature.

- **`projects.core`** (line 8) — `core` types in public signatures: `DataError.Network` (`SafeApiCall.kt:13`), `Either` (`SafeApiCall.kt:14`), `GeoPoint` (`HttpClientFactory.kt:25`), `*Preferences` interfaces (param types of `createHttpClient`), `UnauthorizedSessionEvents` (consumed in body, but not in signature; the cross-module dependency is real because the *signature* uses `SessionPreferences` which is from `core`). **Keep `api`.**
- **`kotlinx.serialization.json`** (line 10) — `@Serializable` annotations on public data classes (`ApiResponse`, `ApiListResponse`, `ApiErrorResponse`). `Json` itself is referenced only inside `createHttpClient`'s body (`HttpClientFactory.kt:21`). The `@Serializable` annotation type comes from `kotlinx-serialization-core` (transitive of `-json`); strictly, consumers only need `-core` to compile types tagged `@Serializable`. **Borderline: `kotlinx-serialization-json` could be `implementation` if YallaClient brings its own** (it does, via Ktor). Recommend **demote to `implementation`** — saves transitive size; YallaClient already declares `kotlinx-serialization-json` directly. Verify by attempting to compile YallaClient against demoted data; if it fails, revert. (~30 min decision + verify)
- **`ktor.client.core`** (line 11) — `HttpClient` is the return type of `createHttpClient` and constructor type for client-scope-owning callers (YallaClient `DataModule.kt:47`). `HttpClientConfig<*>` is in the param shape of `inspektifySetup`. **Keep `api`.**
- **`ktor.client.content.negotiation`** (line 12) — `ContentNegotiation` plugin is referenced **only** in the body of `createHttpClient` (`HttpClientFactory.kt:9`, `HttpClientFactory.kt:147`). No public signature exposes it. **Demote to `implementation`.** (~5 min)
- **`ktor.serialization.kotlinx.json`** (line 13) — `json {}` config DSL is referenced **only** in the body of `createHttpClient` (`HttpClientFactory.kt:16`, `HttpClientFactory.kt:148-155`). No public signature exposes it. **Demote to `implementation`.** (~5 min)
  - **Caveat:** `commonTest` source set imports `io.ktor.serialization.kotlinx.json.json` and `io.ktor.client.plugins.contentnegotiation.ContentNegotiation` from `SafeApiCallTest.kt:13`, `HttpClientFactoryIntegrationTest.kt:18`, `SafeApiCallIntegrationTest.kt:12`. If `commonMain` demotes both to `implementation`, `commonTest` no longer inherits them. **Add explicit `implementation` lines for both libs in `commonTest.dependencies` block** in the same wave-3 commit. (~5 min)
- **`ktor.client.logging`** (line 14) — `LogLevel` and `Logging` plugin referenced **only** in `createHttpClient` body (`HttpClientFactory.kt:11-12`, `HttpClientFactory.kt:100-102`). No public signature exposes them. **Demote to `implementation`.** (~5 min)
- **`kotlinx.coroutines.core`** (line 15) — `CoroutineScope` is a param of `createHttpClient` (line 76); `StateFlow<Boolean>` is a param of `createGuestModeGuardPlugin` (line 42). Both in public signatures. **Keep `api`.**
- **`koin.core`** (line 16) — `val dataModule = module {}` returns `org.koin.core.module.Module`, the inferred public type of `dataModule`. YallaClient's `DataModule.kt` imports `import uz.yalla.data.di.dataModule` and feeds it to `Koin.modules(...)` — needs `Module` in classpath. **Keep `api`.**
- **`datastore.preferences`** (line 18) — `DataStore<Preferences>` is the return type of `expect fun createDataStore()` (`DataStoreFactory.kt:24`). Public. **Keep `api`.** Note: not directly imported by YallaClient (verified via the YallaClient `import uz.yalla.data.` grep), but the `*PreferencesImpl` constructor params surface it via Koin DI graph (consumers don't import directly; they let Koin wire it). API exposure is real.
- **`multiplatform.settings`** (line 19) — `Settings` is the return type of `expect fun createSettings()` (`SettingsFactory.kt:17`). Public. **Keep `api`.** Same note as above re: Koin DI surfacing.

### Verification grep results — usage check

- **`ktor.client.android`** (androidMain line 23) — `import io.ktor.client.engine.android.Android` in `HttpEngine.android.kt:4`. Used. Keep.
- **`koin.android`** (androidMain line 24) — `grep -rn "import org.koin.android" data/src` returns **zero matches**. **Drop the dep entirely.** Wave-3. (~2 min)
- **`ktor.client.darwin`** (iosMain line 34) — `import io.ktor.client.engine.darwin.Darwin` in `HttpEngine.ios.kt:4`. Used. Keep.

### Recommended `Depends on` block for `data/MODULE.md`

```
## Depends on

- `core` — `Either`, `DataError.Network`, `*Preferences` interfaces,
  `UnauthorizedSessionEvents`, `GeoPoint`. Sole SDK-internal dep.
- `kotlinx-serialization-json` — `@Serializable` envelope types.
- `kotlinx-coroutines-core` — `CoroutineScope`, `StateFlow` in public surface.
- `ktor-client-core` — `HttpClient`, `HttpClientConfig` in public surface.
- `koin-core` — `dataModule` (`Module`).
- `androidx-datastore-preferences` — `DataStore<Preferences>` from `createDataStore`.
- `multiplatform-settings` — `Settings` from `createSettings`.

Internal-only (`implementation`-scoped):
`ktor-client-content-negotiation`, `ktor-serialization-kotlinx-json`,
`ktor-client-logging`. Platform engines: `ktor-client-android`,
`ktor-client-darwin`.
```

(Demote `ktor-client-content-negotiation`, `ktor-serialization-kotlinx-json`, `ktor-client-logging` to `implementation`. Drop `koin-android`. Optionally demote `kotlinx-serialization-json` to `implementation` after YallaClient verify pass.)

### SDK-internal deps confirmation

- Only one SDK-internal dep: `:core`. As expected per the brick stack.
- No cycles. data → core is a one-way edge; core has no SDK-internal deps (verified in CORE_AUDIT.md §2).
- No surprising imports.

---

## 3. Restructure candidates (criterion 9-3)

### `wc -l` summary (commonMain + platforms)

```
189  network/HttpClientFactory.kt          (longest)
174  local/PreferenceKeys.kt
171  network/SafeApiCall.kt
133  local/ConfigPreferencesImpl.kt
 77  local/SessionPreferencesImpl.kt
 77  local/PositionPreferencesImpl.kt
 77  local/InterfacePreferencesImpl.kt
 75  local/UserPreferencesImpl.kt
 67  local/StaticPreferencesImpl.kt
 66  di/DataModule.kt
 54  network/GuestModeGuard.kt
 52  network/NetworkConfig.kt
 39  iosMain/local/DataStoreFactory.ios.kt
 34  androidMain/local/DataStoreFactory.android.kt
 28  androidMain/local/SettingsFactory.android.kt
 24  local/DataStoreFactory.kt
 24  api/ApiResponse.kt
 24  api/ApiListResponse.kt
 19  util/IoDispatcher.kt
 19  api/ApiErrorResponse.kt
 18  iosMain/local/SettingsFactory.ios.kt
 17  network/HttpEngine.kt
 17  local/SettingsFactory.kt
 …
```

### God-file candidates (>300 lines or >5 distinct responsibilities)

- **No file >300 lines.** Criterion 11's god-file threshold not triggered.
- **`HttpClientFactory.kt` (189 lines)** is the longest. Responsibilities: `createHttpClient` (the public factory), `handleUnauthorized` (file-private 401 handler), `String?.extractBearerToken` (file-private bearer parser), three private constants (`BEARER_PREFIX`, `REQUEST_TIMEOUT_MS`, `CONNECT_TIMEOUT_MS`, `SOCKET_TIMEOUT_MS`). One public function + 2 file-private helpers + constants — **cohesive**, single concern (HTTP-client construction). Not a god file. Keep as-is.
- **`PreferenceKeys.kt` (174 lines)** — single `internal object` with ~30 keys, organized into 5 region blocks (Session / User / Config / Interface / Position) plus `SESSION_KEYS` list. One concern (DataStore key registry). Not a god file. Keep as-is.
- **`SafeApiCall.kt` (171 lines)** — `safeApiCall` + `retryWithBackoff` + 4 retry constants. Two top-level functions; the retry helper is `@PublishedApi internal` because `safeApiCall` is `inline`. Cohesive — both functions are the network error-mapping pipeline. Keep as-is.

### Nested-package check

- `data/src/commonMain/kotlin/uz/yalla/data/`
  - `api/` — 3 files: `ApiResponse.kt`, `ApiListResponse.kt`, `ApiErrorResponse.kt`. Cohesive.
  - `di/` — 1 file: `DataModule.kt`. Single Koin module entry point.
  - `local/` — 9 files: factories + impls + keys (see below).
  - `network/` — 5 files: `HttpClientFactory`, `SafeApiCall`, `NetworkConfig`, `GuestModeGuard`, `HttpEngine`. Cohesive.
  - `util/` — 2 files: `IoDispatcher`, `Platform`. Cohesive.

- **No organization-only nesting** that wraps a single file or has visibility-narrowing rationale missing. Top-level packages are flat and idiomatic. **No flatten candidates.**

### `local/` sub-package decision

The prompt asks whether the six `*PreferencesImpl` should move to a `local/preferences/` sub-package for visual separation. Current contents of `local/`:

```
DataStoreFactory.kt          (factory — expect)
SettingsFactory.kt           (factory — expect)
PreferenceKeys.kt            (key registry — internal object)
ConfigPreferencesImpl.kt     (impl)
InterfacePreferencesImpl.kt  (impl)
PositionPreferencesImpl.kt   (impl)
SessionPreferencesImpl.kt    (impl)
StaticPreferencesImpl.kt     (impl)
UserPreferencesImpl.kt       (impl)
```

**Recommendation: leave as-is.** Reasons:
1. Six files of the same kind in one directory is well within readable bounds; precedent from CORE_AUDIT.md §3 declined to split similar-shaped groups (`order/Order.kt` 9 nested types in one file, kept).
2. The sub-package would force a `local.preferences` import path that doesn't carry information beyond what's already in the class names (`*PreferencesImpl`).
3. The factory + key registry + impls form a single cohesive layer (DataStore-backed local persistence). Splitting impls out makes the package hop more in casual reading.

Only flag this for split if Islom prefers visual separation; otherwise no action. (~0 min if unchanged, ~15 min if split — file moves + import updates only)

---

## 4. Quality / rewrite candidates (criterion 11)

### `data/src/commonMain/kotlin/uz/yalla/data/network/SafeApiCall.kt`

- Lines 55-124 — `try { … } catch (… ) { … }` chain mapping HTTP exceptions to `Either<DataError.Network, T>`. Per CLAUDE.md, **try/catch around network calls is exactly what data is for**. This is correct, not a violation. Keep.
- Lines 73-78, 96-101 — two near-identical inner try/catch blocks that pull `ApiErrorResponse` from a 4xx body, fall back to `null` on parse failure. Two pieces of duplicated logic across the status-code path and the `ClientRequestException` path. **Suggest:** extract a `private suspend fun HttpResponse.tryReadErrorMessage(): String?` to deduplicate. ~10 lines saved. (~15 min) Sub-100-line; lands in the wave commit without gate.
- Lines 151-167 of `SafeApiCallTest.kt` — known gap: `JsonConvertException` (Ktor 3.x) is not caught by the `SerializationException` branch in `safeApiCall`. The test is currently `assertFailsWith<JsonConvertException>` — documenting the gap rather than mapping it. **Real bug.** Fix: add `catch (_: io.ktor.serialization.JsonConvertException) { Either.Failure(DataError.Network.Serialization) }` (or use the parent `ContentConvertException`). (~10 min) Sub-100-line, lands in wave-4 commit.
- Lines 183-195 of `HttpClientFactoryIntegrationTest.kt` (for context, not data/src/commonMain): noted that `HttpRequestTimeoutException` extends `IOException` and gets routed to `Connection`, not `Timeout`. Same shape — needs an explicit `catch` before the generic `IOException`. (~5 min) Sub-100-line.

### `data/src/commonMain/kotlin/uz/yalla/data/network/HttpClientFactory.kt`

- Lines 79-95 — manual `MutableStateFlow` cache + 4 `scope.launch { … collectLatest { … } }` blocks observing each preference Flow. This is not a violation per se, but it's a recurring pattern (4 caches × 4 launches, each 2 lines). Could collapse into a tiny helper:
  ```kotlin
  private fun <T> CoroutineScope.cacheFlow(source: Flow<T>, initial: T): StateFlow<T> {
      val cache = MutableStateFlow(initial)
      launch { source.collectLatest { cache.value = it } }
      return cache.asStateFlow()
  }
  ```
  ~10 lines saved + clearer intent. Sub-100-line. (~20 min) Borderline — current code is already explicit and easy to read; the helper hides scope-cancel semantics. **Flag, defer.**
- Line 144 — `header("Authorization", BEARER_PREFIX + accessTokenCache.value)` is set on `defaultRequest`, which is evaluated **once** at request time using the *current* cache value. Correct given that observers feed the cache reactively. Keep.

### `data/src/commonMain/kotlin/uz/yalla/data/network/HttpClientFactory.kt` — `Auth` plugin gap (criterion 11)

- Lines 104-125 — `HttpCallValidator` + `validateResponse` + `handleResponseExceptionWithRequest` is a hand-rolled 401 handler. Per CLAUDE.md, the right approach is **Ktor's `Auth` plugin (`loadTokens` / `refreshTokens`) + `SessionStore` + `SessionExpiredSignal`**. This codebase **already has** `SessionExpiredSignal` (named `UnauthorizedSessionEvents` per core G4) and a `SessionStore`-equivalent (`SessionPreferences`).
- The current implementation:
  1. Does **not** use Ktor's `Auth` plugin.
  2. Caches the token in a `MutableStateFlow<String>` (line 80) and reads it from the cache, not from `SessionPreferences` directly.
  3. Manually parses `Bearer ...` from the request `Authorization` header in `handleUnauthorized` (line 184-189) to compare against the cached token.
  4. Manually clears `SessionPreferences` and publishes `UnauthorizedSessionEvents` on 401.
- **This is the criterion-11 architecture violation.** A proper rewrite would:
  - Install `Auth { bearer { loadTokens { sessionPrefs.accessToken.first() } refreshTokens { … } } }`.
  - Drop the cache for `accessToken`.
  - Drop `handleUnauthorized` and the bearer-token-extraction helper.
  - Keep observers for locale/guestMode/position only.
- **Estimated impact:** 60-80 lines deleted (the 401 handling block, the `extractBearerToken` helper, the `accessToken` cache + observer launch); 15-20 lines added (the `Auth { bearer { … } }` block + a `refreshTokens` no-op slot since this codebase doesn't have a refresh endpoint yet — `loadTokens` returns null on logout-equivalent, which Ktor handles by dropping the header). **Net delta ~50 lines removed.**
- **REWRITE — sub-100 LINES** if rationally scoped (no refresh endpoint to wire). Lands in wave-4 commit with a one-paragraph rationale; doesn't strictly need the gate (under 100 lines), but the architectural shift is significant — **recommend gate anyway**. The behavioral preservation pass (compare 401 → token-clear → `UnauthorizedSessionEvents.publish()` semantics before and after) is non-trivial. **(~3-4h including tests + integration verify)**

### `data/src/commonMain/kotlin/uz/yalla/data/local/ConfigPreferencesImpl.kt`

- Lines 128-133 — `private fun Preferences.getLongSafe(key: ...): Long` wraps `try { this[key] ?: 0L } catch (_: ClassCastException) { 0L }`. **try/catch in business logic** in the strict reading of CLAUDE.md, but the catch is around a typed cast in a key-value store, not business orchestration. Per CLAUDE.md's data-layer carve-out for try/catch around storage/network adapters, this is fine. Keep.

### `data/src/commonMain/kotlin/uz/yalla/data/local/UserPreferencesImpl.kt`

- Lines 52-74 — `paymentType` Flow + `setPaymentType` setter. Reads three keys (`PAYMENT_TYPE`, `CARD_ID`, `CARD_NUMBER`) and rehydrates via `PaymentKind.from`. Setter writes one or three keys depending on the sealed branch. This is the only reasonably non-trivial preference impl pattern (sealed-type-aware persistence). Idiomatic Kotlin; keep.

### `data/src/commonMain/kotlin/uz/yalla/data/local/PreferenceKeys.kt`

- Lines 30-174 — single `internal object` with all keys. Per CLAUDE.md's "constants where enums fit" target, **no rewrite needed** — these are `Preferences.Key<T>` instances, not enum-shaped sentinels.

### Architecture violations — full pass

- **try/catch in non-network business logic** — none in commonMain other than `getLongSafe` (typed-cast in DataStore adapter, fine) and the SafeApiCall network-mapping chain (sanctioned).
- **Mappers as classes / DTO extension functions** — none. data has no DTO mappers yet (no DTOs yet — only the three envelope types in `api/`). When real DTO mappers land, criterion 11 says they go as `internal object Mapper { fun … }`.
- **Service classes named `Api`** — none in data. (YallaClient has `*ApiService` mixed with `*Service` — out of scope for this audit.)
- **`InMemoryTokenProvider` / manual `Authorization` header / `AuthEventBus`** — see HttpClientFactory section above. The hand-rolled 401 handler is the closest match to "manual `Authorization` header" — `defaultRequest { header("Authorization", BEARER_PREFIX + accessTokenCache.value) }` (line 144) is exactly that. **REWRITE candidate** under the Ktor `Auth` plugin path. Already covered above.

### Untestable shape

- `createHttpClient` itself is hard to test directly because it constructs a real `HttpClient(createHttpEngine())` — `expect fun createHttpEngine()` resolves to a platform engine. The existing `HttpClientFactoryIntegrationTest.kt` works around this by **rebuilding the plugin stack manually** with `MockEngine` (lines 249-343). Documented in the test KDoc (lines 65-74). The "keep-in-sync caveat" note is honest — the test mirrors but does not directly invoke `createHttpClient`. This is the standard expect/actual testability gap; not a god-class problem.
- All other public surfaces (`safeApiCall`, `createGuestModeGuardPlugin`, `*PreferencesImpl`, `parseGeoPoint`, `NetworkConfig`) are testable today — the existing test suite proves it.

### VALUE-CLASS IDENTIFIERS — deferred G3 from CORE_AUDIT.md

This phase explicitly picks up the deferred work. Below is the inventory of raw `Int` / `String` identifiers that cross the data → core seam.

#### Sites in `core/src/commonMain` (the wire-format anchor)

| Current site | Raw type | Value-class proposal | `@Serializable`? |
| ------------ | -------- | -------------------- | ---------------- |
| `core/order/Order.kt:17` (`Order.id`) | `Int` (non-null, `@SerialName("id")`) | `OrderId(val raw: Int)` | yes (wire) |
| `core/order/Order.kt:39` (`Order.Executor.id`) | `Int` (non-null, `@SerialName("id")`) | `ExecutorId(val raw: Int)` | yes (wire) |
| `core/order/Order.kt:66` (`Order.Taxi.id`) | `Int` (non-null) | `TaxiId(val raw: Int)` | yes (wire) |
| `core/order/Executor.kt:17` (`Executor.id`) | `Int` (`@SerialName("id")`) | `ExecutorId` (shared with above) | yes (wire) |
| `core/order/ExtraService.kt:27` (`ExtraService.id`) | `Int` (`@SerialName("id")`) | `ExtraServiceId(val raw: Int)` | yes (wire) |
| `core/order/ServiceBrand.kt:14` (`ServiceBrand.id`) | `Int` (`@SerialName("id")`) | `ServiceBrandId(val raw: Int)` | yes (wire) |
| `core/location/Address.kt:20` (`Address.id`) | `Int?` (nullable, `@SerialName("id")`) | `AddressId(val raw: Int)` (nullable site stays `AddressId?`) | yes (wire) |
| `core/location/AddressOption.kt:18` (`AddressOption.id`) | `Int` (`@SerialName("id")`) | `AddressOptionId(val raw: Int)` | yes (wire) |
| `core/payment/PaymentCard.kt:9` (`PaymentCard.cardId`) | `String` (non-null, `@SerialName("cardId")`) | `CardId(val raw: String)` | yes (wire) |
| `core/payment/PaymentKind.kt:60` (`PaymentKind.Card.cardId`) | `String` | `CardId` (shared with above) | no (constructor of sealed class — not direct wire) |
| `core/payment/PaymentKind.kt:74` (`PaymentKind.from.cardId`) | `String?` (factory param) | `CardId?` | no |

The `id: String` in core's enums (`OrderStatus.id`, `PaymentKind.id`, `PlaceKind.id`, `PointKind.id`, `LocaleKind.code`, `MapKind.id`, `ThemeKind.id`, `GenderKind.id`) is the *enum discriminator string*, not an identity reference. Don't wrap those.

#### Sites in `data/src/commonMain` (the consumer / persistence side)

| Current site | Raw type | Touch needed |
| ------------ | -------- | ------------ |
| `data/local/UserPreferencesImpl.kt:55-57, 66-67` (`cardId` field of `PaymentKind.Card`) | `String` | rewrite the `PaymentKind.from(... cardId = prefs[CARD_ID] ...)` call to `cardId = prefs[CARD_ID]?.let { CardId(it) }`; rewrite the setter branch to `prefs[CARD_ID] = value.cardId.raw` |
| `data/local/PreferenceKeys.kt:63` (`CARD_ID = stringPreferencesKey("cardId")`) | `String` key for the wire form | unchanged (storage stays string, consumer wraps) |

No other data-side identity references. The `data/api/*Response.kt` envelopes are generic; identity types live in the `T` parameter, set by consumers.

#### Sites in `core/preferences/` (the contract surface)

`UserPreferences.paymentType: Flow<PaymentKind>` already type-flows through the value class once `PaymentKind.Card.cardId: CardId`. No interface signature change.

#### Estimated total impact

- **~12 value-class declarations** in core (one new file per type, or grouped under `core/identity/Ids.kt` — recommend grouping; separate files are bucket 2-1 inflation).
- **~30-40 line touches in core** to swap raw types for value classes in data classes + `from()` factories.
- **~6 line touches in data** (`UserPreferencesImpl.kt` only — `paymentType` Flow + setter).
- **~80-150 line touches in YallaClient** (every consumer of `Order.id`, `Executor.id`, `Address.id`, `PaymentKind.Card.cardId`, etc. — primarily the repository layer in `data/ride/`, `data/user/`, `data/payment/`, `data/geo/`). Estimate is best-effort without compile pass.
- **~20-40 line touches in foundation/composites** (any UI-layer formatter that displays an ID directly).

**Total: 200-300 lines across SDK + YallaClient.**

**REWRITE >100 LINES — NEEDS GATE.** Cleared in CORE_AUDIT.md §9 G3 ("DEFER to phase-2 `data` plan"); now is the moment to land it.

**Recommendation:** ship as a single wave-4 commit on the cleanup branch with `refactor!:` prefix. SDK side lands first (core wave + data touch); YallaClient migration is its own follow-up (per criterion 1, batched with the consolidated migration list). The `@Serializable` value classes preserve the wire format byte-for-byte, so DTOs and persistence don't re-serialize; behavior is preserved.

**Caveat:** value classes that are nullable in storage (e.g., `Address.id: Int?`) compose poorly with kotlinx-serialization's default codec for nullable `@JvmInline value class` types. Verify with a tiny round-trip test before mass-applying — if nullable wrappers don't round-trip cleanly, leave `Address.id` as raw `Int?` and only wrap the non-null sites. (~30 min verify + implement; rolls into the gate paragraph.)

### Summary of section 4 rewrite candidates

| Item | Lines | Gate? |
| ---- | ----- | ----- |
| Extract `tryReadErrorMessage` in SafeApiCall | ~10 | no (sub-100) |
| Catch `JsonConvertException` in SafeApiCall | ~3 | no |
| Catch `HttpRequestTimeoutException` distinctly | ~3 | no |
| `cacheFlow` helper in HttpClientFactory (deferred) | ~10 | no |
| Ktor `Auth` plugin migration (HttpClientFactory) | ~50 net | **yes, recommend** |
| Value-class identifiers (G3 follow-through) | 200-300 | **yes** |

---

## 5. Promote/demote candidates (criterion 1)

Applied lego test to every public type in `data/src/commonMain`.

### Bricks (stays in data — vast majority)

`createHttpClient`, `safeApiCall`, `createGuestModeGuardPlugin`, `NetworkConfig`, `DEFAULT_GUEST_ALLOWED_SEGMENTS`, `ApiResponse<T>`, `ApiListResponse<T>`, `ApiErrorResponse`, `dataModule`, `createDataStore`, `createSettings`, `ioDispatcher`, `platformName`, `createHttpEngine`. All six `*PreferencesImpl` are `internal` — not part of the public API.

All pass the lego test:
- No hardcoded product copy (no Russian / Uzbek strings; verified via `grep -rn '[А-Яа-яЁё]' data/src/commonMain` → 0 matches).
- No screen-shaped or ViewModel-shaped types.
- No Ildam-specific business orchestration.

### Borderline — flag for Islom

- **`NetworkConfig.deviceType`** default `"client"` (line 30). The constant value `"client"` is product-specific (the operator-vs-client distinction is Ildam-shaped — Yalla has driver and dispatcher apps that would send `"driver"` / `"operator"`). **It's a default that can be overridden, so technically a brick.** But the *default value itself* is product-specific. Moving the default to YallaClient would force every consumer to pass `deviceType = "client"` explicitly. Keep the parameter, **drop the default** (let YallaClient + future-app construct `NetworkConfig` without a default). Removes one speculative knob from the SDK. **Decision for Islom.** (~5 min if approved)
- **`NetworkConfig.deviceMode`** default `"mobile"` (line 31). Same shape. (~5 min if approved)
- **`DEFAULT_GUEST_ALLOWED_SEGMENTS`** (line 45-52) — six product-specific endpoint names (`client`, `valid`, `register`, `location-name`, `cost`, `lists`). These are Ildam-specific URL-path tokens. **The list itself is an assembly artifact**, even though the mechanism (`createGuestModeGuardPlugin(allowedSegments: Set<String>)`) is a brick. **Recommend demote `DEFAULT_GUEST_ALLOWED_SEGMENTS` to YallaClient** as a constant in YallaClient's network module; SDK's `NetworkConfig.guestAllowedSegments` becomes a non-default required param (or defaults to `emptyList()`). Removes ~10 lines of product-specific knowledge from the SDK. **Decision for Islom — `refactor!:` since it changes the public default.** (~30 min)

### Demotion candidates

- See above three borderlines. None are unambiguous demotions; all flagged.

### Promotion candidates (YallaClient → data)

- **None.** YallaClient's `composeApp/src/commonMain/kotlin/uz/yalla/client/di/DataModule.kt` (read at audit time) is pure assembly: it composes three `HttpClient` instances bound to product-specific `BuildKonfig.SECRET_KEY` and `NetworkConstants.BASE_URL_*`. Nothing brick-shaped is hiding there.

### Notes about hardcoded strings

- No Russian / Uzbek string literals in `data/src/commonMain` (verified).
- Wire-format strings (header names: `"lang"`, `"brand-id"`, `"User-Agent-OS"`, `"x-position"`, `"Bearer "`, etc.) are protocol bricks — they describe the SDK's HTTP contract with the backend. Stay in data.
- `"client"`, `"mobile"`, `"android"`, `"ios"`, the six guest-allowed-segment tokens — see borderline above.

### Verdict for `MIGRATION_LIST.md`

- "## To promote into data" — empty.
- "## To demote from data" — `DEFAULT_GUEST_ALLOWED_SEGMENTS` (recommended), `NetworkConfig.deviceType`/`deviceMode` defaults (recommended).
- "## To decide" — three borderlines above.

---

## 6. Missing tests (criterion 6)

### Inventory by package

#### `data/api/`

- `ApiResponse<T>` — covered by `ApiResponseTest.kt:12-27` (deserialize happy path + null result).
- `ApiListResponse<T>` — covered by `ApiResponseTest.kt:30-45`.
- `ApiErrorResponse` — covered by `ApiResponseTest.kt:48-63`.
- **No gap.** All three envelope types fully tested.

#### `data/di/`

- `dataModule` — **no test.** A Koin module is normally tested by spinning a test Koin app and asserting `koinApplication { modules(dataModule) }` resolves all six `*Preferences` interfaces. Worth adding: 1 test file (~30 min, ~6 single-line resolution assertions). Catches accidental dep removals. **Gap.**

#### `data/local/`

- `ConfigPreferencesImpl` — covered by `ConfigPreferencesImplTest.kt`. 9 tests. Round-trip per property: support number, support telegram, info instagram/telegram, privacy policies, bonus limits, balance, isBonusEnabled, isCardEnabled, orderCancelTime. Defaults check: yes (`shouldReturnEmptyStringsAndZerosOnColdRead` tests the cold-read defaults across all 12 fields). **Missing**: `getLongSafe` `ClassCastException` fallback — the legacy-Int-stored-as-Long migration path is the *reason* the helper exists, but no test exercises it. Worth adding 1 test (~10 min) that manually writes an `Int` to a `Long` key and asserts read-back returns `0L`. Sub-cleanup gap, easy.
- `InterfacePreferencesImpl` — covered by `InterfacePreferencesImplTest.kt`. 11 tests. Round-trip per property: locale, theme, mapKind, skipOnboarding, onboardingStage. Defaults: yes. Dual-write-to-StaticPreferences: yes (locale + onboardingStage). **No gap.**
- `PositionPreferencesImpl` — covered by `PositionPreferencesImplTest.kt`. 6 tests. Round-trip + GPS-fallback-to-map + zero default. **No gap.**
- `SessionPreferencesImpl` — covered by `SessionPreferencesImplTest.kt`. 11 tests. Round-trip per property: accessToken, firebaseToken, isGuestMode, isDeviceRegistered. `clearSession` semantics: yes (clears both static and DataStore copies). Dual-write-to-StaticPreferences: yes (guestMode + deviceRegistered). **No gap.**
- `StaticPreferencesImpl` — covered by `StaticPreferencesImplTest.kt`. 11 tests. Round-trip + defaults + key-prefix isolation + cross-instance shared state. **No gap.**
- `UserPreferencesImpl` — covered by `UserPreferencesImplTest.kt`. 9 tests. Round-trip per property + `PaymentKind.Card`/`Cash` switching + cold-read defaults to `Cash`. **No gap.**
- `parseGeoPoint` — covered by `ParseGeoPointTest.kt`. 9 tests. Happy parse + null/blank/empty/malformed/missing-longitude/negative + fallback chain. **No gap.**
- `PreferenceKeys` — covered by `PreferenceKeysTest.kt`. 1 test (asserts uniqueness across all 29 keys). **No gap.** Optional addition: assert `SESSION_KEYS` excludes interface + position keys (fails today if someone naively adds `LOCALE_TYPE` to `SESSION_KEYS`). (~5 min, low value.)

#### `data/network/`

- `safeApiCall` — covered by `SafeApiCallTest.kt`. 12 tests. Maps every documented `DataError.Network.*` variant **except** `Serialization`. Documented gap at lines 151-167: `JsonConvertException` (Ktor 3.x) is not caught by the `SerializationException` branch — the test currently `assertFailsWith<JsonConvertException>` rather than `assertEquals(DataError.Network.Serialization, ...)`. **Real bug**, see section 4 above for the fix; once fixed, the test inverts to assert the success-side mapping. **Gap (intentional, fix in wave-4).**
- `retryWithBackoff` — covered by `RetryWithBackoffTest.kt`. 6 tests. Idempotent + non-idempotent + IOException + SocketTimeoutException + non-retryable + exhaustion. **No gap.**
- `safeApiCall` integration with retry — covered by `SafeApiCallIntegrationTest.kt`. 2 tests. **No gap.**
- `createGuestModeGuardPlugin` — covered by `GuestModeGuardTest.kt` (5 tests) + `GuestModeGuardConfigTest.kt` (5 tests). Whitelist behavior + path-segment matching + trailing slash + custom config + empty whitelist + guest-mode-off bypass. **No gap.**
- `NetworkConfig` — covered indirectly by `GuestModeGuardConfigTest.kt:30-50`. The default-segments check + custom-config flow exercise the data class. **No gap.**
- `DEFAULT_GUEST_ALLOWED_SEGMENTS` — covered by `GuestModeGuardConfigTest.kt:30-50`. **No gap.**
- `createHttpClient` — covered indirectly by `HttpClientFactoryIntegrationTest.kt` (5 tests, with a documented "keep-in-sync caveat" because the platform engine forces a hand-rebuild of the plugin stack). Tests cover: 401 → `UnauthorizedSessionEvents` emit + session clear, IOException retry on idempotent, guest-mode block, generic IOException → `Connection`, SocketTimeoutException → `Timeout`, scope-cancel-stops-observer. **No gap on observable behavior, BUT:** integration test does **not** assert that the Auth flow is wired via Ktor's `Auth` plugin (because it isn't — see section 4). When the rewrite lands, the integration test's hand-rebuild of `HttpCallValidator` vs. `Auth` swap needs a fresh test asserting `Auth` plugin's `loadTokens`/`refreshTokens` slots are populated correctly. **Future gap, blocked on the rewrite.**

#### `data/util/`

- `ioDispatcher` — `expect val`. Platform-specific behavior. Untestable in `commonTest` (the actual returns `Dispatchers.IO` on Android and `Dispatchers.Default` on iOS — `Dispatcher` instances aren't structurally equality-comparable in a meaningful way). **Untestable platform code; not a "missing test" gap.**
- `platformName` — `expect val`. Same shape. The actuals are `"android"` and `"ios"` literal strings. Could test in `androidUnitTest` and `iosTest` source sets (which don't exist yet). Adding them is ~10 min per platform, but **of marginal value** — the actual is one line of `actual val platformName = "android"`. **Flag, defer.**

#### Platform-specific `expect`/`actual` (androidMain / iosMain)

- `createDataStore.android.kt` — uses `Context.filesDir`. Not testable in `commonTest`. Robolectric or instrumentation test on real Android emulator could exercise it; current test bar is unit-test-only via `InMemoryDataStore` fake. **Untestable platform code.**
- `createDataStore.ios.kt` — uses `NSFileManager`. Same shape. **Untestable platform code.**
- `createSettings.android.kt` — uses `Context.getSharedPreferences`. **Untestable platform code in current setup.**
- `createSettings.ios.kt` — uses `NSUserDefaults`. **Untestable platform code in current setup.**
- `createHttpEngine.android.kt` — `Android.create()`. Same. **Untestable platform code.**
- `createHttpEngine.ios.kt` — `Darwin.create()`. Same. **Untestable platform code.**
- `ioDispatcher.android.kt` — `Dispatchers.IO`. As noted above. **Untestable.**
- `ioDispatcher.ios.kt` — `Dispatchers.Default`. **Untestable.**
- `platformName.android.kt` — literal `"android"`. **Practically untestable** (would test that the literal equals the literal).
- `platformName.ios.kt` — literal `"ios"`. **Practically untestable.**

### Summary by package

| Package | Effort | Gap |
| ------- | ------ | --- |
| `api/` | 0 min | none |
| `di/` | ~30 min, 1 test file (Koin module resolution smoke test) | `dataModule` |
| `local/` | ~10 min, 1 test (ClassCastException fallback in `getLongSafe`) | `ConfigPreferencesImpl` legacy-int path |
| `network/` | ~10 min, 1 test rewrite (after `JsonConvertException` fix in wave-4) | `safeApiCall` `Serialization` mapping |
| `util/` | (untestable) | none actionable |
| platform-specific | (untestable in current setup) | none actionable |

**Total wave-8 effort estimate: ~50 min, ~3 new tests + 1 test rewrite.** Brings data test count from current baseline (15 test files) to 16 + assertion additions in 1 file. data is in much better shape than core was at this stage of its audit — most of the test bar is already met.

---

## 7. MODULE.md staleness (criterion 5)

Current `data/MODULE.md` (23 lines) uses the old `# Module / # Package …` format. Phase-1 form (per `bom/MODULE.md`, `resources/MODULE.md`, post-cleanup `core/MODULE.md`) is:

```
# Module <name>
> One-line tagline.

## What this is
## What this is NOT
## Usage
## Notes
## Depends on
```

### Sections to add

- **`> One-line tagline.`** — currently missing. Suggested: `> Data atoms — HTTP client, DataStore preferences, response envelopes.`
- **`## What this is`** — replace the four `# Package …` blurbs with a tight 5-7 bullet list:
  - `safeApiCall` + `retryWithBackoff` for HTTP error mapping to `Either<DataError.Network, T>`.
  - `createHttpClient` with token caching, locale/position/brand headers, 401 → `UnauthorizedSessionEvents` wiring, guest-mode allowlist plugin.
  - Six `*PreferencesImpl` implementing `core`'s preference contracts: DataStore-backed (async) for five, NSUserDefaults/SharedPreferences-backed (sync) for the sixth (`StaticPreferences`).
  - `createDataStore` / `createSettings` / `createHttpEngine` / `ioDispatcher` / `platformName` — `expect`/`actual` platform glue.
  - `dataModule` — Koin module that wires every persistence singleton.
  - Generic envelope types: `ApiResponse<T>`, `ApiListResponse<T>`, `ApiErrorResponse`.
- **`## What this is NOT`** — explicitly:
  - **Not** a domain module — no `Order`, `OrderStatus`, `Either`, `DataError` (those live in `core`).
  - **Not** a UI module — no Compose, no string resources.
  - **Not** a feature module — no repositories, no use cases (those live in YallaClient).
  - **Not** a DTO mapper layer — DTOs and the `internal object Mapper` pattern live in feature-specific data modules in YallaClient (e.g. `data/ride/`).
- **`## Usage`** — 5-10 lines showing typical SDK consumer wiring:
  ```kotlin
  startKoin {
      modules(dataModule, networkModule)
  }

  val client: HttpClient by inject()
  val sessionPrefs: SessionPreferences by inject()

  suspend fun fetchOrder(id: Int): Either<DataError.Network, OrderDto> =
      safeApiCall(isIdempotent = true) {
          client.get("orders/$id")
      }
  ```
- **`## Notes`** — fold in:
  - The scope-ownership rule for `createHttpClient` (currently in the `createHttpClient` KDoc; quote the ADR-011 line in MODULE.md too).
  - The `StaticPreferencesImpl` startup-timing rationale (sync reads needed before DataStore loads).
  - The `getLongSafe` legacy-Int migration shim in `ConfigPreferencesImpl`.
  - The known `JsonConvertException` mapping gap in `safeApiCall` (until wave-4 fixes it).
  - The hand-rolled 401 handler vs. Ktor `Auth` plugin (until wave-4 migrates).
- **`## Depends on`** — the block from section 2.

### Sections to remove

- **`# Package uz.yalla.data.api`** through **`# Package uz.yalla.data.util`** (lines 8-23) — all four per-package blurbs. Per-package KDoc lives on the source, not in MODULE.md (matches the precedent set when core's MODULE.md had its 11 per-package blurbs removed). Drop entirely.

### Sections to rewrite

- **Lines 1-6** — opening paragraph. Currently says "Data-layer infrastructure skeleton for the Yalla SDK" which is fine but doesn't follow the phase-1 tagline + structured-section format. Replace with the phase-1 form.

### Cross-check from prompt

- The current `data/MODULE.md` doesn't reference any moved/flattened package, so no stale-package cleanup needed (unlike core's `contract/preferences` legacy reference).

Total wave-10 effort: full rewrite of `data/MODULE.md` from scratch on phase-1 form. **~25 min** (slightly longer than core's 20 min because data has more `## Notes` items to fold in).

---

## 8. Reviewer notes

### Pushback on specific findings

- **Section 4 — Ktor `Auth` plugin migration.** I flagged this as the main quality rewrite, but I want to mark a strong caution: the current implementation has been battle-tested against the production Asterisk + Oktell PBX login flow, the 401-token-clear flow, the conflate-rapid-fire `UnauthorizedSessionEvents` semantics, and the bearer-string parsing edge cases (the `extractBearerToken` helper handles "Bearer " prefix, whitespace, empty token, mismatched casing). A clean rewrite to Ktor's `Auth` plugin would land most of those for free, but **the integration test today does not directly invoke `createHttpClient`** (line 56-74 keep-in-sync caveat). That means the rewrite has no regression net. **Recommend:** before applying the `Auth` rewrite in wave-4, write a *new* integration test that imports `createHttpClient` directly and uses a custom `HttpClientEngine` injected via a yet-to-be-added engine-injection seam. Sub-task; ~2h. Without that, the rewrite is high-risk for a behavior-preservation phase.

- **Section 4 — value-class identifiers.** I'm proposing 200-300 lines of touch across SDK + YallaClient. Two pushbacks:
  1. **Nullable wrappers + serialization don't compose cleanly out of the box** in kotlinx-serialization. The `Address.id: Int?` site needs verification before mass-applying; if it doesn't round-trip, leave it raw and apply the wrapper only to non-null sites. That's a partial rollout and I'm OK with that — partial value-class boundaries are still a net win.
  2. **YallaClient migration is its own follow-up project per criterion 1.** That means the SDK side lands in this phase but the YallaClient ID-touches are batched. The risk is that a partially-applied wrapper at the SDK boundary leaves YallaClient compiling against `OrderId` while everything below assumes `Int`. The `value class` `inline` semantics make this *largely* a compile-time relabel, but kotlinx-serialization classloading and Ktor body-deserialization may surface runtime quirks. **Mitigation:** in the wave-4 commit, add 4-6 round-trip tests asserting JSON serialization of `Order`, `Executor`, `Address`, `PaymentCard`, `PaymentKind.Card` produce identical bytes before and after the wrapper. If they don't, revert that specific type.

- **Section 5 — `DEFAULT_GUEST_ALLOWED_SEGMENTS` demotion.** I flagged the six product-specific URL tokens for demotion to YallaClient. Counterargument: the tokens exist in the SDK *because* there's no other consumer yet. If a second backend integration ever lands (hypothetical Driver app sharing this SDK), it would either reuse these defaults or override them — same as today. The cost of moving them is a `refactor!:` breaking change for zero functional gain. **Lean toward keep**, but flag for Islom as a borderline.

- **Section 1 — `NetworkConfig.deviceType` / `deviceMode` speculative knobs.** I called these bucket 2-5. They have one default value across SDK + YallaClient. But the moment a Driver app or operator console lands, those become the legitimate parameterization point. **Speculative-but-cheap to keep.** Don't delete.

### Cross-cutting patterns

- **`scope.launch { dataStore.edit { it[KEY] = value } }` repeats across all six DataStore-backed `*PreferencesImpl` setters.** Approximately **30+ identical pattern instances** across `ConfigPreferencesImpl`, `InterfacePreferencesImpl`, `PositionPreferencesImpl`, `SessionPreferencesImpl`, `UserPreferencesImpl`. Could extract a helper like:
  ```kotlin
  internal fun <T> CoroutineScope.persist(
      dataStore: DataStore<Preferences>,
      key: Preferences.Key<T>,
      value: T,
  ) = launch { dataStore.edit { it[key] = value } }
  ```
  Saves ~30 lines and centralizes the edit dispatch. **But:** the current spelling is greppable (every setter is one block), and the helper would obscure scope cancellation semantics. The audit is for *observation* — flag, don't auto-apply. **(~30 min if approved; sub-100 lines.)**
- **`dataStore.data.map { it[KEY].orEmpty() }` (and `.orFalse()`, `.or0()`)** repeats across the same six classes. Approximately **30+ instances**. Same cost/benefit as above. Same "flag, don't auto-apply" recommendation.
- **Class-level KDoc paragraphs across all six `*PreferencesImpl`** share a similar shape: "DataStore-backed implementation of [X]Preferences. Manages …" Could trim to "Implements [X]Preferences over DataStore." once. ~6 paragraphs × ~5 lines = ~30 lines saved. Bucket 2-1; sweep in wave-2.
- **`@since 0.0.1`/`@since 0.0.5`/`@since 0.0.7`/`@since 0.0.9` ceremony tags** appear on **~30 of 31 source files in data**. Same posture as core's wave-2 `@since` sweep. Drop in wave-2; trivial sed.
- **`@see` cross-references** between `*PreferencesImpl` and `*Factory` files form a dense web of redundant pointers. Most of them carry no information beyond "these things touch each other" — Koin's DI graph already documents that. Sweep selectively in wave-2.

### Concerns with the criteria as applied to data

- **Criterion 6's state-machine bar** doesn't apply — data has no Orbit `ContainerHost`. Same posture as core. Mention in wave-9 verification but don't try to invent state machines.

- **Criterion 11's "rewrite eligible" bar** has one big-ticket item (`Auth` plugin) and one cross-cutting item (value-class identifiers). The `Auth` rewrite is *justified* on architectural grounds (CLAUDE.md explicitly calls out the pattern), but the behavior-preservation requirement in this phase makes it a higher-risk change than the criterion suggests. Recommend a defensive "test seam first, rewrite second" sequencing in wave-4.

- **Criterion 4's "no SDK-internal dep beyond `core`" holds** — verified. `data → core` is the only SDK-internal edge.

- **Criterion 9-3's god-file threshold (>300 lines, >5 responsibilities)** is not triggered for any file in data. Largest is 189 lines (`HttpClientFactory.kt`), well under.

- **Criterion 2's bucket 2-3 (single-use abstractions)** finds nothing in data because data has no interfaces — it implements core's interfaces. The pattern would surface once feature-specific data modules in YallaClient grow into the SDK (out of phase scope).

- **Criterion 2's bucket 2-4 (dead code)** finds nothing actionable because every public function is consumed by either `dataModule` or by YallaClient (verified import grep). Compare with core, which had ~5 dead types/variants. data is a thinner module with sharper edges; less waste accumulates.

### Cross-comparison with CORE_AUDIT.md

- **CORE_AUDIT.md found ~35 file-level findings; this audit finds ~32.** Both modules carry similar amounts of `@since` ceremony + paraphrase KDoc.
- **CORE_AUDIT.md flagged 1 unused dep (`kermit`); this audit flags 1 unused dep (`koin-android`).** Same shape.
- **CORE_AUDIT.md flagged 1 wrong-scope dep (`kotlinx.datetime` `api` → `implementation`); this audit flags 3 wrong-scope deps (`ktor.client.content.negotiation`, `ktor.serialization.kotlinx.json`, `ktor.client.logging` `api` → `implementation`)** plus 1 borderline (`kotlinx.serialization.json` if YallaClient brings its own).
- **CORE_AUDIT.md found 0 god files; this audit finds 0 god files.** Same.
- **CORE_AUDIT.md found 1 orphan-flatten target (`contract/location/`); this audit finds 0 flatten targets.** data was structured cleaner from the start.
- **CORE_AUDIT.md's longest single rewrite candidate was 70 lines (DataError dead-code purge); this audit's longest is the value-class rollout at 200-300 lines (deferred from core G3 + this phase's anchor).** Bigger rewrite item, but wave-4-gateable.

---

## Summary stats

- **Section 1 findings:** ~32 file-level findings across ~31 source files. Mix of ~30 `@since` tags (every file), ~30 lines of `// <property name>` redundancy in `PreferenceKeys.kt`, ~80-100 lines of paraphrase KDoc across api/factory/expect-actual files. 0 dead code findings, 0 single-use abstraction findings, 1 borderline 2-5 (`NetworkConfig.deviceType`/`deviceMode` defaults).
- **Section 2 findings:** 1 unused dep (`koin-android`), 3 wrong-scope deps (`ktor-client-content-negotiation`, `ktor-serialization-kotlinx-json`, `ktor-client-logging` should be `implementation`), 1 borderline-demote dep (`kotlinx-serialization-json` if YallaClient verifies). 1 commonTest follow-up (declare the demoted Ktor libs as `commonTest` `implementation`).
- **Section 3 findings:** 0 god files. 0 flatten targets. 1 borderline (`local/preferences/` sub-package, recommend leave as-is).
- **Section 4 findings:** 6 quality candidates — 4 small (sub-100 lines, no gate), 2 large (gate-recommended). The large ones are the **Ktor `Auth` plugin migration** (~50 lines net, recommend gate due to behavioral risk) and the **value-class identifiers rollout** (200-300 lines across SDK + YallaClient, **gate required**, picks up deferred core G3).
- **Section 5 findings:** 0 promotion candidates, 0 unambiguous demotions, 3 borderlines (`DEFAULT_GUEST_ALLOWED_SEGMENTS`, `NetworkConfig.deviceType`, `NetworkConfig.deviceMode`).
- **Section 6 findings:** 3 small gaps (~50 min total) — Koin `dataModule` smoke test, `getLongSafe` ClassCastException fallback test, `safeApiCall` `Serialization` mapping (gap is paired with the `JsonConvertException` fix in wave-4). Platform-specific `expect`/`actual` largely untestable in current setup; flagged separately, no action.
- **Section 7 findings:** 1 full MODULE.md rewrite. Drop 4 stale per-package blurbs; add the 5 phase-1 sections.
- **Longest single rewrite candidate:** **value-class identifiers rollout — ~200-300 lines** across `core/` (12 new value-class declarations + ~30-40 line touches), `data/local/UserPreferencesImpl.kt` (~6 line touches), and YallaClient repository/feature layer (~80-150 line touches). **CROSSES THE 100-LINE GATE — NEEDS APPROVAL.** Picks up deferred CORE_AUDIT.md G3.
- **Second-longest rewrite candidate:** Ktor `Auth` plugin migration in `HttpClientFactory.kt`. ~50 lines net (60-80 deleted, 15-20 added). Sub-100, but architecturally significant — **gate-recommended even though under threshold** because behavioral preservation is non-trivial and the existing integration test doesn't directly cover `createHttpClient`.
- **Blocking issues:** none. Audit is fully derivable from the source.

---

## 9. Approval (Islom, 2026-04-29)

Decisions locked for waves 2-10. Default position: agree with subagent + assistant recommendations.

### Gate items

- **G7 — Ktor `Auth` plugin migration:** **APPROVED with defensive sequencing.** Wave 4 first writes a NEW integration test that imports `createHttpClient` directly (filling the regression-net gap the existing `HttpClientFactoryIntegrationTest` left open), THEN applies the `Auth { bearer { loadTokens; refreshTokens } }` rewrite that deletes the hand-rolled 401 handler + `extractBearerToken` helper + bearer-string parsing. `refactor!:` prefix; ~50 lines net removed; ~3-4h including the new test seam.
- **G8 — Value-class identifier rollout:** **APPROVED with nullable-verification first.** Wave 4 first writes a tiny round-trip test for `@Serializable @JvmInline value class` over a nullable site (`Address.id: Int?`) to verify kotlinx-serialization composes cleanly. If it does, mass-apply 12 value classes grouped under a single `core/identity/Ids.kt` + ~30-40 line touches in core + ~6 line touches in data (`UserPreferencesImpl`). If the nullable round-trip is broken, leave `Address.id` raw and apply wrappers only to non-null sites (partial rollout). YallaClient migration (~80-150 lines) is its own follow-up project per criterion 1; appended to `MIGRATION_LIST.md` in wave 6. `refactor!:` prefix; SDK side ~30-40 lines net.

### Quick approvals (subagent + me agree)

- **A1.** Drop unused `koin-android` androidMain dep — wave 3.
- **A2.** Demote 3 ktor `api()` → `implementation()`: `content-negotiation`, `serialization-kotlinx-json`, `client-logging` — wave 3.
- **A3.** Sweep ~30 `@since` tags across data — wave 2.
- **A4.** Sweep ~80-100 lines paraphrase KDoc per audit §1 — wave 2.
- **A5.** Drop ~30 redundant property-name comments in `PreferenceKeys.kt` — wave 2.
- **A6.** Extract `tryReadErrorMessage` in `SafeApiCall` (~10 lines saved) — wave 4.
- **A7.** Catch `JsonConvertException` in `safeApiCall` — bug fix, wave 4.
- **A8.** Catch `HttpRequestTimeoutException` distinctly so it routes to `DataError.Network.Timeout` — bug fix, wave 4.
- **A9.** Test backfill (~50 min) — wave 8: Koin `dataModule` smoke test, `getLongSafe` ClassCastException fallback test, `safeApiCall` Serialization mapping (paired with A7).
- **A10.** Full MODULE.md rewrite to phase-1 form — wave 10.
- **A11.** No god-file splits, no `local/preferences/` sub-package, no `cacheFlow`/`persist` helper extractions. Explicit > clever.

### Out of scope (kept / rejected)

- `NetworkConfig.deviceType` / `deviceMode` defaults — KEPT. Speculative-but-cheap; legitimate parameterization point if Driver/Operator app lands.
- `DEFAULT_GUEST_ALLOWED_SEGMENTS` demotion — REJECTED. No second consumer means demotion is `refactor!:` churn for zero current gain.
- Class-level KDoc trim across 6 `*PreferencesImpl` — included in wave 2's A4 sweep.
