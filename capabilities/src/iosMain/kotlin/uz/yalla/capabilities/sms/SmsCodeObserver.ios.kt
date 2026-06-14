package uz.yalla.capabilities.sms

import androidx.compose.runtime.Composable

@Composable
public actual fun ObserveSmsCode(
    enabled: Boolean,
    codeLength: Int,
    alphanumeric: Boolean,
    restartKey: Any?,
    onCode: (String) -> Unit
): Unit = Unit
