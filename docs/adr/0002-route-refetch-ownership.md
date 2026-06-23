# 2. Route refetch ownership: the client refetches, the SDK only emits off-route

- Status: accepted
- Date: 2026-06-23
- Deciders: yalla-sdk maintainers + canon master panel (map-route-panel-2026-06-23) + Islom
- Relates to: [0003 off-route state machine](0003-off-route-state-machine.md)

## Context

The shared route-following engine ([`DriverMotionModel`] + [`RouteProgressGeometry`]) detects when
a driver has left the planned route (a fix that projects beyond the snap threshold). Once that
happens, *someone* must fetch a fresh route from the routing backend, because the old polyline is
now wrong and the car is chord-interpolating toward raw GPS in the meantime.

There were two candidate owners:

1. **The SDK** — port the iOS `NavigationRouteProvider` into the maps module so the SDK refetches
   the route itself when it goes off-route.
2. **The client** (`YallaClient`'s `OrderSheetViewModel`) — the SDK only *emits* an off-route
   signal; the client, which already owns all networking for the order screen, performs the refetch
   and hands the new polyline back via `setRoute`.

The canon panel explicitly **flagged option 1 as a policy scatter**: pushing a network capability
into the maps SDK duplicates request/retry/auth/cancellation machinery the client already owns, and
splits "who talks to the routing backend" across two layers. The panel's standing principle (deep
modules, one owner per concern; "less code, delete first") points at option 2.

## Decision

**The client owns refetch. The SDK only emits a binary off-route signal.**

- `DriverMotionModel` surfaces off-route as state ([`RouteState.OFF_ROUTE`]) and latches a single
  edge signal per ON_ROUTE→OFF_ROUTE crossing, consumed via `consumeOffRouteSignal()` (gated by a
  refetch cooldown so GPS noise at the boundary cannot storm the network).
- `YallaClient`'s `OrderSheetViewModel` consumes that signal and performs the refetch using the
  networking it already owns, then calls `setRoute(newPolyline)` to re-seed the engine.
- The SDK never imports or initiates a network call for routing. No `NavigationRouteProvider` is
  ported.

## Consequences

**Positive**

- One owner for routing-backend traffic (the client), so retry/auth/cancellation/cooldown policy is
  not duplicated or split across the SDK boundary. The SDK stays a pure motion+geometry module.
- The cooldown + edge-latch live in the SDK (where the crossing is detected) but the *refetch policy*
  (how, with what backoff, against which endpoint) stays in the client where the rest of it lives.

**Negative / the tradeoff the panel flagged**

- The off-route → refetch concern is now **split across two repos**: the SDK detects and signals,
  the client acts. A reader chasing "why didn't we refetch after going off-route" must look in both
  `DriverMotionModel` (was the signal latched / cooled down?) and `OrderSheetViewModel` (was the
  signal consumed and acted on?). This is the scatter cost we accept in exchange for not duplicating
  the network stack inside the SDK. It is mitigated by: (a) a single, documented signal API
  (`consumeOffRouteSignal`); (b) the cooldown living with the detector so the client cannot
  accidentally storm; (c) this ADR as the breadcrumb between the two halves.

**Enforcement**

- The SDK has no routing-network dependency; a future Konsist rule MAY forbid `io.ktor.`/network
  imports from `uz.yalla.maps.motion..` to keep this honest (the core already forbids them via
  `CorePurityKonsistTest`).
