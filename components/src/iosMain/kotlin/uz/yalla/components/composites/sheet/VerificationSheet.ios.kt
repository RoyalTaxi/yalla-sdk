package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import uz.yalla.components.config.requireConfig
import uz.yalla.components.platform.findKeyWindowRootController

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
    val currentOnCodeChange by rememberUpdatedState(onCodeChange)
    val currentOnConfirm by rememberUpdatedState(onConfirm)
    val currentOnResend by rememberUpdatedState(onResend)
    val currentOnCodeComplete by rememberUpdatedState(onCodeComplete)
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    val handle =
        remember {
            requireConfig().sheet.createVerification(
                code = code,
                codeLength = codeLength,
                headline = headline,
                description = description,
                confirmText = confirmText,
                resendText = resendText,
                title = title,
                isError = isError,
                isLoading = isLoading,
                resendEnabled = resendEnabled,
                dismissEnabled = dismissEnabled,
                alphanumeric = alphanumeric,
                onCodeChange = { currentOnCodeChange(it) },
                onConfirm = { currentOnConfirm() },
                onResend = { currentOnResend() },
                onCodeComplete = { currentOnCodeComplete(it) },
                onDismissRequest = { currentOnDismissRequest() }
            )
        }

    LaunchedEffect(code, description, isError, isLoading, resendText, resendEnabled) {
        handle.update(code, description, isError, isLoading, resendText, resendEnabled)
    }

    DisposableEffect(isVisible) {
        if (!isVisible) {
            return@DisposableEffect onDispose {}
        }

        val parent = findKeyWindowRootController() ?: return@DisposableEffect onDispose {}
        handle.present(parent)

        onDispose { handle.dismiss() }
    }
}
