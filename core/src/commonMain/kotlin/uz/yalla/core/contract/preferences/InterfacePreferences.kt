package uz.yalla.core.contract.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind

/**
 * Contract for UI-related preferences.
 *
 * Controls app locale, theme, map provider, and onboarding state.
 * All properties are reactive [Flow]s for automatic UI updates.
 *
 * @since 0.0.1
 */
interface InterfacePreferences {
    val localeType: Flow<LocaleKind>

    fun setLocaleType(value: LocaleKind)

    val themeType: Flow<ThemeKind>

    fun setThemeType(value: ThemeKind)

    val mapKind: Flow<MapKind>

    fun setMapKind(value: MapKind)

    val skipOnboarding: Flow<Boolean>

    fun setSkipOnboarding(value: Boolean)

    val onboardingStage: Flow<String>

    fun setOnboardingStage(value: String)
}
