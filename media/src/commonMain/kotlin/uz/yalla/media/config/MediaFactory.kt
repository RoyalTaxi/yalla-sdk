package uz.yalla.media.config

/**
 * Native bridge the host installs via [YallaMedia.install]. Shared code drives the picker/camera
 * through this seam; each platform supplies its own image-handle type (Android `Uri`, iOS `NSData`),
 * which is why the interface is `expect`/`actual` rather than a single shared declaration.
 *
 * Both operations are fire-and-forget: the result is delivered to the supplied callback on the main
 * thread, an empty list / `null` on cancel. There is no `present`/`dismiss` lifecycle to expose, so —
 * unlike the other SDK factories — this one intentionally returns callbacks rather than a
 * `{Concept}Handle` (see README §6).
 */
public expect interface MediaFactory
