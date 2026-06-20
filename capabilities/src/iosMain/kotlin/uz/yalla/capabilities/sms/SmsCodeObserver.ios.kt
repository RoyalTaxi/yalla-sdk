package uz.yalla.capabilities.sms

import androidx.compose.runtime.Composable

// TODO(quality, needs-decision): M15 — this iOS actual is a guaranteed no-op (iOS has no
//  SMS-retriever API). Decide the boundary deliberately: either make SMS autofill honestly
//  Android-only (drop the common expect, expose ObserveSmsCode from androidMain), or give iOS
//  real OTP autofill via UITextContentType.oneTimeCode in the native verification field.
//  Blocked: changing/removing the public expect is a breaking API change needing owner sign-off.
@Composable
public actual fun ObserveSmsCode(
    enabled: Boolean,
    codeLength: Int,
    alphanumeric: Boolean,
    restartKey: Any?,
    onCode: (String) -> Unit
): Unit = Unit
