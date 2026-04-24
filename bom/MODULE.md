# Module bom

> First-party version alignment for the Yalla SDK.

## What this is

A Maven Bill of Materials (`java-platform`). Pins every published `uz.yalla.sdk:*` module to the same version.

## What this is NOT

A transitive-dependency BOM. Does **not** align versions of Compose Multiplatform, Ktor, Koin, or any other third-party library. Bring your own third-party BOMs.

## Usage

```kotlin
dependencies {
    implementation(platform("uz.yalla.sdk:bom:<version>"))
    implementation("uz.yalla.sdk:core")       // version omitted
    implementation("uz.yalla.sdk:design")
    implementation("uz.yalla.sdk:primitives")
}
```
