# Yalla SDK

[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

A Kotlin Multiplatform SDK powering the Yalla ride-hailing app. Public, Apache-2.0-licensed source. Binaries distributed via GitHub Packages.

> **Positioning.** This is Yalla's engine — opinionated around Yalla's backend envelope, preference schema, and Uzbekistan-specific defaults. Others are welcome to adopt the SDK, but it is not a general-purpose ride-hailing library. If you need backend-agnostic abstractions, you will be happier forking than configuring.

## Modules

| Module | Coordinates | Description |
|---|---|---|
| `core` | `uz.yalla.sdk:core:<version>` | Domain models, `Either`, `DataError`, preference contracts |
| `data` | `uz.yalla.sdk:data:<version>` | `safeApiCall`, `HttpClientFactory`, DataStore-backed preferences |
| `resources` | `uz.yalla.sdk:resources:<version>` | Shared icons, strings, drawables, fonts |
| `design` | `uz.yalla.sdk:design:<version>` | `ColorScheme`, `FontScheme`, `YallaTheme` |
| `foundation` | `uz.yalla.sdk:foundation:<version>` | `BaseViewModel`, `LocationManager`, settings |
| `platform` | `uz.yalla.sdk:platform:<version>` | `expect`/`actual` native wrappers (`NativeSheet`, etc.) |
| `primitives` | `uz.yalla.sdk:primitives:<version>` | Building-block UI (buttons, fields, indicators) |
| `composites` | `uz.yalla.sdk:composites:<version>` | Composed UI (cards, sheets, items) |
| `maps` | `uz.yalla.sdk:maps:<version>` | Google Maps + MapLibre abstraction |
| `media` | `uz.yalla.sdk:media:<version>` | Camera, image picker, compression |
| `firebase` | `uz.yalla.sdk:firebase:<version>` | Analytics, crashlytics, messaging |
| `bom` | `uz.yalla.sdk:bom:<version>` | First-party version alignment |

## Installation

GitHub Packages requires authentication to consume Maven artifacts. Add a GitHub personal access token with `read:packages` scope to your `~/.gradle/gradle.properties`:

```properties
gpr.user=<your-github-username>
gpr.key=<your-github-pat>
```

Then add the repository and a module to your project's `build.gradle.kts`:

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/RoyalTaxi/yalla-sdk")
        credentials {
            username = providers.gradleProperty("gpr.user").get()
            password = providers.gradleProperty("gpr.key").get()
        }
    }
}

dependencies {
    implementation("uz.yalla.sdk:core:<version>")
    implementation("uz.yalla.sdk:design:<version>")
    implementation("uz.yalla.sdk:primitives:<version>")
}
```

Or import the BOM for first-party version alignment:

```kotlin
dependencies {
    implementation(platform("uz.yalla.sdk:bom:<version>"))
    implementation("uz.yalla.sdk:core")
    implementation("uz.yalla.sdk:design")
}
```

## Documentation

- Getting started: [`docs/00-START-HERE.md`](docs/00-START-HERE.md)
- Architecture: [`docs/01-ARCHITECTURE.md`](docs/01-ARCHITECTURE.md)
- Building a component: [`docs/02-COMPONENT-GUIDE.md`](docs/02-COMPONENT-GUIDE.md)
- Common patterns: [`docs/03-PATTERNS.md`](docs/03-PATTERNS.md)
- Publishing flow: [`docs/04-PUBLISHING.md`](docs/04-PUBLISHING.md)
- Testing: [`docs/05-TESTING.md`](docs/05-TESTING.md)
- Design decisions (ADRs): [`docs/06-DECISIONS.md`](docs/06-DECISIONS.md)
- API reference: <https://royaltaxi.github.io/yalla-sdk/>

## Versioning

Pre-1.0 is **full-risk mode** — every third-segment bump may be breaking. Expect API churn between alphas, no deprecation cycle. See `.claude/rules/publishing.md` for the rules.

Post-1.0 (from `1.0.0` onward) follows strict SemVer: `1.0.N` is patch only, `1.N.0` is additive only, `2.0.0` is breaking.

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md). Honest stance: this is Yalla's SDK. External PRs are welcome but not actively solicited; scope discipline is tight.

## Security

See [`SECURITY.md`](SECURITY.md) — do not open public issues for vulnerabilities.

## License

Apache 2.0 — see [`LICENSE`](LICENSE).
