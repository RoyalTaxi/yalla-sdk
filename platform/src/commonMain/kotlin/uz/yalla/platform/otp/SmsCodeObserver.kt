package uz.yalla.platform.otp

import androidx.compose.runtime.Composable

/**
 * Observes incoming SMS messages for OTP codes.
 *
 * On Android, uses the SMS Retriever API to automatically detect and extract
 * verification codes from incoming SMS messages. On iOS, this is a no-op since
 * iOS handles SMS autofill natively via `textContentType = .oneTimeCode`.
 *
 * @param onCodeReceived Called with the raw SMS message body when an SMS is received.
 *   The caller should filter and extract the numeric code.
 * @since 0.0.6-alpha05
 */
@Composable
expect fun ObserveSmsCode(onCodeReceived: (String) -> Unit)
