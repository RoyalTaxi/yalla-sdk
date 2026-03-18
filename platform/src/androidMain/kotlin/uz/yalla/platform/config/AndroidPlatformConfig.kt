package uz.yalla.platform.config

import uz.yalla.platform.PlatformConfig
import uz.yalla.platform.YallaPlatform

/**
 * Android platform configuration. No factories needed — all components use Compose.
 * @since 0.0.1
 */
class AndroidPlatformConfig : PlatformConfig

/** Convenience installer for Android — no configuration required. */
fun YallaPlatform.installAndroid() {
    install(AndroidPlatformConfig())
}
