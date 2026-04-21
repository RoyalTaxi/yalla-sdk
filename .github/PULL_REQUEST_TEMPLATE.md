## Summary

<!-- One or two sentences: what does this PR do? -->

## Checklist

- [ ] `ktlintCheck` passes locally (`./gradlew ktlintCheck`)
- [ ] `detekt` passes locally (`./gradlew detekt`)
- [ ] `apiCheck` passes locally (`./gradlew apiCheck`) OR baselines updated with `apiDump` and included in this PR
- [ ] Tests cover the change (`./gradlew test allTests`)
- [ ] Docs updated if public surface changed
- [ ] Visual-verified via the live-verify loop (wire to YallaClient, launch emulator via mobile/android MCPs, pixel-accurate against design intent) for non-trivial UI changes
- [ ] CHANGELOG.md entry added under `[Unreleased]`

## Test plan

<!-- Specific manual steps (screens touched, platforms checked), if applicable. -->

## Screenshots / goldens

<!-- Before/after for visual changes, both light + dark. -->
