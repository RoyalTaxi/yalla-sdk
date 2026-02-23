package uz.yalla.components.foundation.reactive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import uz.yalla.components.foundation.locale.getCurrentLanguage
import uz.yalla.core.kind.LocaleKind

/**
 * Remembers and observes the current locale, triggering recomposition on change.
 *
 * @return Current [LocaleKind]
 */
@Composable
fun rememberCurrentLanguage(): LocaleKind {
    var language by remember { mutableStateOf(LocaleKind.from(getCurrentLanguage())) }

    LaunchedEffect(Unit) {
        language = LocaleKind.from(getCurrentLanguage())
    }

    return language
}
