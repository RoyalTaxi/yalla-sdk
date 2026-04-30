# Module maps

> Provider-agnostic Compose-Multiplatform map. MapLibre as the cross-platform default; Google Maps swappable at runtime via user preference.

## What this is

- **API contracts** (`api/`): `MapProvider`, `MapController`,
  `LiteMap`, `ExtendedMap`, `StaticMap`, `MapScope`. Provider-
  agnostic surface that feature modules consume. `MapController`
  exposes `cameraPosition: StateFlow<CameraPosition>`,
  `markerState: StateFlow<MarkerState>`, `isReady: StateFlow<Boolean>`.
- **Providers** (`provider/`):
  - `provider/google/` — Google Maps implementation
    (`GoogleMapProvider`, `GoogleMapController`, `GoogleLiteMap`,
    `GoogleExtendedMap`, `GoogleStaticMap`).
  - `provider/libre/` — MapLibre implementation
    (`LibreMapProvider`, `LibreMapController`, `LibreLiteMap`,
    `LibreExtendedMap`, `LibreStaticMap`).
  - `provider/SwitchingMapProvider.kt` /
    `provider/SwitchingMapController.kt` — runtime delegation:
    consumer tells us "Google" or "Libre" via Koin, we forward
    every call.
  - `provider/common/` — shared style constants, init state,
    camera/location effects, `UserLocationPainter`.
- **Compose layer** (`compose/`): expect/actual map composables —
  `GoogleMap`, `Marker`, `Polyline`, `Circle`,
  `BitmapDescriptor`, `CameraPositionState`, `MarkerState`. Render
  on Google Maps Android natively; iOS uses Apple Maps via cocoapods
  + `MapKit`/`GoogleMaps`. (Naming is historical — these are the
  Google-flavored composables. Libre composables live under
  `provider/libre/component/`.)
- **Models** (`model/` + `api/model/`): platform-neutral data
  classes — `LatLng`, `LatLngBounds`, `CameraPosition`, `MarkerState`,
  `MapType`, `MapProperties`, `MapUiSettings`, `Cap`, `JointType`,
  `MapStyle`, `MapCapabilities`. Two `CameraPosition` types exist
  intentionally: API-layer and compose-layer (different shape).
- **Config** (`config/` + `config/platform/`): `MapConstants`
  (default zoom, world bounding box, style URLs);
  `getPlatformGestures`, `getDisabledGestures`,
  `getPlatformOrnamentOptions`, `getPlatformRenderOptions` — MapLibre
  platform-specific defaults.
- **DI** (`di/`): `MapModule` Koin module + `MapDependencies`
  contract. Consumer wires their Google API key + map-style URL.
- **Util** (`util/`): geo math — coordinate conversions,
  `haversineDistance`, heading normalization, `BoundingBox`
  builders, `PaddingValues` arithmetic for camera padding.

## What this is NOT

- **Not** a tile server, not a routing engine, not a search engine.
  Tiles + style come from MapLibre's URL or Google's hosted
  service; routing comes from `:data`'s order-flow APIs; search is
  YallaClient's place-autocomplete service.
- **Not** an icon factory. Marker icons come from `:resources`'
  `YallaIcons` or caller-supplied `BitmapDescriptor`s.
- **Not** a feature module. No ViewModels, no business orchestration.
  The provider-agnostic API is consumed BY feature modules in
  YallaClient that wrap state into ride-specific domain models.

## Usage

```kotlin
implementation("uz.yalla.sdk:maps")
```

```kotlin
// One-time DI install with consumer-supplied API keys:
startKoin {
    modules(
        MapModule(
            googleApiKey = BuildConfig.GOOGLE_MAPS_KEY,
            libreStyleUrl = BuildConfig.LIBRE_STYLE_URL,
        )
    )
}

// In a screen:
@Composable
fun RideScreen() {
    val provider: MapProvider = koinInject()
    val controller = remember { provider.createController() }

    LaunchedEffect(Unit) { controller.bind(this) }

    provider.LiteMap(
        controller = controller,
        modifier = Modifier.fillMaxSize(),
    ) {
        // map scope — markers, polylines, circles
    }
}
```

## Notes

- **Two map providers, runtime switch.** `SwitchingMapProvider`
  reads the user's preference (Koin-injected) and forwards every
  call to the matching `GoogleMapProvider` or `LibreMapProvider`.
  Adding a third provider means: one new `MapProvider` subclass,
  one new `MapController` subclass, register in `MapModule`. All
  composables are expect/actual so adding a third platform-renderer
  is a parallel exercise.
- **`MapController` is `StateFlow`-driven.** Camera and marker
  state expose `StateFlow<T>` so feature ViewModels can `collectAsState`
  in Compose without juggling listeners. `kotlinx.coroutines.core`
  is therefore an `api()` dep — wave D promoted it.
- **God-files in providers are the natural shape.**
  `LibreMapController.kt` and `GoogleMapController.kt` are 400-455
  lines each because they wrap each provider's full
  camera/marker/animation API surface. Splitting them by extension-
  function file would just push complexity around. Treat the file
  size as a signal to design carefully, not as a violation. Same
  applies to `MapControllerFakeProviderTest.kt` (616 lines) — a
  comprehensive fake-and-its-verification needs to be in one place.
- **Marker sync suppression flags.** `LibreMapController` and
  `GoogleMapController` both carry `suppressMarkerSyncUntilIdle` +
  `skipNextIdleMarkerSync` boolean flags coordinating padding-only
  camera moves. The flags ensure the marker doesn't visually jump
  when only the camera padding (i.e. the visual center) changes
  programmatically. Touch them carefully — the audit found this
  is the trickiest invariant in the module.
- **iOS test link is broken (pre-existing).** `:maps:linkDebugTestIosSimulatorArm64`
  fails on Apple Maps cocoapods symbol resolution. Verified
  pre-existing on the pre-cleanup baseline (wave-D commit message
  has the `git stash` repro). Tests on iOS remain unrunnable until
  the cocoapods linker config is fixed; out of scope for cleanup.
  Android compile + Android-only tests cover the cross-platform
  abstraction adequately for now.
- **`design` and `resources` were dropped in wave D.** Map
  composables don't read theme tokens directly — caller-supplied
  marker images / polyline colors come through the API surface.
  If a future map composable wants `System.color.*` it should pull
  the dep back in via the consumer's binding, not transitively.

## Depends on

- `core` (api — `GeoPoint` + address types appear in
  `MapController.bind`/`fitBounds`/`markerState`)
- `compose.runtime` (api — `@Composable`)
- `compose.ui` (api — `Modifier` on every Yalla*Map composable)
- `compose.foundation` (api — slot composables in public surface)
- `compose.material3` (implementation — internal-only)
- `compose.components.resources` (implementation — internal resource
  loading)
- `kotlinx.coroutines.core` (api —
  `StateFlow<CameraPosition>`/`<MarkerState>`/`<Boolean>` on
  `MapController`)
- `geo` (api — moko-geo types in public params)
- `geo.compose` (implementation — internal `LocationProvider`
  bridging)
- `maplibre.compose` (api — `LibreMapController.bind` takes
  `org.maplibre.compose.camera.CameraState` directly)
- `koin.core`, `koin.compose`, `androidx.lifecycle.runtime.compose`
  (implementation — internal-only)
- `androidx.core.ktx`, `google.maps.compose`, `play.services.maps`
  (androidMain implementation)
- `kotlinx.coroutines.test` (commonTest)
- No SDK-internal dep beyond `core`.
