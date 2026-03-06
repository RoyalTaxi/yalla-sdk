package uz.yalla.data.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module
import uz.yalla.core.contract.preferences.ConfigPreferences
import uz.yalla.core.contract.preferences.InterfacePreferences
import uz.yalla.core.contract.preferences.PositionPreferences
import uz.yalla.core.contract.preferences.SessionPreferences
import uz.yalla.core.contract.preferences.UserPreferences
import uz.yalla.data.local.ConfigPreferencesImpl
import uz.yalla.data.local.InterfacePreferencesImpl
import uz.yalla.data.local.PositionPreferencesImpl
import uz.yalla.data.local.SessionPreferencesImpl
import uz.yalla.data.local.UserPreferencesImpl
import uz.yalla.data.local.createDataStore
import uz.yalla.data.util.ioDispatcher

/**
 * Koin module providing data layer infrastructure.
 *
 * Registers [DataStore] and all five preferences implementations
 * (session, user, config, interface, position) sharing a single store.
 *
 * Usage:
 * ```kotlin
 * startKoin {
 *     modules(dataModule)
 * }
 * ```
 *
 * @since 0.0.4
 */
val dataModule = module {
    single { createDataStore() }
    single { CoroutineScope(ioDispatcher + SupervisorJob()) }

    single<SessionPreferences> { SessionPreferencesImpl(get(), get()) }
    single<UserPreferences> { UserPreferencesImpl(get(), get()) }
    single<ConfigPreferences> { ConfigPreferencesImpl(get(), get()) }
    single<InterfacePreferences> { InterfacePreferencesImpl(get(), get()) }
    single<PositionPreferences> { PositionPreferencesImpl(get(), get()) }
}
