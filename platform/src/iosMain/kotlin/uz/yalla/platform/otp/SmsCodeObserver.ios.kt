package uz.yalla.platform.otp

import androidx.compose.runtime.Composable

/**
 * iOS actual for [ObserveSmsCode].
 *
 * No-op implementation. On iOS, SMS autofill is handled natively by the system keyboard
 * when a `UITextField` has `textContentType = .oneTimeCode`. The [onCodeReceived] callback
 * is never invoked.
 */
@Composable
actual fun ObserveSmsCode(onCodeReceived: (String) -> Unit) {
    // iOS handles SMS autofill natively — no custom implementation needed
}
