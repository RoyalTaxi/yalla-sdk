package uz.yalla.capabilities.sms

import androidx.compose.runtime.Composable

/**
 * Observes incoming OTP SMS while composed and invokes [onCode] with the extracted
 * code.
 *
 * Android only — autofills via the SMS Retriever / User Consent APIs. On iOS this is
 * a no-op (the platform offers OTP autofill natively via the keyboard, not through
 * this API).
 *
 * @param enabled when `false` no observation is started.
 * @param codeLength exact length of the code to extract (see [extractOtp]).
 * @param alphanumeric whether the code may contain letters (still requires a digit).
 * @param restartKey when its value changes, observation is torn down and re-armed
 *   (and the internal "already delivered" guard is reset) — pass the verification
 *   attempt id so a resend starts listening again.
 * @param onCode invoked once per delivery with the extracted code.
 */
@Composable
public expect fun ObserveSmsCode(
    enabled: Boolean,
    codeLength: Int,
    alphanumeric: Boolean,
    restartKey: Any?,
    onCode: (String) -> Unit
)
