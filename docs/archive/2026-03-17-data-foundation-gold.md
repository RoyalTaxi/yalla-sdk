# Data & Foundation Gold Standard Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Promote data and foundation modules to gold standard by adding comprehensive tests.

**Architecture:** 6 new test files — 2 in data (SafeApiCall), 4 in foundation (BaseViewModel, DataErrorMapper, Settings, LocationMappers). No production code changes.

**Tech Stack:** kotlin.test, kotlinx.coroutines.test (runTest), Ktor MockEngine, Turbine (StateFlow testing)

---

## Chunk 1: Data Module — SafeApiCall Tests

### Task 1: SafeApiCallTest.kt — Status Code Path

**Files:**
- Create: `data/src/commonTest/kotlin/uz/yalla/data/network/SafeApiCallTest.kt`

- [ ] **Step 1: Write SafeApiCallTest with all 11 tests**

```kotlin
package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import uz.yalla.core.error.DataError
import uz.yalla.core.result.Either
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@Serializable
private data class TestResponse(val id: Int, val name: String)

class SafeApiCallTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

    private fun mockClient(
        handler: MockRequestHandleScope.() -> io.ktor.client.engine.mock.HttpResponseData,
    ) = HttpClient(MockEngine) {
        expectSuccess = false
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        engine {
            addHandler { handler() }
        }
    }

    // --- Status code path (expectSuccess = false) ---

    @Test
    fun shouldReturnSuccessOnHttp200() = runTest {
        val client = mockClient {
            respond("""{"id":1,"name":"test"}""", HttpStatusCode.OK, jsonHeaders)
        }

        val result = safeApiCall<TestResponse> { client.get("/test") }

        assertIs<Either.Success<TestResponse>>(result)
        assertEquals(1, result.data.id)
        assertEquals("test", result.data.name)
    }

    @Test
    fun shouldReturnSuccessForUnitResponse() = runTest {
        val client = mockClient {
            respond("", HttpStatusCode.OK)
        }

        val result = safeApiCall<Unit> { client.get("/test") }

        assertIs<Either.Success<Unit>>(result)
    }

    @Test
    fun shouldReturnClientErrorOnHttp400() = runTest {
        val client = mockClient {
            respond("", HttpStatusCode.BadRequest)
        }

        val result = safeApiCall<TestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Client, result.error)
    }

    @Test
    fun shouldReturnClientWithMessageOnHttp400WithBody() = runTest {
        val client = mockClient {
            respond("""{"message":"Invalid input"}""", HttpStatusCode.BadRequest, jsonHeaders)
        }

        val result = safeApiCall<TestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        val error = result.error
        assertIs<DataError.Network.ClientWithMessage>(error)
        assertEquals(400, error.code)
        assertEquals("Invalid input", error.message)
    }

    @Test
    fun shouldReturnServerErrorOnHttp500() = runTest {
        val client = mockClient {
            respond("", HttpStatusCode.InternalServerError)
        }

        val result = safeApiCall<TestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Server, result.error)
    }

    @Test
    fun shouldReturnClientErrorOnHttp3xx() = runTest {
        val client = mockClient {
            respond("", HttpStatusCode.MovedPermanently)
        }

        val result = safeApiCall<TestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Client, result.error)
    }

    @Test
    fun shouldReturnUnknownErrorOnUnexpectedStatusCode() = runTest {
        val client = mockClient {
            respond("", HttpStatusCode(600, "Custom"))
        }

        val result = safeApiCall<TestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Unknown, result.error)
    }

    @Test
    fun shouldReturnSerializationErrorOnMalformedJson() = runTest {
        val client = mockClient {
            respond("{not valid json", HttpStatusCode.OK, jsonHeaders)
        }

        val result = safeApiCall<TestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Serialization, result.error)
    }

    // --- Exception path ---

    @Test
    fun shouldReturnConnectionErrorOnIOException() = runTest {
        val client = HttpClient(MockEngine) {
            engine { addHandler { throw IOException("network down") } }
        }

        val result = safeApiCall<TestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Connection, result.error)
    }

    @Test
    fun shouldReturnTimeoutErrorOnSocketTimeoutException() = runTest {
        val client = HttpClient(MockEngine) {
            engine { addHandler { throw SocketTimeoutException("timed out") } }
        }

        val result = safeApiCall<TestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Timeout, result.error)
    }

    @Test
    fun shouldReturnGuestErrorOnGuestBlockedException() = runTest {
        val client = HttpClient(MockEngine) {
            engine { addHandler { throw GuestBlockedException("/orders") } }
        }

        val result = safeApiCall<TestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Guest, result.error)
    }
}
```

- [ ] **Step 2: Run tests**

Run: `./gradlew :data:allTests --tests "uz.yalla.data.network.SafeApiCallTest" 2>&1 | tail -20`
Expected: All 11 tests PASS

- [ ] **Step 3: Commit**

```bash
git add data/src/commonTest/kotlin/uz/yalla/data/network/SafeApiCallTest.kt
git commit -m "test(data): add SafeApiCall status code and exception path tests"
```

---

### Task 2: SafeApiCallIntegrationTest.kt — Retry Pipeline

**Files:**
- Create: `data/src/commonTest/kotlin/uz/yalla/data/network/SafeApiCallIntegrationTest.kt`

- [ ] **Step 1: Write SafeApiCallIntegrationTest with 2 tests**

```kotlin
package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import uz.yalla.core.error.DataError
import uz.yalla.core.result.Either
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@Serializable
private data class TestResponse(val id: Int, val name: String)

class SafeApiCallIntegrationTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

    @Test
    fun shouldRetryAndSucceedOnTransientFailure() = runTest {
        var callCount = 0
        val client = HttpClient(MockEngine) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler {
                    callCount++
                    if (callCount < 2) throw IOException("transient")
                    respond("""{"id":1,"name":"recovered"}""", HttpStatusCode.OK, jsonHeaders)
                }
            }
        }

        val result = safeApiCall<TestResponse>(isIdempotent = true) { client.get("/test") }

        assertIs<Either.Success<TestResponse>>(result)
        assertEquals("recovered", result.data.name)
        assertEquals(2, callCount)
    }

    @Test
    fun shouldReturnFailureWhenRetriesExhausted() = runTest {
        var callCount = 0
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    callCount++
                    throw IOException("persistent failure")
                }
            }
        }

        val result = safeApiCall<TestResponse>(isIdempotent = true) { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Connection, result.error)
        assertEquals(3, callCount)
    }
}
```

- [ ] **Step 2: Run tests**

Run: `./gradlew :data:allTests --tests "uz.yalla.data.network.SafeApiCallIntegrationTest" 2>&1 | tail -20`
Expected: Both tests PASS

- [ ] **Step 3: Commit**

```bash
git add data/src/commonTest/kotlin/uz/yalla/data/network/SafeApiCallIntegrationTest.kt
git commit -m "test(data): add SafeApiCall retry integration tests"
```

---

## Chunk 2: Foundation Module — Infra Tests

### Task 3: DefaultDataErrorMapperTest.kt

**Files:**
- Create: `foundation/src/commonTest/kotlin/uz/yalla/foundation/infra/DefaultDataErrorMapperTest.kt`

- [ ] **Step 1: Write DefaultDataErrorMapperTest with 8 tests**

```kotlin
package uz.yalla.foundation.infra

import uz.yalla.core.error.DataError
import uz.yalla.resources.Res
import uz.yalla.resources.error_client_request
import uz.yalla.resources.error_connection_timeout
import uz.yalla.resources.error_data_format
import uz.yalla.resources.error_network_unexpected
import uz.yalla.resources.error_no_internet
import uz.yalla.resources.error_server_busy
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultDataErrorMapperTest {
    private val mapper = DefaultDataErrorMapper()

    @Test
    fun shouldMapConnectionToNoInternet() {
        assertEquals(Res.string.error_no_internet, mapper.map(DataError.Network.Connection))
    }

    @Test
    fun shouldMapTimeoutToConnectionTimeout() {
        assertEquals(Res.string.error_connection_timeout, mapper.map(DataError.Network.Timeout))
    }

    @Test
    fun shouldMapClientToClientRequest() {
        assertEquals(Res.string.error_client_request, mapper.map(DataError.Network.Client))
    }

    @Test
    fun shouldMapClientWithMessageToClientRequest() {
        assertEquals(
            Res.string.error_client_request,
            mapper.map(DataError.Network.ClientWithMessage(400, "bad request"))
        )
    }

    @Test
    fun shouldMapServerToServerBusy() {
        assertEquals(Res.string.error_server_busy, mapper.map(DataError.Network.Server))
    }

    @Test
    fun shouldMapSerializationToDataFormat() {
        assertEquals(Res.string.error_data_format, mapper.map(DataError.Network.Serialization))
    }

    @Test
    fun shouldMapGuestToClientRequest() {
        assertEquals(Res.string.error_client_request, mapper.map(DataError.Network.Guest))
    }

    @Test
    fun shouldMapUnknownToNetworkUnexpected() {
        assertEquals(Res.string.error_network_unexpected, mapper.map(DataError.Network.Unknown))
    }
}
```

- [ ] **Step 2: Run tests**

Run: `./gradlew :foundation:allTests --tests "uz.yalla.foundation.infra.DefaultDataErrorMapperTest" 2>&1 | tail -20`
Expected: All 8 tests PASS

- [ ] **Step 3: Commit**

```bash
git add foundation/src/commonTest/kotlin/uz/yalla/foundation/infra/DefaultDataErrorMapperTest.kt
git commit -m "test(foundation): add DefaultDataErrorMapper tests"
```

---

### Task 4: BaseViewModelTest.kt

**Files:**
- Create: `foundation/src/commonTest/kotlin/uz/yalla/foundation/infra/BaseViewModelTest.kt`

**Note:** BaseViewModel is abstract and depends on `viewModelScope` → needs `Dispatchers.setMain`. Uses turbine for StateFlow assertions. The `launchWithLoading` test must use timing longer than `showAfter` to trigger loading=true.

- [ ] **Step 1: Write BaseViewModelTest with 8 tests**

```kotlin
package uz.yalla.foundation.infra

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import uz.yalla.core.error.DataError
import uz.yalla.resources.Res
import uz.yalla.resources.error_no_internet
import uz.yalla.resources.error_unexpected
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

private class TestViewModel(
    mapper: DataErrorMapper = DefaultDataErrorMapper()
) : BaseViewModel(mapper)

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun shouldStartWithLoadingFalse() {
        val vm = TestViewModel()
        assertFalse(vm.loading.value)
    }

    @Test
    fun shouldShowLoadingDuringLaunchWithLoading() = runTest {
        val vm = TestViewModel()

        vm.loading.test {
            assertFalse(awaitItem()) // initial

            val job = async {
                with(vm) {
                    vm.safeScope.launchWithLoading(
                        showAfter = 1.milliseconds,
                        minDisplayTime = 1.milliseconds,
                    ) {
                        delay(100.milliseconds)
                    }
                }
            }

            assertTrue(awaitItem()) // loading shown
            assertFalse(awaitItem()) // loading hidden
            job.await()
        }
    }

    @Test
    fun shouldShowErrorDialogOnHandleException() {
        val vm = TestViewModel()

        vm.handleException(RuntimeException("boom"))

        assertTrue(vm.showErrorDialog.value)
        assertEquals(Res.string.error_unexpected, vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldShowErrorDialogOnHandleDataError() {
        val vm = TestViewModel()

        vm.handleDataError(DataError.Network.Connection)

        assertTrue(vm.showErrorDialog.value)
        assertEquals(Res.string.error_no_internet, vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldDismissErrorDialog() {
        val vm = TestViewModel()
        vm.handleException(RuntimeException("boom"))

        vm.dismissErrorDialog()

        assertFalse(vm.showErrorDialog.value)
        assertNull(vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldCatchExceptionInSafeScope() = runTest {
        val vm = TestViewModel()

        vm.safeScope.launchWithLoading {
            throw RuntimeException("unhandled")
        }

        // Exception caught by handler, not propagated
        assertTrue(vm.showErrorDialog.value)
        assertNotNull(vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldCatchExceptionInLaunchSafe() = runTest {
        val vm = TestViewModel()

        with(vm) {
            vm.safeScope.launchSafe {
                throw RuntimeException("unhandled")
            }
        }

        assertTrue(vm.showErrorDialog.value)
        assertNotNull(vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldMapDataErrorToCorrectMessage() {
        val vm = TestViewModel()

        vm.handleDataError(DataError.Network.Connection)
        assertEquals(Res.string.error_no_internet, vm.currentErrorMessageId.value)

        vm.dismissErrorDialog()

        vm.handleDataError(DataError.Network.Server)
        val serverMsg = vm.currentErrorMessageId.value
        assertNotNull(serverMsg)
        // Server maps to error_server_busy, which is different from Connection's error_no_internet
        assertTrue(serverMsg != Res.string.error_no_internet)
    }
}
```

- [ ] **Step 2: Run tests**

Run: `./gradlew :foundation:allTests --tests "uz.yalla.foundation.infra.BaseViewModelTest" 2>&1 | tail -20`
Expected: All 8 tests PASS

- [ ] **Step 3: Commit**

```bash
git add foundation/src/commonTest/kotlin/uz/yalla/foundation/infra/BaseViewModelTest.kt
git commit -m "test(foundation): add BaseViewModel tests"
```

---

## Chunk 3: Foundation Module — Settings & Location Tests

### Task 5: SettingsOptionsTest.kt

**Files:**
- Create: `foundation/src/commonTest/kotlin/uz/yalla/foundation/settings/SettingsOptionsTest.kt`

- [ ] **Step 1: Write SettingsOptionsTest with 15 tests**

```kotlin
package uz.yalla.foundation.settings

import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingsOptionsTest {

    // --- ThemeOption ---

    @Test
    fun shouldMapThemeKindLightToThemeOptionLight() {
        assertEquals(ThemeOption.Light, ThemeOption.from(ThemeKind.Light))
    }

    @Test
    fun shouldMapThemeKindDarkToThemeOptionDark() {
        assertEquals(ThemeOption.Dark, ThemeOption.from(ThemeKind.Dark))
    }

    @Test
    fun shouldMapThemeKindSystemToThemeOptionSystem() {
        assertEquals(ThemeOption.System, ThemeOption.from(ThemeKind.System))
    }

    @Test
    fun shouldContainAllThemeOptions() {
        val all = ThemeOption.all
        assertEquals(3, all.size)
        assertTrue(all.contains(ThemeOption.Light))
        assertTrue(all.contains(ThemeOption.Dark))
        assertTrue(all.contains(ThemeOption.System))
    }

    @Test
    fun shouldRoundTripThemeKind() {
        ThemeOption.all.forEach { option ->
            assertEquals(option, ThemeOption.from(option.kind))
        }
    }

    // --- LanguageOption ---

    @Test
    fun shouldMapLocaleKindUzToLanguageOptionUzbek() {
        assertEquals(LanguageOption.Uzbek, LanguageOption.from(LocaleKind.Uz))
    }

    @Test
    fun shouldMapLocaleKindRuToLanguageOptionRussian() {
        assertEquals(LanguageOption.Russian, LanguageOption.from(LocaleKind.Ru))
    }

    @Test
    fun shouldMapLocaleKindUzCyrillicToUzbekCyrillic() {
        assertEquals(LanguageOption.UzbekCyrillic, LanguageOption.from(LocaleKind.UzCyrillic))
    }

    @Test
    fun shouldMapLocaleKindEnToEnglish() {
        assertEquals(LanguageOption.English, LanguageOption.from(LocaleKind.En))
    }

    @Test
    fun shouldContainAllLanguageOptions() {
        val all = LanguageOption.all
        assertEquals(2, all.size)
        assertTrue(all.contains(LanguageOption.Uzbek))
        assertTrue(all.contains(LanguageOption.Russian))
    }

    @Test
    fun shouldRoundTripLocaleKind() {
        LanguageOption.all.forEach { option ->
            assertEquals(option, LanguageOption.from(option.kind))
        }
    }

    // --- MapOption ---

    @Test
    fun shouldMapMapKindGoogleToMapOptionGoogle() {
        assertEquals(MapOption.Google, MapOption.from(MapKind.Google))
    }

    @Test
    fun shouldMapMapKindLibreToMapOptionLibre() {
        assertEquals(MapOption.Libre, MapOption.from(MapKind.Libre))
    }

    @Test
    fun shouldContainAllMapOptions() {
        val all = MapOption.all
        assertEquals(2, all.size)
        assertTrue(all.contains(MapOption.Google))
        assertTrue(all.contains(MapOption.Libre))
    }

    @Test
    fun shouldRoundTripMapKind() {
        MapOption.all.forEach { option ->
            assertEquals(option, MapOption.from(option.kind))
        }
    }
}
```

- [ ] **Step 2: Run tests**

Run: `./gradlew :foundation:allTests --tests "uz.yalla.foundation.settings.SettingsOptionsTest" 2>&1 | tail -20`
Expected: All 15 tests PASS

- [ ] **Step 3: Commit**

```bash
git add foundation/src/commonTest/kotlin/uz/yalla/foundation/settings/SettingsOptionsTest.kt
git commit -m "test(foundation): add SettingsOptions factory and collection tests"
```

---

### Task 6: LocationMappersTest.kt

**Files:**
- Create: `foundation/src/commonTest/kotlin/uz/yalla/foundation/location/LocationMappersTest.kt`

**Note:** Constructing `Order` is complex — it has many nested required fields. Use minimal valid instances with placeholder values for unused fields.

- [ ] **Step 1: Write LocationMappersTest with 7 tests**

```kotlin
package uz.yalla.foundation.location

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.Address
import uz.yalla.core.location.AddressOption
import uz.yalla.core.location.PlaceKind
import uz.yalla.core.location.SavedAddress
import uz.yalla.core.order.ExtraService
import uz.yalla.core.order.Order
import uz.yalla.core.order.OrderStatus
import uz.yalla.core.payment.PaymentKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocationMappersTest {

    @Test
    fun shouldMapAddressOptionToFoundLocation() {
        val option = AddressOption(
            id = 42, title = "Office", address = "123 Main St",
            distance = 1.5, lat = 41.3, lng = 69.2, isFromDatabase = false
        )

        val result = option.toFoundLocation()

        assertEquals(42, result.id)
        assertEquals("Office", result.name)
        assertEquals("123 Main St", result.address)
        assertEquals(GeoPoint(41.3, 69.2), result.point)
        assertNull(result.placeKind)
    }

    @Test
    fun shouldMapSavedAddressToFoundLocation() {
        val saved = SavedAddress(
            distance = 2.0, duration = 10.0, lat = 41.3, lng = 69.2,
            address = "456 Elm St", title = "Home",
            kind = PlaceKind.Home, parent = SavedAddress.Parent(name = null)
        )

        val result = saved.toFoundLocation()

        assertNull(result.id)
        assertEquals("Home", result.name)
        assertEquals("456 Elm St", result.address)
        assertEquals(GeoPoint(41.3, 69.2), result.point)
        assertEquals(PlaceKind.Home, result.placeKind)
    }

    @Test
    fun shouldMapAddressToLocation() {
        val address = Address(id = 7, name = "Tashkent", lat = 41.3, lng = 69.2, isFromDatabase = true)

        val result = address.toLocation()

        assertEquals(7, result.id)
        assertEquals("Tashkent", result.name)
        assertEquals(GeoPoint(41.3, 69.2), result.point)
    }

    @Test
    fun shouldMapAddressToLocationWithCustomPoint() {
        val address = Address(id = 7, name = "Tashkent", lat = 41.3, lng = 69.2, isFromDatabase = true)
        val custom = GeoPoint(lat = 40.0, lng = 70.0)

        val result = address.toLocation(point = custom)

        assertEquals(7, result.id)
        assertEquals("Tashkent", result.name)
        assertEquals(custom, result.point)
    }

    @Test
    fun shouldMapRouteToLocation() {
        val route = Order.Taxi.Route(
            coords = Order.Taxi.Route.Coords(lat = 41.3, lng = 69.2),
            fullAddress = "789 Oak Ave",
            index = 1
        )

        val result = route.toLocation()

        assertEquals(1, result.id)
        assertEquals("789 Oak Ave", result.name)
        assertEquals(GeoPoint(41.3, 69.2), result.point)
    }

    @Test
    fun shouldSortRouteLocationsByIndex() {
        val order = testOrder(
            routes = listOf(
                Order.Taxi.Route(Order.Taxi.Route.Coords(3.0, 3.0), "C", index = 3),
                Order.Taxi.Route(Order.Taxi.Route.Coords(1.0, 1.0), "A", index = 1),
                Order.Taxi.Route(Order.Taxi.Route.Coords(2.0, 2.0), "B", index = 2),
            )
        )

        val result = order.sortedRouteLocations()

        assertEquals(3, result.size)
        assertEquals("A", result[0].name)
        assertEquals("B", result[1].name)
        assertEquals("C", result[2].name)
    }

    @Test
    fun shouldMapFoundLocationToLocation() {
        val found = FoundLocation(
            id = 10, name = "Found Place",
            address = "should be dropped",
            point = GeoPoint(41.3, 69.2),
            placeKind = PlaceKind.Work
        )

        val result = found.toLocation()

        assertEquals(10, result.id)
        assertEquals("Found Place", result.name)
        assertEquals(GeoPoint(41.3, 69.2), result.point)
    }

    // --- Test helpers ---

    private fun testOrder(routes: List<Order.Taxi.Route>) = Order(
        comment = "",
        dateTime = 0L,
        executor = Order.Executor(
            coords = Order.Executor.Coords(0.0, 0.0, 0.0),
            vehicle = Order.Executor.Vehicle(
                callsign = "", color = Order.Executor.Vehicle.Color("", ""),
                id = 0, mark = "", model = "", stateNumber = ""
            ),
            fatherName = "", givenNames = "", id = 0, phone = "", photo = "", rating = 0.0, surName = ""
        ),
        id = 1,
        paymentType = PaymentKind.Cash,
        service = "",
        status = OrderStatus.New,
        statusTime = emptyList(),
        taxi = Order.Taxi(
            bonusAmount = 0, clientTotalPrice = 0.0, distance = 0.0, fixedPrice = false,
            routes = routes, services = emptyList(), startPrice = 0,
            tariff = "", tariffId = 0, totalPrice = 0, waitingTime = 0
        )
    )
}
```

- [ ] **Step 2: Run tests**

Run: `./gradlew :foundation:allTests --tests "uz.yalla.foundation.location.LocationMappersTest" 2>&1 | tail -20`
Expected: All 7 tests PASS

- [ ] **Step 3: Commit**

```bash
git add foundation/src/commonTest/kotlin/uz/yalla/foundation/location/LocationMappersTest.kt
git commit -m "test(foundation): add LocationMappers tests"
```

---

### Task 7: Run All Tests & Final Verification

- [ ] **Step 1: Run all data module tests**

Run: `./gradlew :data:allTests 2>&1 | tail -10`
Expected: BUILD SUCCESSFUL, 40 tests pass

- [ ] **Step 2: Run all foundation module tests**

Run: `./gradlew :foundation:allTests 2>&1 | tail -10`
Expected: BUILD SUCCESSFUL, 46 tests pass

- [ ] **Step 3: Run full SDK build**

Run: `./gradlew build 2>&1 | tail -10`
Expected: BUILD SUCCESSFUL (spotless may warn — pre-existing, not our issue)
