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
