# Module foundation

> ViewModel infrastructure, location, locale, settings — the brick glue between core and the UI brick stack.

## What this is

- **ViewModel infrastructure** (`infra/`): `BaseViewModel` with smart loading
  state via `LoadingController`, error-dialog flows, `safeScope` +
  `launchSafe`/`launchWithLoading` extensions, and a
  `protected open` `mapDataErrorToUserMessage` seam mapping every
  `DataError.Network.*` variant to a localized `StringResource`.
- **Location** (`location/`): `LocationManager` (the canonical
  `core.location.LocationProvider` implementation, wired by Koin),
  `LocationPermissionState`, `Location` / `FoundLocation` UI-ready
  models, `LocationMappers` extensions converting `core.*` types into
  foundation's UI shapes, and `expect`/`actual` glue for moko-geo
  (`createLocationTracker`) and platform settings deep-link
  (`openLocationSettings`).
- **Locale** (`locale/`): `expect fun changeLanguage(languageCode)` +
  `expect fun getCurrentLanguage()` — primitive-in/string-out platform
  bridge to the system locale.
- **Settings options** (`settings/`): `Selectable` interface +
  `OptionModel<T>` + the `LanguageOption`, `MapOption`, `ThemeOption`
  sealed hierarchies. Each Option carries a localized name resource and
  an icon, ready to feed a settings list UI.
- **Animation** (`animation/`): `Modifier.staggerReveal(visible, index)`
  for cascading-reveal layouts.

## What this is NOT

- **Not** a domain-types module (those live in `core` — `Either`,
  `DataError`, `OrderId`, etc.).
- **Not** a networking or persistence module (those live in `data`).
- **Not** a UI primitive layer (those live in `primitives` /
  `composites`).
- **Not** a feature module — no screens, no business orchestration, no
  `Service` repositories. Those compose on top of `BaseViewModel` in
  YallaClient.
- **Not** a state-machine framework — `BaseViewModel` is the
  cross-cutting infra layer, not an MVI base. See *Notes* on the
  Orbit dual-inheritance pattern.

## Usage

```kotlin
implementation("uz.yalla.sdk:foundation")
```

```kotlin
// Feature ViewModel: BaseViewModel for loading/error infra +
// Orbit ContainerHost for the state machine. Project pattern.
class HomeViewModel(
    private val orderRepo: OrderRepository,
) : BaseViewModel(),
    ContainerHost<HomeState, HomeEffect> {

    override val container = container<HomeState, HomeEffect>(HomeState())

    fun refresh() = intent {
        safeScope.launchWithLoading {
            orderRepo.list()
                .onSuccess { reduce { state.copy(orders = it) } }
                .onFailure { handleDataError(it) }
        }
    }
}
```

```kotlin
// LocationManager wired by Koin; consumed via core.location.LocationProvider:
val locationProvider: LocationProvider by inject()
locationProvider.startTracking()
locationProvider.currentLocation.collect { /* ... */ }
```

## Notes

- **`BaseViewModel` + Orbit dual-inheritance pattern.** Foundation's
  `BaseViewModel : ViewModel` is the cross-cutting infra layer (loading
  state, error dialog, `safeScope`). It is **not** custom MVI. The
  project pattern is to layer Orbit's `ContainerHost<State, Effect>` on
  top via dual-inheritance in feature view models — Orbit ships from
  YallaClient's deps, not foundation's. CLAUDE.md's "no custom MVI"
  rule reads as "no custom *state machine* MVI"; this isn't that.
- **`LocationManager` scope ownership (ADR-013 in spirit).** The
  caller owns `scope` — typically a process-lifetime
  `SupervisorJob + Dispatchers.Main`. Cancelling the scope stops every
  in-flight tracking operation. There is no `close()` method; the
  scope's lifecycle *is* the lifecycle.
- **Permission API carve-out.** `LocationManager.startTracking` /
  `stopTracking` use `runCatching { … }.onFailure { Logger.w(...) }`
  at the moko-geo seam — system-API boundary error handling for
  permission-denied or GPS-off failures. Pragmatic carve-out, not a
  criterion-11 try/catch violation. The same posture applies to
  moko-permissions APIs.
- **`LoadingController` `try { } finally { }`.** Resource-cleanup
  idiom (no `catch`), not error-swallowing. Ensures `_loading` is
  reset on any path, including cancellation. Sanctioned carve-out.
- **`LocationManager.DEFAULT_LOCATION = GeoPoint(41.2995, 69.2401)`.**
  Tashkent center — product-specific default for cold-start map camera
  initialization. Override per construction.
- **`LanguageOption` Phase-3 narrowing.** Currently exhaustive over
  `core.settings.LocaleKind` (Uzbek + Russian only). When
  `LocaleKind` grows, `LanguageOption.from(LocaleKind)`'s `when`
  must be updated.
- **`changeLanguage` Android limitation.** `Locale.setDefault` updates
  the JVM locale but does NOT trigger a Compose recomposition or refresh
  visible UI. The host app must recreate the Activity or call
  `AppCompatDelegate.setApplicationLocales` after invoking this.
  Documented on the `expect` declaration.
- **`LocationProvider` is in `core`, not `foundation`.** The
  `core.location.LocationProvider` interface is the canonical contract
  consumed by `:maps`. `LocationManager` implements it. A redundant
  Composable wrapper that previously lived in foundation was deleted in
  the phase-3 cleanup.
- **`LocationMappers` is domain↔domain, not DTO↔domain.** The
  extension functions adapt `core.*` types (`Address`, `AddressOption`,
  `SavedAddress`, `Order.Taxi.Route`) into foundation's UI-ready
  `Location` / `FoundLocation`. They stay in foundation because no
  network or storage is involved; CLAUDE.md's `internal object Mapper`
  rule applies to DTO seams only.

## Depends on

- `core` (api)
- `resources` (api — `StringResource` on every public `*Option.name`)
- `compose.runtime` (api — `@Composable` + `ProvidableCompositionLocal`)
- `compose.ui` (api — `Modifier` on `Modifier.staggerReveal`)
- `androidx.lifecycle.viewmodel` (api — `BaseViewModel : ViewModel`)
- `androidx.lifecycle.runtime.compose` (api — `Lifecycle.State` in
  `ObserveAsEvents`)
- `kotlinx.coroutines.core` (api — `StateFlow` / `CoroutineScope` in
  `LocationManager` + `BaseViewModel.safeScope`)
- `geo` (api — moko-geo `LocationTracker` is a public ctor param)
- `compose.animation`, `compose.components.resources`, `koin.core`,
  `kermit` (implementation — used internally only)
- `androidx.core.ktx`, `koin.android` (androidMain implementation)
- No SDK-internal dep beyond `core` and `resources`.
