package uz.yalla.core.preferences

interface StaticPreferences {
    val localeCode: String

    val isDeviceRegistered: Boolean

    val isGuestMode: Boolean

    fun setLocaleCode(value: String)

    fun setDeviceRegistered(value: Boolean)

    fun setGuestMode(value: Boolean)
}
