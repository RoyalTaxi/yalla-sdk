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

---

## Phase 3 — `foundation` additions

### Promotions / demotions surfaced

*From phase 3 `foundation`:* none. Per `FOUNDATION_AUDIT.md` §5: zero promotions, zero demotions. Two borderline notes documented in MODULE.md:
- `LocationManager.DEFAULT_LOCATION = GeoPoint(41.2995, 69.2401)` (Tashkent center) — kept; product-specific default that's parameterizable.
- `LanguageOption` Phase-3 narrowing to Uzbek + Russian — kept; matches `core.settings.LocaleKind` shape.

The foundation/core `LocationProvider` collision question (raised in the audit prompt) **resolved**: `core.location.LocationProvider` is the canonical interface (consumed by `:maps`); foundation's Composable wrapper was redundant and got deleted in the G14 sweep below.

### Breaking changes shipped

*From phase 3 `foundation`:*

- `589c3b4ac refactor!(foundation): delete dead CompositionLocal sweep`
  Three dead public surfaces removed (~134 lines net):
    - `foundation.locale.LocaleProvider` + `LocaleState` + `LocalLocaleState` + `currentLocaleState()` — YallaClient defines its own `LocaleProvider` with Activity-recreation logic foundation's lacked, so YallaClient's is canonical.
    - `foundation.locale.currentLocale` (`ObserveLocale.kt`) — Composable wrapper, zero callers.
    - `foundation.location.LocationProvider` (Composable wrapper) + `LocalLocationManager` + `currentLocationManager()` — Koin DI is the actual injection seam. NOT to be confused with `core.location.LocationProvider` (the interface that `LocationManager` implements), which is canonical and stays.
  Action: YallaClient already uses `getCurrentLanguage()`/`getCurrentLanguageState()` directly + Koin DI for `LocationManager`; no migration needed.

- `121e5567e refactor!(foundation): inline DefaultDataErrorMapper into BaseViewModel`
  `DataErrorMapper` interface and `DefaultDataErrorMapper` impl deleted. The 8-variant `DataError.Network` → `StringResource` mapping moved into `BaseViewModel.mapDataErrorToUserMessage`'s `protected open` default body. `BaseViewModel(...)` constructor is now no-arg. Action: any consumer constructing `BaseViewModel(customMapper)` must subclass and override `mapDataErrorToUserMessage` instead — same effect, Kotlin-idiomatic seam.

- `395bf94bc refactor!(foundation): tighten api/implementation split, drop unused deps`
  17 declared deps → 13. **Dropped 11 unused** (`projects.design`, `compose.foundation`, `compose.material3`, `koin.compose.viewmodel`, `orbit.core`/`viewmodel`/`compose`, `kotlinx.serialization.json`, `connectivity.device`, `geo.compose`, `androidx.lifecycle.viewmodel.compose`). **Promoted 5 to `api`** (`compose.runtime`, `androidx.lifecycle.runtime.compose`, `kotlinx.coroutines.core`, `projects.resources`, `geo`). **Added 2** (`androidx.lifecycle.viewmodel`, `compose.ui` — both expose types in foundation's public surface). Action: YallaClient consumers that already declare these explicitly are unaffected. Catalog gained `androidx-lifecycle-viewmodel` alias.

- `743335bb2 refactor!(foundation): demote ExtendedLocation + apply @Immutable uniformly`
  G16: `ExtendedLocation` data class and `LocationManager.extendedLocation` field demoted to `internal`. Public surface uses `LocationManager.currentLocation` (GeoPoint-shaped). G17: `@Immutable` applied to `Location`, `FoundLocation`, `OptionModel<T>`, `Selectable`, and the sealed `*Option` parents. Action: any consumer reading `extendedLocation.altitude`/`speed`/`bearing`/`timestamp` must now use a custom `LocationManager` subclass or the underlying moko-geo `LocationTracker`. Verified zero external consumers.

### Architectural patterns documented (no code change, MODULE.md notes)

- **`BaseViewModel` is NOT a custom-MVI violation.** YallaClient feature ViewModels do `class HomeViewModel : BaseViewModel(...), ContainerHost<HomeState, HomeEffect>` — dual-inheritance. Foundation's BaseViewModel is the cross-cutting infra layer (loading + error dialog); Orbit is layered on top in YallaClient.
- **`LoadingController` `try { } finally { }`** — resource-cleanup idiom, no catch. Sanctioned pragmatic carve-out (not a criterion-11 violation).
- **`LocationManager` `runCatching { … }.onFailure { … }`** — system-API boundary error handling at the moko-geo seam (permission-denied, GPS-off). Sanctioned pragmatic carve-out.
- **`LocationMappers.kt`** — domain↔domain coercions (`core.*` types → foundation UI-ready types), NOT DTO mappers. Stays in foundation; doesn't move to `data/`.

---

---

## Phase 3 — `primitives` additions

### Promotions / demotions surfaced

*From phase 3 `primitives`:* none. Per `PRIMITIVES_AUDIT.md` §5: zero promotions, zero demotions. Three borderline default-copy flags neutralized (see breaking changes below).

### Pending consumers (audit decision G20 deferred)

- **`System.space.*` / `System.radius.*` token adoption** — primitives currently uses literal `dp` / `RoundedCornerShape(N.dp)` in every `*Defaults.dimens()` factory instead of reading from the design theme. ~150 substitutions across 30 components. Deferred per G20 to avoid visual regressions; revisit when the brand-spacing values are settled. Documented in `primitives/MODULE.md` notes.

### Breaking changes shipped

*From phase 3 `primitives`:*

- `f9ce149e4 refactor!(primitives): delete unused PhoneVisualTransformation + NumberVisualTransformation`
  Removed: `uz.yalla.primitives.transformation.PhoneVisualTransformation` and `uz.yalla.primitives.transformation.NumberVisualTransformation` (both classes). Zero callers SDK-wide. Same shape as core G1 / design G9 / foundation G14. Action: YallaClient's private `CardNumberVisualTransformation` (the historical migration target) keeps its private impl; if a future caller needs the public transformation surface, recreate from scratch with a real producer. `NumberField`'s private `PhoneVisualTransformation` object continues to provide the actual phone-input formatting.
- `9e063e724 refactor!(primitives): drop dead helpers, suppressions, and unused params`
  - `LocationPin` lost its unused `loading: Boolean = false` parameter. Action: drop the `loading = ...` argument at any call site.
  - `LoadingIndicatorDimens` constructor lost `smallStrokeWidth`/`mediumStrokeWidth`/`largeStrokeWidth` (3 fields) and the `strokeWidth(size)` method. `LoadingIndicatorDefaults.dimens(...)` factory likewise dropped 3 params. Action: remove any `smallStrokeWidth = ...` / `dimens.strokeWidth(...)` references; the component renders via `NativeLoadingIndicator` which doesn't take stroke width.
  - `uz.yalla.primitives.navigation.{ToolbarAction, ToolbarIcon}` typealiases removed. Action: import from `uz.yalla.platform.navigation.*` directly (the typealias targets).
  - `MaskFormatter` narrowed: `countPlaceholders(mask, maskChar)` and `extractRaw(formatted, mask, maskChar)` deleted; only `format()` remains. Zero callers anywhere.
- `fb4bdab66 refactor!(primitives): tighten api/implementation split, swap ArrowBack to YallaIcons`
  - **Deps changes (pom.xml).** `compose.runtime`, `compose.ui`, `compose.foundation`, `compose.material3`, `projects.design`, `projects.resources` all promoted to `api()`. Consumers that already declare these directly are unaffected; consumers relying on transitive `compose.materialIconsExtended` from primitives lose it (composites and platform still declare it independently).
  - **`NavigationButton.icon` default** changed from `Icons.AutoMirrored.Filled.ArrowBack` (Material Icons) to `YallaIcons.ArrowLeft` (from `:resources`). Visual-equivalent left-pointing arrow; same UX.
  - **`NavigationButton.contentDescription` default** dropped the `"Navigate back"` literal English string; default is now `null`. Callers must pass a localized string via `stringResource(...)` or accept `null` = decorative. Aligns with `TopBar.navigationIconContentDescription`'s existing posture.
- `d045a8568 refactor!(primitives): neutralize default copy + simplify squareSize`
  - **`SensitiveButton.confirmText` and `SensitiveButton.countdownText` are now REQUIRED `String` params** (was `String? = null` with order-specific `stringResource` fallback). Callers must pass localized strings. The component is "sensitive button," not "cancel order button"; defaults that referenced `Res.string.order_cancel_action_yes` / `Res.string.order_cancel_countdown` were leaking feature-specific copy into the SDK. Action: update every `SensitiveButton(...)` call site to pass `confirmText` and `countdownText` explicitly.
  - **`SplashOverlay.message` default** changed from `stringResource(Res.string.location_gps_subtitle)` (location-specific) to `""` (empty = no message slot rendered). Callers pass a localized string only when they need one.

### Architectural patterns documented (no code change)

- **Colors + Dimens + Defaults convention.** Each primitive component carries `{Component}Colors` (`@Immutable data class`) + `{Component}Dimens` (`@Immutable data class`) + `{Component}Defaults` (object with `colors()` / `dimens()` factories that read theme tokens via `System.color.*`, `System.font.*`). Documented in `primitives/MODULE.md` notes since the deleted `COMPONENT_STANDARD.md` no longer carries it.
- **`@Preview` annotations in commonMain** — accepted; ship into the published artifact but R8-shrinkable consumer-side.
- **`runComposeUiTest` behavioral test bar** — deferred post-alpha. Current `commonTest` is data-class equality only; behavioral coverage for the 14 untested components arrives with the runComposeUiTest infrastructure decision later.
- **`LocationPin.DEFAULT_LOCATION`-style product defaults** — kept; product-specific defaults are acceptable when parameterizable (consumer can override).

---

## Phase 3 — `composites` additions

### Promotions / demotions surfaced

*From phase 3 `composites`:* none. Per `COMPOSITES_AUDIT.md` §5: zero promotions, zero demotions of cross-module symbols. One symbol demoted *within* the module: `Modifier.expandableSheetDraggable` → `internal` (zero YallaClient consumers).

### Pending consumers (audit decisions deferred)

- **G1 — `materialIconsExtended` swap.** Composites still uses `Icons.AutoMirrored.{Filled,Default}.ArrowForward(Ios)` in 4 spots (`drawer/Navigable.kt`, `card/NavigableCard.kt`, `item/NavigableItem.kt`, `item/AddressItem.kt`). YallaIcons does not currently ship a chevron-right or arrow-right vector. Deferred until `:resources` adds the vectors; THEN composites can drop `materialIconsExtended` from its dep list. Documented in `composites/MODULE.md` notes.
- **G4 — Shallow-test rewrite.** All 33 composites tests assert `Colors`/`Dimens`/`Defaults` data-class equality only; the state-holders (`ExpandableSheetState`, `HeaderableSheetState`, `SnackbarController`, `DeviceConnectivityState`) have zero behavioral coverage. Deferred per the same `runComposeUiTest`-infrastructure stance primitives took. Three non-composable tests (A7-A9) ARE backfilled in wave 8 (`DeviceConnectivityStateTest`, `SnackbarControllerTest`, `PaymentResourceTest`) since they don't need a Compose UI test harness.
- **System.space.* / System.radius.* token adoption** — composites uses raw `dp` literals in every `*Defaults.dimens()` factory. Same posture as primitives (deferred). Documented.
- **`DeviceConnectivityState` package** — currently `uz.yalla.composites.sheet`, but the class has no sheet relationship. Move to a `connectivity/` sub-package deferred — the YallaClient import-rename churn is net-negative for cosmetic gain.

### Breaking changes shipped

*From phase 3 `composites`:*

- `92d74be26 docs(composites): strip @since tags + paraphrase KDoc per criterion 2`
  Mechanical: 201 `@since 0.0.1` tags removed across 44 commonMain files; ~400 lines of `@param`/`@property` paraphrase KDoc on `*Colors`/`*Dimens` data classes stripped. Action: none for consumers — KDoc-only changes.

- `beb58df2f refactor!(composites): tighten api/implementation split, drop 5 unused deps`
  - **5 unused deps dropped:** `kotlinx.serialization.json`, `cupertino`, `constraintlayout`, `coil`, `coil.compose`. Net: 21 declared deps → 16. Action for YallaClient: zero — these were never transitively reachable through composites's public surface (the audit confirmed zero imports).
  - **6 deps promoted to `api()`:** `projects.design`, `projects.resources`, `compose.runtime`, `compose.ui`, `compose.foundation`, `compose.material3`. Already in YallaClient's direct dep list, so transitive promotion is zero-impact.
  - `materialIconsExtended` stays `implementation` pending G1.

- `df4c4a66d refactor(composites): split HeaderableSheet.kt into 2 files`
  Non-breaking: `HeaderableSheetState` class + `rememberHeaderableSheetState` factory moved to a new `composites/sheet/HeaderableSheetState.kt` (193 lines); `HeaderableSheet.kt` 376 → 218 lines. Mirrors the existing `ExpandableSheet`/`ExpandableSheetState` precedent. Public API surface unchanged — both symbols are still in `uz.yalla.composites.sheet`.

- `e27982f95 refactor!(composites): A1+A11+A12+A13 — default copy, visibility, suppress`
  - **A13 (DatePickerSheet hardcoded copy).** `DatePickerSheetState` gained `title: String? = null`; both platform actuals (`DatePickerSheet.android.kt`, `DatePickerSheet.ios.kt`) now read `state.title` instead of the hardcoded `stringResource(Res.string.register_input_birthdate)`. Null hides the header row entirely. Action: every YallaClient `DatePickerSheet` caller must pass `state.title = stringResource(Res.string.<screen-title>)` (or `null`). Same posture as primitives wave-5 default-copy neutralization.
  - **A1 (SelectionSheet container color).** `SelectionSheetColors` data class introduced + `SelectionSheetDefaults.colors()` factory. The composable now takes a new `colors: SelectionSheetColors` param (defaulted) instead of hardcoding `System.color.background.base`. Action: callers wanting a non-default container can override; existing call sites are unaffected.
  - **A11 (Modifier.expandableSheetDraggable → internal).** Zero YallaClient consumers verified. The modifier is plumbing for `ExpandableSheet`'s root box; consumers receive the wired sheet, not the raw modifier.
  - **A12 (drop `@Suppress("FunctionName")` on DatePickerSheet expect/actual).** Composables ARE allowed PascalCase; the suppression and the "Kotlin compiler flags PascalCase…" comment were noise. No behavioral change.

### Architectural patterns documented (no code change, MODULE.md notes)

- **Coil — single-consumer absent.** Earlier revisions of `composites/AvatarCard` referenced Coil's `AsyncImage` in KDoc only (suggested-impl pattern); the actual `avatar` slot is `@Composable () -> Unit`, caller-driven. Coil deps were never functionally needed — dropped in wave 3.
- **Material3 `Button` use in ActionPickerSheet / AddressItem (G2).** Kept. Both are structural tappable-row shapes, not CTA buttons; primitives' `PrimaryButton`/`SecondaryButton` enforce CTA shape and would conflict. Document the carve-out.
- **`SnackbarController.sendData` (G3 RETRACTED).** The audit initially flagged it for deletion; cross-check found 8 `YallaClient/feature/home/.../HomeRoute.kt:357-413` call-sites. Kept as-is; the "redundant nullable wrapper" framing was wrong.
- **`Sheet.kt` internal `remember { mutableStateOf(...) }`.** Acceptable carve-out from the stateless-atom rule — ephemeral nested-scroll state the caller has no business observing. Same posture as primitives' `SensitiveButton.Animatable`.
- **`SnackbarController` is a process singleton.** Channel-based event bus; keeps semantics simple at the cost of test-isolation. `SnackbarControllerTest` (wave 8) drains the channel between cases. Refactor to injectable dep is a scope change beyond cleanup.

---

## Phase status

- Phase 2 `core` — done. Plan: [PHASE_2_CORE_PLAN.md](PHASE_2_CORE_PLAN.md). Audit: [CORE_AUDIT.md](CORE_AUDIT.md).
- Phase 2 `data` — done. Plan: [PHASE_2_DATA_PLAN.md](PHASE_2_DATA_PLAN.md). Audit: [DATA_AUDIT.md](DATA_AUDIT.md).
- Phase 3 `design` — done. Plan: [PHASE_3_DESIGN_PLAN.md](PHASE_3_DESIGN_PLAN.md). Audit: [DESIGN_AUDIT.md](DESIGN_AUDIT.md).
- Phase 3 `foundation` — done. Plan: [PHASE_3_FOUNDATION_PLAN.md](PHASE_3_FOUNDATION_PLAN.md). Audit: [FOUNDATION_AUDIT.md](FOUNDATION_AUDIT.md).
- Phase 3 `primitives` — done. Plan: [PHASE_3_PRIMITIVES_PLAN.md](PHASE_3_PRIMITIVES_PLAN.md). Audit: [PRIMITIVES_AUDIT.md](PRIMITIVES_AUDIT.md).
- Phase 3 `composites` — done. Plan: [PHASE_3_COMPOSITES_PLAN.md](PHASE_3_COMPOSITES_PLAN.md). Audit: [COMPOSITES_AUDIT.md](COMPOSITES_AUDIT.md).
- Phase 4 `firebase`, `maps`, `media`, `platform` — TODO.
