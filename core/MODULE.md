# Module core

> Logic atoms — `Either`, `DataError`, domain enums, preference contracts, util.

## What this is

- `Either<E, D>` and the `DataError.Network.*` sealed hierarchy: the SDK-wide
  result and error types. Every repository / use-case returns
  `Either<DataError, T>`; pattern-match in features.
- Domain models: `Order` (with `Executor`, `Taxi`, `Route`, `StatusTime`),
  `OrderStatus`, `ExtraService`, `ServiceBrand`, `Address`, `AddressOption`,
  `SavedAddress`, `Route`, `PointRequest`, `GeoPoint`, `PaymentKind`,
  `PaymentCard`, `Client`.
- Domain enums: `LocaleKind`, `MapKind`, `ThemeKind`, `GenderKind`,
  `PlaceKind`, `PointKind`. Each has a tolerant `from(code: String?)`
  factory that normalizes input and falls back to a documented default.
- Preference *contracts* (interfaces only): `ConfigPreferences`,
  `InterfacePreferences`, `PositionPreferences`, `SessionPreferences`,
  `StaticPreferences`, `UserPreferences`. Implementations live in `data`.
- Location *contract* (interface only): `LocationProvider`. Implementation
  lives in `foundation`.
- Session events: `UnauthorizedSessionEvents` — conflated `Channel<Unit>`
  signal for token-refresh failure handling; producers and consumers
  defined here, wiring lives in `data`.
- Utilities: `formatMoney`, `formatArgs`, `or0`/`orFalse` null defaults,
  `toLocalFormattedDate` / `toLocalFormattedTime`, `normalizedId`.

## What this is NOT

- **Not** a networking module — no `Service`, no `safeApiCall`, no DTOs.
  Those live in `data`.
- **Not** a UI module — no Compose code, no string resources, no themes.
  Those live in `design` / `primitives` / `composites` / `resources`.
- **Not** a platform module — no Android- or iOS-specific types. Those
  live in `platform`.
- **Not** a mapper layer — DTO ↔ domain mapping happens in `data`'s
  `internal object` mappers; `core` only holds the domain side.

## Usage

```kotlin
implementation("uz.yalla.sdk:core")
```

```kotlin
suspend fun loadOrder(id: Int): Either<DataError, Order> = orderRepository.getOrder(id)

loadOrder(orderId)
    .onSuccess { order -> updateUi(order) }
    .onFailure { error ->
        when (error) {
            is DataError.Network.ClientWithMessage -> showError(error.message)
            is DataError.Network                   -> showGenericNetworkError(error)
        }
    }
```

```kotlin
val locale = LocaleKind.from(persistedCode) // tolerant, falls back to Uz
```

## Notes

- **`OrderStatus.in_fetters` alias** is a product-specific deserialization
  workaround — Ildam's older API spelled the in-progress status as
  `"in_fetters"` instead of `"in_progress"`. The alias maps both spellings
  to the same `OrderStatus.InProgress` value so consumers don't need to
  per-status-normalize. If a future PBX migration removes the legacy form,
  the alias can be deleted (safe — `from()` keeps the new spelling working).
- **`LocaleKind` has only `Uz` and `Ru`** by deliberate ADR-014 narrowing
  in phase 3 of the wider Ildam product; persisted `"en"` or `"uz-Cyrl"`
  values from earlier installs fall back to `Uz` via `from()`.
- **Generic-parameter order on `Either<E, D>`** is error-first, success-second.
  Matches Arrow's `Either<Left, Right>` and Rust's `Result<T, E>` (dual).
- **`UnauthorizedSessionEvents`** plays the `SessionExpiredSignal` role
  recommended by `CLAUDE.md` — conflated channel, idempotent consumer.
  Different name, same shape; rename is a future cosmetic refactor.

## Depends on

- `kotlinx.coroutines.core`
- `kotlinx.serialization.json`
- `kotlinx.datetime`
- No SDK-internal deps. `core` is a leaf in the brick stack.
