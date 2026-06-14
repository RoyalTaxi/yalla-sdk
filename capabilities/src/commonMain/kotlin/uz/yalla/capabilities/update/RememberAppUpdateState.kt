package uz.yalla.capabilities.update

import androidx.compose.runtime.Composable

@Composable
public expect fun rememberAppUpdateState(
    appId: String,
    countryCode: String = "uz"
): AppUpdateState
