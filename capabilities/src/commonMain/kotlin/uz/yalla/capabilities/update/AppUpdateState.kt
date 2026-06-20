package uz.yalla.capabilities.update

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Observable result of an app-update check. Produced by [rememberAppUpdateState].
 *
 * While [isChecking] is `true` the check is in flight. Once it completes,
 * [isUpdateAvailable] reports whether a newer version exists and, if so, [storeUrl]
 * is the store URL to open (empty otherwise).
 */
// TODO(quality, needs-decision): M2/M3 — collapse the 3 parallel mutable fields + ""
//  sentinel into a sealed AppUpdateStatus { Checking; UpToDate; Available(storeUrl) } and
//  deepen the abstraction (expose launchUpdate(launcher)) so the app stops re-querying Play.
//  Blocked: breaking change to the published AppUpdateState API + requires coordinated edits
//  to the app consumer (AppGates.kt, another module) — needs owner sign-off.
@Stable
public class AppUpdateState {
    /** `true` when a newer version is available (valid once [isChecking] is `false`). */
    public var isUpdateAvailable: Boolean by mutableStateOf(false)
        internal set

    /** Store URL to open the update, or empty when no update is available. */
    public var storeUrl: String by mutableStateOf("")
        internal set

    /** `true` while the update check is still in flight. */
    public var isChecking: Boolean by mutableStateOf(true)
        internal set
}
