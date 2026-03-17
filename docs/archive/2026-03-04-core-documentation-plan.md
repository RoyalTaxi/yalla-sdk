# Core Module Documentation Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add comprehensive KDoc to all public APIs in core module, set up Dokka, create MODULE.md, docs/ structure, and root README.

**Architecture:** Three-layer docs: KDoc (in-code) → Dokka (auto-generated API ref) → Markdown (architecture & guides). Core module first, other modules after refactor+tests.

**Tech Stack:** Kotlin KDoc, Dokka 2.0.0, Markdown

---

### Task 1: Set up Dokka in build-logic

**Files:**
- Modify: `gradle/libs.versions.toml` — add dokka version and plugin
- Modify: `build-logic/convention/build.gradle.kts` — add dokka dependency
- Modify: `build-logic/convention/src/main/kotlin/KmpLibraryConventionPlugin.kt` — apply dokka plugin
- Modify: `build.gradle.kts` (root) — add dokka multi-module task

**Step 1: Add Dokka to version catalog**

In `gradle/libs.versions.toml`:
```toml
# Under [versions], add:
dokka = "2.0.0"

# Under [plugins], add:
dokka = { id = "org.jetbrains.dokka", version.ref = "dokka" }
```

**Step 2: Add Dokka to build-logic dependencies**

In `build-logic/convention/build.gradle.kts`, add to dependencies:
```kotlin
dependencies {
    compileOnly(libs.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
    // ADD:
    compileOnly("org.jetbrains.dokka:dokka-gradle-plugin:${libs.versions.dokka.get()}")
}
```

**Step 3: Apply Dokka in KmpLibraryConventionPlugin**

In `KmpLibraryConventionPlugin.kt`, add to `apply()` method's `with(pluginManager)`:
```kotlin
apply("org.jetbrains.dokka")
```

**Step 4: Add multi-module Dokka to root build.gradle.kts**

Add at the top of `build.gradle.kts`:
```kotlin
plugins {
    // existing plugins...
    alias(libs.plugins.dokka) // ADD
}
```

**Step 5: Verify Dokka setup**

Run: `cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew :core:dokkaGenerate --dry-run`
Expected: Task graph resolves without errors

**Step 6: Commit**

```bash
git add gradle/libs.versions.toml build-logic/ build.gradle.kts
git commit -m "chore(docs): set up Dokka 2.0.0 for API documentation generation"
```

---

### Task 2: Create core MODULE.md

**Files:**
- Create: `core/MODULE.md`

**Step 1: Write MODULE.md**

```markdown
# Module core

Core types, error hierarchy, and contracts for the yalla-sdk.

This module provides the foundational building blocks used across all other SDK modules:

## Packages

| Package | Description |
|---------|-------------|
| `uz.yalla.core.error` | Sealed error hierarchy (`DataError`) for typed error handling |
| `uz.yalla.core.result` | `Either<D, E>` result type for functional error handling |
| `uz.yalla.core.order` | Order domain models and status state machine |
| `uz.yalla.core.location` | Address, route, and geographic point types |
| `uz.yalla.core.geo` | `GeoPoint` with Haversine distance calculation |
| `uz.yalla.core.payment` | Payment types (`Cash`, `Card`) and card models |
| `uz.yalla.core.profile` | User profile models |
| `uz.yalla.core.settings` | App settings enums (locale, theme, map provider) |
| `uz.yalla.core.session` | Session event bus for unauthorized state handling |
| `uz.yalla.core.contract.preferences` | Reactive preference contracts (Flow-based) |
| `uz.yalla.core.contract.location` | Location tracking contract |
| `uz.yalla.core.util` | Formatting, normalization, and extension utilities |

## Dependencies

- `kotlinx-coroutines-core` — Flow-based reactive contracts
- `kotlinx-serialization-json` — Serializable types
- `kotlinx-datetime` — Date/time formatting
```

**Step 2: Update core/build.gradle.kts to include MODULE.md in Dokka**

Add to `core/build.gradle.kts`:
```kotlin
dokka {
    dokkaSourceSets.configureEach {
        includes.from("MODULE.md")
    }
}
```

**Step 3: Commit**

```bash
git add core/MODULE.md core/build.gradle.kts
git commit -m "docs(core): add MODULE.md with package descriptions for Dokka"
```

---

### Task 3: KDoc — error and result packages

**Files:**
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/error/DataError.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/result/Either.kt`

**Step 1: Add KDoc to DataError.kt**

```kotlin
package uz.yalla.core.error

/**
 * Sealed error hierarchy for all data-layer operations.
 *
 * Used with [uz.yalla.core.result.Either] to provide typed error handling
 * without exceptions in business logic.
 *
 * ## Usage
 * ```kotlin
 * when (error) {
 *     is DataError.Network.Connection -> showNoInternetDialog()
 *     is DataError.Network.ClientWithMessage -> showError(error.message)
 *     is DataError.Network.Guest -> navigateToLogin()
 *     else -> showGenericError()
 * }
 * ```
 *
 * @see uz.yalla.core.result.Either
 * @since 0.0.1
 */
sealed class DataError {
    /**
     * Network-related errors from API calls.
     *
     * Each subtype maps to a specific HTTP or connectivity failure scenario.
     */
    sealed class Network : DataError() {
        /** Device has no internet connection. */
        data object Connection : Network()

        /** Request timed out before server responded. */
        data object Timeout : Network()

        /** Server returned 5xx error. */
        data object Server : Network()

        /** Server returned 4xx error without message body. */
        data object Client : Network()

        /**
         * Server returned 4xx error with a human-readable message.
         *
         * @property code HTTP status code
         * @property message Error message from backend, suitable for UI display
         */
        data class ClientWithMessage(val code: Int, val message: String) : Network()

        /** Response body could not be deserialized. */
        data object Serialization : Network()

        /** User is in guest mode and attempted an authenticated action. */
        data object Guest : Network()

        /** Unclassified network error. */
        data object Unknown : Network()
    }
}
```

**Step 2: Add KDoc to Either.kt**

```kotlin
package uz.yalla.core.result

/**
 * A discriminated union representing either a successful result or a failure.
 *
 * Preferred over try-catch for business logic error handling. All repository
 * and use-case functions return `Either<Data, DataError>`.
 *
 * ## Usage
 * ```kotlin
 * val result: Either<Order, DataError> = orderRepository.getOrder(id)
 * result
 *     .onSuccess { order -> updateUi(order) }
 *     .onFailure { error -> handleError(error) }
 * ```
 *
 * @param D The success data type
 * @param E The error type (typically [uz.yalla.core.error.DataError])
 * @see onSuccess
 * @see onFailure
 * @since 0.0.1
 */
sealed interface Either<out D, out E> {
    /** Successful result containing [data]. */
    data class Success<D>(val data: D) : Either<D, Nothing>
    /** Failed result containing [error]. */
    data class Failure<E>(val error: E) : Either<Nothing, E>
}

/**
 * Executes [action] if this is [Either.Success], then returns the same [Either] for chaining.
 *
 * @param action Callback invoked with the success data
 * @return This same [Either] instance
 */
inline fun <D, E> Either<D, E>.onSuccess(action: (D) -> Unit): Either<D, E> {
    if (this is Either.Success) action(data)
    return this
}

/**
 * Executes [action] if this is [Either.Failure], then returns the same [Either] for chaining.
 *
 * @param action Callback invoked with the error
 * @return This same [Either] instance
 */
inline fun <D, E> Either<D, E>.onFailure(action: (E) -> Unit): Either<D, E> {
    if (this is Either.Failure) action(error)
    return this
}
```

**Step 3: Run tests to verify no breakage**

Run: `cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew :core:allTests`
Expected: All tests pass (KDoc is comments only)

**Step 4: Commit**

```bash
git add core/src/commonMain/kotlin/uz/yalla/core/error/ core/src/commonMain/kotlin/uz/yalla/core/result/
git commit -m "docs(core): add KDoc to DataError and Either"
```

---

### Task 4: KDoc — order package

**Files:**
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/order/OrderStatus.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/order/Order.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/order/Executor.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/order/ServiceBrand.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/order/ExtraService.kt`

**Step 1: Add KDoc to OrderStatus.kt**

Add before `sealed class OrderStatus`:
```kotlin
/**
 * Order lifecycle state machine.
 *
 * Represents all possible states of a taxi order from creation through completion.
 * Use companion object sets ([active], [ongoing], [nonInteractive]) to check order
 * state groups without exhaustive when-expressions.
 *
 * ## Lifecycle
 * ```
 * New → Sending/UserSending/NonStopSending → Appointed → AtAddress → InProgress → Completed
 *                                                                                 → Canceled
 * ```
 *
 * @property id Wire-format identifier used in API communication
 * @since 0.0.1
 */
```

Add before `data class Unknown`:
```kotlin
/** Unrecognized status from API, preserves [originalId] for debugging. */
```

Add before `fun from`:
```kotlin
/** Parses an API status string into the corresponding [OrderStatus]. */
```

**Step 2: Add KDoc to Order.kt**

Add before `data class Order`:
```kotlin
/**
 * Complete taxi order aggregate containing all order details.
 *
 * This is the primary domain model returned from order-related API calls.
 * Contains nested types for executor info, vehicle details, route, and pricing.
 *
 * @property comment Passenger's note to the driver
 * @property dateTime Order creation timestamp (epoch millis)
 * @property executor Assigned driver details
 * @property id Unique order identifier
 * @property paymentType Selected payment method
 * @property service Service/tariff name
 * @property status Current order lifecycle state
 * @property statusTime History of status transitions with timestamps
 * @property taxi Route, pricing, and tariff details
 * @since 0.0.1
 */
```

Add before `data class Executor` (inside Order):
```kotlin
/** Assigned driver with contact info, location, and vehicle details. */
```

Add before `data class Coords` (inside Executor):
```kotlin
/** Real-time driver coordinates and heading. */
```

Add before `data class Vehicle`:
```kotlin
/** Driver's vehicle information. */
```

Add before `data class StatusTime`:
```kotlin
/** Records when the order transitioned to a specific status. */
```

Add before `data class Taxi`:
```kotlin
/** Route, pricing, and tariff details for the order. */
```

Add before `data class Route` (inside Taxi):
```kotlin
/** A waypoint in the order's route. */
```

Add before `fun Order.Executor.toExecutor()`:
```kotlin
/** Converts this detailed executor to a lightweight [Executor] for map tracking. */
```

**Step 3: Add KDoc to Executor.kt**

```kotlin
/**
 * Lightweight executor location data for real-time map tracking.
 *
 * Unlike [Order.Executor], this contains only position data needed
 * for rendering the driver marker on the map.
 *
 * @property id Driver identifier
 * @property lat Current latitude
 * @property lng Current longitude
 * @property heading Vehicle heading in degrees (0-360)
 * @property distance Distance to pickup point in meters
 * @since 0.0.1
 */
```

**Step 4: Add KDoc to ServiceBrand.kt**

```kotlin
/**
 * Taxi service brand/company information.
 *
 * @property id Unique brand identifier
 * @property name Display name of the service
 * @property photo URL of the brand logo
 * @since 0.0.1
 */
```

**Step 5: Add KDoc to ExtraService.kt**

```kotlin
/**
 * Optional extra service that can be added to an order.
 *
 * Cost can be either a fixed amount or a percentage of the base fare,
 * determined by [costType].
 *
 * @property id Service identifier
 * @property cost Cost value (interpreted based on [costType])
 * @property name Display name
 * @property costType Either [COST_TYPE_COST] for fixed or [COST_TYPE_PERCENT] for percentage
 * @since 0.0.1
 */
```

Add before `val isPercentCost`:
```kotlin
/** Returns true if this service's cost is a percentage of the base fare. */
```

**Step 6: Commit**

```bash
git add core/src/commonMain/kotlin/uz/yalla/core/order/
git commit -m "docs(core): add KDoc to order package — Order, OrderStatus, Executor, ServiceBrand, ExtraService"
```

---

### Task 5: KDoc — location package

**Files:**
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/location/Address.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/location/AddressOption.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/location/SavedAddress.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/location/Route.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/location/PointRequest.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/location/PointKind.kt`

**Step 1: Add KDoc to Address.kt**

```kotlin
/**
 * Basic address representation with geographic coordinates.
 *
 * @property id Database identifier, null for addresses not yet persisted
 * @property name Human-readable address string
 * @property lat Latitude
 * @property lng Longitude
 * @property isFromDatabase True if this address was loaded from local storage
 * @since 0.0.1
 */
```

**Step 2: Add KDoc to AddressOption.kt**

```kotlin
/**
 * Address search result or autocomplete suggestion.
 *
 * Returned by address search APIs, includes distance from current location
 * for sorting relevance.
 *
 * @property id Unique identifier from search provider
 * @property title Primary address text (street, building)
 * @property address Secondary address text (city, region)
 * @property distance Distance from current location in meters
 * @property lat Latitude
 * @property lng Longitude
 * @property isFromDatabase True if from local history rather than search API
 * @since 0.0.1
 */
```

**Step 3: Add KDoc to SavedAddress.kt**

```kotlin
/**
 * User's saved/favorite address with navigation metrics.
 *
 * @property distance Distance from current location in meters
 * @property duration Estimated travel time in seconds
 * @property lat Latitude
 * @property lng Longitude
 * @property address Full address string
 * @property title User-assigned label
 * @property kind Place category (home, work, other)
 * @property parent Parent location info (e.g., city name)
 * @since 0.0.1
 */
```

Add before `data class Parent`:
```kotlin
/** Parent location for hierarchical address display. */
```

**Step 4: Add KDoc to Route.kt**

```kotlin
/**
 * A calculated route between waypoints.
 *
 * @property distance Total route distance in meters
 * @property duration Estimated travel time in seconds
 * @property points Ordered list of coordinates forming the route polyline
 * @since 0.0.1
 */
```

Add before `data class Point`:
```kotlin
/** A single coordinate in the route polyline. */
```

**Step 5: Add KDoc to PointRequest.kt**

```kotlin
/**
 * A waypoint for route calculation requests.
 *
 * @property kind Role of this point in the route (start, intermediate, stop)
 * @property lng Longitude
 * @property lat Latitude
 * @since 0.0.1
 */
```

**Step 6: Add KDoc to PointKind.kt**

```kotlin
/**
 * Classification of a waypoint in a route.
 *
 * @property wireValue API wire-format identifier
 * @since 0.0.1
 */
```

**Step 7: Commit**

```bash
git add core/src/commonMain/kotlin/uz/yalla/core/location/
git commit -m "docs(core): add KDoc to location package — Address, Route, SavedAddress, PointKind"
```

---

### Task 6: KDoc — payment and profile packages

**Files:**
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/payment/PaymentKind.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/payment/PaymentCard.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/profile/Client.kt`

**Step 1: Add KDoc to PaymentKind.kt**

```kotlin
/**
 * Payment method for an order.
 *
 * Either [Cash] or [Card] with linked card details. Factory method [from]
 * handles API deserialization with fallback to [Cash] for unknown types.
 *
 * @property id Wire-format identifier ("cash" or "card")
 * @since 0.0.1
 */
```

Add before `data object Cash`:
```kotlin
/** Cash payment. */
```

Add before `data class Card`:
```kotlin
/**
 * Card payment with linked card details.
 *
 * @property cardId Unique card identifier from payment provider
 * @property maskedNumber Masked card PAN for display (e.g., "**** 1234")
 */
```

Add before `fun from`:
```kotlin
/**
 * Parses payment type from API response fields.
 *
 * Falls back to [Cash] if [id] is unrecognized or [cardId] is blank.
 */
```

**Step 2: Add KDoc to PaymentCard.kt**

```kotlin
/**
 * A saved payment card from the user's wallet.
 *
 * @property cardId Unique card identifier from payment provider
 * @property maskedPan Masked card number for display
 * @since 0.0.1
 */
```

Add before `fun toPaymentType()`:
```kotlin
/** Converts to [PaymentKind.Card] for use in order creation. */
```

**Step 3: Add KDoc to Client.kt**

```kotlin
/**
 * User profile data returned from the API.
 *
 * @property phone Phone number (primary identifier)
 * @property name First name
 * @property surname Last name
 * @property image Profile photo URL
 * @property birthday Date of birth string
 * @property balance Account balance in smallest currency unit
 * @property gender Gender identifier string
 * @since 0.0.1
 */
```

**Step 4: Commit**

```bash
git add core/src/commonMain/kotlin/uz/yalla/core/payment/ core/src/commonMain/kotlin/uz/yalla/core/profile/
git commit -m "docs(core): add KDoc to payment and profile packages"
```

---

### Task 7: KDoc — contract packages (preferences + location)

**Files:**
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/contract/preferences/SessionPreferences.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/contract/preferences/ConfigPreferences.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/contract/preferences/PositionPreferences.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/contract/preferences/InterfacePreferences.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/contract/preferences/UserPreferences.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/contract/location/LocationProvider.kt`

**Step 1: Add KDoc to SessionPreferences.kt**

```kotlin
/**
 * Contract for session-related persistent storage.
 *
 * Manages authentication tokens, device registration, and guest mode state.
 * All properties are reactive [Flow]s for automatic UI updates on changes.
 * Implemented in the data module using Multiplatform Settings.
 *
 * @since 0.0.1
 */
```

Add before `fun performLogout()`:
```kotlin
/** Clears all session data (tokens, guest state, device registration). */
```

**Step 2: Add KDoc to ConfigPreferences.kt**

```kotlin
/**
 * Contract for app configuration storage.
 *
 * Stores server-provided configuration values (support contacts,
 * payment limits, policy URLs). All properties are reactive [Flow]s.
 * Implemented in the data module.
 *
 * @since 0.0.1
 */
```

**Step 3: Add KDoc to PositionPreferences.kt**

```kotlin
/**
 * Contract for persisting the user's last known positions.
 *
 * Used to restore map camera position and show approximate location
 * before GPS lock is acquired. All properties are reactive [Flow]s.
 *
 * @since 0.0.1
 */
```

**Step 4: Add KDoc to InterfacePreferences.kt**

```kotlin
/**
 * Contract for UI-related preferences.
 *
 * Controls app locale, theme, map provider, and onboarding state.
 * All properties are reactive [Flow]s for automatic UI updates.
 *
 * @since 0.0.1
 */
```

**Step 5: Add KDoc to UserPreferences.kt**

```kotlin
/**
 * Contract for user profile data storage.
 *
 * Persists locally-cached user profile fields and preferred payment method.
 * All properties are reactive [Flow]s.
 *
 * @since 0.0.1
 */
```

**Step 6: Add KDoc to LocationProvider.kt**

```kotlin
/**
 * Contract for GPS location tracking.
 *
 * Provides reactive location updates and one-shot location queries.
 * Implemented per-platform (Android/iOS) in the platform module.
 *
 * ## Usage
 * ```kotlin
 * locationProvider.currentLocation
 *     .filterNotNull()
 *     .collect { point -> updateMapCamera(point) }
 * ```
 *
 * @since 0.0.1
 */
```

Add before `fun getCurrentLocation()`:
```kotlin
/** Returns the last known location or null if unavailable. */
```

Add before `fun getCurrentLocationOrDefault()`:
```kotlin
/** Returns the last known location or [GeoPoint.Zero] if unavailable. */
```

Add before `fun startTracking()`:
```kotlin
/** Starts receiving location updates via [currentLocation]. */
```

Add before `fun stopTracking()`:
```kotlin
/** Stops location updates to conserve battery. */
```

**Step 7: Commit**

```bash
git add core/src/commonMain/kotlin/uz/yalla/core/contract/
git commit -m "docs(core): add KDoc to contract packages — preferences and location interfaces"
```

---

### Task 8: KDoc — session and util packages

**Files:**
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/session/UnauthorizedSessionEvents.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/util/DateTimeFormatting.kt`
- Modify: `core/src/commonMain/kotlin/uz/yalla/core/util/Normalization.kt`

Note: `NullableDefaults.kt`, `StringFormatter.kt`, `GeoPoint.kt` already have KDoc. Settings enums (`LocaleKind`, `MapKind`, `ThemeKind`), `GenderKind`, and `PlaceKind` already have KDoc.

**Step 1: Add KDoc to UnauthorizedSessionEvents.kt**

```kotlin
/**
 * Global event bus for unauthorized session events.
 *
 * When the API returns a 401/unauthorized response, publish an event here.
 * Observers (typically the root navigator) collect [events] to trigger
 * logout and redirect to the login screen.
 *
 * Uses a [CONFLATED][kotlinx.coroutines.channels.Channel.Factory.CONFLATED] channel
 * so only the latest event is retained if the collector is slow.
 *
 * ## Usage
 * ```kotlin
 * // Publishing (in network interceptor):
 * UnauthorizedSessionEvents.publish()
 *
 * // Collecting (in root navigator):
 * UnauthorizedSessionEvents.events.collect { navigateToLogin() }
 * ```
 *
 * @since 0.0.1
 */
```

Add before `fun publish()`:
```kotlin
/** Emits an unauthorized event to all active collectors. */
```

**Step 2: Add KDoc to DateTimeFormatting.kt**

Add before `fun Long?.toLocalFormattedDate()`:
```kotlin
/**
 * Formats an epoch timestamp to a localized date string (dd.MM.yyyy).
 *
 * Handles both seconds and milliseconds epoch formats automatically.
 * Returns empty string if null or non-positive.
 */
```

Add before `fun Long?.toLocalFormattedTime()`:
```kotlin
/**
 * Formats an epoch timestamp to a localized time string (HH:mm).
 *
 * Handles both seconds and milliseconds epoch formats automatically.
 * Returns empty string if null or non-positive.
 */
```

**Step 3: Add KDoc to Normalization.kt**

Add before `internal fun String?.normalizedId()`:
```kotlin
/** Trims, lowercases, and defaults to empty string. Used for enum/ID parsing. */
```

Add before `internal fun String?.normalizedLocaleCode()`:
```kotlin
/** Normalizes locale codes: trims, replaces underscores with hyphens, lowercases. */
```

**Step 4: Run all core tests**

Run: `cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew :core:allTests`
Expected: All tests pass

**Step 5: Commit**

```bash
git add core/src/commonMain/kotlin/uz/yalla/core/session/ core/src/commonMain/kotlin/uz/yalla/core/util/
git commit -m "docs(core): add KDoc to session and util packages"
```

---

### Task 9: Create docs/ folder structure

**Files:**
- Create: `docs/architecture/overview.md`
- Create: `docs/architecture/adr/001-either-error-handling.md`
- Create: `docs/guides/onboarding.md`

**Step 1: Write architecture overview**

`docs/architecture/overview.md`:
```markdown
# yalla-sdk Architecture Overview

## Module Dependency Graph

```
┌─────────────────────────────────────────────────┐
│                  composites                      │
│              (complex UI screens)                │
├──────────┬──────────┬──────────┬────────────────┤
│primitives│   maps   │  media   │   firebase     │
│(basic UI)│(map impl)│(img/cam) │  (analytics)   │
├──────────┴──────────┴──────────┴────────────────┤
│                  foundation                      │
│        (BaseViewModel, DI, error mapping)        │
├─────────────────────────────────────────────────┤
│                    design                        │
│              (theme, tokens)                     │
├─────────────────────────────────────────────────┤
│                     data                         │
│       (Ktor networking, repositories,            │
│        preferences implementation)               │
├──────────────────┬──────────────────────────────┤
│     platform     │         resources             │
│  (expect/actual) │    (Compose resources)        │
├──────────────────┴──────────────────────────────┤
│                     core                         │
│     (domain models, contracts, Either,           │
│      error types, utilities)                     │
└─────────────────────────────────────────────────┘
```

## Key Patterns

### Error Handling
All data operations return `Either<Data, DataError>` instead of throwing exceptions.
See [ADR-001](adr/001-either-error-handling.md).

### Reactive Preferences
Core defines preference contracts as interfaces with `Flow<T>` properties.
Data module implements them using Multiplatform Settings + DataStore.

### State Machine
`OrderStatus` is a sealed class modeling the complete order lifecycle.
Status groupings (`active`, `ongoing`, `nonInteractive`) simplify UI logic.

### Contract Pattern
Core defines interfaces (`LocationProvider`, `*Preferences`), platform/data modules implement them.
This keeps core dependency-free and testable.

## Targets

- **Android**: via `com.android.kotlin.multiplatform.library`
- **iOS**: `iosArm64`, `iosSimulatorArm64` (static frameworks)

## Publishing

GitHub Packages under `uz.yalla.sdk` group. BOM module for version alignment.
```

**Step 2: Write ADR-001**

`docs/architecture/adr/001-either-error-handling.md`:
```markdown
# ADR-001: Either Pattern for Error Handling

**Status:** Accepted
**Date:** 2024-01-01

## Context

Business logic error handling needed a consistent, type-safe approach across all SDK modules.
Raw try-catch scatters error handling, loses type information, and makes it impossible to
exhaustively handle all error cases at compile time.

## Decision

Use a custom `Either<D, E>` sealed interface where:
- `Either.Success<D>` wraps successful results
- `Either.Failure<E>` wraps typed errors (typically `DataError`)

All repository and data-source functions return `Either` instead of throwing exceptions.
Extension functions `onSuccess`/`onFailure` enable fluent chaining.

## Consequences

- **Positive:** Compile-time exhaustive error handling, no uncaught exceptions in business logic
- **Positive:** Chainable API reduces boilerplate
- **Positive:** DataError sealed hierarchy ensures all error cases are enumerated
- **Trade-off:** Slightly more verbose than raw return types for simple cases
```

**Step 3: Write onboarding guide**

`docs/guides/onboarding.md`:
```markdown
# Developer Onboarding Guide

## Prerequisites

- Android Studio Meerkat+ or Fleet
- Xcode 16+ (for iOS builds)
- JDK 11+

## Getting Started

1. Clone the repository
2. Open in Android Studio (KMP plugin required)
3. Sync Gradle — all dependencies resolve from Maven Central and Google

## Project Structure

```
yalla-sdk/
├── build-logic/       # Convention plugins (KMP, Compose setup)
├── core/              # Domain models, contracts, error types
├── data/              # Network layer, repositories, preferences impl
├── foundation/        # BaseViewModel, DI setup, error mapping
├── design/            # Theme tokens
├── platform/          # Platform-specific implementations (expect/actual)
├── resources/         # Compose multiplatform resources
├── primitives/        # Basic reusable UI components
├── composites/        # Complex UI screens/flows
├── maps/              # Google Maps integration
├── media/             # Image/camera handling
├── firebase/          # Firebase analytics, crashlytics
├── bom/               # Bill of Materials for version alignment
└── docs/              # Documentation
```

## Core Concepts

### Error Handling
We use `Either<Data, DataError>` instead of exceptions. See `core/result/Either.kt`.

### Preference Contracts
Core defines interfaces with `Flow<T>` properties. Data module implements them.
Example: `SessionPreferences` → `SessionPreferencesImpl` in data module.

### Order Status
`OrderStatus` is a sealed class state machine. Use companion object sets to check groups:
- `OrderStatus.active` — driver assigned
- `OrderStatus.ongoing` — not finished
- `OrderStatus.nonInteractive` — being sent, no driver yet

## Running Tests

```bash
./gradlew :core:allTests        # Core module tests
./gradlew allTests              # All module tests
```

## Code Quality

```bash
./gradlew ktlintCheck           # Kotlin style
./gradlew detekt                # Static analysis
./gradlew spotlessCheck         # Formatting
```

## API Documentation

```bash
./gradlew dokkaHtmlMultiModule  # Generate HTML docs → build/dokka/
```
```

**Step 4: Commit**

```bash
git add docs/
git commit -m "docs: add architecture overview, ADR-001, and onboarding guide"
```

---

### Task 10: Create root README.md

**Files:**
- Create: `README.md` (project root)

**Step 1: Write README.md**

```markdown
# yalla-sdk

Kotlin Multiplatform SDK for the Yalla ride-hailing platform.

## Modules

| Module | Description |
|--------|-------------|
| `core` | Domain models, error types, contracts |
| `data` | Networking (Ktor), repositories, preferences |
| `foundation` | BaseViewModel, DI, error mapping |
| `design` | Theme system and design tokens |
| `platform` | Platform-specific implementations |
| `resources` | Compose Multiplatform resources |
| `primitives` | Basic reusable Compose components |
| `composites` | Complex UI screens and flows |
| `maps` | Google Maps integration |
| `media` | Image and camera handling |
| `firebase` | Firebase analytics and crashlytics |
| `bom` | Bill of Materials for version alignment |

## Setup

Add the BOM and required modules to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(platform("uz.yalla.sdk:bom:<version>"))
    implementation("uz.yalla.sdk:core")
    implementation("uz.yalla.sdk:data")
    // ... other modules as needed
}
```

## Documentation

- [Architecture Overview](docs/architecture/overview.md)
- [Onboarding Guide](docs/guides/onboarding.md)
- [Architecture Decisions](docs/architecture/adr/)

Generate API docs: `./gradlew dokkaHtmlMultiModule`

## Development

```bash
./gradlew build             # Build all modules
./gradlew :core:allTests    # Run core tests
./gradlew ktlintCheck       # Check code style
./gradlew detekt            # Static analysis
```

## Targets

- Android (minSdk 26)
- iOS (arm64, simulatorArm64)

## Publishing

Published to GitHub Packages under `uz.yalla.sdk` group.
```

**Step 2: Commit**

```bash
git add README.md
git commit -m "docs: add root README.md"
```

---

### Task 11: Verify Dokka generation

**Step 1: Run Dokka**

Run: `cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew :core:dokkaGenerate`
Expected: HTML output in `core/build/dokka/` with all KDoc rendered

**Step 2: Run full test suite**

Run: `cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew :core:allTests`
Expected: All tests pass

**Step 3: Run lint checks**

Run: `cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew :core:ktlintCheck`
Expected: No violations

**Step 4: Final commit (if any fixes needed)**

```bash
git commit -m "docs(core): fix any lint/dokka issues"
```
