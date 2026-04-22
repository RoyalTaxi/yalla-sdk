package uz.yalla.platform.config

import uz.yalla.platform.PlatformConfig
import uz.yalla.platform.YallaPlatform

/**
 * Android [PlatformConfig] implementation.
 *
 * No factories are required on Android because all platform UI components (sheets, icon buttons,
 * navigation) are implemented with Compose Material3 directly — no UIKit interop layer exists.
 * This is a marker class: it exists solely to satisfy the [PlatformConfig] contract and signal
 * that [YallaPlatform] has been initialized for the Android target. See ADR-015d.
 *
 * Contrast with [IosPlatformConfig][uz.yalla.platform.config.IosPlatformConfig], which requires
 * three factories because UIKit components (sheet presentation, circle and squircle icon buttons)
 * cannot be driven from Kotlin/Compose without a Swift-side adapter.
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
