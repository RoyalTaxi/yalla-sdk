package uz.yalla.capabilities.update

import androidx.compose.runtime.Composable

@Composable
expect fun rememberAppUpdateState(
    appId: String,
    countryCode: String = "uz"
): AppUpdateState
