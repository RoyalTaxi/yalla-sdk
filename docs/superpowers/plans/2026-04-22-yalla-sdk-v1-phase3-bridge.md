# yalla-sdk v1 Phase 3 — Bridge Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Land Phase 3 of the v1.0 launch — Bridge modules (design, foundation, platform, resources) — with all Gate-1 scope-leak and asymmetry items fixed, publish-gate hardened, and iOS visual-regression tooling wired. Version bump to `0.0.10-alpha01`.

**Architecture:** Four-module sweep + three cross-cutting deliverables. Breaking changes allowed under full-risk pre-1.0 mode — no deprecation, no `@RequiresOptIn`. Every breaking cluster gets an ADR in `docs/06-DECISIONS.md`. YallaClient gets a lockstep branch `chore/sdk-phase3-bridge` → `dev`.

**Tech Stack:** KMP (Android + iOS), Kotlin 2.2+, Koin, Compose Multiplatform, moko-geo, Valkyrie, swift-snapshot-testing (new), BCV 0.18.1 Klib mode.

**Worktree:** `/Users/islom/StudioProjects/yalla-sdk/.worktrees/v1-phase3-bridge` on branch `feature/v1-phase3-bridge` (already created from `6ffbddb`).

---

## Task List (top-down)

1. design — Font actual KDoc + FontScheme equality test
2. foundation — LocationManager caller-owned CoroutineScope (breaking, ADR-013)
3. foundation — LocationServices Android Koin hardening + iOS `open(url:options:)` modernization
4. foundation — Remove `LanguageOption.UzbekCyrillic` + `LanguageOption.English` + matching `LocaleKind` cases (breaking, ADR-014)
5. foundation — Tests: LocationManager lifecycle, ObserveAsEvents, ChangeLanguage
6. platform — Four asymmetry ADRs + implement (ADR-015)
7. platform — Tests for 14 expect/actual pairs (androidMain behavioral tests + iOS where feasible)
8. resources — Rename `values-be/` → `values-uz-Cyrl/`, add `values-uz/strings.xml` (Uzbek Latin), Valkyrie task-graph hardening, MODULE.md correction
9. infra — `publish.yml` apiCheck + allTests gate (ADR-016)
10. infra — iOS visual-regression wiring scaffold (swift-snapshot-testing via Xcode project)
11. detekt NO-SOURCE fix (wire to `src/<target>Main/kotlin` roots)
12. version bump `0.0.9-alpha01` → `0.0.10-alpha01` + apiDump + CHANGELOG
13. YallaClient lockstep branch `chore/sdk-phase3-bridge` → `dev`
14. PR + spec/quality reviews + merge + CI publish

---

## Task 1 — design: Font actual KDoc + FontScheme equality test

**Files:**
- Modify: `design/src/androidMain/kotlin/uz/yalla/design/font/Font.android.kt`
- Modify: `design/src/iosMain/kotlin/uz/yalla/design/font/Font.ios.kt`
- Create: `design/src/commonTest/kotlin/uz/yalla/design/font/FontSchemeEqualityTest.kt`

- [ ] **Step 1: KDoc each Android actual**

```kotlin
package uz.yalla.design.font

import org.jetbrains.compose.resources.FontResource
import uz.yalla.resources.Res
import uz.yalla.resources.roboto_bold
import uz.yalla.resources.roboto_medium
import uz.yalla.resources.roboto_normal

/** Android actual: Roboto Bold. */
actual val boldFont: FontResource = Res.font.roboto_bold

/** Android actual: Roboto Medium. */
actual val mediumFont: FontResource = Res.font.roboto_medium

/** Android actual: Roboto Regular. */
actual val normalFont: FontResource = Res.font.roboto_normal
```

- [ ] **Step 2: KDoc each iOS actual**

```kotlin
package uz.yalla.design.font

import org.jetbrains.compose.resources.FontResource
import uz.yalla.resources.Res
import uz.yalla.resources.sfpro_bold
import uz.yalla.resources.sfpro_medium
import uz.yalla.resources.sfpro_normal

/** iOS actual: SF Pro Bold. */
actual val boldFont: FontResource = Res.font.sfpro_bold

/** iOS actual: SF Pro Medium. */
actual val mediumFont: FontResource = Res.font.sfpro_medium

/** iOS actual: SF Pro Regular. */
actual val normalFont: FontResource = Res.font.sfpro_normal
```

- [ ] **Step 3: Write the failing equality test**

```kotlin
package uz.yalla.design.font

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FontSchemeEqualityTest {
    private fun sample(): FontScheme = FontScheme(
        title = FontScheme.Title(
            xLarge = TextStyle(fontSize = 30.sp),
            large = TextStyle(fontSize = 22.sp),
            base = TextStyle(fontSize = 20.sp),
        ),
        body = FontScheme.Body(
            caption = TextStyle(fontSize = 13.sp),
            large = FontScheme.Body.Weighty(
                regular = TextStyle(fontSize = 18.sp),
                medium = TextStyle(fontSize = 18.sp),
                bold = TextStyle(fontSize = 18.sp),
            ),
            base = FontScheme.Body.Weighty(
                regular = TextStyle(fontSize = 16.sp),
                medium = TextStyle(fontSize = 16.sp),
                bold = TextStyle(fontSize = 16.sp),
            ),
            small = FontScheme.Body.Weighty(
                regular = TextStyle(fontSize = 14.sp),
                medium = TextStyle(fontSize = 14.sp),
                bold = TextStyle(fontSize = 14.sp),
            ),
        ),
        custom = FontScheme.Custom(
            carNumber = TextStyle(fontSize = 12.sp),
        ),
    )

    @Test
    fun structuralEquality_sameContent_areEqual() {
        assertEquals(sample(), sample())
        assertEquals(sample().hashCode(), sample().hashCode())
    }

    @Test
    fun structuralEquality_differentTitle_areNotEqual() {
        val a = sample()
        val b = sample().copy(
            title = a.title.copy(large = TextStyle(fontSize = 99.sp)),
        )
        assertNotEquals(a, b)
    }
}
```

- [ ] **Step 4: Run the test**

```bash
./gradlew :design:allTests
```

Expected: both tests PASS on `desktopTest`, `iosSimulatorArm64Test`, and `testDebugUnitTest`.

- [ ] **Step 5: Commit**

```bash
git add design/
git commit -m "feat(design): KDoc Font actuals + FontScheme equality test"
```

---

## Task 2 — foundation: LocationManager caller-owned CoroutineScope (breaking)

**Why breaking:** `LocationManager` currently owns a `SupervisorJob`-rooted scope and requires an explicit `close()` call. Callers who forget leak the scope (same pattern ADR-011 fixed in `HttpClient`). This task mirrors ADR-011: take `CoroutineScope` as a constructor param; caller owns lifecycle.

**Files:**
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationManager.kt`
- Create: `docs/06-DECISIONS.md` — append ADR-013
- Modify: `.claude/rules/library-api.md` (if needed to reflect foundation carve-out)

- [ ] **Step 1: Write ADR-013 in `docs/06-DECISIONS.md`**

Append section:

```markdown
## ADR-013: LocationManager caller-owned CoroutineScope (2026-04-22)

**Status:** Accepted.

**Context:** `LocationManager` internally created `CoroutineScope(SupervisorJob() + Dispatchers.Main)` and required callers to invoke `close()` to tear down tracking. Silent leaks when `close()` was forgotten — same root cause as `HttpClient` pre-ADR-011.

**Decision:** Constructor takes a `CoroutineScope` parameter. Caller owns lifecycle. `LocationManager` no longer has a `close()` method — cancel the passed-in scope instead.

Signature:

```kotlin
class LocationManager(
    val locationTracker: LocationTracker,
    private val scope: CoroutineScope,
    private val defaultLocation: GeoPoint = DEFAULT_LOCATION,
) : LocationProvider
```

**Consequences:** Breaking. All YallaClient `LocationManager(...)` constructions must pass a scope. Koin module in YallaClient should use a lifetime-bounded scope (e.g. process-lifetime `CoroutineScope(SupervisorJob() + Dispatchers.Main)` kept in the DI graph and cancelled on app teardown). Mirrors ADR-011.

**Migration:**

```kotlin
// Before
val lm = LocationManager(locationTracker)
// …
lm.close()

// After
val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
val lm = LocationManager(locationTracker, scope)
// …
scope.cancel()
```
```

- [ ] **Step 2: Refactor LocationManager**

Full new file contents:

```kotlin
package uz.yalla.foundation.location

import co.touchlab.kermit.Logger
import dev.icerock.moko.geo.LocationTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.location.LocationProvider
import uz.yalla.core.geo.GeoPoint

/**
 * Manages device location tracking and permission state.
 *
 * Implements [LocationProvider] so it can be injected directly into the maps module.
 *
 * ## Scope ownership (ADR-013)
 *
 * `LocationManager` does **not** own its `CoroutineScope`. The caller constructs and
 * cancels the scope — typically a process-lifetime `SupervisorJob` held in the DI
 * container. When that scope is cancelled, all in-flight tracking operations stop.
 *
 * There is no `close()` method — the scope's lifecycle *is* the lifecycle.
 *
 * ## Usage
 *
 * ```kotlin
 * val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
 * val lm = LocationManager(locationTracker, scope)
 *
 * lm.startTracking()
 * lm.currentLocation.collect { point -> … }
 *
 * // On teardown:
 * scope.cancel()
 * ```
 *
 * @param locationTracker moko-geo tracker for the platform.
 * @param scope Caller-owned scope; cancel to stop all in-flight tracking work.
 * @param defaultLocation Fallback when user location is unavailable.
 * @since 0.0.10
 */
class LocationManager(
    val locationTracker: LocationTracker,
    private val scope: CoroutineScope,
    private val defaultLocation: GeoPoint = DEFAULT_LOCATION,
) : LocationProvider {

    private val _extendedLocation = MutableStateFlow<ExtendedLocation?>(null)

    /** Current device location with extended metadata, or `null` if tracking is off or no fix yet. */
    val extendedLocation: StateFlow<ExtendedLocation?> = _extendedLocation.asStateFlow()

    /** Current location as [GeoPoint]; `null` if no fix. */
    override val currentLocation: Flow<GeoPoint?> = _extendedLocation.map { it?.toGeoPoint() }

    private val _isTracking = MutableStateFlow(false)

    /** `true` while tracking is active (startTracking called and not stopped). */
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _permissionState = MutableStateFlow<LocationPermissionState?>(null)

    /** Last-observed permission state; `null` if not yet updated by caller. */
    val permissionState: StateFlow<LocationPermissionState?> = _permissionState.asStateFlow()

    override fun startTracking() {
        if (_isTracking.value) return
        scope.launch {
            runCatching {
                locationTracker.startTracking()
                _isTracking.value = true
                locationTracker
                    .getExtendedLocationsFlow()
                    .distinctUntilChanged()
                    .collect { extLoc ->
                        _extendedLocation.value = ExtendedLocation(
                            latitude = extLoc.location.coordinates.latitude,
                            longitude = extLoc.location.coordinates.longitude,
                            accuracy = extLoc.location.coordinatesAccuracyMeters.toFloat(),
                            altitude = extLoc.altitude.altitudeMeters,
                            speed = extLoc.speed.speedMps.toFloat(),
                            bearing = extLoc.azimuth.azimuthDegrees.toFloat(),
                            timestamp = extLoc.timestampMs,
                        )
                    }
            }.onFailure { e ->
                _isTracking.value = false
                Logger.w("LocationManager") { "startTracking failed: ${e.message}" }
            }
        }
    }

    override fun stopTracking() {
        if (!_isTracking.value) return
        scope.launch {
            runCatching {
                locationTracker.stopTracking()
                _isTracking.value = false
            }.onFailure { e ->
                Logger.w("LocationManager") { "stopTracking failed: ${e.message}" }
            }
        }
    }

    /** Updates the externally-observed permission state. */
    fun updatePermissionState(state: LocationPermissionState?) {
        _permissionState.value = state
    }

    override fun getCurrentLocation(): GeoPoint? = _extendedLocation.value?.toGeoPoint()

    override fun getCurrentLocationOrDefault(): GeoPoint = getCurrentLocation() ?: defaultLocation

    companion object {
        /** Default fallback location: Tashkent, Uzbekistan. */
        val DEFAULT_LOCATION = GeoPoint(41.2995, 69.2401)
    }
}
```

- [ ] **Step 3: Build and apiDump**

```bash
./gradlew :foundation:compileKotlinIosArm64 :foundation:compileKotlinIosSimulatorArm64 :foundation:assembleDebug
./gradlew :foundation:apiDump
```

Expected: compilation passes. `foundation.klib.api` baseline regenerates showing the constructor change and `close()` removal.

- [ ] **Step 4: Commit**

```bash
git add foundation/ docs/06-DECISIONS.md foundation/api/
git commit -m "feat(foundation)!: LocationManager caller-owned scope (ADR-013)"
```

---

## Task 3 — foundation: LocationServices Android Koin hardening + iOS open(url:) modernization

**Problem:** Android actual calls `GlobalContext.get().get()` which throws `NullPointerException` or `NoDefinitionFoundException` when Koin isn't initialized. iOS actual uses `openURL(url:)` which is soft-deprecated in iOS 16 in favor of `open(url:options:completionHandler:)`.

**Files:**
- Modify: `foundation/src/androidMain/kotlin/uz/yalla/foundation/location/LocationServices.android.kt`
- Modify: `foundation/src/iosMain/kotlin/uz/yalla/foundation/location/LocationServices.ios.kt`

- [ ] **Step 1: Harden Android actual**

```kotlin
package uz.yalla.foundation.location

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import org.koin.core.context.GlobalContext

private fun requireContext(fnName: String): Context =
    runCatching { GlobalContext.get().get<Context>() }.getOrElse { cause ->
        error(
            "yalla-sdk foundation.location.$fnName requires a Koin global Context binding. " +
                "Call startKoin { androidContext(applicationContext) } before invoking this function. " +
                "Root cause: ${cause::class.simpleName}: ${cause.message}",
        )
    }

/** @see isLocationServicesEnabled */
actual fun isLocationServicesEnabled(): Boolean {
    val context = requireContext("isLocationServicesEnabled")
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

/** @see openLocationSettings */
actual fun openLocationSettings() {
    val context = requireContext("openLocationSettings")
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
```

- [ ] **Step 2: Modernize iOS actual**

```kotlin
package uz.yalla.foundation.location

import platform.CoreLocation.CLLocationManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

/** @see isLocationServicesEnabled */
actual fun isLocationServicesEnabled(): Boolean =
    CLLocationManager.locationServicesEnabled()

/**
 * @see openLocationSettings
 *
 * iOS does not allow deep-linking to the system Location Services page, so this opens
 * the app's settings page via `UIApplicationOpenSettingsURLString`.
 */
actual fun openLocationSettings() {
    val url = NSURL(string = UIApplicationOpenSettingsURLString) ?: return
    UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
}
```

- [ ] **Step 3: Build**

```bash
./gradlew :foundation:compileKotlinIosArm64 :foundation:assembleDebug
```

Expected: both targets compile.

- [ ] **Step 4: Commit**

```bash
git add foundation/
git commit -m "fix(foundation): LocationServices Android Koin error surface + iOS open(url:options:)"
```

---

## Task 4 — foundation: Remove `LanguageOption.UzbekCyrillic` + `LanguageOption.English` (breaking)

**Files:**
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/i18n/LanguageOption.kt`
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/i18n/LocaleKind.kt` (drop `En`, `UzCyrillic` cases)
- Modify: `foundation/src/commonMain/kotlin/uz/yalla/foundation/i18n/ChangeLanguage.kt` (adjust mappings)
- Modify: any internal `LanguageOption.from(kind)` call-sites
- Modify: `docs/06-DECISIONS.md` — ADR-014
- Potentially modify: `resources/src/commonMain/composeResources/values-en/` — keep as fallback locale OR delete

- [ ] **Step 1: ADR-014 in `docs/06-DECISIONS.md`**

```markdown
## ADR-014: Narrow LanguageOption to production-ready locales (2026-04-22)

**Status:** Accepted.

**Context:** `LanguageOption` sealed hierarchy had four cases: `Uzbek`, `UzbekCyrillic`, `Russian`, `English`. `LanguageOption.all` listed only Uzbek and Russian; `UzbekCyrillic` and `English` were marked "Not yet production-ready" in KDoc but still exposed on the public surface. `LocaleKind` had the matching four cases.

**Decision:** Remove `LanguageOption.UzbekCyrillic` and `LanguageOption.English` from the sealed hierarchy. Remove `LocaleKind.En` and `LocaleKind.UzCyrillic`. `LanguageOption.from(kind)` now exhaustively covers the remaining two cases.

**Resources:** `values-en/` directory kept on disk as a fallback for any consumer that surfaces English UI outside the picker. `values-be/` is renamed to `values-uz-Cyrl/` by Task 8 but not accompanied by a `LocaleKind.UzCyrillic` restoration — it's a locale resource for platform-level language selection only.

**Consequences:** Breaking. Any YallaClient code referencing `LanguageOption.UzbekCyrillic` or `.English`, or `LocaleKind.En`, or `.UzCyrillic`, must be removed in lockstep.

**Migration:**

```kotlin
// Before
LanguageOption.from(LocaleKind.En)
LanguageOption.all + LanguageOption.English

// After — no English path; remove the call site.
```
```

- [ ] **Step 2: Edit `LocaleKind.kt`** — keep only `Uz` and `Ru` cases. Update any KDoc.

- [ ] **Step 3: Edit `LanguageOption.kt`** — delete `UzbekCyrillic` and `English` data objects. Update `companion object` — `all` stays `[Uzbek, Russian]`. `from(kind)` exhaustive over remaining cases.

- [ ] **Step 4: Edit `ChangeLanguage.kt` and actuals** — ensure ISO-code mapping table has only `uz`, `ru`.

- [ ] **Step 5: Grep for other internal call sites**

```bash
cd /Users/islom/StudioProjects/yalla-sdk/.worktrees/v1-phase3-bridge
grep -rn "LocaleKind.En\|LocaleKind.UzCyrillic\|LanguageOption.English\|LanguageOption.UzbekCyrillic" . --include="*.kt"
```

Expected: zero matches after Task 4 edits. Any hits → fix in-tree.

- [ ] **Step 6: Build + apiDump**

```bash
./gradlew :foundation:assembleDebug :foundation:compileKotlinIosArm64 :foundation:apiDump
```

- [ ] **Step 7: Commit**

```bash
git add foundation/ docs/06-DECISIONS.md
git commit -m "feat(foundation)!: narrow LanguageOption + LocaleKind to production-ready locales (ADR-014)"
```

---

## Task 5 — foundation: Tests for LocationManager lifecycle, ObserveAsEvents, ChangeLanguage

**Files:**
- Create: `foundation/src/commonTest/kotlin/uz/yalla/foundation/location/LocationManagerLifecycleTest.kt`
- Create: `foundation/src/androidUnitTest/kotlin/uz/yalla/foundation/lifecycle/ObserveAsEventsTest.kt` (Compose runtime test)
- Create: `foundation/src/commonTest/kotlin/uz/yalla/foundation/i18n/ChangeLanguageCommonTest.kt`

- [ ] **Step 1: LocationManager lifecycle test**

Using a fake `LocationTracker` — if moko-geo's `LocationTracker` interface is test-friendly, implement a fake. Otherwise, the subagent investigates alternatives (wrap in `interface` in commonMain if moko-geo's type is a class). Test cases:

```kotlin
package uz.yalla.foundation.location

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LocationManagerLifecycleTest {

    @Test
    fun startTracking_isIdempotent() = runTest {
        val tracker = FakeLocationTracker()
        val scope = TestScope().backgroundScope
        val lm = LocationManager(tracker, scope)
        lm.startTracking()
        lm.startTracking()
        assertEquals(1, tracker.startCalls)
    }

    @Test
    fun stopTracking_beforeStart_isNoOp() = runTest {
        val tracker = FakeLocationTracker()
        val lm = LocationManager(tracker, TestScope().backgroundScope)
        lm.stopTracking()
        assertEquals(0, tracker.stopCalls)
        assertFalse(lm.isTracking.value)
    }

    @Test
    fun scopeCancellation_stopsTracking() = runTest {
        val tracker = FakeLocationTracker()
        val scope = CoroutineScope(SupervisorJob())
        val lm = LocationManager(tracker, scope)
        lm.startTracking()
        scope.cancel()
        // After scope cancel, no launched coroutine can run. Subsequent startTracking
        // calls silently no-op (per scope contract).
        assertFalse(scope.isActive)
    }
}
```

(FakeLocationTracker — subagent writes a minimal fake satisfying moko-geo's `LocationTracker` interface. If not possible, abstract to our own `LocationSource` interface — subagent must ask before going that route; that's additional scope.)

- [ ] **Step 2: ObserveAsEvents Compose test (Android Robolectric)**

`foundation/src/androidUnitTest/` + Robolectric. Verify: events collected while STARTED, not collected after lifecycle goes to CREATED.

- [ ] **Step 3: ChangeLanguage common test**

Tests that `changeLanguage("uz")` and `changeLanguage("ru")` accept the production-ready ISO codes and `getCurrentLanguage()` returns a non-null string. Per-platform actuals are the implementation boundary — just smoke test via common test on both targets.

- [ ] **Step 4: Run tests**

```bash
./gradlew :foundation:allTests
```

- [ ] **Step 5: Commit**

```bash
git add foundation/src/*Test/
git commit -m "test(foundation): LocationManager lifecycle, ObserveAsEvents, ChangeLanguage"
```

---

## Task 6 — platform: Four asymmetry ADRs + implement (breaking)

Single batched ADR (**ADR-015**) covering all four platform expect/actual asymmetries, with four decisions called out inline. Implementation happens in the same task because they're small diff-wise.

**Files:**
- Modify: `docs/06-DECISIONS.md` — append ADR-015
- Modify: `platform/src/commonMain/kotlin/uz/yalla/platform/sheet/NativeSheet.kt` + both actuals
- Modify: `platform/src/commonMain/kotlin/uz/yalla/platform/system/SystemBarColors.kt` + both actuals
- Modify: `platform/src/commonMain/kotlin/uz/yalla/platform/sms/ObserveSmsCode.kt` — delete
- Create: `platform/src/androidMain/kotlin/uz/yalla/platform/sms/ObserveSmsCodeAndroid.kt` — Android-only public surface
- Modify: `platform/src/iosMain/kotlin/uz/yalla/platform/sms/ObserveSmsCode.ios.kt` — delete
- Modify: `platform/src/iosMain/kotlin/uz/yalla/platform/config/IosPlatformConfig.kt` — no change (confirm asymmetry)
- Modify: `platform/src/androidMain/kotlin/uz/yalla/platform/config/AndroidPlatformConfig.kt` — optional widening

- [ ] **Step 1: ADR-015 in `docs/06-DECISIONS.md`**

```markdown
## ADR-015: Platform expect/actual asymmetry resolutions (2026-04-22)

**Status:** Accepted.

**Context:** Four `expect`/`actual` pairs in the `platform` module had semantic or surface asymmetries that needed a single decision per pair before the alpha → beta gate.

### ADR-015a — `NativeSheet.onFullyExpanded`

**Decision:** Semantics locked: "fires once when the sheet has settled at its initial fully-expanded detent". Both actuals already satisfy this for the SDK's single-detent sheet usage. Android's `distinctUntilChanged` + `current == target == Expanded` filter handles multi-detent degradation correctly. iOS's `onPresented` callback fires when the UIKit presentation animation completes, which is equivalent. KDoc is the deliverable.

### ADR-015b — `SystemBarColors` dual overload

**Decision:** Remove the color-based overload `SystemBarColors(statusBarColor, navigationBarColor)`. Keep only `SystemBarColors(darkIcons: Boolean)`. Reason: `YallaTheme` already owns bar colors via its color scheme; the color overload duplicated that responsibility. The icon-contrast toggle is the only platform-level decision the caller should make.

**Breaking.** YallaClient must migrate `SystemBarColors(statusBarColor = X, navigationBarColor = Y)` call sites to `SystemBarColors(darkIcons = X.luminance() > 0.5f)` or rely on theme.

### ADR-015c — `ObserveSmsCode` iOS posture

**Decision:** Remove from `commonMain` expect surface. iOS SMS OTP autofill is a native keyboard feature (`UITextContentType.oneTimeCode`), not a callback API. Exposing a no-op iOS actual is misleading. Move to `androidMain`-only public surface:

```kotlin
// platform/src/androidMain/kotlin/uz/yalla/platform/sms/ObserveSmsCodeAndroid.kt
@Composable
fun ObserveSmsCode(onCodeReceived: (String) -> Unit)
```

iOS callers: use `TextField` with `KeyboardOptions(keyboardType = KeyboardType.NumberPassword)` and let the system surface the autofill suggestion.

**Breaking.** YallaClient OTP screen must not call `ObserveSmsCode` from commonMain; move the Android-side call into an Android-only composable, or use `expect`/`actual` at the call-site.

### ADR-015d — `PlatformConfig` asymmetry

**Decision:** Accept the asymmetry. Android has no required native component factories; iOS needs three. Widening `AndroidPlatformConfig` to a symmetric builder adds surface with no runtime value. Document that `AndroidPlatformConfig` is a marker class and `YallaPlatform.installAndroid()` is a one-liner, while `IosPlatformConfig.Builder` requires explicit factories.

No code change. KDoc tightened only.
```

- [ ] **Step 2: KDoc `NativeSheet.onFullyExpanded` with ADR-015a semantic**

Add to the `expect` declaration:

```kotlin
/**
 * Called once when the sheet has settled at its fully-expanded detent.
 *
 * - **Android:** fires when `SheetValue` transitions to `Expanded` and is stable there
 *   (`current == target == Expanded`). In a multi-detent sheet, only the settle-at-Expanded
 *   edge fires, not intermediate detents.
 * - **iOS:** fires when the UIKit presentation animation completes (via `onPresented`
 *   callback on the presenter). For a single-detent sheet, this is equivalent to Android.
 *
 * Fires at most once per presentation; not re-invoked on re-composition.
 */
```

- [ ] **Step 3: Remove `SystemBarColors` color overload**

Edit `platform/src/commonMain/kotlin/uz/yalla/platform/system/SystemBarColors.kt` — delete the `(statusBarColor, navigationBarColor)` expect. Delete matching actuals from both android and ios. Leave only `SystemBarColors(darkIcons: Boolean)`.

- [ ] **Step 4: Move `ObserveSmsCode` to Android-only**

- Delete `platform/src/commonMain/kotlin/uz/yalla/platform/sms/ObserveSmsCode.kt`
- Delete `platform/src/iosMain/kotlin/uz/yalla/platform/sms/ObserveSmsCode.ios.kt`
- Move Android actual body into a **non-expect** Android-only public composable:

```kotlin
// platform/src/androidMain/kotlin/uz/yalla/platform/sms/ObserveSmsCode.kt
package uz.yalla.platform.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * Observes an incoming SMS OTP via Google's SMS Retriever API.
 *
 * Android-only. iOS handles OTP autofill natively in the keyboard — see ADR-015c.
 *
 * @param onCodeReceived Called with the full SMS message body when one is retrieved.
 */
@Composable
fun ObserveSmsCode(onCodeReceived: (String) -> Unit) {
    val context = LocalContext.current
    val currentCallback = rememberUpdatedState(onCodeReceived)

    DisposableEffect(Unit) {
        val client = SmsRetriever.getClient(context)
        client.startSmsRetriever()

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
                val extras = intent.extras ?: return
                val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return
                if (status.statusCode == CommonStatusCodes.SUCCESS) {
                    val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE).orEmpty()
                    if (message.isNotBlank()) {
                        currentCallback.value(message)
                    }
                }
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            ContextCompat.RECEIVER_EXPORTED,
        )

        onDispose {
            runCatching { context.unregisterReceiver(receiver) }
        }
    }
}
```

- [ ] **Step 5: KDoc-tighten `AndroidPlatformConfig` and `IosPlatformConfig`**

Per ADR-015d: document accepted asymmetry in the `AndroidPlatformConfig` class KDoc ("no factories required"), and in `IosPlatformConfig.Builder` ("three factories required; Android has no analogous requirements").

- [ ] **Step 6: Build + apiDump**

```bash
./gradlew :platform:assembleDebug :platform:compileKotlinIosArm64 :platform:apiDump
```

- [ ] **Step 7: Commit**

```bash
git add platform/ docs/06-DECISIONS.md
git commit -m "feat(platform)!: resolve 4 expect/actual asymmetries (ADR-015)"
```

---

## Task 7 — platform: Tests for 14 expect/actual pairs

Scope: behavioral Android tests for each expect/actual pair. iOS tests only where feasible without snapshot tooling (Task 10 wires that scaffold — snapshot-based UI tests are out of scope here, but basic function tests are in).

14 pairs to cover:

1. `NativeCircleIconButton` — androidUnitTest (Compose runtime)
2. `NativeLoadingIndicator` — androidUnitTest
3. `NativeSheet` — androidUnitTest + iOS basic (present/dismiss callback order)
4. `NativeSwitch` — androidUnitTest
5. `NativeWheelDatePicker` — androidUnitTest
6. `ObserveSmsCode` — **now Android-only**, test in androidUnitTest
7. `SystemBarColors(darkIcons: Boolean)` — androidUnitTest + iOS smoke (no crash)
8. `getAppSignature()` — androidUnitTest (with test fixtures), iOS returns null/empty
9. `rememberAppUpdateState` — androidUnitTest (stub Play Core)
10. `rememberHapticController` — androidUnitTest
11. `rememberInAppBrowser` — androidUnitTest (verify launch path present)
12. `statusBarHeight` — androidUnitTest (with window insets fake)
13. `NativeNavHost` — androidUnitTest (navigation basics)
14. (removed overload of `SystemBarColors`)

Per-pair test file naming: `<Name>Test.kt` in the appropriate test source set.

**Strategy:** since this is a lot of ground, the implementing subagent writes *behavioral smoke* tests (construction + key callback fires + no-crash), not full visual validation. Visual validation is Phase 4's job (ADR-005 + Roborazzi/swift-snapshot wiring).

- [ ] **Step 1:** Implementer subagent should build test scaffolds per-pair. If any `expect` requires non-trivial platform mocks (e.g. `rememberAppUpdateState` needs Play Core), the test can be a no-op smoke that just verifies the function exists and returns without crashing when called with valid inputs.

- [ ] **Step 2: Run**

```bash
./gradlew :platform:allTests
```

Expected: all tests green.

- [ ] **Step 3: Commit**

```bash
git add platform/src/*Test/
git commit -m "test(platform): behavioral coverage for 14 expect/actual pairs"
```

---

## Task 8 — resources: Rename values-be → values-uz-Cyrl, add values-uz, harden Valkyrie, update MODULE.md

**Files:**
- Rename: `resources/src/commonMain/composeResources/values-be/` → `values-uz-Cyrl/`
- Create: `resources/src/commonMain/composeResources/values-uz/strings.xml`
- Modify: `resources/build.gradle.kts` — explicit Valkyrie task graph
- Modify: `resources/MODULE.md` — reflect actual locales

- [ ] **Step 1: Git-rename the directory**

```bash
git mv resources/src/commonMain/composeResources/values-be \
       resources/src/commonMain/composeResources/values-uz-Cyrl
```

- [ ] **Step 2: Seed values-uz/strings.xml from values-uz-Cyrl (Uzbek Cyrillic)**

**Islom-self-review**: generate the Latin version from the existing Cyrillic text using standard Uzbek Latin ↔ Cyrillic mapping (not machine translation). The subagent should:
1. Read `values-uz-Cyrl/strings.xml`.
2. Apply the deterministic Cyrillic→Latin transliteration for each `<string>` body (the official 2019 Uzbek Latin orthography: а→a, б→b, д→d, е→e, ф→f, г→g, ҳ→h, и→i, к→k, л→l, м→m, н→n, о→o, п→p, қ→q, р→r, с→s, т→t, у→u, в→v, х→x, й→y, з→z, ў→oʻ, ғ→gʻ, ш→sh, ч→ch, нг→ng, я→ya, ю→yu, ё→yo, ъ→ʼ, ь→`` (drop), щ→shch, э→e, ы→i — etc.; subagent consults a canonical table).
3. Write to `values-uz/strings.xml`.
4. Flag in the commit message: "Islom-review required: Uzbek Latin strings generated from Cyrillic via transliteration table; manual review recommended before 1.0."

**Important:** the subagent must NOT use an LLM or external translation service for this. It must use a deterministic transliteration only. If uncertain, the subagent asks before proceeding.

- [ ] **Step 3: Harden Valkyrie task graph in resources/build.gradle.kts**

Current configuration adds `dependsOn(valkyrieTask)` via `tasks.configureEach` for `compileKotlin*` and `*sourcesJar` tasks only. Tighten to:

```kotlin
val valkyrieTask = "generateValkyrieImageVectorCommonMain"

// Explicit dependency graph: every task that consumes Valkyrie output
// must declare a dependency. Narrower than configureEach and harder to drift.
tasks.matching { t ->
    t.name.startsWith("compileKotlin") ||
    t.name.endsWith("SourcesJar") ||
    t.name.endsWith("sourcesJar") ||
    t.name.startsWith("processResources") ||
    t.name == "jar" ||
    t.name.startsWith("bundle") && t.name.endsWith("Release") ||
    t.name.startsWith("bundle") && t.name.endsWith("Debug")
}.configureEach {
    dependsOn(valkyrieTask)
}
```

- [ ] **Step 4: Update `resources/MODULE.md`** — change the locale list:

```markdown
Current locales: `values` (default), `values-en` (English fallback asset), `values-ru` (Russian), `values-uz` (Uzbek Latin), `values-uz-Cyrl` (Uzbek Cyrillic).
```

- [ ] **Step 5: Build**

```bash
./gradlew :resources:assembleDebug :resources:compileKotlinIosArm64
```

- [ ] **Step 6: Commit**

```bash
git add resources/
git commit -m "feat(resources): rename values-be to values-uz-Cyrl, add values-uz (Uzbek Latin), harden Valkyrie task graph"
```

Commit body: "Islom-review required: Uzbek Latin strings generated via Cyrillic→Latin transliteration."

---

## Task 9 — infra: publish.yml apiCheck + allTests gate

**File:** `.github/workflows/publish.yml`

- [ ] **Step 1: ADR-016 in `docs/06-DECISIONS.md`**

```markdown
## ADR-016: Publish workflow gates on apiCheck + allTests (2026-04-22)

**Status:** Accepted.

**Context:** `publish.yml` currently runs `./gradlew publish` on push to main with no prior gates. A broken main branch publishes broken artifacts. The CI workflow (`ci.yml`) runs only lint on PRs (Ubuntu-only, no iOS) and is a lagging signal after merge.

**Decision:** Add a `verify` job to `publish.yml` that runs before `publish`:
- `./gradlew apiCheck` (BCV Klib — Native + commonMain)
- `./gradlew allTests` (all test source sets across targets)

The `publish` job `needs: verify`. If either gate fails, no artifact ships.

**Consequences:** Publishes become slower (allTests + apiCheck add ~5–10 min) but prevent broken-main publishes. `ci.yml` stays lint-only (Ubuntu); publish gate runs on macOS-latest where Xcode + CocoaPods are present.
```

- [ ] **Step 2: Edit `.github/workflows/publish.yml`**

```yaml
name: Publish SDK

on:
  push:
    branches: [main]
  workflow_dispatch:

permissions:
  contents: read
  packages: write

jobs:
  verify:
    name: Verify
    runs-on: macos-latest
    timeout-minutes: 60
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - uses: gradle/actions/setup-gradle@v4
      - name: apiCheck
        run: ./gradlew apiCheck
      - name: allTests
        run: ./gradlew allTests

  publish:
    name: Publish
    needs: verify
    runs-on: macos-latest
    timeout-minutes: 90
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - uses: gradle/actions/setup-gradle@v4
      - name: Publish all modules
        run: ./gradlew publish
        env:
          GITHUB_ACTOR: ${{ github.actor }}
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

- [ ] **Step 3: Commit**

```bash
git add .github/workflows/publish.yml docs/06-DECISIONS.md
git commit -m "ci: publish.yml gates on apiCheck + allTests before publish (ADR-016)"
```

---

## Task 10 — infra: iOS visual-regression wiring scaffold

**Goal (Phase 3):** Scaffold only — a stub iOS XCTest target under a new `docs/iosSnapshotTests/` folder (or `iosTests/`) that consumes `pointfreeco/swift-snapshot-testing 1.19.2` via SPM. Do NOT attempt to capture real snapshots this task — that's Phase 4. This task just lands the tooling so Phase 4 can hit the ground running.

**Files:**
- Create: `iosTests/Package.swift`
- Create: `iosTests/Tests/YallaSnapshotScaffoldTests/ScaffoldTest.swift` (one trivial test asserting the SPM package resolves)
- Create: `iosTests/README.md` (how to run: `cd iosTests && swift test`)
- Modify: `.claude/rules/publishing.md` — note that `iosTests/` is not published

- [ ] **Step 1: Create SPM package**

```swift
// iosTests/Package.swift
// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "YallaSnapshotTests",
    platforms: [.iOS(.v16)],
    products: [],
    dependencies: [
        .package(
            url: "https://github.com/pointfreeco/swift-snapshot-testing.git",
            from: "1.19.2"
        ),
    ],
    targets: [
        .testTarget(
            name: "YallaSnapshotScaffoldTests",
            dependencies: [
                .product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
            ]
        ),
    ]
)
```

- [ ] **Step 2: Scaffold test**

```swift
// iosTests/Tests/YallaSnapshotScaffoldTests/ScaffoldTest.swift
import XCTest
import SnapshotTesting

final class ScaffoldTest: XCTestCase {
    func testSnapshotTestingLoads() {
        // Sanity test: just prove the SPM dependency resolves. Real snapshots land in Phase 4.
        XCTAssertEqual(1 + 1, 2)
    }
}
```

- [ ] **Step 3: README**

```markdown
# iosTests — iOS Visual Regression

Scaffolded in Phase 3 of the v1.0 launch. Used by Phase 4 onward for UI snapshot
regression on iOS primitives and composites.

## Running

```bash
cd iosTests
swift test
```

## Tooling

- [`pointfreeco/swift-snapshot-testing`](https://github.com/pointfreeco/swift-snapshot-testing) 1.19.2
- Tolerance per ADR-… TBD (Phase 4).
- Not published to any Maven / Swift registry.
```

- [ ] **Step 4: Verify locally**

```bash
cd /Users/islom/StudioProjects/yalla-sdk/.worktrees/v1-phase3-bridge/iosTests
swift test
```

Expected: one test passes. If SwiftPM can't resolve, the subagent investigates and blocks.

- [ ] **Step 5: Commit**

```bash
cd ..
git add iosTests/
git commit -m "chore(iosTests): scaffold swift-snapshot-testing 1.19.2 for Phase 4 visual regression"
```

---

## Task 11 — detekt NO-SOURCE fix

**Problem:** detekt CI task reports NO-SOURCE on KMP source layout because it's looking at pre-KMP paths. Need to point detekt at each `src/<target>Main/kotlin` root.

**Files:**
- Modify: `build-logic/convention/src/main/kotlin/KmpLibraryConventionPlugin.kt` (or wherever detekt is configured centrally)
- Modify: `build.gradle.kts` root (if detekt is there)
- Verify: `./gradlew detekt` actually scans files after fix

- [ ] **Step 1: Investigate**

```bash
cd /Users/islom/StudioProjects/yalla-sdk/.worktrees/v1-phase3-bridge
./gradlew detekt --info 2>&1 | grep -E "(source|NO-SOURCE|detekt)" | head -40
```

- [ ] **Step 2: Update detekt config to explicitly include KMP source sets**

In the detekt `extension` config:

```kotlin
detekt {
    source.setFrom(
        files(
            "src/commonMain/kotlin",
            "src/androidMain/kotlin",
            "src/androidUnitTest/kotlin",
            "src/iosMain/kotlin",
            "src/commonTest/kotlin",
            "src/iosTest/kotlin",
        )
    )
    // … existing config
}
```

- [ ] **Step 3: Verify**

```bash
./gradlew detekt --info 2>&1 | grep -E "Analyzing" | head -20
```

Expected: detekt logs "Analyzing" lines for actual `.kt` files, not NO-SOURCE.

- [ ] **Step 4: Commit**

```bash
git add build-logic/ build.gradle.kts
git commit -m "fix(ci): detekt scans KMP source roots (src/<target>Main/kotlin)"
```

---

## Task 12 — Version bump + apiDump + CHANGELOG

**Files:**
- Modify: `gradle.properties` — `yalla.sdk.version=0.0.10-alpha01`
- Modify: `CHANGELOG.md` — add `0.0.10-alpha01` section
- Regenerate: `<module>/api/<module>.klib.api` via `./gradlew apiDump`

- [ ] **Step 1: Bump version**

Edit `gradle.properties`:

```properties
yalla.sdk.version=0.0.10-alpha01
```

- [ ] **Step 2: Regenerate BCV baselines**

```bash
./gradlew apiDump
```

Expected: multiple `.klib.api` files updated reflecting all breaking changes. Inspect the diff to confirm it matches our intended changes (no unexpected drift).

- [ ] **Step 3: Update CHANGELOG.md**

Add a new section at the top:

```markdown
## 0.0.10-alpha01 — 2026-04-22

### Breaking
- **`LocationManager`** now requires a caller-owned `CoroutineScope` constructor parameter. `close()` method removed. (ADR-013)
- **`LanguageOption.UzbekCyrillic`** and **`LanguageOption.English`** removed. **`LocaleKind.En`** and **`LocaleKind.UzCyrillic`** removed. (ADR-014)
- **`SystemBarColors(statusBarColor, navigationBarColor)`** overload removed. Use `SystemBarColors(darkIcons: Boolean)`. (ADR-015b)
- **`ObserveSmsCode`** moved from `commonMain` expect to `androidMain`-only public surface. iOS callers: use `KeyboardType.NumberPassword` + native autofill. (ADR-015c)

### Added
- Unit tests for `LocationManager` lifecycle, `ObserveAsEvents`, `ChangeLanguage`.
- Behavioral tests for 13 remaining `platform` expect/actual pairs.
- `FontScheme` structural-equality test.
- `values-uz/strings.xml` (Uzbek Latin) — Islom-review required before 1.0.
- iOS visual-regression scaffold under `iosTests/` using swift-snapshot-testing 1.19.2.

### Changed
- **`values-be/` → `values-uz-Cyrl/`** (Phase 1 follow-up; was mislabeled as Belarusian while containing Uzbek Cyrillic text).
- `LocationServices` Android actual now raises a clear error if Koin global `Context` is not bound (previously NPE'd).
- `LocationServices` iOS actual uses `UIApplication.open(url:options:completionHandler:)` (modern API).
- `NativeSheet.onFullyExpanded` semantics locked via ADR-015a; both actuals already satisfied.
- `resources` Valkyrie task graph tightened to cover non-compile tasks too.
- `detekt` now scans KMP `src/<target>Main/kotlin` roots (no more NO-SOURCE).

### Infrastructure
- **`publish.yml`** gates on `./gradlew apiCheck allTests` before publish (ADR-016).

### Decisions
- ADR-013: LocationManager caller-owned scope.
- ADR-014: LanguageOption / LocaleKind narrowed.
- ADR-015: Platform expect/actual asymmetry resolutions (a–d).
- ADR-016: publish.yml gates on apiCheck + allTests.
```

- [ ] **Step 4: Commit**

```bash
git add gradle.properties CHANGELOG.md */api/
git commit -m "chore: bump yalla.sdk.version to 0.0.10-alpha01"
```

---

## Task 13 — YallaClient lockstep: chore/sdk-phase3-bridge → dev

**Scope:** Update YallaClient so its build passes against `0.0.10-alpha01`.

**Files (in `~/StudioProjects/YallaClient`):**
- `gradle/libs.versions.toml` — bump `yalla-sdk` version + ensure no `mavenLocal()`
- Call-site migrations for each of the breaking changes
- Per `composeApp/src/commonMain/**` grep

- [ ] **Step 1: Create branch**

```bash
cd ~/StudioProjects/YallaClient
git checkout dev
git pull
git checkout -b chore/sdk-phase3-bridge
```

- [ ] **Step 2: Bump SDK version**

Edit `gradle/libs.versions.toml` — change `yalla-sdk = "0.0.9-alpha01"` to `yalla-sdk = "0.0.10-alpha01"`.

Verify no `mavenLocal()` in `settings.gradle.kts` (should have been dropped in Phase 2 PR #304).

- [ ] **Step 3: Migrate `LocationManager` constructors (ADR-013)**

Grep:

```bash
grep -rn "LocationManager(" composeApp/src/commonMain/ composeApp/src/androidMain/
```

Each constructor call needs a `CoroutineScope` parameter. In Koin modules, introduce a process-lifetime scope:

```kotlin
val locationModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single { LocationManager(get(), get()) }
}
```

Remove any `lm.close()` calls.

- [ ] **Step 4: Migrate `LanguageOption` + `LocaleKind` references (ADR-014)**

Grep:

```bash
grep -rn "LanguageOption.English\|LanguageOption.UzbekCyrillic\|LocaleKind.En\|LocaleKind.UzCyrillic" composeApp/src/
```

Delete each call site. If the language picker flow still surfaces English/Cyrillic options, those UX paths are deleted (per "not production-ready" note).

- [ ] **Step 5: Migrate `SystemBarColors` color overload (ADR-015b)**

Grep:

```bash
grep -rn "SystemBarColors(statusBarColor\|SystemBarColors(navigationBarColor" composeApp/src/
```

Replace with `SystemBarColors(darkIcons = …)`. Derive `darkIcons` from whatever logic set the color — usually theme-based.

- [ ] **Step 6: Migrate `ObserveSmsCode` (ADR-015c)**

Grep:

```bash
grep -rn "ObserveSmsCode" composeApp/src/
```

If called from `commonMain`: refactor the OTP screen to use `expect`/`actual` at its own level, with the Android actual calling `uz.yalla.platform.sms.ObserveSmsCode`. iOS actual becomes a no-op that relies on `KeyboardType.NumberPassword` at the input-field level.

- [ ] **Step 7: Build YallaClient locally**

```bash
./gradlew :composeApp:compileKotlinIosArm64 :composeApp:assembleDebug
```

Expected: both pass. If any warning about deprecated/changed symbols, address.

- [ ] **Step 8: Commit + push**

```bash
git add gradle/libs.versions.toml composeApp/
git commit -m "chore(sdk): adopt yalla-sdk 0.0.10-alpha01 (Phase 3 — Bridge)

Migrations:
- LocationManager: pass caller-owned CoroutineScope (ADR-013)
- LanguageOption/LocaleKind: remove English + UzbekCyrillic refs (ADR-014)
- SystemBarColors: single boolean overload (ADR-015b)
- ObserveSmsCode: Android-only invocation path (ADR-015c)"
git push -u origin chore/sdk-phase3-bridge
```

- [ ] **Step 9: Open PR against `dev`**

```bash
gh pr create --base dev --head chore/sdk-phase3-bridge \
  --title "chore(sdk): adopt yalla-sdk 0.0.10-alpha01 (Phase 3 Bridge)" \
  --body "$(cat <<'EOF'
## Summary
Adopts yalla-sdk Phase 3 Bridge release (`0.0.10-alpha01`), applying the four breaking-change migrations in lockstep.

## ADRs referenced
- ADR-013: LocationManager caller-owned CoroutineScope
- ADR-014: LanguageOption narrowing
- ADR-015a–d: Platform expect/actual asymmetry resolutions
- ADR-016: publish.yml gate (no YallaClient impact)

## Pre-merge gate
Waits for yalla-sdk's `0.0.10-alpha01` to publish on GitHub Packages (publish.yml success).

## Test plan
- [ ] composeApp builds for android + iosArm64
- [ ] Language picker shows only Uzbek + Russian
- [ ] OTP screen works on Android (SMS Retriever) and iOS (native autofill)
- [ ] No LocationManager leak regressions (scope is cancelled on app teardown)
EOF
)"
```

---

## Task 14 — yalla-sdk PR + reviews + merge + publish

- [ ] **Step 1: Push branch**

```bash
cd /Users/islom/StudioProjects/yalla-sdk/.worktrees/v1-phase3-bridge
git push -u origin feature/v1-phase3-bridge
```

- [ ] **Step 2: Open PR**

```bash
gh pr create --base main --head feature/v1-phase3-bridge \
  --title "Phase 3 — Bridge: design + foundation + platform + resources (0.0.10-alpha01)" \
  --body "$(cat <<'EOF'
## Summary
Lands Phase 3 of the yalla-sdk v1.0 launch initiative. Covers the four Bridge modules (`design`, `foundation`, `platform`, `resources`) plus two cross-cutting deliverables (publish-gate, iOS visual-regression scaffold).

Version bump: `0.0.9-alpha01` → `0.0.10-alpha01`.

## ADRs
- **ADR-013:** LocationManager caller-owned CoroutineScope
- **ADR-014:** LanguageOption / LocaleKind narrowed to Uzbek + Russian
- **ADR-015a:** NativeSheet.onFullyExpanded semantics locked
- **ADR-015b:** SystemBarColors consolidated to single boolean overload
- **ADR-015c:** ObserveSmsCode moved to androidMain-only
- **ADR-015d:** PlatformConfig asymmetry documented as accepted
- **ADR-016:** publish.yml gates on apiCheck + allTests

## Lockstep
YallaClient PR: `chore/sdk-phase3-bridge` → `dev` (see linked PR).

## Post-merge
publish.yml runs apiCheck + allTests, then publishes `0.0.10-alpha01` to GitHub Packages.

## Pre-1.0 disclosure
Full-risk mode: no deprecation, no @RequiresOptIn. All breaking changes land directly.
EOF
)"
```

- [ ] **Step 3: Wait for PR CI (lint-only)**

Expected: green.

- [ ] **Step 4: Self-review ask — spawn Agent(s) as critics per Phase 2 pattern**

- `kmp-library-author` subagent: review ADR-013, ADR-014, ADR-015a–d for correctness and API-hygiene.
- `superpowers:code-reviewer` subagent: review the entire diff against the plan.

- [ ] **Step 5: Address reviewer findings, re-push if needed.**

- [ ] **Step 6: Squash-merge to main**

```bash
gh pr merge <PR-NUMBER> --squash
```

- [ ] **Step 7: Watch publish.yml**

```bash
gh run list --workflow=publish.yml --limit=3
gh run watch
```

Expected: verify job green (apiCheck + allTests), then publish job green. `0.0.10-alpha01` hits `maven.pkg.github.com/RoyalTaxi/yalla-sdk`.

- [ ] **Step 8: Verify GitHub Packages has the new version**

```bash
curl -sS -u "isloms:$GITHUB_TOKEN" -o /dev/null -w "%{http_code}\n" \
  "https://maven.pkg.github.com/RoyalTaxi/yalla-sdk/uz/yalla/sdk/bom/0.0.10-alpha01/bom-0.0.10-alpha01.pom"
```

Expected: 200 or 302.

- [ ] **Step 9: Update memory**

Append to `/Users/islom/.claude/projects/-Users-islom-StudioProjects-yalla-sdk/memory/project-v1-launch.md`:

```markdown
### Phase 3 shipped content (`0.0.10-alpha01` — 2026-04-22)
- 4 ADRs: ADR-013 (LocationManager scope), ADR-014 (LanguageOption narrowing), ADR-015a–d (platform asymmetries), ADR-016 (publish.yml gate).
- Bridge modules: design (KDoc + equality test), foundation (scope + i18n narrowing + LocationServices hardening + tests), platform (4 asymmetry fixes + 13-pair tests), resources (rename + Uzbek Latin + Valkyrie + MODULE.md).
- Infra: publish.yml verify-then-publish gate, iOS snapshot scaffold, detekt KMP source fix.
- Phase 3 closed. Remaining: Phase 4 (UI + ADR-005 slot migration), Phase 5 (Services), Phase 6 (1.0 tag).
```

- [ ] **Step 10: Close the YallaClient PR after `0.0.10-alpha01` is visible**

Wait for GitHub Packages to serve the artifact, then trigger YallaClient PR's CI (re-push a no-op or use Actions-tab `Re-run all jobs`). Once green, merge to `dev`.

---

## Completion criteria (all must be true)

- [ ] All 14 tasks above complete.
- [ ] `0.0.10-alpha01` published to GitHub Packages.
- [ ] YallaClient PR merged to `dev`.
- [ ] memory/project-v1-launch.md reflects Phase 3 shipped content.
- [ ] Remaining Phase 4/5/6 follow-ups updated in memory.

## Post-Phase-3 stop

Same pattern as Phase 1 + 2 end: stop at the boundary; await user signal before Phase 4.
