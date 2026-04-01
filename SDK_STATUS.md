# Yalla SDK — Status Dashboard

> **Bitta haqiqat manbai.** Bu faylga qarab SDK ning hozirgi holati, har bir modul, nima qilingan,
> nima qolgan — hammasini bilish mumkin. Har session shu fayldan boshlanadi.

**Version:** `0.0.7-alpha02`
**Last updated:** 2026-04-01 (major polish session: 187 files, +4191/-316 lines)

---

## Quick Decision Guide

**"Bu component SDK ga borishi kerakmi?"**
→ 2+ feature module ishlatsa — ha. 1 ta feature ishlatsa — yo'q, feature ichida qolsin.

**"Bu fix breaking change mi?"**
→ Public function signature o'zgarsa — ha. Internal logic o'zgarsa — yo'q.

**"Test yozish kerakmi?"**
→ Public API = albatta. Internal util = agar complex bo'lsa. UI render = Colors/Dimens equality.

**"Qachon version bump?"**
→ Har bir publish dan oldin. GitHub Packages immutable — bir xil version qayta publish bo'lmaydi.

---

## Module Status

### Legend
- DONE = Production-ready, tested, documented
- GOOD = Ishlaydi, minor polish kerak
- WIP = Active development
- TODO = Rejalashtirilgan, hali boshlanmagan

| Module | Files | Tests | Status | Notes |
|--------|-------|-------|--------|-------|
| **core** | 35 | 17 | DONE | Either, DataError, domain models, contracts |
| **data** | 20 | 7 | GOOD | SafeApiCall, preferences, HTTP factory |
| **design** | 7 | 3 | DONE | Color/Font tokens, YallaTheme |
| **foundation** | 20 | 5 | GOOD | BaseViewModel, location, locale, settings |
| **primitives** | 30 | 8 | GOOD | Buttons, fields, indicators, pins, topbar |
| **composites** | 45 | 28 | GOOD | Cards, items, sheets, snackbar, drawers |
| **platform** | 26 | 5 | GOOD | Native sheet/switch/nav, expect/actual |
| **maps** | 61 | 1 | GOOD | Google/Libre provider abstraction |
| **media** | 11 | 4 | GOOD | Camera, image picker, compression |
| **firebase** | 8 | 3 | GOOD | Analytics, crashlytics, messaging |
| **resources** | 69 | 0 | DONE | Icons, strings, drawables |

---

## What Was Done (Gold Standard Refactoring)

8 phase refactoring — **ALL COMPLETE** (March 2026):

| Phase | What | Result |
|-------|------|--------|
| 1-3 | Buttons | ButtonLayout infra, 8 buttons rewritten, 3 deleted |
| 4 | Fields | PrimaryField, DateField, PinRow, NumberField |
| 5 | Cards | ContentCard building block, 6 cards renamed |
| 6 | Sheets + Indicators + TopBars | SheetHeader, FormSheet, OtpSheet, SelectionSheet<T> |
| 7 | Items + Component Extraction | ListItem building block, 5 items renamed, 5 new SDK components |
| 8 | Naming + Cleanup | Final renames, unused deleted, MODULE.md updated |

---

## Known Issues (Prioritized)

### P0 — Fixed (2026-04-01)
- [x] SafeApiCall: SocketTimeoutException catch order (was after IOException)
- [x] SafeApiCallTest: Updated to expect correct Timeout behavior
- [x] UserPreferencesImpl: Manual payment parsing → PaymentKind.from()
- [x] Either: Added mapFailure() extension + tests
- [x] PreferenceKeysTest: Added missing ONBOARDING_STAGE key
- [x] IconType.Back → BACK (naming consistency) — YallaClient update on next SDK publish
- [x] MODULE.md (primitives): Fixed "State + Defaults" → "Colors + Dimens + Defaults"

### P0 — Fixed (2026-04-01, Maps module)
- [x] SwitchingMapProvider: Added close() to cancel leaked CoroutineScope
- [x] SwitchingMapController: Extracted hardcoded 5s timeout to constant, added isClosed flag
- [x] LibreMapController: Documented confusing dual marker sync flags
- [x] MapStyles: Documented intentionally hardcoded colors (no design module dependency)
- [x] MapController, MapConstants, GoogleMapController: Added Compose-level KDoc (threading, lifecycle, @param)

### P0 — Fixed (2026-04-01, Media module)
- [x] ImageCompressor.ios: Null safety on NSData→ByteArray conversion
- [x] ImageFilterHelper.android: Fixed unreachable code, documented ownership
- [x] SystemCameraLauncher.ios: Safe JPEG conversion (no force unwrap)
- [x] ImageHelper.android: Null check after BitmapFactory.decodeStream
- [x] ViewControllerHelper.ios: Extracted shared getRootViewController (dedup)
- [x] KDoc added to all touched public APIs

### P0 — Fixed (2026-04-01, KDoc full pass)
- [x] ALL public APIs across ALL 11 modules now have Compose/Kotlin-level KDoc
- [x] @param, @return, @since, @see on every public class, function, property
- [x] 187 files updated, +4191 lines of documentation
- [x] Maps test coverage: 1 → 9 test files (model, util, API model tests)

### P1 — Should Fix (Non-breaking)
- [ ] Media test coverage improvement (4 → target 10+)
- [ ] Maps iOS test linker issue (pre-existing, native SDK dependency)

### P2 — Design Decisions (Breaking API — Next Major Version)
- [ ] ListItem/items: title as String → @Composable slot
- [ ] ActionSheet/ConfirmationSheet: String params → slots
- [ ] EmptyState/Snackbar: State class unbundling
- [ ] SearchField/DateField: placeholder String → slot

> **P2 items are NOT bugs.** They are API design improvements for the next major
> version. The current API works correctly. Don't touch these until we're ready
> for a breaking release with full YallaClient migration.

### P3 — Nice to Have
- [ ] NumberField: internal focus state → FocusRequester
- [ ] LocationPin: hardcoded dp values → Dimens

---

## How to Work on SDK

### Adding a new component
1. Decide: primitives (building block) or composites (composed from primitives)?
2. Follow `COMPONENT_STANDARD.md` — parameter order, Colors/Dimens, Defaults
3. Write test (at minimum: Colors/Dimens equality)
4. Add to `MODULE.md` of the target module
5. Update this file

### Fixing a bug
1. Write test that reproduces → RED
2. Fix → GREEN
3. Update "Known Issues" section above
4. Bump version if publishing

### Publishing
1. Bump `yalla.sdk.version` in `gradle.properties`
2. Push to `main` → GitHub Actions publishes automatically
3. Update `yalla-sdk` version in YallaClient's `libs.versions.toml`

---

## Architecture Rules

```
core ← data ← foundation ← primitives ← composites
                ↑                            ↑
              design                      platform
```

- **core**: Domain models, Either, DataError, contracts. NO UI, NO Android/iOS.
- **data**: Network, preferences, DataStore. Depends only on core.
- **design**: Color tokens, font tokens, YallaTheme. NO components.
- **foundation**: BaseViewModel, location, locale. Bridge between data and UI.
- **primitives**: Building block composables. NO composition from other components.
- **composites**: Composed from primitives. Cards, sheets, items, snackbar.
- **platform**: expect/actual for native UI (sheets, switches, navigation).
- **maps**: Google/Libre map abstraction. Provider-agnostic API.
- **media**: Camera, gallery, image compression. Platform-specific.
- **firebase**: Analytics, crashlytics, messaging wrapper.
- **resources**: Compose Resources — icons, strings, drawables.

### Key Standards
- **COMPONENT_STANDARD.md** — How to build components (mandatory reading)
- **AUDIT_RESULTS.md** — Component usage audit across YallaClient
- **MODULE.md** (per module) — What's inside each module

---

## "Qachon bo'ldi?"

SDK is **done** when:
1. All P0 issues fixed — **DONE**
2. All P1 issues fixed — 2 items left
3. Every module has MODULE.md — **DONE** (10/10)
4. COMPONENT_STANDARD.md exists — **DONE**
5. YallaClient compiles with current SDK — **DONE**
6. Both platforms (iOS + Android) build — **DONE**

SDK is **production-ready** when:
1. Above + P2 items addressed (next major version)
2. Maps/media test coverage improved
3. Beta version stamp (Islom's call)

**Current verdict: SDK is GOOD. Not perfect, but solid and maintainable.**
