package uz.yalla.data.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module
import uz.yalla.core.contract.preferences.ConfigPreferences
import uz.yalla.core.contract.preferences.InterfacePreferences
import uz.yalla.core.contract.preferences.PositionPreferences
import uz.yalla.core.contract.preferences.SessionPreferences
import uz.yalla.core.contract.preferences.StaticPreferences
import uz.yalla.core.contract.preferences.UserPreferences
import uz.yalla.data.local.ConfigPreferencesImpl
import uz.yalla.data.local.InterfacePreferencesImpl
import uz.yalla.data.local.PositionPreferencesImpl
import uz.yalla.data.local.SessionPreferencesImpl
import uz.yalla.data.local.StaticPreferencesImpl
import uz.yalla.data.local.UserPreferencesImpl
import uz.yalla.data.local.createDataStore
import uz.yalla.data.local.createSettings
import uz.yalla.data.util.ioDispatcher

/**
 * Koin module providing data layer infrastructure.
 *
 * Registers:
 * - [DataStore][androidx.datastore.core.DataStore] singleton for async preferences
 * - [Settings][com.russhwolf.settings.Settings] singleton for synchronous preferences
 * - [CoroutineScope] bound to [ioDispatcher][uz.yalla.data.util.ioDispatcher] for background writes
 * - [StaticPreferences][uz.yalla.core.contract.preferences.StaticPreferences] -- synchronous startup reads
 * - [SessionPreferences][uz.yalla.core.contract.preferences.SessionPreferences] -- tokens and guest mode
 * - [UserPreferences][uz.yalla.core.contract.preferences.UserPreferences] -- profile data
 * - [ConfigPreferences][uz.yalla.core.contract.preferences.ConfigPreferences] -- server config
 * - [InterfacePreferences][uz.yalla.core.contract.preferences.InterfacePreferences] -- UI settings
 * - [PositionPreferences][uz.yalla.core.contract.preferences.PositionPreferences] -- geographic state
 *
 * All five async preferences implementations share a single [DataStore] instance.
 *
 * Usage:
 * ```kotlin
 * startKoin {
 *     modules(dataModule)
 * }
 * ```
 *
 * @see uz.yalla.data.local.createDataStore
 * @see uz.yalla.data.local.createSettings
 * @since 0.0.4
 */
val dataModule = module {
    single { createDataStore() }
    single { createSettings() }
    single { CoroutineScope(ioDispatcher + SupervisorJob()) }

    single<StaticPreferences> { StaticPreferencesImpl(get()) }
    single<SessionPreferences> { SessionPreferencesImpl(get(), get(), get()) }
    single<UserPreferences> { UserPreferencesImpl(get(), get()) }
    single<ConfigPreferences> { ConfigPreferencesImpl(get(), get()) }
    single<InterfacePreferences> { InterfacePreferencesImpl(get(), get(), get()) }
    single<PositionPreferences> { PositionPreferencesImpl(get(), get()) }
}
