# Foundation Module — Full Overhaul Design

**Date:** 2026-03-17
**Module:** `foundation` (yalla-sdk)
**Approach:** Restructure + Full Quality (Approach C)

## Context

Foundation module is the glue layer between `core` (types/contracts) and UI modules (`primitives`, `composites`). It provides ViewModel infrastructure, location management, locale handling, and reactive utilities.

Critical review found **19 issues**: 1 critical (deadlock in LoadingController), 2 high (dead code), 10 documentation gaps, 6 code quality problems.

**SDK context:** 2 apps (Rider + Driver) share this SDK. Foundation stays as one module — an intentional glue layer with clear internal separation between architecture infrastructure and domain logic.

---

## 1. Package Reorganization

### Current Structure (6 packages, 20 common files)
```
foundation/
├── viewmodel/     → BaseViewModel, LoadingController, DataErrorMapper, DefaultDataErrorMapper, ViewModelExtensions
├── reactive/      → ObserveAsEvents, ObserveLocale
├── location/      → LocationManager, LocationProvider, LocationProviderAdapter, LocationState, LocationTrackerFactory
├── locale/        → ChangeLanguage, LocaleProvider
├── model/         → SelectableItemModel, MapModel, ThemeModel, LanguageModel, Location
└── util/          → LocationModelExtensions
```

### New Structure (4 packages, 18–19 common files)
```
foundation/
├── infra/                       ← Architecture infrastructure (universal, Yalla-agnostic)
│   ├── BaseViewModel.kt
│   ├── LoadingController.kt
│   ├── LoadingExtensions.kt     ← from ViewModelExtensions.kt (only if CoroutineScope ext is used)
│   ├── DataErrorMapper.kt
│   ├── DefaultDataErrorMapper.kt
│   └── ObserveAsEvents.kt       ← from reactive/
│
├── location/                    ← Location tracking (package path unchanged)
│   ├── LocationManager.kt
│   ├── LocationProvider.kt
│   ├── LocationState.kt
│   └── LocationTrackerFactory.kt
│
├── locale/                      ← Language management
│   ├── ChangeLanguage.kt
│   ├── LocaleProvider.kt
│   └── ObserveLocale.kt         ← from reactive/
│
└── model/                       ← UI-ready domain models
    ├── SelectableItemModel.kt
    ├── MapModel.kt
    ├── ThemeModel.kt
    ├── LanguageModel.kt
    ├── Location.kt
    └── LocationModelExtensions.kt  ← from util/
```

### Changes Summary
| Action | File | From → To |
|--------|------|-----------|
| MOVE | ObserveAsEvents.kt | `reactive/` → `infra/` |
| MOVE | ObserveLocale.kt | `reactive/` → `locale/` |
| MOVE | LocationModelExtensions.kt | `util/` → `model/` |
| RENAME pkg | viewmodel/ files | `viewmodel/` → `infra/` |
| DELETE | LocationProviderAdapter.kt | Dead code — LocationManager implements LocationProvider directly |
| MERGE+DELETE | ViewModelExtensions.kt | CoroutineScope extension → `infra/LoadingExtensions.kt`; ViewModel extension removed (BaseViewModel covers it) |
| DELETE pkg | `reactive/` | Empty after moves |
| DELETE pkg | `util/` | Empty after moves |

---

## 2. Critical Bug Fix — LoadingController Deadlock

**File:** `infra/LoadingController.kt`
**Issue:** `delay(remaining)` inside `mutex.withLock` blocks other coroutines from acquiring the mutex during the delay period.

### Fix
Extract delay computation from mutex, execute delay outside lock. Use a generation counter to safely guard the second mutex block against interleaving operations:

```kotlin
// New field in LoadingController:
private var generation = 0L

// In withLoading(), replace the finally block:
try {
    block()
} finally {
    // Phase 1: compute delay and capture generation inside mutex
    val (remainingDelay, gen) = mutex.withLock {
        activeOperations--
        if (activeOperations == 0) {
            localShowJob?.cancel()
            showJob?.cancel()
            showJob = null
            val delay = visibleSince?.elapsedNow()?.let { elapsed ->
                val remaining = minDisplayTime - elapsed
                if (remaining.isPositive()) remaining else null
            }
            Pair(delay, ++generation)
        } else {
            Pair(null, generation)
        }
    }

    // Phase 2: delay OUTSIDE the mutex (no deadlock)
    remainingDelay?.let { delay(it) }

    // Phase 3: cleanup only if no other operation has modified state
    mutex.withLock {
        if (activeOperations == 0 && generation == gen) {
            _loading.value = false
            visibleSince = null
        }
    }
}
```

Note: `localShowJob` is captured from the enclosing `coroutineScope` block (see current code lines 76–95). The full `coroutineScope` structure remains unchanged — only the `finally` block is modified.

### Why generation counter

Between phase 1 and phase 3, a new operation could start and finish, modifying `activeOperations` and `visibleSince`. The generation counter ensures phase 3 only cleans up if it is still the "last writer" — if another operation has modified state since, `generation != gen` and we skip, letting the latest operation handle cleanup.

---

## 3. Dead Code Removal

### 3a. LocationProviderAdapter — DELETE
- `LocationManager` already implements `LocationProvider` interface (line 58)
- `LocationProviderAdapter` is a pure pass-through proxy — adds nothing
- Not imported anywhere in SDK (grep confirmed)
- **Before deletion:** verify YallaClient and Driver app Koin modules do not bind `LocationProviderAdapter` as `LocationProvider` implementation

### 3b. BaseViewModel.failure Channel — DELETE
```kotlin
// Remove these 2 lines:
private val _failure = Channel<Int>(Channel.UNLIMITED)
val failure: Flow<Int> = _failure.receiveAsFlow()
```
- Never consumed anywhere in SDK
- Untyped `Int` — meaningless API

### 3c. ViewModelExtensions.kt — DELETE
- `ViewModel.launchWithLoading(loadingController, block)` → redundant with `BaseViewModel.safeScope.launchWithLoading()`
- `CoroutineScope.launchWithLoading(loadingController, block)` → takes an **explicit** `LoadingController` parameter (unlike BaseViewModel's member extension which uses its internal controller). This is a different API for non-BaseViewModel contexts. Verify usage in SDK and client apps during implementation. If used, keep as `infra/LoadingExtensions.kt`. If unused, delete entirely.
- Delete ViewModelExtensions.kt file

---

## 4. API & Naming Fixes

### 4a. ThemeModel — PascalCase
| Before | After |
|--------|-------|
| `data object LIGHT` | `data object Light` |
| `data object DARK` | `data object Dark` |
| `data object SYSTEM` | `data object System` |
| `ThemeModel.THEMES` | `ThemeModel.all` |

Note: `fromThemeKind()` when-branches automatically update with the renamed data objects.

### 4b. LanguageModel — Rename collection
| Before | After |
|--------|-------|
| `LanguageModel.LANGUAGES` | `LanguageModel.all` |

Note: `all` keeps only `Uzbek` and `Russian` (UzbekCyrillic and English are defined but not production-ready). Documented in KDoc.

### 4c. MapModel — Rename collection
| Before | After |
|--------|-------|
| `MapModel.MAPS` | `MapModel.all` |

Note: Using `all` instead of `entries` for all companion collections to avoid confusion with Kotlin's `Enum.entries` property.

### 4d. Function Renames
| Before | After | Reason |
|--------|-------|--------|
| `rememberLocationManager()` | `currentLocationManager()` | Doesn't use `remember{}`; just reads CompositionLocal |
| `rememberLocaleState()` | `currentLocaleState()` | Doesn't use `remember{}`; just reads CompositionLocal |
| `rememberCurrentLanguage()` | `currentLocale()` | Returns `LocaleKind`, not a language string — `currentLocale()` is more accurate. Also avoids confusion with `getCurrentLanguage(): String` in the same `locale/` package. `LaunchedEffect(Unit)` never re-fires, so `current` prefix is more honest than `remember` |

### 4e. LocationManager Encapsulation
- `val locationTracker: LocationTracker` → `private val locationTracker: LocationTracker`
- Stops leaking moko-geo through public API

### 4f. LocationManager Error Logging
Add Kermit logging in `runCatching.onFailure` blocks:
```kotlin
.onFailure { e ->
    _isTracking.value = false
    Logger.w("LocationManager") { "startTracking failed: ${e.message}" }
}
```

### 4g. LocationTrackerFactory.android — Fix Koin Anti-pattern
```kotlin
// Before (anonymous KoinComponent hack):
actual fun createLocationTracker(): LocationTracker =
    object : KoinComponent { val context: Context by inject() }.run { ... }

// After (clean Koin access):
actual fun createLocationTracker(): LocationTracker {
    val context: Context = GlobalContext.get().get()
    return LocationTracker(permissionsController = PermissionsController(applicationContext = context))
}
```

### 4h. ObserveAsEvents — Fix Deprecated API
Replace `LocalLifecycleOwner.current` with non-deprecated import from `androidx.lifecycle.compose`.

### 4i. ChangeLanguage.ios.kt
Remove unnecessary `synchronize()` call.

---

## 5. New Dependency — Kermit Logging

### 5a. Version catalog (`gradle/libs.versions.toml`)
```toml
[versions]
kermit = "2.0.4"  # verify latest stable at implementation time

[libraries]
kermit = { module = "co.touchlab:kermit", version.ref = "kermit" }
```

### 5b. Core module (`core/build.gradle.kts`)
```kotlin
commonMain.dependencies {
    api(libs.kermit)  // api so all modules can use Logger without re-declaring
}
```

All modules depend on `core` → SDK-wide logging available everywhere.

### 5c. Usage in foundation
```kotlin
import co.touchlab.kermit.Logger

// In LocationManager:
Logger.w("LocationManager") { "startTracking failed: ${e.message}" }
```

**Future:** Migrate `YallaFirebaseLogger` in firebase module to use Kermit as backend.

---

## 6. Documentation Overhaul

### 6a. MODULE.md (new file)
```markdown
# Module foundation

Core types and UI layer bridge — ViewModel infrastructure, location management,
locale handling, and UI-ready domain models for the Yalla SDK.

## Architecture

Foundation serves as the intentional glue layer between `core` (pure types/contracts)
and UI modules (`primitives`, `composites`). It contains two categories:

- **Infrastructure** (`infra`): Universal architecture utilities — ViewModel, loading, events
- **Domain** (`location`, `locale`, `model`): Yalla-specific implementations and UI-ready models

# Package uz.yalla.foundation.infra
Base ViewModel with loading state management, error handling, and lifecycle-aware event observation.

# Package uz.yalla.foundation.location
Device location tracking with reactive updates, permission state, and CompositionLocal integration.

# Package uz.yalla.foundation.locale
App language management with platform-specific implementations and CompositionLocal state propagation.

# Package uz.yalla.foundation.model
UI-ready domain models for app settings (theme, language, map provider) and location data.
```

### 6b. `@since 0.0.1` on ALL public types and functions

### 6c. File-specific KDoc additions
| File | Action |
|------|--------|
| DefaultDataErrorMapper | Add class-level KDoc |
| LocationTrackerFactory | Add KDoc on expect fun with platform behavior docs |
| LocationModelExtensions | Add KDoc to all 5 extension functions |
| MapModel, ThemeModel, LanguageModel | Add class-level KDoc + `@property` tags |
| Location.kt (`Location`, `FoundLocation`) | Add class-level KDoc on both, `@property` tags, document `toLocation()` address drop |
| ChangeLanguage.android.kt | Add limitation warning in KDoc |
| ObserveLocale.kt | Document single-fire limitation |

---

## 7. Breaking Changes Impact

These changes will break imports in YallaClient and potentially Driver app:

| Change | Impact |
|--------|--------|
| `uz.yalla.foundation.viewmodel.*` → `uz.yalla.foundation.infra.*` | All ViewModel imports |
| `uz.yalla.foundation.reactive.ObserveAsEvents` → `uz.yalla.foundation.infra.ObserveAsEvents` | Event observation imports |
| `uz.yalla.foundation.reactive.rememberCurrentLanguage` → `uz.yalla.foundation.locale.currentLocale` | Locale observation imports (file also moves from `reactive/` to `locale/`) |
| `uz.yalla.foundation.util.*` → `uz.yalla.foundation.model.*` | Location extension imports |
| `ThemeModel.LIGHT/DARK/SYSTEM` → `.Light/.Dark/.System` | Settings screen references |
| `ThemeModel.THEMES` → `ThemeModel.all` | Theme list references |
| `LanguageModel.LANGUAGES` → `LanguageModel.all` | Language picker references |
| `MapModel.MAPS` → `MapModel.all` | Map picker references |
| `rememberLocationManager()` → `currentLocationManager()` | Map screens |
| `rememberLocaleState()` → `currentLocaleState()` | Locale provider setup |
| `rememberCurrentLanguage()` → `currentLocale()` | Locale observation |
| `LocationManager.locationTracker` → private | Direct tracker access (if used) |
| `LocationProviderAdapter` removed | DI module bindings (if used) |
| `BaseViewModel.failure` removed | Any failure observers (if used) |

**Mitigation:** All changes are in alpha SDK. Search-and-replace in client apps.

---

## 8. Files Summary

### Modified (14 files)
- `infra/BaseViewModel.kt` — remove failure channel, add KDoc
- `infra/LoadingController.kt` — fix deadlock
- `infra/DataErrorMapper.kt` — add `@since`
- `infra/DefaultDataErrorMapper.kt` — add class KDoc
- `infra/ObserveAsEvents.kt` — fix deprecated API
- `location/LocationManager.kt` — private locationTracker, add Kermit logging
- `location/LocationProvider.kt` — rename `rememberLocationManager()` → `currentLocationManager()`
- `location/LocationTrackerFactory.android.kt` — fix Koin anti-pattern
- `locale/ObserveLocale.kt` — rename `rememberCurrentLanguage()` → `currentLocale()`
- `locale/LocaleProvider.kt` — rename `rememberLocaleState()` → `currentLocaleState()`
- `locale/ChangeLanguage.ios.kt` — remove `synchronize()`
- `model/ThemeModel.kt` — PascalCase rename, `THEMES` → `all`
- `model/LanguageModel.kt` — `LANGUAGES` → `all`
- `model/MapModel.kt` — `MAPS` → `all`

### External modified (2 files)
- `core/build.gradle.kts` — add Kermit dependency
- `gradle/libs.versions.toml` — add Kermit version entry

### Deleted (2 files)
- `location/LocationProviderAdapter.kt` — dead code
- `viewmodel/ViewModelExtensions.kt` — redundant (CoroutineScope ext kept only if used)

### Created (1-2 files)
- `foundation/MODULE.md` — Dokka package descriptions
- `infra/LoadingExtensions.kt` — only if `CoroutineScope.launchWithLoading()` is used

### KDoc pass (all 20 common files)
All public types and functions get `@since 0.0.1` tags + file-specific KDoc additions per Section 6.

### Package changes
- `viewmodel/` → `infra/` (rename)
- `reactive/` → dissolved (files moved to `infra/` and `locale/`)
- `util/` → dissolved (file moved to `model/`)
