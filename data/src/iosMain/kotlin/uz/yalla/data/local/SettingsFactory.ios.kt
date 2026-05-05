package uz.yalla.data.local

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

/**
 * iOS implementation of [createSettings].
 *
 * Wraps [NSUserDefaults.standardUserDefaults] via
 * [NSUserDefaultsSettings] for synchronous key-value access.
 */
actual fun createSettings(): Settings = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
