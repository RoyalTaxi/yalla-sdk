package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow

public interface SessionPreferences {
    public val accessToken: Flow<String>

    public suspend fun setAccessToken(value: String)

    public val firebaseToken: Flow<String>

    public suspend fun setFirebaseToken(value: String)

    public val isGuestMode: Flow<Boolean>

    public suspend fun setGuestMode(value: Boolean)

    public val isDeviceRegistered: Flow<Boolean>

    public suspend fun setDeviceRegistered(value: Boolean)

    public suspend fun clearSession()

    public suspend fun clearAll()

    public suspend fun clearAndEnterGuestMode() {
        clearSession()
        setGuestMode(true)
    }
}
