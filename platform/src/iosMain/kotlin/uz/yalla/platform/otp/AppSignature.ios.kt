package uz.yalla.platform.otp

/**
 * iOS actual for [getAppSignature].
 *
 * Always returns `null`. iOS does not require an app-specific hash for SMS autofill;
 * the system handles OTP detection natively when a `UITextField` has
 * `textContentType = .oneTimeCode`.
 */
actual fun getAppSignature(): String? = null
