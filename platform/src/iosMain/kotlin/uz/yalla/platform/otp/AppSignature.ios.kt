package uz.yalla.platform.otp

/**
 * No app signature needed on iOS.
 * SMS autofill works without any app-specific hash.
 */
actual fun getAppSignature(): String? = null
