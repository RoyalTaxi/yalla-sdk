# yalla-sdk — Project Instructions

## What This Is

KMP library monorepo powering the Yalla ride-hailing app. Consumers (like `YallaClient`) pull modules from GitHub Packages. **This is a library repo**, so the primary concern is **public API stability** — every published change is seen by downstream consumers.

## Existing Documentation

Before any non-trivial change, **read the existing `docs/`** — it's comprehensive and current:

| File | Purpose |
|------|---------|
| `docs/00-START-HERE.md` | Onboarding; read first |
| `docs/01-ARCHITECTURE.md` | Module map and dependency graph |
| `docs/02-COMPONENT-GUIDE.md` | How to build a component |
| `docs/03-PATTERNS.md` | Common recipes |
| `docs/04-PUBLISHING.md` | Version bump + publish flow (authoritative) |
| `docs/05-TESTING.md` | Testing conventions |
| `docs/06-DECISIONS.md` | Architecture Decision Records |
| `COMPONENT_STANDARD.md` | Gold-standard component rules |
| `SDK_STATUS.md` | Per-module status |
| `AUDIT_RESULTS.md` | Last full audit findings |

Do not duplicate content between those files and `docs/obsidian/`. The `docs/obsidian/` vault is for **session logs, stale-stamped architecture snapshots, and decision records from new work** — not a rewrite of the onboarding guide.

## SDLC

Full pipeline per global CLAUDE.md. Project-specific VERIFY for this repo:

```bash
./gradlew build                                   # All modules, all targets
./gradlew :<module>:test                          # Unit tests
./gradlew ktlintCheck                             # Lint
./gradlew detekt                                  # Static analysis
```

**Note**: the `org.jetbrains.kotlinx.binary-compatibility-validator` plugin is **NOT currently wired up** in this repo. `./gradlew apiCheck` does not exist. The `audit-api` skill provides a manual-diff fallback until the plugin is added.

**Library-specific hard rules**:
- **No breaking changes without a patch bump + alpha reset** (during pre-1.0). Breaking change = anything that makes downstream `YallaClient` fail to compile or change runtime behavior
- **Public API is annotated and stable.** `@OptIn`-gated APIs can change; non-gated APIs cannot without deprecation cycle
- **Binary compatibility matters**: since `apiCheck` isn't wired up, rely on the `audit-api` skill for manual public-API diffs before any non-trivial bump
- **Unified version**: the repo uses a single `yalla.sdk.version` in `gradle.properties`. All modules publish at that version. The `bom/` module is a `java-platform` that auto-tracks it

## Modules

Real modules (verified against the live repo):

```
yalla-sdk/
├── bom/              # BOM — java-platform, tracks yalla.sdk.version, pins all modules
├── core/             # Domain models, contracts, utils (uz.yalla.sdk:core)
├── data/             # SafeApiCall, mappers, remote models (uz.yalla.sdk:data)
├── resources/        # Shared strings, drawables, fonts (uz.yalla.sdk:resources)
├── design/           # Design tokens, colors, typography (uz.yalla.sdk:design)
├── platform/         # Platform-native UI helpers (uz.yalla.sdk:platform)
├── foundation/       # Low-level building blocks (uz.yalla.sdk:foundation)
├── primitives/       # Compose UI primitives — buttons, dialogs, fields (uz.yalla.sdk:primitives)
├── composites/       # Higher-level UI compositions (uz.yalla.sdk:composites)
├── maps/             # Google Maps + MapLibre abstraction (uz.yalla.sdk:maps)
├── media/            # Camera, gallery, media picker (uz.yalla.sdk:media)
├── firebase/         # Firebase analytics, crashlytics, messaging (uz.yalla.sdk:firebase)
└── build-logic/      # Convention plugins (not published)
```

Group ID for all published artifacts: `uz.yalla.sdk`. See `docs/01-ARCHITECTURE.md` for the dependency graph.

## Version & Publish Source of Truth

- **Unified version**: `gradle.properties` → `yalla.sdk.version` (e.g. `0.0.8-alpha03`)
- **BOM**: `bom/build.gradle.kts` reads `yalla.sdk.version` via `findProperty` and applies it to every constraint
- **Publishing**: CI only. `.github/workflows/publish.yml` runs `./gradlew publish` on push to `main`. **Never run `./gradlew publish` locally** (per `docs/04-PUBLISHING.md`). Emergency local override exists but is opt-in
- **Single publish repo**: `https://maven.pkg.github.com/RoyalTaxi/yalla-sdk` — all modules go to one repo, not one per module

## Quick Commands

```bash
# Build
./gradlew build
./gradlew :<module>:build                         # single module

# Test
./gradlew test
./gradlew :<module>:test

# Lint
./gradlew ktlintFormat                            # auto-fix code style
./gradlew ktlintCheck
./gradlew detekt

# NOTE: apiCheck / apiDump not wired up. Use the audit-api skill for public API diffs.

# Publish (CI does this automatically on push to main — don't run locally)
git push origin main                              # normal path
# Emergency local override (not the normal path):
GITHUB_ACTOR=isloms GITHUB_TOKEN=<token> ./gradlew publish
```

See `docs/04-PUBLISHING.md` for the full publish flow.

## Convention Plugins

Module `build.gradle.kts` uses plugin IDs directly (not catalog aliases):

```kotlin
// core/build.gradle.kts (KMP library)
plugins {
    id("yalla.sdk.kmp")
}

// primitives/build.gradle.kts (Compose UI library)
plugins {
    id("yalla.sdk.kmp.compose")
}
```

Plugin implementations in `build-logic/convention/`:
- `yalla.sdk.kmp` → `KmpLibraryConventionPlugin`
- `yalla.sdk.kmp.compose` → `KmpComposeConventionPlugin`

Both plugins set `group = "uz.yalla.sdk"` and `version = findProperty("yalla.sdk.version")` automatically — module build files don't set these themselves.

## Code Style

- Kotlin official style, max line length 120
- `val` > `var`, sealed interfaces for state
- **Public API hygiene**: every public type, function, and property needs KDoc
- **No throwing for business errors**: use `Either` in domain/data layers. Exceptions only for programmer errors
- **Experimental APIs**: gate with `@RequiresOptIn` markers (see `.claude/rules/library-api.md`)
- Auto-format on save via `.claude/settings.json` ktlint hook

## Publishing Flow (Summary)

Full details in `docs/04-PUBLISHING.md`. TL;DR:

1. Feature branch, make your change
2. If breaking, write ADR in `docs/06-DECISIONS.md`
3. Run `audit-api` skill for a manual public-API diff (apiCheck not wired up)
4. Update `yalla.sdk.version` in `gradle.properties` via `bump-version` skill
5. Full build + tests locally
6. Commit, PR, merge to main
7. CI publishes all modules automatically via `.github/workflows/publish.yml`
8. Update downstream consumers (`YallaClient`'s `gradle/libs.versions.toml`) in a separate PR

The `bump-version` skill handles steps 4-6. The `audit-api` skill handles step 3.

## Second Brain

Project-specific Obsidian vault: `docs/obsidian/` (symlinked into `~/Ildam-Brain/projects/yalla-sdk`). At session CLOSE, invoke the `update-obsidian-vault` skill. The vault captures **new decisions and session logs**; the existing `docs/00-START-HERE.md` through `06-DECISIONS.md` remain the stable long-form reference.

## Skills Available

Defined in `.claude/skills/`:
- `bump-version` — Bump unified `yalla.sdk.version` in `gradle.properties`, commit, CI publishes
- `publish-module` — Docs + emergency local-publish fallback (normal path is CI)
- `audit-api` — Manual public-API diff (apiCheck fallback)
- `add-module` — Scaffold a new module following the component standard

## Subagents Available

Defined in `.claude/agents/`:
- `kmp-library-author` — KMP library API design specialist. Knows `expect`/`actual`, binary compatibility, `@RequiresOptIn` discipline.

## Path-Scoped Rules

Defined in `.claude/rules/` (auto-loaded when editing matching paths):
- `library-api.md` — `**/src/commonMain/**/*.kt`, `androidMain`, `iosMain` — Public API stability, KDoc, `@RequiresOptIn`
- `publishing.md` — `**/build.gradle.kts`, `gradle/libs.versions.toml`, `gradle.properties`, `bom/**`, `build-logic/**` — Version source, BOM, convention plugins, publish targets
