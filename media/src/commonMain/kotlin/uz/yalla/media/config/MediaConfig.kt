package uz.yalla.media.config

/**
 * Install-time configuration for the media SDK, built via [Builder] and registered with
 * [YallaMedia.install]. Carries the native [factory] the shared picker/camera launchers drive.
 *
 * This type is platform-agnostic; only [MediaFactory]'s image-handle types differ per platform, so
 * the config lives once in `commonMain` (mirroring `maps/config/MapsConfig.kt`).
 *
 * @property factory the installed native bridge for picking and capturing images.
 */
public class MediaConfig private constructor(
    public val factory: MediaFactory
) {
    /** Builder for [MediaConfig]; [factory] is required. */
    public class Builder {
        public var factory: MediaFactory? = null

        public fun build(): MediaConfig =
            MediaConfig(
                factory = requireNotNull(factory) { "MediaConfig.factory required" }
            )
    }
}
