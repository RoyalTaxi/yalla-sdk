package uz.yalla.foundation.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal providing access to [LocationManager].
 *
 * Must be provided at a parent composable level for child components to access.
 */
val LocalLocationManager =
    staticCompositionLocalOf<LocationManager> {
        error("LocationManager not provided. Wrap your content with LocationProvider.")
    }

/**
 * Provides [LocationManager] to the composition tree.
 *
 * ## Usage
 *
 * ```kotlin
 * LocationProvider(locationManager) {
 *     // Child composables can access locationManager via LocalLocationManager.current
 *     MyScreen()
 * }
 * ```
 *
 * @param locationManager The location manager instance to provide
 * @param content Composable content that can access the location manager
 */
@Composable
fun LocationProvider(
    locationManager: LocationManager,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalLocationManager provides locationManager,
        content = content,
    )
}

/**
 * Retrieves current [LocationManager] from composition.
 *
 * @throws IllegalStateException if called outside [LocationProvider]
 */
@Composable
fun rememberLocationManager(): LocationManager = LocalLocationManager.current
