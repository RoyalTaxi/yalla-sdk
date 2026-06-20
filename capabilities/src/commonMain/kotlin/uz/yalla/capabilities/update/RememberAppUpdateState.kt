package uz.yalla.capabilities.update

import androidx.compose.runtime.Composable

/**
 * Checks whether a newer app version is available and remembers the result as an
 * [AppUpdateState].
 *
 * On Android [appId] / [countryCode] are unused (Play decides availability); on iOS
 * they drive an App Store lookup. [countryCode] defaults to `"uz"` and only affects
 * the iOS App Store storefront queried. If the installed version can't be determined
 * the check fails closed (no update is reported), so it never triggers a spurious
 * force-update prompt.
 *
 * @param appId the application id (Android package / iOS bundle id).
 * @param countryCode iOS App Store storefront for the version lookup; defaults to `"uz"`.
 */
@Composable
public expect fun rememberAppUpdateState(
    appId: String,
    countryCode: String = "uz"
): AppUpdateState
