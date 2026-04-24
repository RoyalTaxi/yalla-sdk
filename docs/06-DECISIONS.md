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

---

## ADR-010: Either generic parameter order flipped to `<E, D>` (error-first)

**Decision**: Flip `Either<out D, out E>` to `Either<out E, out D>` — error first, success second. Applies to the sealed interface declaration and every extension (`mapSuccess`, `mapFailure`, `fold`, `getOrNull`, `getOrThrow`, etc.).

**Why**: Ecosystem convention is error-first. Arrow's `Either<A, B>` is `<Left=Error, Right=Success>`. Every Kotlin engineer who has used Arrow reads `Either<X, Y>` with `X` as the error type. Our previous order (`<Data, Error>`) inverted that and would confuse every non-Yalla contributor permanently after 1.0. Fix it now, while pre-1.0 full-risk mode allows breaking changes.

**Consequence**: Every call-site in `core`, `data`, and YallaClient flips. Cumulative breaking: `0.0.8-alpha04` → `0.0.9-alpha01`. YallaClient's scratch branch `chore/sdk-phase2-either-flip` merges to YallaClient's `dev` branch in lockstep with this SDK PR.

Decided: 2026-04-21. Part of Phase 2 of the v1.0 launch.

---

## ADR-011: `createHttpClient` scope ownership moves to caller

**Decision**: `createHttpClient` now takes a `CoroutineScope` parameter. The SDK does not internally construct a `CoroutineScope(ioDispatcher + SupervisorJob())`. Caller owns lifecycle; when the caller cancels the scope, the client's background flows (header polling, 401 reaction) stop cleanly.

**Why**: The previous signature leaked an unmanaged scope for the process lifetime. `HttpClient.close()` does not cancel that scope, because the scope is unreachable from the returned `HttpClient`. This ADR inverts ownership: caller scope → SDK uses it → caller cancels it → SDK cleans up.

**Consequence**: Breaking change for YallaClient — its Koin `single<HttpClient>(...)` calls must supply a scope. YallaClient's `chore/sdk-phase2-either-flip` branch absorbs this alongside the Either flip (or a second scratch branch if separation is cleaner). After this ADR, long-running tests can construct and tear down an `HttpClient` inside a `runTest { ... }` block without leaking coroutines.

Decided: 2026-04-21. Part of Phase 2 of the v1.0 launch.

---

## ADR-012: HTTP 3xx responses map to `DataError.Network.Client`

**Decision**: 3xx HTTP status codes that reach `safeApiCall` map to `DataError.Network.Client`. Rationale: Ktor's `HttpClient` follows redirects automatically when `Location` is present; a 3xx surfacing from `safeApiCall` means the server returned an unfollowable redirect (missing/invalid `Location`), which is a client-facing protocol issue (caller's request triggered an unreachable redirect) rather than a server error.

**Why**: Option considered and rejected: a dedicated `DataError.Network.Redirect` type. That would be semantically cleaner but introduces a sealed-hierarchy addition that breaks exhaustive `when` at every caller. Under full-risk pre-1.0 mode we could accept the break, but the marginal benefit of a redirect-specific type doesn't justify touching every downstream `when`.

**Consequence**: Non-breaking. KDoc on the mapping documents the rationale so future readers don't re-litigate.

Decided: 2026-04-21. Part of Phase 2 of the v1.0 launch.

---

## ADR-013: `LocationManager` caller-owned `CoroutineScope`

**Decision**: `LocationManager`'s primary constructor takes a `CoroutineScope` parameter. The SDK does not internally construct `CoroutineScope(SupervisorJob() + Dispatchers.Main)`. Caller owns lifecycle; cancelling the scope stops all in-flight tracking. The `close()` method is removed — there is nothing for the SDK to close when it doesn't own the scope.

Signature:

```kotlin
class LocationManager(
    val locationTracker: LocationTracker,
    private val scope: CoroutineScope,
    private val defaultLocation: GeoPoint = DEFAULT_LOCATION,
) : LocationProvider
```

**Why**: Same root cause as ADR-011 (`createHttpClient`): the previous signature leaked an unmanaged scope for the process lifetime. Callers who forgot to call `close()` leaked the scope silently. This ADR inverts ownership: caller scope → SDK uses it → caller cancels it → SDK cleans up.

**Consequence**: Breaking. Every YallaClient `LocationManager(...)` construction must pass a scope — typically a process-lifetime single in the Koin graph:

```kotlin
val locationModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single { LocationManager(get(), get()) }
}
```

YallaClient's `chore/sdk-phase3-bridge` branch carries the call-site migration in lockstep.

Decided: 2026-04-22. Part of Phase 3 of the v1.0 launch.

---

## ADR-014: `LanguageOption` and `LocaleKind` narrowed to production-ready locales

**Decision**: Remove `LanguageOption.UzbekCyrillic` and `LanguageOption.English` from the sealed hierarchy. Remove `LocaleKind.UzCyrillic` and `LocaleKind.En` from the enum. `LanguageOption.from(kind)` is now exhaustive over the two remaining cases (`Uz`, `Ru`). iOS `getCurrentLanguage()` falls back to `"uz"` instead of `"en"` when the system doesn't expose one.

Resulting state:

```kotlin
// core/settings/LocaleKind.kt
@Serializable
enum class LocaleKind(val code: String) {
    @SerialName("uz") Uz("uz"),
    @SerialName("ru") Ru("ru"),
}

// foundation/settings/LanguageOption.kt
sealed class LanguageOption(...) : Selectable {
    data object Uzbek : LanguageOption(...)
    data object Russian : LanguageOption(...)
    companion object {
        val all = listOf(Uzbek, Russian)
        fun from(kind: LocaleKind): LanguageOption = when (kind) {
            LocaleKind.Uz -> Uzbek
            LocaleKind.Ru -> Russian
        }
    }
}
```

**Why**: `UzbekCyrillic` and `English` were labelled "not production-ready" but still exposed. They were stable API surface implying support that didn't exist. Under full-risk pre-1.0 mode we delete rather than deprecate.

String resources: `values-en/strings.xml` stays on disk as a fallback asset — it is not tied to `LocaleKind.En` on the API side. `values-be/` is renamed to `values-uz-Cyrl/` by this Phase's Task 8 because the directory contains Uzbek Cyrillic text, but that's a resource-bundle filename correction, not a `LocaleKind.UzCyrillic` restoration.

**Consequence**: Breaking on two planes:
1. API — any YallaClient call to `LanguageOption.English`, `LanguageOption.UzbekCyrillic`, `LocaleKind.En`, or `LocaleKind.UzCyrillic` fails to compile.
2. Persistence — stored `InterfacePreferences.localeType` values of `"en"` or `"uz-Cyrl"` fall through `LocaleKind.from(code)` to `LocaleKind.Uz` silently. Acceptable: no user in YallaClient prod has persisted those (neither was in the picker's `all` list).

YallaClient's `chore/sdk-phase3-bridge` branch drops any dead references.

Decided: 2026-04-22. Part of Phase 3 of the v1.0 launch.

---

## ADR-015: `platform` module — four expect/actual asymmetries resolved

Four separate decisions bundled into one ADR because they are all Phase 3 Bridge work on the same module.

### ADR-015a: `NativeSheet.onFullyExpanded` semantics locked

**Decision**: No code change. The `onFullyExpanded` parameter's KDoc on the `expect` declaration is tightened to guarantee observable behavior on both platforms.

**Semantics**:
- On Android: fires when `SheetValue.Expanded == currentValue == targetValue` — i.e., after the settle animation completes and the sheet is at rest at the fully-expanded detent.
- On iOS: fires when the `UISheetPresentationController` presentation animation completes and the sheet is at the largest configured detent.

**Why**: The parameter existed without a contract. Consumers who use `onFullyExpanded` to trigger post-expand actions (e.g., scroll to content, focus a field) need a deterministic fire point. "Animation settled, not in-progress" is the only useful semantic — firing mid-animation would cause jank.

**Consequence**: Non-breaking. KDoc tightened; both actuals already matched these semantics.

Decided: 2026-04-22. Part of Phase 3 of the v1.0 launch.

---

### ADR-015b: `SystemBarColors` color overload removed

**Decision**: Delete the `SystemBarColors(statusBarColor: Color, navigationBarColor: Color)` `expect` declaration and both Android and iOS `actual` implementations. Keep only `SystemBarColors(darkIcons: Boolean)`.

**Why**: `YallaTheme` owns system bar background colors; the color overload duplicated that responsibility and created two paths for the same concern. The iOS actual silently ignored `navigationBarColor` (the home indicator area has no configurable background on iOS), making the cross-platform contract misleading. The `darkIcons` overload is the only surface consumers genuinely need.

**Consequence**: Breaking. YallaClient call sites using `SystemBarColors(statusBarColor = ..., navigationBarColor = ...)` must migrate to `SystemBarColors(darkIcons = ...)` or remove the call if `YallaTheme` already handles it.

Decided: 2026-04-22. Part of Phase 3 of the v1.0 launch.

---

### ADR-015c: `ObserveSmsCode` moved to androidMain-only public surface

**Decision**: Delete `platform/src/commonMain/kotlin/uz/yalla/platform/otp/SmsCodeObserver.kt` (the `expect` declaration) and `platform/src/iosMain/.../SmsCodeObserver.ios.kt` (the no-op iOS `actual`). The Android implementation moves from `actual fun ObserveSmsCode` to a plain `fun ObserveSmsCode` in `androidMain`, retaining the same signature.

**Why**: The no-op iOS `actual` was deceptive — calling `ObserveSmsCode` on iOS compiled and ran silently, never triggering `onCodeReceived`. iOS apps should configure `UITextField.textContentType = .oneTimeCode` and let the system keyboard handle SMS autofill; no SDK composable is needed or correct there. The `expect`/`actual` pattern is appropriate when both platforms have a real implementation; a permanent no-op `actual` is a smell that the API boundary is wrong.

**Consequence**: Breaking. iOS-targeting common code that calls `ObserveSmsCode` will no longer compile. iOS call sites should be removed — the native autofill path is zero-code. Android call sites are unaffected at the Kotlin level; they just no longer have the `actual` keyword in the implementation.

Decided: 2026-04-22. Part of Phase 3 of the v1.0 launch.

---

### ADR-015d: `PlatformConfig` Android/iOS asymmetry accepted

**Decision**: No code change. The asymmetry between `AndroidPlatformConfig` (a marker class with no factories) and `IosPlatformConfig` (a class with a `Builder` requiring three factories) is intentional and documented in KDoc on both classes.

**Why**:
- Android: Compose Material3 supplies all platform UI primitives (sheets, icon buttons, navigation) natively. No UIKit interop layer exists. `AndroidPlatformConfig` is a marker that initializes the platform with no additional wiring.
- iOS: UIKit components cannot be driven from Kotlin/Compose alone. Sheet presentation requires a `UISheetPresentationController` adapter, and icon buttons require a `UIView`-backed Swift renderer. These three factories (`sheetPresenter`, `circleButton`, `squircleButton`) must be supplied by the host application's Swift layer.

Forcing a symmetric API (e.g., dummy factory parameters on Android) would be a leaky abstraction and add noise to every Android integration.

**Consequence**: Non-breaking. KDoc updated on both `AndroidPlatformConfig` and `IosPlatformConfig.Builder` to explain the asymmetry.

Decided: 2026-04-22. Part of Phase 3 of the v1.0 launch.

---

## ADR-017: String parameters migrated to `@Composable () -> Unit` slots in 8 composites

**Status**: Accepted. Supersedes the "slot migration planned" note in ADR-005.

**Decision**: All 8 composites that previously accepted `String` (or `String?`) text parameters now accept `@Composable () -> Unit` slots instead. `EmptyStateState` is deleted entirely — `EmptyState` takes three separate slots. Default text style and color are delivered to each slot via `ProvideTextStyle` (Material3), so a bare `Text("…")` in the slot inherits them automatically. Style-override params (`titleStyle`, `descriptionStyle`) on `Navigable` and `EmptyState` are removed; callers that need a custom style pass a styled `Text` inside their slot.

**Migrated surfaces**:

1. `composites/src/.../item/ListItem.kt` — `title: String`, `subtitle: String?` → slots
2. `composites/src/.../item/IconItem.kt` — `title: String`, `subtitle: String?` → slots
3. `composites/src/.../item/NavigableItem.kt` — `title: String`, `subtitle: String?` → slots
4. `composites/src/.../item/SelectableItem.kt` — `title: String` → slot
5. `composites/src/.../item/PricingItem.kt` — `name: String`, `price: String` → slots
6. `composites/src/.../item/AddressItem.kt` (both overloads) — `text: String` → slot (overload 1); `placeholder: String` → slot (overload 2). `locations: List<String>` stays as data.
7. `composites/src/.../drawer/Navigable.kt` — `title: String`, `description: String?` → slots. `titleStyle` and `descriptionStyle` params removed; default style delivered via `ProvideTextStyle`.
8. `composites/src/.../view/EmptyState.kt` — `EmptyStateState(image, title, description)` data class deleted. `EmptyState` takes `image: @Composable () -> Unit`, `title: @Composable () -> Unit`, `description: (@Composable () -> Unit)? = null`.

**Why**: String parameters bundle content with implicit style decisions baked into the component. A slot gives the caller full control: annotated strings, inline icons alongside text, per-call `Modifier`, or anything else that composes. The Material3 framework convention (`Button`, `Card`, `Text`) already uses slots throughout; keeping String params in the SDK made our API feel foreign and required consumers to work around limitations (e.g., annotated strings, accessibility annotations). `ProvideTextStyle` carries the default so simple callers pay zero extra cost — `Text("x")` in a slot is as terse as the old `"x"` string argument.

`EmptyStateState` is deleted rather than deprecated because it is a pre-1.0 breaking window and the data class provided no value that three direct parameters don't provide more clearly.

**Consequence**: Breaking at every call site. Mechanical translation at each usage:
- `title = "x"` → `title = { Text("x") }`
- `subtitle = "y"` → `subtitle = { Text("y") }`
- `subtitle = null` → `subtitle = null` (unchanged — slot is nullable)
- `state = EmptyStateState(image = p, title = "t", description = "d")` → `image = { Image(p, null) }, title = { Text("t") }, description = { Text("d") }`

YallaClient will rewrite 16+ `Navigable` call sites and every `ListItem`/`IconItem`/`NavigableItem`/`SelectableItem`/`PricingItem`/`AddressItem`/`EmptyState` call site. That migration lands in a separate YallaClient PR (`chore/sdk-phase4-ui-bridge`).

Decided: 2026-04-22. Phase 4 of the v1.0 launch.

---

## ADR-018: `SwitchingMapProvider` and `SwitchingMapController` caller-owned `CoroutineScope`

**Status**: Accepted. Closes the scope-leak trifecta (ADR-011 HttpClient, ADR-013 LocationManager, ADR-018 SwitchingMapProvider/Controller).

**Decision**: `SwitchingMapProvider`'s primary constructor takes a `CoroutineScope` parameter alongside `googleProvider`, `libreProvider`, and `interfacePreferences`. The SDK no longer internally creates `CoroutineScope(SupervisorJob() + Dispatchers.Main)`. `SwitchingMapProvider.close()` is removed entirely — there is nothing for the SDK to close when it does not own the scope.

`SwitchingMapController` follows the same pattern: its constructor takes the `CoroutineScope` passed by `SwitchingMapProvider.createController(...)`. The controller derives a supervised child scope (`CoroutineScope(scope.coroutineContext + SupervisorJob())`) so that cancelling the parent propagates automatically. The `close()` override on `SwitchingMapController` is retained (it is part of the `MapController` interface contract) and now cancels the child scope only.

Resulting `SwitchingMapProvider` signature:

```kotlin
class SwitchingMapProvider(
    private val googleProvider: MapProvider,
    private val libreProvider: MapProvider,
    private val interfacePreferences: InterfacePreferences,
    private val scope: CoroutineScope,
) : MapProvider
```

**Why**: Same root cause as ADR-011 and ADR-013. The previous implementation created an unmanaged `CoroutineScope` at construction time. Callers who forgot to call `close()` leaked the scope and its preference-observation coroutine for the process lifetime, with no compile-time or runtime warning. Inverting ownership to the caller eliminates the silent leak: caller scope cancelled → observation stopped → no leak. The trifecta (HttpClient, LocationManager, SwitchingMapProvider) is now uniform: every long-running coroutine in the SDK runs inside a caller-supplied scope.

**Consequence**: Breaking. The old single-argument constructor `SwitchingMapProvider(interfacePreferences)` is gone. YallaClient's Koin module must be updated to supply four arguments and a scope — typically the same process-lifetime scope already established for `LocationManager`:

Before:

```kotlin
single<MapProvider> {
    SwitchingMapProvider(interfacePreferences = get())
}
```

After:

```kotlin
val mapScope = single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

single<MapProvider> {
    SwitchingMapProvider(
        googleProvider = GoogleMapProvider(),
        libreProvider = LibreMapProvider(),
        interfacePreferences = get(),
        scope = get(),
    )
}
```

Any call to `switchingMapProvider.close()` at YallaClient call sites must be removed — the method no longer exists. Lifetime is managed entirely by cancelling the scope.

YallaClient's `chore/sdk-phase5-bridge` branch carries the call-site migration in lockstep.

Decided: 2026-04-22. Phase 5 of the v1.0 launch.

---

## ADR-019: YallaGallery common surface narrowed to PHPicker-equivalent; Paging3 grid becomes Android-only

**Status**: Accepted.

**Decision**: `YallaGallery` common `expect` signature narrows to `(modifier, onImageSelected)` — the intersection of what Android's `PickVisualMedia` and iOS's `PHPickerViewController` deliver identically. The rich Paging3-backed grid moves to a new Android-only `YallaGalleryPagingGrid` composable with the full previous parameter set. `@ExperimentalYallaGalleryApi` is retired; both public composables are plain stable API.

**Why**: Pre-Phase-5 common surface promised a richness that iOS couldn't deliver — `lazyGridState`, `backgroundColor`, `header`, `progressIndicator`, `permissionDeniedContent` were all Android-only in practice. Callers passing these params for iOS got silent no-ops. Honest API: common surface = what both platforms actually do.

**Consequence**: Breaking. YallaClient call sites either switch to the narrow signature (cross-platform single-image pick) or migrate Android-only rich screens to `YallaGalleryPagingGrid`. `@OptIn(ExperimentalYallaGalleryApi::class)` removed everywhere.

Decided: 2026-04-22. Phase 5 of the v1.0 launch.

---

## ADR-020: Spacing and corner-radius tokens graduate into the design system

**Status**: Accepted.

**Decision**: Introduce two new token families alongside the existing `System.color.*` and `System.font.*`:

- `System.space.*` — semantic spacing aliases (`screenEdge`, `sheetEdge`, `contentEdge`, `itemGap`, `sectionGap`, `heroGap`, `inlineGap`) plus an escape-hatch `scale` (`xxs…massive`).
- `System.radius.*` — flat t-shirt radius scale (`xs`, `s`, `m`, `l`, `xl`, `sheet`).

Both are additive to `YallaTheme` (new parameters `spaceScheme`, `radiusScheme`, both with default factories) and resolve via `CompositionLocal` with a sensible default so previews still work outside a theme.

**Why**: An audit of YallaClient's `feature/**` screens surfaced eight distinct horizontal-padding values (4, 8, 10, 12, 16, 20, 24, 32, 36) across 15 modules, with multiple screens (`DetailsSheet`, `LoginScreen`, `CancelSheet`) mixing three or more within a single file. The bug class is structural, not stylistic: when the API is *"pick a number,"* consumers pick inconsistently. When the API becomes *"pick a semantic name,"* the 30-vs-20 bug fixed today on `LanguageScreen` stops being typable.

`System.color.*` and `System.font.*` already prove the pattern works. Spacing and radius are the two remaining primitive dimensions every composable reaches for. Typography (`font.*`) and colors (`color.*`) are intentionally not expanded in this change — they cover their ground.

**Structure choices**:

1. **Two-layer spacing** (semantic flat + nested `scale`) instead of a single t-shirt scale. Semantic names (`screenEdge`) win on ergonomics so correct usage is the shortest to type; the scale remains accessible but requires an extra `.scale.` hop, making it the conscious choice. This is the same discipline Tailwind and Material 3 enforce, scaled down to our surface.
2. **Flat radius scale** — radii don't split into categories the way colors do (`text`/`background`/`border`). Size carries intent.
3. **Single `standardSpaceScheme()` / `standardRadiusScheme()` factory** (no dark/light variant) — unlike colors, spacing doesn't depend on theme mode. Override via `YallaTheme` params for white-labeling.

**Consequence**: Non-breaking and purely additive on the public API (`apiCheck` passes after `apiDump` regenerates the baseline for `YallaTheme`'s new signature and the two new schemes). Pre-1.0 full-risk mode applies: no `@Deprecated` cycle needed for future tuning of values since no consumer is migrating from a prior shape.

**YallaClient follow-up** (not bundled with this SDK release): screen-by-screen sweep to replace raw `.dp` padding/radius values with `System.space.*` / `System.radius.*`. Prioritized by audit severity (`DetailsSheet`, `LoginScreen`, `CancelSheet` first). A detekt rule flagging raw `.dp` inside `padding()` / `RoundedCornerShape(...)` calls is a follow-up to preserve discipline after the sweep lands.

Decided: 2026-04-22.

---

## ADR-021: Motion tokens live inside `design`, not a standalone `motion` module

**Status**: Accepted.

**Decision**: Add a `motion` sub-namespace under the existing `design` module rather than standing up a separate `uz.yalla.sdk:motion` artifact. The token catalog is published under `System.motion.*`, accessible anywhere `YallaTheme` is already in scope. Tactile patterns (`System.haptic.*`) ship in a follow-up ADR/PR — see **Scope** below.

**Scope of this ADR**:

- **Ships in 0.0.17-alpha01** (PR #13): the `System.motion.*` catalog — `duration`, `easing`, `spring`, `stagger`.
- **Deferred to a focused follow-up** (separate PR, tracked as Chunk 0.C.haptic): `System.haptic.*` + `HapticController` `expect`/`actual`. Kept in this ADR so the architectural decision ("haptic co-locates with motion inside `design`") is captured alongside the motion decision — implementation lands incrementally because haptic forces `design` to gain its first `androidMain`/`iosMain` source sets, which is a higher-risk change than the pure-commonMain motion catalog.

**Namespaces shipped in 0.0.17-alpha01**:

- `System.motion.duration.*` — `instant (100ms)`, `quick (200ms)`, `standard (350ms)`, `slow (500ms)`, `contemplative (800ms)`. Returned as `Duration` (`kotlin.time`), not raw `Long`, so `animateFloatAsState(tween(duration.standard.inWholeMilliseconds.toInt()))` reads correctly but `delay(duration.quick)` also works.
- `System.motion.easing.*` — `standard`, `emphasized`, `entrance`, `exit` as `Easing` (Compose `CubicBezierEasing`). Material 3's curves are the baseline — we override only where the reference frames (Linear/Arc/Raycast) ship measurably different feel.
- `System.motion.spring.*` — `bouncy`, `gentle`, `snappy`, `stiff` as `SpringSpec<Float>`. Damping-ratio + stiffness tuned to the catalog in `YallaClient/docs/MOTION.md`.
- `System.motion.stagger.*` — `list (30ms)`, `grid (50ms)`, `cards (75ms)` as `Duration`. Consumed by a `Modifier.staggerReveal(visible, index)` helper also published from `design`.

**Namespaces deferred (architectural shape captured, implementation follows)**:

- `System.haptic.*` — `selection`, `confirm`, `warn`, `error`, `hero` as `HapticKind` enum values.

**Haptic bridging** (deferred — `expect`/`actual` shape for reference):

```kotlin
// commonMain
@Immutable interface HapticController { fun perform(kind: HapticKind) }
@Composable expect fun rememberHapticController(): HapticController

// androidMain: HapticFeedbackConstants + VibrationEffect.Composition (API 30+),
//              graceful fallback to HapticFeedbackConstants below.
// iosMain:    UIImpactFeedbackGenerator for selection/confirm/warn/error;
//              CHHapticEngine composition for `hero` (iOS 13+, falls back to
//              heavyImpact on older).
```

This is the first `expect`/`actual` surface in `design`. Previously `design` was platform-agnostic by construction; the motion catalog forces the split because haptic feedback has no cross-platform abstraction in Compose Multiplatform.

**Why a sub-namespace, not a module**:

1. Motion **is** design. Splitting it out would mirror Material's `material3-motion` misstep — consumers always pull it anyway, the extra artifact adds resolution + publish cost for zero isolation benefit.
2. BOM complexity is already non-trivial (12 artifacts). Adding a 13th for something that ships next to tokens we already publish is taxation without representation.
3. Haptic `expect`/`actual` forcing the module to go multiplatform is fine — `design` is already KMP; adding `androidMain`/`iosMain` source sets is a source-set addition, not a module structure change.

**Consequence**:

- `design` module stays commonMain-only in 0.0.17-alpha01 (motion catalog is pure Compose types). The haptic follow-up adds `design`'s first `androidMain`/`iosMain` source sets — BCV baseline regen needed at that point for `System.haptic.*` + the `HapticController` `expect` class. The motion token publish also regens the `design` baseline (new `System.motion.*` surface), done in PR #13.
- `foundation` module gains no new deps — the haptic pattern `HapticPattern` (enum-ish) lives in `design` because it's a token, not infrastructure.
- YallaClient consumers import exactly like the existing tokens: `uz.yalla.design.theme.System` — no new imports, just new properties.
- Pre-1.0 posture: public from day one, no `@RequiresOptIn`. If a token value needs retuning after shipping, we change the value (ABI-safe); if the *shape* of a token changes (e.g., `Duration` → `Long`), that's a breaking change and triggers the patch-bump-plus-alpha-reset rule.

**Non-goals**:

- **Not** shipping a "motion builder DSL." Components take `SpringSpec` / `Easing` / `Duration` directly from the tokens. No wrapper types that add indirection.
- **Not** shipping shared-element transition helpers in this ADR. That depends on Compose Multiplatform reaching parity on `SharedTransitionLayout` across Android + iOS, tracked separately.
- **Not** bundling platform-specific sound/vibration packs. Haptics stay abstract; concrete `CHHapticEngine` composition files are a follow-up for Phase ∞ polish.

Decided: 2026-04-24. Execution tracked as Chunk 0.C in the YallaClient refactor plan (`YallaClient/docs/superpowers/plans/2026-04-23-yalla-client-refactor.md`).

---

## ADR-022: Semantic top-level variants graduate into `DataError`

**Status**: Accepted.

> **Naming note**: earlier drafts of this ADR called the hierarchy
> `DomainError`. On implementation audit, the existing core type was already
> named `DataError` (dates to 0.0.1, 8 consumer files). Renaming would churn
> every consumer for marginal naming clarity. Kept the existing name.
> This ADR is about **extending `DataError` with semantic top-level
> variants** — the hierarchy shape described below is the one actually
> implemented in core, published under that name. If a rename ever
> becomes worth the churn, it gets its own ADR.

**Decision**: Extend the existing `sealed class DataError` in `core` with five semantic top-level variants (siblings to `Network`) so feature code pattern-matches on "what happened from the domain's perspective" rather than reconstructing meaning from HTTP status codes inside `Network.ClientWithMessage`. `Either<DataError, T>` remains the one-and-only error shape a domain or data function can return.

**Hierarchy** (all `commonMain`, published from `uz.yalla.sdk:core`):

```kotlin
sealed class DataError {

    /** Authentication missing or invalid. Caller should surface a login prompt. */
    data object Unauthorized : DataError()

    /** Authentication present but insufficient for the requested action. */
    data class Forbidden(val reason: String?) : DataError()

    /** Request can't be satisfied in the current server state (409). */
    data class Conflict(val reason: String?) : DataError()

    /** Request-shape problem surfaced by the server. `fields` maps field name → message. */
    data class Validation(val fields: Map<String, String>) : DataError()

    /** Addressed resource doesn't exist (semantic 404 with typed server payload). */
    data object NotFound : DataError()

    /** Pre-existing transport-level hierarchy. Unchanged in this ADR. */
    sealed class Network : DataError() {
        data object Connection : Network()
        data object Timeout : Network()
        data object Server : Network()
        data object Client : Network()
        data class ClientWithMessage(val code: Int, val message: String) : Network()
        data object Serialization : Network()
        data object Guest : Network()
        data object Unknown : Network()
    }
}
```

**How it reaches feature layers**: `data` module's `SafeApiCall` maps HTTP status codes + `IOException` subtypes to `DataError` variants. Feature view models pattern-match on the `DataError` hierarchy and translate to a UI-layer `SideEffect.ShowError(kind)` — the UI layer never sees a `Throwable`. Adding new sibling variants (like the five shipped by this ADR) **is a compile-break at exhaustive `when` call sites** — consumers must add the new branches or an `else`. This is documented in the Consequence section.

**Why this shape**:

1. **Sealed class, not enum**, because variants carry payloads (`fields`, `reason`). Sealed class (pre-existing choice) keeps the exhaustive-`when` safety while letting constants like `Unauthorized` be `data object` for zero-allocation pattern matching.
2. **`Network` stays nested sealed, not flat**, because "something went wrong at the network layer" is a meaningful branching point in the UI ("show the offline banner + retry") that's strictly coarser than per-subtype handling. Flattening would force every consumer to re-aggregate.
3. **Semantic variants are siblings of `Network`, not nested under it**, because `Unauthorized`, `Forbidden`, `Conflict`, `Validation`, `NotFound` describe the business meaning of a failure regardless of which transport delivered it. Nesting them under `Network` would falsely imply "only HTTP requests can be unauthorized" — but the same variants may show up from disk-side auth checks, cache expiry, etc.
4. **No HTTP-specific `4xx`/`5xx` leak at the domain edge** for semantic variants. Business code only ever sees `Conflict`, `Validation`, `Forbidden`, etc. The status-code detail is preserved inside `Network.ClientWithMessage.code` when callers need it for observability.
5. **No `Retryable` flag.** Whether a failure is retryable is context-sensitive (network vs business), and putting a bool on the hierarchy would lie as often as it told the truth. The `data` layer's retry policy lives in its own `RetryPolicy` type, not on the error.

**Consequence**:

- Every exhaustive `when (error: DataError)` site at every consumer of `core` breaks at compile time on bump to the version that ships these variants. Consumers add missing branches or an `else`. This is a **minor-breaking change** per the library-api rules — treated as an additive version bump with alpha reset (`0.0.15-alphaNN` → `0.0.16-alpha01`) because we're pre-1.0 and exhaustive-`when`-break is acceptable in that posture. YallaClient's bump PR must include the branch additions; not free.
- `SafeApiCall` in `data` module gains mapping rules for the new variants (HTTP 401 → `Unauthorized`, 403 → `Forbidden`, 409 → `Conflict`, 422 → `Validation`, typed 404 bodies → `NotFound`). Tracked as a follow-up PR once YallaClient's consumer sites are caught up.
- BCV baseline regen: new sealed variants published stable, no `@RequiresOptIn` gate. All variants ship committed.

**Non-goals**:

- Not shipping a `Result<T>` wrapper. `Either<DataError, T>` is already our result type (per ADR-001); this ADR narrows the error side of that, nothing more.
- Not shipping localized error messages. `Validation.fields` contains server-provided messages; feature code maps to locale-resolved strings via the existing `stringResource` mechanism. The SDK doesn't guess users' languages.
- Not shipping a crash-reporting SDK integration. Sentry hookup is Chunk 0.F in YallaClient.
- Not renaming `DataError` → `DomainError`. Discussed above in the Naming note.

Decided: 2026-04-24. Execution tracked as Chunk 0.D in the YallaClient refactor plan. Depends on ADR-001 (`Either`). Implemented in yalla-sdk 0.0.16-alpha01.
