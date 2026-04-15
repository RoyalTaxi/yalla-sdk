---
title: "yalla-sdk"
last_verified_sha: TBD
last_updated: 2026-04-15
last_author: claude
status: draft
tags: [project, entry]
---

# yalla-sdk

**KMP library monorepo** — core, data, design, components, platform, maps, media, firebase, foundation, composites, bom.

This is the Obsidian vault for `yalla-sdk`. It complements the existing `docs/00-START-HERE.md` through `06-DECISIONS.md` — those are the stable long-form reference, this vault is for **new decisions, session logs, and stamped architecture snapshots**.

## Navigation

- [[00-overview]] — what this SDK is, who consumes it, status
- [[01-architecture/README|Architecture]] — module map, dependency graph, binary compatibility
- [[02-patterns/README|Patterns]] — component gold standard, API design patterns
- [[03-decisions/README|Decisions]] — ADRs (may cross-reference `docs/06-DECISIONS.md`)
- [[04-features/README|Features]] — per-module notes
- [[05-operations/README|Operations]] — publish flow, version bump, release history
- [[06-sessions/README|Sessions]] — auto-maintained session logs

## Important Existing Docs (outside this vault)

These live at the repo root in `docs/` — they predate the vault and are the authoritative reference for onboarding:

- `docs/00-START-HERE.md` — onboarding
- `docs/01-ARCHITECTURE.md` — module map
- `docs/02-COMPONENT-GUIDE.md` — how to build a component
- `docs/03-PATTERNS.md` — common recipes
- `docs/04-PUBLISHING.md` — publish flow
- `docs/05-TESTING.md` — testing conventions
- `docs/06-DECISIONS.md` — historical ADRs
- `COMPONENT_STANDARD.md` — gold-standard rules
- `SDK_STATUS.md` — per-module status
- `AUDIT_RESULTS.md` — last full audit

**Do not duplicate** content between those files and this vault. This vault is additive — new sessions, new decisions, new stamps.

## Related

- Source: [GitHub](https://github.com/RoyalTaxi/yalla-sdk)
- Parent meta-vault: `~/Ildam-Brain/projects/yalla-sdk`
- Primary consumer: [[../../YallaClient/docs/obsidian/README|YallaClient]]
