# Module platform

> Cross-platform `expect/actual` host — native sheet, picker, loading, toggle, navigation, system bars. The bridge between Compose-Multiplatform code and platform-native controls.

## What this is

- **Composable expect/actuals**: every public symbol is an `expect`
  composable or class with Android/iOS implementations.
  - `sheet/` — `NativeSheet` modal bottom sheet (Material3
    `ModalBottomSheet` on Android; `UISheetPresentationController` on
    iOS).
  - `picker/` — `NativeWheelDatePicker` (wheel-picker library on
    Android; `UIDatePicker` on iOS).
  - `indicator/` — `NativeLoadingIndicator` (`CircularProgressIndicator`
    on Android; `UIActivityIndicatorView` on iOS).
  - `toggle/` — `NativeSwitch` (Material3 `Switch` on Android;
    `UISwitch` on iOS).
  - `button/` — `NativeCircleIconButton`, `SheetIconButton` —
    icon-shaped tappable controls.
  - `browser/` — `InAppBrowser` (Custom Tabs on Android;
    `SFSafariViewController` on iOS).
  - `haptic/` — `HapticFeedback` perform-haptic facade.
  - `system/` — `SystemBarColors` (status + navigation bar tint).
- **Navigation** (`navigation/`): `NativeNavHost`, `Navigator`,
  `Route`, `ScreenProvider`, `NativeRootComponent`, `ToolbarState`,
  `ToolbarAction`. Built on Decompose; renders Compose `Children`
  on Android, hosts a `UINavigationController` stack on iOS.
- **Update** (`update/`): `VersionComparator` (semver compare),
  `AppUpdateState` (Play in-app update state on Android; identity
  on iOS).
- **OTP** (`otp/`): `AppSignature` (Android-only `SmsRetrieverClient`
  signature) + `SmsCodeObserver` (Android SMS Retriever auto-fill).
- **System utilities** (`util/`, `system/`): `IconMapper`, status-bar
  control. `IconType` enum is the icon vocabulary shared between
  common code and platform-specific renderers.
- **`YallaPlatform`** (`YallaPlatform.kt`): one-time platform
  initialization seam. Consumer calls `YallaPlatform.install(config)`
  with their `IosPlatformConfig` or `AndroidPlatformConfig` at app
  startup.

## What this is NOT

- **Not** Material3 itself. Composables wrap M3 on Android, but the
  public surface is the `Native*` `expect` declarations, not M3 types.
- **Not** the design layer. Tokens (`System.color.*`, `System.font.*`)
  live in `:design`; platform consumes them but doesn't ship them.
- **Not** a UI primitive layer. `PrimaryButton`, `PrimaryField`, etc.
  live in `:primitives` and delegate to platform's `Native*`
  composables for behaviors that can't be done in pure Compose.
- **Not** a feature-orchestration module. No ViewModels, no
  business state, no Service repositories.

## Usage

```kotlin
implementation("uz.yalla.sdk:platform")
```

```kotlin
// One-time init in your Application / @main entry point:
YallaPlatform.install(
    AndroidPlatformConfig(/* ... */) // or IosPlatformConfig(...)
)

// Inside any Composable:
NativeSheet(
    isVisible = state.showLanguagePicker,
    onDismissRequest = onDismiss,
) {
    // Sheet content — uses Material3 sheet on Android, native
    // sheet presenter on iOS.
}

NativeSwitch(
    checked = state.darkMode,
    onCheckedChange = onToggleDark,
)
```

## Notes

- **`expect/actual` is the contract.** Every public composable in
  commonMain is `@Composable expect fun NativeXxx(...)`. Tests against
  the contract belong in `commonTest`; platform-specific behavior
  belongs in `androidMain`/`iosMain` tests (currently sparse).
- **Decompose for navigation, not Voyager / NavGraph.** Navigation is
  a Decompose `StackNavigation` + `Children` rendering. Decompose
  types (`ComponentContext`, `StackNavigation`) leak into the public
  API of `NativeRootComponent` and `NavigatorImpl`, so `decompose`
  + `decompose-compose` are `api()` deps.
- **`materialIconsExtended` was dropped.** The single use-site
  (`Icons.AutoMirrored.Filled.ArrowBack` in `NativeNavHost.android`)
  was swapped for `YallaIcons.ArrowLeft` in wave A. Same swap
  primitives wave 3 did. Composites is the last module still pulling
  `materialIconsExtended` (4 chevron sites — see composites G1).
- **`datetime-wheel-picker` is androidMain-only.** The `NativeWheelDatePicker`
  expect declaration in commonMain takes `kotlinx.datetime.LocalDate`
  (which IS in commonMain api), not the wheel-picker library types.
  The library is imported only inside the Android actual.
- **`@Suppress("FunctionName")` carve-outs already cleaned.** Wave 5
  of composites dropped these from `DatePickerSheet`; equivalent
  suppressions in this module's source were already absent. Composables
  ARE allowed PascalCase; no suppression needed.
- **Status-bar / system-bar control on iOS.** `SystemBarColors` on
  iOS uses `UIStatusBarStyle` updates via `setNeedsStatusBarAppearanceUpdate`.
  Compose's status-bar APIs do not cross to iOS, hence the
  platform-specific actual.

## Depends on

- `design` (api — `System.color.*` / `System.font.*` are read inside
  `Defaults.colors()` factories that ship in our public surface)
- `resources` (api — `StringResource`, `DrawableResource`,
  `YallaIcons.*` appear in public composable parameters)
- `compose.runtime` (api — `@Composable`)
- `compose.ui` (api — `Modifier`, `Color`, `Shape`, `Dp`, `TextStyle`
  on every public composable / `Colors` / `Dimens`)
- `compose.foundation` (api — layout primitives in public surfaces)
- `compose.material3` (api — `Surface`, `Icon`, `Text`, `TextField`
  in many public composables)
- `kotlinx.datetime` (api — `LocalDate` on `NativeWheelDatePicker`)
- `decompose` + `decompose.compose` (api — `ComponentContext`,
  `StackNavigation` exposed through `NavigatorImpl` and
  `NativeRootComponent`)
- `compose.components.resources` (implementation — internal-only
  resource loading)
- `androidx.core.ktx`, `google.play.app.update`, `androidx.browser`,
  `play.services.auth.api.phone`, `datetime-wheel-picker` (androidMain
  implementation)
- No SDK-internal dep beyond `design` / `resources`.
