package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Contract for session-related persistent storage.
 *
 * Manages authentication tokens, device registration, and guest mode state.
 * All properties are reactive [Flow]s for automatic UI updates on changes.
 * Implemented in the data module using Multiplatform Settings.
 *
 * ## Lifecycle
 * After successful login, [accessToken] is set and [isGuestMode] becomes `false`.
 * On logout (or 401), [clearSession] resets all session state while preserving
 * interface preferences (locale, theme, map provider).
 *
 * @see StaticPreferences for synchronous access to guest mode and device registration
 * @see uz.yalla.core.session.UnauthorizedSessionEvents
 */
interface SessionPreferences {

    /**
     * Bearer token for authenticated API requests.
     *
     * Emits an empty string when not authenticated.
     */
    val accessToken: Flow<String>

    /** Persists the authentication access token; pass empty string to clear. */
    fun setAccessToken(value: String)

    /**
     * Firebase Cloud Messaging registration token.
     *
     * Used for push notification delivery targeting.
     */
    val firebaseToken: Flow<String>

    fun setFirebaseToken(value: String)

    /**
     * Whether the user is browsing without authentication.
     *
     * Guest users can view the map and search, but cannot place orders.
     */
    val isGuestMode: Flow<Boolean>

    fun setGuestMode(value: Boolean)

    /**
     * Whether this device has been registered with the backend.
     *
     * Registration sends the FCM token and device metadata to enable
     * push notifications and device-targeted messaging.
     */
    val isDeviceRegistered: Flow<Boolean>

    fun setDeviceRegistered(value: Boolean)

    /**
     * Clears all session and user data, preserving interface settings.
     *
     * Called on logout or when the server returns 401 Unauthorized.
     * Resets [accessToken], [isGuestMode], [isDeviceRegistered], and
     * all [UserPreferences] fields. Does **not** clear [InterfacePreferences].
     *
     * @see uz.yalla.core.session.UnauthorizedSessionEvents
     */
    fun clearSession()
}
