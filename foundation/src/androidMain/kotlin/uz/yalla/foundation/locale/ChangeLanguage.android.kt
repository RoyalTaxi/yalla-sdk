package uz.yalla.foundation.locale

import android.content.Context
import org.koin.core.context.GlobalContext
import uz.yalla.core.settings.LocaleKind
import java.util.Locale

private const val LOCALE_PREFERENCES = "yalla_locale"
private const val LANGUAGE_CODE_KEY = "language_code"

public actual fun changeLanguage(languageCode: String) {
    val language = LocaleKind.from(languageCode).code
    Locale.setDefault(Locale.forLanguageTag(language))
    languagePreferences()?.edit()?.putString(LANGUAGE_CODE_KEY, language)?.apply()
}

public actual fun getCurrentLanguage(): String =
    languagePreferences()
        ?.getString(LANGUAGE_CODE_KEY, null)
        ?.takeIf { it.isNotBlank() }
        ?: Locale.getDefault().language.takeIf { it.isNotBlank() }
        ?: LocaleKind.Uz.code

private fun languagePreferences() =
    runCatching {
        GlobalContext
            .get()
            .get<Context>()
            .applicationContext
            .getSharedPreferences(LOCALE_PREFERENCES, Context.MODE_PRIVATE)
    }.getOrNull()
