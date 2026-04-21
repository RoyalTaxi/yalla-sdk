# Phase 1 — Investigation Notes

> Created: 2026-04-21. Branch: `feature/v1-phase1-foundation`. Base commit: `930509c`.

## Baseline (Task 1)

- `ktlintCheck`: **FAIL** (pre-existing)
- `detekt`: **PASS**
- `test`: **UNRUNNABLE** (see notes below)

Any regressions during Phase 1 must be measured against this baseline.

### `ktlintCheck` — pre-existing failures

Two tasks fail. Both are style-only violations. No code logic changes are required to fix them; `./gradlew ktlintFormat` will resolve them in one shot. Phase 1 must not regress them further; a later task in this phase should run `ktlintFormat` and re-baseline.

| Task | Violations | Rules |
|---|---|---|
| `:core:ktlintCommonMainSourceSetCheck` | 7 | `standard:no-empty-first-line-in-class-body` (7) |
| `:composites:ktlintCommonTestSourceSetCheck` | 123 | `standard:multiline-expression-wrapping` (67), `standard:no-empty-first-line-in-class-body` (56) |

Files affected in `:core` (all under `core/src/commonMain/kotlin/uz/yalla/core/contract/`):
- `location/LocationProvider.kt`
- `preferences/ConfigPreferences.kt`
- `preferences/InterfacePreferences.kt`
- `preferences/PositionPreferences.kt`
- `preferences/SessionPreferences.kt`
- `preferences/StaticPreferences.kt`
- `preferences/UserPreferences.kt`

Files affected in `:composites` are all test files under `composites/src/commonTest/kotlin/uz/yalla/composites/` (card/, item/, sheet/ subdirectories).

### `test` — unrunnable on this host

- The spec calls for `./gradlew test -q`. There is **no `test` task at the root project** (KMP doesn't auto-create one).
- The canonical aggregator per `docs/05-TESTING.md` is `./gradlew allTests`.
- `allTests` fails on this host at `:maps:xcodeVersion` with `MissingXcodeException`. The host has only Command Line Tools (`/Library/Developer/CommandLineTools`), not a full Xcode install, so Kotlin/Native iOS simulator test targets cannot be linked.
- Android unit test tasks (`testDebugUnitTest`) aren't emitted by these modules either; tests run via `iosSimulatorArm64Test` + `testDebugUnitTest` aggregated by `allTests`.
- **Net effect:** no test count can be captured on this host. CI has Xcode and should be the authoritative source of the test-count baseline. Later in Phase 1, once CI is wired up, the baseline row should be updated from the CI run.

## Alpha-Start Investigations (Task 2)

(filled in during Task 2)
