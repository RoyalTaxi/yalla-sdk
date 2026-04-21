package uz.yalla.data.local

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [InterfacePreferencesImpl].
 *
 * Uses [UnconfinedTestDispatcher] so `scope.launch { dataStore.edit { ... } }`
 * runs eagerly — by the time a setter returns, the in-memory DataStore has
 * observed the write.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class InterfacePreferencesImplTest {

    @Test
    fun shouldDefaultLocaleToUzOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals(LocaleKind.Uz, impl.localeType.first())
    }

    @Test
    fun shouldPropagateSetLocaleTypeToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setLocaleType(LocaleKind.Ru)

        assertEquals(LocaleKind.Ru, impl.localeType.first())
    }

    @Test
    fun shouldDualWriteLocaleCodeToStaticPreferencesSynchronously() = runTest(UnconfinedTestDispatcher()) {
        val staticPrefs = StaticPreferencesImpl(MapSettings())
        val impl = newImpl(this, staticPreferences = staticPrefs)

        impl.setLocaleType(LocaleKind.En)

        // StaticPreferences writes are synchronous: the code is readable even
        // before the DataStore write dispatches.
        assertEquals(LocaleKind.En.code, staticPrefs.localeCode)
    }

    @Test
    fun shouldDefaultThemeToSystemOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals(ThemeKind.System, impl.themeType.first())
    }

    @Test
    fun shouldPropagateSetThemeTypeToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setThemeType(ThemeKind.Dark)

        assertEquals(ThemeKind.Dark, impl.themeType.first())
    }

    @Test
    fun shouldDefaultMapKindToGoogleOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals(MapKind.Google, impl.mapKind.first())
    }

    @Test
    fun shouldPropagateSetMapKindToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setMapKind(MapKind.Libre)

        assertEquals(MapKind.Libre, impl.mapKind.first())
    }

    @Test
    fun shouldDefaultSkipOnboardingToFalseOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertFalse(impl.skipOnboarding.first())
    }

    @Test
    fun shouldPropagateSetSkipOnboardingToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setSkipOnboarding(true)

        assertTrue(impl.skipOnboarding.first())
    }

    @Test
    fun shouldDefaultOnboardingStageToFreshOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals(PreferenceKeys.DEFAULT_ONBOARDING_STAGE, impl.onboardingStage.first())
    }

    @Test
    fun shouldPropagateSetOnboardingStageToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setOnboardingStage("COMPLETED")

        assertEquals("COMPLETED", impl.onboardingStage.first())
    }

    @Test
    fun shouldDualWriteOnboardingStageToStaticPreferencesSynchronously() = runTest(UnconfinedTestDispatcher()) {
        val staticPrefs = StaticPreferencesImpl(MapSettings())
        val impl = newImpl(this, staticPreferences = staticPrefs)

        impl.setOnboardingStage("IN_PROGRESS")

        assertEquals("IN_PROGRESS", staticPrefs.onboardingStage)
    }

    private fun newImpl(
        scope: TestScope,
        staticPreferences: StaticPreferencesImpl = StaticPreferencesImpl(MapSettings()),
    ): InterfacePreferencesImpl = InterfacePreferencesImpl(
        dataStore = InMemoryDataStore(),
        scope = scope.backgroundScope,
        staticPreferences = staticPreferences,
    )
}
