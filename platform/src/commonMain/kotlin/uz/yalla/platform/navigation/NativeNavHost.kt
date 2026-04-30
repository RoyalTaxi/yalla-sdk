package uz.yalla.platform.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Cross-platform navigation host composable.
 *
 * On iOS, renders via `UINavigationController` with native transitions, large titles,
 * and swipe-back gestures. On Android, renders via Compose `Children` with Material3
 * `TopAppBar` / `LargeTopAppBar`.
 *
 * ## Usage
 * ```kotlin
 * @Composable
 * fun App(rootComponent: NativeRootComponent<AppRoute>) {
 *     YallaTheme {
 *         NativeNavHost(
 *             rootComponent = rootComponent,
 *             screenProvider = AppScreenProvider(),
 *         )
 *     }
 * }
 * ```
 */
@Composable
expect fun <C : Route> NativeNavHost(
    rootComponent: NativeRootComponent<C>,
    screenProvider: ScreenProvider<C>,
    modifier: Modifier = Modifier,
)
