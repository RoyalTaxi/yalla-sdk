# MIGRATION_LIST.md

Working document for SDK ↔ YallaClient relocations surfaced during the
`cleanup/phase-2-3-4` branch. Per `CLEANUP_CRITERIA.md` criterion 1
(lego test) and criterion 8 (single alpha bump at end of phase 4).

This file is consumed by YallaClient migration once the cleanup branch
publishes its single alpha tag. Deleted at the end of phase 4 alongside
`CLEANUP_CRITERIA.md`, `CORE_AUDIT.md`, `PHASE_2_*_PLAN.md`.

---

## To promote into the SDK (YallaClient → SDK)

*From phase 2 `core`:* none. The audit found no bricks living in
YallaClient that should move into core.

## To demote from the SDK (SDK → YallaClient)

*From phase 2 `core`:* none. Every public type in core passes the lego
test as a brick — no hardcoded product copy, no Ildam-specific business
orchestration, no screen-shaped or ViewModel-shaped types.

## To decide

*From phase 2 `core`:*
- **`OrderStatus.in_fetters` alias** (`core/src/.../order/OrderStatus.kt`).
  The legacy-API alias is a product-specific deserialization workaround
  (Ildam's old API used `"in_fetters"` for what is now `"in_progress"`).
  Keeping it in core is justified to avoid forcing every consumer to do
  the per-status normalization, but it's worth flagging as
  product-specific in `core/MODULE.md` Notes (handled in wave 10) so future
  maintainers know it isn't a general taxi-domain concept.

## Breaking changes shipped to SDK alpha

Tracked here for the YallaClient migration. Each entry is a `refactor!:`
commit on `cleanup/phase-2-3-4`.

*From phase 2 `core`:*
- `9a87a9652 refactor(core): drop unused Mapper typealias`
  Was: `typealias Mapper<T, R> = (T) -> R` in `core/util/`. Action: any
  YallaClient code referencing `uz.yalla.core.util.Mapper` substitutes
  the stdlib type `(T) -> R` directly. (Zero importers verified.)
- `482eb16df refactor!(core): drop unused DataError semantic variants`
  Removed: `DataError.Unauthorized`, `Forbidden(reason)`, `Conflict(reason)`,
  `Validation(fields)`, `NotFound`. Network branch unchanged. Action:
  YallaClient `when` on `DataError` drops the dead branches; recreate the
  variants when a real producer is added.
- `35e309c14 refactor!(core): flatten contract/location/* to location/*`
  Was: `uz.yalla.core.contract.location.LocationProvider`. Now:
  `uz.yalla.core.location.LocationProvider`. Action: import-path rename
  in YallaClient; behavior unchanged.
- `d5a60ec21 refactor!(core): convert ExtraService.costType String to enum`
  Was: `costType: String` + `COST_TYPE_COST`/`COST_TYPE_PERCENT` constants
  + `isPercentCost` accessor. Now: `costType: ExtraService.CostType`
  with `Fixed` and `Percent` variants. Wire format unchanged. Action:
  YallaClient call sites switch from string comparison / `isPercentCost`
  to `when (service.costType)` over the typed enum. Case-insensitive
  deserialization (`PERCENT` etc.) no longer accepted — server contract
  is strict lowercase.

---

---

## Phase 2 — `data` additions (and the `core` value-class rollout it pulled in)

### Promotions / demotions surfaced

*From phase 2 `data`:* none. Per `DATA_AUDIT.md` §5: zero promotions, zero unambiguous demotions. Three borderlines (`NetworkConfig.deviceType`/`deviceMode` defaults, `DEFAULT_GUEST_ALLOWED_SEGMENTS`) explicitly KEPT — speculative-but-cheap defaults for a future Driver/Operator app that may legitimately parameterize them.

### Breaking changes shipped

*From phase 2 `data`:*

- `3e165bd1a refactor(data): tighten api/implementation split, drop unused deps`
  Demoted three `api()` declarations to `implementation()`:
  `ktor-client-content-negotiation`, `ktor-serialization-kotlinx-json`,
  `ktor-client-logging`. Dropped unused androidMain `koin-android`. Action:
  YallaClient must declare any direct usage of these libs explicitly in its
  own `build.gradle.kts` — it can no longer rely on data's transitive `api()`
  resolution. (Most likely YallaClient already declares these for its own
  HttpClient instances; verify during migration.)

- `de64475c0 refactor!(data): migrate HttpClientFactory to Ktor Auth plugin`
  `createHttpClient`'s 401 handling moved from a hand-rolled `HttpCallValidator`
  + `extractBearerToken` parser to Ktor's `Auth { bearer { … } }` plugin.
  End-state behavior preserved (401 clears the session and publishes
  `UnauthorizedSessionEvents`), but with one subtle delta: the bearer token
  is no longer re-read from `sessionPrefs.accessToken` on every request — it
  is loaded lazily by Auth's `loadTokens` and cached internally until cleared
  via `refreshTokens`. Logout now requires one extra request that uses the
  stale token, gets 401, triggers a no-op refresh, then subsequent requests
  carry no token. Action: YallaClient typically does not rely on the
  per-request token-fresh-read; if it does (e.g., custom token-rotation
  flows that don't 401), wire `clearToken()` on the `BearerAuthProvider` at
  rotation points.
  Test seam added: `createHttpClient(... engine: HttpClientEngine? = null)` —
  YallaClient's signature compatible (defaulted parameter).

- `7bd4125a6 refactor!(core): introduce typed identifiers (OrderId, CardId, …)`
  Picks up the deferred core-G3. Eight value classes added in
  `core/identity/Ids.kt`:
    - `OrderId(val raw: Int)` — `Order.id`
    - `ExecutorId(val raw: Int)` — `Order.Executor.id`, `Executor.id`
    - `ExtraServiceId(val raw: Int)` — `ExtraService.id`
    - `ServiceBrandId(val raw: Int)` — `ServiceBrand.id`
    - `AddressId(val raw: Int)` — `Address.id` (NULLABLE on the wire)
    - `AddressOptionId(val raw: Int)` — `AddressOption.id`
    - `CardId(val raw: String)` — `PaymentCard.cardId`,
      `PaymentKind.Card.cardId`, `PaymentKind.from(... cardId: CardId? ...)`
  Wire format byte-for-byte unchanged (verified by
  `SerializationRoundTripTest`'s `encoded.contains(...)` assertions).
  Action: YallaClient call sites that touch these IDs must wrap on
  ingress (`OrderId(rawIntFromSomewhere)`) or unwrap on egress
  (`order.id.raw`). The compile errors are systematic and mechanical;
  estimate ~80-150 lines of touch-up across YallaClient's
  `data/ride/`, `data/user/`, `data/payment/`, `data/geo/`, plus any UI
  formatter that displays an ID directly. **The `composites` SDK module
  already has its `cardId.raw.length` unwrap at the issuer-detection
  branch** — the YallaClient migration follows the same pattern.

### Bug fixes shipped (non-breaking)

- `3dccad345 fix(data): map JsonConvertException + HttpRequestTimeoutException`
  - `safeApiCall` now catches Ktor 3.x's `JsonConvertException` (via the
    `ContentConvertException` parent) and maps to
    `DataError.Network.Serialization`. Previously escaped unmapped.
  - `safeApiCall` now catches `HttpRequestTimeoutException` BEFORE the
    `IOException` branch and maps to `DataError.Network.Timeout`.
    Previously routed to `Connection`. Behavioral fix, no migration needed.

---

---

## Phase 3 — `design` additions

### Promotions / demotions surfaced

*From phase 3 `design`:* none. Per `DESIGN_AUDIT.md` §5: zero promotions, zero demotions, one borderline (`ThemedImage.OrderHistory`/`OrderSearch`/`TariffCard` enum entry naming — kept; visual brand is the product).

### Pending consumers (audit decision G9)

- **`uz.yalla.design.motion.*` — `MotionScheme` + `LocalMotionScheme` + `standardMotionScheme()`.**
  Shipped in `0.0.17-alpha01` (commit `a9daf28a8`) as Chunk 0.C of the YallaClient refactor plan (`YallaClient/docs/superpowers/plans/2026-04-23-yalla-client-refactor.md`). Currently has zero callers anywhere in SDK or YallaClient. Decision G9: **keep** the surface. Action for the YallaClient migration: either consume `System.motion.duration.*` / `easing.*` / `spring.*` / `stagger.*` to replace ad-hoc `tween(durationMillis = ...)` calls, or surface a follow-up to delete and re-introduce when the haptic + motion pair actually ships together.

### Breaking changes shipped

*From phase 3 `design`:*

- `4e9868f6c refactor!(design): remove unused FontScheme.Body.numeric extension`
  Removed: `FontScheme.Body.numeric: TextStyle` extension property and the
  `internal const val FONT_FEATURE_TABULAR_NUMERALS = "tnum"`. Zero callers
  anywhere; KDoc claimed an animated-price use case but no consumer ever
  shipped against it. Action: any future numeric-display consumer can
  rebuild the extension in one line —
  `style.copy(fontFeatureSettings = "tnum")`.

- `6d0a271ac refactor!(design): tighten api/implementation split, drop unused deps`
  - Dropped unused androidMain deps: `compose.uiTooling`, `androidx.core.ktx`.
    Verified zero references in `design/src/androidMain`.
  - Promoted `compose.runtime` and `compose.ui` from `implementation()` to
    `api()`. Both are exposed in design's public types
    (`@Composable`, `ProvidableCompositionLocal`, `Color`, `TextStyle`, `Dp`).
  Action for YallaClient: typically no-op — consumers already declare
  these via `KmpComposeConventionPlugin`. Verify the pom.xml change doesn't
  surface a transitive resolution issue at consumer build time.

- `18e0dc4c5 refactor!(design): demote raw color tokens to internal`
  56 raw color constants in `design/color/Color.kt` (`LightTextBase`,
  `DarkBackgroundBase`, accent + gradient tokens, etc.) are no longer part
  of the public API. Verified zero external consumers SDK-wide. Action for
  YallaClient: any direct import of `uz.yalla.design.color.Light*` /
  `Dark*` / accent / gradient symbols must switch to `System.color.text.*`
  / `System.color.background.*` / `System.color.accent.*` /
  `System.color.gradient.*` — the canonical access path documented in
  `design/MODULE.md`.

---

## Phase status

- Phase 2 `core` — done. Plan: [PHASE_2_CORE_PLAN.md](PHASE_2_CORE_PLAN.md). Audit: [CORE_AUDIT.md](CORE_AUDIT.md).
- Phase 2 `data` — done. Plan: [PHASE_2_DATA_PLAN.md](PHASE_2_DATA_PLAN.md). Audit: [DATA_AUDIT.md](DATA_AUDIT.md).
- Phase 3 `design` — done. Plan: [PHASE_3_DESIGN_PLAN.md](PHASE_3_DESIGN_PLAN.md). Audit: [DESIGN_AUDIT.md](DESIGN_AUDIT.md).
- Phase 3 `foundation`, `primitives`, `composites` — TODO.
- Phase 4 `firebase`, `maps`, `media`, `platform` — TODO.
