---
name: add-module
description: Scaffold a new yalla-sdk module following the existing component standard. Use when Islom wants to add a new library module. Triggers include phrases like "add module", "new module", "create yalla library", "scaffold module".
allowed-tools: Read, Write, Edit, Bash, Glob, Grep
---

# Add Module

Scaffold a new module in yalla-sdk following the existing component standard defined in `COMPONENT_STANDARD.md`.

## Preconditions

1. Read `COMPONENT_STANDARD.md` — this is the source of truth for what a module must have
2. Read an existing well-structured module (e.g., `core`, `design`, or `primitives` for Compose modules) as a template reference
3. Confirm the proposed name follows conventions: lowercase, hyphen-separated, module directory name is just the short form (e.g., `analytics`, not `yalla-analytics`)

## Process

### 1. Gather Requirements

Ask Islom:
- Module name (short form, e.g., `analytics`)
- Purpose in one sentence
- Target platforms (common, android, ios, or all)
- Dependencies on other yalla-sdk modules
- Is this a KMP library (`yalla.sdk.kmp`) or a Compose library (`yalla.sdk.kmp.compose`)?

### 2. Create Directory Structure

Follow the standard KMP library layout (check `core/` or `primitives/` for real examples):

```
<name>/
├── build.gradle.kts
├── src/
│   ├── commonMain/kotlin/uz/yalla/sdk/<name>/
│   │   ├── <Name>.kt                     # main entry point
│   │   └── ...
│   ├── commonTest/kotlin/uz/yalla/sdk/<name>/
│   ├── androidMain/kotlin/uz/yalla/sdk/<name>/   # if android target
│   └── iosMain/kotlin/uz/yalla/sdk/<name>/       # if iOS target
└── CHANGELOG.md                           # optional, start with current yalla.sdk.version
```

### 3. Write `build.gradle.kts`

Use the convention plugin — group and version are set by the plugin from `gradle.properties`, so this file should be minimal:

```kotlin
plugins {
    id("yalla.sdk.kmp")                          // KMP library
    // id("yalla.sdk.kmp.compose")               // OR use this for Compose UI libraries
    // alias(libs.plugins.kotlin.serialization)  // if needed
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Typesafe project accessors
            implementation(projects.core)
            // implementation(projects.foundation)

            // Third-party deps via version catalog
            // implementation(libs.ktor.client.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
```

Do NOT set `group`, `version`, or hardcode plugin versions — the convention plugin handles those from `gradle.properties` → `yalla.sdk.version`.

### 4. Register in `settings.gradle.kts`

```kotlin
include(":<name>")
```

### 5. Version Catalog (usually NOT needed)

The repo uses a **unified version** from `gradle.properties`. Do NOT add a per-module version key to `gradle/libs.versions.toml`.

Most modules are referenced via typesafe project accessors (`projects.<name>`) rather than through the catalog. Only add a `[libraries]` entry if a sibling module needs to consume it through the catalog form — and if so, point it at the existing unified version:

```toml
[libraries]
yalla-<name> = { module = "uz.yalla.sdk:<name>", version.ref = "yalla-sdk" }
```

Note: group ID is `uz.yalla.sdk`. The `yalla-sdk` version ref in the catalog is currently stale relative to `gradle.properties` — this is a known repo inconsistency that publishing works around (publishing reads `gradle.properties` directly).

### 6. Update `bom/build.gradle.kts`

The BOM is a `java-platform` with no `src/` directory. Add a constraint so the BOM ships the new module. Edit `bom/build.gradle.kts` and add to the existing `dependencies { constraints { ... } }` block:

```kotlin
dependencies {
    constraints {
        // ... existing constraints ...
        api("uz.yalla.sdk:<name>:$version")
    }
}
```

The BOM reads `version` from `project.findProperty("yalla.sdk.version")` — no separate BOM version, no per-module pinning logic.

### 7. Write a Starter Entry Point

Create `<name>/src/commonMain/kotlin/uz/yalla/sdk/<name>/<Name>.kt` with a minimal public API, fully KDoc'd:

```kotlin
package uz.yalla.sdk.<name>

/**
 * Entry point for the <name> module.
 *
 * <One-sentence description of what consumers get from this.>
 */
public object <Name> {
    // Keep this empty or with minimal marker functions during scaffolding.
    // Real API comes in a follow-up session.
}
```

### 8. Add a Placeholder Test

`<name>/src/commonTest/kotlin/uz/yalla/sdk/<name>/<Name>Test.kt`:

```kotlin
package uz.yalla.sdk.<name>

import kotlin.test.Test

class <Name>Test {
    @Test
    fun moduleCompiles() {
        // Smoke test — ensures commonTest source set and DI wire up correctly.
    }
}
```

### 9. API Dump (skip — not wired up)

The `binary-compatibility-validator` plugin is not currently wired up in this repo. `apiDump` and `apiCheck` tasks do not exist. Skip this step. If/when the validator is added, the generated dump goes under `<name>/api/`.

### 10. Update Documentation

- `docs/01-ARCHITECTURE.md` — add the module to the dependency graph
- `docs/00-START-HERE.md` — update the module list if needed
- `SDK_STATUS.md` — add the module with `experimental` status
- `docs/06-DECISIONS.md` — write an ADR if the new module represents a significant architectural addition

### 11. Build and Verify

```bash
./gradlew :<name>:build
./gradlew :<name>:test
# apiCheck not wired up — skip
```

### 12. Commit

```
feat(<name>): scaffold module

<One-line description>

- settings.gradle.kts: include :<name>
- bom/build.gradle.kts: add uz.yalla.sdk:<name> constraint
- <name>/: initial scaffold per COMPONENT_STANDARD.md
- docs/01-ARCHITECTURE.md: updated dependency graph
- SDK_STATUS.md: added with experimental status

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
```

## Non-goals

- Do NOT run `./gradlew publish` — CI publishes automatically on push to main
- Do NOT bump `yalla.sdk.version` as part of scaffolding — a new module at the current unified version is fine; bump happens via the `bump-version` skill when releasing
- Do NOT add per-module version keys to `libs.versions.toml` — the repo uses a unified version
- Do NOT add implementation details beyond the minimal stub — leave real development to a follow-up session
