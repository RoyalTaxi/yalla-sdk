package uz.yalla.platform.navigation

/**
 * Marker interface for navigation route definitions.
 *
 * Concrete route types should be sealed classes implementing this interface
 * and annotated with `@Serializable` for Decompose state preservation.
 *
 * ```kotlin
 * @Serializable
 * sealed class AppRoute : Route {
 *     @Serializable data object Home : AppRoute()
 *     @Serializable data class Profile(val userId: String) : AppRoute()
 * }
 * ```
 *
 * @see Navigator
 * @see ScreenProvider
 * @see NativeRootComponent
 */
interface Route

/**
 * Per-screen configuration controlling navigation bar behavior.
 *
 * Returned by [ScreenProvider.configFor] for each route.
 *
 *   On iOS, controls `UINavigationController.setNavigationBarHidden`.
 *   On Android, controls whether a `TopAppBar` is rendered inside the `Scaffold`.
 *   Default `true`.
 * @see ScreenProvider.configFor
 */
data class ScreenConfig(
    val showsNavigationBar: Boolean = true,
)
