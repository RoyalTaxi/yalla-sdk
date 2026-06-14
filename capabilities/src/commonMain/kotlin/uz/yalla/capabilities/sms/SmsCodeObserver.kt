package uz.yalla.capabilities.sms

import androidx.compose.runtime.Composable

@Composable
public expect fun ObserveSmsCode(
    enabled: Boolean,
    codeLength: Int,
    alphanumeric: Boolean,
    restartKey: Any?,
    onCode: (String) -> Unit
)
