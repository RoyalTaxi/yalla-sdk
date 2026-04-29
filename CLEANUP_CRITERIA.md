# yalla-sdk cleanup criteria — phases 2-4

> Working document for the in-flight cleanup. Delete this file at the end of phase 4 (single alpha tag, criterion 8).

The bar that every remaining cleanup wave is judged against. Phase 1 already shipped (terse MODULE.md, lean tooling, dropped boilerplate, flattened packages, convention plugins). Phases 2-4 carry the same spirit deeper into production code.

## Mental model

The SDK is a **LEGO kit at many granularities**:

| Brick type            | Module                       |
| --------------------- | ---------------------------- |
| Raw materials         | `resources`                  |
| Visual language       | `design`                     |
| Logic atoms / utility | `core`, `foundation`         |
| Data atoms            | `data`                       |
| UI atoms              | `primitives`                 |
| UI molecules          | `composites`                 |
| Platform-specific     | `platform`                   |
| Integrations          | `firebase`, `maps`, `media`  |
| Version alignment     | `bom`                        |
| Build conventions     | `build-logic`                |

YallaClient is the **assembly layer** — it pulls bricks and wires the product (screens, navigation graph, business orchestration, product copy). This frame replaces the simpler "infrastructure vs feature" reading: the SDK contains every reusable building block, regardless of granularity or whether it carries domain meaning. What it does not contain is the wired-up product.

## Constraints (hard, non-negotiable)

- No net new features.
- No removed features.
- Code may be relocated across the SDK ↔ YallaClient boundary, but its observable behavior is preserved.
- Phase 1 modules (`bom`, `build-logic`, `resources`) are out of scope for further restructuring.

---

## Criterion 1 — Where code lives (the lego test)

**SDK** — a brick that snaps together with other bricks. Composable, no hardcoded product copy, no Ildam-specific business orchestration.

**YallaClient** — an assembly. The final wired-up product surface (screens, navigation graph, hardcoded business rules, product strings).

**Examples**

- `PhoneFormatter`             → brick, SDK.
- `RideStatus` enum             → brick (UI/data/business compose with it), SDK.
- `BalanceCard(amount, …)`      → composite brick, SDK (`composites`).
- `WalletScreen` wired to `BalanceCard` + Ildam business rules + product copy → assembly, YallaClient.
- "Принять заказ" string + `AcceptRideButton` that hardcodes it → assembly, YallaClient. The same button parametrized over its label → composite brick, SDK.

**During cleanup**

- Brick living in YallaClient that should snap into SDK → flag for promotion.
- Assembly living in SDK that should be in YallaClient → flag for demotion.
- Borderline cases → flag, defer to Islom.

The promotion/demotion list is consolidated and applied in one batch against YallaClient at the end of the long-lived branch (criterion 8).

---

## Criterion 2 — What gets deleted (AI-blob test)

Anything in any of these five buckets is deleted on sight:

1. **Docs/config bloat** — KDoc paraphrasing signatures, READMEs, CONTRIBUTING.md, CODE_OF_CONDUCT.md, ISSUE_TEMPLATE/, over-engineered tooling configs (detekt, ktlint, spotless, BCV — already removed in phase 1; same posture continues).
2. **Comment redundancy** — comments above obvious code, restated parameter names, "TODO: review" cruft, copy-paste banner comments.
3. **Single-use abstractions** — interfaces with one implementation kept "for testability" (use a fake of the concrete class instead), helpers called once, factory functions wrapping a single constructor, wrapper classes that delegate every method to a field.
4. **Dead code** — unused parameters, unreachable branches, builders never built, types nothing references, tests for behavior that no longer exists.
5. **Speculative generalization** — generics that always resolve to one concrete type, configuration knobs with one default value, sealed `when` branches that don't matter in this codebase, `T : Comparable<T>` style constraints with one call site.

---

## Criterion 3 — Public API surface (left alone)

Alpha versioning. `internal` vs `public` is **not** a cleanup concern. No tightening passes. Public API hardening is a 1.0 problem.

---

## Criterion 4 — Module dependency rules

**Discover-and-document.** Cleanup maps each module's actual `dependencies {}` graph, flags any cycles or surprising imports, and records the recovered DAG under a **"Depends on"** section in each MODULE.md.

- No pre-imposed layering.
- Cycles get fixed in the wave that surfaces them.
- Sideways same-phase deps are fine if real and minimal — just document them.

---

## Criterion 5 — Documentation bar

- **Every public symbol gets KDoc** that adds information beyond what the signature already conveys: behavior, side effects, threading rules, ownership, hidden constraints, error semantics.
- A KDoc that paraphrases the signature is itself bloat (criterion 2-1) and is deleted.
- **MODULE.md** per module follows the phase-1 form:

  ```
  # Module <name>
  > One-line tagline.

  ## What this is
  ## What this is NOT
  ## Usage
  ## Notes
  ## Depends on
  ```

- `docs/` and `docs/adr/` stay deleted. No ADRs are reintroduced as part of the cleanup.

---

## Criterion 6 — Test bar

A module is not "done" until **all** of these hold:

- Every public function has a test.
- Every `Either.Failure` / sealed-error variant has a test.
- Every state-machine (Orbit `ContainerHost`) intent → state-transition is tested.
- Stack: JUnit 4 + `kotlin.test` assertions + Turbine for flows + ktor-client-mock for HTTP.
- **Hand-written fakes only.** No MockK, no Mockito, no JUnit 5.

Tests for behavior that no longer exists go (criterion 2-4).

---

## Criterion 7 — Build/tooling discipline

- Default: each module's `build.gradle.kts` applies its convention plugin and lists `dependencies {}`. Nothing else.
- Real one-off needs (Google Maps API key wiring, Firebase Services plugin, Valkyrie codegen for resources) sit inline with a `// why-not-in-plugin: …` comment explaining the deviation.
- Anything non-trivial in a module build file justifies extracting a new convention plugin (e.g., `KmpFirebaseConventionPlugin`, `KmpMapsConventionPlugin`).
- Existing plugins: `KmpLibraryConventionPlugin`, `KmpComposeConventionPlugin`, `BomConventionPlugin`, `YallaPublishing`.

---

## Criterion 8 — Versioning during cleanup

- Long-lived `cleanup/phase-2-3-4` branch off `main`.
- **No alpha is published while the cleanup is in flight.**
- Phases land on this branch in order: 2 → 3 → 4.
- A single alpha tag at the end ships the cleaned SDK.
- YallaClient migrates **once**, against that single alpha, with the consolidated migration list (per criterion 1).
- Until then, `main` continues to receive only urgent fixes; cleanup branch periodically rebases onto `main` to absorb them.

---

## Criterion 9 — Per-module work shape

For each module, in this order:

1. **Delete bloat** (criterion 2). Smaller code first — every later step is cheaper.
2. **Map dependencies**, fix any cycles, write the MODULE.md *Depends on* section.
3. **Restructure** — flatten over-nested packages (precedent: `contract/preferences/* → preferences/*`), normalize naming, split god files (>~300 lines, >5 responsibilities).
4. **Quality pass** (criterion 11) — propose rewrites where warranted, gate before applying.
5. **Promote/demote** (criterion 1) — flag pieces that need to cross the SDK/YallaClient boundary; do **not** move yet.
6. **KDoc** every public symbol per criterion 5.
7. **Test** to bar per criterion 6.
8. **Compile + test** the module and its intra-SDK consumers.
9. **Commit** in 5-7-fix waves with detailed conventional-commit bodies. Use `refactor!:` prefix where the public shape changes (alpha, so allowed).

---

## Criterion 10 — Phase order

| Phase | Modules                                            | Why this order                                              |
| ----- | -------------------------------------------------- | ----------------------------------------------------------- |
| 2     | `core`, `data`                                     | Logic atoms; UI and integrations depend on these.            |
| 3     | `design`, `foundation`, `primitives`, `composites` | UI brick stack; depends on phase-2 atoms.                    |
| 4     | `firebase`, `maps`, `media`, `platform`            | Integrations / platform-specific bricks; depend on 2 + 3.    |

Within a phase, modules are cleaned in the order listed (their natural dependency order inside the phase).

---

## Criterion 11 — Quality bar (rewrite eligible)

A rewrite is justified when **any** of the following hold in a module:

- **God classes/files** (>~300 lines or >5 distinct responsibilities).
- **Inconsistent patterns within the module** — e.g., three different ways to map errors → unify to one.
- **Non-idiomatic Kotlin** that is more than a one-line lift:
  - Callback chains where Flow / coroutines fit.
  - Manual JSON parsing where kotlinx.serialization fits.
  - `ArrayList` / `HashMap` literals → `mutableListOf` / `mutableMapOf`.
  - `Any?` returns where a sealed type belongs.
  - String-typed identifiers that should be value classes.
- **Architecture violations** per Islom's global CLAUDE.md:
  - `try { … } catch { … }` in business logic — errors live in `data/` and get mapped to `Either<DataError, T>`.
  - `feature/` importing from `data/`; `domain/` importing from `data/` or `feature/`.
  - Mappers as classes or DTO extension functions — should be `internal object Mapper { fun … }`.
  - Custom MVI instead of Orbit (`org.orbit-mvi:orbit-core`).
  - Arrow types instead of the project's `Either<L, R>` + `DataError` hierarchy.
  - `Service` named `Api`, or missing companion-object endpoint constants.
  - `InMemoryTokenProvider`, manual `Authorization` headers, or `AuthEventBus` (use Ktor `Auth` plugin + `SessionStore` + `SessionExpiredSignal`).
- **Untestable shape** that blocks the criterion-6 test bar (e.g., a god class that can't be exercised without instantiating the world).

**Targets to converge on** (all from the global CLAUDE.md): `Either`/`DataError`; `Service` classes with companion endpoint constants; `internal object` mappers; Orbit + Decompose; partial extension files (`MyComponent+Intent.kt`, `MyComponent+Network.kt`); `BaseComponent`; Route + Screen split; `LocalAppColors.current` + `LocalStrings.current` over static singletons; transient form input in the View, not in Orbit state.

**Behavior is preserved.** No new features, no removed features. Only internals reshape.

**Gate.** Rewrites touching >100 lines get a one-paragraph rationale presented to Islom before commit. He approves. Smaller rewrites land in the wave commit directly.

---

## Open items / known follow-ups

- **YallaClient migration** is its own follow-up project, scoped after the single alpha tag publishes. Inputs: the consolidated promotion/demotion list from criterion 1, and the breaking-change summary from `refactor!:` commits.
- **Convention plugin growth** — phases 3 and 4 may surface enough one-offs to justify `KmpFirebaseConventionPlugin`, `KmpMapsConventionPlugin`, etc. (criterion 7). Decide per-module when it surfaces, not pre-emptively.
