package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind

interface InterfacePreferences {
    val localeType: Flow<LocaleKind>

    fun setLocaleType(value: LocaleKind)

    val themeType: Flow<ThemeKind>

    fun setThemeType(value: ThemeKind)

    val mapKind: Flow<MapKind>

    fun setMapKind(value: MapKind)

    val skipOnboarding: Flow<Boolean>

    fun setSkipOnboarding(value: Boolean)
}
