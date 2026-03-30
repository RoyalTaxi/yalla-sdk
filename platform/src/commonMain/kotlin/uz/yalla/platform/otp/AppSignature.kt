package uz.yalla.platform.otp

/**
 * Gets the app signature hash required by Android's SMS Retriever API.
 *
 * The SMS sent by the server must contain this hash for the SMS Retriever API
 * to automatically detect it. Returns `null` on iOS or if the hash cannot be computed.
 *
 * @since 0.0.6-alpha05
 */
expect fun getAppSignature(): String?
