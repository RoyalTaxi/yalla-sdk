# 2. Route refetch ownership: the client owns off-route detection + refetch

- Status: accepted (amended 2026-06-23)
- Date: 2026-06-23
- Deciders: yalla-sdk maintainers + canon master panel (map-route-panel-2026-06-23) + Islom
- Relates to: [0003 off-route state machine](0003-off-route-state-machine.md)

## Amendment (2026-06-23)

The original decision (below) had the SDK **emit** a one-shot off-route signal
(`consumeOffRouteSignal()` + a `refetchCooldownMillis`-gated suppression) that the client would
consume and act on with a refetch.

That seam was never used. The client (`OrderSheetViewModel.OffRouteTracker` in YallaClient's
`:feature:taxi:logic`) **detects off-route itself** and never consumed the SDK signal — confirmed by
grep: no `consumeOffRouteSignal` reference anywhere in YallaClient. `OffRouteTracker` re-implements
the binary state machine client-side (same windowed `RouteProgressGeometry.projectForward`, same
enter/exit hysteresis, same cooldown) using thresholds **mirrored** from `RouteFollowingConfig`
(`OFF_ROUTE_ENTER_METERS = 30`, `OFF_ROUTE_EXIT_METERS = 15`, `REFETCH_COOLDOWN_MILLIS = 15_000`).
This duplication is deliberate: `:feature:taxi:logic` does **not** depend on `:maps`, so the gate
that lives in the client cannot read the SDK's config type directly.

So the SDK's emitted signal + its cooldown were **dead weight** — a second copy of the state machine
that nothing read — and were removed per "one owner; less code, delete first":

- Deleted from the SDK: `consumeOffRouteSignal()`, the pending-signal latch, the
  `refetchCooldownMillis`-gated suppression, and the unused `routeSnapThreshold` knob in
  `RouteFollowingConfig` (off-route gating is on `offRouteEnterMeters`/`offRouteExitMeters`).
- Kept in the SDK: the internal binary `routeState` (`ON_ROUTE`/`OFF_ROUTE`) with enter/exit
  hysteresis — the model still needs it to draw the connector honesty line and to choose
  follow-vs-chord. It is computed internally and is **not** surfaced as a client-facing signal.

This deletion is **behavior-neutral**: the client's `OffRouteTracker` was always the live owner of
detection + refetch, so removing the unconsumed SDK signal changes no runtime behavior.

**Net decision now: the client owns off-route detection AND refetch (live, flag-gated). The SDK does
pure route geometry + the connector honesty line; it does not signal the client about routing.**

## Context

The shared route-following engine ([`DriverMotionModel`] + [`RouteProgressGeometry`]) can detect when
a driver has left the planned route (a fix that projects beyond the off-route threshold). Once that
happens, *someone* must fetch a fresh route from the routing backend, because the old polyline is now
wrong and the car is chord-interpolating toward raw GPS in the meantime.

There were two candidate owners:

1. **The SDK** — port the iOS `NavigationRouteProvider` into the maps module so the SDK refetches
   the route itself when it goes off-route.
2. **The client** (`YallaClient`'s order screen) — the client, which already owns all networking for
   the order screen, both *detects* off-route (from route geometry, via the shared `projectForward`
   primitive) and performs the refetch, then hands the new polyline back via `setRoute`.

The canon panel explicitly **flagged option 1 as a policy scatter**: pushing a network capability
into the maps SDK duplicates request/retry/auth/cancellation machinery the client already owns, and
splits "who talks to the routing backend" across two layers. The panel's standing principle (deep
modules, one owner per concern; "less code, delete first") points at option 2.

## Decision

**The client owns off-route detection AND refetch (live, flag-gated). The SDK does pure route
geometry + the connector honesty line; it does not emit a refetch signal.**

- `OrderSheetViewModel.OffRouteTracker` (in `YallaClient`'s `:feature:taxi:logic`) detects off-route
  from the route geometry — windowed `RouteProgressGeometry.projectForward`, enter/exit hysteresis,
  edge latch, cooldown — and gates the refetch, then calls `setRoute(newPolyline)` to re-seed the
  engine. Its thresholds are mirrored constants of `RouteFollowingConfig` because `:logic` does not
  depend on `:maps`.
- `DriverMotionModel` computes a binary `routeState` internally (with enter/exit hysteresis) **only**
  for its own rendering decisions — drawing the connector and choosing follow-vs-chord. It is not a
  client-facing signal.
- The SDK never imports or initiates a network call for routing. No `NavigationRouteProvider` is
  ported.

## Consequences

**Positive**

- A single live owner for the entire off-route → refetch concern (the client): detection, retry,
  auth, cancellation, and the cooldown all live in one layer. The SDK stays a pure motion+geometry
  module.
- No dead signal API straddling the repo boundary to keep in sync (the original design's edge-latch
  + cooldown were exactly that, and were unused — so they are now deleted).

**Negative / the tradeoff**

- The off-route thresholds exist in **two places**: `RouteFollowingConfig` (SDK, for rendering) and
  the mirrored constants in `OffRouteTracker` (client, for the refetch gate). They must stay in sync
  by hand. This is the accepted cost of the `:logic` ↛ `:maps` boundary — the client cannot read the
  SDK config type. Mitigated by: both deriving from the *same* `projectForward` geometry, this ADR as
  the breadcrumb, and the small, named constant block in `OffRouteTracker` pointing back here.

**Enforcement**

- The SDK has no routing-network dependency; a future Konsist rule MAY forbid `io.ktor.`/network
  imports from `uz.yalla.maps.motion..` to keep this honest (the core already forbids them via
  `CorePurityKonsistTest`).
