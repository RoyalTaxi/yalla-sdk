# FOUNDATION_AUDIT.md

Audit output for wave 1 of phase-3 `foundation` cleanup. Drives waves 2-10. Findings keyed to `CLEANUP_CRITERIA.md`. All paths absolute under `/Users/islom/StudioProjects/yalla-sdk/`.

---

## 1. AI-blob deletions (criterion 2)

### `foundation/MODULE.md`

- **2-1** lines 1-25 — entire file. Both the "Architecture" framing paragraph (lines 6-12: "Foundation serves as the intentional glue layer between `core` and UI modules…") and the four `# Package` blurbs (lines 14-25) belong to the pre-phase-1 form. Same precedent as `core/MODULE.md` and `data/MODULE.md`. (~25 min — full rewrite, see section 7.)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/animation/StaggerReveal.kt`

- **2-1** lines 14-46 — the class-level KDoc carries info (the "always composed and measured… visually hidden until [visible] becomes true" paragraph and the `graphicsLayer`-zero-recomposition rationale). The `@param`/`@return`/`@see graphicsLayer` ceremony block (lines 38-46) paraphrases the signature. Trim the `@param`/`@return`/`@see` block. (~3 min)
- **2-1** line 44 — `@since 0.0.1` ceremony tag. (~30 sec)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/BaseViewModel.kt`

- **2-1** lines 18-46 — class-level KDoc. The "Features" bullet list (21-25) is a paraphrase of the public surface. The Usage block (27-42) carries info. Drop the bullet list, keep Usage and the `@see LoadingController` line. (~3 min)
- **2-1** line 45 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 50, 55, 60, 70-76, 79-84, 91-96, 103, 109-118, 127-132, 138-146, 149-157 — every member has `@since 0.0.1` ceremony plus `@param`/`@return` paraphrase. The `safeScope` paragraph at lines 70-76 ("Exceptions thrown in this scope are caught and mapped to user-friendly messages") carries info; keep that. The `launchWithLoading` and `launchSafe` `@param block` paraphrase ("Suspending operation to execute") doesn't. The `@see LoadingController` cross-refs are info-dense; keep. (~10 min)
- **2-1** lines 75, 83, 95, 103, 117, 131, 145, 156 — eight `@since 0.0.1` tags inline. (~3 min)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/LoadingController.kt`

- **2-1** lines 16-44 — class-level KDoc. The "Features" bullet list (19-23) paraphrases the public surface. The Usage block (25-40) carries info. The `@param showAfter`/`@param minDisplayTime` block at 42-43 restates the constructor. (~3 min)
- **2-1** line 44 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 52, 61-71, 122-126 — per-member ceremony. The `withLoading` `@param T`/`@param showAfter`/etc. at 65-70 paraphrases. The companion-object `DEFAULT_SHOW_AFTER`/`DEFAULT_MIN_DISPLAY_TIME` one-liners at 122-126 just restate the const name. (~3 min)
- **2-1** lines 71, 123, 126 — three `@since 0.0.1` tags. (~1 min)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/ObserveAsEvents.kt`

- **2-1** lines 10-36 — first overload class-level KDoc. The "default overload" paragraph and Usage block (10-29) carry info. The `@param T`/`@param flow`/`@param key`/`@param onEvent`/`@see` block (30-35) paraphrases the signature; the `@see` line is the single-info-density keeper. (~3 min)
- **2-1** line 35 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 54-72 — second overload class-level KDoc. The "Use this overload when the default `STARTED` threshold is not appropriate" paragraph (54-62) carries info. The `@param`/`@see` block (64-71) paraphrases. (~3 min)
- **2-1** line 71 — `@since 0.0.1`. (~30 sec)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/DataErrorMapper.kt`

- **2-1** lines 6-13 — class-level KDoc paraphrases ("Maps `DataError` to user-friendly `StringResource` messages"). The `@see DefaultDataErrorMapper` line is borderline-info. (~1 min)
- **2-1** line 12 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 15-21 — `map()` KDoc. `@param error The data error to map` / `@return Localized message resource for the error` is signature paraphrase. (~1 min)
- **2-1** line 20 — `@since 0.0.1`. (~30 sec)
- **2-3** entire file — `DataErrorMapper` is an interface with one implementation (`DefaultDataErrorMapper`), no override anywhere SDK-wide or in YallaClient. Verified: `grep -rn "DataErrorMapper\|DefaultDataErrorMapper"` against the entire SDK + YallaClient returns only foundation/src declarations. The "Implement this interface to customize error message mapping per app or feature" KDoc claim has zero callers. **Single-use abstraction kept "for testability"** — the `BaseViewModel` constructor takes `DataErrorMapper = DefaultDataErrorMapper()` as the seam, but `BaseViewModelTest.kt` only ever uses the default. **Recommend deleting `DataErrorMapper` interface and inlining `DefaultDataErrorMapper`'s `map()` body into `BaseViewModel.mapDataErrorToUserMessage`** (which is already `protected open` for override-time customization — that's the actual extension seam). (~15 min — see section 4 for the rewrite framing.)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/DefaultDataErrorMapper.kt`

- **2-1** lines 13-19 — class-level KDoc paraphrases ("Default implementation mapping `DataError` subtypes to localized string resources. Maps each error category to its corresponding user-facing message"). (~1 min)
- **2-1** line 18 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 21-27 — `map()` override KDoc. Standard `@param`/`@return` paraphrase. (~1 min)
- **2-1** line 26 — `@since 0.0.1`. (~30 sec)
- Cross-check: post-wave-2 `DataError.Network.*`-only simplification (commit `482eb16df`) is reflected — the `when` covers exactly the eight surviving Network variants (lines 30-37). Shape is correct.
- **2-3** rolls into the bucket-2-3 finding above; if `DataErrorMapper` interface is deleted, this concrete class collapses into a top-level function or moves into `BaseViewModel` as a `protected open` default.

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/ChangeLanguage.kt`

- **2-1** lines 3-10 — class-level KDoc on `expect fun changeLanguage`. "Implement actual functions in androidMain and iosMain" is paraphrase of the `expect` keyword. The `@param languageCode ISO 639-1 language code` line is info-dense. (~1 min)
- **2-1** line 9 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 13-17 — class-level KDoc on `expect fun getCurrentLanguage`. The `@return ISO 639-1 language code (e.g., "en", "uz", "ru")` is info-dense. The framing sentence "Gets the current system language code" paraphrases the function name. (~1 min)
- **2-1** line 17 — `@since 0.0.1`. (~30 sec)
- The `expect` shape itself is fine — both functions return primitives (`Unit`/`String`); no platform-type leakage into commonMain.

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/LocaleProvider.kt`

- **2-1** lines 8-13 — `LocaleState` data class KDoc. `@property language Currently selected language` / `@property onLanguageChange Callback when language should change` is full property paraphrase. (~1 min)
- **2-1** line 13 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 20-23 — `LocalLocaleState` KDoc. "CompositionLocal for locale state" is exact paraphrase of the val name. (~1 min)
- **2-1** line 23 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 30-35 — `LocaleProvider` `@Composable` KDoc. `@param state Current locale state` / `@param content Child composables` paraphrase. (~1 min)
- **2-1** line 35 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 48-55 — `currentLocaleState()` KDoc. The `@throws IllegalStateException` line is info-dense. The framing sentence paraphrases. (~1 min)
- **2-1** line 53 — `@since 0.0.1`. (~30 sec)
- **2-4** entire file — verified: `grep -rn "LocaleProvider\|LocaleState\|LocalLocaleState\|currentLocaleState"` against SDK + YallaClient finds zero foundation-specific consumers. YallaClient defines its own `LocaleProvider`/`LocaleState` (`composeApp/.../locale/LocaleProvider.kt`) and uses that instead. **Foundation's `LocaleProvider` + `LocaleState` + `LocalLocaleState` + `currentLocaleState()` are dead public surface** (~57 lines). **Bucket 2-4 + bucket 2-3** (a CompositionLocal pattern with no consumers is a single-use abstraction with zero use). Recommend **delete the file**, `currentLocale()` from `ObserveLocale.kt` is the only locale Composable that actually gets consumed (verified via `currentLocale\(\)` grep — also zero direct hits in YallaClient, but it's a candidate that ought to surface in a `LocaleProvider` consumer first). (~10 min decision — flag for Islom.)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/ObserveLocale.kt`

- **2-1** lines 11-22 — `currentLocale()` `@Composable` KDoc. The "Note" paragraph (lines 14-17, "Uses `LaunchedEffect(Unit)` internally — the value is read once at composition and does NOT update if the locale changes during the composition lifecycle. For reactive locale observation, use `LocaleProvider` / `currentLocaleState` instead") is information-dense. The framing line + `@return` line are paraphrase. (~1 min)
- **2-1** line 19 — `@since 0.0.1`. (~30 sec)
- **2-4** rolls into the same bucket-2-4 finding as `LocaleProvider.kt`: zero consumers in YallaClient (`grep -rn "currentLocale"` against YallaClient finds nothing). The file's class KDoc points consumers at `LocaleProvider` for reactive use, but no one wired up either path. **Flag for Islom** (same gate as `LocaleProvider.kt`): delete both files together, since they're the same dead-locale-Composable surface.

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/Location.kt`

- **2-1** lines 6-12 — `Location` data class KDoc. `@property id Optional location identifier` / `@property name Optional human-readable location name` / `@property point Optional geographic coordinates` is full property paraphrase. (~1 min)
- **2-1** line 12 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 20-28 — `FoundLocation` data class KDoc. Same shape — five `@property` lines paraphrasing the field names. (~1 min)
- **2-1** line 28 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 36-43 — `toLocation()` extension KDoc. `@return Location with id, name, and point carried over` paraphrases the body. (~1 min)
- **2-1** line 41 — `@since 0.0.1`. (~30 sec)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationMappers.kt`

- **2-1** lines 9-14, 25-30, 41-47, 56-64, 73-78 — five mapper-function KDoc blocks. Each `@return` line paraphrases the body (`@return FoundLocation populated from this address option's fields`). The `@see` cross-refs add no info beyond what the function signature already shows. (~5 min)
- **2-1** lines 14, 30, 47, 63, 77 — five `@since 0.0.1` tags. (~2 min)
- The mapper functions themselves are sound — domain↔domain coercions (`Address`/`AddressOption`/`SavedAddress`/`Order.Taxi.Route` from `core/` to foundation's `Location`/`FoundLocation`). **Not DTO mappers** — see section 4 for the architecture-violation discussion.

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationManager.kt`

- **2-1** lines 16-46 — class-level KDoc. The "Scope ownership (ADR-013)" paragraph (21-28) is information-dense and load-bearing. The Usage block (29-40) carries info. The `@param locationTracker` / `@param scope` / `@param defaultLocation` block (42-44) restates the constructor signature. (~3 min)
- **2-1** line 45 — `@since 0.0.10`. (~30 sec)
- **2-1** lines 55-60, 63-67, 72-77, 82-87, 89-95, 123-127, 140-145, 150-154, 157-162, 164-170 — every public member has either an info-dense paragraph (the `extendedLocation` "or `null` if tracking is off" sentence at 56-60, the `isTracking` "between `startTracking` and `stopTracking` or scope cancellation" sentence at 73-76) or pure paraphrase (`updatePermissionState` `@param state New permission state, or null if unknown`). Sweep the obvious paraphrase, keep the info-dense bits. (~5 min)
- **2-1** lines 59, 66, 76, 86, 94, 126, 144, 153, 161, 168 — ten `@since 0.0.1` ceremony tags. (~3 min)
- The "ADR-013" reference at line 21 — same posture as `core/error/DataError.kt:28-31`'s ADR-022 reference and `design/motion/MotionScheme.kt:159-166`'s ADR-021 reference. ADRs were removed (`docs/adr/` is gone per criterion 5 / CLAUDE.md). Stale meta-commentary. **Recommend keep the substantive paragraph** (the scope-ownership rationale is real product knowledge), **drop the "(ADR-013)" parenthetical**. (~30 sec)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationProvider.kt`

- **2-1** lines 7-13 — `LocalLocationManager` KDoc. "CompositionLocal providing access to `LocationManager`. Must be provided at a parent composable level for child components to access" — paraphrase of the val name + the `staticCompositionLocalOf` boilerplate body. (~1 min)
- **2-1** line 13 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 19-32 — `LocationProvider` `@Composable` KDoc. The Usage block carries info (it's the only place documenting how to access `LocalLocationManager.current`). The `@param`/`@return` block (29-31) paraphrases. (~1 min)
- **2-1** line 33 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 46-53 — `currentLocationManager()` KDoc. `@throws IllegalStateException If called outside a LocationProvider` is info-dense. The framing line + `@see` is paraphrase/cross-ref. (~1 min)
- **2-1** line 51 — `@since 0.0.1`. (~30 sec)
- **2-4** entire file — verified: `grep -rn "LocalLocationManager\|currentLocationManager\|uz\.yalla\.foundation\.location\.LocationProvider"` against SDK + YallaClient finds zero hits. YallaClient defines its own `LocationProvider` (`composeApp/.../location/LocationProvider.kt`) — same naming-collision pattern as locale. **Foundation's CompositionLocal-based `LocationProvider` is dead public surface** (~55 lines). The actual consumer surface is the `LocationManager` class, injected via Koin (verified — `LocationDiModule.kt` and ~20 ViewModels in YallaClient inject `LocationManager` directly, not via the CompositionLocal). **Bucket 2-4** (dead) **and bucket 2-3** (CompositionLocal pattern abandoned in favor of Koin injection — zero use). Recommend **delete the file**. (~10 min — see section 5 for the related foundation/core `LocationProvider` *interface* discussion.)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationServices.kt`

- **2-1** lines 3-14 — `expect fun isLocationServicesEnabled` KDoc. The platform-routing bullet list (lines 9-10, "Android: Checks GPS_PROVIDER…") is info-dense — both actuals carry the same info redundantly, but having it once at the `expect` site is fine. The "This is independent of app-level permission" paragraph (5-7) is the real keeper. The `@return` line paraphrases. (~1 min)
- **2-1** line 13 — `@since 0.0.8`. (~30 sec)
- **2-1** lines 17-28 — `expect fun openLocationSettings` KDoc. "Use when location permission is granted but location services are disabled" (19-21) is info-dense; the platform-routing block (23-26) is info-dense (the "iOS does not allow deep-linking" caveat is load-bearing). Trim only the framing sentence. (~1 min)
- **2-1** line 27 — `@since 0.0.8`. (~30 sec)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationState.kt`

- **2-1** lines 6-15 — `ExtendedLocation` data class KDoc. Seven `@property` lines paraphrasing the field names. (~2 min)
- **2-1** line 15 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 26-32 — `toGeoPoint()` KDoc. `@return GeoPoint with this location's coordinates` paraphrases the body. (~1 min)
- **2-1** line 30 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 36-39 — `LocationPermissionState` enum KDoc paraphrases the enum name. (~30 sec)
- **2-1** line 39 — `@since 0.0.1`. (~30 sec)
- **2-2** lines 42-47, 49-54, 56-61, 63-68 — every enum entry has a one-liner KDoc that restates the entry name (`/** Permission not yet requested. */` above `NOT_DETERMINED`, `/** Permission granted. */` above `GRANTED`, etc.). 4 entries, ~12 lines of comment redundancy. (~2 min)
- **2-1** lines 46, 53, 60, 67 — four `@since 0.0.1` tags inline. (~1 min)
- **2-4** `ExtendedLocation` — verified: `grep -rn "ExtendedLocation\|extendedLocation"` against SDK + YallaClient finds **zero consumers outside foundation**. The full data class is used only as the internal state inside `LocationManager.kt` (lines 53, 61, 68, 106-114, 155, 162). The `extendedLocation: StateFlow<ExtendedLocation?>` public surface and the `toGeoPoint()` extension are unconsumed. **Demote `ExtendedLocation` to `internal`** (sub-100-line, behavior-preserving since the StateFlow's only use is internal-to-LocationManager). The `LocationPermissionState` enum *is* consumed (`HomeViewModel+Intent.kt`, `MapSheetViewModel+Observe.kt`); keep public. (~15 min — refactor the public `extendedLocation: StateFlow` away from `LocationManager`.)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationTrackerFactory.kt`

- **2-1** lines 5-13 — `expect fun createLocationTracker` KDoc. The platform-routing bullet list (lines 7-8, "Android: Uses Koin-injected application Context to create PermissionsController. iOS: Creates PermissionsController directly (no DI needed)") is info-dense. The framing sentence paraphrases the function name. (~1 min)
- **2-1** line 13 — `@since 0.0.1`. (~30 sec)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/settings/Selectable.kt`

- **2-1** lines 10-19 — `Selectable` interface KDoc. The body (10-13, "Common contract for settings options displayed in selection lists. Implemented by sealed hierarchies (`ThemeOption`, `LanguageOption`, `MapOption`)") is info-dense — it's the only place documenting that the three sealed hierarchies share this contract. The `@property name`/`@property icon` block (16-17) paraphrases. (~1 min)
- **2-1** line 18 — `@since 0.0.1`. (~30 sec)
- **2-1** lines 25-36 — `toSelectableItem()` extension KDoc. The body explains the iconColor caveat (lines 31-32, "Use Color.Unspecified for multi-color icons (e.g. flags)") which is info-dense. The framing + `@param T`/`@return` block paraphrases. (~1 min)
- **2-1** line 34 — `@since 0.0.1`. (~30 sec)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/settings/OptionModel.kt`

- **2-1** lines 6-14 — `OptionModel<T>` data class KDoc. Four `@property` lines paraphrasing the field names. The `@param T Type of the underlying data item` line restates the generic. (~1 min)
- **2-1** line 14 — `@since 0.0.1`. (~30 sec)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/settings/ThemeOption.kt`

- **2-1** lines 15-22 — class-level KDoc. The body (16-19, "Sealed hierarchy mapping `ThemeKind` to display properties") is info-dense — it documents the ThemeKind ↔ ThemeOption coupling. The `@property kind` block paraphrases. (~1 min)
- **2-1** line 21 — `@since 0.0.1`. (~30 sec)
- **2-2** lines 29, 36, 43 — three per-`data object` one-liner KDocs (`/** Light theme option — always uses light color scheme. @since 0.0.1 */`). The `@since` is ceremony; the rest paraphrases the entry name. The "always uses light color scheme" qualifier is borderline-info (it disambiguates from `System` which follows OS), but the `Light`/`Dark`/`System` names are already self-describing. Sweep. (~1 min)
- **2-1** lines 51, 54-60 — companion object KDoc. The `from()` `@param`/`@return` block paraphrases. (~1 min)
- **2-1** lines 51, 59 — two `@since 0.0.1` tags. (~1 min)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/settings/LanguageOption.kt`

- **2-1** lines 13-21 — class-level KDoc. The body (14-18, "Sealed hierarchy mapping `LocaleKind` to picker display properties. **Narrowed in Phase 3 (ADR-014) to the production-ready locales: `Uzbek` and `Russian`**") is information-dense — the ADR-014-narrowing note is real product history. **The "(ADR-014)" parenthetical is stale meta-commentary** (same as `LocationManager.kt`'s ADR-013 and `core/error/DataError.kt`'s ADR-022). Recommend keep the substantive content, drop the parenthetical. (~30 sec)
- **2-1** line 20 — `@since 0.0.1`. (~30 sec)
- **2-2** lines 28, 35 — two per-`data object` one-liner KDocs (`/** Uzbek (Latin script). @since 0.0.1 */`). (~30 sec)
- **2-1** lines 42-56 — companion object KDoc + `from()` `@param`/`@return` block paraphrases. The `@return The corresponding LanguageOption — exhaustive over current LocaleKind cases` is info-dense (documents the exhaustive intent of the `when`). Keep the info-dense sentence. (~1 min)
- **2-1** line 51 — `@since 0.0.1`. (~30 sec)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/settings/MapOption.kt`

- **2-1** lines 9-17 — class-level KDoc. "Has no icon — only a localized name" is info-dense (disambiguates from `ThemeOption`/`LanguageOption` which have icons). The framing sentence + `@property kind` paraphrases. (~1 min)
- **2-1** line 16 — `@since 0.0.1`. (~30 sec)
- **2-2** lines 23, 29 — two per-`data object` one-liners. (~30 sec)
- **2-1** lines 36, 39-45 — companion object KDoc. `@param kind`/`@return` paraphrase. (~1 min)
- **2-1** lines 36, 44 — two `@since 0.0.1` tags. (~1 min)

### Platform actuals — paraphrase + `@since` ceremony

- `foundation/src/androidMain/kotlin/uz/yalla/foundation/locale/ChangeLanguage.android.kt`:
  - **2-1** lines 5-14, 20-25 — both `actual fun` KDoc blocks. The "Limitation: Only calls `Locale.setDefault`. For full in-app language change, the Activity must be recreated or AppCompatDelegate.setApplicationLocales used. This function alone does NOT update visible UI" paragraph (7-9) is info-dense and load-bearing — keep that. The framing sentences + `@param`/`@return` blocks paraphrase. (~2 min)
  - **2-1** lines 13, 25 — two `@since 0.0.1` tags. (~1 min)
- `foundation/src/androidMain/kotlin/uz/yalla/foundation/location/LocationServices.android.kt`:
  - **2-1** lines 18, 26 — two `/** @see <expect-fn> */` one-liners. Pure paraphrase via `@see` cross-ref. (~30 sec)
- `foundation/src/androidMain/kotlin/uz/yalla/foundation/location/LocationTrackerFactory.android.kt`:
  - **2-1** line 8 — same `/** @see createLocationTracker */` one-liner. (~30 sec)
- `foundation/src/iosMain/kotlin/uz/yalla/foundation/locale/ChangeLanguage.ios.kt`:
  - **2-1** lines 8-15, 23-28 — both `actual fun` KDoc blocks. "Sets the AppleLanguages user default. App restart may be required" (10-11) is info-dense — keep. The rest paraphrases. (~1 min)
  - **2-1** lines 14, 27 — two `@since 0.0.1` tags. (~1 min)
- `foundation/src/iosMain/kotlin/uz/yalla/foundation/location/LocationServices.ios.kt`:
  - **2-1** lines 8, 12-18 — two KDoc blocks. The "iOS does not allow deep-linking to the system Location Services page, so this opens the app's settings page via `UIApplicationOpenSettingsURLString`" paragraph (14-16) is info-dense — keep. The framing `@see` line and the modern-API parenthetical (17, "Uses the modern `open(url:options:completionHandler:)` API (iOS 10+)") are info-dense. (~30 sec)
- `foundation/src/iosMain/kotlin/uz/yalla/foundation/location/LocationTrackerFactory.ios.kt`:
  - **2-1** line 6 — `/** @see createLocationTracker */` one-liner. (~30 sec)

### Cross-cutting bucket counts (foundation only)

- **2-1 (paraphrase / ceremony):** ~26 source files affected; **85 `@since` tags** total across all source sets. ~150-180 lines of paraphrase across ViewModel/loading/event/locale/location/settings classes and the platform-actual one-liners. Wave-2 sweep, **~50 min total**.
- **2-2 (comment redundancy):** 4 `LocationPermissionState` enum-entry one-liners + 7 settings `data object` one-liners + 0 platform-actual one-liners (covered under 2-1's `@see` form). ~15 lines total. (~5 min)
- **2-3 (single-use abstractions):** **two findings, both flagged for Islom.**
  1. **`DataErrorMapper` interface** (`foundation/.../infra/DataErrorMapper.kt`) + its sole impl `DefaultDataErrorMapper` — zero overrides anywhere SDK-wide or in YallaClient. The `BaseViewModel` constructor seam is unused. Inline the body into `BaseViewModel` and delete the interface (or keep `DefaultDataErrorMapper` as a top-level function and delete the interface). (~15 min)
  2. **`foundation.location.LocationProvider`** Composable wrapper (`foundation/.../location/LocationProvider.kt`) — the CompositionLocal pattern was abandoned in favor of Koin DI everywhere. (~10 min)
- **2-4 (dead code):** **three findings, all flagged for Islom.**
  1. **`foundation.locale.LocaleProvider` + `LocaleState` + `LocalLocaleState` + `currentLocaleState()`** (`foundation/.../locale/LocaleProvider.kt`) — zero consumers; YallaClient defines its own. ~57 lines.
  2. **`foundation.locale.currentLocale()`** (`foundation/.../locale/ObserveLocale.kt`) — zero consumers; YallaClient uses `getCurrentLanguage()` (the underlying `expect`) directly. ~25 lines.
  3. **`foundation.location.LocationProvider` + `LocalLocationManager` + `currentLocationManager()`** (`foundation/.../location/LocationProvider.kt`) — zero consumers; Koin injection is the actual seam. ~55 lines.
  4. **`ExtendedLocation` public surface** (`foundation/.../location/LocationState.kt:17-34`) — used internally inside `LocationManager` only. Public exposure unused. **Demote to `internal`** rather than delete. ~17 lines.
- **2-5 (speculative generalization):** the `BaseViewModel(dataErrorMapper: DataErrorMapper = DefaultDataErrorMapper())` constructor parameter is a configuration knob with one default value, ever. Rolls into bucket 2-3 above. (~0 min — same fix as 2-3.)

---

## 2. Module dependency graph (criterion 4)

`foundation/build.gradle.kts` declarations (17 total):

| line | declaration | scope | libs key |
| ---- | ----------- | ----- | -------- |
| 9 | `api(projects.core)` | commonMain | `core` (SDK-internal) |
| 10 | `implementation(projects.design)` | commonMain | `design` (SDK-internal) |
| 11 | `implementation(projects.resources)` | commonMain | `resources` (SDK-internal) |
| 14 | `implementation(compose.runtime)` | commonMain | `compose-runtime` |
| 15 | `implementation(compose.foundation)` | commonMain | `compose-foundation` |
| 16 | `implementation(compose.material3)` | commonMain | `compose-material3` |
| 17 | `implementation(compose.components.resources)` | commonMain | `compose-components-resources` |
| 20 | `implementation(libs.koin.core)` | commonMain | `koin-core` |
| 21 | `implementation(libs.koin.compose.viewmodel)` | commonMain | `koin-compose-viewmodel` |
| 24 | `implementation(libs.orbit.core)` | commonMain | `orbit-core` |
| 25 | `implementation(libs.orbit.viewmodel)` | commonMain | `orbit-viewmodel` |
| 26 | `implementation(libs.orbit.compose)` | commonMain | `orbit-compose` |
| 29 | `implementation(libs.kotlinx.coroutines.core)` | commonMain | `kotlinx-coroutines-core` |
| 30 | `implementation(libs.kotlinx.serialization.json)` | commonMain | `kotlinx-serialization-json` |
| 33 | `implementation(libs.kermit)` | commonMain | `kermit` |
| 36 | `implementation(libs.androidx.lifecycle.viewmodel.compose)` | commonMain | `androidx-lifecycle-viewmodel-compose` |
| 37 | `implementation(libs.androidx.lifecycle.runtime.compose)` | commonMain | `androidx-lifecycle-runtime-compose` |
| 40 | `implementation(libs.geo)` | commonMain | `moko-geo` |
| 41 | `implementation(libs.geo.compose)` | commonMain | `moko-geo-compose` |
| 44 | `implementation(libs.connectivity.device)` | commonMain | `connectivity-device` |
| 48 | `implementation(libs.kotlinx.coroutines.test)` (test) | commonTest | — |
| 49 | `implementation(libs.turbine)` (test) | commonTest | — |
| 53 | `implementation(libs.androidx.core.ktx)` | androidMain | `androidx-core-ktx` |
| 54 | `implementation(libs.koin.android)` | androidMain | `koin-android` |

### Verification grep results — `api()` exposure check

Only one `api()` declaration:

- **`projects.core`** (line 9) — `core` types in public signatures: `DataError` (`BaseViewModel.kt:97, 147`; `DataErrorMapper.kt:22`; `DefaultDataErrorMapper.kt:28`), `LocaleKind` (`LocaleProvider.kt:16, 17`; `LanguageOption.kt:25, 53`), `MapKind` (`MapOption.kt:20, 46`), `ThemeKind` (`ThemeOption.kt:26, 61`), `GeoPoint` (`Location.kt:17, 34`; `LocationState.kt:33`; `LocationManager.kt:50, 68, 155, 162, 170`), `Address`/`AddressOption`/`SavedAddress`/`Order.Taxi.Route`/`PlaceKind` (mappers + Location), `LocationProvider` (the *core* interface — `LocationManager.kt:13, 51`). **Keep `api`.**

### Verification grep results — `implementation()` `api`-promotion candidates

Per the prompt's checklist, four candidates need verification:

- **`compose.runtime`** (line 14) — used in `BaseViewModel.kt`, `Selectable.kt`, `ObserveLocale.kt`, `LocaleProvider.kt`, `LocationProvider.kt`, `StaggerReveal.kt`, `ObserveAsEvents.kt`. `@Composable` annotation appears on the public `currentLocale()`, `LocaleProvider()`, `currentLocaleState()`, `LocationProvider()`, `currentLocationManager()`, `Modifier.staggerReveal()`, `ObserveAsEvents()` (both overloads), and `T.toSelectableItem()`. `staticCompositionLocalOf<T>` returns `ProvidableCompositionLocal<T>` — the type of public `LocalLocaleState`, `LocalLocationManager`. **`compose.runtime` types saturate the public API.** Should be `api`, not `implementation`. **Promote to `api`.** Note: same posture as `design` G13 finding. (~5 min)
- **`androidx.lifecycle.viewmodel.compose`** (line 36) — verified: `grep -rn "import androidx\.lifecycle\.viewmodel\." foundation/src` returns **zero matches**. The lib is declared but never imported. **Drop the dep entirely.** (~2 min)
- **`androidx.lifecycle.runtime.compose`** (line 37) — used: `androidx.lifecycle.compose.LocalLifecycleOwner` in `ObserveAsEvents.kt:6`, plus `androidx.lifecycle.Lifecycle` and `repeatOnLifecycle` (the `androidx-lifecycle-runtime-compose` artifact bundles these). `Lifecycle.State` is in the public signature of the second `ObserveAsEvents` overload (`ObserveAsEvents.kt:76`). **Public type. Should be `api`.** **Promote to `api`.** (~5 min)
- **`orbit.viewmodel`** (line 25) — verified: `grep -rn "ContainerHost\|orbit\." foundation/src` returns **zero matches**. The lib is declared but never imported. `BaseViewModel` extends `androidx.lifecycle.ViewModel` (`BaseViewModel.kt:3, 47`), **not** `ContainerHost<S, E>`. `BaseViewModel` is a **custom MVI base class**, not Orbit. **Drop the dep entirely.** (~2 min — see section 4 for the architecture discussion.)

The prompt also asked about `geo`/`geo.compose`:

- **`libs.geo`** (line 40) — `dev.icerock.moko.geo.LocationTracker` is a constructor parameter on `LocationManager` (`LocationManager.kt:48`) and the return type of `expect fun createLocationTracker()` (`LocationTrackerFactory.kt:14`). Both **public type signatures**. Should be `api`, not `implementation`. **Promote to `api`.** (~5 min)
- **`libs.geo.compose`** (line 41) — verified: `grep -rn "import dev\.icerock\.moko\.geo\.compose\." foundation/src` returns **zero matches**. Declared but never imported. **Drop the dep entirely.** (~2 min)

Plus `androidx.lifecycle.ViewModel`:

- The `androidx.lifecycle.viewmodel` artifact is **not** declared explicitly — it transitively comes through `androidx.lifecycle.viewmodel.compose` (which is unused and being dropped). After the drop, foundation will need `androidx.lifecycle.viewmodel` declared explicitly. `BaseViewModel : ViewModel` makes `ViewModel` a public supertype. **Add `api(libs.androidx.lifecycle.viewmodel)` after the `viewmodel.compose` drop.** Verify the `version-catalog` (`gradle/libs.versions.toml`) has the bare `androidx-lifecycle-viewmodel` alias defined; if not, add it. (~5 min)

### Verification grep results — `implementation()` usage check

For each remaining `implementation()` dep, verified actual usage.

- **`projects.design`** (line 10) — verified: `grep -rn "import uz\.yalla\.design" foundation/src` returns **zero matches**. Declared but never imported. **Drop the dep entirely.** (~2 min)
- **`projects.resources`** (line 11) — used: `uz.yalla.resources.Res`, `uz.yalla.resources.error_*`, `uz.yalla.resources.icons.*`, `uz.yalla.resources.language_*`, `uz.yalla.resources.settings_*` across `BaseViewModel.kt`, `DefaultDataErrorMapper.kt`, `ThemeOption.kt`, `LanguageOption.kt`, `MapOption.kt`. `StringResource` is the type of `LanguageOption.name`, `MapOption.name`, `ThemeOption.name`, `Selectable.name`, `currentErrorMessageId: StateFlow<StringResource?>`, `mapDataErrorToUserMessage`/`mapThrowableToUserMessage` return type. **Public types.** **Promote `projects.resources` to `api`** (since `StringResource` and the `Res.*` accessor surface bleed into public signatures). The same case as `design`'s `compose.components.resources` discussion. (~5 min)
- **`compose.foundation`** (line 15) — verified: `grep -rn "import androidx\.compose\.foundation\." foundation/src` returns **zero matches**. Declared but never imported. **Drop the dep entirely.** (~2 min)
- **`compose.material3`** (line 16) — verified: `grep -rn "import androidx\.compose\.material3\.\|MaterialTheme" foundation/src` returns **zero matches**. Declared but never imported. **Drop the dep entirely.** (~2 min)
- **`compose.components.resources`** (line 17) — used: `org.jetbrains.compose.resources.StringResource` and `stringResource()` in `BaseViewModel.kt`, `Selectable.kt`, `*Option.kt` files. `StringResource` is in public type signatures (covered under `projects.resources` above). The `stringResource()` function is internal-only. The `:resources` module already declares `api(compose.components.resources)`, so the `StringResource` symbol arrives transitively via `:resources`. **Demote to internal-only-needed-stringResource() use, keep `implementation`.** (~0 min — already correct.)
- **`koin.core`** (line 20) — verified: `grep -rn "import org\.koin" foundation/src/commonMain` returns **zero matches** (the only Koin imports are in `androidMain`'s `LocationServices.android.kt` and `LocationTrackerFactory.android.kt`, both `org.koin.core.context.GlobalContext`). **Move `koin.core` from commonMain to androidMain (`implementation`)**, since it's only used in androidMain. Or keep in commonMain because the `GlobalContext` API is conceptually a foundation-level dependency. **Recommend keep in commonMain `implementation`** for symmetry with the existing `androidMain` `koin.android` dep — the alternative creates two different idioms for the same DI surface. (~0 min — keep as-is, but document the asymmetry.)
- **`koin.compose.viewmodel`** (line 21) — verified: `grep -rn "import org\.koin\.compose" foundation/src` returns **zero matches**. Declared but never imported. **Drop the dep entirely.** (~2 min)
- **`orbit.core`** (line 24) — verified: same grep as `orbit.viewmodel`. Zero imports. **Drop.** (~2 min)
- **`orbit.compose`** (line 26) — same. **Drop.** (~2 min)
- **`kotlinx.coroutines.core`** (line 29) — used heavily (`CoroutineScope`, `MutableStateFlow`, `StateFlow`, `Flow`, `launch`, `Mutex`, `Job` etc.). `StateFlow<T>` is in public signatures (`loading`, `showErrorDialog`, `currentErrorMessageId`, `extendedLocation`, `currentLocation`, `isTracking`, `permissionState`). `CoroutineScope` is a public constructor param of `LocationManager`. **Should be `api`, not `implementation`.** **Promote to `api`.** Same case as `data` audit's wave-3 finding. (~5 min)
- **`kotlinx.serialization.json`** (line 30) — verified: `grep -rn "@Serializable\|kotlinx\.serialization" foundation/src` returns **zero matches**. Declared but never imported. **Drop the dep entirely.** (~2 min)
- **`kermit`** (line 33) — used: `co.touchlab.kermit.Logger` in `LocationManager.kt:3, 118, 135` (the `Logger.w("LocationManager") { ... }` calls in `runCatching.onFailure`). Used internally only; not in any public signature. Keep `implementation`. (~0 min — already correct.)
- **`connectivity.device`** (line 44) — verified: `grep -rn "Connectivity\|connectivity" foundation/src` returns **zero matches**. Declared but never imported. **Drop the dep entirely.** (~2 min)

### Test deps

- **`kotlinx.coroutines.test`** (line 48) — used in `BaseViewModelTest.kt`, `LoadingControllerTest.kt`, `LocationManagerTrackingTest.kt`. Keep.
- **`turbine`** (line 49) — used in `BaseViewModelTest.kt`, `LoadingControllerTest.kt`. Keep.

### androidMain / iosMain deps

- **`androidx.core.ktx`** (line 53) — verified: `grep -rn "import androidx\.core\." foundation/src` returns **zero matches**. Declared but never imported. **Drop the dep entirely.** (~2 min)
- **`koin.android`** (line 54) — verified: `grep -rn "import org\.koin\.android" foundation/src` returns **zero matches**. The `LocationServices.android.kt` and `LocationTrackerFactory.android.kt` use `org.koin.core.context.GlobalContext` (from `koin-core`), not `koin-android` (which would surface `androidContext`/`androidLogger` integration helpers). **Drop the dep entirely.** Same finding as `data` wave-3. (~2 min)

### Recommended `Depends on` block for `foundation/MODULE.md`

```
## Depends on

- `core` — `DataError`, `LocaleKind`/`MapKind`/`ThemeKind`, `GeoPoint`,
  `Address`/`AddressOption`/`SavedAddress`/`PlaceKind`, `Order.Taxi.Route`,
  `LocationProvider` (interface) in public surface. Sole SDK-internal dep.
- `resources` — `StringResource` in `Selectable.name`, `*Option.name`,
  `BaseViewModel.currentErrorMessageId`, error/locale string accessors.
- `compose-runtime` — `@Composable`, `ProvidableCompositionLocal` types in
  `currentLocale`, `LocaleProvider`, `LocationProvider`, `staggerReveal`,
  `ObserveAsEvents`, `toSelectableItem`, `Local*`.
- `kotlinx-coroutines-core` — `StateFlow`, `Flow`, `CoroutineScope` in
  `BaseViewModel.loading`/`showErrorDialog`, `LocationManager.currentLocation`,
  `LocationManager(scope)` constructor.
- `androidx-lifecycle-viewmodel` — `BaseViewModel : ViewModel`.
- `androidx-lifecycle-runtime-compose` — `Lifecycle.State` in second
  `ObserveAsEvents` overload.
- `moko-geo` — `LocationTracker` in `LocationManager` constructor and
  `createLocationTracker()` return type.

Internal-only (`implementation`-scoped):
`compose-components-resources` (`stringResource()` helper), `kermit`
(`LocationManager` warn-log), `koin-core` (Android-only `GlobalContext`
lookup in `LocationServices.android.kt`/`LocationTrackerFactory.android.kt`).

Drop: `projects.design`, `compose.foundation`, `compose.material3`,
`koin.compose.viewmodel`, `orbit.core`, `orbit.viewmodel`, `orbit.compose`,
`kotlinx-serialization-json`, `androidx-lifecycle-viewmodel-compose`,
`moko-geo-compose`, `connectivity-device`, `androidx-core-ktx`,
`koin-android`. Total: 13 unused deps to drop.
```

(Promote 6 deps from `implementation` to `api`: `projects.resources`, `compose.runtime`, `kotlinx.coroutines.core`, `androidx.lifecycle.runtime.compose`, `moko.geo`. Add new `api(libs.androidx.lifecycle.viewmodel)`. Drop 13 deps. Net change: 17 declared → 11 declared.)

### SDK-internal deps confirmation

- After cleanup, one SDK-internal dep: `:core` (`api`) + `:resources` (promoted to `api`). Two-edge graph from foundation. **Surprising:** the prompt called out that the existing `implementation(projects.design)` was wrong — verified, **zero `import uz.yalla.design.*` lines in foundation/src**. The dep is legacy from when `design` and `foundation` were closer-coupled; should be dropped.
- No cycles. foundation → core/resources is a one-way edge; both have no upward deps.

---

## 3. Restructure candidates (criterion 9-3)

### `wc -l` summary (commonMain + platforms + iosTest)

```
172  commonMain/location/LocationManager.kt          (longest in commonMain)
169  iosTest/location/LocationManagerTrackingTest.kt
159  commonMain/infra/BaseViewModel.kt
129  commonMain/infra/LoadingController.kt
 89  commonMain/infra/ObserveAsEvents.kt
 84  commonMain/location/LocationMappers.kt
 70  commonMain/animation/StaggerReveal.kt
 69  commonMain/location/LocationState.kt
 68  commonMain/settings/ThemeOption.kt
 58  commonMain/settings/LanguageOption.kt
 57  commonMain/locale/LocaleProvider.kt
 55  commonMain/location/LocationProvider.kt
 52  commonMain/settings/MapOption.kt
 50  commonMain/location/Location.kt
 45  commonMain/settings/Selectable.kt
 39  commonMain/infra/DefaultDataErrorMapper.kt
 33  androidMain/location/LocationServices.android.kt
 32  commonMain/locale/ObserveLocale.kt
 30  iosTest/location/FakePermissionsController.kt
 29  iosMain/locale/ChangeLanguage.ios.kt
 29  commonMain/location/LocationServices.kt
 26  iosMain/location/LocationServices.ios.kt
 26  androidMain/locale/ChangeLanguage.android.kt
 23  commonMain/infra/DataErrorMapper.kt
 21  commonMain/settings/OptionModel.kt
 19  commonMain/locale/ChangeLanguage.kt
 14  commonMain/location/LocationTrackerFactory.kt
 14  androidMain/location/LocationTrackerFactory.android.kt
 10  iosMain/location/LocationTrackerFactory.ios.kt
```

### God-file candidates (>300 lines or >5 distinct responsibilities)

- **No file >300 lines.** Criterion 11's god-file threshold not triggered. `LocationManager.kt` at 172 is the longest commonMain file.
- **`LocationManager.kt` (172 lines)** — single class with: `extendedLocation` flow, `currentLocation` flow (override), `isTracking` flow, `permissionState` flow, `startTracking`/`stopTracking` (overrides), `updatePermissionState`, `getCurrentLocation`/`getCurrentLocationOrDefault` (overrides), companion-object `DEFAULT_LOCATION`. One concern (device-location tracking) with the standard reactive-state surface. Cohesive. Keep as-is.
- **`BaseViewModel.kt` (159 lines)** — single class with: `loading`/`showErrorDialog`/`currentErrorMessageId` flows, `safeScope` (CoroutineScope), `handleException`/`handleDataError`/`dismissErrorDialog`, `launchWithLoading`/`launchSafe` extensions, `mapDataErrorToUserMessage`/`mapThrowableToUserMessage` (open-for-override). One concern (ViewModel base infrastructure). Cohesive. Keep as-is.
- **`LoadingController.kt` (129 lines)** — single class with: `loading: StateFlow<Boolean>`, `withLoading()` (the orchestration body), companion `DEFAULT_*` constants. One concern. Cohesive. Keep as-is.

### Nested-package check

- `foundation/src/commonMain/kotlin/uz/yalla/foundation/`
  - `animation/` — 1 file: `StaggerReveal.kt`. Single-file package.
  - `infra/` — 5 files: `BaseViewModel.kt`, `LoadingController.kt`, `ObserveAsEvents.kt`, `DataErrorMapper.kt`, `DefaultDataErrorMapper.kt`. Mixed: ViewModel + Loading + Event observation + Error mapping.
  - `locale/` — 3 files: `ChangeLanguage.kt`, `LocaleProvider.kt`, `ObserveLocale.kt`. Cohesive.
  - `location/` — 8 files. Largest package; covers the entire location stack.
  - `settings/` — 5 files: `Selectable.kt`, `OptionModel.kt`, `ThemeOption.kt`, `LanguageOption.kt`, `MapOption.kt`. Cohesive.

### `infra/` split discussion (per prompt)

The `infra/` package mixes **two concerns**:

1. ViewModel infrastructure (`BaseViewModel.kt`, `LoadingController.kt`, `ObserveAsEvents.kt`).
2. Error mapping (`DataErrorMapper.kt`, `DefaultDataErrorMapper.kt`).

The error-mapping pair is consumed only by `BaseViewModel` (verified: `grep -rn "DataErrorMapper" foundation/src` only matches the three files). It's effectively a private dependency of the ViewModel layer.

**Recommendation: keep `infra/` flat for now, but plan to delete the `DataErrorMapper` interface entirely** (section 1's bucket-2-3 finding). After deletion:
- `DataErrorMapper.kt` is gone.
- `DefaultDataErrorMapper.kt` collapses into a `private fun` or `companion object` member inside `BaseViewModel.kt`.
- The naive split `infra/viewmodel/` vs. `infra/error/` becomes unnecessary — there's no error subsystem to separate, just a `when` statement inside `BaseViewModel`.

**No restructure needed if bucket-2-3 lands.** If Islom rejects the `DataErrorMapper` deletion, then the split is worth doing for coherence — but the deletion is cleaner. (~0 min as default; ~10 min for the split if deletion is rejected.)

### Other organization-only nesting

- **`animation/` (single-file package)** — same shape as `design`'s `motion/`/`radius/`/`space/`/`theme/` single-file packages, which the design audit kept as-is per the per-concern-grouping convention. **Keep.**
- **`locale/`** — three files, cohesive. Keep.
- **`location/`** — eight files, the largest package. After bucket-2-4 deletions (`LocationProvider.kt` Composable wrapper, possibly `LocaleProvider.kt`-style cleanup), drops to 6-7 files. Still cohesive (location stack). **Keep.**
- **`settings/`** — five files, the `Selectable` contract + three sealed hierarchies + the presentation model. **Keep.**

### Recommendation

**No structural restructure needed.** The package boundaries are clean. The `infra/` split debate is mooted by the bucket-2-3 deletion of `DataErrorMapper`. (~0 min)

---

## 4. Quality / rewrite candidates (criterion 11)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/BaseViewModel.kt`

- **Architecture violation: custom MVI instead of Orbit (criterion 11).** `BaseViewModel` extends `androidx.lifecycle.ViewModel` (line 47) and exposes hand-rolled `MutableStateFlow<Boolean>` (loading, showErrorDialog) + `MutableStateFlow<StringResource?>` (currentErrorMessageId) + a `CoroutineExceptionHandler`-bound `safeScope` for error handling. **No `ContainerHost<State, Effect>`.** Per CLAUDE.md / criterion 11, the project's MVI bar is Orbit's `ContainerHost<S, E>`.
  - **However** — verified across YallaClient: every feature ViewModel imports `BaseViewModel` from foundation and **then** layers Orbit on top by also implementing `ContainerHost<State, Effect>`. Example: `feature/home/.../HomeViewModel.kt` extends `BaseViewModel(...)`, `ContainerHost<HomeState, HomeEffect>`. So the project pattern is "BaseViewModel for cross-cutting infra (loading + error dialog) + Orbit for state-machine logic," not "BaseViewModel as the state machine."
  - **Verdict:** the criterion-11 anti-pattern reads as "*replace* state-machine infra with Orbit," which doesn't apply here — `BaseViewModel` is the cross-cutting infra layer, not the state-machine layer. The dual-inheritance pattern across YallaClient confirms this. **No rewrite needed**, but the relationship is worth documenting in MODULE.md `## Notes` so future contributors understand the layer split. (~0 min code, ~5 min docs in wave 10)
- **Mixed concerns within the file.** `BaseViewModel` carries: (a) loading-state proxy via `LoadingController`; (b) error-dialog state (3 flows); (c) `CoroutineExceptionHandler` infrastructure; (d) `launchWithLoading`/`launchSafe` extension functions; (e) error-mapping seam (`mapDataErrorToUserMessage`/`mapThrowableToUserMessage`). Five concerns, all related to "cross-cutting ViewModel utilities" — borderline, but cohesive. The `LoadingController` is *separately* defined in its own file (`LoadingController.kt`), which is the right factoring; the error-mapping seam should follow the same pattern (extract or inline — see below). (~0 min — pattern is OK as-is.)
- **DataErrorMapper-as-interface (architecture violation, criterion 11 + bucket 2-3).** `BaseViewModel(dataErrorMapper: DataErrorMapper = DefaultDataErrorMapper())` — interface-with-one-impl, never overridden anywhere. The actual override mechanism is the `protected open fun mapDataErrorToUserMessage(error: DataError): StringResource = dataErrorMapper.map(error)` at line 147, which is the Kotlin-idiomatic seam. **The constructor parameter and the interface are speculative generalization (bucket 2-5) on top of bucket 2-3.**
  - **Fix:** delete `DataErrorMapper.kt`, fold `DefaultDataErrorMapper.map`'s body directly into the default body of `mapDataErrorToUserMessage`. Delete the constructor parameter. Subclasses still override the `protected open` member. ~30 lines net deletion. Sub-100-line. (~15 min)
  - Suggested target pattern from criterion 11: matches "Mappers as classes — should be `internal object Mapper { fun … }`" — but here the cleaner Kotlin idiom is "fold the `when` into the `protected open` default body" since there's only one consumer site.

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/LoadingController.kt`

- The class shape is sound — owns `loading: StateFlow<Boolean>`, mutex-coordinated `withLoading(...)` orchestration, companion-object defaults. **No try/catch in business logic** (line 95's `try { block() } finally { ... }` is the standard "resource cleanup" idiom, not error-swallowing — the critical path for clearing `_loading` and the show-job state. This is the *pragmatic carve-out* for `try/finally`-without-`catch`; not a criterion-11 violation.). **Keep as-is.**
- One nit: the `Pair(delay, ++generation)` pattern at lines 107, 109 uses `Pair<Duration?, Long>` where a `data class FinishHandle(val remainingDelay: Duration?, val gen: Long)` would name the fields. ~3-line edit, low value. **Defer.** (~0 min)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/infra/ObserveAsEvents.kt`

- Two `@Composable` overloads. The first delegates to `Lifecycle.State.STARTED`; the second takes a `minState: Lifecycle.State`. Standard pattern. **No quality issues.** Test gap noted in section 6. (~0 min)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/ChangeLanguage.kt`

- **`expect fun changeLanguage(languageCode: String): Unit`** — primitive in, `Unit` out. **No platform-type leakage.** The `actual` impls (`Locale.setDefault` on Android, `NSUserDefaults` on iOS) carry their platform types only inside the actual function body. Per the prompt's check ("does the actual implementation leak platform types into commonMain via the expect signature?"): **no leakage**. Clean. **Keep.** (~0 min)
- **`expect fun getCurrentLanguage(): String`** — same. Primitive return. Clean. **Keep.**

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationManager.kt`

- **Side-effecting deps in constructor (per prompt):** `LocationManager(locationTracker: LocationTracker, scope: CoroutineScope, defaultLocation: GeoPoint = DEFAULT_LOCATION)` — three params. None are factories called in `init {}`. The `init` block is implicit (no body); no `LocationTrackerFactory.create()` invocation in constructor. **No constructor-time side effects.** The `scope` is *caller-owned* per the ADR-013-style scope-ownership doc (lines 21-28). **Clean.** (~0 min)
- **Coroutine leakage / dispose surface (per prompt):** confirmed at lines 21-28 of the class KDoc — scope is caller-owned; no internal `Job()`. Cancelling the caller's scope cancels all in-flight tracking. **No `dispose()` is needed because there's no internal lifecycle to dispose.** The pattern is the right one. (~0 min)
- **`runCatching { … }.onFailure { … }` in `startTracking` (lines 99-120) and `stopTracking` (lines 130-138).** This is the "permission-API or system-service" pragmatic carve-out — if `locationTracker.startTracking()` throws (permission denied, GPS off, etc.), the manager logs a warning via Kermit and resets `_isTracking = false` instead of letting the exception propagate up the coroutine and crash the host scope. The criterion-11 anti-pattern is `try { … } catch { … }` in *business logic*; this is **system-boundary error handling** for moko-geo's exception model. Not a violation. **Keep.** Note for the audit: this is the same posture as moko-permissions APIs — the exception boundary lives at the SDK ↔ platform seam.
  - The `Logger.w("LocationManager") { ... }` call is informational only; doesn't swallow domain semantics. (~0 min)
- **Mapper inline (lines 106-114).** Inside `startTracking`, the moko-geo `extendedLocation` is mapped onto foundation's `ExtendedLocation` data class via a 7-field constructor invocation. This is a **DTO↔domain mapping** (moko-geo's `extLoc.location.coordinates.latitude` etc. are platform types; `ExtendedLocation` is the foundation domain type). Per CLAUDE.md, mappers should be `internal object Mapper { fun fromDto(...): Domain }` and live in `data/`. But moko-geo's `extLoc` isn't a DTO — it's a platform-SDK type. **Borderline.** The mapping is 9 lines, single-call-site; pulling it into a named function `private fun MokoExtendedLocation.toExtendedLocation()` would be a stylistic improvement but doesn't need to live in `data/` (no network/storage involved). Sub-30-line refactor. **Defer.** (~0 min — could be a wave-2 polish item.)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationMappers.kt`

- **Per prompt: are these DTO mappers (mover candidate) or domain↔domain (foundation-appropriate)?** Verified by reading the imports: every input type is from `uz.yalla.core.*` (`AddressOption`, `SavedAddress`, `Address`, `Order.Taxi.Route`, `GeoPoint`, `PlaceKind`); every output type is a foundation type (`Location`, `FoundLocation`). **These are domain↔domain coercions** — `core` domain types being adapted into foundation's UI-ready models. **Not DTO mappers.** No move to `data/` warranted. **Keep.** (~0 min)
- **However** — the file shape is "extension functions on `core/` types living in `foundation/`" which is a bit unusual. Per CLAUDE.md, mappers should be `internal object Mapper { fun fromDto(...): Domain }`. Foundation's mappers are top-level extension functions, not `internal object`. The CLAUDE.md guidance was tightened around DTO seams; for domain↔domain coercions in foundation, the extension-function pattern is idiomatic Kotlin and matches `core`'s `Order.toExecutor()` precedent. **No change needed.** (~0 min)
- **Borderline:** `Order.sortedRouteLocations()` is the only function that does more than a 1:1 field copy — it sorts and maps the route list. The naming is fine (`sortedRouteLocations`); it's used in YallaClient (`feature/home/.../OrderManager.kt`, `OrdersSheet.kt`, etc.). **Keep.** (~0 min)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationProvider.kt`

- Already covered in section 1 bucket 2-4. **The Composable wrapper around `LocationManager` is dead public surface.** Both `LocalLocationManager` and `currentLocationManager()` have zero callers. **Delete the file.** ~55 lines removed. Public-API change → `refactor!:`. (~10 min)
- Note: this is **not** to be confused with `core.location.LocationProvider` (the *interface* that `LocationManager` implements) — that is consumed by `:maps` (`MapDependencies.kt:35`, `MapEffects.kt:24`). See section 5 for the interface-level discussion.

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/location/LocationState.kt`

- **`ExtendedLocation` demote-to-internal** (section 1 bucket 2-4). Currently public; consumed only inside `LocationManager`. The `extendedLocation: StateFlow<ExtendedLocation?>` public field at `LocationManager.kt:61` is also unconsumed externally (verified via the YallaClient grep — only `LocationManager.currentLocation` and `LocationManager.permissionState` are read). **Demote both `ExtendedLocation` and `LocationManager.extendedLocation` to `internal`.** ~17 lines impact. Sub-100-line. (~15 min)

### `foundation/src/commonMain/kotlin/uz/yalla/foundation/locale/LocaleProvider.kt` + `ObserveLocale.kt`

- Already covered in section 1 bucket 2-4. **Both files are dead public surface** (~57 + ~25 = ~82 lines). **Recommend delete both, gate on Islom.** Same shape as `design` G9 (motion package gate). Two readings:
  1. **Truly dead** — YallaClient defines its own `LocaleProvider`/`LocaleState` and uses `getCurrentLanguage()` directly; the foundation-side abstraction was an early-design slot that never got consumed.
  2. **Forward-staged** — the foundation `LocaleProvider`/`currentLocale` was the canonical pattern; YallaClient's local copy is what's wrong, and a future migration consolidates back into foundation.
  My read leans (1) — the YallaClient `LocaleProvider.kt` has logic foundation's doesn't (post-language-change Activity recreation on Android), so foundation's version was demonstrably insufficient and YallaClient's is canonical. **Lean delete.** (~15 min if approved; **gate on Islom**.)

### Architecture violations — full pass

- **`try { … } catch { … }` in business logic** — none. The `LoadingController.kt:95` is `try { … } finally { … }` (no catch — the standard cleanup idiom). The `LocationManager.kt:99-120, 130-138` are `runCatching { … }.onFailure { … }` at the SDK ↔ moko-geo boundary (system-API carve-out). **Both pragmatic carve-outs apply.** No criterion-11 violation. (See section 8 for the carve-out discussion.)
- **Mappers as classes / DTO extension functions** — `LocationMappers.kt` is domain↔domain (verified above), not DTO. No violation. The implicit DTO mapping inside `LocationManager.kt:106-114` is at the moko-geo boundary; not a criterion-11 violation but could be extracted as a stylistic polish.
- **Service classes named `Api`** — N/A; foundation has no networking.
- **Custom MVI** — `BaseViewModel` is custom-shaped but the project pattern uses Orbit `ContainerHost` *on top* of it (verified across YallaClient feature ViewModels). Not a violation per the project's actual usage pattern. (See above.)
- **Arrow types instead of `Either` + `DataError`** — verified: `grep -rn "arrow\.\|Either\b" foundation/src` returns the project's `uz.yalla.core.error.DataError` only, no Arrow. **No violation.**
- **`InMemoryTokenProvider` / manual `Authorization` header / `AuthEventBus`** — N/A; foundation has no networking.
- **String-typed identifiers that should be value classes** — `Location.id: Int?`, `FoundLocation.id: Int?` are bare `Int?` where core has `OrderId`/`AddressId`/`AddressOptionId` value classes. **Same pattern as core's audit § 4 finding** (deferred to phase-2 `data` plan, which already landed). **However**, `FoundLocation` and `Location` are foundation types (UI-ready models), and the IDs they carry are `AddressOption.id.raw`-style unwrapped values (verified in `LocationMappers.kt:18, 51, 67`). **Recommend keep `Int?` here** — these are the post-unwrap values consumed by UI; re-wrapping them as `AddressId`/`AddressOptionId` would add ceremony at the read site without adding type safety (the wrapper is already shed at the mapper boundary). Decision: **defer indefinitely**, no action. (~0 min)

### Compose recomposition correctness

- **`@Stable` / `@Immutable` annotations:** verified `grep -rn "@Stable\|@Immutable" foundation/src/commonMain` returns **zero matches**. The data classes `Location`, `FoundLocation`, `ExtendedLocation`, `OptionModel<T>`, `LocaleState`, `Selectable`-implementing data objects (3 sealed hierarchies, 7 entries total) have no `@Immutable` annotation. Compose's stability inference may infer them stable already (all-`val` fields with stable types — `Int?`, `String?`, `GeoPoint`, `LocaleKind`, `MapKind`, `ThemeKind`, `ImageVector`, `StringResource`, `Painter?`, `Color`). **Same gap as `design` G12** (which Islom approved adding `@Immutable` uniformly). **Recommend apply same `@Immutable` annotations across foundation's data classes.** ~7 annotations across 7 files. Sub-100-line. (~15 min)
  - Affected files: `Location.kt` (×2), `LocationState.kt` (×1), `OptionModel.kt` (×1), `LocaleProvider.kt:LocaleState` (×1), `MapOption.kt`/`LanguageOption.kt`/`ThemeOption.kt` (sealed-hierarchy data objects — borderline, since `data object` is structurally stable already; the explicit annotation matches design's precedent regardless).

### Untestable shape

- **`BaseViewModel`** uses `androidx.lifecycle.ViewModel` + `viewModelScope`, which makes it `Dispatchers.Main`-bound by default. `BaseViewModelTest.kt` already handles this with `Dispatchers.setMain(UnconfinedTestDispatcher())` in `@BeforeTest`. **Testable.** (~0 min)
- **`LocationManager`** takes a `LocationTracker` and a `CoroutineScope` — both DI-injected. Tests in `iosTest` use `FakePermissionsController` to seed a real `LocationTracker` (since on iOS `LocationTracker(permissionsController = …)` works without platform infrastructure). **Testable in iosTest, not in commonTest** — see section 6. (~0 min — by-design carve-out.)
- **`ObserveAsEvents`** — `@Composable` that uses `LocalLifecycleOwner` + `LaunchedEffect` + `repeatOnLifecycle`. Not directly testable in commonTest (compose-ui-test isn't wired). Existing test (`ObserveAsEventsTest.kt:1-32`) is a stub with a TODO. **Untestable in current setup, but not a god-class problem** — it's a Compose UI primitive that needs `runComposeUiTest` infra. (~0 min — accepted gap.)
- **`StaggerReveal` `Modifier.staggerReveal`** — same untestable-without-Compose-UI-harness problem. Section 6 notes the gap.

### Summary of section 4 rewrite candidates

| Item | Lines | Gate? |
| ---- | ----- | ----- |
| Delete `DataErrorMapper` interface, inline `DefaultDataErrorMapper` body into `BaseViewModel.mapDataErrorToUserMessage` | ~30 | no (sub-100) |
| Delete `foundation.location.LocationProvider` (Composable wrapper) | ~55 | no (sub-100) |
| Delete `foundation.locale.LocaleProvider` + `LocaleState` + `LocalLocaleState` + `currentLocaleState` | ~57 | no (sub-100) |
| Delete `foundation.locale.currentLocale` (`ObserveLocale.kt`) | ~25 | no (sub-100) |
| Demote `ExtendedLocation` + `LocationManager.extendedLocation` to `internal` | ~17 | no (sub-100) |
| Add `@Immutable` to foundation data classes | ~7 | no |
| **Combined dead-CompositionLocal sweep** (LocaleProvider + currentLocale + LocationProvider Composable) | **~137** | **yes — borderline gate, recommend** |

The combined CompositionLocal sweep crosses the 100-line gate threshold (137 lines deleted across three files). **REWRITE >100 LINES — NEEDS GATE.** Same shape as `core` G1 (DataError variants) and `design` G9 (motion package): zero-consumer public surface that's been speculative-staged but never wired. Recommend Islom decide as a single decision (delete all three CompositionLocal slots, since they share the same "zero-consumer" diagnosis).

---

## 5. Promote/demote candidates (criterion 1)

Applied lego test to every public type in `foundation/src/commonMain`.

### Bricks (stays in foundation — vast majority)

`BaseViewModel`, `LoadingController`, `ObserveAsEvents`, `Modifier.staggerReveal`, `Location`, `FoundLocation`, `LocationManager`, `ExtendedLocation` (post-demote: internal), `LocationPermissionState`, `LocationMappers` extensions, `Selectable`, `OptionModel<T>`, `ThemeOption`, `LanguageOption`, `MapOption`, `toSelectableItem`, `expect fun changeLanguage` / `getCurrentLanguage` / `isLocationServicesEnabled` / `openLocationSettings` / `createLocationTracker`.

All pass the lego test:
- **No hardcoded product copy** — verified `grep -rn '[А-Яа-яЁё]\|[ʻ]' foundation/src/commonMain` returns 0 matches. Strings are loaded via `Res.string.*` references (resource accessors), never inline.
- **No screen-shaped types** — no `*Screen`, `*Route`, `*Sheet` types.
- **No Ildam-specific business orchestration** — the only product-flavored content is the `LocationManager.DEFAULT_LOCATION = GeoPoint(41.2995, 69.2401)` (Tashkent, Uzbekistan). This is the SDK's geographic anchor and arguably product-specific in the literal sense, but per the design audit's reasoning ("Yalla branding IS the product"), the geographic anchor for an Uzbek taxi SDK belongs in the SDK. Same call as the Tashkent default.

### Critical: `foundation.location.LocationProvider` interface vs. `core.location.LocationProvider` interface (per prompt)

Two separate `LocationProvider` types in the codebase:

1. **`core.location.LocationProvider`** (post-phase-2 wave-4 flatten) — the **contract**. Pure interface in `core/src/commonMain/kotlin/uz/yalla/core/location/LocationProvider.kt`. Defines `currentLocation: Flow<GeoPoint?>`, `getCurrentLocation()`, `getCurrentLocationOrDefault()`, `startTracking()`, `stopTracking()`. Implemented by `foundation.location.LocationManager`. Consumed externally by `:maps` (`MapDependencies.kt:35`, `MapEffects.kt:24`). **This is the canonical interface.**
2. **`foundation.location.LocationProvider`** — a `@Composable` wrapper that takes a `LocationManager` and provides it via `LocalLocationManager`. **Not** an interface; not a contract. Pure Composable boilerplate. **Zero consumers anywhere** (verified in section 1 bucket 2-4).

**Verdict: foundation's `LocationProvider` is a redundant naming collision with core's interface.** Delete the foundation Composable + its `LocalLocationManager`/`currentLocationManager` slot per section 1's bucket 2-4 finding. After deletion, only the core interface remains under the `LocationProvider` name. (Same gate as section 4's CompositionLocal sweep.)

### foundation's `Location` type vs. core's `GeoPoint` (per prompt)

Per the prompt's ask: are they redundant?

- **`core.geo.GeoPoint`** — `data class GeoPoint(val lat: Double, val lng: Double)`. Pure 2-tuple of coordinates. (`core/src/commonMain/kotlin/uz/yalla/core/geo/GeoPoint.kt`.)
- **`foundation.location.Location`** — `data class Location(val id: Int?, val name: String?, val point: GeoPoint?)`. Triple of (optional id, optional name, optional GeoPoint). The `Location` *contains* a `GeoPoint`; it's not a coordinates wrapper.

**Distinct concepts.** `GeoPoint` is the 2-D map coordinate; `Location` is the UI-ready model carrying an id, a display name, and the coordinates. **Both belong in the SDK.** The `LocationMappers.kt` bridge (`Address.toLocation`, `AddressOption.toFoundLocation`, `Order.Taxi.Route.toLocation`) is non-trivial: it pulls together the per-source-domain id + name + lat/lng into a single uniform `Location`/`FoundLocation` shape that UI consumers iterate without caring whether the source was an address, an option, or a route point. **Two-type design is justified.** (~0 min — no demotion.)

### Borderline — flag for Islom

- **`LocationManager.DEFAULT_LOCATION = GeoPoint(41.2995, 69.2401)`** (Tashkent center). Product-specific in the literal sense (the SDK is Uzbekistan-shaped). Per "Yalla branding IS the product" from design, the geographic anchor belongs in the SDK. **Keep, but document in MODULE.md `## Notes`** so future contributors know the default isn't a placeholder. (~0 min — already there, just note it.)
- **`LanguageOption` narrowed to Uzbek + Russian** (`LanguageOption.kt:14-18`, "ADR-014 Phase 3 narrowing"). The original LocaleKind set was wider; narrowing to two production locales is product-specific. But `LocaleKind` is in `core` (the wider set) and `LanguageOption` in `foundation` — both have to stay narrow because YallaClient consumes `LanguageOption.all` for the picker UI. This is the canonical "core defines the wider type, foundation surfaces the production-narrow subset" pattern. **Keep.** (~0 min)

### Demotion candidates

None directly. Foundation matches its description as the brick-glue layer between core (atoms) and primitives/composites/feature (UI/assemblies).

### Promotion candidates (YallaClient → foundation)

- **None observed in this audit.** YallaClient does define a custom `LocaleProvider`/`LocaleState` (`composeApp/src/commonMain/kotlin/uz/yalla/client/locale/LocaleProvider.kt`) that has logic foundation's version doesn't (Android Activity recreation on language change). **If foundation's `LocaleProvider` is deleted per bucket 2-4, no promotion is needed; YallaClient's keeps living where it is** (it depends on Activity-level concerns that don't belong in a brick).
- **Caveat:** this audit didn't deep-dive every YallaClient `feature/*` ViewModel. If common patterns surface across multiple features (e.g. shared loading-controller wiring patterns), they'd promote into foundation. Out of scope for the audit prompt.

### Notes about hardcoded strings and product semantics

- No Russian/Uzbek string literals in `foundation/src/commonMain` (verified).
- No business rules embedded in domain types.
- Settings options (`ThemeOption`, `LanguageOption`, `MapOption`) carry **generic** capability names (`Light`/`Dark`/`System`, `Uzbek`/`Russian`, `Google`/`Libre`). No "PrioritySupport"-style product-tier names; no operator-vs-client distinctions. **All bricks.** (~0 min)
- The `LanguageOption.icon = YallaIcons.FlagUz` / `FlagRu` references are flag *icon resources*, not text — visual brand, not business semantics. Same posture as design's brand colors. (~0 min)

### Verdict for `MIGRATION_LIST.md`

- "## To promote into foundation" — empty (out of scope for this audit).
- "## To demote from foundation" — empty.
- "## To decide" — `foundation.location.LocationProvider` Composable + `foundation.locale.LocaleProvider` + `currentLocale` deletion gate (section 4 / section 1 bucket 2-4); document the Tashkent default in MODULE.md.

---

## 6. Missing tests (criterion 6)

Foundation has 7 commonTest files + 2 iosTest files (1 test class + 1 fake-helper). Inventory:

- `commonTest/infra/BaseViewModelTest.kt` — 8 tests covering loading flow, error-dialog state machine, exception handling.
- `commonTest/infra/DefaultDataErrorMapperTest.kt` — 8 tests covering every `DataError.Network.*` variant.
- `commonTest/infra/LoadingControllerTest.kt` — 7 tests covering fast/slow/min-display-time/concurrent/deadlock paths.
- `commonTest/infra/ObserveAsEventsTest.kt` — placeholder stub (no real tests; needs compose-ui-test harness).
- `commonTest/locale/ChangeLanguageTest.kt` — 5 smoke tests (no-throw + non-blank).
- `commonTest/location/LocationMappersTest.kt` — 7 tests for every mapper extension.
- `commonTest/settings/SettingsOptionsTest.kt` — 12 tests for ThemeOption / LanguageOption / MapOption (round-trip + completeness).
- `iosTest/location/LocationManagerTrackingTest.kt` — 6 tests covering tracking idempotence, scope cancellation, permission state, default-location fallback.
- `iosTest/location/FakePermissionsController.kt` — fake harness (not a test).

### Inventory by package

#### `foundation/animation/`

- **`Modifier.staggerReveal`** — **no test.** The reveal-animation behavior (alpha + translationY based on `visible` and `index * staggerMs` delay) is the load-bearing logic. Untestable in commonTest without the Compose UI test harness. Same gap as `ObserveAsEvents`. **Accepted gap pending compose-ui-test wiring.** (~0 min — flagged as accepted carve-out.)

#### `foundation/infra/`

- `BaseViewModel` — 8 tests cover loading-flow / error-dialog state-machine / exception-mapper integration. Per the prompt: "for `BaseViewModel`: state-machine intent → state-transition tests per criterion 6's Orbit `ContainerHost` bar. List gaps." **`BaseViewModel` is NOT an Orbit ContainerHost** (see section 4) — it's the cross-cutting infra layer that feature ViewModels inherit alongside their Orbit `ContainerHost<S, E>`. **Criterion 6's state-machine bar applies to feature-level ViewModels, not foundation's `BaseViewModel`**, which is testable as cross-cutting infra. Existing 8 tests cover the state-flow surface. **No gap.** (~0 min)
- **`launchSafe` / `launchWithLoading` overlap test** — there's no test that asserts `safeScope.launchWithLoading { throw ... }` produces both the loading-cleanup AND the error-dialog flip. The existing tests cover each path separately. ~1 test, ~5 min. **Optional; low value.** (~0 min)
- `LoadingController` — 7 tests cover fast/slow/min-display/concurrent/deadlock. The `loading` flow is fully exercised. **No gap.** (~0 min)
- `ObserveAsEvents` — placeholder stub. Real tests blocked by missing compose-ui-test harness. **Accepted gap** per the file's KDoc. (~0 min)
- `DefaultDataErrorMapper` — 8 tests, one per `DataError.Network.*` variant (Connection, Timeout, Client, ClientWithMessage, Server, Serialization, Guest, Unknown). **Confirmed: every surviving Network variant is covered post-wave-2 simplification.** **No gap.** (~0 min)
- `DataErrorMapper` interface — no contract test. After bucket-2-3 deletion (section 1), this is moot. If kept, ~1 round-trip-fake test, ~5 min. (~0 min — defer to Islom's decision.)

#### `foundation/locale/`

- `changeLanguage` / `getCurrentLanguage` (`expect`) — 5 smoke tests (no-throw + non-blank-after-change). The tests run in iosSimulatorArm64 only (per the file's class KDoc — Android JVM unit-test runner not wired). **Limited coverage but accepted carve-out.** (~0 min)
- **`currentLocale()`** (`ObserveLocale.kt`) — **no test.** Even if the function is dead-code-deleted per section 1 bucket 2-4, it would have needed a Compose-UI-harness test. ~0 min if deleted; ~30 min if kept. (~0 min — defer to Islom's decision.)
- **`LocaleProvider` / `LocaleState` / `LocalLocaleState` / `currentLocaleState`** — **no test.** Same shape — Compose-UI-harness test if kept. **If deleted per section 1 bucket 2-4, no test gap.** (~0 min — defer.)

#### `foundation/location/`

- `Location` / `FoundLocation` data classes — round-trip equality / `copy()` sanity not tested. Trivial data classes with all-`val` fields; the implicit `data class equals/hashCode` is a Kotlin guarantee. **Optional; low value.** (~0 min)
- `FoundLocation.toLocation()` extension — covered by `LocationMappersTest.shouldMapFoundLocationToLocation`. **No gap.** (~0 min)
- `LocationMappers.kt` — every public extension has a test in `LocationMappersTest.kt`: `AddressOption.toFoundLocation`, `SavedAddress.toFoundLocation`, `Address.toLocation` (×2 variants), `Order.Taxi.Route.toLocation`, `Order.sortedRouteLocations`, `FoundLocation.toLocation`. **Complete.** (~0 min)
- `LocationManager` (per prompt: "tracking lifecycle (start/stop/dispose), permission state, coroutine cancellation. Existing iosTest covers tracking on iOS; commonMain coverage gap?"):
  - **iosTest covers** start-idempotence, stop-before-start no-op, scope-cancellation-stops-tracking, updatePermissionState propagation, `getCurrentLocationOrDefault` with custom default + SDK default, `getCurrentLocation` null-when-no-fix.
  - **commonTest cannot** instantiate `LocationManager` because moko-geo's `LocationTracker` is an `expect class` whose iOS actual is satisfied by `PermissionsControllerProtocol` (an interface, hence fakeable) but whose Android actual requires a real `Context`. The `FakePermissionsController` only works on iOS.
  - **This is the standard expect/actual testability carve-out**: the seam testable on one platform via interface-fake doesn't extend to the other. The `iosTest` directory absorbs the platform-test responsibility. **Accepted gap; no commonTest coverage achievable without restructuring the moko-geo `expect class` to be more testable.** (~0 min — accepted carve-out, document in `## Notes`.)
- `LocationManager.startTracking` / `stopTracking` `runCatching` paths — covered by iosTest's scope-cancel test (which exercises the failure branch). The success branch can't be tested without a real GPS fix on the simulator. **Acceptable.** (~0 min)
- `LocationServices` (`isLocationServicesEnabled`, `openLocationSettings`) `expect` functions — **no tests.** Both depend on platform system services (Android `LocationManager.GPS_PROVIDER`, iOS `CLLocationManager.locationServicesEnabled`); cannot be tested in commonTest. The actuals are short and platform-specific. **Accepted gap.** (~0 min)
- `LocationProvider` Composable wrapper / `LocalLocationManager` / `currentLocationManager` — **no tests.** Compose-UI-harness gap; if deleted per section 1 bucket 2-4, moot. (~0 min — defer.)
- `LocationState` (`ExtendedLocation` + `LocationPermissionState`) — `LocationPermissionState` enum is a 4-entry pure data type; covered indirectly by `LocationManagerTrackingTest.updatePermissionStatePropagatesValue`. `ExtendedLocation.toGeoPoint()` is a 1-line conversion; not directly tested but trivially correct. **Optional.** (~0 min)
- `LocationTrackerFactory.createLocationTracker()` `expect` — both actuals are 3-5 line wrappers. **No test;** untestable without real `PermissionsController` instantiation. Accepted. (~0 min)

#### `foundation/settings/`

- `ThemeOption` / `LanguageOption` / `MapOption` — 12 tests in `SettingsOptionsTest.kt`. Round-trip (`from(option.kind) == option`), completeness (`all.size == n`, `all.contains(...)`), kind→option mapping. **Complete.** (~0 min)
- `Selectable` interface — no contract test. Single-method interface; the method types are `StringResource` and `ImageVector?`. The `toSelectableItem()` extension converts to `OptionModel`. Not directly tested in `SettingsOptionsTest.kt` — only the round-trip behavior. **Optional**; could add a `toSelectableItem(LanguageOption.Uzbek)` round-trip via Compose-UI-harness, but the value is tiny. (~0 min)
- `toSelectableItem()` extension — `@Composable`; needs Compose-UI-harness. **Accepted gap.** (~0 min)
- `OptionModel<T>` — pure data class; no logic. **No gap.** (~0 min)

### Summary by package

| Package | Effort | Gap |
| ------- | ------ | --- |
| `animation/` | ~0 min (accepted: needs compose-ui-test) | `Modifier.staggerReveal` |
| `infra/` | ~0 min unconditional | `ObserveAsEvents` (compose-ui-test gap, accepted); optional `launchSafe` overlap test |
| `locale/` | ~0 min (defer to deletion gate) | `currentLocale`, `LocaleProvider` family — drop with the dead-code purge |
| `location/` | ~0 min (accepted: moko-geo expect-class testability carve-out) | `LocationManager` commonTest coverage (no path), `LocationServices`/`LocationTrackerFactory` actuals |
| `settings/` | ~0 min | `toSelectableItem`/`Selectable` (compose-ui-test gap, accepted); option round-trip + completeness already covered |

**Total wave-8 effort estimate: ~0 min unconditional + ~5 min optional `launchSafe` overlap test.** Brings foundation test count from 9 files (8 commonTest + 1 iosTest test class) to 9-10 files. Most gaps are **accepted carve-outs** (compose-ui-test harness not wired; moko-geo `expect class` not fakeable in commonTest).

### Note on `moko-permissions` / `expect/actual` testability carve-out

Per the prompt: "`iosTest` in this module uses moko-permissions's `FakePermissionsController` for `LocationManager`. Same harness can't run in commonTest because moko-permissions has platform-specific dependencies. Document this as the standard expect/actual testability carve-out."

**Confirmed.** The `iosTest/FakePermissionsController.kt` implements `dev.icerock.moko.permissions.ios.PermissionsControllerProtocol` (an iOS-side typealias for the Android `PermissionsController` class). Because the iOS protocol is an interface, it's fakeable. The Android `PermissionsController` is a class with non-mockable internals; the same fake doesn't compile in commonTest because the typealias resolves differently per platform. **Document this in MODULE.md `## Notes`** as the canonical carve-out. (~5 min in wave 10.)

---

## 7. MODULE.md staleness (criterion 5)

Current `foundation/MODULE.md` (25 lines) uses the old `# Module / # Package …` format with an "Architecture" section. Phase-1 form (per `bom/MODULE.md`, post-cleanup `core/MODULE.md`, post-cleanup `data/MODULE.md`, post-cleanup `design/MODULE.md`) is:

```
# Module <name>
> One-line tagline.

## What this is
## What this is NOT
## Usage
## Notes
## Depends on
```

### Sections to add

- **`> One-line tagline.`** — currently missing. Suggested: `> Brick-glue: ViewModel infra, location, locale, settings options.`
- **`## What this is`** — replace the "Architecture" + four `# Package` blurbs with a tight 5-7 bullet list:
  - `BaseViewModel` — cross-cutting ViewModel base (loading flow, error-dialog state, exception-handling `safeScope`). Feature ViewModels extend this *and* implement Orbit's `ContainerHost<S, E>` separately.
  - `LoadingController` — smart-timing loading-state coordinator (delays show, enforces min-display, deduplicates concurrent operations). Used internally by `BaseViewModel`.
  - `ObserveAsEvents` — `@Composable` flow collector tied to `Lifecycle.State.STARTED` (or a custom min-state).
  - `Modifier.staggerReveal` — draw-phase-only stagger reveal animation.
  - `LocationManager` — moko-geo-backed `LocationProvider` impl. Caller-owned scope (no internal `dispose()`).
  - `Location` / `FoundLocation` — UI-ready domain models bridging `core/` types (`Address`, `AddressOption`, `SavedAddress`, `Order.Taxi.Route`).
  - Settings options (`ThemeOption`, `LanguageOption`, `MapOption`) — sealed hierarchies on the `Selectable` contract for picker UIs.
  - Locale `expect` functions (`changeLanguage`, `getCurrentLanguage`) and location-services `expect` functions (`isLocationServicesEnabled`, `openLocationSettings`, `createLocationTracker`).
- **`## What this is NOT`** — explicitly:
  - **Not** a state-machine framework — Orbit `ContainerHost` is the project pattern; `BaseViewModel` is the *cross-cutting infra layer that sits underneath* the state machine, not the state machine itself.
  - **Not** a UI module — no `Button`, `TextField`, `Card`, `Sheet`. Those live in `primitives` / `composites`.
  - **Not** a networking module — no Ktor, no DTO mappers. Those live in `data`.
  - **Not** a feature module — no screens, no navigation, no business orchestration.
  - **Not** a `core` extension — `core` carries pure types and the `LocationProvider` interface; foundation provides the `LocationManager` impl + UI-ready models.
- **`## Usage`** — 8-12 lines showing typical SDK consumer wiring:
  ```kotlin
  // Feature ViewModel: BaseViewModel + Orbit
  class HomeViewModel(
      private val getOrders: GetOrdersUseCase,
  ) : BaseViewModel(), ContainerHost<HomeState, HomeEffect> {
      override val container = container<HomeState, HomeEffect>(HomeState())

      fun loadOrders() = intent {
          safeScope.launchWithLoading {
              getOrders().fold(
                  ifFailure = { handleDataError(it) },
                  ifSuccess = { reduce { state.copy(orders = it) } },
              )
          }
      }
  }

  // Composable: ObserveAsEvents for one-shot effects
  ObserveAsEvents(viewModel.container.sideEffectFlow) { effect -> /* ... */ }
  ```
- **`## Notes`** — fold in:
  - The `BaseViewModel` + Orbit `ContainerHost` dual-inheritance pattern explanation.
  - The `LocationManager` caller-owned scope rationale (preserve the substantive paragraph from the current KDoc, drop the "(ADR-013)" parenthetical).
  - The `LocationManager.DEFAULT_LOCATION = GeoPoint(41.2995, 69.2401)` Tashkent anchor note (product-specific by design).
  - The `LanguageOption` Phase-3 narrowing to Uzbek+Russian (drop the "(ADR-014)" parenthetical, keep the substantive content).
  - The moko-geo `expect class` testability carve-out (why `LocationManager` is tested in iosTest, not commonTest).
  - The compose-ui-test harness-not-wired note (why `ObserveAsEvents`, `Modifier.staggerReveal`, `toSelectableItem` lack tests).
  - The dead-CompositionLocal cleanup status (if Islom approves the section-4 sweep; otherwise note that `LocaleProvider`/`currentLocale`/`LocationProvider` Composable are consumer-pending).
- **`## Depends on`** — the block from section 2.

### Sections to remove

- **Lines 6-12 (`## Architecture`)** — the "Foundation serves as the intentional glue layer between `core` (pure types/contracts) and UI modules (`primitives`, `composites`). It contains two categories: …" framing paragraph. This is exactly the "Foundation serves as the intentional glue layer..." aspirational framing the prompt called out. Drop. The role description moves into the tagline + `## What this is`.
- **Lines 14-25 (`# Package uz.yalla.foundation.infra` through `# Package uz.yalla.foundation.settings`)** — all four per-package blurbs. Per-package KDoc lives on the source, not in MODULE.md. Drop entirely. Matches the precedent set when core's MODULE.md had 11 per-package blurbs removed, data's had 4 removed, and design's had 4 removed.

### Sections to rewrite

- **Lines 1-5** — opening paragraph. "Core types and UI layer bridge — ViewModel infrastructure, location management, locale handling, and UI-ready domain models for the Yalla SDK." Replace with the phase-1 tagline + `## What this is` form.

### Cross-check from prompt

- `foundation/MODULE.md` does not reference any moved/flattened package, so no stale-package cleanup needed beyond the four `# Package` blurbs themselves.
- The prompt asked to confirm that "foundation/MODULE.md has both 'Architecture' framing and per-package blurbs. Drop both per phase-1 precedent." **Both confirmed; both dropped in the rewrite.**

Total wave-10 effort: full rewrite of `foundation/MODULE.md` from scratch on phase-1 form. **~30 min** — slightly longer than design's because the dual-inheritance / scope-ownership / expect-class-testability notes need careful framing.

---

## 8. Reviewer notes

### Pushback on specific findings

- **Section 1, bucket 2-4 on `LocaleProvider` / `currentLocale` / `LocationProvider` Composable.** I flagged all three CompositionLocal-based wrappers as dead, totaling ~137 lines. Two readings:
  1. **Truly dead.** YallaClient defines its own `LocaleProvider` (with Activity-recreation logic foundation's lacks), and Koin DI is the actual seam for `LocationManager` everywhere. The foundation Composables are early-design slots that never got consumed.
  2. **Forward-staged.** The CompositionLocal pattern is canonical for the SDK; YallaClient's local `LocaleProvider` is what's wrong (it should consume foundation's), and the Koin-only injection pattern is a YallaClient-specific shortcut that the SDK shouldn't accommodate.
  
  **I lean (1).** YallaClient's `LocaleProvider` carries substantive logic foundation's doesn't; if foundation were canonical, it'd need to absorb that logic. The simpler read is that Koin DI won the architecture battle at the consumer level, and the CompositionLocal slots are vestigial. **Recommend delete the trio as a single sweep.** Public API change → `refactor!:` (alpha, allowed). Sub-200-line. Same shape as `core` G1 (DataError variants) and `design` G9 (motion package): zero-consumer cleanup. The 100-line gate threshold is crossed only when all three are bundled; bundling makes sense because they share the same diagnosis.

- **Section 1, bucket 2-3 on `DataErrorMapper` interface.** I flagged this as a single-use abstraction (interface with one impl, never overridden by any consumer). The KDoc claims "Implement this interface to customize error message mapping per app or feature" — but no one does. The actual override seam in YallaClient is `BaseViewModel.mapDataErrorToUserMessage` (the `protected open` default), which is the Kotlin-idiomatic pattern. **Recommend delete the interface, inline `DefaultDataErrorMapper.map`'s body into the `protected open` default.** Sub-100-line, ~30 lines net deletion. Public API change (the `BaseViewModel(dataErrorMapper: …)` constructor goes away) → `refactor!:` (alpha, allowed). Verified consumers across YallaClient: every `BaseViewModel` subclass uses the default constructor, none pass a custom mapper. Safe to delete.

- **Section 4 on `BaseViewModel` "custom MVI" architecture violation.** Initially looks like a criterion-11 violation (custom state-flow infra instead of Orbit). On closer reading, it isn't: feature ViewModels extend `BaseViewModel` *and* implement `ContainerHost<S, E>` separately (verified across YallaClient). `BaseViewModel` is the cross-cutting infra layer (loading + error dialog), not the state machine. **No rewrite needed**, but the dual-inheritance pattern needs documenting in MODULE.md. The criterion-11 anti-pattern reads as "custom MVI *instead of* Orbit"; this is "supplementary infra *alongside* Orbit." Different shape.

- **Section 4 on `LocationManager.startTracking` / `stopTracking` `runCatching`.** Initially looks like a criterion-11 "try/catch in business logic" violation. On closer reading, it isn't: `runCatching { ... }.onFailure { ... }` is the system-API boundary error handler — moko-geo's `LocationTracker.startTracking()` can throw permission/GPS-off exceptions, and the manager needs to (a) reset `_isTracking = false`, (b) log a warning, (c) avoid crashing the host coroutine scope. **This is the SDK ↔ moko-geo seam**, the exact carve-out the criterion implicitly allows ("errors live in `data/` and get mapped to `Either<DataError, T>`" — but moko-geo isn't in `data/`, it's a platform SDK). **Pragmatic carve-out applies.** The same pattern would surface for any moko-permissions-style API. Document the carve-out in MODULE.md `## Notes`.

- **Section 4 on `LoadingController.kt:95`'s `try { ... } finally { ... }`.** No `catch`. This is the standard "ensure cleanup runs" idiom for the mutex-guarded `_loading` state. Not a criterion-11 violation. **Keep.** Worth noting that the criterion-11 wording specifically calls out `try { … } catch { … }`, not `try { … } finally { … }`; the former swallows errors, the latter does not.

- **Section 4 on `@Immutable` annotations gap.** Same posture as design G12. Foundation has zero `@Immutable` annotations across all data classes; design has them only on motion (everywhere else also missing). The cross-module inconsistency is the real signal — **either annotate everywhere uniformly, or rely on stability inference everywhere.** I recommend annotate, matching design G12's resolution. Sub-100-line, mechanical, behavior-preserving.

### Cross-cutting patterns

- **`@since 0.0.X` ceremony tags affect 85 KDoc blocks across the module** (verified via `grep -rn "@since" foundation/src --include="*.kt" | wc -l`). Largest count of any module audited so far (core: ~26, data: ~30, design: ~21, foundation: ~85). Most show `@since 0.0.1`; a handful show `0.0.8`/`0.0.10`/`0.0.7`. SDK is alpha (criterion 3); none of the consumers track them. Drop in the wave-2 KDoc sweep. Consistent with `core` G5, `data` wave-2, `design` A1. (~10 min sweep — larger than other modules due to the 85 count.)

- **Per prompt: "every settings Option type shares the same Selectable + label/icon pattern — confirm consistency."** Verified by reading the three sealed hierarchies:
  - `ThemeOption` — `Selectable` impl, `(icon: ImageVector, name: StringResource)` + `kind: ThemeKind`. 3 entries.
  - `LanguageOption` — `Selectable` impl, `(icon: ImageVector, name: StringResource)` + `kind: LocaleKind`. 2 entries.
  - `MapOption` — `Selectable` impl, `(name: StringResource)` + `kind: MapKind`. **No icon.** 2 entries.
  - **`MapOption` overrides `Selectable.icon: ImageVector?` to its default `null`** (verified by reading `Selectable.kt:21-22`: `val icon: ImageVector? get() = null`). This is the documented carve-out (`MapOption.kt:13`: "Has no icon — only a localized name"). **Pattern is consistent** across all three; the icon-null variation is a property of map-provider options, not a rule violation.
  - All three companion objects share the `from(kind: K): Self` shape, the `all: List<Self>` accessor, and the round-trip-tested invariant. **Confirmed consistent. Keep.**

- **`runCatching { … }.onFailure { Logger.w(...) { ... } }`** appears twice in `LocationManager.kt` (lines 99-119, 130-138). Same shape. Borderline-rewrite-candidate: a `private inline fun runTrackingOp(op: String, block: suspend () -> Unit)` helper would dedupe ~10 lines, but the bodies are short and the names (`startTracking`/`stopTracking`) are self-describing. **Defer.** (~0 min)

- **Five `expect` functions** in foundation (`changeLanguage`, `getCurrentLanguage`, `isLocationServicesEnabled`, `openLocationSettings`, `createLocationTracker`). All return primitives or moko-geo types (no platform-Java/Foundation type leakage into commonMain). The `actual` impls are short (≤30 lines each on Android, ≤30 lines each on iOS). Standard KMP shape. **No restructure. Keep.** Worth noting in `## Notes` that the moko-geo `LocationTracker` *return type* is the only non-primitive `expect` return — it's a third-party platform-SDK type, not a Java/Foundation type.

### Concerns with the criteria as applied to foundation

- **Criterion 6's state-machine bar (Orbit `ContainerHost`)** doesn't apply to foundation directly — `BaseViewModel` is cross-cutting infra, not a state machine. Feature ViewModels are where the state machine lives, and they're outside foundation's scope. Mention in wave-9 verification that "`BaseViewModel` is intentionally not a `ContainerHost`; the state-machine bar applies to its subclasses in `feature/*`."

- **Criterion 11's "no try/catch in business logic"** has two pragmatic carve-outs in foundation:
  1. **`try { … } finally { … }`** in `LoadingController.kt` — resource cleanup, not error handling. Not a violation.
  2. **`runCatching { ... }.onFailure { ... }`** in `LocationManager.kt` — at the moko-geo system-API seam. Not business logic; not a violation.
  
  The prompt asked specifically about `catch (_: Exception)` in `LocationManager` for permission denial. **Verified: no bare `catch` exists in `LocationManager.kt`** — both error paths are `runCatching { ... }.onFailure { ... }` with a logged warning. Functionally equivalent to a permission-denied carve-out, but expressed in a more idiomatic (functional) form. **Sanctioned exception-to-the-rule per the pragmatic carve-out for system-API APIs.** Document the carve-out in `## Notes`.

- **Criterion 4's "no SDK-internal deps"** does NOT hold — foundation depends on `core` (an `api` declaration) and `resources` (currently `implementation`, recommended `api` promotion). After cleanup, two SDK-internal deps. This is the expected shape per the brick stack: foundation is the brick-glue layer, not a leaf. The criterion's "discover-and-document" wording is the right reading; the audit documents the deps but doesn't impose a single-leaf constraint.

- **Criterion 1 (lego test)** holds well for foundation. The single product-flavored item (the Tashkent default coordinate) is brand-anchored, not business-logic-anchored. Same posture as design's brand colors. The prompt's check for "settings option *names* that look product-specific" surfaced no candidates — `LanguageOption.Uzbek`/`Russian`, `MapOption.Google`/`Libre`, `ThemeOption.Light`/`Dark`/`System` are all generic capability names.

---

## Summary stats

- **Section 1 findings:** 27 file-level findings across 28 source files (16 commonMain + 6 platform actuals + 2 commonTest fixtures + 4 platform-specific imports). Mix of ~150-180 lines of paraphrase KDoc + **85 `@since` tags** (largest of any audited module so far) + ~15 lines of enum/data-object one-liners + 4 dead-public-surface candidates (`LocaleProvider` family, `currentLocale`, foundation's `LocationProvider` Composable, `ExtendedLocation` demote-to-internal) + 1 single-use-abstraction candidate (`DataErrorMapper` interface).
- **Section 2 findings:** **13 unused deps to drop** (`projects.design`, `compose.foundation`, `compose.material3`, `koin.compose.viewmodel`, `orbit.core`, `orbit.viewmodel`, `orbit.compose`, `kotlinx-serialization-json`, `androidx-lifecycle-viewmodel-compose`, `moko-geo-compose`, `connectivity-device`, `androidx-core-ktx`, `koin-android`). **6 wrong-scope deps to promote** to `api` (`projects.resources`, `compose.runtime`, `kotlinx.coroutines.core`, `androidx.lifecycle.runtime.compose`, `moko.geo`). **1 missing dep to add** (`androidx.lifecycle.viewmodel`, after dropping `viewmodel.compose`). Net: 17 declared → 11 declared, much cleaner graph.
- **Section 3 findings:** 0 god files (longest is 172 lines). 0 organization-only nesting. The `infra/` split debate is mooted by the bucket-2-3 deletion of `DataErrorMapper`. No restructure needed.
- **Section 4 findings:** 6 quality candidates. 1 single-use-abstraction deletion (`DataErrorMapper` interface, ~30 lines). 1 dead-CompositionLocal deletion (`foundation.location.LocationProvider` Composable, ~55 lines). 1 dead-CompositionLocal deletion (`foundation.locale.LocaleProvider` family + `currentLocale`, ~82 lines). 1 demote-to-internal (`ExtendedLocation` + public field, ~17 lines). 1 `@Immutable` consistency sweep (~7 annotations). Combined CompositionLocal sweep crosses 100 lines (~137 total) — **NEEDS GATE**. 0 architecture violations after pragmatic-carve-out review.
- **Section 5 findings:** 0 promotion, 0 demotion, 2 borderline notes (Tashkent default and `LanguageOption` Phase-3 narrowing — both keep, document in `## Notes`). The foundation/core `LocationProvider` collision is the prompt's load-bearing question — verified that core's interface is canonical, foundation's Composable is dead.
- **Section 6 findings:** 0 actionable gaps after carve-outs. ~3-4 untested public surfaces (`Modifier.staggerReveal`, `ObserveAsEvents`, `toSelectableItem`, `currentLocale` if kept) all share the compose-ui-test-harness-not-wired carve-out. The moko-geo `expect class` testability carve-out covers `LocationManager`'s commonTest gap. Wave-8 effort: **0 min unconditional + ~5 min optional**.
- **Section 7 findings:** 1 full MODULE.md rewrite + 4 stale `# Package` blurbs to drop + 1 stale "Architecture" framing paragraph to drop (matches the "Foundation serves as the intentional glue layer..." aspirational framing the prompt called out).
- **Longest single rewrite candidate:** **combined dead-CompositionLocal sweep at ~137 lines** (foundation `LocationProvider` Composable ~55 lines + `LocaleProvider` family ~57 lines + `currentLocale` ~25 lines). **Crosses the 100-line gate. NEEDS GATE.** Same shape as `core` G1 (DataError variants) and `design` G9 (motion package). Recommend Islom decide as a single decision (delete all three CompositionLocal slots together; they share the same zero-consumer diagnosis).
- **Blocking issues:** none. Audit is fully derivable from the source.

### Questions for Islom (foundation/core boundary)

These need answers before wave-2 lands:

1. **Delete the dead-CompositionLocal trio?** (foundation `LocationProvider` Composable + `LocaleProvider` family + `currentLocale`). Section 4's combined gate, ~137 lines. Recommend yes (same posture as core G1 and design G9). YallaClient does not consume any of them; YallaClient defines its own `LocaleProvider` with Activity-recreation logic foundation's lacks.

2. **Delete the `DataErrorMapper` interface and inline its body into `BaseViewModel.mapDataErrorToUserMessage`?** Section 4, ~30 lines. Single-use abstraction, no override anywhere; `protected open fun mapDataErrorToUserMessage` is the actual override seam (and matches CLAUDE.md's "fold the `when` into the `protected open` default" idiom). Public API change → `refactor!:`.

3. **Demote `ExtendedLocation` (data class) and `LocationManager.extendedLocation` (StateFlow public field) to `internal`?** Section 4, ~17 lines. Used only inside `LocationManager`; no external consumer reads it. Public API change → `refactor!:`.

4. **Apply `@Immutable` to all foundation data classes uniformly?** Section 4, ~7 annotations. Same posture as design G12.

5. **Promote `compose.runtime`, `kotlinx.coroutines.core`, `androidx.lifecycle.runtime.compose`, `projects.resources`, `moko.geo` to `api`?** Section 2. All five carry types in public signatures. Same posture as design G13 (compose.runtime + compose.ui promotion). Public API change (pom.xml deps) → `refactor!:`.

6. **Drop the 13 unused deps?** Section 2. The biggest is `projects.design` (declared but zero `import uz.yalla.design.*` lines in foundation/src) and the three Orbit deps (foundation doesn't use Orbit; the project pattern is "Orbit at the feature layer, not the foundation layer"). Default approve unless Islom planned to consume any of these in a near-term wave.

7. **`LocationManager.DEFAULT_LOCATION` (Tashkent center)** — keep as-is, document in `## Notes`. Confirm.

8. **`LanguageOption` Phase-3 narrowing to Uzbek + Russian** — keep, document in `## Notes`. Drop the "(ADR-014)" parenthetical from the KDoc per the same posture as `core/error/DataError.kt`'s ADR-022 cleanup.
