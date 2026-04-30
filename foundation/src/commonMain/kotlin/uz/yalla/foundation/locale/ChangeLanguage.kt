package uz.yalla.foundation.locale

/**
 * Sets the application locale to [languageCode] (BCP-47 language tag, e.g.
 * `"uz"`, `"ru"`).
 *
 * **Android limitation:** this calls `Locale.setDefault` only. Visible UI
 * does not update until the Activity is recreated or
 * `AppCompatDelegate.setApplicationLocales` is called by the host app.
 * **iOS:** delegates to the system locale machinery; behavior depends on the
 * OS version.
 */
expect fun changeLanguage(languageCode: String)

/**
 * Returns the application's currently active language code (BCP-47 language
 * tag, e.g. `"uz"`, `"ru"`). May return the platform default if no override
 * has been set via [changeLanguage].
 */
expect fun getCurrentLanguage(): String
