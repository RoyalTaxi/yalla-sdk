package uz.yalla.core.contract.preferences

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
 * @since 0.0.1
 */
interface InterfacePreferences {

    /** Current app locale. Drives string resource selection and RTL layout. */
    val localeType: Flow<LocaleKind>

    /**
     * Persists the selected app locale.
     *
     * @param value The locale to apply
     * @see LocaleKind
     */
    fun setLocaleType(value: LocaleKind)

    /** Current app theme (light, dark, or system). */
    val themeType: Flow<ThemeKind>

    /**
     * Persists the selected app theme.
     *
     * @param value The theme to apply
     * @see ThemeKind
     */
    fun setThemeType(value: ThemeKind)

    /** Current map rendering provider (Google Maps or MapLibre). */
    val mapKind: Flow<MapKind>

    /**
     * Persists the selected map provider.
     *
     * @param value The map provider to use
     * @see MapKind
     */
    fun setMapKind(value: MapKind)

    /** Whether to bypass the onboarding flow entirely. */
    val skipOnboarding: Flow<Boolean>

    /**
     * Persists the onboarding skip preference.
     *
     * @param value `true` to skip onboarding on next app launch
     */
    fun setSkipOnboarding(value: Boolean)

    /**
     * Current onboarding progress stage identifier.
     *
     * Used to resume onboarding from where the user left off.
     */
    val onboardingStage: Flow<String>

    /**
     * Persists the onboarding progress stage.
     *
     * @param value Stage identifier string
     */
    fun setOnboardingStage(value: String)
}
