package uz.yalla.foundation.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import uz.yalla.core.settings.LocaleKind

/**
 * Current app language state.
 *
 * @property language Currently selected language
 * @property onLanguageChange Callback when language should change
 */
data class LocaleState(
    val language: LocaleKind,
    val onLanguageChange: (LocaleKind) -> Unit,
)

/**
 * CompositionLocal for locale state.
 */
val LocalLocaleState =
    staticCompositionLocalOf<LocaleState> {
        error("LocaleState not provided. Wrap content with LocaleProvider.")
    }

/**
 * Provides locale state to composition tree.
 *
 * @param state Current locale state
 * @param content Child composables
 */
@Composable
fun LocaleProvider(
    state: LocaleState,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalLocaleState provides state,
        content = content,
    )
}

/**
 * Returns the current [LocaleState] from the composition tree.
 *
 * @return [LocaleState] provided by the nearest [LocaleProvider].
 * @throws IllegalStateException If called outside a [LocaleProvider].
 * @see LocaleProvider
 */
@Composable
fun currentLocaleState(): LocaleState = LocalLocaleState.current
