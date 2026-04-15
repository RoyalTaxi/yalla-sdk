---
title: "Modules Index"
last_verified_sha: TBD
last_updated: 2026-04-15
last_author: claude
status: draft
tags: [modules, index]
---

# Modules

> In the other projects this folder is called "features." In a library monorepo, the equivalent unit is a **module**. One page per module explains its public API, consumers, and internal structure.

Authoritative per-module status: [`SDK_STATUS.md`](../../../SDK_STATUS.md).

## Modules in yalla-sdk

- `bom/` — Bill of Materials (version coordination)
- `core/` — Domain models, contracts, utils (`uz.yalla.core.*`)
- `data/` — SafeApiCall, mappers, remote models (`uz.yalla.data.*`)
- `foundation/` — Low-level primitives
- `design/` — Design tokens, colors, typography
- `components/` — UI primitives
- `composites/` — Higher-level UI compositions
- `platform/` — Platform-native helpers
- `maps/` — Maps abstraction
- `media/` — Camera, gallery
- `firebase/` — Firebase wrappers

## Expected Pages

One per module:
- `core.md`, `data.md`, `components.md`, `design.md`, …

Each page:
- Public API entry points (with KDoc snippet)
- Internal structure
- Dependencies (what it pulls in)
- Consumers (who depends on it)
- Current version + last published SHA
