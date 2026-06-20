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

    // TODO(quality, needs-decision): M6 — these credential mutators are fire-and-forget (non-suspend),
    // so callers cannot await durable persistence or order a token write before a dependent request
    // (the same race clearAndEnterGuestMode documents). Making them `suspend` is the fix, but it is a
    // breaking .api change with many external consumers (datastore impl, auth/profile/home VMs, test
    // fakes). Needs owner sign-off + a coordinated multi-module migration.
    /** Persists the access token. Fire-and-forget: completion is not awaitable (see the M6 TODO). */
    public fun setAccessToken(value: String)

    /** The Firebase/push token, or empty when none. */
    public val firebaseToken: Flow<String>

    /** Persists the Firebase/push token. Fire-and-forget (see the M6 TODO). */
    public fun setFirebaseToken(value: String)

    /** Whether the app is in guest (not signed-in) mode. */
    public val isGuestMode: Flow<Boolean>

    /** Sets the guest-mode flag. Fire-and-forget (see the M6 TODO). */
    public fun setGuestMode(value: Boolean)

    /** Whether this device has completed push registration. */
    public val isDeviceRegistered: Flow<Boolean>

    /** Sets the device-registration flag. Fire-and-forget (see the M6 TODO). */
    public fun setDeviceRegistered(value: Boolean)

    /**
     * Clears the session: both credential tokens, the guest/device flags, and the cached
     * profile/config values — but PRESERVES the user-experience prefs (locale, theme, map style,
     * onboarding-skip, last map/GPS position). This is the intended logout reset: the user keeps
     * their language and theme across a re-login.
     */
    public fun clearSession()

    /**
     * Clears EVERYTHING this store holds, including the user-experience prefs (locale, theme, map
     * style, onboarding) that [clearSession] preserves. Use this only for a full factory reset, not
     * a normal logout — dropping the user's language on logout is a regression.
     */
    public fun clearAll()

    /**
     * Clears the session and marks it as guest, completing before it returns. Use this for logout:
     * calling the clear and `setGuestMode(true)` separately is two fire-and-forget writes that race
     * — a late clear can wipe the just-set guest flag, which hangs any `isGuestMode.first { it }`
     * wait and skips the work after it.
     *
     * Logout SHOULD preserve the user-experience prefs (locale, theme, map style, onboarding) — the
     * set [clearSession] keeps — so the user's language is not reset. See the TODO below: the
     * production datastore override currently wipes them via a full clear.
     *
     * Real implementations MUST override this to do it in a single atomic edit (the default below is
     * a sequential fallback adequate only for synchronous in-memory test doubles).
     */
    // TODO(quality, needs-decision): logout reset divergence — the production datastore override
    // (datastore/SessionPreferencesImpl.clearAndEnterGuestMode) does a full prefs.clear(), wiping the
    // user's locale/theme/map style/onboarding on logout; the intended "keep" set is the one
    // clearSession() preserves. The behavioural fix lives in the datastore module, which is outside
    // this module's scope. This default is left calling clearAll() to keep the committed in-memory
    // contract (consumer ProfileLogoutTest pins "clearAll" + "setGuestMode(true)") stable until the
    // datastore change lands as one coordinated edit.
    public suspend fun clearAndEnterGuestMode() {
        clearAll()
        setGuestMode(true)
    }
}
