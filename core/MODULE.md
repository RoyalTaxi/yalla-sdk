# Module core

Shared, stable primitives for the Yalla SDK and its clients — the contracts-and-pure-logic layer
that every other module and both platforms (Android, iOS via the exported framework) build on.

## What lives here

- **Result & errors** — [`Either`][uz.yalla.core.result.Either] (the universal
  `Either<DomainError, T>`
  result) and the [`DomainError`][uz.yalla.core.error.DomainError] taxonomy.
- **Geo math** — [`GeoPoint`][uz.yalla.core.geo.GeoPoint], `distanceTo`/`bearingTo`, and
  `headingAlongRoute` (the live driver-heading primitive).
- **Phone** — [`UzPhone`][uz.yalla.core.phone.UzPhone], the single source of truth for `+998`
  numbers
  (canonical national form, E.164, display mask, validation).
- **Domain models** — order, payment, location, profile, settings types and their wire decoders.
- **Typed ids** — [`Ids`][uz.yalla.core.identity.OrderId] value classes (type-safe on JVM/Android;
  note
  they erase to the underlying type at the ObjC/Swift boundary).
- **Preferences & session contracts** — the `*Preferences` interfaces (implemented in `datastore`)
  and
  [`SessionEventBus`][uz.yalla.core.session.SessionEventBus] (the 401/unauthorized broadcast).

## Conventions

- **Decoders:** `from(id)` is total and normalizes case/whitespace via the internal `normalizedId()`
  helper, defaulting to a defined fallback; `of(...)` parsers are strict and may fail (return
  `null`).
- **Locale:** [`LocaleKind`][uz.yalla.core.settings.LocaleKind] is identified by a BCP-47 primary
  language subtag (`code`), not an opaque wire id, and strips region/script subtags before matching.
- **PII safety:** credential/PII-bearing types ([`Client`][uz.yalla.core.profile.Client],
  `Order.Driver`,
  the payment card types) redact their `toString()` to avoid CWE-532 log leaks.
- **iOS interop:** prefer the total `Either` accessors (`fold`/`getOrNull`/`getOrElse`) over
  `getOrThrow`, whose thrown Kotlin exception does not bridge to a catchable Swift error.

## Public contract

The published surface is pinned by the committed ABI dumps under `core/api/` (`core.klib.api`,
`android/core.api`); any diff is a reviewable breaking change. KDoc on the public surface is the
buyer-facing documentation.
