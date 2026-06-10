package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import uz.yalla.components.config.requireConfig

@Composable
actual fun VerificationSheet(
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
        onResend = onResend,
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
