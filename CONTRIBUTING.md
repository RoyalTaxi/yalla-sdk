# Contributing

Thanks for considering a contribution. Read this page before opening a PR.

## Scope Discipline

The Yalla SDK is Yalla-opinionated. PRs adding backend-agnostic abstractions, alternative payment providers, or anything that broadens the SDK beyond ride-hailing are likely to be declined. See [`docs/06-DECISIONS.md`](docs/06-DECISIONS.md) for the design ADRs.

## Before Submitting a PR

- Read the relevant docs under `docs/`.
- Run `./gradlew ktlintCheck detekt` locally at minimum (fast, no Xcode needed). For full coverage: `./gradlew apiCheck allTests` (requires Xcode + CocoaPods; these also run on `main` via `publish.yml`). PR CI runs lint-only.
- If your change modifies public API, run `./gradlew apiDump` and commit the updated `<module>/api/*.api` baselines.
- If your change affects iOS binary-compat, run the `audit-api` skill (see `.claude/skills/audit-api/SKILL.md`) and include its diff in the PR body.

## Commit Style

Conventional commits: `type(scope): subject`.

- `feat(module):` — new public surface
- `fix(module):` — bug fix
- `refactor(module):` — internal restructuring, no public-surface change
- `docs(module):` — docs only
- `test(module):` — tests only
- `build:` — build system, Gradle, CI
- `ci:` — GitHub Actions workflows

## PR Checklist

- [ ] CI is green.
- [ ] `apiCheck` is green; baselines updated if needed.
- [ ] Tests cover the change.
- [ ] Docs updated if public surface changed.
- [ ] Visual-verified via the live-verify loop (wire to YallaClient, launch emulator via MCPs, verify pixel-accurate against design intent) for non-trivial UI changes.

## Code Style

- Kotlin official style, max line length 120.
- ktlint enforced in CI.
- Public API requires KDoc.
- `val` > `var`. Sealed hierarchies for state.
- `Either<E, D>` (error-first) for fallible operations; no raw try-catch in business logic.

## Local Setup

- JDK 21 (`JAVA_HOME` set).
- Android Studio latest stable, or IntelliJ IDEA with the Android + KMP plugins.
- Xcode 16+ for iOS builds.
- CocoaPods (`gem install cocoapods`) for iOS.

## Versioning Policy (Pre-1.0)

This repo is in full-risk pre-1.0 mode until `1.0.0` tags. Expect breaking changes between alpha releases. See `.claude/rules/publishing.md` for the bump rules.

## Maintainer

Islom Sheraliyev (`@isloms`) is the sole maintainer. Response is best-effort.
