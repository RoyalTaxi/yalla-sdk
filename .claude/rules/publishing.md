---
paths:
  - "**/build.gradle.kts"
  - "settings.gradle.kts"
  - "gradle/libs.versions.toml"
  - "gradle.properties"
  - "bom/**"
  - "build-logic/**"
---

# Publishing and Version Management Rules

This is a library monorepo. Version bumps and publishes affect downstream consumers (primarily `YallaClient`). These rules are enforced during any edit to build files.

## Version Source (Unified)

Single source of truth: `gradle.properties` key `yalla.sdk.version` (e.g., `0.0.8-alpha03`).

**The repo uses a unified version** for all modules — there is no per-module version. The convention plugin `KmpLibraryConventionPlugin` (`build-logic/convention/...`) and the BOM (`bom/build.gradle.kts`) both read this property at configuration time via `findProperty("yalla.sdk.version")`.

Consequences:
- Do NOT add per-module version keys to `gradle/libs.versions.toml`
- Do NOT set `group` or `version` in module `build.gradle.kts` — the convention plugin does that
- Do NOT try to publish a single module at a different version than its siblings — it's not how the repo works

## Version Scheme (Pre-1.0 Alpha)

Per `docs/04-PUBLISHING.md`:

| Change type | Bump |
|-------------|------|
| Bug fix | Pre-release increment (alpha02 → alpha03) |
| New component | Pre-release increment |
| Breaking API change | Third-segment bump + reset to alpha01 (0.0.7-alphaNN → 0.0.8-alpha01) |
| Production release | Drop the `-alpha` suffix entirely |

Use the `bump-version` skill — it edits `gradle.properties` directly.

## BOM

`bom/` is a `java-platform` with no `src/` directory. Its `dependencies { constraints { ... } }` block in `bom/build.gradle.kts` is the source of truth for what the BOM ships.

```kotlin
// bom/build.gradle.kts
plugins { `java-platform` }

group = "uz.yalla.sdk"
version = project.findProperty("yalla.sdk.version") as String

dependencies {
    constraints {
        api("uz.yalla.sdk:core:$version")
        api("uz.yalla.sdk:data:$version")
        // ... every published module ...
    }
}
```

The BOM auto-tracks the unified version. Bumping `yalla.sdk.version` automatically bumps every constraint and the BOM itself — no separate BOM version, no manual sync step.

**When adding a new module**: add a new `api("uz.yalla.sdk:<name>:$version")` line to the constraints block. See the `add-module` skill.

## Convention Plugins

Module `build.gradle.kts` uses plugin IDs, not catalog aliases:

```kotlin
// KMP library
plugins {
    id("yalla.sdk.kmp")
}

// Compose UI library
plugins {
    id("yalla.sdk.kmp.compose")
}
```

Real plugin implementations in `build-logic/convention/`:
- `yalla.sdk.kmp` → `KmpLibraryConventionPlugin`
- `yalla.sdk.kmp.compose` → `KmpComposeConventionPlugin`

Both plugins configure KMP targets, set `group = "uz.yalla.sdk"`, set `version = findProperty("yalla.sdk.version")`, and apply `maven-publish`. Do NOT duplicate this config in individual modules.

## Publish Targets

All modules publish to a **single GitHub Packages repository**:

```
https://maven.pkg.github.com/RoyalTaxi/yalla-sdk
```

Maven coordinates for any artifact: `uz.yalla.sdk:<moduleName>:<version>` (e.g., `uz.yalla.sdk:core:0.0.8-alpha03`).

Required env vars for publishing (handled by CI; only relevant for emergency local override):
- `GITHUB_ACTOR` — GitHub username (`isloms`)
- `GITHUB_TOKEN` — PAT with `write:packages` scope

## Who Publishes

**CI, not you.** `.github/workflows/publish.yml` runs `./gradlew publish` on push to `main`, publishing all modules at the current unified version.

From `docs/04-PUBLISHING.md`: *"Never run `./gradlew publish` locally — always push to main and let CI handle it."*

Emergency local override is allowed but opt-in. See the `publish-module` skill for the escape hatch.

## Publish Preconditions (Before Pushing to Main)

- [ ] Working tree is clean
- [ ] Version bumped via `bump-version` skill
- [ ] Full build passes: `./gradlew build`
- [ ] All tests pass: `./gradlew test`
- [ ] Public API audited via `audit-api` skill (apiCheck not wired up — this is the manual fallback)
- [ ] Breaking changes have an ADR in `docs/06-DECISIONS.md`

Skipping any of these ships broken artifacts to downstream consumers.

## Never

- Add per-module version keys to `gradle/libs.versions.toml` (contradicts the unified version)
- Set `group` or `version` in a module `build.gradle.kts` (convention plugin does it)
- Publish from a feature branch (CI publishes from main only)
- Publish with uncommitted changes
- Run `./gradlew publish` locally as the normal path (emergency override only)
- Try to publish a single module — CI publishes all modules together

## Pre-Existing Repo Inconsistencies (Known)

Flag these if touched, but do NOT silently "fix" them:

1. `gradle/libs.versions.toml` has `yalla-sdk = "0.0.1-alpha08"` which is stale — publishing reads `gradle.properties` instead. A handful of `[libraries]` entries reference this stale value but it's dead for actual publishing.
2. The `docs/04-PUBLISHING.md` table labels breaking changes as "Minor" while the example (`0.0.7 → 0.0.8`) is a third-segment bump (SemVer Patch). Labeling inconsistency in the doc itself.

## Gradle Properties

Respect the repo's performance tuning in `gradle.properties`:
- Configuration cache enabled
- Build cache enabled
- Parallel workers
- Heap sizing tuned for Kotlin Native builds

Do not add flags that disable these without writing an ADR first.
