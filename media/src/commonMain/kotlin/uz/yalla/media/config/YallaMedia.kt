package uz.yalla.media.config

import uz.yalla.media.config.YallaMedia.current
import uz.yalla.media.config.YallaMedia.install
import kotlin.concurrent.Volatile

/**
 * Process-wide entry point holding the installed [MediaConfig]. Call [install] exactly once at app
 * start, before any picker or camera is launched; [current] reads it back. The backing field is
 * `@Volatile` so a read on a non-init thread (e.g. composition) observes the [install] write under
 * the JVM/Native memory model.
 *
 * [install] is single-shot: the [MediaFactory] it carries is the trusted bridge that receives the
 * user's image bytes, so a second [install] is a loud failure rather than a silent re-assignment.
 */
public object YallaMedia {
    @Volatile
    private var config: MediaConfig? = null

    /**
     * Installs the global [MediaConfig]. Must be called exactly once at app start.
     *
     * @throws IllegalStateException if a config is already installed.
     */
    public fun install(config: MediaConfig) {
        check(this.config == null) {
            "YallaMedia already installed. Call YallaMedia.install(...) exactly once at app start."
        }
        this.config = config
    }

    /** Returns the installed [MediaConfig], or throws if [install] has not been called. */
    public fun current(): MediaConfig =
        config
            ?: error("YallaMedia not installed. Call YallaMedia.install(...) at app start.")
}

internal fun requireMedia(): MediaConfig = YallaMedia.current()
