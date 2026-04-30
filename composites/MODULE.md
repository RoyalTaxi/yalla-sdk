# Module composites

> Pre-built LEGO assemblies — cards, items, sheets, drawers, snackbars, views. Bricks (primitives) snapped together into screen-ready composites.

## What this is

- **Cards** (`card/`): `ContentCard` (base), `AvatarCard`, `BannerCard`,
  `FeedCard`, `InfoCard`, `NavigableCard`, `SelectionCard`,
  `SummaryCard`, `ToggleCard`. Content containers with theme-aware
  shape, padding, and elevation.
- **Items** (`item/`): `ListItem` (base), `IconItem`, `NavigableItem`,
  `SelectableItem`, `AddressItem`, `PlaceButton`, `PricingItem`,
  `ValueItem`. Row-shaped composites for lists, settings,
  selection grids, and address views.
- **Sheets** (`sheet/`): the heaviest package.
  - `Sheet`, `BottomSheetCard` — thin wrappers over the platform-native
    `NativeSheet` for visibility-driven modal sheets.
  - `ExpandableSheet` + `ExpandableSheetState` — anchored-draggable
    two-state (collapsed/expanded) sheet.
  - `HeaderableSheet` + `HeaderableSheetState` — three-section sheet
    (header / body / footer); body collapses while header+footer stay.
  - `FormSheet`, `OtpSheet`, `ConfirmationSheet`, `ActionSheet`,
    `ActionPickerSheet`, `SelectionSheet` — opinionated sheet shapes
    for common flows.
  - `DatePickerSheet` (expect/actual) — Android wheel-picker /
    iOS native UIDatePicker.
  - `SheetHeader`, `SheetSnackbarHost`, `SheetNestedScrollConnection`
    — composition primitives.
  - `DeviceConnectivityState` — moko-connectivity state-holder
    (currently in `sheet/` for historical reasons; see Notes).
- **Drawer** (`drawer/`): `DrawerItemIcon`, `Navigable`,
  `SectionBackground` — building blocks for navigation drawers.
- **Snackbar** (`snackbar/`): `Snackbar` composable,
  `SnackbarHost` collector, and the singleton `SnackbarController`
  channel-event bus.
- **Views** (`view/`): `CarNumber` (Uzbekistan license plate),
  `RouteView` (origin-destination list), `LocationPoint` (single
  route stop), `EmptyState` (empty-list placeholder).
- **Util** (`util/`): `PaymentKind.toPainter()` /
  `PaymentKind.getStringResource()` — composables resolving payment
  brands (Cash, Humo, Uzcard) to painters and string resources.

## What this is NOT

- **Not** the design-token layer. Colors, fonts, spacing, radius,
  motion live in `design`; composites consume them via `System.color.*`
  / `System.font.*` and expose overridable `Colors` / `Dimens` data
  classes.
- **Not** the atom layer. Buttons, fields, dialogs, OTP rows live in
  `primitives`. Composites *use* primitives; they don't reimplement
  them. Two intentional carve-outs (Material3 `Button` in
  `ActionPickerSheet` and `AddressItem`) are documented in Notes.
- **Not** a feature module. Composites accept primitive params
  (`String`, `Boolean`, callbacks, sealed UI types) — they do not
  know about `Order`, `User`, `Trip`, etc. Feature ViewModels and
  screen orchestration belong in YallaClient.
- **Not** a string-copy module. Localized text comes from the caller
  via `stringResource(...)`. After wave 5's A13, even
  `DatePickerSheet`'s "Date of birth" header is caller-supplied via
  `DatePickerSheetState.title`.
- **Not** a CTA-button factory. The `Button`/`Surface` shapes inside
  `ActionPickerSheet`/`AddressItem` are structural row buttons, not
  primary call-to-actions. CTA buttons stay in primitives.

## Usage

```kotlin
implementation("uz.yalla.sdk:composites")
```

```kotlin
@Composable
fun OrderConfirmationScreen(
    state: OrderConfirmationState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) = ConfirmationSheet(
    isVisible = state.isVisible,
    onDismissRequest = onDismiss,
    title = stringResource(Res.string.confirm_order_title),
    description = stringResource(Res.string.confirm_order_body),
    actionText = stringResource(Res.string.confirm_order_action),
    onAction = onConfirm,
) {
    SummaryCard(
        title = stringResource(Res.string.confirm_order_summary),
        items = state.summaryRows,
    )
}
```

```kotlin
// Snackbar — global controller, host once at the root:
@Composable
fun App() {
    SnackbarHost()
    Box {
        FeatureScreens()
        // Anywhere in the tree:
        // SnackbarController.show(SnackbarData("Saved!"))
        // SnackbarController.dismiss()
    }
}
```

## Notes

- **Colors + Dimens + Defaults convention.** Every composable follows
  the shape established in `primitives/MODULE.md`: `{Component}Colors`
  (`@Immutable data class`) + `{Component}Dimens` + `{Component}Defaults`
  with `colors()` / `dimens()` factories that read theme tokens.
  Composites is the largest single consumer of `System.color.*`
  (~130 references) and `System.font.*` (~80 references) in the SDK.
- **Stateless-atom posture, with one carve-out.** Composites are
  stateless — state lives outside, hoisted by the caller. The only
  internal `remember { mutableStateOf(...) }` is in `Sheet.kt` for
  ephemeral nested-scroll plumbing the caller has no business
  observing. Same posture as primitives' `SensitiveButton.Animatable`.
- **Material3 `Button` carve-out (G2).** `ActionPickerSheet.kt:231`
  and `AddressItem.kt:166` use `androidx.compose.material3.Button`
  directly instead of primitives' `PrimaryButton`/`SecondaryButton`.
  Reason: structural full-row tappable shapes are not CTA buttons;
  the primitive enforces CTA shape and would conflict. Document the
  exception, don't generalize it.
- **Coil — never used in the public surface.** Earlier revisions
  referenced `coil3.compose.AsyncImage` in KDoc Usage examples. Wave
  3 confirmed the `avatar` slot in `AvatarCard` is
  `@Composable () -> Unit` — caller-driven. Coil deps were dropped
  entirely; the suggested-impl pattern in KDoc is preserved.
- **Lottie / ConstraintLayout — single-consumer in primitives only.**
  Composites does not use either. Don't pull them back in unless a
  composite has a real animation/anchored-layout need; primitives
  already pays the dep cost.
- **`materialIconsExtended` deferred swap (G1).** Four files use
  `Icons.AutoMirrored.{Filled,Default}.ArrowForward(Ios)` for
  navigation chevrons (`drawer/Navigable`, `card/NavigableCard`,
  `item/NavigableItem`, `item/AddressItem`). YallaIcons does not
  ship a chevron-right vector yet. Migrate once `:resources` adds
  the vector; until then, `materialIconsExtended` stays as
  `implementation`.
- **`SnackbarController` is a process singleton.** A buffered
  `Channel<SnackbarEvent>` consumed via `events: Flow<...>`. Events
  are never replayed on lifecycle restart, never deduplicated,
  consumed exactly once. Process-singleton makes test isolation
  fiddly — `SnackbarControllerTest` uses Turbine to bound the
  collection window per test. An injectable refactor is a scope
  change beyond cleanup.
- **`SnackbarController.sendData(event: SnackbarData?)` carve-out
  (G3 RETRACTED).** The audit initially flagged it for deletion;
  cross-check found 8 YallaClient call-sites. The "redundant nullable
  wrapper" framing is wrong — YallaClient explicitly uses the
  nullable-data convention. Kept.
- **`DeviceConnectivityState` lives in `sheet/`.** Historical accident
  (the original use-case was a connectivity-banner sheet host).
  A move to `connectivity/` is on the migration list as deferred —
  YallaClient's import-path churn outweighs the cosmetic improvement.
- **`DatePickerSheet` no longer ships a default header.** Wave 5's
  A13 dropped the hardcoded `Res.string.register_input_birthdate`
  default. Callers pass `DatePickerSheetState.title =
  stringResource(...)` for their screen, or `null` to hide the row.
  Same posture as primitives wave 5 (`SensitiveButton`,
  `SplashOverlay`).
- **`Modifier.expandableSheetDraggable` is `internal`.** Wiring for
  `ExpandableSheet`'s root box; not part of the composition API.
  Zero YallaClient consumers verified.
- **Test coverage shape (G4 deferred).** 229 tests, covering
  data-class equality on every `*Colors` / `*Dimens` / `*Defaults`
  plus the three non-composable state holders backfilled in wave 8
  (`DeviceConnectivityState`, `SnackbarController`, `PaymentResource`).
  Compose-composable behavior — sheet anchor logic, scroll
  connection, drag fraction — has no `runComposeUiTest` coverage
  yet; deferred until that infrastructure decision lands.
- **`System.space.*` / `System.radius.*` not yet consumed.**
  Composites uses raw `dp` literals in every `*Defaults.dimens()`
  factory. Same deferral as primitives — revisit when the design
  token YallaClient migration settles.

## Depends on

- `core` (api — `PaymentKind`, `LocaleKind`, `Selectable`,
  `OptionModel<T>`, address types appear in public composable params)
- `design` (api — `System.color.*` / `System.font.*` are read inside
  `Defaults.colors()` factories that ship in our public surface)
- `resources` (api — `StringResource`, `DrawableResource`,
  `YallaIcons.*` appear in public composable parameters)
- `compose.runtime` (api — `@Composable`)
- `compose.ui` (api — `Modifier`, `Color`, `Shape`, `Dp`, `TextStyle`
  on every `*Colors` / `*Dimens`)
- `compose.foundation` (api — `RowScope` / `ColumnScope` content
  slots, layout primitives in public surfaces)
- `compose.material3` (api — `Surface`, `Icon`, `Text`, `TextField`
  in many public composables)
- `foundation` (implementation — `Selectable`/`OptionModel` glue
  consumed internally; `BaseViewModel` is NOT used here)
- `primitives` (implementation — composites delegate to
  `PrimaryButton`, `IconButton`, `PinRow`, etc.)
- `platform` (implementation — `NativeSheet`, `NativeWheelDatePicker`,
  `SheetIconButton` actuals)
- `compose.materialIconsExtended`, `compose.components.resources`,
  `compose.ui.tooling.preview` (implementation — internal-only;
  see G1 for the materialIconsExtended exit plan)
- `kotlinx.datetime` (implementation — `LocalDate` on
  `DatePickerSheetState`)
- `connectivity.device` (implementation — `Connectivity` provider in
  `DeviceConnectivityState`)
- `androidx.core.ktx`, `datetime-wheel-picker` (androidMain
  implementation)
- No SDK-internal dep beyond `core` / `design` / `resources` /
  `foundation` / `primitives` / `platform`.
