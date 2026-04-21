package uz.yalla.data.local

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [SessionPreferencesImpl].
 *
 * Uses [UnconfinedTestDispatcher] so `scope.launch { dataStore.edit { ... } }`
 * runs eagerly: by the time a setter returns, the in-memory DataStore has
 * observed the write, and the next assertion line reads the settled state.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SessionPreferencesImplTest {

    @Test
    fun shouldReturnEmptyAccessTokenOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals("", impl.accessToken.first())
    }

    @Test
    fun shouldPropagateSetAccessTokenToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setAccessToken("token-abc")

        assertEquals("token-abc", impl.accessToken.first())
    }

    @Test
    fun shouldReturnEmptyFirebaseTokenOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals("", impl.firebaseToken.first())
    }

    @Test
    fun shouldPropagateSetFirebaseTokenToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setFirebaseToken("fcm-token")

        assertEquals("fcm-token", impl.firebaseToken.first())
    }

    @Test
    fun shouldReturnFalseGuestModeOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertFalse(impl.isGuestMode.first())
    }

    @Test
    fun shouldPropagateSetGuestModeToDataStoreFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setGuestMode(true)

        assertTrue(impl.isGuestMode.first())
    }

    @Test
    fun shouldDualWriteGuestModeToStaticPreferencesSynchronously() = runTest(UnconfinedTestDispatcher()) {
        val staticPrefs = StaticPreferencesImpl(MapSettings())
        val impl = newImpl(this, staticPreferences = staticPrefs)

        // StaticPreferences is synchronous — the value is readable immediately,
        // regardless of whether the DataStore write has dispatched yet.
        impl.setGuestMode(true)

        assertTrue(staticPrefs.isGuestMode)
    }

    @Test
    fun shouldClearStaticGuestAndDeviceOnClearSession() = runTest(UnconfinedTestDispatcher()) {
        val staticPrefs = StaticPreferencesImpl(MapSettings())
        staticPrefs.setGuestMode(true)
        staticPrefs.setDeviceRegistered(true)
        val impl = newImpl(this, staticPreferences = staticPrefs)

        impl.clearSession()

        assertFalse(staticPrefs.isGuestMode)
        assertFalse(staticPrefs.isDeviceRegistered)
    }

    @Test
    fun shouldReturnFalseDeviceRegisteredOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertFalse(impl.isDeviceRegistered.first())
    }

    @Test
    fun shouldDualWriteDeviceRegisteredToStaticPreferencesSynchronously() = runTest(UnconfinedTestDispatcher()) {
        val staticPrefs = StaticPreferencesImpl(MapSettings())
        val impl = newImpl(this, staticPreferences = staticPrefs)

        impl.setDeviceRegistered(true)

        assertTrue(staticPrefs.isDeviceRegistered)
    }

    @Test
    fun shouldClearSessionKeysOnDataStore() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)
        impl.setAccessToken("token")
        impl.setFirebaseToken("fcm")
        impl.setGuestMode(true)
        impl.setDeviceRegistered(true)

        impl.clearSession()

        assertEquals("", impl.accessToken.first())
        assertEquals("", impl.firebaseToken.first())
        assertFalse(impl.isGuestMode.first())
        assertFalse(impl.isDeviceRegistered.first())
    }

    @Test
    fun shouldOverwritePreviousTokenOnSuccessiveSets() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setAccessToken("first")
        impl.setAccessToken("second")

        assertEquals("second", impl.accessToken.first())
    }

    private fun newImpl(
        scope: TestScope,
        staticPreferences: StaticPreferencesImpl = StaticPreferencesImpl(MapSettings()),
    ): SessionPreferencesImpl = SessionPreferencesImpl(
        dataStore = InMemoryDataStore(),
        scope = scope.backgroundScope,
        staticPreferences = staticPreferences,
    )
}
