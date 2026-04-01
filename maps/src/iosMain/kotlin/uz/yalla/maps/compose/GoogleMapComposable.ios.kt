package uz.yalla.maps.compose

import androidx.compose.runtime.ComposableTargetMarker

/**
 * iOS implementation of [GoogleMapComposable].
 *
 * Uses [ComposableTargetMarker] to restrict map overlay composables
 * to the Google Maps composition on iOS.
 *
 * @since 0.0.1
 */
@Retention(AnnotationRetention.BINARY)
@ComposableTargetMarker(description = "Google Map Composable")
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
)
actual annotation class GoogleMapComposable
