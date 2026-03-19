# Module platform

Native UI components and navigation for Kotlin Multiplatform.

This module bridges the gap between shared Compose Multiplatform code and platform-native
controls. Each `expect` composable delegates to UIKit on iOS and Material3 on Android,
giving consumers native look-and-feel without platform-specific code.

## Architecture

Components follow the **expect/actual** pattern:
- **commonMain** declares the API surface (`expect fun` composables and data types)
- **androidMain** implements via Jetpack Compose / Material3
- **iosMain** implements via UIKit wrappers (`UIViewControllerRepresentable`, `UIViewRepresentable`)

Platform-specific configuration is injected once at startup through [YallaPlatform.install][uz.yalla.platform.YallaPlatform.install].

# Package uz.yalla.platform

[YallaPlatform] singleton for one-time module initialization and [PlatformConfig] marker interface.

# Package uz.yalla.platform.navigation

Cross-platform navigation built on Decompose. Provides [NativeNavHost], [Navigator], [Route],
[ScreenProvider], [NativeRootComponent], [ToolbarState], and [ToolbarAction].

# Package uz.yalla.platform.config

Platform-specific configuration classes: [IosPlatformConfig] with native factory interfaces
and [AndroidPlatformConfig].

# Package uz.yalla.platform.sheet

[NativeSheet] bottom sheet composable with native presentation on each platform.

# Package uz.yalla.platform.button

Native icon buttons: [NativeCircleIconButton], [NativeSquircleIconButton], and the
shared [SheetIconButton] composable.

# Package uz.yalla.platform.toggle

[NativeSwitch] toggle composable backed by `UISwitch` on iOS and Material3 `Switch` on Android.

# Package uz.yalla.platform.picker

[NativeWheelDatePicker] date picker backed by `UIDatePicker` on iOS and wheel-style picker on Android.

# Package uz.yalla.platform.indicator

[NativeLoadingIndicator] progress indicator backed by `UIActivityIndicatorView` on iOS
and `CircularProgressIndicator` on Android.

# Package uz.yalla.platform.system

[SystemBarColors] composable for controlling status bar and navigation bar appearance.

# Package uz.yalla.platform.update

In-app update utilities: [VersionComparator] for semver comparison and [AppUpdateState]
for reactive update-available status.

# Package uz.yalla.platform.model

[IconType] enum defining the icon vocabulary shared between common code and platform renderers.

# Package uz.yalla.platform.util

iOS-only utilities such as `ViewControllerUtil` for UIKit interop helpers.
