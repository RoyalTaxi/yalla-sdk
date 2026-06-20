package uz.yalla.datastore

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module
import uz.yalla.core.preferences.ConfigPreferences
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.preferences.PositionPreferences
import uz.yalla.core.preferences.SessionPreferences
import uz.yalla.core.preferences.UserPreferences

/**
 * Koin module backing the persistence layer.
 *
 * Binds the single shared [androidx.datastore.core.DataStore] (with corruption recovery — see
 * `createDataStore`), the app-lifetime write [CoroutineScope], and the five `*Preferences` ports
 * ([SessionPreferences], [UserPreferences], [ConfigPreferences], [InterfacePreferences],
 * [PositionPreferences]). Include this module before resolving any preferences port.
 *
 * The write scope carries a [CoroutineExceptionHandler] so a failed fire-and-forget write (e.g. an
 * `IOException` from a full disk) is reported once instead of being silently dropped on Native or
 * crashing the process on Android via the default uncaught handler.
 */
public val datastoreModule: Module =
    module {
        single { createDataStore(this) }

        // Encrypts the sensitive keys at rest (Keystore on Android, Keychain on iOS); see SecureStore.
        single { createSecureStore(this) }

        single { CoroutineScope(ioDispatcher + SupervisorJob() + datastoreExceptionHandler) }

        single<SessionPreferences> { SessionPreferencesImpl(get(), get(), get()) }
        single<UserPreferences> { UserPreferencesImpl(get(), get(), get()) }
        single<ConfigPreferences> { ConfigPreferencesImpl(get(), get()) }
        single<InterfacePreferences> { InterfacePreferencesImpl(get(), get()) }
        single<PositionPreferences> { PositionPreferencesImpl(get(), get()) }
    }

/**
 * Catches failures from fire-and-forget preference writes so a persistence error (disk full, corruption,
 * atomic-rename failure) does not vanish silently or crash the host process. Without it, an uncaught
 * exception under the [SupervisorJob] root reaches the platform default handler (process crash on
 * Android; effectively dropped on Native).
 */
private val datastoreExceptionHandler: CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, throwable ->
        // TODO(quality, needs-decision): route to the host's telemetry/Crashlytics seam — this module
        // has no logging port today, and wiring a reporter needs the owner's choice of sink. Until then
        // the handler's presence is what prevents the silent-loss / process-crash failure mode (#2).
        throwable.printStackTrace()
    }
