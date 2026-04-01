package uz.yalla.data.local

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

/**
 * iOS implementation of [createSettings].
 *
 * Wraps [NSUserDefaults.standardUserDefaults] via
 * [NSUserDefaultsSettings] for synchronous key-value access.
 *
 * @return [Settings] backed by NSUserDefaults
 * @see createSettings
 * @since 0.0.7
 */
actual fun createSettings(): Settings =
    NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
