# Module firebase

> Thin facade over gitlive Kotlin Firebase — Analytics, Crashlytics, Cloud Messaging. Consumer-friendly API surface with pluggable logging.

## What this is

- **`YallaFirebase`** — singleton entry point. `install(config)` once
  at app startup; thereafter `YallaFirebase.analytics`,
  `.crashlytics`, `.messaging` return live wrappers.
- **`analytics/`** — `YallaAnalytics` wraps `gitlive.firebase.analytics`.
  Predefined `AnalyticsEvent` sealed hierarchy (login, signup,
  screen-view, custom) plus a `TrackEvent` low-level call.
- **`crashlytics/`** — `YallaCrashlytics` wraps
  `gitlive.firebase.crashlytics`. `recordException(throwable)`,
  `setUserId`, `log(message)`. Wrapped in `runCatching` to keep
  Crashlytics outages from killing the app.
- **`messaging/`** — `YallaMessaging` wraps
  `gitlive.firebase.messaging`. Token fetch, topic subscribe /
  unsubscribe. `MessagingDelegate` is the consumer's hook for token
  refresh + remote-message handling.
- **`logging/`** — `YallaFirebaseLogger` interface (`d`, `i`, `w`,
  `e`). Caller injects a real logger (Kermit, Timber, etc.); a
  `NoopLogger` is the install-time default so the wrapper is
  always callable.

## What this is NOT

- **Not** Firebase Analytics / Crashlytics directly. Use this module
  for the gitlive-wrapped, KMP-portable surface; if you need the
  raw Firebase SDK on a single platform, depend on the platform-
  specific Firebase BOM yourself.
- **Not** a remote-config wrapper. Remote Config lives in YallaClient
  (per the prior phase analysis); not bundled here.
- **Not** a logger. `YallaFirebaseLogger` is the *seam* the consumer
  fills — a logger lives elsewhere (Kermit, etc.).
- **Not** a typed-event registry. `AnalyticsEvent` is a sealed
  hierarchy of events the consumer wants pre-registered; ad-hoc
  events go through `TrackEvent`.

## Usage

```kotlin
implementation("uz.yalla.sdk:firebase")
```

```kotlin
// One-time install in your Application / @main:
YallaFirebase.install(
    config = /* AndroidFirebaseConfig or IosFirebaseConfig */,
    logger = MyKermitLogger(),
)

// Anywhere thereafter:
YallaFirebase.analytics.log(AnalyticsEvent.Login)
YallaFirebase.crashlytics.recordException(throwable)
YallaFirebase.messaging.subscribeToTopic("promotions")
```

## Notes

- **`runCatching` carve-outs.** `YallaCrashlytics`,
  `YallaMessaging`, `YallaAnalytics` all wrap their underlying
  gitlive-Firebase calls in `runCatching { ... }.onFailure { logger.e(...) }`.
  Sanctioned pragmatic carve-out (same posture foundation took with
  moko-geo) — Firebase SDKs frequently throw on missing
  `google-services.json` / token expiry / network outages, and we
  refuse to crash the host app over telemetry.
- **`gitlive` API surface is `api()`.** Every gitlive-Firebase type
  appears in our public signatures (`FirebaseAnalytics`,
  `FirebaseMessaging`, etc.); consumers transitively need them.
  This is intentional — `gitlive` is the contract, our wrappers
  are convenience.
- **Initialization order.** `YallaFirebase.install` MUST run before
  any feature ViewModel calls a Yalla*Firebase service — otherwise
  every accessor throws `IllegalStateException("YallaFirebase not
  initialized")`. Document in YallaClient's app-startup checklist.
- **`AnalyticsEvent` sealed hierarchy** is a starter kit, not the
  full event registry. Add new variants as product requirements
  surface; treat the sealed-when as the source of truth so the
  compiler enforces handler exhaustiveness.

## Depends on

- `firebase-gitlive-app` (api)
- `firebase-gitlive-analytics` (api)
- `firebase-gitlive-crashlytics` (api)
- `firebase-gitlive-messaging` (api)
- `kotlinx.coroutines.core` (implementation — used internally for
  `suspend fun` Firebase operations; no public types leak)
- `firebase-bom`, `androidx.core.ktx` (androidMain — internal)
- No SDK-internal dep.
