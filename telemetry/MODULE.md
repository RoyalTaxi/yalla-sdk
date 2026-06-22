# Module telemetry

The SDK-wide observability hub: a process-global [Telemetry] entry point that fans
analytics events out to the installed `TelemetrySink`s and routes crashes to the
installed `CrashReporter`. The contract (interfaces, the sealed `ParamValue` model,
the `event { }` DSL) lives in `commonMain`; concrete sinks/reporters are supplied by
the host platform.

## Lifecycle

`Telemetry.install(sinks, crash)` is **one-shot**: call it once at process start-up,
on the main thread, before any `track`/`setUser`/`recordCrash`. Until then the object
is a safe no-op (empty sink list, `CrashReporter.Noop`). A second `install` silently
replaces the previous set with no flush/close hook, so sinks must not hold closeable
native resources that need release on replacement.

## Threading

`track`, `setUser`, and `recordCrash` are safe to call from any thread. The installed
configuration is published through `@Volatile` fields and read as a single snapshot
per call, so readers see either the pre-install no-op default or the fully-installed
set — never a torn `{sinks, crash}` view.

## Exception isolation

Telemetry is best-effort and never throws back to the caller: every sink call, the
crash-reporter `setUser`, and `recordCrash` are individually wrapped in `runCatching`.
A misbehaving sink or reporter can neither break the fan-out nor reach the caller. The
`crashGuard` path must route through `Telemetry.recordCrash`, **not**
`Telemetry.crash.record(...)` directly, so a throwing reporter inside the coroutine
exception handler cannot escape and crash the process.

## Data handling

Event names, param keys/values, and the `setUser` id are forwarded verbatim to every
sink and on to third-party analytics. Callers MUST NOT pass PII: the id must be an
opaque, non-PII pseudonym (server account id or salted hash), never a phone/email/name,
and free user text (search queries, addresses, card data) must never be a param. The
prohibition is enforced by caller discipline; the module does not redact.

## Platform parity (known gap)

Android installs real implementations (`FirebaseAnalyticsSink` + `FirebaseCrashReporter`)
from `YallaApp`. **iOS currently installs nothing** — there is no iOS `installTelemetry`
wiring and no iOS `TelemetrySink`/`CrashReporter`, so every event and crash is silently
discarded on iOS. Closing this requires changes outside this module (an iOS
`installTelemetry()` in the app's `EntryPoint`, an iOS sink/reporter, and exporting this
contract into the iOS framework) and is tracked as the headline telemetry finding.
