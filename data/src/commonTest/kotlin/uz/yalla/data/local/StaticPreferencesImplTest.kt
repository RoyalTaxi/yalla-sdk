package uz.yalla.data.local

import com.russhwolf.settings.MapSettings
import uz.yalla.core.settings.LocaleKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StaticPreferencesImplTest {

    @Test
    fun shouldDefaultLocaleCodeToUzOnColdRead() {
        val impl = StaticPreferencesImpl(MapSettings())

        assertEquals(LocaleKind.Uz.code, impl.localeCode)
    }

    @Test
    fun shouldDefaultDeviceRegisteredToFalseOnColdRead() {
        val impl = StaticPreferencesImpl(MapSettings())

        assertFalse(impl.isDeviceRegistered)
    }

    @Test
    fun shouldDefaultGuestModeToFalseOnColdRead() {
        val impl = StaticPreferencesImpl(MapSettings())

        assertFalse(impl.isGuestMode)
    }

    @Test
    fun shouldDefaultOnboardingStageToFreshOnColdRead() {
        val impl = StaticPreferencesImpl(MapSettings())

        assertEquals(PreferenceKeys.DEFAULT_ONBOARDING_STAGE, impl.onboardingStage)
    }

    @Test
    fun shouldRoundTripLocaleCodeSynchronously() {
        val impl = StaticPreferencesImpl(MapSettings())

        impl.setLocaleCode("ru")

        assertEquals("ru", impl.localeCode)
    }

    @Test
    fun shouldRoundTripDeviceRegisteredSynchronously() {
        val impl = StaticPreferencesImpl(MapSettings())

        impl.setDeviceRegistered(true)

        assertTrue(impl.isDeviceRegistered)
    }

    @Test
    fun shouldRoundTripGuestModeSynchronously() {
        val impl = StaticPreferencesImpl(MapSettings())

        impl.setGuestMode(true)

        assertTrue(impl.isGuestMode)
    }

    @Test
    fun shouldRoundTripOnboardingStageSynchronously() {
        val impl = StaticPreferencesImpl(MapSettings())

        impl.setOnboardingStage("COMPLETED")

        assertEquals("COMPLETED", impl.onboardingStage)
    }

    @Test
    fun shouldOverwritePreviousLocaleCodeOnSuccessiveSets() {
        val impl = StaticPreferencesImpl(MapSettings())

        impl.setLocaleCode("ru")
        impl.setLocaleCode("en")

        assertEquals("en", impl.localeCode)
    }

    @Test
    fun shouldUsePrefixedKeysToAvoidCollisionsWithCallerSettings() {
        // Document the contract that StaticPreferencesImpl owns keys prefixed
        // with "startup_". A caller who inserts a same-named but unprefixed key
        // into the shared Settings must not accidentally flip state.
        val settings = MapSettings()
        settings.putString("locale", "RU") // unprefixed — caller's namespace
        settings.putBoolean("guest_mode", true) // unprefixed
        val impl = StaticPreferencesImpl(settings)

        assertEquals(LocaleKind.Uz.code, impl.localeCode)
        assertFalse(impl.isGuestMode)
    }

    @Test
    fun shouldShareStateAcrossInstancesBackedBySameSettings() {
        // Two impls over the same Settings instance must see each other's writes.
        // This is the dual-write contract exercised by
        // SessionPreferencesImpl.setGuestMode.
        val settings = MapSettings()
        val writer = StaticPreferencesImpl(settings)
        val reader = StaticPreferencesImpl(settings)

        writer.setGuestMode(true)
        writer.setOnboardingStage("IN_PROGRESS")

        assertTrue(reader.isGuestMode)
        assertEquals("IN_PROGRESS", reader.onboardingStage)
    }
}
