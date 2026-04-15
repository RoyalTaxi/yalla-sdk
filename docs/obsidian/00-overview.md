---
title: "Overview"
last_verified_sha: TBD
last_updated: 2026-04-15
last_author: claude
status: draft
tags: [overview]
---

# yalla-sdk — Overview

For the authoritative elevator pitch, architecture, and module breakdown, read [`docs/00-START-HERE.md`](../00-START-HERE.md). This page is a pointer, not a rewrite.

## Why This Vault Page Exists

To participate in the Obsidian vault graph. Cross-project references from YallaClient and yalla-sip-phone can link to this page as a known entry point.

## Quick Facts

- **Type**: Kotlin Multiplatform library monorepo
- **Modules**: bom, core, data, design, foundation, components, composites, platform, maps, media, firebase
- **Primary consumer**: [[../../YallaClient/docs/obsidian/README|YallaClient]]
- **Publishing**: GitHub Packages (`maven.pkg.github.com/RoyalTaxi/yalla-<name>`)
- **Version coordination**: `yalla-bom` tracks all module versions

## See Also

- [`docs/00-START-HERE.md`](../00-START-HERE.md) — onboarding
- [`docs/01-ARCHITECTURE.md`](../01-ARCHITECTURE.md) — module map
- [`docs/06-DECISIONS.md`](../06-DECISIONS.md) — historical ADRs
