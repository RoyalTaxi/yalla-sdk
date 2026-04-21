# Architecture Decisions

> Why things are the way they are. Read this before proposing changes.

## ADR-001: Either over try-catch

**Decision**: All fallible operations return `Either<D, E>` instead of throwing exceptions.

**Why**: Exceptions are invisible in the type system. A function that throws gives no compile-time
signal about failure modes. `Either` forces callers to handle both success and failure paths.

**Consequence**: Every repository function returns `Either`. Use `onSuccess`/`onFailure` for side effects,
`mapSuccess`/`mapFailure` for transformations.

---

## ADR-002: Kotlin Multiplatform with expect/actual

**Decision**: Share code via KMP. Platform-specific code uses `expect`/`actual`.

**Why**: One codebase for Android + iOS. Native performance, native UI where needed.
expect/actual provides compile-time safety — missing implementations are caught at build time.

**Consequence**: Common code can't reference Android or iOS APIs directly. Platform code lives
in `androidMain`/`iosMain` source sets.

---

## ADR-003: Colors + Dimens + Defaults pattern (not State bundling)

**Decision**: Components use separate `Colors`/`Dimens` classes with a `Defaults` factory object.
Content is passed as individual parameters, not bundled into State classes.

**Why**: State bundling (putting text, icon, and loading flag into one data class) couples
content to the component. Separate parameters are more flexible and follow Compose conventions
(see Jetpack Compose `Button`, `TextField`, `Card` APIs).

**Consequence**: Components have more parameters but are more composable. Colors and Dimens are
`@Immutable` to help the Compose compiler skip recomposition.

---

## ADR-004: Google Maps + MapLibre dual support

**Decision**: The maps module supports both Google Maps and MapLibre at runtime.

**Why**: MapLibre provides offline maps and custom tile servers (important for Uzbekistan
where Google Maps coverage is limited). Google Maps provides traffic and street view.
Users choose their preferred provider.

**Consequence**: `SwitchingMapProvider` and `SwitchingMapController` handle runtime switching.
Both providers must implement the same `MapController` interface. State is preserved during switches.

---

## ADR-005: String parameters in composites (current, slot migration planned)

**Decision**: Some composite components (ListItem, ActionSheet, etc.) accept `String` parameters
instead of `@Composable` slot lambdas.

**Why**: This was the initial API design. Slots are more flexible but require more boilerplate
at call sites. The current API works correctly.

**Plan**: Migrate to slots in the next major version (breaking change). This is tracked as P2
in `SDK_STATUS.md`. Don't change these APIs without coordinating the YallaClient migration.

---

## ADR-006: In-repo documentation over external tools

**Decision**: All documentation lives in the repository (`docs/`, `MODULE.md`, `SDK_STATUS.md`,
`COMPONENT_STANDARD.md`). No Notion, Confluence, or external wikis.

**Why**: Documentation that lives with the code stays in sync with the code. External docs
drift. New developers clone the repo and have everything they need.

**Consequence**: Documentation must be updated in the same PR as code changes. Review includes
doc review.

---

## ADR-007: Design tokens in a separate module

**Decision**: Colors and fonts live in `design/`, not scattered across component modules.

**Why**: Single source of truth for the visual language. Changing a brand color updates everywhere.
Components reference `System.color.*` and `System.font.*` — never hardcoded values.

**Exception**: `maps/` module has hardcoded overlay colors because it can't depend on `design/`
(different dependency level). These are documented in `MapStyles.kt`.

---

## ADR-008: safeApiCall with retry for idempotent calls

**Decision**: `safeApiCall` supports automatic retry with exponential backoff for idempotent calls.

**Why**: Mobile networks are unreliable. GET requests are safe to retry. POST requests are not
(could create duplicate orders). The `isIdempotent` flag controls this.

**Consequence**: Always set `isIdempotent = true` for GET/HEAD requests. Never for POST/PUT/DELETE
unless you've verified idempotency on the server side.

---

## ADR-009: Disable five ktlint_official rules that conflict with Compose patterns

**Decision**: Disable `multiline-expression-wrapping`, `function-signature`, `class-signature`, `argument-list-wrapping`, and `no-empty-first-line-in-class-body` in `.editorconfig` for this codebase.

**Why**: The `ktlint_official` code style includes these rules. In practice, on Compose-heavy Kotlin code (every primitive + composite + many platform actuals), they fight each other during `ktlintFormat`: one rule's preferred wrapping triggers another's violation, and the format pass does not converge even after three consecutive iterations (~901 violations found, 535 residual after auto-fix). Forcing the rules by manual reformat would touch ~80 files across every UI module, with meaningful regression risk on layout-sensitive call sites.

The existing `.editorconfig` already disables nine ktlint_official rules (`trailing-comma-on-call-site`, `trailing-comma-on-declaration-site`, `class-naming`, `filename`, `no-wildcard-imports`, `function-expression-body`, `string-template-indent`, `function-type-modifier-spacing`, plus `function-naming` for `@Composable`). Adding five more is consistent with that precedent.

**Consequence**: Five rules no longer enforced. The remaining ktlint_official rules continue to enforce 120-column max, indentation, naming, and import ordering. CI gates on `./gradlew ktlintCheck` per Phase 1 plan. If a future PR wants to re-enable any disabled rule, it must include a full-codebase reformat in the same commit and pass CI, with an ADR update.

Decided: 2026-04-21. Part of Phase 1 of the v1.0 launch.
