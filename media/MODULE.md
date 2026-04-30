# Module media

> Cross-platform camera, image picker, gallery, and image compression. CameraX on Android, AVFoundation on iOS, single Compose API.

## What this is

- **Camera** (`camera/`): `YallaCamera` (live preview + capture +
  frame analysis) and a state-driven `YallaCamera(state)` overload
  for advanced control. `YallaCameraState` exposes
  `capture()`/`toggleCamera()` for programmatic ops. `CameraMode`
  enum (Front/Back) and `SystemCameraLauncher` for delegating to
  the system camera app.
- **Picker** (`picker/`): `rememberImagePickerLauncher` returns an
  `ImagePickerLauncher` for imperative launch. `SelectionMode`
  (Single / Multiple), `ResizeOptions`, `FilterOptions` (grayscale,
  sepia, invert), `toImageBitmap` byte-array conversion helper.
- **Gallery** (`gallery/`): `YallaGallery` cross-platform image-picker
  wrapper (system photo picker). For Android in-app paged grids,
  `YallaGalleryPagingGrid` lives in `androidMain`-only with its
  own `GalleryPickerState` + paging-driven view model.
- **Utils** (`utils/`): `compressImage` JPEG-compression helper +
  `CompressionConfig` presets (default, profile photo, chat image).

## What this is NOT

- **Not** a video pipeline. CameraX's video capture and
  AVFoundation's `AVAssetWriter` are not exposed. If video lands,
  it'll be its own subpackage.
- **Not** a full asset-library browser. `YallaGallery` is the
  cross-platform single-image picker; `YallaGalleryPagingGrid`
  is Android-only because PHPicker on iOS has its own UI we don't
  override.
- **Not** an image editor. Filters in `FilterOptions` are
  per-pixel transforms applied during pick / capture. No crop, no
  text overlay, no annotations.
- **Not** a camera-permissions module. Permission UX is wired via
  `accompanist-permissions` (Android) / `AVCaptureDevice` request
  callbacks (iOS); the caller handles denied-state UI through the
  `permissionDeniedContent` slot on `YallaCamera`.

## Usage

```kotlin
implementation("uz.yalla.sdk:media")
```

```kotlin
// Live camera with capture button:
YallaCamera(
    modifier = Modifier.fillMaxSize(),
    cameraMode = CameraMode.Back,
    captureIcon = { onClick ->
        IconButton(onClick = onClick) {
            Icon(YallaIcons.Camera, contentDescription = null)
        }
    },
    onCapture = { bytes -> bytes?.let(::uploadPhoto) },
)

// Imperative image picker:
val pickerLauncher = rememberImagePickerLauncher(
    selectionMode = SelectionMode.Single,
    onResult = { uris -> /* … */ },
)

Button(onClick = { pickerLauncher.launch() }) {
    Text("Pick a photo")
}
```

## Notes

- **Camera carve-outs (try/catch).** `YallaCamera` actuals wrap
  CameraX `bindToLifecycle` / AVFoundation `AVCaptureSession.startRunning`
  in `runCatching` — same posture as `firebase` and `foundation`.
  System-API boundary means a bound-to-lifecycle failure or a
  missing camera-permission MUST NOT crash the host app.
- **Frame analysis is best-effort.** The `onFrame` callback delivers
  encoded JPEG bytes per-frame on Android (CameraX `ImageAnalysis`)
  and BGRA8888 bytes on iOS (`AVCaptureVideoDataOutput`). The format
  difference is documented per-actual; consumers that want a
  uniform format should normalize on receive.
- **Paging is androidMain-only.** `YallaGalleryPagingGrid` +
  `YallaGalleryDataSource` + `YallaGalleryViewModel` use
  `app.cash.paging` (KMP-portable), but the in-app grid surface
  itself only exists on Android. iOS uses PHPicker's native UI.
  Wave-C demoted `paging-common` and `paging-compose-common` from
  commonMain to androidMain.
- **Compression presets.** `CompressionConfig.profilePhoto` =
  square 800×800 @ 85% JPEG. `CompressionConfig.chatImage` =
  longest-edge 1280 @ 75%. `CompressionConfig.default` = no resize,
  90%. Hand-tuned for the Yalla product; revise only with backend
  alignment.
- **CoreMedia + CoreVideo cinterop on iOS.** Frame analysis uses
  raw `CVPixelBuffer` types. The `coremedia.def` and `corevideo.def`
  cinterop files live in `media/src/nativeInterop/cinterop/`.

## Depends on

- `compose.runtime` (api — `@Composable` on every public composable)
- `compose.ui` (api — `Modifier` on every `YallaCamera` /
  `YallaGallery` overload)
- `compose.foundation` (implementation — internal layout primitives)
- `compose.material3` (implementation — internal Surface/Icon usage)
- `androidx.activity.compose`, `androidx.exifinterface`,
  `androidx.lifecycle.viewmodel.compose`,
  `androidx.lifecycle.runtime.compose`, `accompanist.permissions`,
  `camera.camera2`, `camera.lifecycle`, `camera.view`,
  `kotlinx.coroutines.guava`, `paging.common`, `paging.compose.common`
  (androidMain implementation)
- No SDK-internal dep. Media is leaf-level — no `core` / `design` /
  `resources` dependency required.
