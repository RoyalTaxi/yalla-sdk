# Architecture

> How the SDK is structured, what each module does, and where new code should go.

## Module Dependency Graph

```
                    ┌──────────┐
                    │ resources │  (icons, strings, drawables)
                    └────┬─────┘
                         │
┌────────┐          ┌────┴─────┐
│ design │──────────│   core   │  (domain models, Either, contracts)
└────┬───┘          └────┬─────┘
     │                   │
     │              ┌────┴─────┐
     └──────────────│   data   │  (network, preferences, DataStore)
                    └────┬─────┘
                         │
                    ┌────┴──────────┐
                    │  foundation   │  (BaseViewModel, location, locale)
                    └────┬──────────┘
                         │
              ┌──────────┼──────────┐
              │          │          │
         ┌────┴─────┐   │   ┌──────┴─────┐
         │primitives │   │   │  platform  │  (native sheets, nav, switches)
         └────┬──────┘   │   └────────────┘
              │          │
         ┌────┴──────┐   │
         │composites │   │
         └───────────┘   │
                         │
          ┌──────────────┼──────────────┐
          │              │              │
     ┌────┴───┐    ┌────┴────┐   ┌─────┴─────┐
     │  maps  │    │  media  │   │  firebase  │
     └────────┘    └─────────┘   └───────────┘
```

**Rule**: Dependencies flow downward only. No module may depend on a peer or higher module.

## Module Responsibilities

### core
**Domain layer.** Pure Kotlin — no Android, iOS, or Compose dependencies.

Contains:
- `Either<D, E>` — result type for error handling
- `DataError` — sealed hierarchy of typed errors
- Domain models: `Order`, `Location`, `PaymentKind`, `GeoPoint`, `Profile`
- Contracts: `Preferences` interfaces, `LocationTracker`
- Utilities: date formatting, phone normalization

**When to add here**: New domain model, error type, or contract interface.

### data
**Infrastructure layer.** Network calls, local storage, DataStore.

Contains:
- `safeApiCall` — wraps HTTP calls into `Either`
- `HttpClientFactory` — Ktor client with plugins
- `*PreferencesImpl` — DataStore-backed preference storage
- `ApiResponse` / `ApiErrorResponse` — JSON envelopes

**When to add here**: New API call wrapper, preference, or data source.

### design
**Design tokens.** Colors, fonts, theme — nothing else.

Contains:
- `ColorScheme` — semantic color tokens (text, background, button, etc.)
- `FontScheme` — typography styles (title, body, custom)
- `YallaTheme` — Compose theme provider
- `System` — static accessor for current theme values

**When to add here**: New color token, font style, or theme variant.

### foundation
**Bridge layer.** Connects data to UI.

Contains:
- `BaseViewModel` — error handling, loading state
- `LoadingController` — smart loading indicator (delay + min display)
- `LocationManager` — reactive location tracking
- `ObserveAsEvents` — one-time event collection
- Settings: `ThemeOption`, `LanguageOption`, `MapOption`

**When to add here**: New ViewModel base, shared UI infrastructure.

### primitives
**Building-block components.** Buttons, fields, indicators, pins, top bars.

Every component follows the **Colors + Dimens + Defaults** pattern.
These are the atoms — they don't compose from other SDK components.

**When to add here**: New standalone UI element that doesn't depend on other SDK components.

### composites
**Composed components.** Cards, sheets, items, drawers, snackbar, views.

Built from primitives. Cards compose from `ContentCard`. Items compose from `ListItem`.
Sheets build on `Sheet` → `ExpandableSheet` → `HeaderableSheet`.

**When to add here**: New component that composes from primitives or other composites.

### platform
**Native wrappers.** expect/actual for platform-specific UI.

Contains:
- `NativeSheet` — bottom sheets with platform behavior
- `NativeNavHost` — navigation with Decompose
- `NativeSwitch`, `NativeWheelDatePicker` — native controls
- `SystemBarColors` — status bar styling
- `YallaPlatform` — initialization and config

**When to add here**: Anything that MUST differ between Android and iOS at the UI level.

### maps
**Map abstraction.** Google Maps and MapLibre behind a unified API.

Contains:
- `MapController` — camera, markers, polylines
- `GoogleMapProvider` / `LibreMapProvider` — runtime switching
- Compose-layer: `GoogleMap`, `Marker`, `Polyline`, `Circle`
- Layers: `RouteLayer`, `LocationsLayer`, `LocationIndicator`

**When to add here**: New map feature, overlay, or provider.

### media
**Camera and images.** Capture, pick, compress.

Contains:
- `YallaCamera` — live preview + capture
- `ImagePickerLauncher` — system photo picker
- `ImageCompressor` — binary-search quality compression
- `YallaGallery` — in-app gallery browser (experimental)

**When to add here**: New camera feature, image processing, or picker.

### firebase
**Firebase wrapper.** Analytics, crashlytics, messaging.

Contains:
- `YallaFirebase` — initialization entry point
- `YallaAnalytics` — event logging
- `YallaCrashlytics` — crash reporting
- `YallaMessaging` — push token management

**When to add here**: New Firebase integration.

## Key Design Principles

1. **Depend down, never up.** A module can only depend on modules below it in the graph.
2. **Either for errors.** Never raw try-catch for business logic. All fallible operations return `Either<D, E>`.
3. **Immutable state.** `val` over `var`. `@Immutable` on all Colors/Dimens classes.
4. **expect/actual for platform.** Common code defines the API, platform code implements it.
5. **Design tokens, not hardcoded values.** All colors from `System.color.*`, all fonts from `System.font.*`.
