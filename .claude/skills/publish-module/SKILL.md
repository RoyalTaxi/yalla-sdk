---
name: publish-module
description: Guide for publishing yalla-sdk modules. In this repo, CI publishes automatically on push to main — local publishing is an emergency override only. Use when Islom wants to release. Triggers include phrases like "publish", "release", "push to maven", "publish module", "ship sdk".
allowed-tools: Read, Bash, Glob, Grep
---

# Publish Module

**Important context**: in yalla-sdk, publishing is done by CI, NOT locally. `docs/04-PUBLISHING.md` says explicitly: *"Never run `./gradlew publish` locally — always push to main and let CI handle it."*

The normal release flow is:
1. `bump-version` skill increments `yalla.sdk.version` in `gradle.properties`
2. Commit + push to main
3. `.github/workflows/publish.yml` runs `./gradlew publish` automatically
4. All modules publish to `https://maven.pkg.github.com/RoyalTaxi/yalla-sdk` at the new unified version
5. BOM auto-republishes because it's a subproject in the root `publish` task

This skill mainly exists to: (a) document the flow, (b) verify a publish succeeded, and (c) provide an emergency local-publish path if CI is broken.

## Preconditions — Normal (CI) Path

Stop and tell Islom if any fail:

1. **Clean working tree**: `git status` shows no uncommitted changes
2. **On main**: `git branch --show-current` returns `main`
3. **Up to date with origin**: `git status` says up to date
4. **Last commit is a version bump**: `git log -1 --oneline` looks like `chore: bump SDK version to ...`
5. **Full build passed locally**: run `./gradlew build` before pushing
6. **Tests pass locally**: `./gradlew test`
7. **Public API audited**: invoke the `audit-api` skill first — apiCheck is not wired up, so the audit is manual

## Normal Publish Flow

### 1. Push to Main

```bash
git push origin main
```

That's it. CI takes over.

### 2. Watch the CI Run

```bash
gh run watch                     # tails the running workflow
# OR
gh run list --workflow=publish.yml --limit 5
```

### 3. Verify the Artifact(s)

Once the workflow completes green, verify each module's latest published version matches the new `yalla.sdk.version`:

```bash
VERSION=$(grep '^yalla\.sdk\.version=' gradle.properties | cut -d= -f2)
for MODULE in core data resources design platform foundation primitives composites maps media firebase bom; do
  echo "=== $MODULE ==="
  gh api "/orgs/RoyalTaxi/packages/maven/uz.yalla.sdk.$MODULE/versions" 2>/dev/null \
    | jq -r '.[0].name' || echo "(package not found or auth required)"
done
echo "Expected: $VERSION"
```

Any module whose latest version doesn't match the expected SDK version indicates a partial publish — investigate the CI log.

### 4. Tag (Optional, Global)

The repo uses a unified version, so per-module tags don't make sense. If a tag is wanted at all, it's a single global tag:

```bash
git tag v$(grep '^yalla\.sdk\.version=' gradle.properties | cut -d= -f2)
git push origin v$(grep '^yalla\.sdk\.version=' gradle.properties | cut -d= -f2)
```

Tagging is opt-in. CI does not create tags automatically.

### 5. Report

Print:
- SDK version published: `<version>`
- Modules published: (list)
- GitHub Packages base URL: `https://maven.pkg.github.com/RoyalTaxi/yalla-sdk`
- **Next step**: *"Update `YallaClient/gradle/libs.versions.toml` to reference `yalla-sdk = \"<version>\"` in a separate PR."*

## Emergency Local Publish (Only When CI Is Broken)

Local publishing is explicitly discouraged by `docs/04-PUBLISHING.md`. Use only when CI is blocked and a release cannot wait.

```bash
export GITHUB_ACTOR=isloms
export GITHUB_TOKEN=<PAT with write:packages>
./gradlew publish                 # all modules at once — there's no per-module-only publish configured for CI
```

Note: the root `./gradlew publish` publishes ALL modules, including the BOM. There's no per-module release path in CI.

## Troubleshooting

- **CI fails with `401 Unauthorized`**: the `GITHUB_TOKEN` used by the workflow lacks `write:packages` scope, or the workflow's auth is misconfigured
- **CI fails with `409 Conflict`**: the version already exists on GitHub Packages. Check whether a previous publish completed partially, then bump the version and retry
- **Build passes locally but CI build fails**: usually a JDK/tool version mismatch. Check `.github/workflows/publish.yml` for the configured JDK
- **BOM doesn't include a new module**: the module's constraint wasn't added to `bom/build.gradle.kts`. Add it and bump + re-publish

## Non-goals

- Do NOT run `./gradlew publish` locally as the normal path — always push to main
- Do NOT try to publish a single module when CI's workflow is the authority — CI publishes all modules together
- Do NOT create PRs from this skill — publishing is a `main`-only action, the PR happens upstream during the bump flow
- Do NOT bump the version from this skill — `bump-version` owns that
