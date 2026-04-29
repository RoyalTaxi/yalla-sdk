package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind

/**
 * Contract for UI-related preferences.
 *
 * Controls app locale, theme, map provider, and onboarding state.
 * All properties are reactive [Flow]s that emit the latest value
 * whenever the setting changes, enabling automatic UI recomposition.
 *
 * Implemented in the data module using Multiplatform Settings.
 *
 * @see StaticPreferences for synchronous access to critical settings
 * @see LocaleKind
 * @see ThemeKind
 * @see MapKind
 */
interface InterfacePreferences {

    /** Current app locale. Drives string resource selection and RTL layout. */
    val localeType: Flow<LocaleKind>

    fun setLocaleType(value: LocaleKind)

    val themeType: Flow<ThemeKind>
    fun setThemeType(value: ThemeKind)

    val mapKind: Flow<MapKind>
    fun setMapKind(value: MapKind)

    val skipOnboarding: Flow<Boolean>
    fun setSkipOnboarding(value: Boolean)

    /**
     * Current onboarding progress stage identifier.
     *
     * Used to resume onboarding from where the user left off.
     */
    val onboardingStage: Flow<String>

    fun setOnboardingStage(value: String)
}
