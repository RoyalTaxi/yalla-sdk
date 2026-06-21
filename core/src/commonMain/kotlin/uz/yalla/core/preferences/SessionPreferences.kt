package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Durable store for the session/auth credentials and lifecycle flags.
 *
 * The two clears differ in what they remove (see [clearSession] / [clearAll]): both wipe every
 * credential, but [clearSession] preserves the user-experience prefs (locale, theme, ...) while
 * [clearAll] is a full factory reset. Logout should preserve those prefs.
 */
public interface SessionPreferences {
    /** The bearer access token, or empty when signed out. */
    public val accessToken: Flow<String>

    /** Persists the access token and returns only after the encrypted write is durable. */
    public suspend fun setAccessToken(value: String)

    /** The Firebase/push token, or empty when none. */
    public val firebaseToken: Flow<String>

    /** Persists the Firebase/push token and returns only after the encrypted write is durable. */
    public suspend fun setFirebaseToken(value: String)

    /** Whether the app is in guest (not signed-in) mode. */
    public val isGuestMode: Flow<Boolean>

    /** Sets the guest-mode flag and returns only after the write is durable. */
    public suspend fun setGuestMode(value: Boolean)

    /** Whether this device has completed push registration. */
    public val isDeviceRegistered: Flow<Boolean>

    /** Sets the device-registration flag and returns only after the write is durable. */
    public suspend fun setDeviceRegistered(value: Boolean)

    /**
     * Clears the session: both credential tokens, the guest/device flags, and the cached
     * profile/config values — but PRESERVES the user-experience prefs (locale, theme, map style,
     * onboarding-skip, last map/GPS position). This is the intended logout reset: the user keeps
     * their language and theme across a re-login.
     */
    public suspend fun clearSession()

    /**
     * Clears EVERYTHING this store holds, including the user-experience prefs (locale, theme, map
     * style, onboarding) that [clearSession] preserves. Use this only for a full factory reset, not
     * a normal logout — dropping the user's language on logout is a regression.
     */
    public suspend fun clearAll()

    /**
     * Clears the session and marks it as guest, completing before it returns. Use this for logout:
     * calling the clear and `setGuestMode(true)` separately is two fire-and-forget writes that race
     * — a late clear can wipe the just-set guest flag, which hangs any `isGuestMode.first { it }`
     * wait and skips the work after it.
     *
     * Logout SHOULD preserve the user-experience prefs (locale, theme, map style, onboarding) — the
     * set [clearSession] keeps — so the user's language is not reset.
     *
     * Real implementations MUST override this to do it in a single atomic edit (the default below is
     * a sequential fallback adequate only for synchronous in-memory test doubles).
     */
    public suspend fun clearAndEnterGuestMode() {
        clearSession()
        setGuestMode(true)
    }
}
