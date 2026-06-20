package uz.yalla.foundation.system

import androidx.compose.runtime.Composable

/**
 * Sets the system-bar icon appearance for the current screen.
 *
 * [darkIcons] = `true` requests dark (light-background) icons. The scope differs by platform:
 * **Android** styles both the status bar and the navigation bar; **iOS** styles the status bar only
 * (and only when the host app is not view-controller-based status-bar managed).
 */
@Composable
public expect fun SystemBarColors(darkIcons: Boolean)
