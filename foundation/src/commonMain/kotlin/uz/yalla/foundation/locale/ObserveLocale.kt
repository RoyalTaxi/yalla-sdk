package uz.yalla.foundation.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import uz.yalla.core.settings.LocaleKind

/**
 * Returns the current locale as [LocaleKind].
 *
 * **Note:** Uses `LaunchedEffect(Unit)` internally — the value is read once at composition
 * and does NOT update if the locale changes during the composition lifecycle.
 * For reactive locale observation, use [LocaleProvider] / [currentLocaleState] instead.
 *
 * @return Current [LocaleKind] based on [getCurrentLanguage].
 * @since 0.0.1
 * @see LocaleProvider
 * @see currentLocaleState
 */
@Composable
fun currentLocale(): LocaleKind {
    var language by remember { mutableStateOf(LocaleKind.from(getCurrentLanguage())) }

    LaunchedEffect(Unit) {
        language = LocaleKind.from(getCurrentLanguage())
    }

    return language
}
