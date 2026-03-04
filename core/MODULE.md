# Module core

Core types, error hierarchy, and contracts for the yalla-sdk.

This module provides the foundational building blocks used across all other SDK modules.

# Package uz.yalla.core.error

Sealed error hierarchy (`DataError`) for typed error handling.

# Package uz.yalla.core.result

`Either<D, E>` result type for functional error handling.

# Package uz.yalla.core.order

Order domain models and status state machine.

# Package uz.yalla.core.location

Address, route, and geographic point types.

# Package uz.yalla.core.geo

`GeoPoint` with Haversine distance calculation.

# Package uz.yalla.core.payment

Payment types (`Cash`, `Card`) and card models.

# Package uz.yalla.core.profile

User profile models.

# Package uz.yalla.core.settings

App settings enums (locale, theme, map provider).

# Package uz.yalla.core.session

Session event bus for unauthorized state handling.

# Package uz.yalla.core.contract.preferences

Reactive preference contracts (Flow-based).

# Package uz.yalla.core.contract.location

Location tracking contract.

# Package uz.yalla.core.util

Formatting, normalization, and extension utilities.
