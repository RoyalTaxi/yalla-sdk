# Module bom

> First-party version alignment for the Yalla SDK.

## What this is

A Maven Bill of Materials (`java-platform`). It pins every published first-party module (`uz.yalla.sdk:*`) to the same version as itself. Consumers import the BOM and omit version strings on individual module dependencies.

## What this is NOT

A transitive-dependency BOM. This BOM does **not** align versions of Compose Multiplatform, Ktor, Koin, kotlinx-coroutines, or any other third-party library. Bring your own third-party BOMs.

## Usage

```kotlin
dependencies {
    implementation(platform("uz.yalla.sdk:bom:<version>"))
    implementation("uz.yalla.sdk:core")       // version omitted
    implementation("uz.yalla.sdk:design")     // version omitted
    implementation("uz.yalla.sdk:primitives") // version omitted
}
```

## How the version is set

The BOM auto-tracks `yalla.sdk.version` from `gradle.properties`. Bumping the unified SDK version automatically bumps every constraint and the BOM artifact itself.

## Adding a new module to the BOM

When a new module is published under `uz.yalla.sdk:*`, add a constraint in `bom/build.gradle.kts`:

```kotlin
dependencies {
    constraints {
        api("uz.yalla.sdk:core:$version")
        // ...
        api("uz.yalla.sdk:<new-module>:$version")
    }
}
```
