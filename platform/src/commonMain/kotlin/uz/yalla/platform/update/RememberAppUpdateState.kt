package uz.yalla.platform.update

import androidx.compose.runtime.Composable

/**
 * Creates and remembers an [AppUpdateState] that checks the store for updates.
 *
 * On iOS, queries the App Store lookup API. On Android, uses the Play Core
 * in-app update API. The check runs once when the composable enters composition.
 *
 * @param appId Application identifier (bundle ID on iOS, package name on Android).
 * @param countryCode ISO 3166-1 alpha-2 country code for the store region. Default `"uz"`.
 * @return An [AppUpdateState] whose properties update as the check completes.
 * @since 0.0.1
 */
@Composable
expect fun rememberAppUpdateState(
    appId: String,
    countryCode: String = "uz"
): AppUpdateState
