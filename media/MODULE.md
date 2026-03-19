# Module media

Cross-platform camera, image picker, gallery browser, and image compression for Kotlin Multiplatform.

This module provides composable APIs for capturing photos, selecting images from the device
gallery, and compressing images — all with a single shared API surface. Each `expect` composable
delegates to CameraX / Activity Result APIs on Android and AVFoundation / PHPicker on iOS.

## Architecture

Components follow the **expect/actual** pattern:
- **commonMain** declares the API surface (`expect fun` composables, state holders, and data types)
- **androidMain** implements via CameraX, Accompanist Permissions, and Activity Result APIs
- **iosMain** implements via AVFoundation, PHPicker, and UIKit interop

# Package uz.yalla.media.camera

Camera composables and state management. Provides [YallaCamera] for live camera preview with
capture and frame analysis, [YallaCameraState] for programmatic control, [CameraMode] for
front/back lens selection, and [SystemCameraLauncher] for delegating to the platform camera app.

# Package uz.yalla.media.picker

Image picker composables and utilities. Provides [rememberImagePickerLauncher] to select single
or multiple images via the system photo picker, [SelectionMode] for cardinality control,
[ResizeOptions] for automatic down-scaling, [FilterOptions] for color filters (grayscale, sepia,
invert), [ImagePickerLauncher] for imperative launch, and [toImageBitmap] for byte-array
conversion.

# Package uz.yalla.media.gallery

In-app gallery browser (experimental). Provides [YallaGallery] composable that renders a
paginated grid of device photos with permission handling, [GalleryPickerState] for grid layout
configuration, and [rememberGalleryPickerState] for state creation.

# Package uz.yalla.media.utils

Image compression utilities. Provides [compressImage] for adaptive JPEG compression with
dimension constraints and [CompressionConfig] presets for common use-cases (default, profile
photo, chat image).
