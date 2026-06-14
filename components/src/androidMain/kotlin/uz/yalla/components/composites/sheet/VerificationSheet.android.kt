package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import uz.yalla.capabilities.sms.ObserveSmsCode
import uz.yalla.components.config.requireConfig

@Composable
public actual fun VerificationSheet(
    isVisible: Boolean,
    code: String,
    onCodeChange: (String) -> Unit,
    codeLength: Int,
    headline: String,
    description: String,
    confirmText: String,
    onConfirm: () -> Unit,
    resendText: String,
    onResend: () -> Unit,
    onDismissRequest: () -> Unit,
    title: String?,
    isError: Boolean,
    isLoading: Boolean,
    resendEnabled: Boolean,
    alphanumeric: Boolean,
    onCodeComplete: (String) -> Unit,
    dismissEnabled: Boolean
) {
    var smsArmKey by remember { mutableIntStateOf(0) }

    ObserveSmsCode(
        enabled = isVisible,
        codeLength = codeLength,
        alphanumeric = alphanumeric,
        restartKey = smsArmKey
    ) { smsCode ->
        onCodeChange(smsCode)
        if (smsCode.length == codeLength) onCodeComplete(smsCode)
    }

    requireConfig().sheet.VerificationContent(
        isVisible = isVisible,
        code = code,
        onCodeChange = onCodeChange,
        codeLength = codeLength,
        headline = headline,
        description = description,
        confirmText = confirmText,
        onConfirm = onConfirm,
        resendText = resendText,
        onResend = {
            smsArmKey++
            onResend()
        },
        onDismissRequest = onDismissRequest,
        title = title,
        isError = isError,
        isLoading = isLoading,
        resendEnabled = resendEnabled,
        alphanumeric = alphanumeric,
        onCodeComplete = onCodeComplete,
        dismissEnabled = dismissEnabled
    )
}
