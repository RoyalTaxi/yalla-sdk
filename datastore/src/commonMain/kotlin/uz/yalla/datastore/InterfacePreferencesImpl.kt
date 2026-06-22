package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind
import uz.yalla.core.util.orFalse

internal class InterfacePreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) : InterfacePreferences {
    override val localeType: Flow<LocaleKind> = dataStore.readFlow { LocaleKind.from(it[PreferenceKeys.LOCALE_TYPE]) }

    override fun setLocaleType(value: LocaleKind) {
        dataStore.write(scope) { it[PreferenceKeys.LOCALE_TYPE] = value.code }
    }

    override val themeType: Flow<ThemeKind> = dataStore.readFlow { ThemeKind.from(it[PreferenceKeys.THEME_TYPE]) }

    override fun setThemeType(value: ThemeKind) {
        dataStore.write(scope) { it[PreferenceKeys.THEME_TYPE] = value.id }
    }

    override val mapKind: Flow<MapKind> = dataStore.readFlow { MapKind.from(it[PreferenceKeys.MAP_TYPE]) }

    override fun setMapKind(value: MapKind) {
        dataStore.write(scope) { it[PreferenceKeys.MAP_TYPE] = value.id }
    }

    override val skipOnboarding: Flow<Boolean> = dataStore.readFlow { it[PreferenceKeys.SKIP_ONBOARDING].orFalse() }

    override fun setSkipOnboarding(value: Boolean) {
        dataStore.write(scope) { it[PreferenceKeys.SKIP_ONBOARDING] = value }
    }
}
