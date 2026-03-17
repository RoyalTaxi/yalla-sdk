package uz.yalla.foundation.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal providing access to [LocationManager].
 *
 * Must be provided at a parent composable level for child components to access.
 *
 * @since 0.0.1
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
 * @since 0.0.1
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
 * Returns the current [LocationManager] from composition.
 *
 * @throws IllegalStateException if called outside [LocationProvider]
 * @since 0.0.1
 */
@Composable
fun currentLocationManager(): LocationManager = LocalLocationManager.current
