@file:JvmName("ThemeModeKt")

package uz.yalla.foundation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.koin.mp.KoinPlatform
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.settings.ThemeKind
import kotlin.jvm.JvmName

public fun ThemeKind.resolveIsDark(systemDark: Boolean): Boolean =
    when (this) {
        ThemeKind.Light -> false
        ThemeKind.Dark -> true
        ThemeKind.System -> systemDark
    }

@Composable
public fun rememberIsDarkTheme(): Boolean {
    val preferences = remember { KoinPlatform.getKoin().get<InterfacePreferences>() }
    val theme by preferences.themeType.collectAsState(ThemeKind.System)
    return theme.resolveIsDark(isSystemInDarkTheme())
}
