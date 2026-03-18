# Batch 2: Platform + Media Modules — Native iOS Building Blocks

## Overview

Refactor the `platform` and `media` modules to gold standard with native iOS component architecture. Both modules remain **separate** (no merge) to keep the dependency graph clean — `platform` stays lightweight (UI components), `media` stays self-contained (camera/gallery/compression with heavy deps like CameraX, Paging3, AVFoundation).

**Goal:** Every platform-specific component feels like it was built by a senior Swift developer.

**Approach:** Phased implementation within Batch 2.
- **Phase 1 (this spec):** YallaPlatform.install() architecture + refactor existing components + fix bugs
- **Phase 2 (future spec):** Add new native components (Sheet upgrade, Haptic, Browser, Navigation, etc.)
- **Phase 3 (future spec):** Full KDoc + MODULE.md + comprehensive tests for both modules

**Breaking change:** This is an alpha SDK (0.0.x). CompositionLocal factories are replaced by `YallaPlatform.install()`. No deprecation period — consumers must update integration code.

## Architecture Decisions

### Decision 1: No Media Merge

**media stays separate from platform.** Rationale (from architecture review):
- `primitives` and `composites` depend on `platform`. Merging media would leak CameraX (3.5MB), Paging3 (1MB), AVFoundation cinterops to every UI module.
- SDK consumers who only need buttons/sheets should not pull camera dependencies.
- Modular SDKs are easier to maintain and adopt incrementally.

**Dependency graph (unchanged):**
```
platform --> design, resources
media --> (standalone: paging, camerax, avfoundation, compose)
primitives --> platform, design, resources, core
composites --> platform, primitives, design, resources, core, foundation
```

### Decision 2: Hybrid Native Pattern

Two interop patterns based on complexity:

| Pattern | Criteria | Examples |
|---------|----------|----------|
| **expect/actual** | Fully constructible from Kotlin/Native, no host VC/View needed | UISwitch, UIDatePicker, UIActivityIndicatorView, Haptics, SystemBars |
| **Factory/delegate** | Needs Swift environment, host UIViewController, or Swift-only frameworks | Navigation shell, SFSafariViewController, Sheets with detents, TextField, SearchBar, ContextMenu, ShareSheet, PullToRefresh |

**Note:** NativePullToRefresh requires factory pattern (UIRefreshControl needs a host UIScrollView). NativeShareSheet needs factory (requires parent UIViewController for presentation).

### Decision 3: YallaPlatform.install() — Centralized Factory Registry

Replaces scattered CompositionLocal factories with a single install() call.

**Pattern: interface (not expect class) + platform-specific implementations:**

```kotlin
// commonMain — uz.yalla.platform.YallaPlatform
interface PlatformConfig

object YallaPlatform {
    @PublishedApi
    internal var config: PlatformConfig? = null

    val isInstalled: Boolean get() = config != null

    fun install(config: PlatformConfig) {
        this.config = config
    }

    @PublishedApi
    internal inline fun <reified T : PlatformConfig> requireConfig(): T =
        (config as? T) ?: error(
            "YallaPlatform not installed. Call YallaPlatform.install() " +
            "in your app's entry point before using platform components."
        )

    /** Reset config — test-only, prevents global state pollution. */
    @VisibleForTesting
    fun reset() { config = null }
}
```

```kotlin
// iosMain
class IosPlatformConfig private constructor(
    val sheetPresenter: SheetPresenterFactory,
    val circleButton: CircleIconButtonFactory,
    val squircleButton: SquircleIconButtonFactory,
    val themeProvider: ThemeProvider? = null,
    // Phase 2 additions:
    // val navigation: NavigationFactory? = null,
    // val browser: BrowserFactory? = null,
) : PlatformConfig {
    class Builder {
        var sheetPresenter: SheetPresenterFactory? = null
        var circleButton: CircleIconButtonFactory? = null
        var squircleButton: SquircleIconButtonFactory? = null
        var themeProvider: ThemeProvider? = null

        fun build(): IosPlatformConfig {
            return IosPlatformConfig(
                sheetPresenter = requireNotNull(sheetPresenter) { "sheetPresenter is required" },
                circleButton = requireNotNull(circleButton) { "circleButton is required" },
                squircleButton = requireNotNull(squircleButton) { "squircleButton is required" },
                themeProvider = themeProvider,
            )
        }
    }
}

internal fun requireIosConfig(): IosPlatformConfig =
    YallaPlatform.requireConfig<IosPlatformConfig>()
```

```kotlin
// androidMain — no factories needed, everything is Compose
class AndroidPlatformConfig : PlatformConfig

fun YallaPlatform.installAndroid() {
    YallaPlatform.install(AndroidPlatformConfig())
}
```

**Why interface instead of expect class:** `expect class` with no constructor cannot have an `actual class` with constructor parameters. The compiler rejects mismatched constructors. The interface approach gives each platform full control over its config shape.

**Why builder pattern:** Phase 2 adds 10+ factories. A flat constructor with 15 params is unreadable. Builder keeps the Swift call site clean and allows incremental registration.

### Decision 4: Factory Types as Interfaces (not lambdas)

**Before (current — data class with lambda fields):**
```kotlin
data class SheetPresenterFactory(
    val present: (UIViewController, UIViewController, Double, Long, () -> Unit, () -> Unit) -> Unit,
    val dismiss: (UIViewController, Boolean) -> Unit,
    ...
)
```

**After (interfaces export as Swift protocols):**
```kotlin
interface SheetPresenterFactory {
    fun present(parent: UIViewController, controller: UIViewController,
                cornerRadius: Double, backgroundColor: Long,
                onDismiss: () -> Unit, onPresented: () -> Unit)
    fun updateHeight(controller: UIViewController, height: Double)
    fun dismiss(controller: UIViewController, animated: Boolean)
}

interface CircleIconButtonFactory {
    fun create(icon: String, onClick: () -> Unit,
               borderWidth: Double, borderColor: Long): UIViewController
}
```

**Rationale:** Kotlin interfaces export cleanly to Swift as `@objc protocol`. Data classes with lambda fields export awkwardly. This also eliminates the `NativeSheetPresenterFactory` wrapper class entirely.

### Decision 5: iOS Gallery → PHPickerViewController

**In media module** (not platform). Replace custom iOS gallery (PHAsset + manual grid) with native PHPickerViewController.

**Rationale:** Custom gallery loads ALL images into memory (no pagination, no thumbnail cache). PHPicker provides better performance, accessibility, privacy (limited photo access), and is 100% native. Android Paging3 gallery stays unchanged.

## Bug Fixes (Phase 1)

### Critical Bugs

**BUG-1: Button onClick callback stale on recomposition (iOS)**
- **Files:** `NativeCircleIconButton.ios.kt`, `NativeSquircleIconButton.ios.kt`
- **Problem:** `UIKitViewController` factory runs once. `onClick` lambda is captured at creation and never updated. If parent recomposes with new onClick, old closure is still wired.
- **Fix:** Use mutable callback holder pattern (same as NativeSwitch does correctly):
```kotlin
val callbackHolder = remember { mutableStateOf(onClick) }
callbackHolder.value = onClick
// factory uses callbackHolder.value inside tap handler
```

**BUG-2: NSNotificationCenter observer leak (iOS camera)**
- **File:** `YallaCamera.ios.kt` line 273
- **Problem:** `OrientationListener` registered with NSNotificationCenter but never removed in `onDispose`.
- **Fix:** Add `NSNotificationCenter.defaultCenter.removeObserver(orientationListener)` to `onDispose`.

**BUG-3: YallaBitmapCache 1GB minimum (Android)**
- **File:** `YallaBitmapCache.kt` line 20
- **Problem:** `coerceAtLeast(1024 * 1024)` = 1,048,576 KB = 1GB minimum cache! Bug.
- **Fix:** `coerceAtLeast(4 * 1024)` (4MB minimum). Also reduce from 25% to 12.5% (industry standard per Google docs).

**BUG-4: SheetPresenter dismiss timing (iOS)**
- **File:** `SheetPresenter.kt` line 75
- **Problem:** `isProgrammaticDismiss` reset synchronously after `dismissViewControllerAnimated`, but dismiss is async. If onDismiss callback fires after reset, it's incorrectly treated as user-initiated.
- **Fix:** Reset in the dismiss completion handler, not synchronously.

### Important Fixes

**FIX-5: NativeLoadingIndicator missing update block (iOS)**
- **File:** `NativeLoadingIndicator.ios.kt`
- **Problem:** No `update` block — color/background changes after initial composition are ignored.
- **Fix:** Add `update` block that applies new color/backgroundColor to the UIActivityIndicatorView.

**FIX-6: getRootViewController() 3x duplicate (iOS)**
- **Files:** `NativeSheet.ios.kt`, `SystemCameraLauncher.ios.kt`, `ImagePickerLauncher.ios.kt`
- **Fix:** Extract to single `internal fun findRootViewController(): UIViewController` utility.

**FIX-7: Rotation logic duplicate (Android)**
- **Files:** `gallery/ImageHelper.kt` (uses deprecated `android.media.ExifInterface`), `picker/ImageRotationHelper.kt` (uses AndroidX ExifInterface)
- **Fix:** Consolidate to AndroidX version in single `ImageRotationHelper.kt`.

**FIX-8: deprecated UIApplication.setStatusBarStyle (iOS)**
- **File:** `SystemBarColors.ios.kt`
- **Problem:** Deprecated since iOS 9. Also uses `SideEffect` which runs every recomposition (60fps during animation).
- **Fix:** Use per-ViewController `preferredStatusBarStyle` + `setNeedsStatusBarAppearanceUpdate()`. Wrap in `LaunchedEffect` keyed on params.

**FIX-9: CameraX deprecated LocalLifecycleOwner + executor leak (Android)**
- **File:** `YallaCamera.android.kt`, `CameraProviderState.kt`
- **Problem:** `LocalLifecycleOwner.current` deprecated in Compose 1.7+. `Executors.newSingleThreadExecutor()` in `produceState` never shut down.
- **Fix:** Use `androidx.lifecycle.compose.LocalLifecycleOwner`. Use `Dispatchers.IO` instead of leaked executor.

### Performance Improvements

**PERF-1: UIGraphicsBeginImageContextWithOptions → UIGraphicsImageRenderer (iOS)**
- **Files:** `ImageExtensions.kt`, `ImageResizeHelper.kt`, `ImageCompressor.ios.kt`
- **Impact:** 2-3x faster, Metal-backed on supported devices.

**PERF-2: Frame data double-copy (iOS camera)**
- **File:** `CameraDelegates.kt`
- **Problem:** Every frame: CVPixelBuffer → NSData → ByteArray = 2 copies. At 1080p@30fps = ~480MB/s.
- **Fix:** Use `usePinned` to copy directly from CVPixelBuffer, or downsample first with vImage.

**PERF-3: Compression binary search (both platforms)**
- **Files:** `ImageCompressor.android.kt`, `ImageCompressor.ios.kt`
- **Problem:** Linear quality drop (100→85→70→55→40→25) is aggressive, misses sweet spots.
- **Fix:** Binary search between current quality and 10, targeting maxSizeBytes. Add dimension reduction as last resort.

**PERF-4: Paging3 pageSize 10 → 50 (Android gallery)**
- **File:** `YallaGalleryDataSource.kt`
- **Problem:** 10 items fills ~3 rows in a 3-column grid. Constant loading spinners.
- **Fix:** `pageSize = 50, initialLoadSize = 50`. MediaStore queries are cheap (return URIs, not pixels).

**PERF-5: Replace dispatch_group + coroutine mixing with async/awaitAll (iOS picker)**
- **File:** `ImagePickerLauncher.ios.kt`
- **Problem:** GCD dispatch_group + Kotlin coroutine dispatchers = latent race condition on image data list.
- **Fix:** Pure Kotlin `async`/`awaitAll` for coordinating async image loading.

## Refactoring Details

### 1. Button Deduplication (iOS)

**Problem:** `NativeCircleIconButton.ios.kt` and `NativeSquircleIconButton.ios.kt` are ~90% identical.

**Differences to account for:**
- Shape (circle vs rounded rect)
- Factory type (CircleIconButtonFactory vs SquircleIconButtonFactory)
- `NativeCircleIconButton` uses `key(iconType)` wrapper — squircle does not
- `NativeCircleIconButton` has `alpha` parameter — squircle does not

**Solution:** Extract `internal fun NativeIconButton(factory, iconType, onClick, border, background, alpha: Float = 1f, useKey: Boolean = false)` in iosMain. Both public buttons delegate to it.

### 2. Silent Failure → Clear Error

All CompositionLocal reads replaced with:
```kotlin
val config = requireIosConfig()
val factory = config.circleButton  // Never null, crashes with clear message if not installed
```

### 3. NativeSheetPresenterFactory Removal

`NativeSheetPresenterFactory` wrapper class eliminated. `SheetPresenterFactory` becomes an interface that Swift can implement directly as a protocol.

### 4. Camera Session Interruption (iOS — new)

Add handling for `AVCaptureSessionWasInterrupted` / `AVCaptureSessionInterruptionEnded` notifications. Show user feedback when camera is interrupted (e.g., phone call).

### 5. Material 2 → 3 Migration (media module, Android)

Replace `androidx.compose.material.Card` + `ExperimentalMaterialApi` with `androidx.compose.material3.Card` (stable, no opt-in). Drop `compose.material` dependency.

### 6. Paging3 ViewModel Fix (Android gallery)

Store pager flow as `val` in ViewModel (currently creates new Pager on every call). Remove `YallaGalleryViewModelFactory` boilerplate — use `viewModel { }` factory lambda.

## Package Structure

**platform module (unchanged path):**
```
uz.yalla.platform/
├── YallaPlatform.kt              // install() entry point + PlatformConfig interface
├── button/                       // NativeCircle/SquircleIconButton, SheetIconButton
├── indicator/                    // NativeLoadingIndicator
├── picker/                       // NativeWheelDatePicker
├── sheet/                        // NativeSheet, SheetPresenter (iOS)
├── toggle/                       // NativeSwitch
├── icon/                         // IconMapper, IconType
├── system/                       // SystemBarColors, AppUpdateState, VersionComparator
└── model/                        // IconType enum
```

**media module (stays separate, same path):**
```
uz.yalla.media/
├── camera/                       // YallaCamera, CameraState, SystemCameraLauncher
├── gallery/                      // YallaGallery (Android: Paging3, iOS: PHPicker)
├── picker/                       // ImagePickerLauncher, resize, filters
└── utils/                        // CompressionConfig, ImageCompressor
```

## Consumer Migration

### Factory migration (iOS only)

| Before | After |
|--------|-------|
| `LocalCircleIconButtonFactory provides factory` in CompositionLocal | `YallaPlatform.install(IosPlatformConfig.Builder().apply { ... }.build())` once at app start |
| `LocalSquircleIconButtonFactory provides factory` | (included in IosPlatformConfig) |
| `LocalSheetPresenterFactory provides factory` | (included in IosPlatformConfig) |
| `LocalThemeProvider provides provider` | (included in IosPlatformConfig) |

**No import path changes** — media stays at `uz.yalla.media.*`, platform stays at `uz.yalla.platform.*`.

## Phase 2 Preview (future spec)

New native components (factory pattern unless noted):

| Priority | Component | iOS | Android | Pattern |
|----------|-----------|-----|---------|---------|
| 1 | NativeSheet upgrade | UISheetPresentationController (detents) | ModalBottomSheet | Factory |
| 2 | NativeHaptic | UIImpactFeedbackGenerator | VibrationEffect | expect/actual |
| 3 | NativeInAppBrowser | SFSafariViewController | CustomTabs | Factory |
| 4 | NativeNavigation | UINavigationController (large titles) | Compose nav | Factory |
| 5 | NativeTextField | UITextField wrapper | Compose TextField | Factory |
| 6 | NativeSearchBar | UISearchBar | Compose SearchBar | Factory |
| 7 | NativeContextMenu | UIContextMenuInteraction | DropdownMenu | Factory |
| 8 | NativeShareSheet | UIActivityViewController | ShareCompat | Factory |
| 9 | NativeSegmentedControl | UISegmentedControl | TabRow | expect/actual |
| 10 | NativePullToRefresh | UIRefreshControl | Material PullToRefresh | Factory |

## Success Criteria (Phase 1)

1. `YallaPlatform.install()` replaces all scattered CompositionLocal factories
2. All 4 critical bugs fixed (stale callback, observer leak, cache bug, dismiss timing)
3. All 5 important fixes applied
4. All 5 performance improvements applied
5. iOS button code deduplicated
6. iOS gallery uses PHPickerViewController
7. Android rotation logic consolidated to AndroidX ExifInterface
8. Material 2 removed from media module (migrated to M3)
9. No silent failures — all missing config produces clear error messages
10. Factory types are interfaces (not data classes with lambdas)
11. `./gradlew build` passes on both platforms
12. `YallaPlatform.reset()` available for testing
