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
}
