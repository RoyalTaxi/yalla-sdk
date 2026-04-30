# Yalla SDK

> Kotlin Multiplatform LEGO set — bricks for building ride-hailing apps. Domain types, networking, design tokens, Compose UI primitives + composites, platform glue, Firebase, maps, media. Nothing locked together; you assemble what you need.

[![Maven](https://img.shields.io/badge/maven-1.0.0--alpha01-blue)](https://github.com/RoyalTaxi/yalla-sdk/packages)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![KMP](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg)](https://kotlinlang.org/docs/multiplatform.html)

## What's in the box

The SDK is split into 10 Kotlin Multiplatform modules. Each module has one
responsibility, declares its public API explicitly, and ships its own
`MODULE.md` describing what it is — and what it deliberately is not.

| Module | What it ships | Headline types |
|---|---|---|
| **`core`** | Pure domain — value classes, sealed errors, serializable models | `Either<E, A>`, `DataError`, `OrderId`, `OrderStatus`, `PaymentKind` |
| **`data`** | Ktor-based networking, multiplatform-settings persistence, DTO mappers | `SafeApiCall`, `NetworkConfig`, `ConfigPreferences`, `RetryWithBackoff` |
| **`design`** | Theme tokens — color, font, space, radius, motion | `System.color.*`, `System.font.*`, `YallaTheme` |
| **`foundation`** | ViewModel infra (Orbit-friendly), location tracker, locale glue, settings options | `BaseViewModel`, `LocationManager`, `LanguageOption`, `MapOption` |
| **`primitives`** | Stateless Compose UI atoms | `PrimaryButton`, `NumberField`, `LoadingIndicator`, `LocationPin` |
| **`composites`** | Pre-built assemblies of primitives + design tokens | `ContentCard`, `ListItem`, `ConfirmationSheet`, `RouteView`, `SnackbarHost` |
| **`platform`** | `expect/actual` host for native sheet, picker, switch, navigation | `NativeSheet`, `NativeWheelDatePicker`, `NavigatorImpl` |
| **`firebase`** | Thin facade over gitlive Firebase — analytics, crashlytics, messaging | `YallaFirebase`, `YallaAnalytics`, `YallaCrashlytics` |
| **`media`** | KMP camera + image picker + gallery + compression | `YallaCamera`, `YallaGallery`, `compressImage` |
| **`maps`** | Provider-agnostic Compose map (MapLibre + Google), runtime swap | `MapProvider`, `MapController`, `LiteMap`, `ExtendedMap` |

A `bom` module ships version alignment so consumers can pin once and pull
matching versions of every brick.

## Mental model

```
┌─────────────────────────────────────────────┐
│  YOUR APP (assembly)                        │
│  Feature ViewModels, screens, business logic│
└──────────────────────────┬──────────────────┘
                           │
       ┌───────────────────┼───────────────────┐
       ▼                   ▼                   ▼
  composites           primitives           foundation
       │                   │                   │
       └────────┬──────────┴───────┬───────────┘
                ▼                  ▼
             design            platform
                                   │
                                   ▼
                                 ...
       core ◄──── data, firebase, maps, media
```

Each module is a brick. Domain types travel up; UI types come down. No
module imports its parent. Try the LEGO test — pick any module, ask "could
I drop this into a different ride-hailing app?" If the answer is no, the
module is still doing too much.

## Install

The SDK publishes to GitHub Packages at
`https://maven.pkg.github.com/RoyalTaxi/yalla-sdk`. Authenticate with a
personal access token that has `read:packages` scope.

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/RoyalTaxi/yalla-sdk")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

```kotlin
// app/build.gradle.kts — pull only the bricks you need
dependencies {
    implementation(platform("uz.yalla.sdk:bom:1.0.0-alpha01"))
    implementation("uz.yalla.sdk:core")
    implementation("uz.yalla.sdk:data")
    implementation("uz.yalla.sdk:design")
    implementation("uz.yalla.sdk:foundation")
    implementation("uz.yalla.sdk:primitives")
    implementation("uz.yalla.sdk:composites")
    implementation("uz.yalla.sdk:platform")
    implementation("uz.yalla.sdk:firebase")
    implementation("uz.yalla.sdk:media")
    implementation("uz.yalla.sdk:maps")
}
```

## Quick example

```kotlin
@Composable
fun LoginScreen(
    state: LoginState,
    onPhoneChange: (String) -> Unit,
    onSubmit: () -> Unit,
) = YallaTheme {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .background(System.color.background.base)
            .padding(16.dp),
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
}
```

## Targets

- Android (`compileSdk = 36`, `minSdk = 26`)
- iOS (`iosArm64`, `iosSimulatorArm64` — deployment target 16.6+)

## Versioning

Single SDK version pinned in `gradle.properties`
(`yalla.sdk.version`). Every module publishes at the same version. The
`bom` module aligns transitive versions for consumers.

Currently in alpha; public API may change between alpha releases. See
each module's `MODULE.md` for "What this is" / "What this is NOT" before
relying on a particular type.

## Contributing

This SDK is developed and used by [RoyalTaxi](https://royaltaxi.uz) for
the Yalla ride-hailing app. External contributions are welcome —
open an issue first to discuss scope. Every PR must:

- Pass the full test suite (`./gradlew :<module>:allTests`).
- Update the affected module's `MODULE.md` if public surface changes.
- Follow the established Colors + Dimens + Defaults convention for new
  Compose components (see `primitives/MODULE.md`).

## License

Apache License 2.0 — see [LICENSE](LICENSE).
