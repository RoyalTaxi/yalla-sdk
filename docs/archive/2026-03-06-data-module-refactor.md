# Data Module Refactor Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Transform the data module into a clean infrastructure skeleton — networking, local storage, and API utilities.

**Architecture:** Remove feature-specific code (DTOs, mappers). Split God Object preferences into 5 focused implementations. Eliminate Android/iOS network client duplication by moving config to common. Full KDoc + MODULE.md + tests.

**Tech Stack:** Kotlin Multiplatform, Ktor, DataStore, kotlinx.serialization, kotlinx.coroutines

---

### Task 1: Clean up build.gradle.kts — remove unused dependencies

**Files:**
- Modify: `data/build.gradle.kts`

**Step 1: Remove multiplatform-settings dependencies**

`createStaticSettings()` is being removed. The `multiplatform-settings` library is no longer needed.

```kotlin
plugins {
    id("yalla.sdk.kmp")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core)

            api(libs.kotlinx.serialization.json)
            api(libs.ktor.client.core)
            api(libs.ktor.client.content.negotiation)
            api(libs.ktor.serialization.kotlinx.json)
            api(libs.ktor.client.logging)
            api(libs.kotlinx.coroutines.core)
            api(libs.koin.core)

            api(libs.datastore.preferences)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
```

**Step 2: Verify build compiles**

Run: `./gradlew :data:compileCommonMainKotlinMetadata`
Expected: BUILD SUCCESSFUL (will fail until we remove Settings usages in Task 2)

**Step 3: Commit**

```
chore(data): remove unused multiplatform-settings dependencies
```

---

### Task 2: Delete feature-specific code

**Files to delete:**
- `data/src/commonMain/kotlin/uz/yalla/data/mapper/Mapper.kt`
- `data/src/commonMain/kotlin/uz/yalla/data/mapper/ClientMapper.kt`
- `data/src/commonMain/kotlin/uz/yalla/data/mapper/ExecutorMapper.kt`
- `data/src/commonMain/kotlin/uz/yalla/data/mapper/ServiceBrandMapper.kt`
- `data/src/commonMain/kotlin/uz/yalla/data/remote/model/ClientRemoteModel.kt`
- `data/src/commonMain/kotlin/uz/yalla/data/remote/model/ExecutorRemoteModel.kt`
- `data/src/commonMain/kotlin/uz/yalla/data/remote/model/OrderDetailsRemoteModel.kt`
- `data/src/commonMain/kotlin/uz/yalla/data/remote/model/BrandServiceRemoteModel.kt`
- `data/src/commonMain/kotlin/uz/yalla/data/remote/model/ServiceRemoteModel.kt`
- `data/src/commonMain/kotlin/uz/yalla/data/network/BackendQualifier.kt`

**Step 1: Check if any of these are imported within the data module itself**

Run: `grep -r "import uz.yalla.data.mapper\|import uz.yalla.data.remote.model" data/src/commonMain/`

Only `SafeApiCall.kt` imports `ApiErrorBody` (which stays). The mapper/model files are only used by YallaClient feature modules.

**Step 2: Delete all listed files**

**Step 3: Delete empty directories**

Remove `data/src/commonMain/kotlin/uz/yalla/data/mapper/` and `data/src/commonMain/kotlin/uz/yalla/data/remote/model/` directories.

**Step 4: Verify build**

Run: `./gradlew :data:compileCommonMainKotlinMetadata`
Expected: BUILD SUCCESSFUL

**Step 5: Commit**

```
refactor(data): remove feature-specific DTOs and mappers

Feature-specific remote models and mappers belong in their
owning feature modules, not in the infra skeleton.

Removed: ClientRemoteModel, ExecutorRemoteModel, OrderDetailsRemoteModel,
BrandServiceRemoteModel, ServiceRemoteModel, ClientMapper, ExecutorMapper,
ServiceBrandMapper, Mapper typealias, BackendQualifier
```

---

### Task 3: Rename remote package to api, rename response wrappers

**Files:**
- Move: `data/src/commonMain/kotlin/uz/yalla/data/remote/` → `data/src/commonMain/kotlin/uz/yalla/data/api/`
- Rename classes inside

**Step 1: Create api package and new files**

Create `data/src/commonMain/kotlin/uz/yalla/data/api/ApiResponse.kt`:
```kotlin
package uz.yalla.data.api

import kotlinx.serialization.Serializable

/**
 * Generic envelope for single-result API responses.
 *
 * Maps the JSON structure `{ "result": { ... } }` returned by the backend.
 *
 * @param T the type of the wrapped result
 * @property result the response payload, or `null` if absent
 * @since 0.0.1
 */
@Serializable
data class ApiResponse<T>(
    val result: T? = null,
)
```

Create `data/src/commonMain/kotlin/uz/yalla/data/api/ApiListResponse.kt`:
```kotlin
package uz.yalla.data.api

import kotlinx.serialization.Serializable

/**
 * Generic envelope for list API responses.
 *
 * Maps the JSON structure `{ "list": [ ... ] }` returned by the backend.
 *
 * @param T the element type within the list
 * @property list the response items, or `null` if absent
 * @since 0.0.1
 */
@Serializable
data class ApiListResponse<T>(
    val list: List<T>? = null,
)
```

Create `data/src/commonMain/kotlin/uz/yalla/data/api/ApiErrorResponse.kt`:
```kotlin
package uz.yalla.data.api

import kotlinx.serialization.Serializable

/**
 * Error envelope returned by the backend on failed requests.
 *
 * Maps the JSON structure `{ "message": "..." }`.
 *
 * @property message human-readable error description, or `null`
 * @since 0.0.1
 */
@Serializable
data class ApiErrorResponse(
    val message: String? = null,
)
```

**Step 2: Update SafeApiCall.kt import**

Change `import uz.yalla.data.remote.ApiErrorBody` → `import uz.yalla.data.api.ApiErrorResponse`
Change `response.body<ApiErrorBody>()` → `response.body<ApiErrorResponse>()`
Change `e.response.body<ApiErrorBody>()` → `e.response.body<ApiErrorResponse>()`

**Step 3: Delete old remote package**

Delete `data/src/commonMain/kotlin/uz/yalla/data/remote/` entirely.

**Step 4: Verify build**

Run: `./gradlew :data:compileCommonMainKotlinMetadata`
Expected: BUILD SUCCESSFUL

**Step 5: Commit**

```
refactor(data): rename remote package to api with clearer names

- remote/ → api/
- ApiResponseWrapper → ApiResponse
- ApiPaginationWrapper → ApiListResponse
- ApiErrorBody → ApiErrorResponse
```

---

### Task 4: Add Platform expect/actual for platformName

**Files:**
- Create: `data/src/commonMain/kotlin/uz/yalla/data/util/Platform.kt`
- Create: `data/src/androidMain/kotlin/uz/yalla/data/util/Platform.android.kt`
- Create: `data/src/iosMain/kotlin/uz/yalla/data/util/Platform.ios.kt`

**Step 1: Create common expect**

```kotlin
package uz.yalla.data.util

/**
 * Platform identifier used in HTTP headers.
 *
 * Returns `"android"` or `"ios"` depending on the compilation target.
 *
 * @since 0.0.5
 */
expect val platformName: String
```

**Step 2: Create Android actual**

```kotlin
package uz.yalla.data.util

actual val platformName: String = "android"
```

**Step 3: Create iOS actual**

```kotlin
package uz.yalla.data.util

actual val platformName: String = "ios"
```

**Step 4: Verify build**

Run: `./gradlew :data:compileCommonMainKotlinMetadata`
Expected: BUILD SUCCESSFUL

**Step 5: Commit**

```
feat(data): add platformName expect/actual for HTTP headers
```

---

### Task 5: Create HttpEngine expect/actual and HttpClientFactory in common

This is the biggest task — replaces the duplicated NetworkClient.android.kt and NetworkClient.ios.kt with a common factory.

**Files:**
- Create: `data/src/commonMain/kotlin/uz/yalla/data/network/HttpEngine.kt`
- Create: `data/src/androidMain/kotlin/uz/yalla/data/network/HttpEngine.android.kt`
- Create: `data/src/iosMain/kotlin/uz/yalla/data/network/HttpEngine.ios.kt`
- Create: `data/src/commonMain/kotlin/uz/yalla/data/network/HttpClientFactory.kt`
- Delete: `data/src/commonMain/kotlin/uz/yalla/data/network/NetworkClient.kt`
- Delete: `data/src/androidMain/kotlin/uz/yalla/data/network/NetworkClient.android.kt`
- Delete: `data/src/iosMain/kotlin/uz/yalla/data/network/NetworkClient.ios.kt`

**Step 1: Create HttpEngine expect**

`data/src/commonMain/kotlin/uz/yalla/data/network/HttpEngine.kt`:
```kotlin
package uz.yalla.data.network

import io.ktor.client.engine.HttpClientEngine

/**
 * Platform-specific HTTP client engine.
 *
 * Android uses the Android engine, iOS uses the Darwin engine.
 *
 * @since 0.0.5
 */
expect fun createHttpEngine(): HttpClientEngine
```

**Step 2: Create Android actual**

`data/src/androidMain/kotlin/uz/yalla/data/network/HttpEngine.android.kt`:
```kotlin
package uz.yalla.data.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

actual fun createHttpEngine(): HttpClientEngine = Android.create()
```

**Step 3: Create iOS actual**

`data/src/iosMain/kotlin/uz/yalla/data/network/HttpEngine.ios.kt`:
```kotlin
package uz.yalla.data.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpEngine(): HttpClientEngine = Darwin.create()
```

**Step 4: Create HttpClientFactory in common**

`data/src/commonMain/kotlin/uz/yalla/data/network/HttpClientFactory.kt`:
```kotlin
package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import uz.yalla.core.contract.preferences.InterfacePreferences
import uz.yalla.core.contract.preferences.PositionPreferences
import uz.yalla.core.contract.preferences.SessionPreferences
import uz.yalla.core.session.UnauthorizedSessionEvents
import uz.yalla.data.util.ioDispatcher
import uz.yalla.data.util.platformName

private const val BEARER_PREFIX = "Bearer "
private const val REQUEST_TIMEOUT_MS = 15_000L
private const val CONNECT_TIMEOUT_MS = 10_000L
private const val SOCKET_TIMEOUT_MS = 15_000L

/**
 * Creates a fully configured [HttpClient] for API communication.
 *
 * Sets up content negotiation, timeouts, authentication handling,
 * guest mode guard, and dynamic headers. Platform engine is resolved
 * via [createHttpEngine].
 *
 * @param config network configuration (base URL, brand, secret)
 * @param sessionPrefs session state (token, guest mode)
 * @param interfacePrefs interface state (locale)
 * @param positionPrefs position state (last known location)
 * @param inspektifySetup optional debug inspector plugin setup
 * @return configured [HttpClient] instance
 * @since 0.0.5
 */
fun createHttpClient(
    config: NetworkConfig,
    sessionPrefs: SessionPreferences,
    interfacePrefs: InterfacePreferences,
    positionPrefs: PositionPreferences,
    inspektifySetup: (HttpClientConfig<*>.() -> Unit)? = null,
): HttpClient {
    val scope = CoroutineScope(ioDispatcher + SupervisorJob())
    val localeCache = MutableStateFlow("")
    val accessTokenCache = MutableStateFlow("")
    val guestModeCache = MutableStateFlow(false)

    scope.launch {
        interfacePrefs.localeType.collectLatest { localeCache.value = it.code }
    }
    scope.launch {
        sessionPrefs.accessToken.collectLatest { accessTokenCache.value = it }
    }
    scope.launch {
        sessionPrefs.isGuestMode.collectLatest { guestModeCache.value = it }
    }

    return HttpClient(createHttpEngine()) {
        inspektifySetup?.invoke(this)

        install(Logging) {
            level = LogLevel.ALL
        }

        install(HttpCallValidator) {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    val requestToken = response.call.request
                        .headers[HttpHeaders.Authorization]
                        .extractBearerToken()
                    handleUnauthorized(sessionPrefs, accessTokenCache, requestToken)
                }
            }
            handleResponseExceptionWithRequest { cause, request ->
                if (
                    cause is ClientRequestException &&
                    cause.response.status == HttpStatusCode.Unauthorized
                ) {
                    val requestToken = request.headers[HttpHeaders.Authorization]
                        .extractBearerToken()
                    handleUnauthorized(sessionPrefs, accessTokenCache, requestToken)
                }
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
            socketTimeoutMillis = SOCKET_TIMEOUT_MS
        }

        install(createGuestModeGuardPlugin(guestModeCache))

        defaultRequest {
            url(config.baseUrl)
            header("lang", localeCache.value)
            header("brand-id", config.brandId)
            header("User-Agent-OS", platformName)
            header("Content-Type", "application/json")
            header("Device-Mode", config.deviceMode)
            header("Device", config.deviceType)
            header("secret-key", config.secretKey)
            header("Authorization", BEARER_PREFIX + accessTokenCache.value)
        }

        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    encodeDefaults = true
                }
            )
        }

        install(
            createClientPlugin("DynamicHeaders") {
                onRequest { request, _ ->
                    val location = positionPrefs.lastMapPosition.first()
                    request.headers.set("x-position", "${location.lat} ${location.lng}")
                }
            }
        )
    }
}

private fun handleUnauthorized(
    sessionPrefs: SessionPreferences,
    accessTokenCache: MutableStateFlow<String>,
    requestToken: String?,
) {
    val currentToken = accessTokenCache.value
    if (currentToken.isEmpty()) return
    if (requestToken.isNullOrEmpty()) return
    if (requestToken != currentToken) return
    if (!accessTokenCache.compareAndSet(currentToken, "")) return

    sessionPrefs.clearSession()
    UnauthorizedSessionEvents.publish()
}

private fun String?.extractBearerToken(): String? {
    val value = this?.trim().orEmpty()
    if (!value.startsWith(BEARER_PREFIX, ignoreCase = true)) return null
    if (value.length <= BEARER_PREFIX.length) return null
    return value.substring(BEARER_PREFIX.length).trim().ifEmpty { null }
}
```

> **Note:** This requires renaming `performLogout()` to `clearSession()` in the `SessionPreferences` interface in core. Do this first if not already done, or keep `performLogout()` and rename later.

**Step 5: Delete old NetworkClient files**

- Delete `data/src/commonMain/kotlin/uz/yalla/data/network/NetworkClient.kt`
- Delete `data/src/androidMain/kotlin/uz/yalla/data/network/NetworkClient.android.kt`
- Delete `data/src/iosMain/kotlin/uz/yalla/data/network/NetworkClient.ios.kt`

**Step 6: Verify build**

Run: `./gradlew :data:compileCommonMainKotlinMetadata`

**Step 7: Commit**

```
refactor(data): replace duplicated NetworkClient with common HttpClientFactory

- 140+ line Android/iOS duplication → 1-line expect/actual for engine
- Global mutable caches → scoped to factory function
- Magic numbers → named constants
- platformName expect/actual replaces hardcoded OS strings
```

---

### Task 6: Refactor DataStoreFactory — remove Settings, fix service locator

**Files:**
- Modify: `data/src/commonMain/kotlin/uz/yalla/data/local/SettingsFactory.kt` → rename to `DataStoreFactory.kt`
- Modify: `data/src/androidMain/kotlin/uz/yalla/data/local/SettingsFactory.android.kt` → rename to `DataStoreFactory.android.kt`
- Modify: `data/src/iosMain/kotlin/uz/yalla/data/local/SettingsFactory.ios.kt` → rename to `DataStoreFactory.ios.kt`

**Step 1: Create common expect**

`data/src/commonMain/kotlin/uz/yalla/data/local/DataStoreFactory.kt`:
```kotlin
package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Creates the platform-specific [DataStore] instance for preferences storage.
 *
 * Android stores data in the app's internal files directory.
 * iOS stores data in the documents directory.
 *
 * @since 0.0.1
 */
expect fun createDataStore(): DataStore<Preferences>
```

**Step 2: Create Android actual**

`data/src/androidMain/kotlin/uz/yalla/data/local/DataStoreFactory.android.kt`:
```kotlin
package uz.yalla.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

private const val DATASTORE_FILE = "prefs.preferences_pb"

actual fun createDataStore(): DataStore<Preferences> =
    object : KoinComponent {
        val context: Context by inject()
    }.run {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                File(context.filesDir, DATASTORE_FILE).absolutePath.toPath()
            }
        )
    }
```

**Step 3: Create iOS actual**

`data/src/iosMain/kotlin/uz/yalla/data/local/DataStoreFactory.ios.kt`:
```kotlin
package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

private const val DATASTORE_FILE = "prefs.preferences_pb"

@OptIn(ExperimentalForeignApi::class)
actual fun createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val documentDirectory =
                NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
            (requireNotNull(documentDirectory).path + "/$DATASTORE_FILE").toPath()
        }
    )
```

**Step 4: Delete old SettingsFactory files**

- Delete `data/src/commonMain/kotlin/uz/yalla/data/local/SettingsFactory.kt`
- Delete `data/src/androidMain/kotlin/uz/yalla/data/local/SettingsFactory.android.kt`
- Delete `data/src/iosMain/kotlin/uz/yalla/data/local/SettingsFactory.ios.kt`

**Step 5: Verify build**

Run: `./gradlew :data:compileCommonMainKotlinMetadata`

**Step 6: Commit**

```
refactor(data): rename SettingsFactory to DataStoreFactory, remove Settings

- Removed createStaticSettings() — unused
- Removed multiplatform-settings dependency
- Renamed to reflect actual purpose
```

---

### Task 7: Create PreferenceKeys and split AppPreferencesImpl into 5 classes

**Files:**
- Create: `data/src/commonMain/kotlin/uz/yalla/data/local/PreferenceKeys.kt`
- Create: `data/src/commonMain/kotlin/uz/yalla/data/local/SessionPreferencesImpl.kt`
- Create: `data/src/commonMain/kotlin/uz/yalla/data/local/UserPreferencesImpl.kt`
- Create: `data/src/commonMain/kotlin/uz/yalla/data/local/ConfigPreferencesImpl.kt`
- Create: `data/src/commonMain/kotlin/uz/yalla/data/local/InterfacePreferencesImpl.kt`
- Create: `data/src/commonMain/kotlin/uz/yalla/data/local/PositionPreferencesImpl.kt`
- Delete: `data/src/commonMain/kotlin/uz/yalla/data/local/AppPreferencesImpl.kt`

**Step 1: Create PreferenceKeys**

`data/src/commonMain/kotlin/uz/yalla/data/local/PreferenceKeys.kt`:
```kotlin
package uz.yalla.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Central registry of all [DataStore] preference keys.
 *
 * Keeping keys in one place prevents accidental key collisions
 * across the separate [PreferencesImpl] classes that share a
 * single [DataStore] instance.
 */
internal object PreferenceKeys {
    // Session
    val ACCESS_TOKEN = stringPreferencesKey("accessToken")
    val FIREBASE_TOKEN = stringPreferencesKey("firebaseToken")
    val IS_GUEST_MODE = booleanPreferencesKey("isGuestModeEnable")
    val IS_DEVICE_REGISTERED = booleanPreferencesKey("isDeviceRegistered")

    // User
    val FIRST_NAME = stringPreferencesKey("firstName")
    val LAST_NAME = stringPreferencesKey("lastName")
    val NUMBER = stringPreferencesKey("number")
    val PAYMENT_TYPE = stringPreferencesKey("paymentType")
    val CARD_ID = stringPreferencesKey("cardId")
    val CARD_NUMBER = stringPreferencesKey("cardNumber")

    // Config
    val SUPPORT_NUMBER = stringPreferencesKey("supportNumber")
    val SUPPORT_TELEGRAM = stringPreferencesKey("supportTelegram")
    val INFO_INSTAGRAM = stringPreferencesKey("infoInstagram")
    val INFO_TELEGRAM = stringPreferencesKey("infoTelegram")
    val PRIVACY_POLICY_RU = stringPreferencesKey("privacyPolicyRu")
    val PRIVACY_POLICY_UZ = stringPreferencesKey("privacyPolicyUz")
    val MAX_BONUS = longPreferencesKey("maxBonus")
    val MIN_BONUS = longPreferencesKey("minBonus")
    val BALANCE = longPreferencesKey("balance")
    val IS_BONUS_ENABLED = booleanPreferencesKey("isBonusEnabled")
    val IS_CARD_ENABLED = booleanPreferencesKey("isCardEnabled")
    val ORDER_CANCEL_TIME = intPreferencesKey("orderCancelTime")

    // Interface
    val LOCALE_TYPE = stringPreferencesKey("localeType")
    val THEME_TYPE = stringPreferencesKey("themeType")
    val MAP_TYPE = stringPreferencesKey("mapType")
    val SKIP_ONBOARDING = booleanPreferencesKey("skipOnboarding")

    // Position
    val LAST_MAP_POSITION = stringPreferencesKey("lastMapPosition")
    val LAST_GPS_POSITION = stringPreferencesKey("lastGpsPosition")
}
```

**Step 2: Create SessionPreferencesImpl**

`data/src/commonMain/kotlin/uz/yalla/data/local/SessionPreferencesImpl.kt`:
```kotlin
package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.preferences.SessionPreferences
import uz.yalla.core.util.orFalse

/**
 * [DataStore]-backed implementation of [SessionPreferences].
 *
 * Manages authentication tokens, guest mode, and device registration state.
 * [clearSession] removes session and user data while preserving
 * interface settings (locale, theme, map provider, onboarding).
 *
 * @param dataStore shared preferences store
 * @param scope coroutine scope for write operations
 * @since 0.0.1
 */
internal class SessionPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
) : SessionPreferences {

    override val accessToken: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.ACCESS_TOKEN].orEmpty() }

    override fun setAccessToken(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.ACCESS_TOKEN] = value } }
    }

    override val firebaseToken: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.FIREBASE_TOKEN].orEmpty() }

    override fun setFirebaseToken(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.FIREBASE_TOKEN] = value } }
    }

    override val isGuestMode: Flow<Boolean> =
        dataStore.data.map { it[PreferenceKeys.IS_GUEST_MODE].orFalse() }

    override fun setGuestMode(value: Boolean) {
        scope.launch { dataStore.edit { it[PreferenceKeys.IS_GUEST_MODE] = value } }
    }

    override val isDeviceRegistered: Flow<Boolean> =
        dataStore.data.map { it[PreferenceKeys.IS_DEVICE_REGISTERED].orFalse() }

    override fun setDeviceRegistered(value: Boolean) {
        scope.launch { dataStore.edit { it[PreferenceKeys.IS_DEVICE_REGISTERED] = value } }
    }

    override fun clearSession() {
        scope.launch {
            dataStore.edit { prefs ->
                prefs.remove(PreferenceKeys.ACCESS_TOKEN)
                prefs.remove(PreferenceKeys.FIREBASE_TOKEN)
                prefs.remove(PreferenceKeys.IS_GUEST_MODE)
                prefs.remove(PreferenceKeys.IS_DEVICE_REGISTERED)
                prefs.remove(PreferenceKeys.FIRST_NAME)
                prefs.remove(PreferenceKeys.LAST_NAME)
                prefs.remove(PreferenceKeys.NUMBER)
                prefs.remove(PreferenceKeys.PAYMENT_TYPE)
                prefs.remove(PreferenceKeys.CARD_ID)
                prefs.remove(PreferenceKeys.CARD_NUMBER)
                prefs.remove(PreferenceKeys.SUPPORT_NUMBER)
                prefs.remove(PreferenceKeys.SUPPORT_TELEGRAM)
                prefs.remove(PreferenceKeys.INFO_INSTAGRAM)
                prefs.remove(PreferenceKeys.INFO_TELEGRAM)
                prefs.remove(PreferenceKeys.PRIVACY_POLICY_RU)
                prefs.remove(PreferenceKeys.PRIVACY_POLICY_UZ)
                prefs.remove(PreferenceKeys.MAX_BONUS)
                prefs.remove(PreferenceKeys.MIN_BONUS)
                prefs.remove(PreferenceKeys.BALANCE)
                prefs.remove(PreferenceKeys.IS_BONUS_ENABLED)
                prefs.remove(PreferenceKeys.IS_CARD_ENABLED)
                prefs.remove(PreferenceKeys.ORDER_CANCEL_TIME)
            }
        }
    }
}
```

> **Note:** `clearSession()` replaces `performLogout()`. This requires updating the `SessionPreferences` interface in core. If we can't change core yet, keep the name `performLogout()` temporarily.

**Step 3: Create UserPreferencesImpl**

`data/src/commonMain/kotlin/uz/yalla/data/local/UserPreferencesImpl.kt`:
```kotlin
package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.preferences.UserPreferences
import uz.yalla.core.payment.PaymentKind

/**
 * [DataStore]-backed implementation of [UserPreferences].
 *
 * Manages user profile data: name, phone number, and payment method.
 *
 * @param dataStore shared preferences store
 * @param scope coroutine scope for write operations
 * @since 0.0.1
 */
internal class UserPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
) : UserPreferences {

    override val firstName: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.FIRST_NAME].orEmpty() }

    override fun setFirstName(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.FIRST_NAME] = value } }
    }

    override val lastName: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.LAST_NAME].orEmpty() }

    override fun setLastName(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.LAST_NAME] = value } }
    }

    override val number: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.NUMBER].orEmpty() }

    override fun setNumber(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.NUMBER] = value } }
    }

    override val paymentType: Flow<PaymentKind> =
        dataStore.data.map { prefs ->
            val id = prefs[PreferenceKeys.PAYMENT_TYPE] ?: PaymentKind.Cash.id
            val cardId = prefs[PreferenceKeys.CARD_ID].orEmpty()
            val cardNumber = prefs[PreferenceKeys.CARD_NUMBER].orEmpty()
            if (id == "card" && cardId.isNotEmpty()) {
                PaymentKind.Card(cardId, cardNumber)
            } else {
                PaymentKind.Cash
            }
        }

    override fun setPaymentType(value: PaymentKind) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[PreferenceKeys.PAYMENT_TYPE] = value.id
                if (value is PaymentKind.Card) {
                    prefs[PreferenceKeys.CARD_ID] = value.cardId
                    prefs[PreferenceKeys.CARD_NUMBER] = value.maskedNumber
                } else {
                    prefs.remove(PreferenceKeys.CARD_ID)
                    prefs.remove(PreferenceKeys.CARD_NUMBER)
                }
            }
        }
    }
}
```

**Step 4: Create ConfigPreferencesImpl**

`data/src/commonMain/kotlin/uz/yalla/data/local/ConfigPreferencesImpl.kt`:
```kotlin
package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.preferences.ConfigPreferences
import uz.yalla.core.util.or0
import uz.yalla.core.util.orFalse

/**
 * [DataStore]-backed implementation of [ConfigPreferences].
 *
 * Manages server-provided configuration: support contacts, social links,
 * privacy policy URLs, bonus limits, and balance.
 *
 * @param dataStore shared preferences store
 * @param scope coroutine scope for write operations
 * @since 0.0.1
 */
internal class ConfigPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
) : ConfigPreferences {

    override val supportNumber: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.SUPPORT_NUMBER].orEmpty() }

    override fun setSupportNumber(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.SUPPORT_NUMBER] = value } }
    }

    override val supportTelegram: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.SUPPORT_TELEGRAM].orEmpty() }

    override fun setSupportTelegram(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.SUPPORT_TELEGRAM] = value } }
    }

    override val infoInstagram: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.INFO_INSTAGRAM].orEmpty() }

    override fun setInfoInstagram(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.INFO_INSTAGRAM] = value } }
    }

    override val infoTelegram: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.INFO_TELEGRAM].orEmpty() }

    override fun setInfoTelegram(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.INFO_TELEGRAM] = value } }
    }

    override val privacyPolicyRu: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.PRIVACY_POLICY_RU].orEmpty() }

    override fun setPrivacyPolicyRu(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.PRIVACY_POLICY_RU] = value } }
    }

    override val privacyPolicyUz: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.PRIVACY_POLICY_UZ].orEmpty() }

    override fun setPrivacyPolicyUz(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.PRIVACY_POLICY_UZ] = value } }
    }

    override val maxBonus: Flow<Long> =
        dataStore.data.map { it[PreferenceKeys.MAX_BONUS].or0() }

    override fun setMaxBonus(value: Long) {
        scope.launch { dataStore.edit { it[PreferenceKeys.MAX_BONUS] = value } }
    }

    override val minBonus: Flow<Long> =
        dataStore.data.map { it[PreferenceKeys.MIN_BONUS].or0() }

    override fun setMinBonus(value: Long) {
        scope.launch { dataStore.edit { it[PreferenceKeys.MIN_BONUS] = value } }
    }

    override val balance: Flow<Long> =
        dataStore.data.map { it[PreferenceKeys.BALANCE].or0() }

    override fun setBalance(value: Long) {
        scope.launch { dataStore.edit { it[PreferenceKeys.BALANCE] = value } }
    }

    override val isBonusEnabled: Flow<Boolean> =
        dataStore.data.map { it[PreferenceKeys.IS_BONUS_ENABLED].orFalse() }

    override fun setBonusEnabled(value: Boolean) {
        scope.launch { dataStore.edit { it[PreferenceKeys.IS_BONUS_ENABLED] = value } }
    }

    override val isCardEnabled: Flow<Boolean> =
        dataStore.data.map { it[PreferenceKeys.IS_CARD_ENABLED].orFalse() }

    override fun setCardEnabled(value: Boolean) {
        scope.launch { dataStore.edit { it[PreferenceKeys.IS_CARD_ENABLED] = value } }
    }

    override val orderCancelTime: Flow<Int> =
        dataStore.data.map { it[PreferenceKeys.ORDER_CANCEL_TIME].or0() }

    override fun setOrderCancelTime(value: Int) {
        scope.launch { dataStore.edit { it[PreferenceKeys.ORDER_CANCEL_TIME] = value } }
    }
}
```

**Step 5: Create InterfacePreferencesImpl**

`data/src/commonMain/kotlin/uz/yalla/data/local/InterfacePreferencesImpl.kt`:
```kotlin
package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.preferences.InterfacePreferences
import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind
import uz.yalla.core.util.orFalse

/**
 * [DataStore]-backed implementation of [InterfacePreferences].
 *
 * Manages user-facing settings: locale, theme, map provider, and onboarding state.
 * These values survive session clear (logout).
 *
 * @param dataStore shared preferences store
 * @param scope coroutine scope for write operations
 * @since 0.0.1
 */
internal class InterfacePreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
) : InterfacePreferences {

    override val localeType: Flow<LocaleKind> =
        dataStore.data.map { LocaleKind.from(it[PreferenceKeys.LOCALE_TYPE]) }

    override fun setLocaleType(value: LocaleKind) {
        scope.launch { dataStore.edit { it[PreferenceKeys.LOCALE_TYPE] = value.code } }
    }

    override val themeType: Flow<ThemeKind> =
        dataStore.data.map { ThemeKind.from(it[PreferenceKeys.THEME_TYPE]) }

    override fun setThemeType(value: ThemeKind) {
        scope.launch { dataStore.edit { it[PreferenceKeys.THEME_TYPE] = value.id } }
    }

    override val mapKind: Flow<MapKind> =
        dataStore.data.map { prefs ->
            MapKind.from(prefs[PreferenceKeys.MAP_TYPE] ?: MapKind.Google.id)
        }

    override fun setMapKind(value: MapKind) {
        scope.launch { dataStore.edit { it[PreferenceKeys.MAP_TYPE] = value.id } }
    }

    override val skipOnboarding: Flow<Boolean> =
        dataStore.data.map { it[PreferenceKeys.SKIP_ONBOARDING].orFalse() }

    override fun setSkipOnboarding(value: Boolean) {
        scope.launch { dataStore.edit { it[PreferenceKeys.SKIP_ONBOARDING] = value } }
    }
}
```

**Step 6: Create PositionPreferencesImpl**

`data/src/commonMain/kotlin/uz/yalla/data/local/PositionPreferencesImpl.kt`:
```kotlin
package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.preferences.PositionPreferences
import uz.yalla.core.geo.GeoPoint

/**
 * [DataStore]-backed implementation of [PositionPreferences].
 *
 * Stores geographic positions as `"lat,lng"` strings.
 * [lastGpsPosition] falls back to [lastMapPosition] when no GPS fix is available.
 * These values survive session clear (logout).
 *
 * @param dataStore shared preferences store
 * @param scope coroutine scope for write operations
 * @since 0.0.1
 */
internal class PositionPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
) : PositionPreferences {

    override val lastMapPosition: Flow<GeoPoint> =
        dataStore.data.map { parseGeoPoint(it[PreferenceKeys.LAST_MAP_POSITION]) }

    override fun setLastMapPosition(value: GeoPoint) {
        scope.launch {
            dataStore.edit { it[PreferenceKeys.LAST_MAP_POSITION] = "${value.lat},${value.lng}" }
        }
    }

    override val lastGpsPosition: Flow<GeoPoint> =
        dataStore.data.map { prefs ->
            parseGeoPoint(
                raw = prefs[PreferenceKeys.LAST_GPS_POSITION],
                fallbackRaw = prefs[PreferenceKeys.LAST_MAP_POSITION],
            )
        }

    override fun setLastGpsPosition(value: GeoPoint) {
        scope.launch {
            dataStore.edit { it[PreferenceKeys.LAST_GPS_POSITION] = "${value.lat},${value.lng}" }
        }
    }
}

private fun parseGeoPoint(
    raw: String?,
    fallbackRaw: String? = null,
): GeoPoint {
    val source = raw?.takeIf { it.isNotBlank() } ?: fallbackRaw.orEmpty()
    val parts = source.split(",", limit = 2)
    val lat = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
    val lng = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
    return GeoPoint(lat, lng)
}
```

**Step 7: Delete AppPreferencesImpl.kt**

**Step 8: Verify build**

Run: `./gradlew :data:compileCommonMainKotlinMetadata`

**Step 9: Commit**

```
refactor(data): split AppPreferencesImpl into 5 focused classes

God Object (472 lines, 5 interfaces) → 5 single-responsibility classes:
- SessionPreferencesImpl: auth tokens, guest mode, device registration
- UserPreferencesImpl: name, phone, payment method
- ConfigPreferencesImpl: support contacts, bonus limits, balance
- InterfacePreferencesImpl: locale, theme, map provider, onboarding
- PositionPreferencesImpl: GPS and map positions

All share one DataStore instance. Keys centralized in PreferenceKeys.
```

---

### Task 8: Clean up NetworkConfig and GuestModeGuard

**Files:**
- Modify: `data/src/commonMain/kotlin/uz/yalla/data/network/NetworkConfig.kt`
- Modify: `data/src/commonMain/kotlin/uz/yalla/data/network/GuestModeGuard.kt`

**Step 1: Add KDoc to NetworkConfig**

```kotlin
package uz.yalla.data.network

/**
 * Configuration for HTTP client initialization.
 *
 * @property baseUrl root URL for API requests
 * @property brandId brand identifier sent in request headers
 * @property secretKey API secret key for authentication headers
 * @property deviceType device category, defaults to `"client"`
 * @property deviceMode device form factor, defaults to `"mobile"`
 * @since 0.0.1
 */
data class NetworkConfig(
    val baseUrl: String,
    val brandId: String,
    val secretKey: String,
    val deviceType: String = "client",
    val deviceMode: String = "mobile",
)
```

Note: removed `userAgentOS` — now handled by `platformName` expect/actual.

**Step 2: Clean up GuestModeGuard**

```kotlin
package uz.yalla.data.network

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.StateFlow

/**
 * Exception thrown when a guest user attempts a restricted API call.
 *
 * @see createGuestModeGuardPlugin
 * @since 0.0.1
 */
internal class GuestBlockedException : RuntimeException()

/**
 * Ktor plugin that blocks API calls not in [allowedSegments] when guest mode is active.
 *
 * Checks the last path segment of each request URL against the whitelist.
 * Throws [GuestBlockedException] for unauthorized requests.
 *
 * @param isGuestMode reactive guest mode state
 * @param allowedSegments URL segments permitted in guest mode
 * @since 0.0.1
 */
fun createGuestModeGuardPlugin(
    isGuestMode: StateFlow<Boolean>,
    allowedSegments: Set<String> = DEFAULT_GUEST_ALLOWED_SEGMENTS,
) = createClientPlugin("GuestModeGuard") {
    onRequest { request, _ ->
        if (!isGuestMode.value) return@onRequest

        val path = request.url.encodedPath.trimEnd('/')
        val lastSegment = path.substringAfterLast('/')
        if (lastSegment !in allowedSegments) {
            throw GuestBlockedException()
        }
    }
}

private val DEFAULT_GUEST_ALLOWED_SEGMENTS = setOf(
    "client",
    "valid",
    "register",
    "location-name",
    "cost",
    "lists",
)
```

**Step 3: Verify build**

Run: `./gradlew :data:compileCommonMainKotlinMetadata`

**Step 4: Commit**

```
refactor(data): clean up NetworkConfig and GuestModeGuard

- NetworkConfig: removed userAgentOS (now platformName), added KDoc
- GuestModeGuard: configurable allowedSegments, KDoc, StateFlow instead of MutableStateFlow
```

---

### Task 9: Clean up SafeApiCall with renamed retry function

**Files:**
- Modify: `data/src/commonMain/kotlin/uz/yalla/data/network/SafeApiCall.kt`

**Step 1: Update with KDoc and rename retryIO → retryWithBackoff**

```kotlin
package uz.yalla.data.network

import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.delay
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import uz.yalla.core.error.DataError
import uz.yalla.core.result.Either
import uz.yalla.data.api.ApiErrorResponse
import kotlin.random.Random

private const val DEFAULT_RETRY_COUNT = 3
private const val INITIAL_RETRY_DELAY_MS = 200L
private const val MAX_RETRY_DELAY_MS = 2_000L
private const val RETRY_BACKOFF_FACTOR = 2.0

/**
 * Executes an API call with error handling and optional retry.
 *
 * Wraps the raw [HttpResponse] into [Either] — mapping HTTP status codes
 * and exceptions to [DataError.Network] subtypes. Idempotent calls are
 * retried on IO failures with exponential backoff.
 *
 * @param T the expected success response type
 * @param isIdempotent whether the call can be safely retried on IO failure
 * @param call the suspend function producing the HTTP response
 * @return [Either.Success] with parsed body, or [Either.Failure] with typed error
 * @since 0.0.1
 */
suspend inline fun <reified T> safeApiCall(
    isIdempotent: Boolean = false,
    crossinline call: suspend () -> HttpResponse,
): Either<T, DataError.Network> =
    try {
        val response = retryWithBackoff(isIdempotent = isIdempotent) { call() }
        when (response.status.value) {
            in 200..299 -> {
                if (T::class == Unit::class) {
                    Either.Success(Unit as T)
                } else {
                    Either.Success(response.body())
                }
            }
            in 300..399 -> Either.Failure(DataError.Network.Client)
            in 400..499 -> {
                val message = try {
                    response.body<ApiErrorResponse>().message
                } catch (_: Exception) {
                    null
                }
                if (!message.isNullOrBlank()) {
                    Either.Failure(
                        DataError.Network.ClientWithMessage(
                            code = response.status.value,
                            message = message,
                        )
                    )
                } else {
                    Either.Failure(DataError.Network.Client)
                }
            }
            in 500..599 -> Either.Failure(DataError.Network.Server)
            else -> Either.Failure(DataError.Network.Unknown)
        }
    } catch (_: ServerResponseException) {
        Either.Failure(DataError.Network.Server)
    } catch (e: ClientRequestException) {
        val message = try {
            e.response.body<ApiErrorResponse>().message
        } catch (_: Exception) {
            null
        }
        if (!message.isNullOrBlank()) {
            Either.Failure(
                DataError.Network.ClientWithMessage(
                    code = e.response.status.value,
                    message = message,
                )
            )
        } else {
            Either.Failure(DataError.Network.Client)
        }
    } catch (_: RedirectResponseException) {
        Either.Failure(DataError.Network.Client)
    } catch (_: IOException) {
        Either.Failure(DataError.Network.Connection)
    } catch (_: SocketTimeoutException) {
        Either.Failure(DataError.Network.Timeout)
    } catch (_: SerializationException) {
        Either.Failure(DataError.Network.Serialization)
    } catch (_: ResponseException) {
        Either.Failure(DataError.Network.Unknown)
    } catch (_: GuestBlockedException) {
        Either.Failure(DataError.Network.Guest)
    }

/**
 * Retries a suspending [block] with exponential backoff and jitter.
 *
 * Only retries on [IOException] and [SocketTimeoutException] when
 * [isIdempotent] is `true`. Non-retryable exceptions propagate immediately.
 *
 * @param times maximum number of attempts
 * @param initialDelay delay before the first retry in milliseconds
 * @param maxDelay upper bound for delay in milliseconds
 * @param factor multiplier applied to delay after each retry
 * @param isIdempotent whether the operation is safe to retry
 * @param block the operation to execute
 * @since 0.0.1
 */
@PublishedApi
internal suspend fun <T> retryWithBackoff(
    times: Int = DEFAULT_RETRY_COUNT,
    initialDelay: Long = INITIAL_RETRY_DELAY_MS,
    maxDelay: Long = MAX_RETRY_DELAY_MS,
    factor: Double = RETRY_BACKOFF_FACTOR,
    isIdempotent: Boolean,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: Exception) {
            val retryable = isIdempotent && (e is IOException || e is SocketTimeoutException)
            if (!retryable) throw e
        }
        val jitter = Random.nextLong(0, (currentDelay / 2) + 1)
        delay(currentDelay + jitter)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block()
}
```

**Step 2: Verify build**

Run: `./gradlew :data:compileCommonMainKotlinMetadata`

**Step 3: Commit**

```
refactor(data): clean up SafeApiCall with KDoc and named constants

- retryIO → retryWithBackoff (describes strategy, not target)
- Magic numbers → named constants
- ApiErrorBody → ApiErrorResponse (new name)
- Full KDoc for public API
```

---

### Task 10: Rename performLogout → clearSession in core contract

**Files:**
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/contract/preferences/SessionPreferences.kt`

**Step 1: Rename in interface**

Change `fun performLogout()` → `fun clearSession()`

**Step 2: Update all callers**

Search for `performLogout()` usages across the entire codebase and update.

Run: `grep -r "performLogout" --include="*.kt" .`

Update every call site.

**Step 3: Verify build**

Run: `./gradlew :data:compileCommonMainKotlinMetadata`

**Step 4: Commit**

```
refactor(core): rename performLogout to clearSession

Data layer should not know about UI concepts like "logout".
clearSession() describes the data-layer action accurately.
```

---

### Task 11: Write MODULE.md

**Files:**
- Create: `data/MODULE.md`

**Step 1: Write MODULE.md**

```markdown
# Module data

Data-layer infrastructure skeleton for the Yalla SDK.

Provides networking, local storage, and common API utilities that feature
data modules build upon. Contains no feature-specific business logic.

# Package uz.yalla.data.api

Generic API response envelopes for JSON deserialization.

# Package uz.yalla.data.local

DataStore-backed preference implementations and factory.

# Package uz.yalla.data.network

HTTP client factory, safe API call wrapper, and request plugins.

# Package uz.yalla.data.util

Platform expect/actual declarations for IO dispatcher and platform name.
```

**Step 2: Add Dokka includes to build.gradle.kts if not already configured**

Check if the convention plugin already adds `includes.from("MODULE.md")`. If not, add to `data/build.gradle.kts`.

**Step 3: Commit**

```
docs(data): add MODULE.md for Dokka documentation
```

---

### Task 12: Verify full build and fix any compilation errors

**Step 1: Full SDK build**

Run: `./gradlew build`

Fix any compilation errors from modules that depend on data (foundation, etc.).

**Step 2: Check YallaClient compilation**

Search YallaClient for usages of renamed/removed APIs:
- `ApiResponseWrapper` → `ApiResponse`
- `ApiPaginationWrapper` → `ApiListResponse`
- `ApiErrorBody` → `ApiErrorResponse`
- `provideNetworkClient` → `createHttpClient`
- `AppPreferencesImpl` → individual impl classes
- `performLogout` → `clearSession`
- `BackendQualifier` → remove or define locally
- `createStaticSettings` → remove usages
- Mapper/DTO imports → move to feature modules

**Step 3: Commit any fixes**

```
fix(data): resolve compilation errors from refactoring
```

---

### Task 13: Final commit and summary

**Step 1: Verify clean build**

Run: `./gradlew :data:build`

**Step 2: Final commit if any cleanup needed**

```
chore(data): finalize data module refactoring
```
