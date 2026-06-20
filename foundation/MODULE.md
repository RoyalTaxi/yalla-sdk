# Module foundation

The ViewModel and UI substrate every Yalla product screen builds on: the loading-indicator
machine, locale plumbing, theme resolution, and a handful of Compose helpers. The highest-fan-in
module in the SDK — every product `ViewModel` extends `BaseViewModel`, every spinner routes through
`LoadingController`, and both app roots wrap their content tree in `LocaleProvider`.

## What lives here

- **infra** — [`BaseViewModel`][uz.yalla.foundation.infra.BaseViewModel] (shared `loading` flag +
  the crash-safe `safeScope` log-and-swallow hatch) and
  [`LoadingController`][uz.yalla.foundation.infra.LoadingController], the reference-counted,
  anti-flicker spinner state machine (grace before show, minimum-visible before hide).
- **locale** — [`LocaleProvider`][uz.yalla.foundation.locale.LocaleProvider] (provides the active
  `LocaleKind` and applies the platform language) plus the `changeLanguage`/`getCurrentLanguage`
  expect/actual pair (symmetric set/get on both platforms).
- **theme** — [`rememberIsDarkTheme`][uz.yalla.foundation.theme.rememberIsDarkTheme] and the pure
  `ThemeKind.resolveIsDark(systemDark)` rule it (and every other platform caller) resolves through.
- **system** — [`SystemBarColors`][uz.yalla.foundation.system.SystemBarColors], status/navigation-bar
  icon appearance (status + navigation on Android; status-only on iOS).
- **animation / input** — small Compose modifiers: `staggerReveal` (fade + slide list reveal) and
  `clearFocusOnTap` (dismiss focus on outside taps).
