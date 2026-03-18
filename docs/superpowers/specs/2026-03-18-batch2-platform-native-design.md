# Batch 2: Platform Module — Native iOS Building Blocks

## Overview

Merge the `media` module into `platform` and transform the combined module into a comprehensive native iOS component library with plug-and-play building blocks. The goal: every platform-specific component feels like it was built by a senior Swift developer.

**Approach:** Phased implementation within Batch 2.
- **Phase 1 (this spec):** Restructure + merge media + YallaPlatform.install() architecture + refactor existing components
- **Phase 2 (future spec):** Add new native components (Sheet upgrade, Haptic, Browser, Navigation, TextField, SearchBar, ContextMenu, ShareSheet, SegmentedControl)
- **Phase 3 (future spec):** Full KDoc + MODULE.md + comprehensive tests

## Architecture Decisions

### Hybrid Native Pattern

Two interop patterns coexist based on complexity:

| Pattern | When | Examples |
|---------|------|----------|
| **expect/actual** | Simple UIKit APIs callable from Kotlin/Native | UISwitch, UIDatePicker, UIActivityIndicatorView, UISegmentedControl, Haptics, SystemBars |
| **Factory/delegate** | Complex components requiring real Swift environment | Navigation shell, In-app browser, Sheets with detents, TextField, SearchBar, ContextMenu |

**Rationale:** expect/actual is simpler and self-contained, but cannot access Swift-only APIs (SwiftUI, modern async/await patterns, SFSafariViewController). Factory pattern delegates creation to Swift code, giving full access to the Swift ecosystem.

### YallaPlatform.install() — Centralized Factory Registry

All factory-pattern components register through a single `YallaPlatform.install()` call. This replaces the current scattered CompositionLocal factories (`LocalCircleIconButtonFactory`, `LocalSquircleIconButtonFactory`, `LocalSheetPresenterFactory`) with a unified entry point.

**Current problem:** If a factory CompositionLocal is not provided, buttons silently fail to render (`val factory = ... ?: return`). No error, no warning — the component just disappears.

**Solution:** `YallaPlatform.requireConfig()` throws an informative error if `install()` was not called.

```kotlin
// commonMain — uz.yalla.platform.YallaPlatform
object YallaPlatform {
    internal var config: PlatformConfig? = null
    val isInstalled: Boolean get() = config != null

    internal fun requireConfig(): PlatformConfig =
        config ?: error(
            "YallaPlatform not installed. Call YallaPlatform.install() " +
            "in your app's entry point before using platform components."
        )
}

// commonMain
expect class PlatformConfig

// iosMain
actual class PlatformConfig(
    val sheetPresenterFactory: SheetPresenterFactory,
    val circleButtonFactory: CircleButtonFactory,
    val squircleButtonFactory: SquircleButtonFactory,
    val themeProvider: ThemeProvider? = null,
    // Phase 2 additions:
    // val navigationFactory: NavigationFactory? = null,
    // val browserFactory: BrowserFactory? = null,
)

fun YallaPlatform.install(config: PlatformConfig) {
    this.config = config
}

// androidMain — no factories needed, everything is Compose
actual class PlatformConfig
fun YallaPlatform.install() { config = PlatformConfig() }
```

**Consumer usage (iOS AppDelegate in Swift):**
```swift
YallaPlatform.shared.install(config: PlatformConfig(
    sheetPresenterFactory: MySheetFactory(),
    circleButtonFactory: MyCircleButtonFactory(),
    squircleButtonFactory: MySquircleButtonFactory()
))
```

### iOS Gallery Strategy

**Decision:** Replace custom iOS gallery (PHAsset + manual grid) with native `PHPickerViewController`.

**Rationale:** The custom iOS gallery loads ALL images into memory (no pagination), has no thumbnail cache, and reimplements what Apple's PHPicker already provides with better performance, accessibility, and privacy (limited photo access). Android's custom gallery using Paging3 is well-implemented and stays.

**Impact:**
- Remove: `YallaGallery.ios.kt`, `PhotoLibraryHelper.kt`, `ImageConversionHelper.kt`
- iOS `YallaGallery` actual becomes a wrapper around `PHPickerViewController`
- Android `YallaGallery` actual stays unchanged (Paging3 + MediaStore)

## Package Structure

After merge, the `platform` module organizes into:

```
uz.yalla.platform/
├── YallaPlatform.kt              // install() entry point + PlatformConfig
│
├── ui/                           // Native UI components
│   ├── button/                   // NativeCircle/SquircleIconButton, SheetIconButton
│   ├── indicator/                // NativeLoadingIndicator
│   ├── picker/                   // NativeWheelDatePicker
│   ├── sheet/                    // NativeSheet, SheetPresenter (iOS)
│   ├── toggle/                   // NativeSwitch
│   └── icon/                     // IconMapper, IconType
│
├── media/                        // Device media capabilities
│   ├── camera/                   // YallaCamera, CameraState, SystemCameraLauncher
│   │                             //   Android: CameraX, iOS: AVFoundation
│   ├── gallery/                  // YallaGallery
│   │                             //   Android: Paging3 custom grid, iOS: PHPickerViewController
│   ├── picker/                   // ImagePickerLauncher, resize, filters
│   │                             //   Android: PickVisualMedia, iOS: PHPicker
│   └── compression/              // CompressionConfig, ImageCompressor
│
├── system/                       // System-level utilities
│   ├── bars/                     // SystemBarColors
│   └── update/                   // AppUpdateState, VersionComparator
│
└── util/                         // Internal helpers
    └── Color.kt                  // iOS Color-to-UIColor extension
```

## Refactoring Details

### 1. Button Deduplication (iOS)

**Problem:** `NativeCircleIconButton.ios.kt` and `NativeSquircleIconButton.ios.kt` are 95% identical. Only shape and factory differ.

**Solution:** Extract shared `internal fun NativeIconButton(factory, iconType, onClick, border, background, alpha)` in `iosMain`. Both public buttons delegate to it.

### 2. Silent Failure Fix

**Before:**
```kotlin
val factory = LocalCircleIconButtonFactory.current ?: return  // SILENT!
```

**After:**
```kotlin
val factory = YallaPlatform.requireConfig().circleButtonFactory  // LOUD!
```

### 3. Unsafe Border Color Cast

**Before:**
```kotlin
(border?.brush as? SolidColor)?.value?.toArgb()?.toLong() ?: 0L
```

**After:**
```kotlin
border?.toArgbLong() ?: 0L  // Extension that logs warning for non-solid brushes
```

### 4. Rotation Logic Deduplication (Android)

**Problem:** `rotateImageIfRequired()` exists in both `gallery/ImageHelper.kt` and `picker/ImageRotationHelper.kt`.

**Solution:** Single `ImageRotationHelper.kt` in `media/util/` package, used by both gallery and picker.

### 5. iOS Gallery to PHPickerViewController

Remove custom PHAsset grid on iOS. Replace with `PHPickerViewController` wrapper that matches the `YallaGallery` expect API surface.

### 6. media Module Deletion

1. Move all `media/src/` code into `platform/src/` under `uz.yalla.platform.media.*`
2. Merge `media/build.gradle.kts` dependencies into `platform/build.gradle.kts`
3. Delete `media/` module directory
4. Remove from `settings.gradle.kts`
5. Update all consumer imports (`uz.yalla.media.*` to `uz.yalla.platform.media.*`)

## Dependency Changes

### platform/build.gradle.kts additions (from media merge)

```kotlin
// Android
androidMain.dependencies {
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.paging.compose)
    implementation(libs.paging.runtime)
}

// Common
commonMain.dependencies {
    // existing platform deps +
    implementation(libs.paging.common)  // if multiplatform paging
}
```

## Consumer Migration

### Import changes for YallaClient

| Before | After |
|--------|-------|
| `import uz.yalla.media.camera.*` | `import uz.yalla.platform.media.camera.*` |
| `import uz.yalla.media.gallery.*` | `import uz.yalla.platform.media.gallery.*` |
| `import uz.yalla.media.picker.*` | `import uz.yalla.platform.media.picker.*` |
| `import uz.yalla.media.utils.*` | `import uz.yalla.platform.media.compression.*` |

### Factory migration (iOS)

| Before | After |
|--------|-------|
| `LocalCircleIconButtonFactory provides factory` | `YallaPlatform.install(PlatformConfig(...))` |
| `LocalSquircleIconButtonFactory provides factory` | (included in PlatformConfig) |
| `LocalSheetPresenterFactory provides factory` | (included in PlatformConfig) |
| `LocalThemeProvider provides provider` | (included in PlatformConfig) |

## Phase 2 Preview (future spec)

New native components to be added after Phase 1:

| Priority | Component | iOS Implementation | Android Implementation |
|----------|-----------|-------------------|----------------------|
| 1 | NativeSheet upgrade | UISheetPresentationController (detents, dismiss control) | ModalBottomSheet (stays) |
| 2 | NativeHaptic | UIImpactFeedbackGenerator + UINotificationFeedbackGenerator | VibrationEffect |
| 3 | NativeInAppBrowser | SFSafariViewController | CustomTabs |
| 4 | NativeNavigation | UINavigationController (large titles, connected scroll) | Compose navigation (stays) |
| 5 | NativeTextField | UITextField wrapper | Compose TextField (stays) |
| 6 | NativeSearchBar | UISearchBar | Compose SearchBar (stays) |
| 7 | NativeContextMenu | UIContextMenuInteraction | Compose DropdownMenu (stays) |
| 8 | NativeShareSheet | UIActivityViewController | ShareCompat |
| 9 | NativeSegmentedControl | UISegmentedControl | Compose TabRow (stays) |
| 10 | NativePullToRefresh | UIRefreshControl | Material PullToRefresh (stays) |

Each will be added to `PlatformConfig` as an optional factory field (nullable, with sensible defaults or clear error messages).

## Phase 3 Preview (future spec)

- 100% KDoc on all public APIs
- MODULE.md for Dokka
- Tests: VersionComparator logic, CompressionConfig presets, CameraMode conversion, factory error handling, ImagePicker selection modes
- UI tests where applicable (compose-ui-test)

## Success Criteria (Phase 1)

1. `media` module fully merged into `platform` — no `media/` directory remains
2. `YallaPlatform.install()` replaces all scattered CompositionLocal factories
3. All existing components work identically (no behavior regression)
4. iOS button code deduplicated
5. iOS gallery uses PHPickerViewController
6. Android rotation logic consolidated
7. No silent failures — all missing config produces clear error messages
8. `./gradlew build` passes
9. All consumer imports updated
