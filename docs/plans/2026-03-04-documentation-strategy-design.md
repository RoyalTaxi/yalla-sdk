# Documentation Strategy for yalla-sdk

**Date:** 2026-03-04
**Status:** Approved
**Scope:** Core module first, then other modules after refactor + tests

## Context

yalla-sdk — 11 modulli KMP library (30K+ lines, 376 fayl). Internal SDK, faqat Ildam team ishlatadi.
Hozirgi holat: testlar yozilgan (core), ktlint/detekt/spotless sozlangan, lekin dokumentatsiya nol.

## Approach: KDoc + Dokka + docs/ Markdown

### Layer 1: KDoc (In-code documentation)
- Har bir public class, function, property, sealed class, enum — majburiy KDoc
- Internal/private — faqat murakkab logika bo'lsa
- Format: Summary (imperative mood) → Description → @param/@return → Usage example → @since

### Layer 2: Dokka (Auto-generated API reference)
- build-logic convention plugin ga Dokka qo'shiladi
- Har bir modulda MODULE.md — modul overview
- Output: HTML (browsable)

### Layer 3: docs/ Folder (Architecture & guides)
- `docs/architecture/` — module map, dependency graph, ADRs
- `docs/guides/` — onboarding, module usage
- `docs/plans/` — design documents

## Current Phase: Core Module

### Core Module Inventory (33 files, ~1.5K lines)

**Packages:**
- `order` — Order, OrderStatus, Executor, ServiceBrand, ExtraService (5 files)
- `settings` — LocaleKind, MapKind, ThemeKind (3 files, already have KDoc)
- `contract.preferences` — 5 interfaces (PositionPreferences, InterfacePreferences, ConfigPreferences, SessionPreferences, UserPreferences)
- `contract.location` — LocationProvider interface
- `result` — Either<D, E> sealed interface
- `util` — DateTimeFormatting, NullableDefaults, Normalization, StringFormatter (4 files, some have KDoc)
- `location` — SavedAddress, PointKind, AddressOption, Address, PlaceKind, PointRequest, Route (7 files)
- `payment` — PaymentCard, PaymentKind (2 files)
- `geo` — GeoPoint (1 file, has KDoc)
- `profile` — GenderKind, Client (2 files)
- `error` — DataError sealed hierarchy (1 file)
- `session` — UnauthorizedSessionEvents (1 file)

**KDoc status:** ~30% has KDoc (settings enums, GeoPoint, NullableDefaults, StringFormatter)
**Remaining:** ~70% needs KDoc (23+ files)

## KDoc Standard

```kotlin
/**
 * One-line summary in imperative mood.
 *
 * Detailed description if needed — when to use, important behavior.
 *
 * ## Usage
 * ```kotlin
 * val result = myFunction("param")
 * ```
 *
 * @param name Parameter description
 * @return What it returns
 * @see RelatedClass
 * @since 0.0.4
 */
```

## Non-goals (this phase)
- Other modules documentation (after refactor + tests)
- Docusaurus/static site (overkill for internal team)
- Versioned docs
