package uz.yalla.core.contract.preferences

import kotlinx.coroutines.flow.Flow

interface SessionPreferences {
    val accessToken: Flow<String>
    fun setAccessToken(value: String)
    val firebaseToken: Flow<String>
    fun setFirebaseToken(value: String)
    val isGuestMode: Flow<Boolean>
    fun setGuestMode(value: Boolean)
    val isDeviceRegistered: Flow<Boolean>
    fun setDeviceRegistered(value: Boolean)
    fun performLogout()
}
