package uz.yalla.foundation.locale

/**
 * Persists [languageCode] as the app's preferred UI language and makes it the value a subsequent
 * [getCurrentLanguage] reads back.
 *
 * The set/get pair is symmetric on both platforms: after `changeLanguage(x)`, `getCurrentLanguage()`
 * returns `x`. The mechanism differs by platform but the observable contract does not:
 * - **Android** — `Locale.setDefault`, effective immediately and process-wide.
 * - **iOS** — writes the `AppleLanguages` user-defaults override (read back by [getCurrentLanguage]).
 *   The system bundle's own localization only re-resolves on the next launch; in-session
 *   re-localization is driven by Compose recomposition (see `LocaleProvider`), not by this call.
 *
 * [languageCode] is a BCP-47 primary language subtag (e.g. `"uz"`, `"ru"`).
 */
public expect fun changeLanguage(languageCode: String)

/**
 * Returns the app's current preferred UI language as a non-blank, lowercased BCP-47 primary
 * language subtag (e.g. `"uz"`, `"ru"`).
 *
 * Reads back whatever [changeLanguage] last persisted, falling back to the product default
 * (`LocaleKind.Uz.code`) when no preference is resolvable, so the return is never blank on either
 * platform.
 */
public expect fun getCurrentLanguage(): String
