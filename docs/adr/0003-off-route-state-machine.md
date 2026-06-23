# 3. Off-route state machine: binary ON_ROUTE / OFF_ROUTE with a connector line

- Status: accepted (amended 2026-06-23)
- Date: 2026-06-23
- Deciders: yalla-sdk maintainers + canon master panel (map-route-panel-2026-06-23) + Islom
- Relates to: [0002 route refetch ownership](0002-route-refetch-ownership.md)

## Amendment (2026-06-23)

The **edge-latched signal + refetch cooldown** described below were removed: nothing consumed the
SDK signal — the client (`OrderSheetViewModel.OffRouteTracker`) detects off-route itself and owns the
refetch (see [ADR 0002](0002-route-refetch-ownership.md)). What remains is the **binary state with
hysteresis and the connector line** — which the model still needs internally to draw the connector
and choose follow-vs-chord. Specifically, relative to the
text below: `consumeOffRouteSignal()`, the per-edge latch, `refetchCooldownMillis`, and the unused
`routeSnapThreshold` config knob no longer exist; the tests `offRouteSignalIsEdgeLatchedNotPerFrame`
and `offRouteSignalRespectsRefetchCooldown` were deleted. The state machine itself (enter at 30 m /
exit at 15 m hysteresis, chord fallback, connector) is unchanged. Read the rest of this ADR as the
state-machine design with the "signal/cooldown" side effects struck out.

## Context

The ported iOS engine force-snapped every GPS fix onto the route polyline. The canon panel called
this a **silent lie**: a driver who has genuinely left the route is still drawn glued to it, so the
UI looks correct while the data is wrong. The fix is to make "off the route" a first-class, visible
state instead of hiding it.

There was a live design fork the panel left for Islom:

- **Binary** ON_ROUTE / OFF_ROUTE + a connector line from the raw GPS to the snapped point
  (reference-faithful, less code; favoured by Ousterhout / Architecture / iOS voices).
- **Graded** states (e.g. ON / DRIFTING / OFF) carrying a continuous confidence (Moskała).

A single hard threshold also chatters: a fix hovering at exactly the boundary flips the state every
frame, which — because the state drives refetch (ADR 0002) — would storm the network.

## Decision

**Binary state, with hysteresis and a connector line.** No graded states. *(The edge-latched signal
in the original decision was removed — see the amendment above.)*

State: [`RouteState`] = `ON_ROUTE` | `OFF_ROUTE`. (`AWAITING_ROUTE` from the panel's sketch is not
modelled as a third state — "no route set" is simply route mode being inactive, i.e. chord mode —
keeping the machine genuinely binary.)

Transitions (in `DriverMotionModel.push`, per GPS fix, cross-track from the forward-windowed
projection):

| From       | Condition                                            | To         | Side effect                                  |
|------------|------------------------------------------------------|------------|----------------------------------------------|
| ON_ROUTE   | crossTrack ≤ `offRouteEnterMeters` (default 30)      | ON_ROUTE   | follow route; connector if raw off the line  |
| ON_ROUTE   | crossTrack > `offRouteEnterMeters`                   | OFF_ROUTE  | chord fallback (car never freezes)           |
| OFF_ROUTE  | crossTrack > `offRouteExitMeters` (default 15)       | OFF_ROUTE  | stay off; chord fallback (car never freezes) |
| OFF_ROUTE  | crossTrack ≤ `offRouteExitMeters`                    | ON_ROUTE   | resume following the route                   |

- **Hysteresis.** Enter at 30 m, exit at 15 m; the band between is sticky, so a boundary-hovering
  fix does not flicker.
- **Connector line.** While ON_ROUTE but the raw fix sits off the line, `connector(atMillis)`
  returns the raw→snapped [`RouteConnector`]; the renderer draws it verbatim so the snap is honest.
  Hidden once the raw fix is within `connectorHideThreshold` of the line.
- **Thresholds are named commonMain config** ([`RouteFollowingConfig`]): `offRouteEnterMeters`,
  `offRouteExitMeters`, `routeArrivalThreshold`, `connectorHideThreshold` replace the one overloaded
  `30 m` literal, so a distance means the same thing on Android and iOS.

## Feature flag + chord default

The whole route-following path is gated behind `DriverMotionModel(routeFollowingEnabled = …)`,
**default `false`**. With the flag off, `setRoute` is a no-op and the model is the original chord
interpolator — chord stays the production default. Flipping the flag on-device, one platform at a
time, is done by Islom interactively (not in this workflow):

- **Android:** construct the driver `DriverMotionModel` with `routeFollowingEnabled = true` at the
  marker-motion call site, set the route via `setRoute(polyline)`, and draw `remainingRoute` /
  `connector` from the model.
- **iOS (Google, then MapLibre):** same flag through the K/N motion model; the Swift renderer draws
  `remainingRoute` / `connector` / pose and must not re-project GPS (the Humble-Object invariant the
  Konsist rule enforces on Kotlin and review enforces on Swift).

## Characterization tests (one per transition)

In `DriverMotionModelRouteTest`:

- ON_ROUTE→ON_ROUTE (follow + connector): `connectorLineDrawsFromRawGpsToSnappedPointWhileOnRoute`,
  `connectorHidesWhenRawSitsOnTheLine`, `positionStaysOnRouteAroundTheCornerNotOnTheChord`.
- ON_ROUTE→OFF_ROUTE (chord fallback): `offRouteGpsFallsBackToChordAndSurfacesOffRouteState`.
- OFF_ROUTE stickiness (hysteresis) + OFF_ROUTE→ON_ROUTE: `offRouteUsesEnterExitHysteresis`.
- Flag default: `routeFollowingDefaultsOffSoSetRouteIsAChordNoop`, `enablingTheFlagActivatesRouteMode`.

## Consequences

**Positive**

- The car can no longer silently lie about being on the route; off-route is visible (state +
  connector). Acting on it (refetch) is deferred to the client if/when built — see ADR 0002.
- Binary keeps the machine small and the renderer a Humble Object; no continuous-confidence plumbing
  to maintain across the KMP bridge.

**Negative**

- Two thresholds are tuning knobs that need on-device calibration (default 30/15 m). If on-device
  proves binary insufficient, the fork can be revisited toward graded states — this ADR would then be
  superseded, not amended.
