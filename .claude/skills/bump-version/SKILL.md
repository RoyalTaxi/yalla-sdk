---
name: bump-version
description: Bump the unified yalla-sdk version in gradle.properties and push to main so CI can publish. Use when Islom wants to release a new version. Triggers include phrases like "bump version", "new version", "release X", "bump sdk to", "version up", "release X.Y.Z-alphaNN".
allowed-tools: Read, Edit, Bash, Glob, Grep
---

# Bump Version

Bump the single unified SDK version and push it to main. CI handles the actual publishing.

## Important: This SDK Uses a Unified Version

Every module publishes at the same version, sourced from `yalla.sdk.version` in `gradle.properties`.
There is no per-module version. The BOM (`bom/`) is a pure `java-platform` that reads this same
property. Do NOT try to bump individual modules — that's not how this repo is structured.

The version scheme is pre-release alpha: `MAJOR.MINOR.PATCH-alphaNN` (e.g., `0.0.8-alpha03`).
See `docs/04-PUBLISHING.md` for the canonical rules.

## Process

### 1. Clarify the Target

Ask Islom (or infer from prompt):
- New version string (full form, e.g., `0.0.8-alpha04`)
- Or a bump hint: "next alpha increment", "patch bump", "minor bump with alpha01 reset"
- If the bump is breaking: is there an ADR in `docs/06-DECISIONS.md`? If not, stop and write one first.

### 2. Verify Preconditions

```bash
git status                        # working tree must be clean
git branch --show-current         # should be main (per docs/04-PUBLISHING.md; CI publishes from main)
```

If the tree is dirty, stop and tell Islom to commit or stash first.
If Islom is on a feature branch, confirm he intends to merge to main before CI picks it up.

### 3. Read Current State

Read `gradle.properties` and find the line:
```properties
yalla.sdk.version=<current>
```

Also check the latest published version if needed:
```bash
gh api "/orgs/RoyalTaxi/packages/maven/uz.yalla.sdk.core/versions" 2>/dev/null | jq '.[0].name' || echo "unable to query packages"
```

### 4. Compute New Version

Follow `docs/04-PUBLISHING.md`:

| Change type         | Bump                                      |
|---------------------|-------------------------------------------|
| Bug fix             | Pre-release increment (alpha02 → alpha03) |
| New component       | Pre-release increment                     |
| Breaking API change | Patch bump + reset to alpha01 (0.0.7-alphaNN → 0.0.8-alpha01) |
| Production release  | Drop `-alpha` entirely                    |

### 5. Apply Edit

Single edit to `gradle.properties`:
```properties
yalla.sdk.version=<new>
```

No changes to `gradle/libs.versions.toml` — there's no per-module version key.
No changes to `bom/` — the BOM reads `yalla.sdk.version` at configuration time via `findProperty`.

### 6. Verify the Change Compiles

```bash
./gradlew build
```

Running the full build catches mis-typed properties and signal-tests the BOM still resolves.

### 7. Public API Check

`./gradlew apiCheck` is wired as of commit `9031d23` (`binary-compatibility-validator` 0.18.1,
experimental Klib mode). Run it before every bump — it covers Native (iosArm64, iosSimulatorArm64)
plus everything in `commonMain` that compiles into those targets:

```bash
./gradlew apiCheck
```

If the diff since the last release touches `**/src/androidMain/**`, also invoke the `audit-api`
skill — it is the manual gate for androidMain-only additions until BCV 0.18.1's AGP 9.0 gap
closes upstream.

For a breaking bump (patch-segment bump with alpha01 reset), invoke `audit-api` regardless so
the PR body documents the full public-surface delta.

### 8. Commit

```bash
git add gradle.properties
git commit -m "chore: bump SDK version to <new>"
```

Commit message template:
```
chore: bump SDK version to X.Y.Z-alphaNN

<Optional: one-line summary of what changed since last version>

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
```

### 9. Report

Print a summary:
- SDK bumped: `<old>` → `<new>`
- Build result
- Next step for Islom: "Push to main — CI will run `./gradlew publish` automatically
  via `.github/workflows/publish.yml`. Then update YallaClient's
  `gradle/libs.versions.toml` to `yalla-sdk = \"<new>\"`."

## Do Not

- Edit individual `<module>/build.gradle.kts` version fields — there are none, versions come
  from the convention plugin `KmpLibraryConventionPlugin.kt` which reads `gradle.properties`
- Edit `gradle/libs.versions.toml` for the bump — the SDK version is not in there
- Run `./gradlew publish` locally — `docs/04-PUBLISHING.md` explicitly says "Never run
  `./gradlew publish` locally; always push to main and let CI handle it"
- Try to bump individual modules — the repo uses a unified version
