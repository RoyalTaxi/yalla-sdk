# Data & Foundation Gold Standard Promotion

**Date:** 2026-03-17
**Status:** Draft
**Approach:** B — Comprehensive Gold

## Context

Core module set the gold standard: 100% KDoc, 17 test files, MODULE.md, Dokka setup. Data and foundation need to match.

### Current State

| Metric | Data | Foundation |
|--------|------|-----------|
| KDoc | 100% | 100% |
| Tests | 27 (5 files) | 8 (1 file) |
| MODULE.md | Yes | Yes |
| Dokka | Yes | Yes |
| Gap | SafeApiCall untested | BaseViewModel, mappers, settings untested |

### Key Finding

Data KDoc is already 100% — no gaps to fix. The only work is adding tests for SafeApiCall (the most critical untested code in the SDK).

---

## Section 1: Data Module — SafeApiCall Tests

### 1.1 SafeApiCallTest.kt

Tests the core `safeApiCall<T>()` function using Ktor MockEngine. Each test creates a MockEngine with a specific response, calls `safeApiCall`, and asserts the `Either` result.

**Test infrastructure:** Reuse existing pattern from `GuestModeGuardTest.kt` — MockEngine + `runTest`.

**Tests:**

| Test | MockEngine Response | Expected Result |
|------|-------------------|-----------------|
| `shouldReturnSuccessOnHttp200` | 200 + JSON body | `Either.Success<T>` with deserialized body |
| `shouldReturnSuccessForUnitResponse` | 200 + empty body | `Either.Success<Unit>` |
| `shouldReturnClientErrorOnHttp400` | 400 + no message | `Either.Error(DataError.Network.Client)` |
| `shouldReturnClientWithMessageOnHttp400WithBody` | 400 + ApiErrorResponse JSON | `Either.Error(DataError.Network.ClientWithMessage(400, msg))` |
| `shouldReturnServerErrorOnHttp500` | 500 | `Either.Error(DataError.Network.Server)` |
| `shouldReturnConnectionErrorOnIOException` | MockEngine throws IOException | `Either.Error(DataError.Network.Connection)` |
| `shouldReturnSerializationErrorOnMalformedJson` | 200 + malformed JSON | `Either.Error(DataError.Network.Serialization)` |
| `shouldReturnGuestErrorOnGuestBlockedException` | MockEngine throws GuestBlockedException | `Either.Error(DataError.Network.Guest)` |

**Implementation notes:**
- Need a simple `@Serializable data class TestResponse(val id: Int, val name: String)` as test fixture
- Create helper `fun mockClient(handler: MockRequestHandler): HttpClient` that configures ContentNegotiation + JSON like HttpClientFactory does
- Use `io.ktor.client.engine.mock.respond()` with specific status codes and JSON content type

### 1.2 SafeApiCallIntegrationTest.kt

End-to-end tests that exercise the full `safeApiCall` + `retryWithBackoff` pipeline together.

| Test | Scenario | Expected |
|------|----------|----------|
| `shouldRetryIdempotentCallOnIOException` | First 2 calls throw IOException, 3rd succeeds. `isIdempotent=true` | Success after retries |
| `shouldNotRetryNonIdempotentCallOnIOException` | Throws IOException. `isIdempotent=false` | Immediate Connection error |
| `shouldReturnConnectionErrorWhenRetriesExhausted` | All 3 retries throw IOException. `isIdempotent=true` | Connection error after 3 attempts |
| `shouldRetryOnSocketTimeoutException` | First call times out, second succeeds. `isIdempotent=true` | Success after 1 retry |

**Implementation notes:**
- Use `var callCount = 0` in MockEngine handler to track retry attempts
- Verify `callCount` matches expected retry count

---

## Section 2: Foundation Module — BaseViewModel Tests

### 2.1 BaseViewModelTest.kt

BaseViewModel is abstract — create a minimal `TestViewModel` subclass for testing.

```kotlin
private class TestViewModel(
    mapper: DataErrorMapper = DefaultDataErrorMapper()
) : BaseViewModel(mapper)
```

**Test infrastructure:** `runTest` + turbine for StateFlow assertions.

**Tests:**

| Test | What it exercises | Assertion |
|------|------------------|-----------|
| `shouldStartWithLoadingFalse` | Initial state | `loading.value == false` |
| `shouldShowLoadingDuringLaunchWithLoading` | `launchWithLoading` | loading becomes true then false |
| `shouldShowErrorDialogOnHandleException` | `handleException(RuntimeException())` | `showErrorDialog.value == true`, `currentErrorMessageId` set |
| `shouldShowErrorDialogOnHandleDataError` | `handleDataError(DataError.Network.Connection)` | `showErrorDialog.value == true`, mapped message set |
| `shouldDismissErrorDialog` | `dismissErrorDialog()` after error | Both `showErrorDialog` and `currentErrorMessageId` reset |
| `shouldCatchExceptionInSafeScope` | Launch failing coroutine in `safeScope` | Error dialog shown, no crash |
| `shouldMapDataErrorToCorrectMessage` | `handleDataError` with different error types | Correct StringResource per error type |

### 2.2 DefaultDataErrorMapperTest.kt

Pure function mapping — simplest tests. No coroutines needed.

| Test | Input | Expected StringResource |
|------|-------|------------------------|
| `shouldMapConnectionToNoInternet` | `DataError.Network.Connection` | `Res.string.error_no_internet` |
| `shouldMapTimeoutToConnectionTimeout` | `DataError.Network.Timeout` | `Res.string.error_connection_timeout` |
| `shouldMapClientToClientRequest` | `DataError.Network.Client` | `Res.string.error_client_request` |
| `shouldMapClientWithMessageToClientRequest` | `DataError.Network.ClientWithMessage(400, "msg")` | `Res.string.error_client_request` |
| `shouldMapServerToServerBusy` | `DataError.Network.Server` | `Res.string.error_server_busy` |
| `shouldMapSerializationToDataFormat` | `DataError.Network.Serialization` | `Res.string.error_data_format` |
| `shouldMapGuestToClientRequest` | `DataError.Network.Guest` | `Res.string.error_client_request` |
| `shouldMapUnknownToNetworkUnexpected` | `DataError.Network.Unknown` | `Res.string.error_network_unexpected` |

---

## Section 3: Foundation Module — Settings Tests

### 3.1 SettingsOptionsTest.kt

Tests factory methods and companion collections for all three option sealed classes.

| Test | What | Assertion |
|------|------|-----------|
| `shouldMapThemeKindLightToThemeOptionLight` | `ThemeOption.from(ThemeKind.Light)` | `== ThemeOption.Light` |
| `shouldMapThemeKindDarkToThemeOptionDark` | `ThemeOption.from(ThemeKind.Dark)` | `== ThemeOption.Dark` |
| `shouldMapThemeKindSystemToThemeOptionSystem` | `ThemeOption.from(ThemeKind.System)` | `== ThemeOption.System` |
| `shouldContainAllThemeOptions` | `ThemeOption.all` | size 3, contains all |
| `shouldRoundTripThemeKind` | `ThemeOption.all.forEach { from(it.kind) == it }` | All round-trip |
| `shouldMapLocaleKindUzToLanguageOptionUzbek` | `LanguageOption.from(LocaleKind.Uz)` | `== LanguageOption.Uzbek` |
| `shouldMapLocaleKindRuToLanguageOptionRussian` | `LanguageOption.from(LocaleKind.Ru)` | `== LanguageOption.Russian` |
| `shouldContainAllLanguageOptions` | `LanguageOption.all` | size 2 (Uzbek, Russian) |
| `shouldRoundTripLocaleKind` | `LanguageOption.all.forEach { from(it.kind) == it }` | All round-trip |
| `shouldMapMapKindGoogleToMapOptionGoogle` | `MapOption.from(MapKind.Google)` | `== MapOption.Google` |
| `shouldMapMapKindLibreToMapOptionLibre` | `MapOption.from(MapKind.Libre)` | `== MapOption.Libre` |
| `shouldContainAllMapOptions` | `MapOption.all` | size 2, contains all |
| `shouldRoundTripMapKind` | `MapOption.all.forEach { from(it.kind) == it }` | All round-trip |

---

## Section 4: Foundation Module — LocationMappers Tests

### 4.1 LocationMappersTest.kt

Tests 5 extension functions that convert core/data models to foundation Location types.

**Test fixtures:** Create minimal instances of `AddressOption`, `SavedAddress`, `Address`, `Order.Taxi.Route`, `Order` with known values.

| Test | Function | Assertion |
|------|----------|-----------|
| `shouldMapAddressOptionToFoundLocation` | `AddressOption.toFoundLocation()` | name, lat, lng, address preserved; placeKind null |
| `shouldMapSavedAddressToFoundLocation` | `SavedAddress.toFoundLocation()` | All fields preserved including placeKind |
| `shouldMapAddressToLocation` | `Address.toLocation()` | name, lat, lng mapped correctly |
| `shouldMapAddressToLocationWithCustomPoint` | `Address.toLocation(customGeoPoint)` | Uses custom point, not address's own |
| `shouldMapRouteToLocation` | `Order.Taxi.Route.toLocation()` | index as id, fullAddress as name |
| `shouldSortRouteLocationsByIndex` | `Order.sortedRouteLocations()` | Sorted by route index |

---

## Section 5: Summary

### New Test Files

| Module | File | Tests | Framework |
|--------|------|-------|-----------|
| data | `SafeApiCallTest.kt` | 8 | MockEngine + runTest |
| data | `SafeApiCallIntegrationTest.kt` | 4 | MockEngine + runTest |
| foundation | `BaseViewModelTest.kt` | 7 | runTest + turbine |
| foundation | `DefaultDataErrorMapperTest.kt` | 8 | kotlin.test only |
| foundation | `SettingsOptionsTest.kt` | 13 | kotlin.test only |
| foundation | `LocationMappersTest.kt` | 6 | kotlin.test only |

### Result

| Module | Before | After |
|--------|--------|-------|
| Data | 27 tests / 5 files | 39 tests / 7 files |
| Foundation | 8 tests / 1 file | 42 tests / 5 files |

### Out of Scope

- No production code changes (KDoc already 100%, MODULE.md exists, Dokka configured)
- No try-catch refactoring in SafeApiCall (HTTP exception mapping is idiomatic Ktor)
- No preference implementation tests (DataStore mock complexity, low value)
- No Compose UI tests (ObserveAsEvents, LocaleProvider — require Compose test framework)
