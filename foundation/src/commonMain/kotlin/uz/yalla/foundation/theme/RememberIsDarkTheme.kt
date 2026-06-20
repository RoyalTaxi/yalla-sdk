// JvmName preserves the original `ThemeModeKt` facade so renaming the file (M3: file == declaration)
// is not a breaking change to the committed Android ABI (foundation.api).
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

/**
 * The single source of truth for "should the UI render dark?" given a [ThemeKind] and the current
 * system dark-mode flag. [ThemeKind.System] defers to [systemDark]; [ThemeKind.Light]/[ThemeKind.Dark]
 * are absolute. Callers that already hold a [ThemeKind] (maps, the iOS bridge) resolve through this
 * one rule instead of re-deriving it.
 */
public fun ThemeKind.resolveIsDark(systemDark: Boolean): Boolean =
    when (this) {
        ThemeKind.Light -> false
        ThemeKind.Dark -> true
        ThemeKind.System -> systemDark
    }

/**
 * Observes the user's persisted [ThemeKind] from [InterfacePreferences] (resolved from the SDK's Koin
 * graph, which must be started first) and resolves it to a dark/light boolean via [resolveIsDark],
 * recomposing when the preference or the system dark-mode setting changes.
 */
@Composable
public fun rememberIsDarkTheme(): Boolean {
    val preferences = remember { KoinPlatform.getKoin().get<InterfacePreferences>() }
    val theme by preferences.themeType.collectAsState(ThemeKind.System)
    return theme.resolveIsDark(isSystemInDarkTheme())
}
