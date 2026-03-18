# Batch 2 Phase 1: Platform + Media Refactoring Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor platform module with centralized YallaPlatform.install() architecture, fix critical bugs in both platform and media modules, and apply performance improvements.

**Architecture:** Replace scattered CompositionLocal factories with `interface PlatformConfig` + platform-specific implementations (`IosPlatformConfig` with builder, `AndroidPlatformConfig`). Factory types become interfaces (Swift protocol export). Media stays separate module.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform 1.10+, UIKit interop, CameraX, Paging3, AVFoundation

**Spec:** `docs/superpowers/specs/2026-03-18-batch2-platform-native-design.md`

---

## Chunk 1: Platform Architecture Foundation

These tasks create the new YallaPlatform.install() infrastructure. Must complete before Chunk 2.

### Task 1: Create PlatformConfig interface and YallaPlatform object

**Files:**
- Create: `platform/src/commonMain/kotlin/uz/yalla/platform/YallaPlatform.kt`

- [ ] **Step 1: Create YallaPlatform.kt**

```kotlin
package uz.yalla.platform

import kotlin.jvm.JvmStatic

/**
 * Marker interface for platform-specific configuration.
 *
 * Each platform provides its own implementation:
 * - iOS: [IosPlatformConfig] with native component factories
 * - Android: [AndroidPlatformConfig] (no factories needed)
 *
 * @since 0.0.1
 */
interface PlatformConfig

/**
 * Central entry point for platform module initialization.
 *
 * Call [install] once at app startup before using any native platform component.
 * On iOS, pass an [IosPlatformConfig] with factory implementations.
 * On Android, call [installAndroid] (no configuration needed).
 *
 * @since 0.0.1
 */
object YallaPlatform {
    @PublishedApi
    internal var config: PlatformConfig? = null

    /** Whether [install] has been called. */
    val isInstalled: Boolean get() = config != null

    /** Register platform configuration. Must be called before using platform components. */
    fun install(config: PlatformConfig) {
        this.config = config
    }

    /** Retrieve typed config or throw with clear installation instructions. */
    @PublishedApi
    internal inline fun <reified T : PlatformConfig> requireConfig(): T =
        (config as? T) ?: error(
            "YallaPlatform not installed. Call YallaPlatform.install() " +
            "in your app's entry point before using platform components."
        )

    /** Reset config — for testing only. Prevents global state pollution across tests. */
    fun reset() { config = null }
}
```

- [ ] **Step 2: Verify it compiles**

Run: `./gradlew :platform:compileKotlinMetadata`

- [ ] **Step 3: Commit**

```
feat(platform): add YallaPlatform singleton and PlatformConfig interface
```

### Task 2: Create IosPlatformConfig with factory interfaces

**Files:**
- Create: `platform/src/iosMain/kotlin/uz/yalla/platform/config/IosPlatformConfig.kt`
- Create: `platform/src/iosMain/kotlin/uz/yalla/platform/config/SheetPresenterFactory.kt`
- Create: `platform/src/iosMain/kotlin/uz/yalla/platform/config/CircleIconButtonFactory.kt`
- Create: `platform/src/iosMain/kotlin/uz/yalla/platform/config/SquircleIconButtonFactory.kt`
- Create: `platform/src/iosMain/kotlin/uz/yalla/platform/config/ThemeProvider.kt`

- [ ] **Step 1: Create factory interfaces**

Each factory is a Kotlin interface that exports to Swift as a protocol.

`SheetPresenterFactory.kt`:
```kotlin
package uz.yalla.platform.config

import platform.UIKit.UIViewController

/**
 * Factory for presenting and managing native iOS sheets.
 * Implement in Swift and register via [IosPlatformConfig.Builder].
 * @since 0.0.1
 */
interface SheetPresenterFactory {
    fun present(
        parent: UIViewController,
        controller: UIViewController,
        cornerRadius: Double,
        backgroundColor: Long,
        onDismiss: () -> Unit,
        onPresented: () -> Unit,
    )
    fun updateHeight(controller: UIViewController, height: Double)
    fun updateBackground(controller: UIViewController, backgroundColor: Long)
    fun updateDismissBehavior(
        controller: UIViewController,
        dismissEnabled: Boolean,
        onDismissAttempt: () -> Unit,
    )
    fun dismiss(controller: UIViewController, animated: Boolean)
}
```

`CircleIconButtonFactory.kt`:
```kotlin
package uz.yalla.platform.config

import platform.UIKit.UIViewController

/**
 * Factory for creating circle-shaped native iOS icon buttons.
 * @since 0.0.1
 */
interface CircleIconButtonFactory {
    fun create(
        icon: String,
        onClick: () -> Unit,
        borderWidth: Double,
        borderColor: Long,
    ): UIViewController
}
```

`SquircleIconButtonFactory.kt` — same pattern, different name.

`ThemeProvider.kt`:
```kotlin
package uz.yalla.platform.config

import androidx.compose.runtime.Composable

/**
 * Provides Yalla theme context for native sheets presented outside the Compose tree.
 * @since 0.0.1
 */
interface ThemeProvider {
    @Composable
    fun provide(content: @Composable () -> Unit)
}
```

- [ ] **Step 2: Create IosPlatformConfig with Builder**

`IosPlatformConfig.kt`:
```kotlin
package uz.yalla.platform.config

import uz.yalla.platform.PlatformConfig
import uz.yalla.platform.YallaPlatform

/**
 * iOS platform configuration containing native component factories.
 * Use [Builder] to construct, then pass to [YallaPlatform.install].
 * @since 0.0.1
 */
class IosPlatformConfig private constructor(
    val sheetPresenter: SheetPresenterFactory,
    val circleButton: CircleIconButtonFactory,
    val squircleButton: SquircleIconButtonFactory,
    val themeProvider: ThemeProvider? = null,
) : PlatformConfig {

    class Builder {
        var sheetPresenter: SheetPresenterFactory? = null
        var circleButton: CircleIconButtonFactory? = null
        var squircleButton: SquircleIconButtonFactory? = null
        var themeProvider: ThemeProvider? = null

        fun build(): IosPlatformConfig = IosPlatformConfig(
            sheetPresenter = requireNotNull(sheetPresenter) {
                "sheetPresenter is required. Provide a SheetPresenterFactory implementation."
            },
            circleButton = requireNotNull(circleButton) {
                "circleButton is required. Provide a CircleIconButtonFactory implementation."
            },
            squircleButton = requireNotNull(squircleButton) {
                "squircleButton is required. Provide a SquircleIconButtonFactory implementation."
            },
            themeProvider = themeProvider,
        )
    }
}

/** Retrieve the iOS config or throw. Used by all iOS platform components. */
internal fun requireIosConfig(): IosPlatformConfig =
    YallaPlatform.requireConfig<IosPlatformConfig>()
```

- [ ] **Step 3: Verify iOS compilation**

Run: `./gradlew :platform:compileKotlinIosArm64`

- [ ] **Step 4: Commit**

```
feat(platform): add IosPlatformConfig with factory interfaces and builder
```

### Task 3: Create AndroidPlatformConfig

**Files:**
- Create: `platform/src/androidMain/kotlin/uz/yalla/platform/config/AndroidPlatformConfig.kt`

- [ ] **Step 1: Create AndroidPlatformConfig**

```kotlin
package uz.yalla.platform.config

import uz.yalla.platform.PlatformConfig
import uz.yalla.platform.YallaPlatform

/**
 * Android platform configuration. No factories needed — all components use Compose.
 * @since 0.0.1
 */
class AndroidPlatformConfig : PlatformConfig

/** Convenience installer for Android — no configuration required. */
fun YallaPlatform.installAndroid() {
    install(AndroidPlatformConfig())
}
```

- [ ] **Step 2: Verify Android compilation**

Run: `./gradlew :platform:compileDebugKotlinAndroid`

- [ ] **Step 3: Commit**

```
feat(platform): add AndroidPlatformConfig
```

### Task 4: Extract findRootViewController utility (iOS)

**Files:**
- Create: `platform/src/iosMain/kotlin/uz/yalla/platform/util/ViewControllerUtil.kt`

- [ ] **Step 1: Create shared utility**

Extract the duplicated root VC finder used in NativeSheet, SystemCameraLauncher, ImagePickerLauncher:

```kotlin
package uz.yalla.platform.util

import platform.UIKit.*

/**
 * Finds the topmost presented UIViewController from the key window.
 * Traverses the presentation chain to find the leaf controller.
 * @since 0.0.1
 */
internal fun findRootViewController(): UIViewController? {
    val scenes = UIApplication.sharedApplication.connectedScenes
    val windowScene = scenes.firstOrNull { it is UIWindowScene } as? UIWindowScene
    val window = windowScene?.windows?.firstOrNull { (it as? UIWindow)?.isKeyWindow == true } as? UIWindow
    var controller = window?.rootViewController
    while (controller?.presentedViewController != null) {
        controller = controller.presentedViewController
    }
    return controller
}
```

- [ ] **Step 2: Commit**

```
refactor(platform): extract findRootViewController utility for iOS
```

---

## Chunk 2: Platform Component Migration

Migrate all iOS components from CompositionLocal factories to YallaPlatform. Fix bugs along the way.

### Task 5: Migrate and deduplicate iOS buttons + fix stale callback

**Files:**
- Create: `platform/src/iosMain/kotlin/uz/yalla/platform/button/NativeIconButton.kt` (shared internal)
- Modify: `platform/src/iosMain/kotlin/uz/yalla/platform/button/NativeCircleIconButton.ios.kt`
- Modify: `platform/src/iosMain/kotlin/uz/yalla/platform/button/NativeSquircleIconButton.ios.kt`

- [ ] **Step 1: Create shared NativeIconButton**

Internal composable that both buttons delegate to. Fixes BUG-1 (stale callback) using mutable state holder pattern:

```kotlin
package uz.yalla.platform.button

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.interop.UIKitViewController
import platform.UIKit.UIViewController
import uz.yalla.platform.model.IconType

@Composable
internal fun NativeIconButton(
    factory: (String, () -> Unit, Double, Long) -> UIViewController,
    iconType: IconType,
    onClick: () -> Unit,
    border: androidx.compose.foundation.BorderStroke?,
    background: Modifier,
    alpha: Float = 1f,
    useKey: Boolean = false,
    modifier: Modifier = Modifier,
) {
    // FIX BUG-1: mutable callback holder — updates on every recomposition
    val currentOnClick = rememberUpdatedState(onClick)

    val iconName = iconType.toAssetName()
    val borderWidth = border?.width?.value?.toDouble() ?: 0.0
    val borderColor = (border?.brush as? SolidColor)?.value?.toArgb()?.toLong() ?: 0L

    val content: @Composable () -> Unit = {
        UIKitViewController(
            factory = { factory(iconName, { currentOnClick.value.invoke() }, borderWidth, borderColor) },
            modifier = background.then(modifier),
            update = { controller ->
                controller.view.alpha = alpha.toDouble()
                controller.view.setBackgroundColor(null)
                controller.view.setOpaque(false)
            },
        )
    }

    if (useKey) {
        key(iconType) { content() }
    } else {
        content()
    }
}
```

- [ ] **Step 2: Simplify NativeCircleIconButton.ios.kt**

Replace entire implementation to delegate to NativeIconButton + use requireIosConfig():

```kotlin
@Composable
actual fun NativeCircleIconButton(
    iconType: IconType,
    onClick: () -> Unit,
    modifier: Modifier,
    border: BorderStroke?,
    background: Modifier,
    alpha: Float,
) {
    val config = requireIosConfig()
    NativeIconButton(
        factory = { icon, click, bw, bc -> config.circleButton.create(icon, click, bw, bc) },
        iconType = iconType,
        onClick = onClick,
        border = border,
        background = background,
        alpha = alpha,
        useKey = true,
        modifier = modifier,
    )
}
```

- [ ] **Step 3: Simplify NativeSquircleIconButton.ios.kt** — same pattern with squircleButton factory.

- [ ] **Step 4: Verify compilation**

Run: `./gradlew :platform:compileKotlinIosArm64`

- [ ] **Step 5: Commit**

```
refactor(platform): deduplicate iOS buttons, fix stale onClick callback (BUG-1)
```

### Task 6: Migrate iOS sheet + fix dismiss timing + remove wrapper

**Files:**
- Modify: `platform/src/iosMain/kotlin/uz/yalla/platform/sheet/NativeSheet.ios.kt`
- Modify: `platform/src/iosMain/kotlin/uz/yalla/platform/sheet/SheetPresenter.kt`
- Delete: `platform/src/iosMain/kotlin/uz/yalla/platform/Factory.kt` (after all migrations)

- [ ] **Step 1: Update NativeSheet.ios.kt to use requireIosConfig()**

Replace `LocalSheetPresenterFactory.current` and `LocalThemeProvider.current` with:
```kotlin
val config = requireIosConfig()
val factory = config.sheetPresenter
val themeProvider = config.themeProvider
```

- [ ] **Step 2: Fix BUG-4 in SheetPresenter.kt — dismiss timing**

Move `isProgrammaticDismiss = false` into the dismiss completion handler:

```kotlin
fun dismiss(animated: Boolean = true) {
    isProgrammaticDismiss = true
    factory.dismiss(presentedController, animated) // dismiss is async
    // DO NOT reset here — reset in the dismiss callback
}

// In the dismiss callback/delegate:
fun onDismissCompleted() {
    isProgrammaticDismiss = false
    // ... rest of cleanup
}
```

- [ ] **Step 3: Update SheetPresenter constructor to use interface-based factory**

Replace data class `SheetPresenterFactory` references with the new interface from `uz.yalla.platform.config.SheetPresenterFactory`.

- [ ] **Step 4: Commit**

```
refactor(platform): migrate sheet to YallaPlatform, fix dismiss timing (BUG-4)
```

### Task 7: Fix NativeLoadingIndicator missing update block (iOS)

**Files:**
- Modify: `platform/src/iosMain/kotlin/uz/yalla/platform/indicator/NativeLoadingIndicator.ios.kt`

- [ ] **Step 1: Add update block**

```kotlin
UIKitView(
    factory = { /* existing factory code */ },
    update = { indicator ->
        indicator.color = color?.toUIColor()
        indicator.backgroundColor = backgroundColor?.toUIColor()
    },
    // ...
)
```

- [ ] **Step 2: Commit**

```
fix(platform): add update block to NativeLoadingIndicator for dynamic color changes
```

### Task 8: Fix deprecated SystemBarColors (iOS)

**Files:**
- Modify: `platform/src/iosMain/kotlin/uz/yalla/platform/system/SystemBarColors.ios.kt`

- [ ] **Step 1: Replace deprecated API + SideEffect → LaunchedEffect**

Replace `UIApplication.sharedApplication.setStatusBarStyle()` with per-ViewController approach. Wrap in `LaunchedEffect` keyed on params to avoid 60fps calls during animations.

- [ ] **Step 2: Commit**

```
fix(platform): replace deprecated UIApplication.setStatusBarStyle with VC-based approach
```

### Task 9: Delete old Factory.kt and CompositionLocal declarations

**Files:**
- Delete: `platform/src/iosMain/kotlin/uz/yalla/platform/Factory.kt`

- [ ] **Step 1: Verify no remaining references to old CompositionLocals**

Run: `grep -r "LocalCircleIconButtonFactory\|LocalSquircleIconButtonFactory\|LocalSheetPresenterFactory\|LocalThemeProvider\|NativeSheetPresenterFactory" platform/src/`

Expected: No matches (all migrated in Tasks 5-6).

- [ ] **Step 2: Delete Factory.kt**

- [ ] **Step 3: Full platform build**

Run: `./gradlew :platform:build`

- [ ] **Step 4: Commit**

```
refactor(platform): remove legacy CompositionLocal factories, replaced by YallaPlatform.install()
```

---

## Chunk 3: Media Android Fixes

Independent of platform chunks. Can run in parallel with Chunk 2.

### Task 10: Fix YallaBitmapCache 1GB minimum (BUG-3)

**Files:**
- Modify: `media/src/androidMain/kotlin/uz/yalla/media/picker/YallaBitmapCache.kt`

- [ ] **Step 1: Fix the cache size calculation**

Change line 19-20:
```kotlin
// BEFORE (BUG): 25% of heap, minimum 1GB!
val cacheSize = (maxMemory * 0.25).toInt().coerceAtLeast(1024 * 1024)

// AFTER: 12.5% of heap (Google recommended), minimum 4MB
val cacheSize = (maxMemory / 8).toInt().coerceAtLeast(4 * 1024)
```

- [ ] **Step 2: Commit**

```
fix(media): fix YallaBitmapCache 1GB minimum bug, reduce to 12.5% heap (BUG-3)
```

### Task 11: Consolidate rotation logic (Android)

**Files:**
- Delete: `media/src/androidMain/kotlin/uz/yalla/media/gallery/ImageHelper.kt` (rotation function only)
- Modify: `media/src/androidMain/kotlin/uz/yalla/media/picker/ImageRotationHelper.kt` (keep AndroidX version)
- Modify: `media/src/androidMain/kotlin/uz/yalla/media/gallery/YallaGallery.android.kt` (update import)

- [ ] **Step 1: Move any unique gallery helpers to appropriate location**

Keep `loadThumbnailBitmap()` and `getOriginalImageByteArray()` from ImageHelper.kt (these are gallery-specific). Only the `rotateImageIfRequired()` function is duplicated — remove from ImageHelper, use the AndroidX version from ImageRotationHelper everywhere.

- [ ] **Step 2: Update imports in gallery code**

- [ ] **Step 3: Commit**

```
refactor(media): consolidate rotation logic to AndroidX ExifInterface (FIX-7)
```

### Task 12: Fix CameraX lifecycle issues (Android)

**Files:**
- Modify: `media/src/androidMain/kotlin/uz/yalla/media/camera/YallaCamera.android.kt`
- Modify: `media/src/androidMain/kotlin/uz/yalla/media/camera/CameraProviderState.kt`

- [ ] **Step 1: Fix deprecated LocalLifecycleOwner import**

```kotlin
// BEFORE
import androidx.compose.ui.platform.LocalLifecycleOwner
// AFTER
import androidx.lifecycle.compose.LocalLifecycleOwner
```

- [ ] **Step 2: Fix executor leak in CameraProviderState.kt**

Replace `Executors.newSingleThreadExecutor().asCoroutineDispatcher()` with `Dispatchers.IO`:

```kotlin
val cameraProvider = produceState<ProcessCameraProvider?>(null) {
    withContext(Dispatchers.IO) {
        value = ProcessCameraProvider.getInstance(context).get()
    }
}
```

- [ ] **Step 3: Commit**

```
fix(media): fix CameraX deprecated LocalLifecycleOwner and executor leak (FIX-9)
```

### Task 13: Paging3 improvements (Android gallery)

**Files:**
- Modify: `media/src/androidMain/kotlin/uz/yalla/media/gallery/viewmodel/YallaGalleryViewModel.kt`
- Delete: `media/src/androidMain/kotlin/uz/yalla/media/gallery/viewmodel/YallaGalleryViewModelFactory.kt`
- Modify: `media/src/androidMain/kotlin/uz/yalla/media/gallery/YallaGallery.android.kt`

- [ ] **Step 1: Fix ViewModel — cache pager flow, increase page size**

```kotlin
class YallaGalleryViewModel(
    private val yallaGalleryRepository: YallaGalleryRepository,
) : ViewModel() {
    val images: Flow<PagingData<YallaMediaImage>> =
        Pager(
            config = PagingConfig(
                pageSize = 50,          // was 10
                initialLoadSize = 50,   // was 10
                enablePlaceholders = true,
            ),
        ) {
            yallaGalleryRepository.getPicturePagingSource()
        }.flow.cachedIn(viewModelScope)
}
```

- [ ] **Step 2: Remove YallaGalleryViewModelFactory, use viewModel { } lambda in gallery**

- [ ] **Step 3: Commit**

```
perf(media): increase gallery page size to 50, cache pager flow, remove factory boilerplate (PERF-4)
```

### Task 14: Material 2 → 3 migration (media)

**Files:**
- Modify: `media/src/androidMain/kotlin/uz/yalla/media/gallery/YallaGallery.android.kt`
- Modify: `media/src/iosMain/kotlin/uz/yalla/media/gallery/YallaGallery.ios.kt` (if using M2 imports)
- Modify: `media/build.gradle.kts` — remove `compose.material`, ensure `compose.material3`

- [ ] **Step 1: Replace Card imports**

```kotlin
// BEFORE
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
// AFTER
import androidx.compose.material3.Card
```

Material 3 Card has stable `onClick` parameter — no `ExperimentalMaterialApi` needed.

- [ ] **Step 2: Remove compose.material from build.gradle.kts**

- [ ] **Step 3: Verify build**

Run: `./gradlew :media:build`

- [ ] **Step 4: Commit**

```
refactor(media): migrate Material 2 to Material 3, drop compose.material dependency
```

---

## Chunk 4: Media iOS Fixes

Independent of platform chunks. Can run in parallel with Chunks 2 and 3.

### Task 15: Fix NSNotificationCenter observer leak (BUG-2)

**Files:**
- Modify: `media/src/iosMain/kotlin/uz/yalla/media/camera/YallaCamera.ios.kt`

- [ ] **Step 1: Add observer cleanup in DisposableEffect onDispose**

Find the `DisposableEffect` that calls `stopSession` and add:

```kotlin
onDispose {
    orientationListener.value?.let {
        NSNotificationCenter.defaultCenter.removeObserver(it)
    }
    stopSession(captureSession)
    state.isCameraReady = false
}
```

- [ ] **Step 2: Commit**

```
fix(media): remove NSNotificationCenter observer on dispose (BUG-2)
```

### Task 16: Add camera session interruption handling (iOS)

**Files:**
- Modify: `media/src/iosMain/kotlin/uz/yalla/media/camera/YallaCamera.ios.kt`

- [ ] **Step 1: Register for interruption notifications**

```kotlin
val interruptionObserver = NSNotificationCenter.defaultCenter.addObserverForName(
    name = AVCaptureSessionWasInterruptedNotification,
    `object` = captureSession,
    queue = NSOperationQueue.mainQueue,
) { _ -> state.isCameraReady = false }

val resumeObserver = NSNotificationCenter.defaultCenter.addObserverForName(
    name = AVCaptureSessionInterruptionEndedNotification,
    `object` = captureSession,
    queue = NSOperationQueue.mainQueue,
) { _ -> state.isCameraReady = true }
```

- [ ] **Step 2: Clean up in onDispose**

- [ ] **Step 3: Commit**

```
feat(media): handle camera session interruption/resume on iOS
```

### Task 17: UIGraphicsImageRenderer migration (iOS)

**Files:**
- Modify: `media/src/iosMain/kotlin/uz/yalla/media/camera/ImageExtensions.kt`
- Modify: `media/src/iosMain/kotlin/uz/yalla/media/picker/ImageResizeHelper.kt`
- Modify: `media/src/iosMain/kotlin/uz/yalla/media/utils/ImageCompressor.ios.kt`

- [ ] **Step 1: Replace UIGraphicsBeginImageContextWithOptions with UIGraphicsImageRenderer**

In each file, replace the legacy pattern:
```kotlin
// BEFORE
UIGraphicsBeginImageContextWithOptions(size, false, 1.0)
image.drawInRect(CGRectMake(0.0, 0.0, width, height))
val result = UIGraphicsGetImageFromCurrentImageContext()
UIGraphicsEndImageContext()

// AFTER
val renderer = UIGraphicsImageRenderer(size = size)
val result = renderer.imageWithActions { context ->
    image.drawInRect(CGRectMake(0.0, 0.0, width, height))
}
```

- [ ] **Step 2: Commit**

```
perf(media): replace legacy UIGraphicsBeginImageContextWithOptions with UIGraphicsImageRenderer (PERF-1)
```

### Task 18: iOS Gallery → PHPickerViewController

**Files:**
- Rewrite: `media/src/iosMain/kotlin/uz/yalla/media/gallery/YallaGallery.ios.kt`
- Delete: `media/src/iosMain/kotlin/uz/yalla/media/gallery/PhotoLibraryHelper.kt`
- Delete: `media/src/iosMain/kotlin/uz/yalla/media/gallery/ImageConversionHelper.kt`

- [ ] **Step 1: Rewrite YallaGallery.ios.kt to use PHPickerViewController**

Replace custom PHAsset grid with native PHPicker. The actual fun should present PHPickerViewController and return selected images through the existing callback API.

- [ ] **Step 2: Delete unused helpers (PhotoLibraryHelper, ImageConversionHelper)**

- [ ] **Step 3: Verify build**

Run: `./gradlew :media:compileKotlinIosArm64`

- [ ] **Step 4: Commit**

```
refactor(media): replace custom iOS gallery with native PHPickerViewController
```

### Task 19: Replace dispatch_group with async/awaitAll (iOS picker)

**Files:**
- Modify: `media/src/iosMain/kotlin/uz/yalla/media/picker/ImagePickerLauncher.ios.kt`

- [ ] **Step 1: Replace processPickerResults to use coroutines**

Replace `dispatch_group` + `dispatch_group_enter/leave` pattern with:

```kotlin
private suspend fun processPickerResults(
    results: List<PHPickerResult>,
    // ...
): List<ByteArray> = coroutineScope {
    results.map { result ->
        async(Dispatchers.Default) {
            suspendCancellableCoroutine<ByteArray?> { continuation ->
                result.itemProvider.loadDataRepresentationForTypeIdentifier("public.image") { data, error ->
                    if (error != null || data == null) {
                        continuation.resume(null) {}
                    } else {
                        continuation.resume(data.toByteArray()) {}
                    }
                }
            }
        }
    }.awaitAll().filterNotNull()
}
```

- [ ] **Step 2: Commit**

```
refactor(media): replace dispatch_group with async/awaitAll in iOS picker (PERF-5)
```

---

## Chunk 5: Shared Performance Improvements

### Task 20: Compression binary search (both platforms)

**Files:**
- Modify: `media/src/androidMain/kotlin/uz/yalla/media/utils/ImageCompressor.android.kt`
- Modify: `media/src/iosMain/kotlin/uz/yalla/media/utils/ImageCompressor.ios.kt`

- [ ] **Step 1: Replace linear quality descent with binary search (Android)**

```kotlin
// Binary search for optimal quality
var lo = 10
var hi = config.quality
var bestBytes: ByteArray? = null

while (lo <= hi) {
    val mid = (lo + hi) / 2
    val stream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, mid, stream)
    val result = stream.toByteArray()

    if (result.size <= config.maxSizeBytes) {
        bestBytes = result
        lo = mid + 1  // try higher quality
    } else {
        hi = mid - 1  // try lower quality
    }
}

// If even quality 10 is too big, scale down dimensions
if (bestBytes == null) {
    // Reduce dimensions by 50% and retry
    val smallerBitmap = Bitmap.createScaledBitmap(
        scaledBitmap,
        scaledBitmap.width / 2,
        scaledBitmap.height / 2,
        true,
    )
    val stream = ByteArrayOutputStream()
    smallerBitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
    bestBytes = stream.toByteArray()
}
```

- [ ] **Step 2: Apply same pattern to iOS compressor** (using UIImageJPEGRepresentation quality as Double 0.0-1.0)

- [ ] **Step 3: Commit**

```
perf(media): replace linear quality descent with binary search compression (PERF-3)
```

### Task 21: Fix frame data double-copy (iOS camera)

**Files:**
- Modify: `media/src/iosMain/kotlin/uz/yalla/media/camera/CameraDelegates.kt`

- [ ] **Step 1: Reduce to single copy using usePinned**

Replace NSData intermediate step with direct CVPixelBuffer → ByteArray copy:

```kotlin
val pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) ?: return
CVPixelBufferLockBaseAddress(pixelBuffer, kCVPixelBufferLock_ReadOnly)
val baseAddress = CVPixelBufferGetBaseAddress(pixelBuffer)
val dataSize = CVPixelBufferGetDataSize(pixelBuffer)

val byteArray = ByteArray(dataSize.toInt())
byteArray.usePinned { pinned ->
    memcpy(pinned.addressOf(0), baseAddress, dataSize)
}

CVPixelBufferUnlockBaseAddress(pixelBuffer, kCVPixelBufferLock_ReadOnly)
onFrame(byteArray)
```

- [ ] **Step 2: Commit**

```
perf(media): eliminate frame data double-copy in iOS camera delegate (PERF-2)
```

---

## Chunk 6: Verification

### Task 22: Full build and verification

- [ ] **Step 1: Full Gradle build**

Run: `./gradlew build`

Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Run static analysis**

Run: `./gradlew detekt`
Run: `./gradlew spotlessCheck`

Fix any violations.

- [ ] **Step 3: Verify no old CompositionLocal references remain**

```bash
grep -r "LocalCircleIconButtonFactory\|LocalSquircleIconButtonFactory\|LocalSheetPresenterFactory\|LocalThemeProvider\|NativeSheetPresenterFactory" platform/src/ media/src/
```

Expected: No matches.

- [ ] **Step 4: Verify no Material 2 imports remain in media**

```bash
grep -r "androidx.compose.material\." media/src/ | grep -v "material3"
```

Expected: No matches.

- [ ] **Step 5: Final commit with all passing**

```
chore(sdk): verify batch 2 phase 1 — all builds passing, no legacy references
```

---

## Parallelization Guide

For subagent-driven-development, these chunks can be parallelized:

```
Chunk 1 (foundation) ──┐
                        ├──> Chunk 2 (platform migration, depends on Chunk 1)
Chunk 3 (media Android) ──> independent
Chunk 4 (media iOS) ──────> independent
Chunk 5 (shared perf) ────> depends on Chunk 3 + 4 for compression files
Chunk 6 (verification) ───> depends on ALL
```

**Maximum parallelism: 3 agents** — Chunk 1→2 (sequential), Chunk 3, Chunk 4.
After Chunks 2-4 complete: Chunk 5, then Chunk 6.

## Success Checklist

- [ ] `YallaPlatform.install()` replaces all CompositionLocal factories
- [ ] BUG-1: Button onClick updates on recomposition
- [ ] BUG-2: Camera observer properly cleaned up
- [ ] BUG-3: BitmapCache uses 12.5% heap, 4MB minimum
- [ ] BUG-4: Sheet dismiss timing uses completion handler
- [ ] FIX-5 through FIX-9: All important fixes applied
- [ ] PERF-1 through PERF-5: All performance improvements applied
- [ ] iOS buttons deduplicated to shared NativeIconButton
- [ ] iOS gallery uses PHPickerViewController
- [ ] Android rotation logic uses AndroidX ExifInterface
- [ ] Material 2 removed from media module
- [ ] Factory types are interfaces (Swift protocol export)
- [ ] `./gradlew build` passes
- [ ] `./gradlew detekt` passes
- [ ] `./gradlew spotlessCheck` passes
