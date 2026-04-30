# Module primitives

> Stateless Compose UI bricks — buttons, fields, indicators, pins, top bars. The atom layer the screens snap together from.

## What this is

- **Buttons** (`button/`): `PrimaryButton`, `SecondaryButton`,
  `TextButton`, `IconButton`, `BottomSheetButton`, `NavigationButton`,
  `GenderButton`, `SensitiveButton` (countdown-confirm), and the shared
  `ButtonLayout` skeleton most variants delegate to.
- **Fields** (`field/`): `PrimaryField` (outlined text input),
  `NumberField` (phone-formatted), `DateField` (read-only picker
  trigger), `SearchField`.
- **Indicators** (`indicator/`): `DotsIndicator` (pager dots),
  `LoadingIndicator` (inline spinner via `platform.NativeLoadingIndicator`),
  `StripedProgressBar`, `SplashOverlay` (full-screen blocking loader
  with logo).
- **OTP / PIN** (`otp/`): `PinRow` — a row of digit boxes wired to a
  single text-field state.
- **Map pins** (`pin/`): `LocationPin` (animated address-pill pin with
  jump animation, split across `LocationPin` / `PinContent` /
  `PinHeader` / `PinStick`) and `SearchPin` (Lottie-driven search
  animation).
- **Rating** (`rating/`): `RatingRow` — five-star tappable rating row.
- **Top bars** (`topbar/`): `TopBar` (standard height) and
  `LargeTopBar` (collapsing-style prominent title variant).
- **Dialog** (`dialog/`): `LoadingDialog` — modal blocking loader
  built on `androidx.compose.ui.window.Dialog`.
- **Transformation** (`transformation/`): `MaskFormatter` — utility for
  applying `+998 (##) ###-##-##`-style masks to plain digit input.
- **Util** (`util/`): `Modifier.squareSize` (constrains content to the
  shorter side of the incoming constraints) and `GenderKind.resource`
  (maps the core gender enum to its localized `StringResource`).

## What this is NOT

- **Not** the design-token layer. Colors, fonts, spacing, radius, and
  motion live in `design`; primitives consume them via `System.color.*`
  / `System.font.*` and expose the result as overridable `Colors` /
  `Dimens` data classes.
- **Not** stateful screens or feature orchestration. Primitives are
  stateless atoms — state lives outside, hoisted by the caller.
  Feature ViewModels and navigation belong in YallaClient, not here.
- **Not** the multi-component layout layer. Combinations like
  "header + list + sticky CTA" or wizard-style flows live in
  `composites`. A primitive does one thing.
- **Not** a UI for domain types. Primitives accept primitive params
  (`String`, `Boolean`, callbacks) — they do not know about
  `Order`, `User`, etc. Composites bridge domain → primitive params.
- **Not** a string-copy module. Localized text comes from the caller
  via `stringResource(...)`. Primitives carry no default copy that
  references `resources/strings/*`; strings here would lock translations
  out of the assembly layer.

## Usage

```kotlin
implementation("uz.yalla.sdk:primitives")
```

```kotlin
@Composable
fun LoginScreen(
    state: LoginState,
    onPhoneChange: (String) -> Unit,
    onSubmit: () -> Unit,
) = Column(
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = Modifier.padding(16.dp),
) {
    NumberField(
        value = state.phone,
        onValueChange = onPhoneChange,
        modifier = Modifier.fillMaxWidth(),
    )

    PrimaryButton(
        onClick = onSubmit,
        loading = state.isSubmitting,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(Res.string.login_continue))
    }
}
```

## Notes

- **Colors + Dimens + Defaults convention.** Every public component
  follows the same shape: an `@Immutable {Component}Colors` data class
  for color knobs, an `@Immutable {Component}Dimens` data class for
  size / shape knobs, and a `{Component}Defaults` object exposing
  `colors()` and `dimens()` factories. The factories read the current
  theme via `System.color.*` / `System.font.*` and return objects the
  caller can `.copy(...)`. This means the call site only overrides
  what it cares about while still picking up theme switches:
  ```kotlin
  PrimaryButton(
      onClick = { ... },
      colors = PrimaryButtonDefaults.colors(
          containerColor = System.color.accent.pinkSun,
      ),
  ) { Text("...") }
  ```
  Required params (text, callbacks) come first; `modifier` second;
  styling (`colors`, `dimens`, `textStyle`) last. New components added
  here MUST follow this shape so consumers can reach for any primitive
  with the same mental model.
- **Stateless-atom posture.** Primitives never hoist their own state
  into a `remember { mutableStateOf(...) }` that the caller can't see.
  Animation state used purely for visual transitions (e.g.
  `SensitiveButton`'s countdown `Animatable`) is allowed because the
  caller has no reason to observe it; everything else — selection,
  text content, expansion — is driven by parameters in. This is what
  makes the bricks recombinable without surprise re-render behavior.
- **No default copy.** `SensitiveButton.confirmText` /
  `countdownText` and `SplashOverlay.message` are `String` params
  with no defaults. Earlier revisions defaulted them to
  `stringResource(Res.string.order_cancel_action_yes)` etc., which
  meant primitives carried product-specific copy and locked the
  translation set. The current contract is "caller passes the
  localized string"; this stays.
- **Lottie + ConstraintLayout — internal only.** `compottie` is used
  by `pin/SearchPin.kt` for the Lottie-driven search animation;
  `constraintlayout` is used by `pin/LocationPin.kt` for the
  pin-stick / pin-content / pin-header anchored layout. Both stay
  `implementation` deps — no other primitive needs them, and they
  must not appear in any public signature so consumers don't transitively
  inherit them. If a third primitive needs Lottie, consider whether
  it belongs here at all (Lottie composables are visually heavy and
  rarely "atomic").
- **`System.space` / `System.radius` not yet consumed.** Dimens
  defaults still use raw `dp` literals (e.g. `12.dp`, `16.dp`).
  Migration to `System.space.scale.*` / `System.radius.*` is on the
  YallaClient migration plan (G20 in the audit) but deferred until the
  YallaClient assembly proves the token surface holds up under real use.
- **`SecondaryButton` carries an in-source A4 note.** A criterion-9
  audit flagged that `SecondaryButton` and `PrimaryButton` differ
  only in default colors, suggesting deletion. We kept it because
  the call-site grammar `SecondaryButton(...)` reads cleaner than
  `PrimaryButton(colors = PrimaryButtonDefaults.secondary())`. Revisit
  if a third "tone" variant ever surfaces — at that point a single
  `Button(tone = ButtonTone.Secondary)` factory may win.
- **`LoadingIndicatorColors.track` is reserved.** The current
  `NativeLoadingIndicator` impl on each platform doesn't render a
  background track, so the `track` field is unused at runtime. It's
  kept on the `Colors` surface so the field is in place when a future
  platform impl wants to render a track without a binary-incompatible
  surface change.

## Depends on

- `design` (api — `System.color.*` / `System.font.*` are read inside
  `Defaults.colors()` factories that ship in our public surface)
- `resources` (api — `StringResource`, `DrawableResource`, `YallaIcons`
  appear in public component params)
- `compose.runtime` (api — `@Composable`)
- `compose.ui` (api — `Modifier`, `Color`, `Shape`, `Dp`, `TextStyle`
  on every public component / `Colors` / `Dimens`)
- `compose.foundation` (api — layout primitives + `Image` in public
  surfaces like `SensitiveButton`)
- `compose.material3` (api — `Surface`, `Icon`, `Text`, `TextField`
  in public composables)
- `core` (implementation — `GenderKind` in `GenderResource`,
  `formatArgs` in `SensitiveButton`)
- `platform` (implementation — `NativeLoadingIndicator` actual)
- `compose.components.resources`, `compose.ui.tooling.preview`,
  `compottie`, `compottie.resources`, `constraintlayout`
  (implementation — internal-only)
- No SDK-internal dep beyond `core` / `design` / `resources` / `platform`.
