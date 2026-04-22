package uz.yalla.foundation.locale

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Smoke tests for [changeLanguage] and [getCurrentLanguage].
 *
 * These are platform-specific `expect` functions:
 *   - Android: [changeLanguage] calls [java.util.Locale.setDefault]; [getCurrentLanguage] reads
 *     [java.util.Locale.getDefault().language].
 *   - iOS: [changeLanguage] writes to NSUserDefaults "AppleLanguages"; [getCurrentLanguage] reads
 *     from [platform.Foundation.NSLocale.currentLocale.languageCode].
 *
 * The smoke tests here run in commonTest (compiled into iosSimulatorArm64 via allTests).
 * They verify only that:
 *   1. [changeLanguage] does not throw for common ISO 639-1 codes.
 *   2. [getCurrentLanguage] returns a non-blank string.
 *
 * Note on iOS: [changeLanguage] writes the preference to NSUserDefaults but [getCurrentLanguage]
 * reads from NSLocale.currentLocale (the active system locale), so the returned code reflects the
 * simulator's current locale rather than the just-set preference. This is by design — the iOS
 * implementation requires an app restart to take effect — so we only assert non-blank, not the
 * specific code that was just set.
 *
 * Note on Android: [changeLanguage] calls Locale.setDefault which affects the JVM default for the
 * current process. The Android tests are not executed by allTests (AGP KMP library plugin has no
 * JVM unit-test runner), so these tests currently run on iOS only. An equivalent JVM smoke test
 * can be added to androidUnitTest once the module's test infrastructure supports it.
 */
class ChangeLanguageTest {

    @Test
    fun changeLanguageUzDoesNotThrow() {
        // Must not throw; on iOS writes to NSUserDefaults, on Android calls Locale.setDefault.
        changeLanguage("uz")
    }

    @Test
    fun changeLanguageRuDoesNotThrow() {
        changeLanguage("ru")
    }

    @Test
    fun changeLanguageEnDoesNotThrow() {
        changeLanguage("en")
    }

    @Test
    fun getCurrentLanguageReturnsNonBlankString() {
        val lang = getCurrentLanguage()
        assertTrue(lang.isNotBlank(), "getCurrentLanguage() must return a non-blank ISO 639-1 code, got: '$lang'")
    }

    @Test
    fun getCurrentLanguageAfterChangeIsNonBlank() {
        changeLanguage("uz")
        val lang = getCurrentLanguage()
        assertTrue(lang.isNotBlank(), "getCurrentLanguage() must return non-blank after changeLanguage(\"uz\")")
    }
}
