package uz.yalla.datastore

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module
import uz.yalla.core.preferences.BonusConfigPreferences
import uz.yalla.core.preferences.ConfigPreferences
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.preferences.LegalConfigPreferences
import uz.yalla.core.preferences.OrderConfigPreferences
import uz.yalla.core.preferences.PositionPreferences
import uz.yalla.core.preferences.SessionPreferences
import uz.yalla.core.preferences.SupportConfigPreferences
import uz.yalla.core.preferences.UserPreferences
import uz.yalla.telemetry.Telemetry

public val datastoreModule: Module =
    module {
        single { createDataStore(this) }

        single { createSecureStore(this) }

        single { CoroutineScope(ioDispatcher + SupervisorJob() + datastoreExceptionHandler) }

        single<SessionPreferences> { SessionPreferencesImpl(get(), get()) }
        single<UserPreferences> { UserPreferencesImpl(get(), get(), get()) }
        single<ConfigPreferences> { ConfigPreferencesImpl(get(), get()) }
        single<SupportConfigPreferences> { get<ConfigPreferences>() }
        single<LegalConfigPreferences> { get<ConfigPreferences>() }
        single<BonusConfigPreferences> { get<ConfigPreferences>() }
        single<OrderConfigPreferences> { get<ConfigPreferences>() }
        single<InterfacePreferences> { InterfacePreferencesImpl(get(), get()) }
        single<PositionPreferences> { PositionPreferencesImpl(get(), get()) }
    }

private val datastoreExceptionHandler: CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, throwable ->
        Telemetry.recordCrash(throwable)
    }
