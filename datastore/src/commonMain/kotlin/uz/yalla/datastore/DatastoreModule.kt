package uz.yalla.datastore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module
import uz.yalla.core.preferences.ConfigPreferences
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.preferences.PositionPreferences
import uz.yalla.core.preferences.SessionPreferences
import uz.yalla.core.preferences.StaticPreferences
import uz.yalla.core.preferences.UserPreferences

public val datastoreModule: Module =
    module {
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
