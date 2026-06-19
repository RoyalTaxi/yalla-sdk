package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow

public interface SessionPreferences {
    public val accessToken: Flow<String>

    public fun setAccessToken(value: String)

    public val firebaseToken: Flow<String>

    public fun setFirebaseToken(value: String)

    public val isGuestMode: Flow<Boolean>

    public fun setGuestMode(value: Boolean)

    public val isDeviceRegistered: Flow<Boolean>

    public fun setDeviceRegistered(value: Boolean)

    public fun clearSession()

    public fun clearAll()

    /**
     * Clears all stored preferences and marks the session as guest, completing before it returns.
     * Use this for logout: calling `clearAll()` then `setGuestMode(true)` separately is two
     * fire-and-forget writes that race — a late clear can wipe the just-set guest flag, which hangs
     * any `isGuestMode.first { it }` wait and skips the work after it.
     *
     * Real implementations MUST override this to do it in a single atomic edit (the default below is
     * a sequential fallback adequate only for synchronous in-memory test doubles).
     */
    public suspend fun clearAndEnterGuestMode() {
        clearAll()
        setGuestMode(true)
    }
}
