package uz.yalla.components.foundation.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import uz.yalla.core.kind.LocaleKind

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
 * Retrieves current locale state from composition.
 */
@Composable
fun rememberLocaleState(): LocaleState = LocalLocaleState.current
