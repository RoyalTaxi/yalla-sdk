# Module foundation

Core types and UI layer bridge — ViewModel infrastructure, location management,
locale handling, and UI-ready domain models for the Yalla SDK.

## Architecture

Foundation serves as the intentional glue layer between `core` (pure types/contracts)
and UI modules (`primitives`, `composites`). It contains two categories:

- **Infrastructure** (`infra`): Universal architecture utilities — ViewModel, loading, events
- **Domain** (`location`, `locale`, `settings`): Yalla-specific implementations and UI-ready models

# Package uz.yalla.foundation.infra
Base ViewModel with loading state management, error handling, and lifecycle-aware event observation.

# Package uz.yalla.foundation.location
Device location tracking with reactive updates, permission state, location models, and CompositionLocal integration.

# Package uz.yalla.foundation.locale
App language management with platform-specific implementations and CompositionLocal state propagation.

# Package uz.yalla.foundation.settings
Settings option models (theme, language, map provider) with shared [Selectable] contract and selection list support.

## Depends on

- `core` (api)
- `resources` (api)
- `compose.runtime` (api — `@Composable` + `ProvidableCompositionLocal`)
- `compose.ui` (api — `Modifier` on `Modifier.staggerReveal`)
- `androidx.lifecycle.viewmodel` (api — `BaseViewModel : ViewModel`)
- `androidx.lifecycle.runtime.compose` (api — `Lifecycle.State` in `ObserveAsEvents`)
- `kotlinx.coroutines.core` (api — `StateFlow`/`CoroutineScope` in `LocationManager`)
- `geo` (api — moko-geo `LocationTracker` is a public ctor param)
- `compose.animation`, `compose.components.resources`, `koin.core`, `kermit` (implementation — used internally only)
- `androidx.core.ktx`, `koin.android` (androidMain implementation)
- No SDK-internal dep beyond `core` and `resources`.
