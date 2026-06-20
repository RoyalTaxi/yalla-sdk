package uz.yalla.foundation.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import org.koin.mp.KoinPlatform
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.settings.LocaleKind

/**
 * The active [LocaleKind] for the composition subtree, provided by [LocaleProvider]. Read it via
 * `LocalAppLocale.current` to observe in-session language changes without re-reading preferences.
 */
// TODO(quality, needs-decision): M6 — LocalAppLocale is published in the committed ABI
//  (foundation.klib.api, foundation.api) with no in-repo `.current` reader. Making it `internal` or
//  deleting it is a breaking public-API removal a buyer may already bind to; the call to narrow vs.
//  wire-it-as-the-iOS-re-localization-seam needs the owner's sign-off. Left public + documented.
public val LocalAppLocale: ProvidableCompositionLocal<LocaleKind> = staticCompositionLocalOf { LocaleKind.Uz }

/**
 * Wraps [content] with the user's persisted UI language and keeps the platform locale in sync.
 *
 * Reads the persisted [LocaleKind] from [InterfacePreferences] (resolved from the SDK's Koin graph,
 * which must be started before this composable enters composition) and provides it via
 * [LocalAppLocale]. On every locale change it applies the platform-level language via
 * [changeLanguage] in a keyed [LaunchedEffect] — not in `remember`, so the process-global mutation
 * runs as a committed effect rather than during the composition pass.
 */
@Composable
public fun LocaleProvider(content: @Composable () -> Unit) {
    val preferences = remember { KoinPlatform.getKoin().get<InterfacePreferences>() }
    val initialLocale = remember { LocaleKind.from(getCurrentLanguage()) }
    val locale by preferences.localeType.collectAsState(initialLocale)

    LaunchedEffect(locale) { changeLanguage(locale.code) }

    CompositionLocalProvider(LocalAppLocale provides locale) {
        content()
    }
}
