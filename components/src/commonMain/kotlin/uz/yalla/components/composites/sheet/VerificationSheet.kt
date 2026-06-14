package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable

@Composable
public expect fun VerificationSheet(
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
    title: String? = null,
    isError: Boolean = false,
    isLoading: Boolean = false,
    resendEnabled: Boolean = true,
    alphanumeric: Boolean = false,
    onCodeComplete: (String) -> Unit = {},
    dismissEnabled: Boolean = true
)
