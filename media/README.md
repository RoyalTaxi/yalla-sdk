# `uz.yalla.sdk:media`

Cross-platform (Compose Multiplatform) media module for **picking photos from the gallery** and
**capturing photos with the camera**, plus a single, well-tuned **image-compression** pipeline.

The public API is `@Composable` and identical on Android and iOS. The *presentation* (the actual
gallery / camera UI) is delegated to a native factory that each platform's SDK injects at startup —
so the picker opens in the host's real UIKit / Activity context, with no Compose coupling and no
hacks to find a view controller or activity to present from.

---

## 1. Why this design

The first version embedded a custom camera (AVFoundation / CameraX) and a custom gallery grid inside
the shared module. It was slow (gallery took seconds to open, sometimes never), fragile (camera
froze), and large. The rewrite follows one rule:

> **The shared module owns *behaviour and contracts*. The native SDKs own *presentation*.**

This is the same pattern the rest of the SDK uses for `SheetFactory`, `SnackbarFactory`, etc. The
shared `media` module declares a `MediaFactory` interface; `yalla-sdk-android` and `yalla-sdk-ios`
each provide a native implementation, and the host app injects it once via `YallaMedia.install(...)`.

```
┌───────────────────────────── commonMain (this module) ─────────────────────────────┐
│  rememberImagePickerLauncher(...)        rememberSystemCameraLauncher(...)           │
│  compressImage(bytes, config)            SelectionMode / CompressionConfig           │
└───────────────┬───────────────────────────────────────────────┬─────────────────────┘
                │ delegates presentation to                      │
        ┌───────▼────────┐                              ┌─────────▼────────┐
        │ androidMain     │                              │ iosMain          │
        │ MediaFactory    │  expect/actual               │ MediaFactory     │
        │  (List<Uri>)    │                              │  (List<NSData>)  │
        └───────┬────────┘                              └─────────┬────────┘
                │ implemented by                                  │ implemented by
   ┌────────────▼─────────────┐                     ┌─────────────▼──────────────┐
   │ yalla-sdk-android        │                     │ yalla-sdk-ios              │
   │ YallaMediaFactory        │                     │ YallaMediaFactory (Swift)  │
   │  PickVisualMedia /        │                     │  PHPickerViewController /  │
   │  TakePicture              │                     │  UIImagePickerController   │
   └──────────────────────────┘                     └────────────────────────────┘
                                  injected by the host
                       Android: YallaApp · iOS: AppBootstrap.swift
```

### What is intentionally NOT here

- **No resizing or filtering in the picker.** The picker returns the selected image's *raw bytes*.
  All sizing/quality/format is owned by `compressImage` so there is exactly one pipeline, not two
  that fight each other (the old code resized to 800² in the picker, then `compressImage` resized
  again to 512² — double decode/encode and double quality loss).
- **No custom camera / gallery UI.** We present the OS pickers. They are faster, accessible,
  privacy-respecting (no permission prompts on the gallery), and maintained by Apple/Google.

---

## 2. Module layout

16 files, symmetric across platforms.

```
commonMain/uz/yalla/media/
├── picker/ImagePickerLauncher.kt        expect fun + SelectionMode + ImagePickerLauncher handle
├── camera/SystemCameraLauncher.kt       expect fun + SystemCameraLauncher handle
└── utils/
    ├── ImageCompressor.kt               expect fun compressImage(...)
    └── CompressionConfig.kt             Default / ProfilePhoto / ChatImage presets

androidMain/uz/yalla/media/
├── config/MediaFactory.kt               native contract — returns List<Uri> / Uri?
├── config/YallaMedia.kt                 MediaConfig.Builder + install + requireMedia
├── picker/ImagePickerLauncher.android.kt   reads picked Uris → ByteArray (IO)
├── camera/SystemCameraLauncher.android.kt  reads captured Uri → ByteArray (IO)
├── utils/ImageCompressor.android.kt     BitmapFactory downsample + JPEG quality search
└── ImageViewerFileProvider.kt           FileProvider for the camera output file

iosMain/uz/yalla/media/
├── config/MediaFactory.kt               native contract — returns List<NSData> / NSData?
├── config/YallaMedia.kt                 MediaConfig.Builder + install + requireMedia
├── picker/ImagePickerLauncher.ios.kt    NSData → ByteArray (Default)
├── camera/SystemCameraLauncher.ios.kt   NSData → ByteArray (Default)
└── utils/
    ├── ImageCompressor.ios.kt           UIGraphicsImageRenderer + JPEG quality search
    └── NSDataExtensions.kt              the single NSData.toByteArray() converter
```

---

## 3. Packaging & build configuration

- **Coordinates:** `uz.yalla.sdk:media` (version from the SDK BOM / version catalog).
- **Plugin:** `id("yalla.sdk.kmp.compose")` — a convention plugin that applies:
  - `yalla.sdk.kmp` → AGP KMP library + Kotlin Multiplatform + serialization + `maven-publish` + Dokka.
  - `org.jetbrains.compose` + the Compose compiler + Compose stability config.
- **Targets:** Android (`compileSdk 36`, `minSdk 26`, `JVM_11`), `iosArm64`, `iosSimulatorArm64`.
- **iOS binary:** **static** framework (`isStatic = true`), base name set by the SDK convention.
- **Dependencies:**
  - common: `compose.runtime`, `compose.ui`, `compose.foundation`, `compose.material3`.
  - android: `androidx.core.ktx` (FileProvider, content resolver), `androidx.exifinterface`.
  - ios: none beyond the Kotlin/Native platform libs.

The module ships **no Android permissions**. Its `AndroidManifest.xml` declares only the
`ImageViewerFileProvider` (authority `${applicationId}.provider`, paths from `res/xml/file_paths.xml`)
used to hand the camera a file URI to write into.

---

## 4. Installation (host app)

The shared module must be told which native factory to use, **once**, at startup.

### Android — `YallaApp.onCreate()`

```kotlin
import uz.yalla.media.config.MediaConfig
import uz.yalla.media.config.YallaMedia
import uz.yalla.sdk.android.bridges.media.YallaMediaFactory

YallaMedia.install(
    MediaConfig.Builder().apply {
        factory = YallaMediaFactory(this@YallaApp)
    }.build()
)
```

`YallaMediaFactory(application)` is provided by `yalla-sdk-android` (the `bridges` module, which
`api(libs.yalla.sdk.media)`). No CAMERA / READ_MEDIA permission is needed — the Photo Picker is
permission-free and the system camera writes through the FileProvider.

### iOS — `AppBootstrap.swift`

```swift
EntryPointKt.installMedia(factory: YallaMediaFactory())
```

`installMedia` lives in the app's `EntryPoint.kt`:

```kotlin
fun installMedia(factory: MediaFactory) = YallaMedia.install(MediaConfig.Builder().apply { this.factory = factory }.build())
```

Requirements on iOS:
- `iosApp/build.gradle.kts` must `export(libs.yalla.media)` (so Swift sees `MediaFactory`/`MediaConfig`)
  and `api(libs.yalla.media)`.
- `YallaMediaFactory.swift` (in `yalla-sdk-ios`) must be a member of the app target.
- `Info.plist` must contain `NSCameraUsageDescription` (camera) and `NSPhotoLibraryUsageDescription`.
  *PHPicker itself needs no permission*; the photo-library key is only a safety net for any
  library-read path.

---

## 5. Usage

### Pick from the gallery

```kotlin
val scope = rememberCoroutineScope()

val galleryLauncher = rememberImagePickerLauncher(
    selectionMode = SelectionMode.Single,
    scope = scope,
    onResult = { images: List<ByteArray> ->
        images.firstOrNull()?.let { raw -> /* compress + use */ }
    }
)

// later, e.g. from a button:
galleryLauncher.launch()
```

`SelectionMode`:
- `SelectionMode.Single` — one image (most common; e.g. avatar).
- `SelectionMode.Multiple(maxSelection = n)` — up to `n` images.
- `SelectionMode.Multiple(SelectionMode.INFINITY)` — system maximum (`INFINITY == 0`).

`onResult` is always called on the **main thread**, with an empty list if the user cancels.

### Capture from the camera

```kotlin
val cameraLauncher = rememberSystemCameraLauncher(
    scope = scope,
    onResult = { image: ByteArray? ->
        image?.let { raw -> /* compress + use */ }
    }
)

cameraLauncher.launch()
```

`onResult` receives `null` if the user cancels or the camera is unavailable. Main thread.

### Compress (the one place sizing happens)

```kotlin
val compressed: ByteArray = compressImage(
    imageBytes = raw,
    config = CompressionConfig.ProfilePhoto
)
```

`CompressionConfig` presets (`maxFileSize` / `maxDimension` / `quality`):
| Preset         | Max file | Max dimension | JPEG quality |
|----------------|----------|---------------|--------------|
| `Default`      | 1 MB     | 1024 px       | 80           |
| `ProfilePhoto` | 512 KB   | 512 px        | 85           |
| `ChatImage`    | 2 MB     | 1920 px       | 75           |

`compressImage` downsamples on decode (Android `inSampleSize`; iOS `UIGraphicsImageRenderer`),
then **binary-searches the JPEG quality** to land just under `maxFileSize`, with a half-resolution
fallback if even minimum quality is too big. Run it off the main thread.

### The canonical pattern

Picker/camera give you raw bytes; `compressImage` makes them small. Keep them separate:

```kotlin
val onImagePicked: (ByteArray) -> Unit = { raw ->
    scope.launch(Dispatchers.Default) {
        val compressed = compressImage(raw, CompressionConfig.ProfilePhoto)
        viewModel.onIntent(ProfileIntent.SetImage(compressed))
    }
}

val gallery = rememberImagePickerLauncher(SelectionMode.Single, scope) { it.firstOrNull()?.let(onImagePicked) }
val camera  = rememberSystemCameraLauncher(scope) { it?.let(onImagePicked) }
```

---

## 6. Native implementation

### iOS — `YallaMediaFactory.swift`

- **Gallery:** `PHPickerViewController` with `configuration.filter = .images` and
  `selectionLimit` mapped straight from the contract (1 = single, 0 = unlimited, n = max). Results
  are loaded via `loadDataRepresentation(forTypeIdentifier: "public.image")`, gathered with a
  `DispatchGroup`, and **returned in the user's selection order**.
- **Camera:** `UIImagePickerController(sourceType: .camera)`, guarded by
  `isSourceTypeAvailable(.camera)`; the captured `UIImage` is encoded with
  `jpegData(compressionQuality: 1.0)` (full quality — `compressImage` shrinks it later).
- **Presentation race:** the picker is launched as the photo *action sheet* is dismissing. The
  factory presents from the key window's root view controller and waits for whatever it is currently
  presenting to finish disappearing — via the dismissal's `transitionCoordinator`, falling back to a
  bounded next-runloop re-check. This is why the gallery now opens reliably instead of silently
  failing to present over a half-dismissed sheet.
- Delegates are retained in a single `activeDelegate` slot because the picker controllers do not
  retain their delegates and only one picker is shown at a time.

### Android — `YallaMediaFactory.kt`

- **Gallery:** `ActivityResultContracts.PickVisualMedia` (single) /
  `PickMultipleVisualMedia(max)` (multiple) — the Jetpack **Photo Picker**, permission-free.
- **Camera:** `ActivityResultContracts.TakePicture` writing to a `FileProvider` URI under
  `filesDir/share_images` (cleared before each capture).
- **No Compose coupling:** launches register against the **current Activity's**
  `ActivityResultRegistry` with a unique key, and `unregister()` themselves in the callback. The
  current `ComponentActivity` is tracked with `ActivityLifecycleCallbacks` — the same technique the
  snackbar host uses — so the factory works regardless of which screen triggered it.

---

## 7. Threading model

- Native presentation and `onResult` delivery → **main thread**.
- Reading the picked/captured bytes (Android: `contentResolver.openInputStream`; iOS:
  `NSData.toByteArray`) → off-main on the caller-supplied `scope` (`Dispatchers.IO` on Android,
  `Dispatchers.Default` on iOS), then hopped back to main for `onResult`.
- `compressImage` is synchronous and CPU-bound → the **caller** must run it off the main thread.

The launcher captures the latest `onResult` via `rememberUpdatedState`, so a recomposition with a
new lambda never delivers to a stale one.
