package uz.yalla.maps.compose

/**
 * Composable target marker that restricts composable content to the Google Maps composition.
 *
 * Apply this annotation to composable functions that emit map overlays (markers,
 * polylines, circles) so the compiler enforces they are only called within a
 * [GoogleMap][uz.yalla.maps.compose.GoogleMap] content lambda.
 *
 * @since 0.0.1
 */
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
)
expect annotation class GoogleMapComposable()
