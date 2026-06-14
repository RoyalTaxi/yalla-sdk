package uz.yalla.core.preferences

public interface StaticPreferences {
    public val localeCode: String

    public val isDeviceRegistered: Boolean

    public val isGuestMode: Boolean

    public fun setLocaleCode(value: String)

    public fun setDeviceRegistered(value: Boolean)

    public fun setGuestMode(value: Boolean)
}
