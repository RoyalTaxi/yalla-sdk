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

## Phase status

- Phase 2 `core` — items above. Plan: [PHASE_2_CORE_PLAN.md](PHASE_2_CORE_PLAN.md). Audit: [CORE_AUDIT.md](CORE_AUDIT.md).
- Phase 2 `data` — TODO (separate plan after core lands).
- Phase 3 `design`, `foundation`, `primitives`, `composites` — TODO.
- Phase 4 `firebase`, `maps`, `media`, `platform` — TODO.
