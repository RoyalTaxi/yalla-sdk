package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind

public interface InterfacePreferences {
    public val localeType: Flow<LocaleKind>

    public fun setLocaleType(value: LocaleKind)

    public val themeType: Flow<ThemeKind>

    public fun setThemeType(value: ThemeKind)

    public val mapKind: Flow<MapKind>

    public fun setMapKind(value: MapKind)

    public val skipOnboarding: Flow<Boolean>

    public fun setSkipOnboarding(value: Boolean)
}
