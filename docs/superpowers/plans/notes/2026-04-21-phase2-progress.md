# Phase 2 — Progress Notes

> Created: 2026-04-21. Branch: `feature/v1-phase2-core-data`. Base commit: `a56194d` (main, Phase 1 merge).

## Baseline (Task 0)

- `ktlintCheck`: PASS
- `detekt`: PASS
- `apiCheck`: PASS (covers Native + commonMain via BCV 0.18.1 Klib mode; androidMain-only gated by audit-api skill per ADR-009)
- `yalla.sdk.version`: `0.0.8-alpha04` (will bump to `0.0.9-alpha01` at Task 9 closeout, reflecting two breaking clusters: Either flip + createHttpClient scope)

Any regressions during Phase 2 must be measured against this baseline.

## Task Progress

### Task 1 — Either flip
- SDK commit: f695e1f
- Call-sites flipped (yalla-sdk): 20 (core/src/commonMain + core/src/commonTest + data/src/commonMain)
- Call-sites flipped (YallaClient): 139 across 39 files in 14 modules
- YallaClient scratch branch: chore/sdk-phase2-either-flip
- YallaClient PR: https://github.com/RoyalTaxi/YallaClient/pull/304
- apiCheck post-flip: green (baselines regenerated — Either generics flipped, three additive extensions)
- mavenLocal snapshot tag: 0.0.9-alpha01-phase2-either
- YallaClient :androidApp:assembleDebug: BUILD SUCCESSFUL
- Status: SDK-side done; YallaClient awaits yalla-sdk 0.0.9-alpha01 publish before merge.

