package uz.yalla.core.contract.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Contract for session-related persistent storage.
 *
 * Manages authentication tokens, device registration, and guest mode state.
 * All properties are reactive [Flow]s for automatic UI updates on changes.
 * Implemented in the data module using Multiplatform Settings.
 *
 * @since 0.0.1
 */
interface SessionPreferences {
    val accessToken: Flow<String>
    fun setAccessToken(value: String)
    val firebaseToken: Flow<String>
    fun setFirebaseToken(value: String)
    val isGuestMode: Flow<Boolean>
    fun setGuestMode(value: Boolean)
    val isDeviceRegistered: Flow<Boolean>
    fun setDeviceRegistered(value: Boolean)
    /** Clears all session data (tokens, guest state, device registration). */
    fun performLogout()
}
