# Changelog

All notable changes to the Yalla SDK are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versioning follows [SemVer](https://semver.org/spec/v2.0.0.html) **post-1.0**; pre-1.0 is full-risk mode (every third-segment bump may be breaking, per `.claude/rules/publishing.md`).

## [Unreleased]

### Added
- `binary-compatibility-validator` plugin; `apiCheck` enforces JVM/Android API stability in CI.
- Root POM metadata (name, description, url, licenses, scm, developers) on every published module.
- GitHub Pages deployment pipeline for Dokka reference docs.
- OSS-hygiene files: `LICENSE`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, `SECURITY.md`, `CHANGELOG.md`, `SUPPORT.md`, `CODEOWNERS`, PR + issue templates.
- Public `.github/workflows/ci.yml` — PR + push gate for lint, API check, and tests.

### Removed
- Stale `yalla-sdk = "0.0.1-alpha08"` entry from `gradle/libs.versions.toml` (dead; publishing reads `gradle.properties`).

### Notes

Phase 1 of the v1.0 launch (see `docs/superpowers/specs/2026-04-21-yalla-sdk-v1-launch-design.md`). No code-level changes in this phase.

---

## [0.0.8-alpha04] — 2026-04-21

Released via CI before the launch-spec work began. See git history `b35e248` for contents.
