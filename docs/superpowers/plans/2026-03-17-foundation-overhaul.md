# Foundation Module Overhaul — Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Bring foundation module to gold-standard quality — fix critical deadlock, remove dead code, reorganize packages, clean APIs, add logging, complete KDoc.

**Architecture:** Foundation is the glue layer between `core` (types/contracts) and UI modules (`primitives`, `composites`). Package reorganization consolidates 6 packages into 4: `infra/` (universal architecture), `location/`, `locale/`, `model/`. No external module structure changes.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, Koin DI, moko-geo, Kermit logging, Coroutines/Flow

**Spec:** `docs/superpowers/specs/2026-03-17-foundation-overhaul-design.md`

---

## File Structure

### Files to create
- `foundation/MODULE.md` — Dokka package descriptions

### Files to delete
- `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationProviderAdapter.kt` — dead code
- `foundation/src/commonMain/kotlin/uz/yalla/foundation/viewmodel/ViewModelExtensions.kt` — redundant (`CoroutineScope.launchWithLoading()` not used in SDK; if used in client apps, keep as `infra/LoadingExtensions.kt` instead — see Task 5 Step 2)

### Files to move (package rename via new file + delete old)
| File | From | To |
|------|------|----|
| BaseViewModel.kt | `viewmodel/` | `infra/` |
| LoadingController.kt | `viewmodel/` | `infra/` |
| DataErrorMapper.kt | `viewmodel/` | `infra/` |
| DefaultDataErrorMapper.kt | `viewmodel/` | `infra/` |
| ObserveAsEvents.kt | `reactive/` | `infra/` |
| ObserveLocale.kt | `reactive/` | `locale/` |
| LocationModelExtensions.kt | `util/` | `model/` |

### Files to modify in-place (no package change)
- `location/LocationManager.kt` — private locationTracker, Kermit logging
- `location/LocationProvider.kt` — rename function
- `location/LocationTrackerFactory.android.kt` — fix Koin pattern
- `locale/LocaleProvider.kt` — rename function
- `locale/ChangeLanguage.ios.kt` — remove synchronize()
- `model/ThemeModel.kt` — PascalCase + `all`
- `model/LanguageModel.kt` — `all`
- `model/MapModel.kt` — `all`
- `model/Location.kt` — KDoc only
- `model/SelectableItemModel.kt` — KDoc only
- `location/LocationState.kt` — KDoc only
- `location/LocationTrackerFactory.kt` — KDoc only
- `locale/ChangeLanguage.kt` — KDoc only
- `locale/ChangeLanguage.android.kt` — KDoc only

### External files to modify
- `gradle/libs.versions.toml` — add Kermit version + library entry
- `core/build.gradle.kts` — add Kermit `api` dependency

---

## Chunk 1: Setup & Package Reorganization

### Task 1: Add Kermit Dependency

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `core/build.gradle.kts`

- [ ] **Step 1: Add Kermit to version catalog**

In `gradle/libs.versions.toml`, add to `[versions]` section (after `orbit = "10.0.0"` line 34):
```toml
kermit = "2.0.4"
```

In `[libraries]` section (after orbit-compose line 128), add:
```toml
kermit = { module = "co.touchlab:kermit", version.ref = "kermit" }
```

- [ ] **Step 2: Add Kermit to core module**

In `core/build.gradle.kts`, add to `commonMain.dependencies` block:
```kotlin
commonMain.dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    api(libs.kotlinx.datetime)
    api(libs.kermit)
}
```

- [ ] **Step 3: Verify core builds**

Run: `./gradlew :core:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add gradle/libs.versions.toml core/build.gradle.kts
git commit -m "chore(core): add Kermit logging dependency"
```

---

### Task 2: Package Reorganization — viewmodel/ → infra/

This is the biggest structural change. Move 4 files from `viewmodel/` to `infra/`, updating package declarations. ViewModelExtensions.kt is not moved — it will be deleted in Task 5.

**Base path:** `foundation/src/commonMain/kotlin/uz/yalla/foundation/`

- [ ] **Step 1: Create infra/ directory**

```bash
mkdir -p foundation/src/commonMain/kotlin/uz/yalla/foundation/infra
```

- [ ] **Step 2: Move BaseViewModel.kt to infra/**

Copy file to new location and update package declaration from `uz.yalla.foundation.viewmodel` → `uz.yalla.foundation.infra`. All imports stay the same (they reference external packages: `androidx.lifecycle`, `kotlinx.coroutines`, `uz.yalla.core`, `uz.yalla.resources`). Internal references to `LoadingController`, `DataErrorMapper`, `DefaultDataErrorMapper` stay in the same package — no import changes.

Delete original: `viewmodel/BaseViewModel.kt`

- [ ] **Step 3: Move LoadingController.kt to infra/**

Update package: `uz.yalla.foundation.viewmodel` → `uz.yalla.foundation.infra`. No import changes (all external).

Delete original: `viewmodel/LoadingController.kt`

- [ ] **Step 4: Move DataErrorMapper.kt to infra/**

Update package: `uz.yalla.foundation.viewmodel` → `uz.yalla.foundation.infra`. No import changes.

Delete original: `viewmodel/DataErrorMapper.kt`

- [ ] **Step 5: Move DefaultDataErrorMapper.kt to infra/**

Update package: `uz.yalla.foundation.viewmodel` → `uz.yalla.foundation.infra`. No import changes (DataErrorMapper is in same new package).

Delete original: `viewmodel/DefaultDataErrorMapper.kt`

- [ ] **Step 6: Verify infra/ has 4 files, viewmodel/ has only ViewModelExtensions.kt left**

```bash
ls foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/
ls foundation/src/commonMain/kotlin/uz/yalla/foundation/viewmodel/
```

Expected infra/: BaseViewModel.kt, DataErrorMapper.kt, DefaultDataErrorMapper.kt, LoadingController.kt
Expected viewmodel/: ViewModelExtensions.kt (will be deleted in Task 5)

---

### Task 3: Package Reorganization — reactive/ dissolved

Move ObserveAsEvents.kt → infra/ and ObserveLocale.kt → locale/.

- [ ] **Step 1: Move ObserveAsEvents.kt to infra/**

Update package: `uz.yalla.foundation.reactive` → `uz.yalla.foundation.infra`. No import changes (all external: `androidx.compose`, `androidx.lifecycle`, `kotlinx.coroutines.flow`).

Delete original: `reactive/ObserveAsEvents.kt`

- [ ] **Step 2: Move ObserveLocale.kt to locale/**

Update package: `uz.yalla.foundation.reactive` → `uz.yalla.foundation.locale`. Update the import from `uz.yalla.foundation.locale.getCurrentLanguage` — this stays the same since it's now in the same package, so **remove the import** (same-package access).

Delete original: `reactive/ObserveLocale.kt`

- [ ] **Step 3: Delete empty reactive/ directory**

```bash
rmdir foundation/src/commonMain/kotlin/uz/yalla/foundation/reactive
```

---

### Task 4: Package Reorganization — util/ dissolved

- [ ] **Step 1: Move LocationModelExtensions.kt to model/**

Update package: `uz.yalla.foundation.util` → `uz.yalla.foundation.model`. Remove the two now-same-package imports:
```kotlin
// REMOVE these (now same package):
import uz.yalla.foundation.model.FoundLocation
import uz.yalla.foundation.model.Location
```

Delete original: `util/LocationModelExtensions.kt`

- [ ] **Step 2: Delete empty util/ directory**

```bash
rmdir foundation/src/commonMain/kotlin/uz/yalla/foundation/util
```

- [ ] **Step 3: Verify full new structure**

```bash
find foundation/src/commonMain/kotlin/uz/yalla/foundation -name "*.kt" | sort
```

Expected 20 files (deletions happen in Task 5):
- `infra/`: BaseViewModel.kt, DataErrorMapper.kt, DefaultDataErrorMapper.kt, LoadingController.kt, ObserveAsEvents.kt
- `locale/`: ChangeLanguage.kt, LocaleProvider.kt, ObserveLocale.kt
- `location/`: LocationManager.kt, LocationProvider.kt, LocationProviderAdapter.kt, LocationState.kt, LocationTrackerFactory.kt
- `model/`: LanguageModel.kt, Location.kt, LocationModelExtensions.kt, MapModel.kt, SelectableItemModel.kt, ThemeModel.kt
- `viewmodel/`: ViewModelExtensions.kt (to be deleted next)

- [ ] **Step 4: Build verification**

Run: `./gradlew :foundation:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add -A foundation/src/commonMain/kotlin/uz/yalla/foundation/
git commit -m "refactor(foundation): reorganize packages — viewmodel→infra, dissolve reactive/ and util/"
```

---

## Chunk 2: Bug Fixes, Dead Code Removal & API Fixes

### Task 5: Dead Code Removal

**Files:**
- Delete: `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationProviderAdapter.kt`
- Delete: `foundation/src/commonMain/kotlin/uz/yalla/foundation/viewmodel/ViewModelExtensions.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/BaseViewModel.kt`

- [ ] **Step 1: Delete LocationProviderAdapter.kt**

Verify no imports exist in SDK:
```bash
grep -r "LocationProviderAdapter" --include="*.kt" foundation/ composites/ primitives/ maps/ media/ firebase/ platform/ design/
```
Expected: Only the file definition itself. Delete it.

- [ ] **Step 2: Verify ViewModelExtensions usage in client apps**

The spec requires verifying client apps before deletion. `CoroutineScope.launchWithLoading()` is NOT used within the SDK (grep confirmed), but may be used in YallaClient or Driver:

```bash
# Check SDK (already confirmed clean):
grep -r "launchWithLoading" --include="*.kt" .
# Check client apps if accessible:
grep -r "CoroutineScope.*launchWithLoading\|import.*ViewModelExtensions" --include="*.kt" /Users/macbookpro/Ildam/yalla/YallaClient/ 2>/dev/null || echo "YallaClient not found or no matches"
```

**If used in client apps:** Keep `CoroutineScope.launchWithLoading()` as `infra/LoadingExtensions.kt` (move file, update package, remove only the `ViewModel.launchWithLoading()` extension).
**If unused (expected):** Delete entirely.

- [ ] **Step 3: Delete ViewModelExtensions.kt and viewmodel/ directory**

```bash
rm foundation/src/commonMain/kotlin/uz/yalla/foundation/viewmodel/ViewModelExtensions.kt
rmdir foundation/src/commonMain/kotlin/uz/yalla/foundation/viewmodel
```

- [ ] **Step 4: Remove dead failure channel from BaseViewModel.kt**

In `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/BaseViewModel.kt`, remove lines 57-60:
```kotlin
// DELETE these 4 lines:
private val _failure = Channel<Int>(Channel.UNLIMITED)

/** Flow of failure codes for external observation. */
val failure: Flow<Int> = _failure.receiveAsFlow()
```

Also remove now-unused imports:
```kotlin
// DELETE these imports:
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
```

- [ ] **Step 5: Build verification**

Run: `./gradlew :foundation:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "refactor(foundation): remove dead code — LocationProviderAdapter, failure channel, ViewModelExtensions"
```

---

### Task 6: Critical Bug Fix — LoadingController Deadlock

**Files:**
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/LoadingController.kt`

- [ ] **Step 1: Add generation counter field**

After line 57 (`private var visibleSince: TimeSource.Monotonic.ValueTimeMark? = null`), add:
```kotlin
private var generation = 0L
```

- [ ] **Step 2: Replace the finally block in withLoading()**

Replace the current `finally` block (lines 99-118):
```kotlin
// CURRENT (deadlock):
finally {
    mutex.withLock {
        activeOperations--
        if (activeOperations == 0) {
            localShowJob?.cancel()
            showJob?.cancel()
            showJob = null

            visibleSince?.let { mark ->
                val elapsed = mark.elapsedNow()
                val remaining = minDisplayTime - elapsed
                if (remaining.isPositive()) {
                    delay(remaining)
                }
            }

            _loading.value = false
            visibleSince = null
        }
    }
}
```

With:
```kotlin
finally {
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
    remainingDelay?.let { delay(it) }
    mutex.withLock {
        if (activeOperations == 0 && generation == gen) {
            _loading.value = false
            visibleSince = null
        }
    }
}
```

- [ ] **Step 3: Build verification**

Run: `./gradlew :foundation:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/LoadingController.kt
git commit -m "fix(foundation): resolve LoadingController deadlock — move delay outside mutex with generation counter"
```

---

### Task 7: API & Naming Fixes — Models

**Files:**
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/model/ThemeModel.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/model/LanguageModel.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/model/MapModel.kt`

- [ ] **Step 1: ThemeModel — PascalCase rename**

In `ThemeModel.kt`:
- `data object LIGHT` → `data object Light`
- `data object DARK` → `data object Dark`
- `data object SYSTEM` → `data object System`
- `val THEMES = listOf(LIGHT, DARK, SYSTEM)` → `val all = listOf(Light, Dark, System)`
- `fromThemeKind`: `ThemeKind.Light -> LIGHT` → `ThemeKind.Light -> Light`, etc.

- [ ] **Step 2: LanguageModel — Rename collection**

In `LanguageModel.kt`:
- `val LANGUAGES = listOf(Uzbek, Russian)` → `val all = listOf(Uzbek, Russian)`

- [ ] **Step 3: MapModel — Rename collection**

In `MapModel.kt`:
- `val MAPS = listOf(Google, Libre)` → `val all = listOf(Google, Libre)`

- [ ] **Step 4: Build verification**

Run: `./gradlew :foundation:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add foundation/src/commonMain/kotlin/uz/yalla/foundation/model/
git commit -m "refactor(foundation): PascalCase ThemeModel objects, rename companion collections to 'all'"
```

---

### Task 8: API & Naming Fixes — Function Renames

**Files:**
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationProvider.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/LocaleProvider.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/ObserveLocale.kt`

- [ ] **Step 1: Rename rememberLocationManager()**

In `location/LocationProvider.kt` line 49:
```kotlin
// BEFORE:
fun rememberLocationManager(): LocationManager = LocalLocationManager.current
// AFTER:
fun currentLocationManager(): LocationManager = LocalLocationManager.current
```

Update KDoc accordingly.

- [ ] **Step 2: Rename rememberLocaleState()**

In `locale/LocaleProvider.kt` line 48:
```kotlin
// BEFORE:
fun rememberLocaleState(): LocaleState = LocalLocaleState.current
// AFTER:
fun currentLocaleState(): LocaleState = LocalLocaleState.current
```

Update KDoc accordingly.

- [ ] **Step 3: Rename rememberCurrentLanguage()**

In `locale/ObserveLocale.kt` line 18:
```kotlin
// BEFORE:
fun rememberCurrentLanguage(): LocaleKind {
// AFTER:
fun currentLocale(): LocaleKind {
```

Update KDoc to document single-fire limitation.

- [ ] **Step 4: Build verification**

Run: `./gradlew :foundation:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationProvider.kt \
      foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/LocaleProvider.kt \
      foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/ObserveLocale.kt
git commit -m "refactor(foundation): rename misleading remember* functions to current*"
```

---

### Task 9: API & Naming Fixes — LocationManager, Platform, Deprecated API

**Files:**
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationManager.kt`
- Modify: `foundation/src/androidMain/kotlin/uz/yalla/foundation/location/LocationTrackerFactory.android.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/ObserveAsEvents.kt`
- Modify: `foundation/src/iosMain/kotlin/uz/yalla/foundation/locale/ChangeLanguage.ios.kt`

- [ ] **Step 1: LocationManager — encapsulate locationTracker**

In `location/LocationManager.kt` line 56:
```kotlin
// BEFORE:
val locationTracker: LocationTracker,
// AFTER:
private val locationTracker: LocationTracker,
```

- [ ] **Step 2: LocationManager — add Kermit error logging**

Add import at top of file:
```kotlin
import co.touchlab.kermit.Logger
```

In `startTracking()` — replace the `.onFailure` block (line 104-106):
```kotlin
// BEFORE:
.onFailure {
    _isTracking.value = false
}
// AFTER:
.onFailure { e ->
    _isTracking.value = false
    Logger.w("LocationManager") { "startTracking failed: ${e.message}" }
}
```

In `stopTracking()` — chain `.onFailure` to the existing bare `runCatching`:
```kotlin
// BEFORE:
runCatching {
    locationTracker.stopTracking()
    _isTracking.value = false
}
// AFTER:
runCatching {
    locationTracker.stopTracking()
    _isTracking.value = false
}.onFailure { e ->
    Logger.w("LocationManager") { "stopTracking failed: ${e.message}" }
}
```

- [ ] **Step 3: LocationTrackerFactory.android — fix Koin anti-pattern**

Replace entire file content of `foundation/src/androidMain/kotlin/uz/yalla/foundation/location/LocationTrackerFactory.android.kt`:
```kotlin
package uz.yalla.foundation.location

import android.content.Context
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import org.koin.core.context.GlobalContext

/**
 * Creates Android [LocationTracker] using Koin-provided application context.
 *
 * @since 0.0.1
 */
actual fun createLocationTracker(): LocationTracker {
    val context: Context = GlobalContext.get().get()
    return LocationTracker(
        permissionsController = PermissionsController(applicationContext = context)
    )
}
```

- [ ] **Step 4: ObserveAsEvents — fix deprecated LocalLifecycleOwner import**

In `infra/ObserveAsEvents.kt`, replace the deprecated import:
```kotlin
// BEFORE:
import androidx.compose.ui.platform.LocalLifecycleOwner
// AFTER:
import androidx.lifecycle.compose.LocalLifecycleOwner
```

- [ ] **Step 5: ChangeLanguage.ios.kt — remove synchronize()**

In `foundation/src/iosMain/kotlin/uz/yalla/foundation/locale/ChangeLanguage.ios.kt`, remove line 20:
```kotlin
// DELETE this line:
NSUserDefaults.standardUserDefaults.synchronize()
```

- [ ] **Step 6: Build verification**

Run: `./gradlew :foundation:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 7: Commit**

```bash
git add foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationManager.kt \
      foundation/src/androidMain/kotlin/uz/yalla/foundation/location/LocationTrackerFactory.android.kt \
      foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/ObserveAsEvents.kt \
      foundation/src/iosMain/kotlin/uz/yalla/foundation/locale/ChangeLanguage.ios.kt
git commit -m "fix(foundation): encapsulate LocationManager, add Kermit logging, fix Koin pattern, fix deprecated API"
```

---

## Chunk 3: Documentation & Verification

### Task 10: Documentation — MODULE.md

**Files:**
- Create: `foundation/MODULE.md`

- [ ] **Step 1: Create MODULE.md**

Create `foundation/MODULE.md` with content:
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

- [ ] **Step 2: Commit**

```bash
git add foundation/MODULE.md
git commit -m "docs(foundation): add MODULE.md for Dokka package descriptions"
```

---

### Task 11: Documentation — KDoc & @since Tags (infra/ package)

**Files:**
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/BaseViewModel.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/LoadingController.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/DataErrorMapper.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/DefaultDataErrorMapper.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/ObserveAsEvents.kt`

- [ ] **Step 1: BaseViewModel.kt — add @since**

Add `@since 0.0.1` to class KDoc and all public members (`loading`, `showErrorDialog`, `currentErrorMessageId`, `safeScope`, `handleException`, `handleDataError`, `dismissErrorDialog`, `launchWithLoading`, `launchSafe`, `mapDataErrorToUserMessage`, `mapThrowableToUserMessage`).

- [ ] **Step 2: LoadingController.kt — add @since**

Add `@since 0.0.1` to class KDoc, `loading`, `withLoading`, `DEFAULT_SHOW_AFTER`, `DEFAULT_MIN_DISPLAY_TIME`.

- [ ] **Step 3: DataErrorMapper.kt — add @since**

Add `@since 0.0.1` to interface KDoc and `map` function.

- [ ] **Step 4: DefaultDataErrorMapper.kt — add class KDoc + @since**

Add class-level KDoc:
```kotlin
/**
 * Default implementation mapping [DataError] subtypes to localized string resources.
 *
 * Maps each error category to its corresponding user-facing message.
 *
 * @since 0.0.1
 */
class DefaultDataErrorMapper : DataErrorMapper {
```

- [ ] **Step 5: ObserveAsEvents.kt — add @since**

Add `@since 0.0.1` to both `ObserveAsEvents` overloads.

- [ ] **Step 6: Commit**

```bash
git add foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/
git commit -m "docs(foundation): add @since and KDoc to infra/ package"
```

---

### Task 12: Documentation — KDoc & @since Tags (location/ package)

**Files:**
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationManager.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationProvider.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationState.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationTrackerFactory.kt`

- [ ] **Step 1: LocationManager.kt — add @since**

Add `@since 0.0.1` to class, all public properties, and functions. Also add to companion object `DEFAULT_LOCATION`.

- [ ] **Step 2: LocationProvider.kt — add @since**

Add `@since 0.0.1` to `LocalLocationManager`, `LocationProvider` composable, and `currentLocationManager()`.

- [ ] **Step 3: LocationState.kt — add @since**

Add `@since 0.0.1` to `ExtendedLocation`, `LocationPermissionState`, and their members.

- [ ] **Step 4: LocationTrackerFactory.kt — add KDoc + @since**

Replace the bare expect declaration with:
```kotlin
/**
 * Creates a platform-specific [LocationTracker].
 *
 * - **Android:** Uses Koin-injected application [Context] to create [PermissionsController].
 * - **iOS:** Creates [PermissionsController] directly (no DI needed).
 *
 * @return Configured [LocationTracker] for the current platform
 * @since 0.0.1
 */
expect fun createLocationTracker(): LocationTracker
```

- [ ] **Step 5: Commit**

```bash
git add foundation/src/commonMain/kotlin/uz/yalla/foundation/location/
git commit -m "docs(foundation): add @since and KDoc to location/ package"
```

---

### Task 13: Documentation — KDoc & @since Tags (locale/ package)

**Files:**
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/ChangeLanguage.kt`
- Modify: `foundation/src/androidMain/kotlin/uz/yalla/foundation/locale/ChangeLanguage.android.kt`
- Modify: `foundation/src/iosMain/kotlin/uz/yalla/foundation/locale/ChangeLanguage.ios.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/LocaleProvider.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/ObserveLocale.kt`

- [ ] **Step 1: ChangeLanguage.kt — add @since**

Add `@since 0.0.1` to both `changeLanguage` and `getCurrentLanguage`.

- [ ] **Step 2: ChangeLanguage.android.kt — add limitation warning + @since**

Update KDoc for `changeLanguage`:
```kotlin
/**
 * Changes the app language on Android.
 *
 * **Limitation:** Only calls [Locale.setDefault]. For full in-app language change,
 * the Activity must be recreated or [AppCompatDelegate.setApplicationLocales] used.
 * This function alone does NOT update visible UI.
 *
 * @param languageCode ISO 639-1 language code
 * @since 0.0.1
 */
```

Add `@since 0.0.1` to `getCurrentLanguage`.

- [ ] **Step 3: ChangeLanguage.ios.kt — add @since**

Add `@since 0.0.1` to both functions.

- [ ] **Step 4: LocaleProvider.kt — add @since**

Add `@since 0.0.1` to `LocaleState`, `LocalLocaleState`, `LocaleProvider`, and `currentLocaleState()`.

- [ ] **Step 5: ObserveLocale.kt — document limitation + @since**

Update KDoc to document single-fire limitation:
```kotlin
/**
 * Returns the current locale as [LocaleKind].
 *
 * **Note:** Uses `LaunchedEffect(Unit)` internally — the value is read once at composition
 * and does NOT update if the locale changes during the composition lifecycle.
 * For reactive locale observation, use [LocaleProvider] / [currentLocaleState] instead.
 *
 * @return Current [LocaleKind]
 * @since 0.0.1
 */
@Composable
fun currentLocale(): LocaleKind {
```

- [ ] **Step 6: Commit**

```bash
git add foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/ \
      foundation/src/androidMain/kotlin/uz/yalla/foundation/locale/ \
      foundation/src/iosMain/kotlin/uz/yalla/foundation/locale/
git commit -m "docs(foundation): add @since, KDoc, and limitation warnings to locale/ package"
```

---

### Task 14: Documentation — KDoc & @since Tags (model/ package)

**Files:**
- Modify: all 6 files in `foundation/src/commonMain/kotlin/uz/yalla/foundation/model/`

- [ ] **Step 1: ThemeModel.kt — add class KDoc + @property + @since**

```kotlin
/**
 * UI-ready theme model for settings screens.
 *
 * Sealed hierarchy mapping [ThemeKind] to display properties (icon, localized name).
 *
 * @property icon Theme icon as [ImageVector]
 * @property name Localized theme name
 * @property themeKind Corresponding [ThemeKind] for persistence
 * @since 0.0.1
 */
sealed class ThemeModel(
```

Add `@since 0.0.1` to companion `all` and `fromThemeKind`.

- [ ] **Step 2: LanguageModel.kt — add class KDoc + @property + @since**

```kotlin
/**
 * UI-ready language model for language picker screens.
 *
 * Sealed hierarchy mapping [LocaleKind] to display properties (flag icon, localized name).
 * Only [Uzbek] and [Russian] are in [all] — [UzbekCyrillic] and [English] are defined
 * but not production-ready.
 *
 * @property icon Flag icon as [ImageVector]
 * @property name Localized language name
 * @property localeKind Corresponding [LocaleKind] for persistence
 * @since 0.0.1
 */
sealed class LanguageModel(
```

Add `@since 0.0.1` to companion `all` and `fromLocaleKind`.

- [ ] **Step 3: MapModel.kt — add class KDoc + @property + @since**

```kotlin
/**
 * UI-ready map provider model for settings screens.
 *
 * Sealed hierarchy mapping [MapKind] to display properties (localized name).
 *
 * @property name Localized map provider name
 * @property mapKind Corresponding [MapKind] for persistence
 * @since 0.0.1
 */
sealed class MapModel(
```

Add `@since 0.0.1` to companion `all` and `fromMapKind`.

- [ ] **Step 4: Location.kt — add @property + @since**

Add `@property` tags and `@since 0.0.1` to both `Location` and `FoundLocation` classes. Document `toLocation()` address data loss:
```kotlin
/**
 * Converts to [Location], dropping the [address] field.
 *
 * @since 0.0.1
 */
fun toLocation() = Location(
```

- [ ] **Step 5: SelectableItemModel.kt — add @since**

Add `@since 0.0.1` to class KDoc.

- [ ] **Step 6: LocationModelExtensions.kt — add KDoc to all 5 functions**

Add KDoc with `@return` and `@receiver` tags to each extension:
- `AddressOption.toFoundLocation()` — "Converts address option to [FoundLocation]. @return [FoundLocation] with coordinates and place metadata"
- `SavedAddress.toFoundLocation()` — "Converts saved address to [FoundLocation]. @return [FoundLocation] with address details"
- `Address.toLocation()` — "Converts core [Address] to foundation [Location]. @return [Location] with coordinates and name"
- `Order.Taxi.Route.toLocation()` — "Converts order route point to [Location]. @return [Location] with route coordinates and address"
- `Order.sortedRouteLocations()` — "Returns all route locations sorted by index. @return Sorted list of [Location] from order routes"

Add `@since 0.0.1` to all 5 functions.

- [ ] **Step 7: Commit**

```bash
git add foundation/src/commonMain/kotlin/uz/yalla/foundation/model/
git commit -m "docs(foundation): add @since, class KDoc, and @property tags to model/ package"
```

---

### Task 15: Final Build Verification

- [ ] **Step 1: Full foundation build**

Run: `./gradlew :foundation:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Downstream module — composites**

Composites imports `uz.yalla.foundation.model.SelectableItemModel` (package unchanged).

Run: `./gradlew :composites:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Full SDK build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Verify no old package references remain across entire SDK**

```bash
grep -r "uz.yalla.foundation.viewmodel" --include="*.kt" .
grep -r "uz.yalla.foundation.reactive" --include="*.kt" .
grep -r "uz.yalla.foundation.util" --include="*.kt" .
```
Expected: No matches for any of these (neither in foundation/ nor in other SDK modules like composites/, primitives/, maps/, etc.).

- [ ] **Step 5: Verify file structure**

```bash
find foundation/src -name "*.kt" | sort
```

Expected: 22 files total (18 commonMain + 2 androidMain + 2 iosMain), no files in `viewmodel/`, `reactive/`, or `util/`.

---

## Parallelization Guide

For subagent execution, these task groups are safe to run in parallel (no file conflicts):

**Sequential (must be first):**
- Task 1 (Kermit dependency)
- Tasks 2–4 (package reorganization)

**Parallel group A (after Tasks 2–4):**
- Task 5 (dead code removal) — touches: LocationProviderAdapter, ViewModelExtensions, BaseViewModel
- Task 6 (LoadingController fix) — touches: LoadingController
- Task 7 (model renames) — touches: ThemeModel, LanguageModel, MapModel
- Task 8 (function renames) — touches: LocationProvider, LocaleProvider, ObserveLocale
- Task 9 (platform fixes) — touches: LocationManager, LocationTrackerFactory.android, ObserveAsEvents, ChangeLanguage.ios

**Sequential (after parallel group A):**
- Task 10 (MODULE.md)
- Tasks 11–14 (KDoc pass) — can be parallelized among themselves:
  - Task 11: infra/ files
  - Task 12: location/ files
  - Task 13: locale/ files
  - Task 14: model/ files
- Task 15 (final verification)
