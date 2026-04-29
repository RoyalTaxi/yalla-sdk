# CORE_AUDIT.md

Audit output for wave 1 of `PHASE_2_CORE_PLAN.md`. Drives waves 2-10. Findings keyed to `CLEANUP_CRITERIA.md`. All paths are absolute under `/Users/islom/StudioProjects/yalla-sdk/`.

---

## 1. AI-blob deletions (criterion 2)

### `core/src/commonMain/kotlin/uz/yalla/core/contract/location/LocationProvider.kt`

- **2-1** lines 6-19, 23-32, 35-42, 44-52, 54-62, 64-72 — every block in this file is signature-paraphrasing KDoc (`@see startTracking` etc.) plus an inline `@since 0.0.1` ceremony banner. Real behavior info (permissions, threading) is in `foundation/.../LocationManager.kt`, not here. (~10 min)
- **2-2** none.

### `core/src/commonMain/kotlin/uz/yalla/core/error/DataError.kt`

- **2-1** line 28-31 — the "Naming note" KDoc paragraph references an "ADR-022" that doesn't exist in this repo (`docs/adr/` is gone per criterion 5; per CLAUDE.md, ADRs aren't being reintroduced). Stale meta-commentary. (~2 min)
- **2-1** lines 60-62, 70-72, 81-83, 95-97, 110-112, 118-120 — `@since 0.0.16` etc. tags repeated on every variant; carries no per-variant info. (~5 min)
- **2-4** lines 47-115 — the **semantic top-level variants** `Unauthorized`, `Forbidden(reason)`, `Conflict(reason)`, `Validation(fields)`, `NotFound` are dead code. Verified producer/consumer scan: nothing in `data/src/commonMain` ever returns any of them (`SafeApiCall.kt` only emits `DataError.Network.*` variants — see lines 71, 81, 87, 90, 91, 94, 104, 110, 113, 115, 117, 119, 121, 123). The KDoc on lines 23-29 claims "SafeApiCall is the sole producer," which is true only of the `Network` branch; the semantic branch has zero producers. Consumed only by `foundation/.../DefaultDataErrorMapper.kt:32-36` as exhaustive `when` branches that never fire in production. (~30 min once Islom decides; behavior-preserving deletion)
- **2-5** related: if 2-4 above is approved, `Network` ceases to be `sealed class Network : DataError()` and becomes the type itself — collapsing one level of nesting. Optional. (~15 min)

### `core/src/commonMain/kotlin/uz/yalla/core/geo/GeoPoint.kt`

- **2-1** lines 28-32, 41-44, 56-58 — `@property lat`/`@property lng` blocks paraphrase the names; `@since 0.0.1` is bottom-of-doc cruft. (~3 min)

### `core/src/commonMain/kotlin/uz/yalla/core/location/Address.kt`

- **2-1** lines 8-20 — entire KDoc reads `@property id Database identifier, null for...`/`@property name Human-readable address string` etc. — pure paraphrase of the field. The "used as the standard address model" line is the only sentence with information density. (~2 min)

### `core/src/commonMain/kotlin/uz/yalla/core/location/AddressOption.kt`

- **2-1** lines 7-22 — same shape as `Address.kt`: `@property id Unique identifier from search provider`/`@property title Primary address text` etc. (~2 min)

### `core/src/commonMain/kotlin/uz/yalla/core/location/PlaceKind.kt`

- **2-1** lines 7-15, 32-41 — KDoc on the enum and on `from()` repeat the signature; `@property id Wire-format identifier used in API communication` is restating the property. (~2 min)

### `core/src/commonMain/kotlin/uz/yalla/core/location/PointKind.kt`

- **2-1** lines 11-21 — `PointKindSerializer` KDoc paraphrases the override implementations. (~2 min)
- **2-1** lines 34-44, 56-65 — same as PlaceKind. (~2 min)
- **2-3** lines 22-32 — `PointKindSerializer` is a custom serializer that re-implements what an `EnumSerializer` already does *if* the fallback-to-`POINT` is moved into `PointKind.from`'s default branch. Worth flagging as a single-use abstraction. (See section 4 — could collapse.) (~30 min if approved)

### `core/src/commonMain/kotlin/uz/yalla/core/location/PointRequest.kt`

- **2-1** lines 6-18 — pure `@property` paraphrase. (~2 min)

### `core/src/commonMain/kotlin/uz/yalla/core/location/Route.kt`

- **2-1** lines 6-17, 24-30, 30-35 — every nested data class repeats `@property lat Latitude in degrees`. Outer paragraph "A calculated route between waypoints, returned by the routing API" carries info; the rest doesn't. (~3 min)

### `core/src/commonMain/kotlin/uz/yalla/core/location/SavedAddress.kt`

- **2-1** lines 7-18, 31-39 — full property paraphrase. (~3 min)

### `core/src/commonMain/kotlin/uz/yalla/core/order/Executor.kt`

- **2-1** lines 6-18 — full property paraphrase. (~2 min)

### `core/src/commonMain/kotlin/uz/yalla/core/order/ExtraService.kt`

- **2-1** lines 6-30, 39, 42, 45-52 — KDoc + property paraphrases. (~3 min)
- **2-5** line 32-36 + 38-44 + 51-52 — `costType: String` with two const sentinels (`COST_TYPE_COST`, `COST_TYPE_PERCENT`) plus an `isPercentCost` accessor reads as a hand-rolled enum. See section 4 (rewrite to enum, ~15 min).

### `core/src/commonMain/kotlin/uz/yalla/core/order/Order.kt`

- **2-1** lines 5-22, 33-50, 62-69, 76-86, 95-101, 109-116, 122-138, 152-159, 165-171, 180-189 — every nested data class has full property-by-property paraphrase + `@since 0.0.1`. ~70% of the file is comment. (~30 min)

### `core/src/commonMain/kotlin/uz/yalla/core/order/OrderStatus.kt`

- **2-1** lines 11-21 — `OrderStatusSerializer` KDoc paraphrases the implementation. (~2 min)
- **2-1** line 73 — KDoc on `Unknown` is the one comment with information density (preserves originalId). Keep.
- **2-2** lines 100, 109, 121 — the three `val active`/`ongoing`/`nonInteractive` headline comments are non-redundant. Keep.

### `core/src/commonMain/kotlin/uz/yalla/core/order/ServiceBrand.kt`

- **2-1** lines 6-13 — pure property paraphrase. (~2 min)
- **2-4** entire file — `ServiceBrand` has zero importers outside core (verified by `grep -rn "ServiceBrand" --include="*.kt" | grep -v core/`). Round-trip-tested but not used. Either delete (~5 min) or keep until we know it's used by an unmerged consumer — flag for Islom.

### `core/src/commonMain/kotlin/uz/yalla/core/payment/PaymentCard.kt`

- **2-1** lines 6-13, 18-23 — property + factory paraphrase. (~2 min)
- **2-4** entire file — `PaymentCard` has zero importers outside core. `toPaymentType()` factory wraps `PaymentKind.Card(cardId, maskedNumber)` — single-use even within core (only consumed by the round-trip test). Flag for Islom: is this part of the planned wallet API? If not, drop. (~5 min)

### `core/src/commonMain/kotlin/uz/yalla/core/payment/PaymentKind.kt`

- **2-1** lines 13-26 — `PaymentKindSerializer` doc paraphrases the body. The trade-off paragraph (lines 17-22) carries info; keep. The wire-format paragraph repeats. (~3 min)
- **2-1** lines 39-50, 56-58, 62-65, 68-78 — usual `@property`/`@since` paraphrase. (~3 min)

### `core/src/commonMain/kotlin/uz/yalla/core/preferences/ConfigPreferences.kt`

- **2-1** lines 28-43, 49-54, 59-64, 69-74, 79-84, 89-94, 99-104, 109-114, 119-124, 129-134, 139-144 — every setter has `@param value` paraphrase. The class-level KDoc (5-22) carries info; per-setter KDoc largely doesn't. (~10 min)
- **2-2** lines 26, 36, 46, 56, 66, 76, 86, 96, 106, 116, 126, 136 — every getter has a one-line "Phone number for customer support" comment that is the property name spelled out. (~5 min)

### `core/src/commonMain/kotlin/uz/yalla/core/preferences/InterfacePreferences.kt`

- **2-1** lines 27-32, 39-44, 50-55, 60-66, 75-80 — same setter-paraphrase pattern. (~5 min)

### `core/src/commonMain/kotlin/uz/yalla/core/preferences/PositionPreferences.kt`

- **2-1** lines 26-30, 40-44 — `@param value The GeoPoint…` is paraphrase. (~3 min)

### `core/src/commonMain/kotlin/uz/yalla/core/preferences/SessionPreferences.kt`

- **2-1** lines 25-35, 41-49, 55-63, 69-78 — same setter-paraphrase pattern. (~5 min)
- **2-2** the class-level lifecycle KDoc (12-19) is the rare keeper — it's the only place documenting that `clearSession` preserves interface preferences. Keep.

### `core/src/commonMain/kotlin/uz/yalla/core/preferences/StaticPreferences.kt`

- **2-1** lines 26-31, 41-45, 47-50, 52-55, 56-61, 62-67, 68-72, 73-77 — heavy property+setter paraphrase. Cross-references like `@see SessionPreferences.isDeviceRegistered` carry info; descriptions don't. (~5 min)

### `core/src/commonMain/kotlin/uz/yalla/core/preferences/UserPreferences.kt`

- **2-1** lines 21-29, 31-39, 41-49, 51-63 — paraphrase. (~3 min)

### `core/src/commonMain/kotlin/uz/yalla/core/profile/Client.kt`

- **2-1** lines 6-21 — pure `@property` paraphrase. (~2 min)
- **2-4** entire file — `Client` has zero importers outside core. Round-trip-tested but otherwise unused. Flag for Islom (probably part of planned profile API). (~5 min decision)

### `core/src/commonMain/kotlin/uz/yalla/core/profile/GenderKind.kt`

- **2-1** lines 7-15, 32-40 — class + `from()` paraphrase. (~2 min)

### `core/src/commonMain/kotlin/uz/yalla/core/result/Either.kt`

- **2-1** lines 28-37, 39-44 — `Failure` and `Success` data class KDoc paraphrase (`@property error The failure reason` etc.). The class-level paragraph (lines 4-23) is the only one that carries info — it explains the `<E, D>` order convention. Keep that, drop the per-data-class boilerplate. (~3 min)
- Otherwise the extension-function KDoc (`mapSuccess`, `mapFailure`, `getOrNull`, `fold`) carries useful examples; keep.

### `core/src/commonMain/kotlin/uz/yalla/core/session/UnauthorizedSessionEvents.kt`

- KDoc throughout this file is information-dense (CONFLATED rationale, drainPendingEventIfExists rationale). Keep all of it.

### `core/src/commonMain/kotlin/uz/yalla/core/settings/LocaleKind.kt`

- **2-1** lines 7-19, 30-40 — paraphrase. The "ADR-014 narrowed in Phase 3" sentence (15-16) is information-dense; keep that. (~3 min)

### `core/src/commonMain/kotlin/uz/yalla/core/settings/MapKind.kt`

- **2-1** lines 6-17, 28-37 — paraphrase. Class-level "Default on Android"/"Default on iOS" inline comments carry info. (~3 min)

### `core/src/commonMain/kotlin/uz/yalla/core/settings/ThemeKind.kt`

- **2-1** lines 6-16, 30-40 — paraphrase. (~3 min)

### `core/src/commonMain/kotlin/uz/yalla/core/util/DateTimeFormatting.kt`

- **2-1** lines 7-26, 36-54 — usage-block KDoc carries info; the rest is paraphrase. (~3 min)

### `core/src/commonMain/kotlin/uz/yalla/core/util/Mapper.kt`

- **2-3** entire file (lines 1-10) — `typealias Mapper<T, R> = (T) -> R` has zero usages anywhere in the repo (verified: `grep -rn "import uz.yalla.core.util.Mapper\|: Mapper<" --include="*.kt"` matches only its own declaration). Speculative aliasing of the standard `(T) -> R` function type. **Delete the file.** (~2 min)

### `core/src/commonMain/kotlin/uz/yalla/core/util/Normalization.kt`

- KDoc here is concise and carries info. Keep.
- **2-3** verify intent: `normalizedLocaleCode` is called from one site (`LocaleKind.from`). Inlining it into `LocaleKind.from` would be defensible (single-use). Borderline — flag, don't auto-inline. (~5 min decision)

### `core/src/commonMain/kotlin/uz/yalla/core/util/NullableDefaults.kt`

- **2-1** lines 4-9, 14-19, 24-29, 33-39, 44-49, 53-69, 84-90 — heavy "Convenience extension for safely unwrapping…" paraphrase across each `or0`/`orFalse` overload. (~5 min)

### `core/src/commonMain/kotlin/uz/yalla/core/util/StringFormatter.kt`

- **2-1** lines 4-19 — usage-block info-dense; `@param args …each arg's Any.toString` paraphrase below is removable. (~2 min)

---

## 2. Module dependency graph (criterion 4)

`core/build.gradle.kts` declarations:

| line | declaration | libs key |
| ---- | ----------- | -------- |
| 8 | `implementation(libs.kotlinx.coroutines.core)` | `kotlinx-coroutines-core` |
| 9 | `implementation(libs.kotlinx.serialization.json)` | `kotlinx-serialization-json` |
| 10 | `api(libs.kotlinx.datetime)` | `kotlinx-datetime` |
| 11 | `api(libs.kermit)` | `kermit` |
| 15 | `implementation(libs.kotlinx.coroutines.test)` (test) | — |
| 16 | `implementation(libs.turbine)` (test) | — |

**Verification grep results**:

- `kotlinx.coroutines` — used: `Flow` in 7 files (`preferences/*`, `contract/location/LocationProvider.kt`, `session/UnauthorizedSessionEvents.kt`); `Channel`, `receiveAsFlow` in `UnauthorizedSessionEvents.kt`. Keep `implementation`.
- `kotlinx.serialization` — used: `@Serializable`, `@SerialName`, `KSerializer`, `PrimitiveSerialDescriptor`, etc. across `geo/`, `location/`, `order/`, `payment/`, `profile/`, `settings/`. Keep `implementation`.
- `kotlinx.datetime` — used: `TimeZone`, `toLocalDateTime`, `number` in `util/DateTimeFormatting.kt`. **Used internally only**, not re-exported in any public signature. Should be `implementation`, not `api`. (~5 min — flip to `implementation`.)
- `kermit` — **zero usages** in `core/src` (verified `grep -rn "kermit\|Logger\|Severity" core/src/`). Declared but never imported. **Drop the dep entirely.** (~2 min)
- Test deps `kotlinx.coroutines.test`, `turbine` — used in `UnauthorizedSessionEventsTest.kt` (`runTest`, `app.cash.turbine.test`). Keep.

**Recommended `Depends on` block for `core/MODULE.md`**:

```
## Depends on
- kotlinx.coroutines.core
- kotlinx.serialization.json
- kotlinx.datetime
- No SDK-internal deps.
```

(Drop `kermit` line entirely. Demote `kotlinx.datetime` to `implementation`. `core` is a leaf — no SDK-internal deps.)

---

## 3. Restructure candidates (criterion 9-3)

### Orphan from phase 1's flatten

- `/Users/islom/StudioProjects/yalla-sdk/core/src/commonMain/kotlin/uz/yalla/core/contract/location/LocationProvider.kt` (sole file in `contract/location/`). Confirmed: phase 1 commit `c184119cd` flattened `contract/preferences/* → preferences/*` but skipped `contract/location/`. Move to `core/src/commonMain/kotlin/uz/yalla/core/location/LocationProvider.kt`. Importers to update: `core/preferences/PositionPreferences.kt:14` (`@see` only), `foundation/location/LocationManager.kt:13`, `maps/di/MapDependencies.kt:4`, `maps/provider/common/MapEffects.kt:5`. (~15 min)

### Other organization-only nesting

- None. Top-level packages (`error/`, `geo/`, `location/`, `order/`, `payment/`, `preferences/`, `profile/`, `result/`, `session/`, `settings/`, `util/`) are flat and idiomatic. No further flatten candidates.

### God files (>300 lines or >5 distinct responsibilities)

`wc -l` results — every commonMain file is **under 200 lines**:

```
197  order/Order.kt          (longest)
158  error/DataError.kt
145  result/Either.kt
145  preferences/ConfigPreferences.kt
130  order/OrderStatus.kt
100  payment/PaymentKind.kt
 91  util/NullableDefaults.kt
 90  preferences/SessionPreferences.kt
 …
```

- **No file >300 lines.** Criterion 11's god-file threshold not triggered.
- **`Order.kt` (197 lines)** is the only candidate worth flagging on responsibility-count: `Order`, `Order.Executor`, `Order.Executor.Coords`, `Order.Executor.Vehicle`, `Order.Executor.Vehicle.Color`, `Order.StatusTime`, `Order.Taxi`, `Order.Taxi.Route`, `Order.Taxi.Route.Coords`, plus the `toExecutor()` extension at the bottom — that's 9 nested types in one file. Idiomatic Kotlin pattern (each type only meaningful in `Order` context), so the nesting is structural, not a god-file violation. **Keep as-is.** (~0 min)

---

## 4. Quality / rewrite candidates (criterion 11)

### `core/src/commonMain/kotlin/uz/yalla/core/order/ExtraService.kt`

- Lines 32-44 + 51-52 — **String constants where an enum fits** (criterion 11). `costType: String` with two const sentinels (`COST_TYPE_COST`, `COST_TYPE_PERCENT`) plus a `isPercentCost: Boolean` accessor that does `equals(…, ignoreCase = true)`. Replace with `enum class CostType { Fixed, Percent }` and `costType: CostType`. Suggested target pattern from criterion 11: "String constants where enums fit." Estimated impact: ~15 lines replaced. The companion-object constants and `isPercentCost` accessor go away. ~30 min including tests + downstream sweep. Behavior preserves only if no consumer relies on the `"cost"`/`"percent"` wire format — verify against the data layer's DTO mapping before applying. (Public API shape change → `refactor!:` commit.)

### `core/src/commonMain/kotlin/uz/yalla/core/error/DataError.kt`

- Lines 47-115 — see section 1 above. **Dead-code rewrite candidate**: delete the unused semantic variants. Behavior unaffected since nothing produces them. Public API change. **~70 lines removed.** Net reduction across the codebase if `DefaultDataErrorMapper.kt:32-36` consumer branches also drop. **REWRITE >100 LINES — NEEDS GATE** (counting consumer-side edits). Wait for Islom to decide whether the semantic variants are placeholder for a planned 4xx-mapping pass in `data/`. If they're staged for a future SafeApiCall pass, keep them; otherwise delete.

### `core/src/commonMain/kotlin/uz/yalla/core/order/OrderStatus.kt`

- Lines 4-32 + 53 — `OrderStatusSerializer` is a hand-rolled `KSerializer` that exists because `OrderStatus.Unknown(originalId)` is a polymorphic data class. Acceptable as-is; not idiomatic to "fix" without breaking the unknown-value-graceful-degradation story. **Keep.** (~0 min)

### `core/src/commonMain/kotlin/uz/yalla/core/payment/PaymentKind.kt`

- Lines 27-37 + 53 — same as `OrderStatusSerializer` — hand-rolled because of the documented trade-off (Card-specific fields not in wire). **Keep.** (~0 min)

### `core/src/commonMain/kotlin/uz/yalla/core/location/PointKind.kt`

- Lines 22-32 + 45 — `PointKindSerializer` is similar but the value class has *no* polymorphic data; the fallback-to-`POINT` is the only reason for the custom serializer. Could collapse using `@Serializable` + a `JsonNames` decoder, but kotlinx.serialization-json doesn't fall through to a default for unknown enum values — needs custom serializer regardless. **Keep.** (~0 min)

### `core/src/commonMain/kotlin/uz/yalla/core/util/Mapper.kt`

- Already covered in section 1 (delete — bucket 2-3). Quality-pass also says: typealias-of-stdlib-function-type is bucket 2-5 speculative generalization. (~2 min)

### Cross-cutting: identifier strings

- Across `Address.kt`, `AddressOption.kt`, `Order.kt`, `Executor.kt`, `PaymentCard.kt`, `Client.kt`: every `id: Int` / `id: String` / `cardId: String` / `phone: String` is unwrapped. Criterion 11 lists "String-typed identifiers that should be value classes" as a target. Realistic impact: 7-8 value classes (e.g., `value class OrderId(val raw: Int)`, `value class CardId(val raw: String)`). Each touches every consumer of `Order`/`Executor`/etc. — **REWRITE >100 LINES — NEEDS GATE** (likely 200-400 lines once `data/` mappers and `foundation/` consumers update). **Recommend deferring to phase 2-data wave** rather than landing in core; the value-class boundary is most useful at the DTO↔domain mapping seam. Flag, don't apply. (~0 min in this wave.)

### Architecture violations

- `try { … } catch { … }` in core business logic — none found (`grep -n "try" core/src/commonMain/` returns nothing).
- Mappers as classes — none in core.
- Service classes / `Api` naming — N/A (no networking in core).
- Custom MVI / Arrow / `InMemoryTokenProvider` / `AuthEventBus` — none.
- **`UnauthorizedSessionEvents`** (`core/session/UnauthorizedSessionEvents.kt`) is a global object event bus. Criterion 11 calls out `AuthEventBus` as an anti-pattern (use Ktor `Auth` plugin + `SessionStore` + `SessionExpiredSignal`). **However**, this is the very `SessionExpiredSignal` slot — it's named differently (`UnauthorizedSessionEvents`) but plays the role criterion 11 endorses. The published-via-Ktor-`Auth`-plugin migration would be a **rename**, not a structural change. Decision for Islom: rename to `SessionExpiredSignal` to align with criterion 11 vocabulary, or keep current name and leave as-is. Either way it's not architecturally wrong. (~30 min if renamed, ~0 min otherwise. `refactor!:` if renamed.)

### Untestable shape

- None. Every type in core is a plain data class, sealed type, enum, interface, or pure function. All instantiable in tests with no construction-time side effects.

---

## 5. Promote/demote candidates (criterion 1)

Applied lego test to every public type in `core/src/commonMain`:

### Bricks (stays in core — vast majority)

`GeoPoint`, `Either`, `DataError` (incl. `Network`), `OrderStatus`, `PaymentKind`, `LocaleKind`, `MapKind`, `ThemeKind`, `GenderKind`, `PlaceKind`, `PointKind`, `Address`, `AddressOption`, `Route`, `PointRequest`, `SavedAddress`, `Client`, `Order`, `Executor`, `ExtraService`, `ServiceBrand`, `PaymentCard`, `LocationProvider`, all six `*Preferences` interfaces, `StaticPreferences`, `UnauthorizedSessionEvents`, `formatMoney`, `formatArgs`, `or0`/`orFalse`, `toLocalFormattedDate`/`toLocalFormattedTime`, `Mapper` typealias.

All of these are atomic logic bricks — no product copy, no Ildam-specific business orchestration, no screen-shaped/ViewModel-shaped types.

### Borderline — flag for Islom

- **`uz.yalla.core.order.OrderStatus`** in-fetters alias (line 95: `"in_progress", "in_fetters" -> InProgress`). The `"in_fetters"` legacy-API alias is Ildam-specific deserialization workaround, not a generic concept. **Keep the alias in core** (the alternative is forcing `data/` to do per-status string normalization), but document that the alias is product-specific in MODULE.md notes, not in `OrderStatus`'s KDoc. (~0 min — already there, just noting.)
- **`uz.yalla.core.location.PlaceKind`** has only `Home`/`Work`/`Other`. Generic enough; not a demotion candidate. Keep.

### Demotion candidates

None. `core` matches its description as a brick-only module.

### Notes about hardcoded strings

- No Russian/Uzbek string literals found in `core/src/commonMain` — `grep -rn '[А-Яа-яЁё]\|[ʻ]' --include="*.kt" core/src/commonMain` returns only the SerializationRoundTripTest's `"Amir Temur ko'chasi 1"` test fixture.
- No business rules of the form "if user is type X then Y" embedded in domain types.
- No screen-shaped or ViewModel-shaped types.
- No assemblies — every type composes downward, not into a wired product.

**Verdict for `MIGRATION_LIST.md` (wave 6 will produce it):**

- "## To promote into core" — empty for now (cleanup wave will not invent new bricks).
- "## To demote from core" — empty.
- "## To decide" — `OrderStatus.in_fetters` alias note (decision: keep, document in MODULE.md).

---

## 6. Missing tests (criterion 6)

Inventory by package. Public functions not covered listed inline.

### `core/error/`

**No test directory exists.** Confirmed: `ls core/src/commonTest/kotlin/uz/yalla/core/error` → not present.

- `DataError.Unauthorized` — no equality/serialization test.
- `DataError.Forbidden(reason)` — no test.
- `DataError.Conflict(reason)` — no test.
- `DataError.Validation(fields)` — no test.
- `DataError.NotFound` — no test.
- `DataError.Network.Connection`/`Timeout`/`Server`/`Client`/`ClientWithMessage`/`Serialization`/`Guest`/`Unknown` — no in-core test (consumed by `foundation/.../DefaultDataErrorMapperTest.kt`, but that's not a contract test for the variant itself).

If section 1's bucket-2-4 finding is approved (delete semantic variants), only the Network branch needs tests. ~8 trivial equality + sealed-hierarchy tests. (**~30 min, ~10 tests**)

### `core/preferences/`

**No test directory exists.** Confirmed: `ls core/src/commonTest/kotlin/uz/yalla/core/preferences` → not present.

- `ConfigPreferences` — interface, no contract test (impl-side tests live in `data/`).
- `InterfacePreferences` — interface, no contract test.
- `PositionPreferences` — interface, no contract test.
- `SessionPreferences` — interface, no contract test (and `clearSession` semantics deserve one).
- `StaticPreferences` — interface, no contract test.
- `UserPreferences` — interface, no contract test.

Standard pattern: write a **fake** for each interface in commonTest and a tiny round-trip test that `set→get` flows through. The fakes are valuable to publish for downstream consumers anyway. **~6 fakes + ~6 round-trip tests, ~60 min.**

### `core/contract/location/`

**No test directory exists.** Confirmed.

- `LocationProvider` — interface, no contract test.

After wave 4's flatten, this lives at `core/location/`. Same pattern: hand-written fake + 1-2 round-trip tests. (**~20 min**)

### `core/result/Either.kt` — partial coverage

`EitherTest.kt` and `EitherExtensionsTest.kt` between them cover:

| method | Success path | Failure path |
| ------ | ------------ | ------------ |
| `onSuccess` | yes (EitherTest) | yes (EitherTest) |
| `onFailure` | yes (EitherTest) | yes (EitherTest) |
| `mapSuccess` | yes (both) | yes (both) |
| `mapFailure` | yes (both) | yes (both) |
| `getOrNull` | yes (Extensions) | yes (Extensions) |
| `getOrThrow` | yes (Extensions) | yes (Extensions) |
| `fold` | yes (Extensions) | yes (Extensions) |

**No `flatMapSuccess` exists** in source — checked. So no gap there. **Either is fully covered.** (~0 min)

### `core/util/`

- `DateTimeFormattingTest.kt` covers `toLocalFormattedDate`, `toLocalFormattedTime`, null/non-positive paths, seconds/milliseconds detection. Complete.
- `NormalizationTest.kt` covers both `normalizedId` and `normalizedLocaleCode`. Complete.
- `NullableDefaultsTest.kt` covers `or0` for Int/Long/Float/Double, `orFalse`, `formatMoney` for `Long` and `Long?`. Complete.
- `StringFormatterTest.kt` covers `formatArgs` happy path + missing-arg + repeated placeholder. Complete.
- **`Mapper`** typealias has no test — deletion candidate (section 1), so no gap.

### `core/order/Order.kt`

- `Order.toExecutor()` — covered by `OrderExecutorMappingTest.kt`. Complete.
- `Order` data class itself + nested types — only structural; round-trip not relevant (`Order` lacks `@Serializable`). No test gap **except** an equality/copy regression test would be cheap. Optional. (~10 min if added.)

### `core/order/OrderStatus.kt`

- `from()` happy paths, `in_fetters` alias, normalization, unknown, null — covered by `OrderStatusTest.kt`.
- `active`/`ongoing`/`nonInteractive` set membership — covered.
- Serialization round-trip via `SerializationRoundTripTest.kt`. Complete.

### `core/order/ExtraService.kt`

- `isPercentCost` covered for both branches by `ExtraServiceTest.kt`.
- Round-trip covered. Complete.

### `core/order/Executor.kt`, `ServiceBrand.kt`

- `Executor` round-trip in `SerializationRoundTripTest`. No constructor invariant to test.
- `ServiceBrand` round-trip in `SerializationRoundTripTest`. No constructor invariant.
- **If `ServiceBrand` is deleted as dead (section 1, bucket 2-4)**, the round-trip test for it goes too.

### `core/payment/PaymentCard.kt`, `PaymentKind.kt`

- `PaymentCard.toPaymentType()` covered by `PaymentCardTest.kt`.
- `PaymentKind.from()` covered for cash, card, blank cardId, null cardId, unknown by `PaymentKindTest.kt`.
- Round-trip covered. Complete.
- If `PaymentCard` is deleted as dead (section 1), the test goes too.

### `core/profile/`, `core/settings/`

- `GenderKindTest`, `LocaleKindTest`, `MapKindTest`, `ThemeKindTest` — match-ID, normalize-case, fall-back-on-unknown-or-null all covered. Complete.
- `Client` — round-trip only; data class with no logic. No gap.

### `core/session/UnauthorizedSessionEvents.kt`

- `UnauthorizedSessionEventsTest.kt` covers publish→collect, conflate-rapid-fire, drainPendingEventIfExists. Complete.

### `core/geo/GeoPoint.kt`

- `GeoPointTest.kt` covers happy construction, `Zero` constant, lat/lng range invariants, distance symmetric, distance reasonable. Complete.
- Distance for **antipodal points** is mentioned in the KDoc as accurate but not asserted. Optional addition. (~5 min, low value.)

### `core/location/PlaceKind.kt`

- `PlaceKindTest.kt` covers home, normalization, unknown/null. Complete.

### `core/location/PointKind.kt`

- Round-trip in `SerializationRoundTripTest` covers all entries + unknown→POINT + null→POINT. Complete.

### `core/location/Route.kt`, `Address.kt`, `AddressOption.kt`, `PointRequest.kt`, `SavedAddress.kt`

- All round-trip-tested. No business logic to gap-check.

### Summary by package

| Package | Effort | Gap |
| ------- | ------ | --- |
| `error/` | ~30 min, ~10 tests | DataErrorTest (after section-1 dead-code decision) |
| `preferences/` | ~60 min, ~6 fakes + ~6 round-trip tests | All 6 interfaces lack contract tests |
| `contract/location/` (post-flatten: `location/`) | ~20 min, ~1 fake + ~2 tests | LocationProvider |
| Everything else | 0 min | Already complete |

**Total wave-8 effort estimate: ~110 min, ~20 new tests.** Brings core test count from current baseline (≈80, will confirm in pre-work step 3) to ~100.

---

## 7. MODULE.md staleness (criterion 5)

Current `core/MODULE.md` (54 lines) uses the old `# Module / # Package …` format. Phase-1 form (per `bom/MODULE.md`, `resources/MODULE.md`) is:

```
# Module <name>
> One-line tagline.

## What this is
## What this is NOT
## Usage
## Notes
## Depends on
```

### Sections to add

- **`> One-line tagline.`** — currently missing. Suggested: `> Logic atoms: errors, results, domain enums, preferences contracts.`
- **`## What this is`** — replace the multiple `# Package` blurbs with a tight 3-5 bullet list of what's in core.
- **`## What this is NOT`** — explicitly: no networking (`data`), no UI (`primitives`/`composites`), no platform-specific (`platform`), no DTO mappers.
- **`## Usage`** — 4-6 lines showing `Either<DataError, T>` + a `*Preferences` interface (the two main consumer surfaces).
- **`## Notes`** — fold in the `DataError` naming-history caveat from the current KDoc on lines 28-31, the `OrderStatus.in_fetters` alias note (section 5), and the "ADR-014 narrowed locales" note from `LocaleKind.kt:15-16`.
- **`## Depends on`** — the block from section 2.

### Sections to remove

- **`# Package uz.yalla.core.error`** through **`# Package uz.yalla.core.util`** (lines 7-53) — all 11 per-package blurbs. Per-package KDoc lives on the source, not in MODULE.md. Drop entirely.

### Sections to rewrite

- **Lines 1-5** — opening paragraph. Already says "Core types, error hierarchy, and contracts for the yalla-sdk." Replace with the phase-1 tagline + `What this is` form.

### Cross-check from prompt

- **`uz.yalla.core.contract.preferences`** mention (line 43 of current MODULE.md) — **stale**. The package was flattened in commit `c184119cd`. Remove with the rest of the per-package blurbs.
- **`uz.yalla.core.contract.location`** mention (line 47-49) — **stale once wave 4 flattens it.** Remove with the rest.

Total wave-10 effort: full rewrite of MODULE.md from scratch on phase-1 form. ~20 min.

---

## 8. Reviewer notes

### Pushback on specific findings

- **Section 1, bucket 2-4 on DataError semantic variants (`Unauthorized`, `Forbidden`, `Conflict`, `Validation`, `NotFound`)** — I flagged these as dead, but I want Islom to verify. The KDoc explicitly says these exist for feature code to pattern-match against, and `DefaultDataErrorMapper.kt:32-36` exhaustively branches on them. Two readings:
  1. **Truly dead**: nothing in `data/` produces them; SafeApiCall maps everything to `Network.*`. Drop them and simplify `DefaultDataErrorMapper`.
  2. **Staged for a future SafeApiCall pass** that maps 401→Unauthorized, 403→Forbidden, 409→Conflict, 422→Validation by inspecting the error body. This was teed up but never finished, and the variants exist as a placeholder.
   I'd default to (1) — delete on sight per criterion 2-4, the test bar shows what gets created, and recreating them with a producer is cheap when actually needed. But it's a public-API change → wave-2 commit gets `refactor!:` and the gate.

- **Section 1, bucket 2-4 on `ServiceBrand`, `PaymentCard`, `Client`** — I flagged them as zero-importer-outside-core, but core is a published library. Future YallaClient or a third-party consumer may import them. I'm flagging, not auto-deleting. Defer to Islom's call.

- **Section 4 on identifier value classes** — the suggestion to convert `id: Int`/`cardId: String` etc. into value classes is a textbook criterion-11 finding, but landing it in core alone would force a parallel value-class boundary in `data/` mappers. **Recommend deferring to phase-2 `data` cleanup**, where the DTO seam is the right place to apply the boundary. Don't apply in this phase.

- **Section 4 on `UnauthorizedSessionEvents` rename** — global-object event buses are the criterion-11 anti-pattern, but this *is* the `SessionExpiredSignal` slot (Ktor Auth plugin pattern), just under a different name. Renaming is cosmetic; structure is correct. I'd leave the name unless Islom wants a vocabulary alignment.

### Cross-cutting patterns

- **The `from(id: String?)` enum factory** repeats across `OrderStatus`, `PaymentKind`, `LocaleKind`, `MapKind`, `ThemeKind`, `GenderKind`, `PlaceKind`, `PointKind`. Eight near-identical factories: trim, lowercase, find-by-id, fall back to a default. The shared `normalizedId()` helper (`core/util/Normalization.kt`) does the trim+lowercase part. **Could extract a shared `<E : Enum<E>> fromIdOrDefault(id: String?, default: E, idOf: (E) -> String)` helper**, but each factory has slightly different semantics (`OrderStatus.from` returns `Unknown(originalId)` and uses `lowercase()` only with `.trim()`; `PaymentKind.from` is more complex with the cardId branch). **Don't extract.** The duplication is shallow and pulling it out would obscure per-enum semantics. Flag as "consistent pattern, intentional repetition" rather than "fix".

- **Per-property KDoc paraphrase pattern** affects ~80% of the data classes (`Address`, `AddressOption`, `Order` and every nested class, `Executor`, `ServiceBrand`, `PaymentCard`, `Client`, `SavedAddress`, `Route`, `PointRequest`, `Coords`, `Vehicle`, `Color`). Total `@property` lines that paraphrase the field: ~120 lines across the module. **Single sweep in wave 2 covers the bulk** — sed-able, but careful (some `@property` blocks include real info, like `Order.Taxi.startPrice`'s "in smallest currency unit" qualifier — those need to stay, just the redundant ones go). Plan ~30 min for the sweep.

- **`@since 0.0.X` ceremony tags** appear on ~26 of 35 source files. They're not paraphrase but they're noise — none of the consumers track them, and SDK is alpha (criterion 3). Drop in the wave-2 KDoc sweep. Trivial sed/awk.

### Concerns with the criteria as applied to core

- **Criterion 6's state-machine bar** doesn't apply — core has no Orbit `ContainerHost`. The "every intent → state transition tested" line is a no-op for core. Mention this in wave-9 verification but don't try to invent state machines.

- **Criterion 4's "no SDK-internal deps"** holds — `core` imports nothing from other SDK modules. Confirmed by `grep -rn "import uz.yalla\." core/src/commonMain | grep -v "uz.yalla.core"` returning nothing. This is the cleanest leaf in the brick stack; the audit doesn't need to discover or fix any cycle.

- **Criterion 11's "rewrite eligible" bar** is generous for core. Most "rewrite candidates" here are 5-15 line edits (rename, drop dead, extract enum). Only the DataError dead-code purge crosses 100 lines, and that's a deletion not a rewrite. Effectively, **core has zero `>100 line rewrite needs gate`** items if Islom doesn't want value-class identifiers and doesn't want the `UnauthorizedSessionEvents` rename. The DataError dead-code is a deletion that crosses the line; framed as such, that's the only gate this phase.

---

## Summary stats

- **Section 1 findings:** 35 file-level findings across 26 source files. Mix of ~120 lines of paraphrase KDoc, ~26 `@since` tags, ~5 dead types/dead variants, 1 fully-dead typealias.
- **Section 2 findings:** 1 unused dep (`kermit`), 1 wrong-scope dep (`kotlinx.datetime` should be `implementation`).
- **Section 3 findings:** 1 orphan-flatten target (`contract/location/`). 0 god files.
- **Section 4 findings:** 4 quality candidates (3 small, 1 cross-cutting deferred to data, 1 deletion).
- **Section 5 findings:** 0 promotion, 0 demotion, 1 borderline note (`OrderStatus.in_fetters` documentation only).
- **Section 6 findings:** 3 packages with 0 tests (`error/`, `preferences/`, `contract/location/`). ~20 missing tests, ~110 min effort.
- **Section 7 findings:** 1 full MODULE.md rewrite + 11 stale package blurbs to drop.
- **Longest single rewrite candidate:** `DataError.kt` semantic-variant deletion at **~70 lines deleted from `core/error/DataError.kt:47-115`**, plus ~10 lines of consumer-side cleanup in `foundation/.../DefaultDataErrorMapper.kt:32-36` and its test. **Crosses the 100-line gate when consumer + tests are counted**. **NEEDS GATE.**
- **Blocking issues:** none. Audit is fully derivable from the source; no questions block wave-2.
