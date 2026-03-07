package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.preferences.InterfacePreferences
import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind
import uz.yalla.core.util.orFalse

/**
 * [DataStore]-backed implementation of [InterfacePreferences].
 *
 * Manages user-facing settings: locale, theme, map provider, and onboarding state.
 * These values survive session clear (logout).
 *
 * @param dataStore shared preferences store
 * @param scope coroutine scope for write operations
 * @since 0.0.1
 */
internal class InterfacePreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
) : InterfacePreferences {
    override val localeType: Flow<LocaleKind> =
        dataStore.data.map { LocaleKind.from(it[PreferenceKeys.LOCALE_TYPE]) }

    override fun setLocaleType(value: LocaleKind) {
        scope.launch { dataStore.edit { it[PreferenceKeys.LOCALE_TYPE] = value.code } }
    }

    override val themeType: Flow<ThemeKind> =
        dataStore.data.map { ThemeKind.from(it[PreferenceKeys.THEME_TYPE]) }

    override fun setThemeType(value: ThemeKind) {
        scope.launch { dataStore.edit { it[PreferenceKeys.THEME_TYPE] = value.id } }
    }

    override val mapKind: Flow<MapKind> =
        dataStore.data.map { prefs ->
            MapKind.from(prefs[PreferenceKeys.MAP_TYPE] ?: MapKind.Google.id)
        }

    override fun setMapKind(value: MapKind) {
        scope.launch { dataStore.edit { it[PreferenceKeys.MAP_TYPE] = value.id } }
    }

    override val skipOnboarding: Flow<Boolean> =
        dataStore.data.map { it[PreferenceKeys.SKIP_ONBOARDING].orFalse() }

    override fun setSkipOnboarding(value: Boolean) {
        scope.launch { dataStore.edit { it[PreferenceKeys.SKIP_ONBOARDING] = value } }
    }
}
