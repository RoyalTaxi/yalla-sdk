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
 *
 * @param C Concrete route type.
 * @param rootComponent Decompose root component managing the navigation stack.
 * @param screenProvider Maps routes to screen config and content.
 * @param modifier Optional modifier applied to the host container.
 * @since 0.0.5
 */
@Composable
expect fun <C : Route> NativeNavHost(
    rootComponent: NativeRootComponent<C>,
    screenProvider: ScreenProvider<C>,
    modifier: Modifier = Modifier,
)
