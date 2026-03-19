package uz.yalla.platform.navigation

import androidx.compose.runtime.Composable

/**
 * Maps routes to screen configuration and composable content.
 *
 * App implements this interface to define how each route is rendered.
 * Generic type [C] preserves type safety — no unchecked casts needed.
 *
 * ## Usage
 * ```kotlin
 * class AppScreenProvider : ScreenProvider<AppRoute> {
 *     override fun configFor(route: AppRoute) = when (route) {
 *         AppRoute.Home -> ScreenConfig(showsNavigationBar = false)
 *         AppRoute.Menu -> ScreenConfig(title = "Menu", largeTitleMode = LargeTitleMode.Always)
 *     }
 *
 *     @Composable
 *     override fun Content(route: AppRoute, navigator: Navigator, toolbarState: ToolbarState) {
 *         when (route) {
 *             AppRoute.Home -> HomeScreen()
 *             AppRoute.Menu -> MenuScreen(toolbarState)
 *         }
 *     }
 * }
 * ```
 *
 * @param C Concrete route type (sealed class extending [Route]).
 * @since 0.0.5
 */
interface ScreenProvider<C : Route> {
    /** Return the navigation bar configuration for [route]. */
    fun configFor(route: C): ScreenConfig

    /** Render the screen content for [route]. */
    @Composable
    fun Content(route: C, navigator: Navigator, toolbarState: ToolbarState)
}
