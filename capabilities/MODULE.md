# Module capabilities

Thin cross-platform (KMP) wrappers over device and OS capabilities. Each package is
an independent capability; platforms supply only the IO, the contract lives in
`commonMain`.

## Packages

- **browser** — opens web URLs in the platform in-app browser (Android Custom Tabs /
  iOS `SFSafariViewController`). Only `http`/`https` URLs are honored.
- **sms** — OTP helpers: SMS-retriever autofill (`ObserveSmsCode`, Android-only — a
  no-op on iOS, which autofills OTPs natively via the keyboard), the app-signature
  hash (`getAppSignature`, Android-only), and the internal OTP parser.
- **update** — app-update availability: Play in-app-update on Android, App Store
  version check on iOS. Fails closed when the installed version is unknown.
- **connectivity** — observable online/offline state over the platform network
  monitor. Teardown is tied to the supplied coroutine scope's cancellation.
- **location** — device location provider and location-services helpers, built on
  moko-geo / moko-permissions. Heavier than the other capabilities (it pulls in the
  permission/geo machinery) and requires a main-thread-confined scope.
