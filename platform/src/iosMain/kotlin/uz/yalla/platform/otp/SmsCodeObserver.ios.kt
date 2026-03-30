package uz.yalla.platform.otp

import androidx.compose.runtime.Composable

/**
 * No-op on iOS. SMS autofill is handled natively by the system
 * when a text field has `textContentType = .oneTimeCode`.
 */
@Composable
actual fun ObserveSmsCode(onCodeReceived: (String) -> Unit) {
    // iOS handles SMS autofill natively — no custom implementation needed
}
