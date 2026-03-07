# Data Module Refactor Design

## Purpose

Transform the `data` module from a mixed infra+feature module into a pure infrastructure skeleton for the data layer. It provides networking, local storage, and common API utilities that feature data modules build upon.

## Key Decisions

1. **Infra-only** — Remove all feature-specific DTOs, mappers, and remote models. They belong in feature modules.
2. **Split God Object** — `AppPreferencesImpl` (472 lines, 5 interfaces) becomes 5 focused implementations sharing one `DataStore` instance.
3. **Eliminate platform duplication** — `NetworkClient` Android/iOS (90% identical) becomes common `HttpClientFactory` + 1-line expect/actual for engine.
4. **Rename for clarity** — Every name must precisely describe its purpose.

## Package Structure

```
data/src/commonMain/kotlin/uz/yalla/data/
├── local/
│   ├── DataStoreFactory.kt            expect fun createDataStore()
│   ├── PreferenceKeys.kt              internal object PreferenceKeys
│   ├── SessionPreferencesImpl.kt      : SessionPreferences
│   ├── UserPreferencesImpl.kt         : UserPreferences
│   ├── ConfigPreferencesImpl.kt       : ConfigPreferences
│   ├── InterfacePreferencesImpl.kt    : InterfacePreferences
│   └── PositionPreferencesImpl.kt     : PositionPreferences
├── network/
│   ├── HttpClientFactory.kt           fun createHttpClient()
│   ├── HttpEngine.kt                  expect fun createHttpEngine()
│   ├── NetworkConfig.kt               data class NetworkConfig
│   ├── SafeApiCall.kt                 suspend fun safeApiCall()
│   └── GuestModeGuard.kt             Ktor plugin
├── api/
│   ├── ApiResponse.kt                 @Serializable data class ApiResponse<T>
│   ├── ApiListResponse.kt             @Serializable data class ApiListResponse<T>
│   └── ApiErrorResponse.kt            @Serializable data class ApiErrorResponse
└── util/
    ├── IoDispatcher.kt                expect val ioDispatcher
    └── Platform.kt                    expect val platformName

data/src/androidMain/ — 4 actual files (1-3 lines each)
data/src/iosMain/    — 4 actual files (1-3 lines each)
```

## Naming Changes

| Current | New | Reason |
|---------|-----|--------|
| `SettingsFactory` | `DataStoreFactory` | Only DataStore is used |
| `AppPreferencesImpl` | 5x `*PreferencesImpl` | SRP — one class per interface |
| `NetworkClient` (expect) | `HttpEngine` (expect) | Only engine is platform-specific |
| `provideNetworkClient()` | `createHttpClient()` | Kotlin factory convention |
| `ApiResponseWrapper<T>` | `ApiResponse<T>` | "Wrapper" redundant |
| `ApiPaginationWrapper<T>` | `ApiListResponse<T>` | No pagination metadata, just list |
| `ApiErrorBody` | `ApiErrorResponse` | Parallel with ApiResponse |
| `retryIO()` | `retryWithBackoff()` | Describes strategy, not target |
| `performLogout()` | `clearSession()` | Data layer action, not UI action |

## Removals

Feature-specific code moves to YallaClient feature modules:
- `ClientRemoteModel`, `ExecutorRemoteModel`, `OrderDetailsRemoteModel`, `BrandServiceRemoteModel`, `ServiceRemoteModel`
- `ClientMapper`, `ExecutorMapper`, `ServiceBrandMapper`
- `Mapper.kt` typealias (use extension functions instead)
- `BackendQualifier` (app-level concern)
- `createStaticSettings()` (unused)

## Key Fixes

- **Global mutable caches** in NetworkClient → constructor-injected state in HttpClientFactory
- **Unmanaged CoroutineScope** in AppPreferencesImpl → scoped to DataStore lifecycle
- **Blocking `first()` call** in request pipeline → pre-cached position value
- **Service locator** in SettingsFactory.android → proper DI parameter
- **Hardcoded strings** → constants or config parameters
- **Magic numbers** (timeouts, retry) → named constants in NetworkConfig or companion

## Documentation & Testing

- MODULE.md following core's pattern
- KDoc for all public types
- Tests: SafeApiCall, GuestModeGuard, each PreferencesImpl
