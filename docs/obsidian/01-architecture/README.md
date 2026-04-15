---
title: "Architecture Index"
last_verified_sha: TBD
last_updated: 2026-04-15
last_author: claude
status: draft
tags: [architecture, index]
---

# Architecture (Vault Slice)

The authoritative architecture reference is [`docs/01-ARCHITECTURE.md`](../../01-ARCHITECTURE.md). This folder holds **stamped snapshots** of specific architectural aspects that may need to be versioned across time, and **cross-links** to other vault pages.

## When to Add a Page Here

- A module boundary shifted and the team needs to see the change documented with a SHA stamp
- A new abstraction is introduced that crosses multiple modules
- Binary compatibility guarantees change
- `expect`/`actual` surface expands

## Expected Pages

- `module-graph-snapshot.md` — graph at a given SHA (for history)
- `binary-compat-contract.md` — what guarantees we make to consumers
- `expect-actual-inventory.md` — list of all `expect` declarations and their `actual` implementations
