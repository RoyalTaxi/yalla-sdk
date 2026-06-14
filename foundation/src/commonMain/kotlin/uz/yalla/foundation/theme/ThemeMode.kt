package uz.yalla.foundation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.koin.mp.KoinPlatform
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.settings.ThemeKind

@Composable
public fun rememberIsDarkTheme(): Boolean {
    val preferences = remember { KoinPlatform.getKoin().get<InterfacePreferences>() }
    val theme by preferences.themeType.collectAsState(ThemeKind.System)
    return when (theme) {
        ThemeKind.Light -> false
        ThemeKind.Dark -> true
        ThemeKind.System -> isSystemInDarkTheme()
    }
}
