package uz.yalla.platform.config

import uz.yalla.platform.PlatformConfig
import uz.yalla.platform.YallaPlatform

/**
 * Android [PlatformConfig] implementation.
 *
 * Unlike [IosPlatformConfig][uz.yalla.platform.config.IosPlatformConfig], Android requires no
 * native component factories because all platform components are implemented with Compose
 * Material3 directly. This class exists solely to satisfy the [PlatformConfig] contract.
 *
 * @see YallaPlatform.install
 * @see installAndroid
 * @since 0.0.1
 */
class AndroidPlatformConfig : PlatformConfig

/**
 * Convenience extension to install the Android platform configuration in a single call.
 *
 * Equivalent to `YallaPlatform.install(AndroidPlatformConfig())`.
 *
 * ```kotlin
 * // In Application.onCreate() or MainActivity.onCreate()
 * YallaPlatform.installAndroid()
 * ```
 *
 * @see YallaPlatform.install
 * @see AndroidPlatformConfig
 * @since 0.0.1
 */
fun YallaPlatform.installAndroid() {
    install(AndroidPlatformConfig())
}
