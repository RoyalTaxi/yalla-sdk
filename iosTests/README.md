# iosTests — iOS Visual Regression

Scaffolded in Phase 3 of the v1.0 launch (2026-04-22). Used from Phase 4 onward for
iOS UI snapshot regression on primitives and composites that don't fit the
Android-native Roborazzi harness.

## Running

```bash
cd iosTests
swift test
```

Or open `Package.swift` in Xcode and run the `YallaSnapshotScaffoldTests` scheme.

## Tooling

- [`pointfreeco/swift-snapshot-testing`](https://github.com/pointfreeco/swift-snapshot-testing) 1.19.2 — selected during Phase 1's Investigation C (see `docs/superpowers/plans/notes/2026-04-21-phase1-investigations.md`).
- Tolerance convention per Phase 1 decision: Android 0.1%, iOS 1% (wires in Phase 4).
- Not published to any Maven / Swift registry — internal test tooling only.

## Relationship to the SDK build

The SPM package has no runtime dependency on the KMP modules. It consumes compiled
`.klib` outputs via hand-built SwiftUI host apps (wiring lands in Phase 4). Current
scope is *scaffold only* — one sanity test proving the SPM dependency resolves.
