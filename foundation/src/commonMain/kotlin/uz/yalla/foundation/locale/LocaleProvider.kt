package uz.yalla.foundation.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import org.koin.mp.KoinPlatform
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.settings.LocaleKind

val LocalAppLocale = staticCompositionLocalOf { LocaleKind.Uz }

@Composable
fun LocaleProvider(content: @Composable () -> Unit) {
    val preferences = remember { KoinPlatform.getKoin().get<InterfacePreferences>() }
    val initialLocale = remember { LocaleKind.from(getCurrentLanguage()) }
    val locale by preferences.localeType.collectAsState(initialLocale)

    remember(locale) { changeLanguage(locale.code) }

    CompositionLocalProvider(LocalAppLocale provides locale) {
        content()
    }
}
