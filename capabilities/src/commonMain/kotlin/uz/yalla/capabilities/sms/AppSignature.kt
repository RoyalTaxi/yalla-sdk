package uz.yalla.capabilities.sms

/**
 * Returns the app's SMS-retriever signature hash (the 11-char Base64 hash used in
 * the SMS Retriever API), or `null` if it can't be computed.
 *
 * Android only — always returns `null` on iOS, which has no equivalent app-signature
 * concept. The Android value is computed once and cached.
 */
public expect fun getAppSignature(): String?
