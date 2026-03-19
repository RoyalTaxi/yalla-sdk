package uz.yalla.media.gallery

/**
 * Marks gallery APIs that are experimental and subject to change or removal.
 *
 * Usages of annotated declarations will produce a compiler warning unless the call site
 * also opts in with `@ExperimentalYallaGalleryApi` or `@OptIn(ExperimentalYallaGalleryApi::class)`.
 *
 * @since 0.0.1
 */
@RequiresOptIn(
    message = "This is an experimental API that is subject to change or removal.",
    level = RequiresOptIn.Level.WARNING
)
annotation class ExperimentalYallaGalleryApi
